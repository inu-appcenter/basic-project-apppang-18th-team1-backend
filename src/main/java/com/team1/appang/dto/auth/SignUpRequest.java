package com.team1.appang.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "사용자 이름 (2~20자)", example = "홍길동")
    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    @Size(min = 2, max = 20, message = "이름은 2자에서 20자 사이여야 합니다.")
    private String name;

    //비밀번호는 8자 이상, 영문+숫자 조합의 정규표현식으로 Patten 검증 사용
    //(?=.*[A-Za-z]) : 영문자 포함 여부 체크
    //(?=.*\d) : 숫자 포함 체크
    //[A-Za-z\d]{8,} : 글자수와 종류 제한. (영문+숫자만 가능)
    //!@#$%^&*()_+|~={};:'<>,.?/ 특수문자 허용
    @Schema(description = "비밀번호 (영문+숫자+특수문자 포함 8자 이상)", example = "password1!")
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Pattern(regexp =  "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+|~={};:'<>,.?/]).{8,}$",
            message = "비밀번호 형식이 옳지 않습니다.")
    private String password;

    @Schema(description = "이메일", example = "test@example.com")
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    //숫자만 입력가능
    @Schema(description = "전화번호 (숫자만)", example = "01012345678")
    @NotBlank(message = "전화번호는 필수 입력 항목입니다.")
    @Pattern(regexp = "^\\d+$", message = "전화번호는 숫자만 입력해주세요.")
    private String phoneNumber;

    @Schema(description = "별명 (2~20자)", example = "길동이")
    @NotBlank(message = "별명은 필수 입력 항목입니다.")
    @Size(min=2, max =20, message = "별명은 2자에서 20자 사이여야 합니다.")
    private String nickname;


}