package com.cos.blog.controller;

import com.cos.blog.config.auth.PrincipalDetail;
import com.cos.blog.model.KakaoProfile;
import com.cos.blog.model.OAuthToken;
import com.cos.blog.model.User;
import com.cos.blog.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;


//인증이 되지 않은 사용자들이 출입할 수 있는 경로를 /auth/** 허용
//그냥 주소가 / 이면 index.jsp 허용
//static 이하에 있는 /js/**, /css/**, /image/**
@Controller
@RequiredArgsConstructor
public class UserController {

    @Value("${cos.key}")
    private String cosKey;

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

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

    @GetMapping("/auth/kakao/callback")
    public String kakaoCallback(@RequestParam String code,
                                              @AuthenticationPrincipal PrincipalDetail principal) { // Data를 리턴해주는 컨트롤러 함수

        // Post 방식으로 key=value 데이터를 요청 (카카오 쪽으로)
        RestTemplate rt = new RestTemplate();

        // HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HttpBody 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "ea237f94b6a8360a3e7b0878cd5e2f8d");
        params.add("redirect_uri", "http://localhost:8080/auth/kakao/callback");
        params.add("code", code);

        // HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
            new HttpEntity<>(params, headers);

        // Http 요청하기 - Post방식으로 - response 변수의 응답을 받음
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // Gson, Json Simple, ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        OAuthToken oauthToken = null;
        try {
            oauthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println("카카오 액세스 토큰 : " + oauthToken.getAccess_token());

        RestTemplate rt2 = new RestTemplate();

        // HttpHeader 오브젝트 생성
        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization", "Bearer " + oauthToken.getAccess_token());
        headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest2 =
                new HttpEntity<>(headers2);

        // Http 요청하기 - Post방식으로 - response 변수의 응답을 받음
        ResponseEntity<String> response2 = rt2.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest2,
                String.class
        );

        ObjectMapper objectMapper2 = new ObjectMapper();
        KakaoProfile kakaoProfile = null;
        try {
            kakaoProfile = objectMapper2.readValue(response2.getBody(), KakaoProfile.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // User 오브젝트 : username, password, email
        System.out.println("카카오 아이디(번호) : " + kakaoProfile.getId());
        System.out.println("카카오 닉네임 : " + kakaoProfile.getKakao_account().getProfile().getNickname());

        System.out.println("블로그서버 유저네임 : " + kakaoProfile.getKakao_account().getProfile().getNickname()+"_"+kakaoProfile.getId());
        System.out.println("블로그서버 패스워드 : " + cosKey);

        User kakaoUser = User.builder()
                .username(kakaoProfile.getKakao_account().getProfile().getNickname()+"_"+kakaoProfile.getId())
                .email("audgns@gmail.com")
                .password(cosKey)
                .oauth("kakao")
                .build();

        // 가입자 혹은 비가입자 체크 해서 처리
        User originUser = userService.findUser(kakaoUser.getUsername());

        if (originUser.getUsername() == null) {
            System.out.println("기존 회원이 아니기에 자동 회원가입을 진행합니다.");
            userService.join(kakaoUser);
        }

        System.out.println("자동 로그인을 진행합니다.");
        // 로그인 처리
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(kakaoUser.getUsername(), cosKey));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "redirect:/";
    }
}
