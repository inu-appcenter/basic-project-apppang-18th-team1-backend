package com.team1.appang.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

//회원의 배송지 목록(배송지 관리)을 표현하는 엔티티
@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    //배송지 별칭 (예: "집", "회사")
    private String alias;

    @Column(nullable = false)
    private String recipientName;

    @Column(nullable = false)
    private String recipientPhone;

    @Column(nullable = false)
    private String mainAddress;

    private String detailAddress;

    private String deliveryMessage;

    @Column(nullable = false)
    private boolean isDefault;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Address(Member member, String alias, String recipientName, String recipientPhone,
                    String mainAddress, String detailAddress, String deliveryMessage, boolean isDefault) {
        this.member = member;
        this.alias = alias;
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
        this.mainAddress = mainAddress;
        this.detailAddress = detailAddress;
        this.deliveryMessage = deliveryMessage;
        this.isDefault = isDefault;
    }

    //배송지 정보 수정
    public void update(String alias, String recipientName, String recipientPhone,
                        String mainAddress, String detailAddress, String deliveryMessage) {
        this.alias = alias;
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
        this.mainAddress = mainAddress;
        this.detailAddress = detailAddress;
        this.deliveryMessage = deliveryMessage;
    }

    //기본 배송지 지정/해제
    public void changeDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}
