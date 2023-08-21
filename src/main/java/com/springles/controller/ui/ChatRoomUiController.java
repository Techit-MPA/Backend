package com.springles.controller.ui;


import com.springles.domain.dto.chatroom.ChatRoomReqDTO;
import com.springles.service.ChatRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("v1/home")
@RequiredArgsConstructor
@Slf4j
public class ChatRoomUiController {

    private final ChatRoomService chatRoomService;

    // 게임 만들기 페이지
    @GetMapping("/add")
    public String writeRoom(Model model, ChatRoomReqDTO requestDto){
        model.addAttribute("requestDto",requestDto);
        return "home/add";
    }
    // 게임 만들기
    @PostMapping("/add")
    public String createRoom(@ModelAttribute("requestDto") @Valid ChatRoomReqDTO requestDto){
        chatRoomService.createChatRoom(requestDto);
        return "redirect:index";
    }
    // 게임 목록 리스트 페이지
    @GetMapping("/index")
    public String home(Model model) {
        model.addAttribute("data", "Hello");
        return "home/index";
    }


}
