package com.team1.appang.service.auth;

import com.team1.appang.dto.auth.ResetPasswordRequest;
import com.team1.appang.entity.Member;
import com.team1.appang.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

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
    //비밀번호 조건 확인을 위한 정규식
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$";
    private final MemberRepository memberRepository;
    private final RestClient restClient; //Bean으로 등록된 RestClient를 주입받음
    private final PasswordEncoder passwordEncoder;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.anon-key}")
    private String supabaseAnonKey;


    //트랜잭션 없는 바깥 메서드. DB가 불필요한 부분까지만 먼저 처리한다.
    public String resetPassword(ResetPasswordRequest request) {
        if (request.getNewPassword().matches(PASSWORD_PATTERN)) {
            return ("비밀번호는 8자 이상이며 영문과 숫자를 포함해야 합니다.");
        }

        if (!request.getNewPassword().equals(request.getPasswordCheck())) {
            return ("비밀번호가 일치하지 않습니다.");
        }

        //DB조회보다 Supabase 토큰을 먼저 검증한다. (외부 API 호출이기 때문에 트랜잭션에 포함 X)
        boolean isTokenValid = verifySupabaseToken(request.getSupabaseToken(), request.getEmail());
        if (!isTokenValid) {
            return "만료되거나 유효하지 않은 요청입니다.";
        }

        //트랜잭션이 필요한 부분을 별도의 메서드로 만들고, 이를 호출함
        return updatePassword(request);
    }

    //DB 조회 및 변경. 여기에만 트랜잭션을 걸어 범위를 최소화 한다.
    @Transactional
    public String updatePassword(ResetPasswordRequest request) {
        Optional<Member> memberOpt = memberRepository.findByEmail(request.getEmail());
        if(memberOpt.isEmpty()){
            return("존재하지 않는 회원입니다.");
        }

        //위 조건을 모두 통과한다면 비밀번호 변경을 실행
        Member member = memberOpt.get();
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        member.updatePassword(encodedPassword);

        return("비밀번호가 재설정 되었습니다.");
    }

    //Supabase Auth API를 호출하여 프론트가 보낸 토큰을 검증하는 메서드
    @SuppressWarnings("unchecked") //unchecked경고는 무시
    private boolean verifySupabaseToken(String token, String requestEmail){
        try{
            //Supabase 프로젝트 주소에 고정된 API 엔드포인트를 이어붙여 url을 생성
            String url = supabaseUrl + "/auth/v1/user";

            //RestClient는 요청을 체이닝 방식으로 구성함
            //get 방식으로 요청 생성
            Map<String, Object> body = restClient.get()
                    .uri(url) //url 로 목적지 지정
                    //"Bearer "뒤에 사용자가 보낸 토큰을 이어 붙여 인증 토큰임을 알림
                    .header("Authorization", "Bearer " + token)
                    //어느 프로젝트에서 요청하는지 식별하기 위한 API 키값 헤더 생성
                    .header("apikey", supabaseAnonKey)
                    //실제로 요청을 전송
                    .retrieve() //응답 상태 코드가 400번대거나 500번대면 자동으로 예외를 던짐
                    .body(Map.class); //응답 본문(JSON) 형태를 Map 객체로 변환해서 꺼냄

            //응답 자체가 비어있지 않은지, JSON 안에 email이라는 키가 들어가 있는 지 확인
            if (body != null && body.containsKey("email")) {
                String tokenEmail = (String) body.get("email"); //있다면 email값을 꺼내 String으로 형 변환
                //토큰에 담긴 이메일과 사용자가 요청 본문에 보낸 이메일이 같은지 비교
                return tokenEmail.equalsIgnoreCase(requestEmail);
            }

        }catch (Exception e) { //서버 오류 또는 만료된 토큰
            return false;
        }
        return false;
    }

}
