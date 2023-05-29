package com.stronger.momo.report.controller;

import com.stronger.momo.report.dto.FeedbackDto;
import com.stronger.momo.report.dto.SelfFeedbackDto;
import com.stronger.momo.report.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("api/feedback")
@RestController
public class FeedbackController {

    private final FeedbackService feedbackService;


    /**
     * @param dto 교관 피드백 작성 dto
     * @apiNote 교관 피드백 작성 api
     */
    @PostMapping("/feedback")
    public ResponseEntity<?> createFeedback(
            Authentication authentication,
            @RequestBody FeedbackDto dto) {
        FeedbackDto result =
                feedbackService.createFeedback(authentication, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }


    /**
     * @param feedbackId 교관 피드백 id
     * @param memberId   계획 id
     * @apiNote 교관 피드백 삭제 api
     */
    @DeleteMapping("/{memberId}/feedback/{feedbackId}")
    public ResponseEntity<?> deleteFeedback(
            @PathVariable Long feedbackId,
            @PathVariable Long memberId) {
        feedbackService.deleteFeedback(feedbackId, memberId);
        return ResponseEntity.status(HttpStatus.OK)
                .body("교관 피드백 삭제가 완료되었습니다");
    }


    /**
     * @param dto 교관 피드백 dto
     * @apiNote 교관 피드백 수정 서비스 메서드
     */
    @PutMapping("/{memberId}/feedback")
    public ResponseEntity<?> updateFeedback(
            @RequestBody FeedbackDto dto,
            @PathVariable Long memberId) {
        feedbackService.updateFeedback(dto, memberId);
        return ResponseEntity.status(HttpStatus.OK)
                .body("교관 피드백 수정이 완료되었습니다.");
    }


    /**
     * @param dto      셀프피드백 작성 dto
     * @param memberId 계획 id
     * @apiNote 셀프 피드백 작성 api
     */
    @PostMapping("/{memberId}/self")
    public ResponseEntity<?> createSelfFeedback(
            @RequestBody SelfFeedbackDto dto,
            @PathVariable Long memberId) {
        SelfFeedbackDto result = feedbackService
                .createSelfFeedback(dto, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }


    /**
     * @param authentication 유저 인증 정보
     * @param selfId         셀프 피드백 id
     * @apiNote 피드백 삭제 api
     */
    @DeleteMapping("/self/{selfId}")
    public ResponseEntity<?> deleteSelfFeedback(
            Authentication authentication,
            @PathVariable Long selfId) {
        feedbackService.deleteSelfFeedback(authentication, selfId);
        return ResponseEntity.status(HttpStatus.OK)
                .body("셀프 피드백 삭제가 완료되었습니다");
    }


    /**
     * @param dto 셀프 피드백 dto
     * @apiNote 셀프 피드백 수정 서비스 메서드
     */
    @PutMapping("/self")
    public ResponseEntity<?> updateSelfFeedback(
            @RequestBody SelfFeedbackDto dto) {
        feedbackService.updateSelfFeedback(dto);
        return ResponseEntity.status(HttpStatus.OK)
                .body("셀프 피드백 수정이 완료되었습니다.");
    }
}
