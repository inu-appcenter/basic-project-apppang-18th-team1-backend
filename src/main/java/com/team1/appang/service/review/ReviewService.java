package com.team1.appang.service.review;


import com.team1.appang.dto.review.ReviewCreateRequest;
import com.team1.appang.dto.review.ReviewCreateResponse;
import com.team1.appang.dto.review.ReviewListResponse;
import com.team1.appang.dto.review.ReviewMediaRequest;
import com.team1.appang.dto.review.ReviewHelpfulToggleResponse;
import com.team1.appang.dto.review.ReviewOwnershipResponse;
import com.team1.appang.dto.review.ReviewUpdateRequest;
import com.team1.appang.dto.review.ReviewUpdateResponse;
import com.team1.appang.entity.Member;
import com.team1.appang.entity.Product;
import com.team1.appang.entity.ProductReview;
import com.team1.appang.entity.ReviewHelpful;
import com.team1.appang.entity.ReviewMedia;
import com.team1.appang.exception.MemberNotFoundException;
import com.team1.appang.exception.ProductNotFoundException;
import com.team1.appang.exception.ReviewAccessDeniedException;
import com.team1.appang.exception.ReviewNotFoundException;
import com.team1.appang.exception.ReviewPurchaseRequiredException;
import com.team1.appang.exception.SelfReviewHelpfulException;
import com.team1.appang.repository.MemberRepository;
import com.team1.appang.repository.OrderRepository;
import com.team1.appang.repository.ProductRepository;
import com.team1.appang.repository.ProductReviewRepository;
import com.team1.appang.repository.ReviewHelpfulRepository;
import com.team1.appang.repository.ReviewMediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final ReviewMediaRepository reviewMediaRepository;
    private final ReviewHelpfulRepository reviewHelpfulRepository;
    private final OrderRepository orderRepository;
    @Transactional
    public ReviewCreateResponse createReview(Long productId, Long memberId, ReviewCreateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        //구매 이력이 없으면 리뷰 작성 불가 (취소된 주문은 구매로 인정하지 않음)
        if (!orderRepository.existsPurchaseByMemberAndProduct(memberId, productId)) {
            throw new ReviewPurchaseRequiredException();
        }

        // 리뷰 생성
        ProductReview review = ProductReview.builder()
                .rating(request.rating())
                .content(request.content())
                .helpfulCount(0)
                .member(member)
                .product(product)
                .build();
        ProductReview savedReview = reviewRepository.save(review);
        if (request.mediaList() != null) {
            for (ReviewMediaRequest mediaRequest : request.mediaList()) {
                ReviewMedia reviewMedia = ReviewMedia.builder()
                        .mediaType(mediaRequest.mediaType())
                        .mediaUrl(mediaRequest.mediaUrl())
                        .duration(mediaRequest.duration())
                        .productReview(review)
                        .build();
                reviewMediaRepository.save(reviewMedia);
            }
        }
        return new ReviewCreateResponse("리뷰가 작성되었습니다", savedReview.getId());
    }
        @Transactional(readOnly = true)
        public ReviewListResponse getReviews(Long productId, Long memberId, int page, int size){
            if(!productRepository.existsById(productId))throw new ProductNotFoundException();
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<ProductReview> reviewPage = reviewRepository.findByProductId(productId, pageable);

            List<Long> reviewIds = reviewPage.getContent().stream()
                    .map(ProductReview::getId)
                    .toList();
            Map<Long, String> thumbnailByReviewId = reviewMediaRepository.findByProductReviewIdInOrderByIdAsc(reviewIds).stream()
                    .collect(Collectors.toMap(media->media.getProductReview().getId(),
                            ReviewMedia::getMediaUrl,
                            (first, second) -> first));

            // 비로그인 사용자는 조회할 필요 없이 빈 Set -> isHelpful 전부 false
            Set<Long> helpfulReviewIds = memberId == null
                    ? Set.of()
                    : reviewHelpfulRepository.findByMemberIdAndProductReviewIdIn(memberId, reviewIds).stream()
                            .map(reviewHelpful -> reviewHelpful.getProductReview().getId())
                            .collect(Collectors.toSet());

            return ReviewListResponse.from(reviewPage, thumbnailByReviewId, helpfulReviewIds, memberId);
        }

    @Transactional
    public ReviewHelpfulToggleResponse toggleHelpful(Long reviewId, Long memberId) {
        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(ReviewNotFoundException::new);

        if (review.getMember().getId().equals(memberId)) {
            throw new SelfReviewHelpfulException();
        }

        Optional<ReviewHelpful> existing = reviewHelpfulRepository.findByMemberIdAndProductReviewId(memberId, reviewId);

        if (existing.isPresent()) {
            // 이미 누른 상태 -> 취소
            reviewHelpfulRepository.delete(existing.get());
            review.decreaseHelpfulCount();
            return ReviewHelpfulToggleResponse.removed(review.getHelpfulCount());
        } else {
            // 아직 안 누른 상태 -> 추가
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(MemberNotFoundException::new);

            ReviewHelpful reviewHelpful = ReviewHelpful.builder()
                    .member(member)
                    .productReview(review)
                    .build();
            reviewHelpfulRepository.save(reviewHelpful);
            review.increaseHelpfulCount();
            return ReviewHelpfulToggleResponse.added(review.getHelpfulCount());
        }
    }

    @Transactional
    public ReviewUpdateResponse updateReview(Long reviewId, Long memberId, ReviewUpdateRequest request) {
        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(ReviewNotFoundException::new);

        if (!review.getMember().getId().equals(memberId)) {
            throw new ReviewAccessDeniedException();
        }

        review.update(request.rating(), request.content());
        return new ReviewUpdateResponse("리뷰가 수정되었습니다", review.getId());
    }

    @Transactional(readOnly = true)
    public ReviewOwnershipResponse checkOwnership(Long reviewId, Long memberId) {
        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(ReviewNotFoundException::new);

        return new ReviewOwnershipResponse(review.getMember().getId().equals(memberId));
    }
    }

