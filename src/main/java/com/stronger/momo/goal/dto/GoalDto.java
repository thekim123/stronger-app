package com.stronger.momo.goal.dto;

import com.stronger.momo.goal.entity.Goal;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class GoalDto {
    private Long id;
    private String title;
    private String content;
    private Integer goalCount;
    private Integer actionCount;
    private Integer currentWeeks;
    private LocalDate startDate;
    private LocalDate endDate;

    public static GoalDto fromGoal(Goal goal) {
        return GoalDto.builder()
                .id(goal.getId())
                .title(goal.getTitle())
                .content(goal.getContent())
                .goalCount(goal.getGoalCount())
                .actionCount(goal.getActionCount())
                .currentWeeks(goal.getCurrentWeeks())
                .startDate(goal.getStartDate())
                .endDate(goal.getEndDate())
                .build();
    }
}
