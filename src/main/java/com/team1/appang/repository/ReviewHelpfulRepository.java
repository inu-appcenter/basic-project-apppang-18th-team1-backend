package com.team1.appang.repository;

import com.team1.appang.entity.ReviewHelpful;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewHelpfulRepository extends JpaRepository<ReviewHelpful, Long> {

    Optional<ReviewHelpful> findByMemberIdAndProductReviewId(Long memberId, Long productReviewId);

    List<ReviewHelpful> findByMemberIdAndProductReviewIdIn(Long memberId, List<Long> productReviewIds);
}
