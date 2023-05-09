package com.stronger.momo.sns.controller;

import com.stronger.momo.sns.dto.CommentDto;
import com.stronger.momo.sns.dto.SnsDto;
import com.stronger.momo.sns.service.SnsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sns")
public class SnsController {
    private final SnsService snsService;

    @PostMapping("/write")
    public ResponseEntity<?> writeSns(Authentication authentication, @RequestBody SnsDto dto) {
        snsService.writeSns(authentication, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("sns 작성이 완료되었습니다.");
    }

    @DeleteMapping("/delete/{snsId}")
    public ResponseEntity<?> deleteSns(Authentication authentication, @PathVariable Long snsId) throws AccessDeniedException {
        snsService.deleteSns(authentication, snsId);
        return ResponseEntity.status(HttpStatus.OK).body("sns 삭제가 완료되었습니다.");
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateSns(Authentication authentication, @RequestBody SnsDto dto) throws AccessDeniedException {
        snsService.updateSns(authentication, dto);
        return ResponseEntity.status(HttpStatus.OK).body("sns 수정이 완료되었습니다.");
    }

    @PostMapping("/comment/write")
    public ResponseEntity<?> writeComment(Authentication authentication, @RequestBody CommentDto dto) {
        snsService.writeComment(authentication, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("댓글 작성이 완료되었습니다.");
    }

    @DeleteMapping("/comment/delete/{commentId}")
    public ResponseEntity<?> deleteComment(Authentication authentication, @PathVariable Long commentId) throws AccessDeniedException {
        snsService.deleteComment(authentication, commentId);
        return ResponseEntity.status(HttpStatus.CREATED).body("댓글 삭제가 완료되었습니다.");
    }

    @PutMapping("/comment/update")
    public ResponseEntity<?> updateComment(Authentication authentication, @RequestBody CommentDto dto) throws AccessDeniedException {
        snsService.updateComment(authentication, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("댓글 수정이 완료되었습니다.");
    }

}
