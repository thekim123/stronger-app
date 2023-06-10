package com.stronger.momo.post.controller;

import com.stronger.momo.post.service.SubscribeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/subscribe")
public class SubscribeController {

    private final SubscribeService subscribeService;


    @PostMapping("/{toUserId}")
    public ResponseEntity<?> subscribe(
            Authentication authentication, @PathVariable Long toUserId) {
        subscribeService.subscribe(authentication, toUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body("구독이 완료되었습니다.");
    }

    @DeleteMapping("/{toUserId}")
    public ResponseEntity<?> unSubscribe(
            Authentication authentication, @PathVariable Long toUserId) {
        subscribeService.unSubscribe(authentication, toUserId);
        return ResponseEntity.status(HttpStatus.OK).body("구독이 취소되었습니다.");
    }


}
