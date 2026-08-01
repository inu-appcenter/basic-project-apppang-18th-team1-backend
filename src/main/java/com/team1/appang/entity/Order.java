package com.team1.appang.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") //"order"는 MySQL 예약어(ORDER BY)라 그대로 테이블명으로 쓸 수 없어 orders로 지정
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private int totalProductPrice;

    @Column(nullable = false)
    private int totalDiscountPrice;

    @Column(nullable = false)
    private int finalPaymentPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    //배송지는 Address를 FK로 참조하지 않고, 주문 시점의 값을 그대로 복사해서 저장 (스냅샷)
    //회원이 이후 배송지를 수정/삭제해도 과거 주문 내역은 영향받지 않아야 하기 때문
    @Column(nullable = false)
    private String shippingRecipientName;

    @Column(nullable = false)
    private String shippingRecipientPhone;

    @Column(nullable = false)
    private String shippingMainAddress;

    private String shippingDetailAddress;

    private String shippingDeliveryMessage;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Order(int totalProductPrice, int totalDiscountPrice,
                 int finalPaymentPrice, OrderStatus orderStatus, Member member,
                 String shippingRecipientName, String shippingRecipientPhone,
                 String shippingMainAddress, String shippingDetailAddress, String shippingDeliveryMessage) {
        this.totalProductPrice = totalProductPrice;
        this.totalDiscountPrice = totalDiscountPrice;
        this.finalPaymentPrice = finalPaymentPrice;
        this.orderStatus = orderStatus;
        this.member = member;
        this.shippingRecipientName = shippingRecipientName;
        this.shippingRecipientPhone = shippingRecipientPhone;
        this.shippingMainAddress = shippingMainAddress;
        this.shippingDetailAddress = shippingDetailAddress;
        this.shippingDeliveryMessage = shippingDeliveryMessage;
    }

    //취소 가능 여부 검증은 Service에서 처리하고, 여기서는 상태 변경만 수행
    public void cancel() {
        this.orderStatus = OrderStatus.CANCELLED;
    }

    // 연관관계 편의 메서드: 양쪽 참조를 함께 맞춰줌
    public void addItem(OrderItem item) {
        items.add(item);
        item.assignOrder(this);
    }
}
