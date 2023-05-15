package com.stronger.momo.goal.controller;

import com.stronger.momo.goal.dto.AnalysisDto;
import com.stronger.momo.goal.dto.GoalAnalisysDto;
import com.stronger.momo.goal.service.AnalysisService;
import com.stronger.momo.team.entity.TeamMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("analysis")
@RestController
public class AnalysisController {

    private final AnalysisService analysisService;

    @GetMapping("/team/{teamId}/date/{selectedDate}")
    public ResponseEntity<?> getAnalysis(
            Authentication authentication,
            @PathVariable String selectedDate,
            @PathVariable Long teamId) {
        AnalysisDto result
                = analysisService.getAnalysis(authentication, teamId, selectedDate);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
