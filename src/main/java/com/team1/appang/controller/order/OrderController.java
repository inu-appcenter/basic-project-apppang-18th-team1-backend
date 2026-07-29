package com.team1.appang.controller.order;

import com.team1.appang.dto.MessageResponse;
import com.team1.appang.dto.order.*;
import com.team1.appang.exception.EmptyCartException;
import com.team1.appang.exception.OrderCancelNotAllowedException;
import com.team1.appang.exception.OrderNotFoundException;
import com.team1.appang.exception.OutOfStockException;
import com.team1.appang.service.auth.AuthService;
import com.team1.appang.service.order.OrderService;
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

@Tag(name = "Order", description = "주문 생성 / 조회 / 취소 관련 API")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final AuthService authService;

    //주문 생성(체크아웃) API
    @Operation(summary = "주문 생성", description = "장바구니에서 선택된 상품들로 주문을 생성합니다. 로그인이 필요합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 생성 성공",
                    content = @Content(schema = @Schema(implementation = OrderCreateResponse.class))),
            @ApiResponse(responseCode = "400", description = "선택된 상품이 없거나 재고가 부족함",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "EmptyCartException.message 또는 OutOfStockException.message"
                }
                """))),
            @ApiResponse(responseCode = "401", description = "로그인이 필요함",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "로그인이 필요합니다."
                }
                """)))
    })
    @PostMapping
    public ResponseEntity<?> createOrder() {
        Long memberId = authService.getCurrentMemberId();
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("로그인이 필요합니다."));
        }

        try {
            OrderCreateData data = orderService.createOrder(memberId);
            return ResponseEntity.ok(new OrderCreateResponse("주문이 완료되었습니다.", data));
        } catch (EmptyCartException | OutOfStockException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    //내 주문 목록 조회 API
    @Operation(summary = "주문 목록 조회", description = "로그인한 회원의 주문 내역을 최신순으로 조회합니다. 로그인이 필요합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = OrderListResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인이 필요함",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "로그인이 필요합니다."
                }
                """)))
    })
    @GetMapping
    public ResponseEntity<?> getOrderList() {
        Long memberId = authService.getCurrentMemberId();
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("로그인이 필요합니다."));
        }

        var data = orderService.getOrderList(memberId);
        return ResponseEntity.ok(new OrderListResponse("주문 목록을 조회했습니다.", data));
    }

    //주문 취소 API
    @Operation(summary = "주문 취소", description = "주문 전체를 취소합니다. 이미 배송완료/취소/환불된 주문은 취소할 수 없습니다. 로그인이 필요합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "취소 성공",
                    content = @Content(schema = @Schema(implementation = OrderCancelResponse.class))),
            @ApiResponse(responseCode = "400", description = "취소할 수 없는 주문 상태",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "OrderCancelNotAllowedException.message"
                }
                """))),
            @ApiResponse(responseCode = "401", description = "로그인이 필요함",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "로그인이 필요합니다."
                }
                """))),
            @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "OrderNotFoundException.message"
                }
                """)))
    })
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(
            @Parameter(description = "취소할 주문 id", example = "1")
            @PathVariable Long orderId) {

        Long memberId = authService.getCurrentMemberId();
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("로그인이 필요합니다."));
        }

        try {
            OrderCancelData data = orderService.cancelOrder(memberId, orderId);
            return ResponseEntity.ok(new OrderCancelResponse("주문이 취소되었습니다.", data));
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        } catch (OrderCancelNotAllowedException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
