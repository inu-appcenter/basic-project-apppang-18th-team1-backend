package com.team1.appang.controller.cart;

import com.team1.appang.dto.MessageResponse;
import com.team1.appang.dto.cart.*;
import com.team1.appang.entity.CartItem;
import com.team1.appang.exception.*;
import com.team1.appang.repository.MemberRepository;
import com.team1.appang.service.auth.AuthService;
import com.team1.appang.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final AuthService authService;

    //장바구니 삭제 API
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<?> deleteCartItem(@PathVariable Long cartItemId) {

        Long memberId = authService.getCurrentMemberId();
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("로그인이 필요합니다."));
        }

        try {
            CartDeleteData data = cartService.deleteCartItem(memberId, cartItemId);
            return ResponseEntity.ok(new CartDeleteResponse("상품이 삭제되었습니다.", data));
        } catch (CartItemNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        }
    }

    //장바구니 목록 조회 API
    //shippingType 기준으로 그룹핑된 목록과 결제 금액 요약을 함께 반환
    @GetMapping
    public ResponseEntity<?> getCartList() {

        Long memberId = authService.getCurrentMemberId();

        //비로그인 상태면 401 반환
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("로그인이 필요합니다."));
        }

        CartListData data = cartService.getCartList(memberId);
        return ResponseEntity.ok(new CartListResponse("장바구니 목록을 조회했습니다.", data));
    }

    //장바구니 추가 및 수정 API
    @PostMapping
    public ResponseEntity<?> addOrUpdateCart(@RequestBody CartAddRequest request){

        Long memberId = authService.getCurrentMemberId();
        if(memberId == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("로그인이 필요합니다."));
        }

        try{
            CartItemData data = cartService.addOrUpdateCartItem(
                    memberId,
                    (long) request.productId(),
                    (long) request.optionId(),
                    request.quantity()
            );
            return ResponseEntity.ok(new CartAddResponse("장바구니에 담았습니다.", data));
        } catch (ProductNotFoundException | ProductOptionNotFoundException | MemberNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        } catch (CartQuantityExceededException | InvalidQuantityException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }

    }

    //장바구니 선택/취소 토글 API
    @PatchMapping("/{cartItemId}/select")
    public ResponseEntity<?>  updateSelection(
            @PathVariable Long cartItemId,
            @RequestBody CartSelectRequest request
    ) {
        //로그인 필요
        Long memberId = authService.getCurrentMemberId();
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("로그인이 필요합니다."));
        }
        //오류 발생시 실패 (404 반환)
        try {
            CartItem cartItem = cartService.updateSelection(memberId, cartItemId, request.isSelected());
            return ResponseEntity.ok(
                    new CartSelectResponse("선택 상태가 변경되었습니다.", cartItem.getId(), cartItem.isSelected())
            );
        }catch (CartItemNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        }
        }
}
