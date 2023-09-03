package com.springles.controller.ui;
import com.springles.domain.dto.member.*;
import com.springles.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("v1")
@RequiredArgsConstructor
@Slf4j
public class MemberUiController {

    private final MemberService memberService;

    // 회원가입 페이지 조회
    @GetMapping("/signup")
    public String signUpPage(Model model, MemberCreateRequest memberDto) {
        model.addAttribute("memberDto", memberDto);
        return "member/sign-up";
    }

    // 회원가입 POST 요청
    @PostMapping("/signup")
    public String signup(Model model, @Valid MemberCreateRequest memberDto
    ) {
        model.addAttribute("memberDto", memberDto);
        memberService.signUp(memberDto);
        return "redirect:login-page";
    }

    // 로그인 페이지 조회
    @GetMapping("/login-page")
    public String loginPage(Model model, MemberLoginRequest memberDto) {
        model.addAttribute("memberDto", memberDto);
        return "member/login";
    }

    // 로그인 POST
    @PostMapping("/login")
    public String signup(@ModelAttribute("memberDto") @Valid MemberLoginRequest memberDto, HttpServletResponse response)
    {
        // 로그인 성공, Token 정보 받기
        MemberLoginResponse memberLoginResponse = memberService.login(memberDto);
        // AccessToken Cookie에 저장
        String accessToken = memberLoginResponse.getAccessToken();
        setAtkCookie("accessToken", accessToken, response);
        // RefreshToken id값 Cookie에 저장
        String refreshTokenId = memberLoginResponse.getRefreshToken().getId();
        setRtkCookie("refreshTokenId", refreshTokenId, response);

        return "redirect:index";
    }

    // 로그아웃 요청
    @PostMapping("/logout")
    public String logout(
            HttpServletRequest request
//            @RequestHeader(value = "Authorization") String authHeader
    ) {
        Cookie[] cookies = request.getCookies();
        String authHeader = cookies[0].getValue();
        memberService.logout(authHeader);

        return "redirect:index";
    }

    // 사용자 정보 요청
    @PostMapping("/info")
    public MemberInfoResponse info(String authHeader) {
        return memberService.getUserInfo(authHeader);
    }

    // 쿠키 설정
//    public void setCookie(String name, String value, HttpServletResponse response) {
//        Cookie cookie = new Cookie(name, value);
//        cookie.setDomain("localhost");
//        cookie.setPath("/");
//        cookie.setMaxAge(60*60);
//        cookie.setHttpOnly(true);
//        cookie.setSecure(true);
//        response.addCookie(cookie);
//    }

    // accessToken 쿠키 설정
    public void setAtkCookie(String name, String value, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);  // 1시간(테스트용)
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }

    // refreshToken 쿠키 설정
    public void setRtkCookie(String name, String value, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 14);    // 2주
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }

    // 아이디 찾기 GET
    @GetMapping("/vertification-id")
    public String vertificationId(
            Model model,
            @ModelAttribute("member") MemberVertifIdRequest memberDto
    )
    {
        model.addAttribute("member", memberDto);
        return "member/vertification-id";
    }

    // 비밀번호 찾기 GET
    @GetMapping("/vertification-pw")
    public String vertificationId(
            Model model,
            @ModelAttribute("member") MemberVertifPwRequest memberDto
    )
    {
        model.addAttribute("member", memberDto);
        return "member/vertification-pw";
    }

    // 마이페이지
    @GetMapping("/my-page")
    public String memberProflie(
            Model model,
            HttpServletRequest request
    )
    {
        // accessToken 추출
        String accessToken = (String)request.getAttribute("accessToken");

        // 프로필 조회
        MemberProfileRead profileInfo = memberService.readProfile(accessToken);

        // 멤버 게임기록 조회
        MemberRecordResponse memberRecord = memberService.readRecord(accessToken);

        model.addAttribute("profileInfo", profileInfo);
        model.addAttribute("record", memberRecord);

        return "member/my-page";
    }
}
