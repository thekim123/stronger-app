package com.stronger.momo.goal.controller;

import com.stronger.momo.goal.dto.PlanDto;
import com.stronger.momo.goal.dto.PlanSaveDto;
import com.stronger.momo.goal.service.PlanService;
import com.stronger.momo.team.dto.TeamMemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/plan")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;


    @PostMapping
    public ResponseEntity<?> createPlan(@RequestBody PlanSaveDto dto) {
        PlanDto responseDto = planService.createPlan(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseDto);
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<?> deletePlan(@PathVariable Long planId) {
        planService.deletePlan(planId);
        return ResponseEntity.status(HttpStatus.OK)
                .body("계획 삭제가 완료되었습니다.");
    }

    @PutMapping
    public ResponseEntity<?> updatePlan(@RequestBody PlanSaveDto dto) {
        planService.updatePlan(dto);
        return ResponseEntity.status(HttpStatus.OK)
                .body("계획 수정이 완료되었습니다.");
    }
}
