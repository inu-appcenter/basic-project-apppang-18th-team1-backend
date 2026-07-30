package com.team1.appang.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "product_id"}))
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wishlist {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY) // 일대다 관계이므로
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY) // 일대다 관계이므로
    @JoinColumn(name = "product_id")
    private Product product;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Wishlist(Member member, Product product){
        this.member = member;
        this.product = product;
    }


}
