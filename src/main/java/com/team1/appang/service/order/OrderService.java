package com.team1.appang.service.order;

import com.team1.appang.dto.order.*;
import com.team1.appang.entity.*;
import com.team1.appang.exception.EmptyCartException;
import com.team1.appang.exception.OrderCancelNotAllowedException;
import com.team1.appang.exception.OrderNotFoundException;
import com.team1.appang.exception.OutOfStockException;
import com.team1.appang.repository.CartItemRepository;
import com.team1.appang.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;

    //장바구니에서 선택된(isSelected) 상품들로 주문을 생성하는 로직 (체크아웃)
    @Transactional
    public OrderCreateData createOrder(Long memberId) {
        List<CartItem> selectedItems = cartItemRepository.findByMemberIdWithProductInfo(memberId).stream()
                .filter(CartItem::isSelected)
                .toList();

        if (selectedItems.isEmpty()) {
            throw new EmptyCartException();
        }

        //재고 검증 + 금액 합산을 먼저 끝내고, 검증 통과 후에만 주문 생성/재고 차감으로 넘어감
        int totalProductPrice = 0;
        int totalDiscountPrice = 0;
        for (CartItem cartItem : selectedItems) {
            ProductOption option = cartItem.getProductOption();
            if (option.getStockQuantity() < cartItem.getQuantity()) {
                throw new OutOfStockException(option.getProduct().getName() + "의 재고가 부족합니다.");
            }

            int originUnitPrice = option.getProduct().getOriginPrice() + option.getAdditionalPrice();
            int saleUnitPrice = option.getProduct().getSalePrice() + option.getAdditionalPrice();
            totalProductPrice += originUnitPrice * cartItem.getQuantity();
            totalDiscountPrice += (originUnitPrice - saleUnitPrice) * cartItem.getQuantity();
        }
        int finalPaymentPrice = totalProductPrice - totalDiscountPrice;

        Order order = Order.builder()
                .totalProductPrice(totalProductPrice)
                .totalDiscountPrice(totalDiscountPrice)
                .finalPaymentPrice(finalPaymentPrice)
                .orderStatus(OrderStatus.ORDERED)
                .member(selectedItems.get(0).getMember())
                .build();

        for (CartItem cartItem : selectedItems) {
            ProductOption option = cartItem.getProductOption();
            option.decreaseStock(cartItem.getQuantity());

            int unitPrice = option.getProduct().getSalePrice() + option.getAdditionalPrice();
            OrderItem orderItem = OrderItem.builder()
                    .quantity(cartItem.getQuantity())
                    .price(unitPrice)
                    .productOption(option)
                    .build();
            order.addItem(orderItem);
        }

        orderRepository.save(order);
        cartItemRepository.deleteAll(selectedItems);

        return toOrderCreateData(order);
    }

    //내 주문 목록 조회 로직
    public List<OrderListItemData> getOrderList(Long memberId) {
        return orderRepository.findByMemberIdWithItems(memberId).stream()
                .map(this::toOrderListItemData)
                .toList();
    }

    //주문 취소 로직. 이미 배송완료/취소/환불된 주문은 취소 불가
    @Transactional
    public OrderCancelData cancelOrder(Long memberId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        if (!order.getMember().getId().equals(memberId)) {
            throw new OrderNotFoundException();
        }

        if (order.getOrderStatus() == OrderStatus.CANCELLED
                || order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new OrderCancelNotAllowedException();
        }

        for (OrderItem item : order.getItems()) {
            item.getProductOption().increaseStock(item.getQuantity());
        }
        order.cancel();

        return new OrderCancelData(order.getId(), order.getOrderStatus().getDisplayName());
    }

    private OrderCreateData toOrderCreateData(Order order) {
        return new OrderCreateData(
                order.getId(),
                order.getOrderStatus().getDisplayName(),
                order.getTotalProductPrice(),
                order.getTotalDiscountPrice(),
                order.getFinalPaymentPrice(),
                order.getItems().stream().map(this::toOrderItemData).toList()
        );
    }

    private OrderListItemData toOrderListItemData(Order order) {
        return new OrderListItemData(
                order.getId(),
                order.getOrderStatus().getDisplayName(),
                order.getFinalPaymentPrice(),
                order.getCreatedAt(),
                order.getItems().stream().map(this::toOrderItemData).toList()
        );
    }

    private OrderItemData toOrderItemData(OrderItem item) {
        ProductOption option = item.getProductOption();
        Product product = option.getProduct();

        return new OrderItemData(
                product.getId(),
                product.getName(),
                product.getMainImageUrl(),
                product.getBrand().getName(),
                option.getOptionValue(),
                item.getQuantity(),
                item.getPrice()
        );
    }
}
