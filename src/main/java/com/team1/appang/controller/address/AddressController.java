package com.team1.appang.controller.address;

import com.team1.appang.dto.MessageResponse;
import com.team1.appang.dto.address.AddressCreateRequest;
import com.team1.appang.dto.address.AddressData;
import com.team1.appang.dto.address.AddressDeleteResponse;
import com.team1.appang.dto.address.AddressListResponse;
import com.team1.appang.dto.address.AddressResponse;
import com.team1.appang.dto.address.AddressUpdateRequest;
import com.team1.appang.exception.AddressAccessDeniedException;
import com.team1.appang.exception.AddressNotFoundException;
import com.team1.appang.exception.MemberNotFoundException;
import com.team1.appang.service.address.AddressService;
import com.team1.appang.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Address", description = "배송지 등록 / 조회 / 수정 / 삭제 관련 API")
@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final AuthService authService;

    //배송지 등록 API
    @Operation(summary = "배송지 등록", description = "새 배송지를 등록합니다. 로그인이 필요합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공",
                    content = @Content(schema = @Schema(implementation = AddressResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않음",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "수령인 이름을 입력해주세요."
                }
                """))),
            @ApiResponse(responseCode = "401", description = "로그인이 필요함",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "로그인이 필요합니다."
                }
                """))),
            @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "MemberNotFoundException.message"
                }
                """)))
    })
    @PostMapping
    public ResponseEntity<?> createAddress(
            @Valid @RequestBody AddressCreateRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "잘못된 요청입니다.";
            return ResponseEntity.badRequest().body(new MessageResponse(errorMessage));
        }

        Long memberId = authService.getCurrentMemberId();
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("로그인이 필요합니다."));
        }

        try {
            AddressData data = addressService.createAddress(memberId, request);
            return ResponseEntity.ok(new AddressResponse("배송지가 등록되었습니다.", data));
        } catch (MemberNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        }
    }

    //내 배송지 목록 조회 API
    @Operation(summary = "배송지 목록 조회", description = "로그인한 회원의 배송지 목록을 조회합니다. 기본 배송지가 먼저 나옵니다. 로그인이 필요합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = AddressListResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인이 필요함",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "로그인이 필요합니다."
                }
                """)))
    })
    @GetMapping
    public ResponseEntity<?> getAddresses() {
        Long memberId = authService.getCurrentMemberId();
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("로그인이 필요합니다."));
        }

        var data = addressService.getAddresses(memberId);
        return ResponseEntity.ok(new AddressListResponse("배송지 목록을 조회했습니다.", data));
    }

    //배송지 수정 API
    @Operation(summary = "배송지 수정", description = "본인의 배송지 정보를 수정합니다. 로그인이 필요합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = AddressResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않음",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "수령인 이름을 입력해주세요."
                }
                """))),
            @ApiResponse(responseCode = "401", description = "로그인이 필요함",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "로그인이 필요합니다."
                }
                """))),
            @ApiResponse(responseCode = "403", description = "본인의 배송지가 아님",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "AddressAccessDeniedException.message"
                }
                """))),
            @ApiResponse(responseCode = "404", description = "배송지를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "AddressNotFoundException.message"
                }
                """)))
    })
    @PatchMapping("/{addressId}")
    public ResponseEntity<?> updateAddress(
            @Parameter(description = "수정할 배송지 id", example = "1")
            @PathVariable Long addressId,
            @Valid @RequestBody AddressUpdateRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "잘못된 요청입니다.";
            return ResponseEntity.badRequest().body(new MessageResponse(errorMessage));
        }

        Long memberId = authService.getCurrentMemberId();
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("로그인이 필요합니다."));
        }

        try {
            AddressData data = addressService.updateAddress(memberId, addressId, request);
            return ResponseEntity.ok(new AddressResponse("배송지가 수정되었습니다.", data));
        } catch (AddressNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        } catch (AddressAccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse(e.getMessage()));
        }
    }

    //배송지 삭제 API
    @Operation(summary = "배송지 삭제", description = "본인의 배송지를 삭제합니다. 기본 배송지도 삭제할 수 있습니다. 로그인이 필요합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공",
                    content = @Content(schema = @Schema(implementation = AddressDeleteResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인이 필요함",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "로그인이 필요합니다."
                }
                """))),
            @ApiResponse(responseCode = "403", description = "본인의 배송지가 아님",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "AddressAccessDeniedException.message"
                }
                """))),
            @ApiResponse(responseCode = "404", description = "배송지를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "AddressNotFoundException.message"
                }
                """)))
    })
    @DeleteMapping("/{addressId}")
    public ResponseEntity<?> deleteAddress(
            @Parameter(description = "삭제할 배송지 id", example = "1")
            @PathVariable Long addressId) {
        Long memberId = authService.getCurrentMemberId();
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("로그인이 필요합니다."));
        }

        try {
            Long deletedId = addressService.deleteAddress(memberId, addressId);
            return ResponseEntity.ok(new AddressDeleteResponse("배송지가 삭제되었습니다.", deletedId));
        } catch (AddressNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        } catch (AddressAccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse(e.getMessage()));
        }
    }

    //기본 배송지 지정 API
    @Operation(summary = "기본 배송지 지정", description = "선택한 배송지를 기본 배송지로 지정합니다. 기존 기본 배송지는 자동으로 해제됩니다. 로그인이 필요합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "지정 성공",
                    content = @Content(schema = @Schema(implementation = AddressResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인이 필요함",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "로그인이 필요합니다."
                }
                """))),
            @ApiResponse(responseCode = "403", description = "본인의 배송지가 아님",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "AddressAccessDeniedException.message"
                }
                """))),
            @ApiResponse(responseCode = "404", description = "배송지를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "AddressNotFoundException.message"
                }
                """)))
    })
    @PatchMapping("/{addressId}/default")
    public ResponseEntity<?> setDefaultAddress(
            @Parameter(description = "기본으로 지정할 배송지 id", example = "1")
            @PathVariable Long addressId) {
        Long memberId = authService.getCurrentMemberId();
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("로그인이 필요합니다."));
        }

        try {
            AddressData data = addressService.setDefaultAddress(memberId, addressId);
            return ResponseEntity.ok(new AddressResponse("기본 배송지로 지정되었습니다.", data));
        } catch (AddressNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        } catch (AddressAccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse(e.getMessage()));
        }
    }
}
