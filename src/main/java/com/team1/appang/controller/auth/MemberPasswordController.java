package com.team1.appang.controller.auth;

/*
설명: 비밀번호 재설정에 사용하는 코드
MemberPasswordService를 사용한다
 */

import com.team1.appang.dto.auth.MessageResponse;
import com.team1.appang.dto.auth.ResetPasswordRequest;
import com.team1.appang.exception.InvalidPasswordFormatException;
import com.team1.appang.exception.InvalidTokenException;
import com.team1.appang.exception.MemberNotFoundException;
import com.team1.appang.exception.PasswordMismatchException;
import com.team1.appang.service.auth.MemberPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/password") //공동주소 묶기
@RequiredArgsConstructor
public class MemberPasswordController {
    private final MemberPasswordService memberPasswordService;


    //비밀번호 재설정 API
    //URL: POST /api/user/password/reset
    //사용자가 보낸 Request를 받기 위해 @RequestBody 사용
    @PatchMapping("/reset")
    public ResponseEntity<MessageResponse> resetPassword(@RequestBody ResetPasswordRequest request){
        try {
            memberPasswordService.resetPassword(request);
            //예외 없이 끝까지 통과했다면 성공. 성공 메시지는 여기 Controller가 결정
            return ResponseEntity.ok(new MessageResponse("비밀번호가 재설정 되었습니다."));

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