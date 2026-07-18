package com.team1.appang.repository;

import com.team1.appang.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    List<ProductOption> findByProductId(Long productId);
}