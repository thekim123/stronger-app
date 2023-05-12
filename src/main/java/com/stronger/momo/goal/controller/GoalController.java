package com.stronger.momo.goal.controller;

import com.stronger.momo.goal.dto.*;
import com.stronger.momo.goal.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/goal")
public class GoalController {

    private final GoalService goalService;


    @GetMapping("/todo-list")
    public ResponseEntity<?> getTodoList(Authentication authentication) {
        List<GoalDto> todoList = goalService.getTodoList(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(todoList);
    }

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
    public ResponseEntity<?> createGoal(Authentication authentication, @RequestBody GoalCreateDto dto) {
        GoalDto data = goalService.createGoal(authentication, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(data);
    }


    /**
     * @param authentication 유저 인증 정보
     * @param goalId         계획 id
     * @apiNote 삭제 api
     */
    @DeleteMapping("/{goalId}")
    public ResponseEntity<?> deleteGoal(Authentication authentication, @PathVariable Long goalId) {
        goalService.deleteGoal(authentication, goalId);
        return ResponseEntity.status(HttpStatus.OK).body("계획 삭제가 완료되었습니다.");
    }


    /**
     * @param authentication 유저 인증 정보
     * @param dto            계획 수정 dto
     * @apiNote 수정 api
     */
    @PutMapping
    public ResponseEntity<?> updateGoal(Authentication authentication, @RequestBody GoalUpdateDto dto) {
        goalService.updateGoal(authentication, dto);
        return ResponseEntity.status(HttpStatus.OK).body("계획 수정이 완료되었습니다");
    }

    /**
     * @param authentication 유저 인증 정보
     * @param goalId         계획 id
     * @apiNote 오늘의 계획 완수 api
     */
    @PostMapping("/{goalId}/daily")
    public ResponseEntity<?> dailyCheck(Authentication authentication, @PathVariable Long goalId) {
        goalService.dailyCheck(authentication, goalId);
        return ResponseEntity.status(HttpStatus.OK).body("계획 완수 체크가 완료되었습니다.");
    }

    @DeleteMapping("/{goalId}/daily")
    public ResponseEntity<?> unDailyCheck(Authentication authentication, @PathVariable Long goalId) {
        goalService.unDailyCheck(authentication, goalId);
        return ResponseEntity.status(HttpStatus.OK).body("계획 완수 체크가 완료되었습니다.");
    }


    /**
     * 교관 피드백 작성 api
     *
     * @param dto    교관 피드백 작성 dto
     * @param goalId 계획 id
     */
    @PostMapping("/{goalId}/feedback")
    public ResponseEntity<?> createFeedback(Authentication authentication, @RequestBody FeedbackDto dto, @PathVariable Long goalId) {
        goalService.createFeedback(authentication, dto, goalId);
        return ResponseEntity.status(HttpStatus.CREATED).body("교관 피드백 작성이 완료되었습니다.");
    }

    /**
     * 교관 피드백 삭제 api
     *
     * @param authentication 유저 인증 정보
     * @param feedbackId     교관 피드백 id
     * @param goalId         계획 id
     */
    @DeleteMapping("/{goalId}/feedback/{feedbackId}")
    public ResponseEntity<?> deleteFeedback(Authentication authentication, @PathVariable Long feedbackId, @PathVariable Long goalId) {
        goalService.deleteFeedback(authentication, feedbackId, goalId);
        return ResponseEntity.status(HttpStatus.OK).body("교관 피드백 삭제가 완료되었습니다");
    }

    /**
     * 교관 피드백 수정 서비스 메서드
     *
     * @param authentication 유저 인증 정보
     * @param dto            교관 피드백 dto
     * @param goalId         계획 id
     */
    @PutMapping("/{goalId}/feedback")
    public ResponseEntity<?> updateFeedback(Authentication authentication, @RequestBody FeedbackDto dto, @PathVariable Long goalId) {
        goalService.updateFeedback(authentication, dto, goalId);
        return ResponseEntity.status(HttpStatus.OK).body("교관 피드백 수정이 완료되었습니다.");
    }

    /**
     * 셀프 피드백 작성 api
     *
     * @param dto    셀프피드백 작성 dto
     * @param goalId 계획 id
     */
    @PostMapping("/{goalId}/self")
    public ResponseEntity<?> createSelfFeedback(@RequestBody SelfFeedbackDto dto, @PathVariable Long goalId) {
        goalService.createSelfFeedback(dto, goalId);
        return ResponseEntity.status(HttpStatus.CREATED).body("셀프 피드백 작성이 완료되었습니다.");
    }

    /**
     * 셀프 피드백 삭제 api
     *
     * @param authentication 유저 인증 정보
     * @param selfId         셀프 피드백 id
     * @param goalId         계획 id
     */
    @DeleteMapping("/{goalId}/self/{selfId}")
    public ResponseEntity<?> deleteSelfFeedback(Authentication authentication, @PathVariable Long selfId, @PathVariable Long goalId) {
        goalService.deleteSelfFeedback(authentication, selfId, goalId);
        return ResponseEntity.status(HttpStatus.OK).body("셀프 피드백 삭제가 완료되었습니다");
    }

    /**
     * 셀프 피드백 수정 서비스 메서드
     *
     * @param authentication 유저 인증 정보
     * @param dto            셀프 피드백 dto
     * @param goalId         계획 id
     */
    @PutMapping("/{goalId}/self")
    public ResponseEntity<?> updateSelfFeedback(Authentication authentication, @RequestBody SelfFeedbackDto dto, @PathVariable Long goalId) {
        goalService.updateSelfFeedback(authentication, dto, goalId);
        return ResponseEntity.status(HttpStatus.OK).body("셀프 피드백 수정이 완료되었습니다.");
    }
}
