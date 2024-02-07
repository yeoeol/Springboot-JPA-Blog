package com.cos.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    @GetMapping("/user/join")
    public String joinForm() {
        return "user/joinForm";
    }

    @GetMapping("/user/login")
    public String loginForm() {
        return "user/loginForm";
    }
}
