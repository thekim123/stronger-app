package com.stronger.momo.post.controller;

import com.stronger.momo.post.service.LikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/likes")
public class LikesController {

    private final LikesService likesService;

    @PostMapping("/{postId}")
    public ResponseEntity<?> likePost(Authentication authentication, @PathVariable Long postId) {
        likesService.likePost(authentication, postId);
        return ResponseEntity.status(HttpStatus.OK).body("좋아요 완료");
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> unLikePost(Authentication authentication, @PathVariable Long postId) {
        likesService.unLikePost(authentication, postId);
        return ResponseEntity.status(HttpStatus.OK).body("좋아요 취소");
    }
}
