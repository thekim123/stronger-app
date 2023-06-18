package com.stronger.momo.post.controller;

import com.stronger.momo.post.dto.PostCreateDto;
import com.stronger.momo.post.entity.Post;
import com.stronger.momo.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/post")
public class PostController {
    private final PostService postService;


    @GetMapping("/list")
    public ResponseEntity<?> getPostList(Authentication authentication
            , @PageableDefault(size = 10) Pageable pageable) {
        Page<Post> snsList = postService.getSnsList(authentication, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(snsList);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getProfilePostList(
            Authentication authentication
            , @PathVariable Long userId
            , @PageableDefault(size = 9) Pageable pageable) {
        Page<Post> snsList = postService.getProfileSnsList(authentication, userId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(snsList);

    }

    @PostMapping("/write")
    public ResponseEntity<?> writePost(Authentication authentication, PostCreateDto dto) throws IOException {
        System.out.println(dto);
        postService.writePost(authentication, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("sns 작성이 완료되었습니다.");
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<?> deletePost(Authentication authentication, @PathVariable Long postId) throws AccessDeniedException {
        postService.deletePost(authentication, postId);
        return ResponseEntity.status(HttpStatus.OK).body("sns 삭제가 완료되었습니다.");
    }

    @PutMapping("/update")
    public ResponseEntity<?> updatePost(Authentication authentication, @RequestBody PostCreateDto dto) throws AccessDeniedException {
        postService.updatePost(authentication, dto);
        return ResponseEntity.status(HttpStatus.OK).body("sns 수정이 완료되었습니다.");
    }


}
