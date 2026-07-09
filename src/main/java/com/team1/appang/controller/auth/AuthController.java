package com.team1.appang.controller.auth;


import com.team1.appang.dto.auth.SignUpRequest;
import com.team1.appang.dto.auth.SignUpResponse;
import com.team1.appang.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
=========================================
 설 명:
 회원가입 / 로그인 / 로그아웃을 모두 관리하는 컨트롤러
 각 API별로 분리하면 파일 개수가 너무 많아질듯해 하나로 관리
 비밀번호 재설정은 이미 완성된 상태라 분리를 유지
 ========================================
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor // 서비스 자동 주입을 위해 추가
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(
            //DTO 검증을 위해서 Valid를 붙임. 검증이 끝나면 회원가입 처리 로직이 수행된다.
            //오류 원인은 bindingResult에 자동으로 담긴다
            @Valid @RequestBody SignUpRequest request, BindingResult bindingResult){

        //일차적으로 DTO 검증에서 오류가 발생했는지 확인
        if(bindingResult.hasErrors()){
            //발생한 에러를 꺼내옴
            FieldError fieldError = bindingResult.getFieldError();
            //필드에러가 아니라 글로벌에러일수도 있으니 삼항연산자로 한차례 검사
            //오류원인 필트를 담는 변수
            String errorField = fieldError !=null ? fieldError.getField() : "unknown";
            //오류 원인에 따른 메시지를 담는 변수
            String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "잘못된 요청입니다.";

            //응답 반환
            return ResponseEntity.badRequest().body(SignUpResponse.fail(errorField,errorMessage));
        }

        try {
            //회원가입에 성공했다면 id와 함께 성공 메시지 반환
            Long memberId = authService.singup(request);
            return ResponseEntity.ok()
                    .body(SignUpResponse.success(memberId, "회원가입이 성공적으로 완료되었습니다."));
        } catch (IllegalArgumentException e){
            //서비스에서 보낸 예외처리 및 실패 응답을 반환 (409)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(SignUpResponse.fail(
                    "email",e.getMessage()));
        }
    }
}
