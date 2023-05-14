package com.stronger.momo.goal.dto;

import com.stronger.momo.goal.entity.DailyCheck;
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
    private Long teamId;

    private DailyCheckDto dailyCheck;

    public static GoalDto fromGoal(Goal goal) {
        return GoalDto.builder()
                .id(goal.getId())
                .title(goal.getTitle())
                .content(goal.getContent())
                .goalCount(goal.getGoalCount())
                .actionCount(goal.getActionCount())
                .currentWeeks(goal.getCurrentWeeks())
                .build();
    }

    public static GoalDto convertForTodo(Goal goal) {
        return GoalDto.builder()
                .id(goal.getId())
                .title(goal.getTitle())
                .content(goal.getContent())
                .goalCount(goal.getGoalCount())
                .actionCount(goal.getActionCount())
                .currentWeeks(goal.getCurrentWeeks())
                .dailyCheck(!goal.getDailyCheckList().isEmpty()
                        ? goal.getDailyCheckList().get(0).toDto()
                        : DailyCheckDto.builder().completed(false).build())
                .build();
    }
}
