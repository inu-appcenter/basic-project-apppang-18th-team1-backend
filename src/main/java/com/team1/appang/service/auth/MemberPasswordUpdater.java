package com.team1.appang.service.auth;

import com.team1.appang.dto.auth.ResetPasswordRequest;
import com.team1.appang.entity.Member;
import com.team1.appang.exception.MemberNotFoundException;
import com.team1.appang.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

//DB 조회 + 비밀번호 변경만 전담하는 별도의 빈
//MemberPasswordService와 분리해서, 진짜 프록시를 통한 호출이 이뤄지도록 함
//어노테이션 무시로 DB의 실제 데이터가 변하지 않는 오류를 고치기 위해 클래스 분리
@Component
@RequiredArgsConstructor
public class MemberPasswordUpdater {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String updatePassword(ResetPasswordRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(MemberNotFoundException::new);

        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        member.updatePassword(encodedPassword);

        return "비밀번호가 재설정 되었습니다.";
    }
}