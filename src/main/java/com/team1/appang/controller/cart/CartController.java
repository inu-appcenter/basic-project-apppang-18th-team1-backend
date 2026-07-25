package com.team1.appang.controller.cart;

import com.team1.appang.dto.MessageResponse;
import com.team1.appang.dto.cart.*;
import com.team1.appang.entity.CartItem;
import com.team1.appang.exception.*;
import com.team1.appang.repository.MemberRepository;
import com.team1.appang.service.auth.AuthService;
import com.team1.appang.service.cart.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cart", description = "장바구니 조회 / 추가 / 수정 / 삭제 관련 API")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final AuthService authService;

    //장바구니 삭제 API
    @Operation(summary = "장바구니 삭제", description = "장바구니 아이템을 삭제하고, 삭제 후 갱신된 결제 금액 요약을 반환합니다. 로그인이 필요합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공",
                    content = @Content(schema = @Schema(implementation = CartDeleteResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인이 필요함",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "로그인이 필요합니다."
                }
                """))),
            @ApiResponse(responseCode = "404", description = "장바구니 아이템을 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "CartItemNotFoundException.message"
                }
                """)))
    })
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<?> deleteCartItem(
            @Parameter(description = "삭제할 장바구니 아이템 id", example = "1")
            @PathVariable Long cartItemId) {

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
    @Operation(summary = "장바구니 목록 조회", description = "배송 타입 기준으로 그룹핑된 장바구니 목록과 결제 금액 요약을 함께 조회합니다. 로그인이 필요합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CartListResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인이 필요함",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "로그인이 필요합니다."
                }
                """)))
    })
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
    @Operation(summary = "장바구니 추가/수정", description = "상품을 장바구니에 담습니다. 이미 담긴 상품+옵션 조합이면 수량을 갱신합니다. 로그인이 필요합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추가/수정 성공",
                    content = @Content(schema = @Schema(implementation = CartAddResponse.class))),
            @ApiResponse(responseCode = "400", description = "옵션 불일치, 최대 담기 수량 초과 또는 수량 값이 올바르지 않음",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "ProductOptionMismatchException.message 또는 CartQuantityExceededException.message 또는 InvalidQuantityException.message"
                }
                """))),
            @ApiResponse(responseCode = "401", description = "로그인이 필요함",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "로그인이 필요합니다."
                }
                """))),
            @ApiResponse(responseCode = "404", description = "상품 옵션 또는 회원을 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "ProductOptionNotFoundException.message 또는 MemberNotFoundException.message"
                }
                """)))
    })
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
        } catch (ProductOptionNotFoundException | MemberNotFoundException e) {
            //404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        } catch (ProductOptionMismatchException | CartQuantityExceededException | InvalidQuantityException e) {
            //400
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }

    }

    //장바구니 선택/취소 토글 API
    @Operation(summary = "장바구니 선택 상태 변경", description = "장바구니 아이템의 선택(체크) 상태를 변경합니다. 로그인이 필요합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공",
                    content = @Content(schema = @Schema(implementation = CartSelectResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인이 필요함",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "로그인이 필요합니다."
                }
                """))),
            @ApiResponse(responseCode = "404", description = "장바구니 아이템을 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "CartItemNotFoundException.message"
                }
                """)))
    })
    @PatchMapping("/{cartItemId}/select")
    public ResponseEntity<?>  updateSelection(
            @Parameter(description = "선택 상태를 변경할 장바구니 아이템 id", example = "1")
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