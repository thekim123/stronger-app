package com.stronger.momo.report.dto;

import com.stronger.momo.report.dto.FeedbackDto;
import com.stronger.momo.report.dto.GoalReportDto;
import com.stronger.momo.report.dto.SelfFeedbackDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ReportDto {

    private SelfFeedbackDto selfFeedback;
    private FeedbackDto feedback;

    private List<GoalReportDto> goalList;
}
