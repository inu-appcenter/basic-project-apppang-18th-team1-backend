package com.team1.appang.service.auth;

import com.team1.appang.dto.auth.ResetPasswordRequest;
import com.team1.appang.exception.InvalidPasswordFormatException;
import com.team1.appang.exception.InvalidTokenException;
import com.team1.appang.exception.PasswordMismatchException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

/*
==================================
설명: Member의 비밀전호(인증) 관련 서비스를 모아놓은 클래스
기능: Member의 응답을 받고 길이, 동일함, 토큰, 이메일주소등을 검증한 뒤 비밀번호를 재설정함
==================================
 */
@Slf4j
@Service
@RequiredArgsConstructor //생성자 자동 생성
public class MemberPasswordService {
    //비밀번호 조건 확인을 위한 정규식
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$";
    private final RestClient restClient; //Bean으로 등록된 RestClient를 주입받음
    private final MemberPasswordUpdater memberPasswordUpdater; //DB 업데이트를 전담하는 별도 빈

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.anon-key}")
    private String supabaseAnonKey;


    //트랜잭션 없는 바깥 메서드. DB가 불필요한 부분까지만 먼저 처리한다.
    public String resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().matches(PASSWORD_PATTERN)) {
            throw new InvalidPasswordFormatException();
        }

        if (!request.getNewPassword().equals(request.getPasswordCheck())) {
            throw new PasswordMismatchException();
        }

        //DB조회보다 Supabase 토큰을 먼저 검증한다. (외부 API 호출이기 때문에 트랜잭션에 포함 X)

        boolean isTokenValid = verifySupabaseToken(request.getSupabaseToken(), request.getEmail());
        if (!isTokenValid) {
            throw new InvalidTokenException();
        }

        //다른 빈을 통해 호출하므로 진짜 프록시를 거쳐 @Transactional이 정상 작동함
        return memberPasswordUpdater.updatePassword(request);
    }

    //Supabase Auth API를 호출하여 프론트가 보낸 토큰을 검증하는 메서드
    @SuppressWarnings("unchecked")
    private boolean verifySupabaseToken(String token, String requestEmail){
        try{
            String url = supabaseUrl + "/auth/v1/user";

            Map<String, Object> body = restClient.get()
                    .uri(url)
                    .header("Authorization", "Bearer " + token)
                    .header("apikey", supabaseAnonKey)
                    .retrieve()
                    .body(Map.class);

            if (body != null && body.containsKey("email")) {
                String tokenEmail = (String) body.get("email");
                return tokenEmail.equalsIgnoreCase(requestEmail);
            }

        }catch (Exception e) {
            log.warn("Supabase 토큰 검증 실패: email={}", requestEmail, e);
            return false;
        }
        return false;
    }
}