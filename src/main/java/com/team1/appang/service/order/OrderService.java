package com.team1.appang.service.order;

import com.team1.appang.dto.order.*;
import com.team1.appang.entity.*;
import com.team1.appang.exception.AddressAccessDeniedException;
import com.team1.appang.exception.AddressNotFoundException;
import com.team1.appang.exception.EmptyCartException;
import com.team1.appang.exception.InvalidQuantityException;
import com.team1.appang.exception.MemberNotFoundException;
import com.team1.appang.exception.OrderCancelNotAllowedException;
import com.team1.appang.exception.OrderNotFoundException;
import com.team1.appang.exception.OutOfStockException;
import com.team1.appang.exception.ProductOptionMismatchException;
import com.team1.appang.exception.ProductOptionNotFoundException;
import com.team1.appang.repository.AddressRepository;
import com.team1.appang.repository.CartItemRepository;
import com.team1.appang.repository.MemberRepository;
import com.team1.appang.repository.OrderRepository;
import com.team1.appang.repository.ProductOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final MemberRepository memberRepository;
    private final ProductOptionRepository productOptionRepository;
    private final AddressRepository addressRepository;

    //장바구니에서 선택된(isSelected) 상품들로 주문을 생성하는 로직 (체크아웃)
    @Transactional
    public OrderCreateData createOrder(Long memberId, Long addressId) {
        List<CartItem> selectedItems = cartItemRepository.findByMemberIdWithProductInfo(memberId).stream()
                .filter(CartItem::isSelected)
                .toList();

        if (selectedItems.isEmpty()) {
            throw new EmptyCartException();
        }

        Address address = findOwnedAddress(memberId, addressId);

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
                .shippingRecipientName(address.getRecipientName())
                .shippingRecipientPhone(address.getRecipientPhone())
                .shippingMainAddress(address.getMainAddress())
                .shippingDetailAddress(address.getDetailAddress())
                .shippingDeliveryMessage(address.getDeliveryMessage())
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

    //장바구니를 거치지 않고 상품 하나를 바로 주문하는 로직 (바로구매)
    @Transactional
    public OrderCreateData buyNow(Long memberId, Long productId, Long optionId, int quantity, Long addressId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        if (quantity <= 0) {
            throw new InvalidQuantityException();
        }

        ProductOption option = productOptionRepository.findById(optionId)
                .orElseThrow(ProductOptionNotFoundException::new);

        //요청받은 상품 id가 실제 옵션이 속한 상품과 일치하는지 검증 (CartService.addOrUpdateCartItem과 동일한 검증)
        if (!option.getProduct().getId().equals(productId)) {
            throw new ProductOptionMismatchException("상품과 옵션 정보가 일치하지 않습니다.");
        }

        if (option.getStockQuantity() < quantity) {
            throw new OutOfStockException(option.getProduct().getName() + "의 재고가 부족합니다.");
        }

        Address address = findOwnedAddress(memberId, addressId);

        int originUnitPrice = option.getProduct().getOriginPrice() + option.getAdditionalPrice();
        int saleUnitPrice = option.getProduct().getSalePrice() + option.getAdditionalPrice();
        int totalProductPrice = originUnitPrice * quantity;
        int totalDiscountPrice = (originUnitPrice - saleUnitPrice) * quantity;
        int finalPaymentPrice = totalProductPrice - totalDiscountPrice;

        Order order = Order.builder()
                .totalProductPrice(totalProductPrice)
                .totalDiscountPrice(totalDiscountPrice)
                .finalPaymentPrice(finalPaymentPrice)
                .orderStatus(OrderStatus.ORDERED)
                .member(member)
                .shippingRecipientName(address.getRecipientName())
                .shippingRecipientPhone(address.getRecipientPhone())
                .shippingMainAddress(address.getMainAddress())
                .shippingDetailAddress(address.getDetailAddress())
                .shippingDeliveryMessage(address.getDeliveryMessage())
                .build();

        option.decreaseStock(quantity);

        OrderItem orderItem = OrderItem.builder()
                .quantity(quantity)
                .price(saleUnitPrice)
                .productOption(option)
                .build();
        order.addItem(orderItem);

        orderRepository.save(order);

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

    //배송지를 조회하고 본인 소유인지 검증 (AddressService.findOwnedAddress와 동일한 검증, 서비스 간 의존을 피하기 위해 중복)
    private Address findOwnedAddress(Long memberId, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(AddressNotFoundException::new);

        if (!address.getMember().getId().equals(memberId)) {
            throw new AddressAccessDeniedException();
        }
        return address;
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
