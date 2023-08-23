package com.springles.domain.dto.member;

import com.springles.domain.entity.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberUpdateRequest {

    // 게임 아이디
    private String memberName;

    // 비밃번호
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 6, message = "비밀번호는 6자 이상이여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*]+$", message = "비밀번호는 영문 대소문자, 숫자, 특수문자(!,@,#,$,%,^,&,*)만 입력할 수 있습니다.")
    private String password;

    // 비밃번호 확인
    private String passwordConfirm;

    // 이메일
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;
}