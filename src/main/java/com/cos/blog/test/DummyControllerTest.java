package com.cos.blog.test;

import com.cos.blog.model.RoleType;
import com.cos.blog.model.User;
import com.cos.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Supplier;

@RestController
@RequiredArgsConstructor
public class DummyControllerTest {

    private final UserRepository userRepository;

    @DeleteMapping("/dummy/user/{id}")
    public String delete(@PathVariable int id) {
        try {
            userRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            return "삭제에 실패하였습니다. 해당 id는 DB에 없습니다.";
        }

        return "삭제되었습니다. id : " + id;
    }

    //save 함수는 id를 전달하지 않으면 insert를 함
    //id를 전달했을 때 해당 id에 대한 데이터가 있으면 update를 하고
    //                      id에 대한 데이터가 없으면 insert를 함
    //email, password
    @Transactional  //함수 종료 시에 자동 commit
    @PutMapping("/dummy/user/{id}")
    public User updateUser(@PathVariable int id, @RequestBody User requestUser) {   //json 데이터를 요청 => Java Object(MessageConverter의 Jackson라이브러리가 변환해서 받아줌)
        System.out.println("id = " + id);
        System.out.println("password = " + requestUser.getPassword());
        System.out.println("email = " + requestUser.getEmail());

        User user = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("수정에 실패하였습니다.")
        );
        user.setPassword(requestUser.getPassword());
        user.setEmail(requestUser.getEmail());

//        userRepository.save(user);
        return user;
    }

    @GetMapping("/dummy/users")
    private List<User> list() {
        return userRepository.findAll();
    }

    // 한 페이지당 2건
    @GetMapping("/dummy/user")
    public List<User> pageList(@PageableDefault(size = 2, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return userRepository.findAll(pageable).getContent();
    }

    @PostMapping("/dummy/join")
    public String join(@ModelAttribute User user) { //key = value (약속된 규칙)
        System.out.println("id = " + user.getId());
        System.out.println("username: " + user.getUsername());
        System.out.println("password: " + user.getPassword());
        System.out.println("email: " + user.getEmail());
        System.out.println("role: " + user.getRole());
        System.out.println("createDate: " + user.getCreateDate());

        user.setRole(RoleType.USER);
        userRepository.save(user);
        return "회원가입이 완료되었습니다.";
    }

    @GetMapping("/dummy/user/{id}")
    public User detail(@PathVariable int id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 유저는 없습니다. id: " + id)
        );
        return user;
    }
}
