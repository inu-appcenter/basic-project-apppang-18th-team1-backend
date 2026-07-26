package com.team1.appang.service.review;


import com.team1.appang.dto.review.ReviewCreateRequest;
import com.team1.appang.dto.review.ReviewCreateResponse;
import com.team1.appang.dto.review.ReviewListResponse;
import com.team1.appang.dto.review.ReviewMediaRequest;
import com.team1.appang.entity.Member;
import com.team1.appang.entity.Product;
import com.team1.appang.entity.ProductReview;
import com.team1.appang.entity.ReviewMedia;
import com.team1.appang.exception.MemberNotFoundException;
import com.team1.appang.exception.ProductNotFoundException;
import com.team1.appang.repository.MemberRepository;
import com.team1.appang.repository.ProductRepository;
import com.team1.appang.repository.ProductReviewRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final ReviewMediaRepository reviewMediaRepository;
    @Transactional
    public ReviewCreateResponse createReview(Long productId, Long memberId, ReviewCreateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

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
        public ReviewListResponse getReviews(Long productId, int page, int size){
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
            return ReviewListResponse.from(reviewPage,thumbnailByReviewId);
        }
    }

