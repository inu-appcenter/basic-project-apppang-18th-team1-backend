package com.team1.appang.controller.auth;

/*
설명: 비밀번호 재설정에 사용하는 코드
MemberPasswordService를 사용한다
 */

import com.team1.appang.dto.MessageResponse;
import com.team1.appang.dto.auth.ResetPasswordRequest;
import com.team1.appang.exception.InvalidPasswordFormatException;
import com.team1.appang.exception.InvalidTokenException;
import com.team1.appang.exception.MemberNotFoundException;
import com.team1.appang.exception.PasswordMismatchException;
import com.team1.appang.service.auth.MemberPasswordService;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name = "Auth", description = "회원가입 / 로그인 / 로그아웃 관련 API")
@RestController
@RequestMapping("/api/auth/password") //공동주소 묶기
@RequiredArgsConstructor
public class MemberPasswordController {
    private final MemberPasswordService memberPasswordService;


    //비밀번호 재설정 API
    //URL: PATCH /api/user/password/reset
    //사용자가 보낸 Request를 받기 위해 @RequestBody 사용
    @Operation(summary = "비밀번호 재설정", description = "Supabase 인증 토큰을 검증한 뒤 새 비밀번호로 재설정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 재설정 성공",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 값 검증 실패, 비밀번호 형식 오류 또는 비밀번호 확인 불일치",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "필드 검증 실패 메시지 또는 InvalidPasswordFormatException.message 또는 PasswordMismatchException.message"
                }
                """))),
            @ApiResponse(responseCode = "401", description = "Supabase 토큰 검증 실패",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "InvalidTokenException.message"
                }
                """))),
            @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "message": "MemberNotFoundException.message"
                }
                """)))
    })
    @PatchMapping("/reset")
    public ResponseEntity<MessageResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request,
            BindingResult bindingResult){

        //DTO 검증 실패 시 (필드 누락 등) 400으로 응답
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "잘못된 요청입니다.";
            return ResponseEntity.badRequest().body(new MessageResponse(errorMessage));
        }

        try {
            String message = memberPasswordService.resetPassword(request);
            //예외 없이 끝까지 통과했다면 성공. 성공 메시지는 여기 Controller가 결정
            return ResponseEntity.ok(new MessageResponse(message));

        } catch (InvalidPasswordFormatException | PasswordMismatchException e) {
            //400, 요청 형식 자체가 잘못된 경우
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));

        } catch (MemberNotFoundException e) {
            //404, 회원이 존재하지 않음
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));

        } catch (InvalidTokenException e) {
            //401, 토큰 오류 (Supabase 검증 실패)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(e.getMessage()));
        }
    }
}