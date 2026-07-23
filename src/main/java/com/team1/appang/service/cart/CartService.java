package com.team1.appang.service.cart;

import com.team1.appang.dto.cart.*;
import com.team1.appang.entity.*;
import com.team1.appang.exception.*;
import com.team1.appang.repository.CartItemRepository;
import com.team1.appang.repository.MemberRepository;
import com.team1.appang.repository.ProductOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductOptionRepository productOptionRepository;
    private final MemberRepository memberRepository;

    //X 버튼을 누른 그 상품(cartItemId) 하나만 삭제하고, 남은 장바구니 기준으로 요약을 다시 계산
    @Transactional
    public CartDeleteData deleteCartItem(Long memberId, Long cartItemId) {

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException("장바구니 항목을 찾을 수 없습니다."));

        if (!cartItem.getMember().getId().equals(memberId)) {
            throw new CartItemNotFoundException("장바구니 항목을 찾을 수 없습니다.");
        }

        cartItemRepository.delete(cartItem);

        CartDeleteSummary summary = calculateDeleteSummary(memberId);
        return new CartDeleteData(cartItemId, summary);
    }

    //삭제 후 남은 장바구니 기준으로 결제 금액 요약 계산
    private CartDeleteSummary calculateDeleteSummary(Long memberId) {
        List<CartItem> items = cartItemRepository.findByMemberId(memberId);

        int totalOriginPrice = items.stream()
                .mapToInt(item -> {
                    ProductOption option = item.getProductOption();
                    int unitPrice = option.getProduct().getOriginPrice() + option.getAdditionalPrice();
                    return unitPrice * item.getQuantity();
                }).sum();

        int totalProductPrice = items.stream()
                .mapToInt(item -> {
                    ProductOption option = item.getProductOption();
                    int unitPrice = option.getProduct().getSalePrice() + option.getAdditionalPrice();
                    return unitPrice * item.getQuantity();
                }).sum();

        int totalDiscount = totalOriginPrice - totalProductPrice;
        int totalPaymentAmount = totalProductPrice;

        return new CartDeleteSummary(totalProductPrice, totalDiscount, totalPaymentAmount);
    }


    //장바구니 목록 조회 로직
    public CartListData getCartList(Long memberId) {
        List<CartItem> items = cartItemRepository.findByMemberId(memberId);

        Map<ShippingType, List<CartItem>> grouped = items.stream()
                .collect(Collectors.groupingBy(item -> item.getProductOption().getShippingType()));

        List<ShippingGroupData> shippingGroups = new ArrayList<>();
        int groupId = 1;
        for (ShippingType type : ShippingType.values()) {
            List<CartItem> groupItems = grouped.get(type);
            if (groupItems == null || groupItems.isEmpty()) {
                continue;
            }
            List<CartListItemData> itemDataList = groupItems.stream()
                    .map(this::toItemData)
                    .toList();
            // enum에 있는 displayName을 그대로 사용 (별도 변환 메서드 불필요)
            shippingGroups.add(new ShippingGroupData(groupId++, type.getDisplayName(), itemDataList));
        }

        CartListSummary summary = calculateListSummary(items);
        return new CartListData(shippingGroups, summary);
    }

    private CartListItemData toItemData(CartItem item) {
        ProductOption option = item.getProductOption();
        Product product = option.getProduct();

        int originalPrice = product.getOriginPrice() + option.getAdditionalPrice();
        int salePrice = product.getSalePrice() + option.getAdditionalPrice();

        CartItemPrice price = new CartItemPrice(originalPrice, product.getDiscountRate(), salePrice);

        String estimatedArrivalDate = LocalDate.now().plusDays(1).toString();

        return new CartListItemData(
                item.getId(),
                product.getId(),
                product.getName(),
                product.getMainImageUrl(),
                product.getBrand().getName(),
                option.getOptionValue(), // 속성 하나만 그대로 사용
                estimatedArrivalDate,
                item.getQuantity(),
                option.getStockQuantity(),
                item.isSelected(),
                price
        );
    }

    private CartListSummary calculateListSummary(List<CartItem> items) {
        int totalProductPrice = items.stream()
                .mapToInt(item -> {
                    ProductOption option = item.getProductOption();
                    int unitSalePrice = option.getProduct().getSalePrice() + option.getAdditionalPrice();
                    return unitSalePrice * item.getQuantity();
                })
                .sum();

        int totalCouponDiscount = 0;
        int totalPaymentAmount = totalProductPrice - totalCouponDiscount;

        return new CartListSummary(totalProductPrice, totalCouponDiscount, totalPaymentAmount);
    }

    //장바구니 항목 선택/선택취소 로직
    @Transactional
    public CartItem updateSelection(Long memberId, Long cartItemId, boolean selected) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException("장바구니 항목을 찾을 수 없습니다."));

        if (!cartItem.getMember().getId().equals(memberId)) {
            throw new CartItemNotFoundException("장바구니 항목을 찾을 수 없습니다.");
        }

        cartItem.changeSelected(selected);
        return cartItem;
    }

    //상품 추가 또는 수정 로직
    @Transactional
    public CartItemData addOrUpdateCartItem(Long memberId, Long productId, Long optionId, int quantity) {

        //수량이 0보다 작다면
        if (quantity <= 0) {
            throw new InvalidQuantityException("수량은 1개 이상이어야 합니다.");
            //Exception 발생
        }

        //옵션을 확인하고 없다면 예외처리
        ProductOption option = productOptionRepository.findById(optionId)
                .orElseThrow(() -> new ProductOptionNotFoundException("해당하는 옵션을 찾을 수 없습니다."));

        //요청받은 상품 id가 실제 옵션이 속한 상품과 일치하는지 검증
        if (!option.getProduct().getId().equals(productId)) {
            throw new ProductNotFoundException("상품과 옵션 정보가 일치하지 않습니다.");
        }

        int maxQuantity = option.getStockQuantity(); //최대 수량을 가져옴

        //상품 아이디와 옵션 아이디를 가져와 장바구니 생성
        CartItem cartItem = cartItemRepository
                .findByMemberIdAndProductOptionId(memberId, optionId)
                .orElse(null);

        //장바구니 수량을 결정
        //만약 null이라면 기존값은 0으로 처리함
        int newQuantity = (cartItem == null? 0 : cartItem.getQuantity()) + quantity;

        //최대 수량을 넘을 경우 예외처리
        if (newQuantity > maxQuantity)
            throw new CartQuantityExceededException("최대 구매 가능 수량("+maxQuantity + "개)을 초과했습니다.");
        if (cartItem==null) {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberNotFoundException("회원 정보를 찾을 수 없습니다."));
            cartItem = CartItem.builder()
                    .member(member)
                    .productOption(option)
                    .quantity(quantity)
                    .isSelected(true) //새로 담긴 상품은 기본적으로 선택 상태가 됨
                    .build();
            cartItemRepository.save(cartItem);
        }else {
            cartItem.addQuantity(quantity); //장바구니에 같은 상품이 있다면 수량을 더해줌
        }

        CartSummary summary = calculateSummary(memberId);

        return new CartItemData(
                cartItem.getId(),
                productId,
                optionId,
                cartItem.getQuantity(),
                maxQuantity,
                summary
        );
    }

    //회원의 장바구니 전체를 기준으로 결제 금액 계산
    //쿠폰 할인, 배송비는 0으로 처리
    private CartSummary calculateSummary(Long memberId){
        //회원의 장바구니 속 상품을 꺼내옴
        List<CartItem> items = cartItemRepository.findByMemberId(memberId);

        int totalOriginPrice = items.stream()
                .mapToInt(item ->{
                    ProductOption option = item.getProductOption();
                    int unitPrice = option.getProduct().getOriginPrice();
                    return unitPrice*item.getQuantity();
                })
                .sum();

        int totalSalePrice = items.stream()
                .mapToInt(item -> {
                    ProductOption option = item.getProductOption();
                    int unitSalePrice = option.getProduct().getSalePrice();
                    return unitSalePrice*item.getQuantity();
                })
                .sum();

        int totalInstantDiscount = totalOriginPrice-totalSalePrice;
        int totalCouponDiscount = 0;
        int totalShippingFee = 0;
        int totalPaymentAmount = totalOriginPrice - totalInstantDiscount - totalCouponDiscount + totalShippingFee;

        return new CartSummary(totalOriginPrice, totalInstantDiscount, totalCouponDiscount, totalShippingFee, totalPaymentAmount);

    }


}