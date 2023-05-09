package com.stronger.momo.plan.controller;

import com.stronger.momo.plan.dto.FeedbackDto;
import com.stronger.momo.plan.dto.PlanCreateDto;
import com.stronger.momo.plan.dto.PlanUpdateDto;
import com.stronger.momo.plan.dto.SelfFeedbackDto;
import com.stronger.momo.plan.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plan")
public class PlanController {

    private final PlanService planService;

//    @GetMapping
//    public ResponseEntity<?> getPlan(Authentication authentication) {
//        return ResponseEntity.status(HttpStatus.OK).body(planService.getPlan(authentication));
//    }

    /**
     * 계획 작성 api
     *
     * @param authentication 유저 인증 정보
     * @param dto            계획 작성 dto
     */
    @PostMapping
    public ResponseEntity<?> createPlan(Authentication authentication, @RequestBody PlanCreateDto dto) {
        planService.createPlan(authentication, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("계획 수립이 완료되었습니다.");
    }


    /**
     * 계획 삭제 api
     *
     * @param authentication 유저 인증 정보
     * @param planId         계획 id
     * @throws AccessDeniedException 계획의 소유자가 아닌 경우
     */
    @DeleteMapping("/{planId}")
    public ResponseEntity<?> deletePlan(Authentication authentication, @PathVariable Long planId) throws AccessDeniedException {
        planService.deletePlan(authentication, planId);
        return ResponseEntity.status(HttpStatus.OK).body("계획 삭제가 완료되었습니다.");
    }


    /**
     * 계획 수정 api
     *
     * @param authentication 유저 인증 정보
     * @param dto            계획 수정 dto
     */
    @PutMapping
    public ResponseEntity<?> updatePlan(Authentication authentication, @RequestBody PlanUpdateDto dto) throws AccessDeniedException {
        planService.updatePlan(authentication, dto);
        return ResponseEntity.status(HttpStatus.OK).body("계획 수정이 완료되었습니다");
    }

    /**
     * 오늘의 계획 완수 api
     *
     * @param authentication 유저 인증 정보
     * @param planId         계획 id
     * @throws AccessDeniedException 계획 소유자가 아닌 경우
     */
    @PutMapping("/{planId}/daily")
    public ResponseEntity<?> dailyCheck(Authentication authentication, @PathVariable Long planId) throws AccessDeniedException {
        planService.dailyCheck(authentication, planId);
        return ResponseEntity.status(HttpStatus.OK).body("계획 완수 체크가 완료되었습니다.");
    }


    /**
     * 교관 피드백 작성 api
     *
     * @param dto    교관 피드백 작성 dto
     * @param planId 계획 id
     */
    @PostMapping("/{planId}/feedback")
    public ResponseEntity<?> createFeedback(Authentication authentication, @RequestBody FeedbackDto dto, @PathVariable Long planId) {
        planService.createFeedback(authentication, dto, planId);
        return ResponseEntity.status(HttpStatus.CREATED).body("교관 피드백 작성이 완료되었습니다.");
    }

    /**
     * 교관 피드백 삭제 api
     *
     * @param authentication 유저 인증 정보
     * @param feedbackId     교관 피드백 id
     * @param planId         계획 id
     * @throws AccessDeniedException 교관 피드백의 소유자가 아닌 경우
     */
    @DeleteMapping("/{planId}/feedback/{feedbackId}")
    public ResponseEntity<?> deleteFeedback(Authentication authentication, @PathVariable Long feedbackId, @PathVariable Long planId) throws AccessDeniedException {
        planService.deleteFeedback(authentication, feedbackId, planId);
        return ResponseEntity.status(HttpStatus.OK).body("교관 피드백 삭제가 완료되었습니다");
    }

    /**
     * 교관 피드백 수정 서비스 메서드
     *
     * @param authentication 유저 인증 정보
     * @param dto            교관 피드백 dto
     * @param planId         계획 id
     * @throws AccessDeniedException 계획의 소유자가 아닌 경우
     */
    @PutMapping("/{planId}/feedback")
    public ResponseEntity<?> updateFeedback(Authentication authentication, @RequestBody FeedbackDto dto, @PathVariable Long planId) throws AccessDeniedException {
        planService.updateFeedback(authentication, dto, planId);
        return ResponseEntity.status(HttpStatus.OK).body("교관 피드백 수정이 완료되었습니다.");
    }

    /**
     * 셀프 피드백 작성 api
     *
     * @param dto    셀프피드백 작성 dto
     * @param planId 계획 id
     */
    @PostMapping("/{planId}/self")
    public ResponseEntity<?> createSelfFeedback(@RequestBody SelfFeedbackDto dto, @PathVariable Long planId) {
        planService.createSelfFeedback(dto, planId);
        return ResponseEntity.status(HttpStatus.CREATED).body("셀프 피드백 작성이 완료되었습니다.");
    }

    /**
     * 셀프 피드백 삭제 api
     *
     * @param authentication 유저 인증 정보
     * @param selfId         셀프 피드백 id
     * @param planId         계획 id
     * @throws AccessDeniedException 셀프 피드백의 소유자가 아닌 경우
     */
    @DeleteMapping("/{planId}/self/{selfId}")
    public ResponseEntity<?> deleteSelfFeedback(Authentication authentication, @PathVariable Long selfId, @PathVariable Long planId) throws AccessDeniedException {
        planService.deleteSelfFeedback(authentication, selfId, planId);
        return ResponseEntity.status(HttpStatus.OK).body("셀프 피드백 삭제가 완료되었습니다");
    }

    /**
     * 셀프 피드백 수정 서비스 메서드
     *
     * @param authentication 유저 인증 정보
     * @param dto            셀프 피드백 dto
     * @param planId         계획 id
     * @throws AccessDeniedException 계획의 소유자가 아닌 경우
     */
    @PutMapping("/{planId}/self")
    public ResponseEntity<?> updateSelfFeedback(Authentication authentication, @RequestBody SelfFeedbackDto dto, @PathVariable Long planId) throws AccessDeniedException {
        planService.updateSelfFeedback(authentication, dto, planId);
        return ResponseEntity.status(HttpStatus.OK).body("셀프 피드백 수정이 완료되었습니다.");
    }
}
