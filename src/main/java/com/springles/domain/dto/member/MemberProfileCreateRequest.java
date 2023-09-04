package com.springles.domain.dto.member;

import com.springles.domain.constants.GameRole;
import com.springles.domain.constants.Level;
import com.springles.domain.constants.ProfileImg;
import com.springles.domain.entity.MemberGameInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberProfileCreateRequest {

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 10, message = "닉네임은 2 ~ 10자 사이여야 합니다.")
    private String nickname;

    @NotNull
    private int profileImgNum;

    private ProfileImg profileImg;

    public MemberGameInfo newMemberGameInfo(MemberProfileCreateRequest memberDto, Long memberId) {

        if(memberDto.getProfileImgNum() == 1) {
            profileImg = ProfileImg.PROFILE01;
        } else if (memberDto.getProfileImgNum() == 2) {
            profileImg = ProfileImg.PROFILE02;
        } else if (memberDto.getProfileImgNum() == 3) {
            profileImg = ProfileImg.PROFILE03;
        } else if (memberDto.getProfileImgNum() == 4) {
            profileImg = ProfileImg.PROFILE04;
        } else if (memberDto.getProfileImgNum() == 5) {
            profileImg = ProfileImg.PROFILE05;
        } else if (memberDto.getProfileImgNum() == 6) {
            profileImg = ProfileImg.PROFILE06;
        }

        return MemberGameInfo.builder()
                .nickname(memberDto.getNickname())
                .profileImg(profileImg)
                .level(Level.BEGINNER)
                .exp(0L)
                .inGameRole(GameRole.NONE)
                .isObserver(false)
                .memberId(memberId)
                .build();
    }
}
