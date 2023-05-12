package com.stronger.momo.goal.controller;

import com.stronger.momo.goal.dto.*;
import com.stronger.momo.goal.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//TODO 주소 전부다 goal 로 바꾸기.
@RestController
@RequiredArgsConstructor
@RequestMapping("/plan")
public class GoalController {

    private final GoalService goalService;

    /**
     * @param authentication 유저 인증 정보
     * @param teamId         팀 id
     * @return 해당 팀에서 세운 목표 리스트
     * @apiNote 팀에서 세운 목표 조회 api
     */
    @GetMapping("/team/{teamId}")
    public ResponseEntity<?> findGoalByTeam(Authentication authentication, @PathVariable Long teamId) {
        List<GoalDto> goalList = goalService.findGoalByTeam(authentication, teamId);
        return ResponseEntity.status(HttpStatus.OK).body(goalList);
    }

    /**
     * @param authentication 유저 인증 정보
     * @param dto            계획 작성 dto
     * @apiNote 작성 api
     */
    @PostMapping
    public ResponseEntity<?> createPlan(Authentication authentication, @RequestBody GoalCreateDto dto) {
        goalService.createPlan(authentication, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("계획 수립이 완료되었습니다.");
    }


    /**
     * @param authentication 유저 인증 정보
     * @param planId         계획 id
     * @apiNote 삭제 api
     */
    @DeleteMapping("/{planId}")
    public ResponseEntity<?> deletePlan(Authentication authentication, @PathVariable Long planId) {
        goalService.deletePlan(authentication, planId);
        return ResponseEntity.status(HttpStatus.OK).body("계획 삭제가 완료되었습니다.");
    }


    /**
     * @param authentication 유저 인증 정보
     * @param dto            계획 수정 dto
     * @apiNote 수정 api
     */
    @PutMapping
    public ResponseEntity<?> updatePlan(Authentication authentication, @RequestBody GoalUpdateDto dto)  {
        goalService.updatePlan(authentication, dto);
        return ResponseEntity.status(HttpStatus.OK).body("계획 수정이 완료되었습니다");
    }

    /**
     * @param authentication 유저 인증 정보
     * @param planId         계획 id
     * @apiNote 오늘의 계획 완수 api
     */
    @PutMapping("/{planId}/daily")
    public ResponseEntity<?> dailyCheck(Authentication authentication, @PathVariable Long planId)   {
        goalService.dailyCheck(authentication, planId);
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
        goalService.createFeedback(authentication, dto, planId);
        return ResponseEntity.status(HttpStatus.CREATED).body("교관 피드백 작성이 완료되었습니다.");
    }

    /**
     * 교관 피드백 삭제 api
     *
     * @param authentication 유저 인증 정보
     * @param feedbackId     교관 피드백 id
     * @param planId         계획 id
     */
    @DeleteMapping("/{planId}/feedback/{feedbackId}")
    public ResponseEntity<?> deleteFeedback(Authentication authentication, @PathVariable Long feedbackId, @PathVariable Long planId)   {
        goalService.deleteFeedback(authentication, feedbackId, planId);
        return ResponseEntity.status(HttpStatus.OK).body("교관 피드백 삭제가 완료되었습니다");
    }

    /**
     * 교관 피드백 수정 서비스 메서드
     *
     * @param authentication 유저 인증 정보
     * @param dto            교관 피드백 dto
     * @param planId         계획 id
     */
    @PutMapping("/{planId}/feedback")
    public ResponseEntity<?> updateFeedback(Authentication authentication, @RequestBody FeedbackDto dto, @PathVariable Long planId)   {
        goalService.updateFeedback(authentication, dto, planId);
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
        goalService.createSelfFeedback(dto, planId);
        return ResponseEntity.status(HttpStatus.CREATED).body("셀프 피드백 작성이 완료되었습니다.");
    }

    /**
     * 셀프 피드백 삭제 api
     *
     * @param authentication 유저 인증 정보
     * @param selfId         셀프 피드백 id
     * @param planId         계획 id
     */
    @DeleteMapping("/{planId}/self/{selfId}")
    public ResponseEntity<?> deleteSelfFeedback(Authentication authentication, @PathVariable Long selfId, @PathVariable Long planId)   {
        goalService.deleteSelfFeedback(authentication, selfId, planId);
        return ResponseEntity.status(HttpStatus.OK).body("셀프 피드백 삭제가 완료되었습니다");
    }

    /**
     * 셀프 피드백 수정 서비스 메서드
     *
     * @param authentication 유저 인증 정보
     * @param dto            셀프 피드백 dto
     * @param planId         계획 id
     */
    @PutMapping("/{planId}/self")
    public ResponseEntity<?> updateSelfFeedback(Authentication authentication, @RequestBody SelfFeedbackDto dto, @PathVariable Long planId)   {
        goalService.updateSelfFeedback(authentication, dto, planId);
        return ResponseEntity.status(HttpStatus.OK).body("셀프 피드백 수정이 완료되었습니다.");
    }
}
