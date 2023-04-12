package com.stronger.momo.user.controller;

import com.stronger.momo.user.dto.UserDto;
import com.stronger.momo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody UserDto dto) {
        userService.join(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 완료");
    }

}
