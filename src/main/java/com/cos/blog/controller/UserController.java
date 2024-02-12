package com.cos.blog.controller;

import com.cos.blog.config.auth.PrincipalDetail;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

//인증이 되지 않은 사용자들이 출입할 수 있는 경로를 /auth/** 허용
//그냥 주소가 / 이면 index.jsp 허용
//static 이하에 있는 /js/**, /css/**, /image/**
@Controller
public class UserController {

    @GetMapping("/auth/join")
    public String joinForm() {
        return "user/joinForm";
    }

    @GetMapping("/auth/login")
    public String loginForm() {
        return "user/loginForm";
    }

    @GetMapping("/user/update")
    public String updateForm() {
        return "user/updateForm";
    }
}
