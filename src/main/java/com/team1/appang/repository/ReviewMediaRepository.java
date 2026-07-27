package com.team1.appang.repository;

import com.team1.appang.entity.ReviewMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewMediaRepository extends JpaRepository<ReviewMedia, Long> {
    List<ReviewMedia> findByProductReviewId(Long productReviewId);
    List<ReviewMedia> findByProductReviewIdInOrderByIdAsc(List<Long> productReviewId);
}
