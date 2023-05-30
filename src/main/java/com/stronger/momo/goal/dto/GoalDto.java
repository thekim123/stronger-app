package com.stronger.momo.goal.dto;

import com.stronger.momo.goal.entity.DailyCheck;
import com.stronger.momo.goal.entity.Goal;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Data
public class GoalDto {
    private Long id;
    private String title;
    private Long planId;
    private Integer goalCount;
    private Integer actionCount;
    private Integer currentWeeks;

    private DailyCheckDto dailyCheck;
    private List<DailyCheckDto> dailyCheckList;

    public static GoalDto fromGoal(Goal goal) {
        return GoalDto.builder()
                .id(goal.getId())
                .title(goal.getTitle())
                .goalCount(goal.getGoalCount())
                .currentWeeks(goal.getCurrentWeeks())
                .planId(goal.getPlan().getId())
                .dailyCheck(!goal.getDailyCheckList().isEmpty()
                        ? goal.getDailyCheckList().get(0).toDto()
                        : DailyCheckDto.builder().completed(false).build())
                .build();
    }

    public static GoalDto fromGoalWhenMakeReport(List<DailyCheck> dailyCheckList, Goal goal) {
        System.out.println("dailyCheckList = " + dailyCheckList);

        GoalDto result = GoalDto.builder()
                .id(goal.getId())
                .title(goal.getTitle())
                .goalCount(goal.getGoalCount())
                .currentWeeks(goal.getCurrentWeeks())
                .planId(goal.getPlan().getId())
                .dailyCheckList(!dailyCheckList.isEmpty() ? dailyCheckList.stream()
                        .filter(dailyCheck -> dailyCheck.getGoal().getId().equals(goal.getId()) && dailyCheck.isCompleted())
                        .map(DailyCheck::toDto).collect(Collectors.toList())
                        : new ArrayList<>())
                .build();
        System.out.println("result = " + result);
        return result;
    }

    public static GoalDto fromGoalForTodolist(Goal goal) {
        return GoalDto.builder()
                .id(goal.getId())
                .title(goal.getTitle())
                .goalCount(goal.getGoalCount())
                .currentWeeks(goal.getCurrentWeeks())
                .planId(goal.getPlan().getId())
                .build();
    }


}
