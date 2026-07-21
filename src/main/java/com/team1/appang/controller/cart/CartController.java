package com.team1.appang.controller.cart;

import com.team1.appang.dto.MessageResponse;
import com.team1.appang.dto.cart.CartAddRequest;
import com.team1.appang.dto.cart.CartAddResponse;
import com.team1.appang.dto.cart.CartItemData;
import com.team1.appang.entity.CartItem;
import com.team1.appang.exception.*;
import com.team1.appang.repository.MemberRepository;
import com.team1.appang.service.auth.AuthService;
import com.team1.appang.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final AuthService authService;

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
                    (long) request.getProductId(),
                    (long) request.getOptionId(),
                    request.getQuantity()
            );
            return ResponseEntity.ok(new CartAddResponse("장바구니에 담았습니다.", data));
        } catch (ProductNotFoundException | ProductOptionNotFoundException | MemberNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        } catch (CartQuantityExceededException | InvalidQuantityException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }

    }
}
