package com.stronger.momo.post.controller;

import com.stronger.momo.post.dto.CommentDto;
import com.stronger.momo.post.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/comment")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/write")
    public ResponseEntity<?> writeComment(Authentication authentication, @RequestBody CommentDto dto) {
        commentService.writeComment(authentication, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("댓글 작성이 완료되었습니다.");
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<?> deleteComment(Authentication authentication, @PathVariable Long commentId) throws AccessDeniedException {
        commentService.deleteComment(authentication, commentId);
        return ResponseEntity.status(HttpStatus.CREATED).body("댓글 삭제가 완료되었습니다.");
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateComment(Authentication authentication, @RequestBody CommentDto dto) throws AccessDeniedException {
        commentService.updateComment(authentication, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("댓글 수정이 완료되었습니다.");
    }

}
