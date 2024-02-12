package com.cos.blog.service;

import com.cos.blog.config.auth.PrincipalDetail;
import com.cos.blog.model.RoleType;
import com.cos.blog.model.User;
import com.cos.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//스프링이 컴포넌트 스캔을 통해서 Bean에 등록해줌. IoC를 해줌
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    public final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Transactional(readOnly = true)
    public User findUser(String username) {
        User user = userRepository.findByUsername(username).orElseGet(
                () -> new User()
        );
        return user;
    }

    public void join(User user) {
        String rawPassword = user.getPassword();
        String encPassword = encoder.encode(rawPassword);   //해쉬
        user.setPassword(encPassword);
        user.setRole(RoleType.USER);
        userRepository.save(user);
    }


    public void update(User user, PrincipalDetail principal) {
        User persistence = userRepository.findById(user.getId()).orElseThrow(
                () -> new IllegalArgumentException("회원 찾기 실패")
        );

        // Validate 체크 => oauth 필드에 값이 없으면 수정 가능
        if (persistence.getOauth() == null || persistence.getOauth().equals("")) {
            String rawPassword = user.getPassword();
            String encPassword = encoder.encode(rawPassword);
            persistence.setPassword(encPassword);
            persistence.setEmail(user.getEmail());
            principal.setUser(persistence);
        }


        //회원 수정 함수 종료 시 -> 서비스 종료 -> 트랜잭션 종료 -> commit이 자동으로 됨
    }
}
