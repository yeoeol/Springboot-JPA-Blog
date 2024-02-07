package com.cos.blog.test;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@RequiredArgsConstructor
public class Member {
    private int id;
    private String username;
    private String password;
    private String email;
}
