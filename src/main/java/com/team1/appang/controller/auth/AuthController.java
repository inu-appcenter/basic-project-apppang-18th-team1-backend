package com.team1.appang.controller.auth;


import com.team1.appang.dto.auth.*;
import com.team1.appang.security.JwtTokenProvider;
import com.team1.appang.service.auth.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    private final JwtTokenProvider jwtTokenProvider; //JWT 토큰 의존성 추가

    //로그인 API
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login (
            //DTO 검증을 위해서 Valid를 붙임. 검증이 끝나면 회원가입 처리 로직이 수행된다.
            //오류 원인은 bindingResult에 자동으로 담긴다
            @Valid @RequestBody LoginRequest request,
            BindingResult bindingResult) {
        //검증에서 오류가 발생했는지 확인
        if (bindingResult.hasErrors()) {
            //발생한 에러를 꺼내옴
            FieldError fieldError = bindingResult.getFieldError();
            //필드에러가 아니라 글로벌에러일수도 있으니 삼항연산자로 한차례 검사
            //오류 원인에 따른 메시지를 담는 변수
            String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "잘못된 요청입니다.";
            //응답 반환
            return ResponseEntity.badRequest().body(new LoginResponse(errorMessage, null, null));
        }
            try {
                //서비스 호출하여 인증과 토큰생성 진행
                LoginResponse response = authService.login(request);

                // 토큰을 담은 쿠키를 생성
                ResponseCookie responseCookie = ResponseCookie.from("refreshToken", response.refreshToken())
                        .httpOnly(true) //XSS공격을 차단
                        .secure(false) //HTTPS 통신에서만 쿠키가 전송되도록 함
                        .path("/") //모든 경로에서 쿠키 사용 가능
                        .maxAge(jwtTokenProvider.getRefreshTokenValidityInMilliseconds() / 1000) //쿠키 유효기간은 2주로 설정
                        .sameSite("Strict")
                        .build();
                //쿠키를 함께 실어 반환
                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                        .body(response);

                //로그인 실패시 아래 코드 진행
            } catch (IllegalArgumentException e){
                //401번으로 오류메시지를 보낸다.
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse(e.getMessage(), null, null));
            }

    }
    //이메일 중복 확인 API
    @GetMapping("/emails/exists") //경로에는 쿼리 파라미터를 적지 않음
    public ResponseEntity<EmailExistResponse> checkEmailExisits(
            //파라미터 바인딩을 보장하기 위해 @ModelAttribute 사용
            @Valid @ModelAttribute EmailCheckRequest request,
            BindingResult bindingResult) {

        //검증에서 오류가 발생했는지 확인
        if(bindingResult.hasErrors()){
            //발생한 에러를 꺼내옴
            FieldError fieldError = bindingResult.getFieldError();
            //필드에러가 아니라 글로벌에러일수도 있으니 삼항연산자로 한차례 검사
             //오류 원인에 따른 메시지를 담는 변수
            String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "잘못된 요청입니다.";

            //응답 반환
            return ResponseEntity.badRequest().body( EmailExistResponse.fail(errorMessage));
        }


        //사용가능할때는 존재하지 않을때(false)이므로 그 역 값을 넣음
        boolean isAvailable = !authService.isEmailExists(request.email());
        String message = isAvailable ? "사용 가능한 이메일입니다." : "이미 사용 중인 이메일입니다.";

        return ResponseEntity.ok(EmailExistResponse.success(isAvailable, message));
    }

    //회원가입 API
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
            Long memberId = authService.signup(request);
            return ResponseEntity.ok()
                    .body(SignUpResponse.success(memberId, "회원가입이 성공적으로 완료되었습니다."));
        } catch (IllegalArgumentException e){
            //서비스에서 보낸 예외처리 및 실패 응답을 반환 (409)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(SignUpResponse.fail(
                    "email",e.getMessage()));
        }
    }
}
