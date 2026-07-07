package com.team1.appang.service;

import com.team1.appang.dto.ResetPasswordRequest;
import com.team1.appang.entity.Member;
import com.team1.appang.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

/*
==================================
설명: Member의 비밀전호(인증) 관련 서비스를 모아놓은 클래스
기능: Member의 응답을 받고 길이, 동일함, 토큰, 이메일주소등을 검증한 뒤 비밀번호를 재설정함
==================================
 */
@Service
@RequiredArgsConstructor //생성자 자동 생성
public class MemberPasswordService {

    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.anon-key}")
    private String supabaseAnonKey;


    @Transactional //DB 값을 수정해야하므로
    public String resetPassword(ResetPasswordRequest request) {
        if (request.getNewPassword().length() < 8){
            return("비밀번호는 최소 8자리 이상이어야 합니다.");
        }

        if (!request.getNewPassword().equals(request.getPasswordCheck())){
            return("비밀번호가 일치하지 않습니다.");
        }

        Optional<Member> memberOpt = memberRepository.findByEmail(request.getEmail());
        if(memberOpt.isEmpty()){
            return("존재하지 않는 회원입니다.");
        }

        boolean isTokenValid = verifySupabaseToken(request.getSupabaseToken(), request.getEmail());
        if (!isTokenValid) {
            return "만료되거나 유효하지 않은 요청입니다.";
        }

        //위 조건을 모두 통과한다면 비밀번호 변경을 실행
        Member member = memberOpt.get();
        member.updatePassword(request.getNewPassword());

        return("비밀번호가 재설정 되었습니다.");
    }

    //Supabase Auth API를 호출하여 프론트가 보낸 토큰을 검증하는 메서드
    private boolean verifySupabaseToken(String token, String requestEmail){
        try{
            String url = supabaseUrl + "/auth/v1/user";

            // Http 요청 헤더 구성
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.set("apikey", supabaseAnonKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
                Map<String, Object> body = responseEntity.getBody();
                if (body.containsKey("email")) {
                    String tokenEmail = (String) body.get("email");
                    return tokenEmail.equalsIgnoreCase(requestEmail);
                }
            }

        }catch (Exception e) { //서버 오류 또는 만료된 토큰
            return false;
        }
        return false;
    }
}
