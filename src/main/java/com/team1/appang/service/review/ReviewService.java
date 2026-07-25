package com.team1.appang.service.review;


import com.team1.appang.dto.review.ReviewCreateRequest;
import com.team1.appang.dto.review.ReviewCreateResponse;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        if (request.mediaList()!=null){
        for(ReviewMediaRequest mediaRequest : request.mediaList()){
            ReviewMedia reviewMedia = ReviewMedia.builder()
                    .mediaType(mediaRequest.mediaType())
                    .mediaUrl(mediaRequest.mediaUrl())
                    .duration(mediaRequest.duration())
                    .productReview(review)
                    .build();
            reviewMediaRepository.save(reviewMedia);
        }}
        return new ReviewCreateResponse("리뷰가 작성되었습니다", savedReview.getId());


    }
}
