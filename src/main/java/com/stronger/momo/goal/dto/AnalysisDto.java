package com.stronger.momo.goal.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class AnalysisDto {

    private SelfFeedbackDto selfFeedback;
    private FeedbackDto feedback;

    private List<GoalAnalisysDto> goalList;
}
