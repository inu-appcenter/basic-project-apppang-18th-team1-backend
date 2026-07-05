package com.team1.appang.controller;

/*
설명: 비밀번호 재설정에 사용하는 코드
userPasswordService를 사용한다
 */

import com.team1.appang.dto.MessageResponse;
import com.team1.appang.dto.ResetPasswordRequest;
import com.team1.appang.service.UserPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/password") //공동주소 묶기
@RequiredArgsConstructor
public class UserPasswordController {
    private final UserPasswordService userPasswordService;

    //인증 메일 발송 API
    //URL: Post, api/user/password/verifications
    @PostMapping("/verifications")
    public ResponseEntity<MessageResponse> sendVerificationEmail(){
        return ResponseEntity.ok(new MessageResponse("인증 메일이 발송되었습니다."));
    }

    //인증 코드 확인 API
    //URL: Patch,  api/user/password/verifications
    @PatchMapping("/verifications")
    public ResponseEntity<MessageResponse> verifyCode(){
        return ResponseEntity.ok(new MessageResponse("인증이 완료되었습니다."));
    }

    //비밀번호 재설정 API
    //URL: POST /api/user/password/reset
    //사용자가 보낸 Request를 받기 위해 @RequestBody 사용
    @PatchMapping("/reset")
    public ResponseEntity<MessageResponse> resetPassword(@RequestBody ResetPasswordRequest request){
        String resultMessage = userPasswordService.resetPassword(
                request.getEmail(),
                request.getNewPassword(),
                request.getPasswordCheck(),
                request.getResetToken()
        );

        //200, 성공
        if (resultMessage.contains("비밀번호가 재설정 되었습니다."))
            return ResponseEntity.ok(new MessageResponse(resultMessage));

        //404, 회원이 존재하지 않음
        if (resultMessage.contains("존재하지 않는 회원입니다."))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(resultMessage));

        //401, 토큰 오류거나 이메일이 불일치
        if (resultMessage.contains("만료되거나 유효하지 않은 요청입니다.")||resultMessage.contains("이메일 정보가 일치하지 않습니다."))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(resultMessage));

        //나머지는 400 에러로 처리
        return ResponseEntity.badRequest().body(new MessageResponse(resultMessage));
    }



}
