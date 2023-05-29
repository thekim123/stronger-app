package com.stronger.momo.report.dto;

import com.stronger.momo.goal.dto.DailyCheckDto;
import com.stronger.momo.goal.dto.PlanDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ReportDto {

    private SelfFeedbackDto selfFeedback;
    private FeedbackDto feedback;
    private PlanDto plan;

}
