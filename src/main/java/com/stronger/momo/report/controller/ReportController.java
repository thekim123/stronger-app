package com.stronger.momo.report.controller;

import com.stronger.momo.report.dto.ReportDto;
import com.stronger.momo.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("api/report")
@RestController
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/plan/{planId}/date/{selectedDate}")
    public ResponseEntity<?> getAnalysis(
            Authentication authentication,
            @PathVariable String selectedDate,
            @PathVariable Long planId) {
        ReportDto result
                = reportService.getAnalysisData(authentication, planId, selectedDate);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


}
