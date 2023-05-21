package com.stronger.momo.goal.controller;

import com.stronger.momo.goal.dto.*;
import com.stronger.momo.goal.service.GoalService;
import com.stronger.momo.report.dto.FeedbackDto;
import com.stronger.momo.report.dto.SelfFeedbackDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/goal")
public class GoalController {

    private final GoalService goalService;


    /**
     * @param authentication 유저 인증 정보
     * @param teamId         팀 id
     * @return 해당 팀에서 세운 목표 리스트
     * @apiNote 팀별 todolist 조회 api
     */
    @GetMapping("/todo-list/{teamId}")
    public ResponseEntity<?> getTodoList(
            Authentication authentication
            , @PathVariable Long teamId) {
        List<GoalDto> todoList =
                goalService.getTodoList(authentication, teamId);
        return ResponseEntity.status(HttpStatus.OK).body(todoList);
    }

    /**
     * @param memberId 팀 멤버 id
     * @return 해당 팀에서 세운 목표 리스트
     * @apiNote 팀에서 세운 목표 조회 api
     */
    @GetMapping("/team/{memberId}")
    public ResponseEntity<?> findGoalByMember(
            @PathVariable Long memberId) {
        List<PlanDto> planList =
                goalService.findGoalByMember(memberId);
        return ResponseEntity.status(HttpStatus.OK).body(planList);
    }

    /**
     * @param authentication 유저 인증 정보
     * @param dto            계획 작성 dto
     * @apiNote 작성 api
     */
    @PostMapping
    public ResponseEntity<?> createGoal(
            Authentication authentication,
            @RequestBody GoalCreateDto dto) {
        GoalDto data = goalService.createGoal(authentication, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(data);
    }


    /**
     * @param authentication 유저 인증 정보
     * @param goalId         계획 id
     * @apiNote 삭제 api
     */
    @DeleteMapping("/{goalId}")
    public ResponseEntity<?> deleteGoal(
            Authentication authentication,
            @PathVariable Long goalId) {
        goalService.deleteGoal(authentication, goalId);
        return ResponseEntity.status(HttpStatus.OK)
                .body("계획 삭제가 완료되었습니다.");
    }


    /**
     * @param authentication 유저 인증 정보
     * @param dto            계획 수정 dto
     * @apiNote 수정 api
     */
    @PutMapping
    public ResponseEntity<?> updateGoal(
            Authentication authentication,
            @RequestBody GoalUpdateDto dto) {
        goalService.updateGoal(authentication, dto);
        return ResponseEntity.status(HttpStatus.OK)
                .body("계획 수정이 완료되었습니다");
    }

    /**
     * @param authentication 유저 인증 정보
     * @param goalId         계획 id
     * @apiNote 오늘의 계획 완수 api
     */
    @PostMapping("/{goalId}/daily")
    public ResponseEntity<?> dailyCheck(
            Authentication authentication,
            @PathVariable Long goalId) {
        goalService.dailyCheck(authentication, goalId);
        return ResponseEntity.status(HttpStatus.OK)
                .body("계획 완수 체크가 완료되었습니다.");
    }

    @PutMapping("/{goalId}/daily")
    public ResponseEntity<?> unDailyCheck(
            Authentication authentication,
            @PathVariable Long goalId) {
        goalService.updateDailyCheck(authentication, goalId);
        return ResponseEntity.status(HttpStatus.OK)
                .body("계획 완수 체크가 완료되었습니다.");
    }


}
