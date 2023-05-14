package com.stronger.momo.user.controller;

import com.stronger.momo.user.dto.UserDto;
import com.stronger.momo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        UserDto dto = userService.getProfile(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(Authentication authentication, @RequestBody UserDto dto) {
        userService.updateProfile(authentication, dto);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

}
