package com.stronger.momo.goal.controller;

import com.stronger.momo.goal.dto.PlanSaveDto;
import com.stronger.momo.goal.dto.PlanDto;
import com.stronger.momo.goal.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plan")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @PostMapping
    public ResponseEntity<?> createPlan(PlanSaveDto dto) {
        planService.createPlan(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("계획 생성이 완료되었습니다.");
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
