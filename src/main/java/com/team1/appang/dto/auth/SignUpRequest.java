package com.team1.appang.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

//회원가입에서 Request를 받을 DTO
@Getter
@NoArgsConstructor // JSON을 DTO로 변환
public class SignUpRequest {

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    @Size(min = 2, max = 20, message = "이름은 2자에서 20자 사이여야 합니다.")
    private String name;


    //비밀번호는 8자 이상, 영문+숫자 조합의 정규표현식으로 Patten 검증 사용
    //(?=.*[A-Za-z]) : 영문자 포함 여부 체크
    //(?=.*\d) : 숫자 포함 체크
    //[A-Za-z\d]{8,} : 글자수와 종류 제한. (영문+숫자만 가능)
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
    message = "비밀번호는 8자 이상이며, 영문과 숫자를 모두 포함해야 합니다.")
    private String password;

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    //숫자만 입력가능
    @NotBlank(message = "전화번호는 필수 입력 항목입니다.")
    @Pattern(regexp = "^\\d+$", message = "전화번호는 숫자만 입력해주세요.")
    private String phoneNumber;

    @NotBlank(message = "별명은 필수 입력 항목입니다.")
    @Size(min=2, max =20, message = "별명은 2자에서 20자 사이여야 합니다.")
    private String nickname;


}
