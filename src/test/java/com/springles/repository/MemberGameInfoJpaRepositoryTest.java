//package com.springles.repository;
//
//import com.springles.domain.constants.GameRole;
//import com.springles.domain.constants.Level;
//import com.springles.domain.constants.ProfileImg;
//import com.springles.domain.entity.GameRecord;
//import com.springles.domain.entity.Member;
//import com.springles.domain.entity.MemberGameInfo;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@Transactional
//class MemberGameInfoJpaRepositoryTest {
//
//    @Autowired
//    MemberJpaRepository memberJpaRepository;
//    @Autowired
//    MemberGameInfoJpaRepository memberGameInfoJpaRepository;
//    @Autowired
//    PasswordEncoder passwordEncoder;
//
//    @AfterEach
//    public void deleteAll(){
//        memberJpaRepository.deleteAll();
//        memberGameInfoJpaRepository.deleteAll();
//    }
//
//    @Test
//    @DisplayName("memberName에 해당하는 MemberGameInfo 반환 테스트 - CASE.성공")
//    void findByMemberId() {
//        // given
//        Member member = Member.builder()
//                .memberName("mafia1").password(passwordEncoder.encode("password1!"))
//                .email("mafia@gmail.com").role("USER").isDeleted(false).build();
//
//        memberGameInfoJpaRepository.save(
//                MemberGameInfo.builder()
//                        .id(1L)
//                        .member(member)
//                        .profileImg(ProfileImg.PROFILE01)
//                        .inGameRole(GameRole.MAFIA)
//                        .nickname("나는야마피아")
//                        .level(Level.BEGINNER)
//                        .exp(0L)
//                        .build()
//        );
//
//        // when
//        Optional<MemberGameInfo> optionalMemberGameInfo = memberGameInfoJpaRepository.findByMemberId(1L);
//
//        // then
//        assertEquals(optionalMemberGameInfo.get().getMember().getId(), 1L);
//        assertEquals(optionalMemberGameInfo.get().getProfileImg(), ProfileImg.PROFILE01);
//        assertEquals(optionalMemberGameInfo.get().getInGameRole(), GameRole.MAFIA);
//        assertEquals(optionalMemberGameInfo.get().getNickname(), "나는야마피아");
//        assertEquals(optionalMemberGameInfo.get().getLevel(), Level.BEGINNER);
//        assertEquals(optionalMemberGameInfo.get().getExp(), 0L);
//    }
//
//    @Test
//    @DisplayName("memberId에 해당하는 멤버 랭킹 반환 테스트 - CASE.성공")
//    void findByMemberRank() {
//        // given
//        // Member 생성
//        Member member1 = Member.builder()
//                .memberName("mafia1").password(passwordEncoder.encode("password1!"))
//                .email("mafia@gmail.com").role("USER").isDeleted(false).build();
//
//        Member member2 = Member.builder()
//                .memberName("mafia2").password(passwordEncoder.encode("password1!"))
//                .email("mafia@gmail.com").role("USER").isDeleted(false).build();
//
//        memberJpaRepository.save(member1);
//        memberJpaRepository.save(member2);
//
//        // MemberGameInfo 생성
//        memberGameInfoJpaRepository.save(
//                MemberGameInfo.builder()
//                        .member(member1).profileImg(ProfileImg.PROFILE01)
//                        .inGameRole(GameRole.MAFIA)
//                        .nickname("나는야마피아1").level(Level.BEGINNER).exp(100L).build());
//
//        memberGameInfoJpaRepository.save(
//                MemberGameInfo.builder()
//                        .member(member2).profileImg(ProfileImg.PROFILE01)
//                        .inGameRole(GameRole.MAFIA)
//                        .nickname("나는야마피아1").level(Level.BEGINNER).exp(200L).build());
//
//        // when
//        Long result = memberGameInfoJpaRepository.findByMemberRank(2L);
//
//        // then
//        assertEquals(result, 1L);
//    }
//}