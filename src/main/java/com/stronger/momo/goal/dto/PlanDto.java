package com.stronger.momo.goal.dto;

import com.stronger.momo.goal.entity.DailyCheck;
import com.stronger.momo.goal.entity.Plan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanDto {
    private Long id;
    private String title;
    private String description;
    private List<GoalDto> goalList;
    private Long memberId;


    public static PlanDto from(Plan plan) {
        return PlanDto.builder()
                .id(plan.getId())
                .title(plan.getTitle())
                .description(plan.getDescription())
                .memberId(plan.getMember().getId())
                .goalList(plan.getGoalList().stream()
                        .map(GoalDto::fromGoal)
                        .collect(Collectors.toList()))
                .build();
    }

    public static PlanDto whenMakeReport(Plan plan, List<DailyCheck> dailyCheckList) {
        return PlanDto.builder()
                .id(plan.getId())
                .title(plan.getTitle())
                .description(plan.getDescription())
                .memberId(plan.getMember().getId())
                .goalList(plan.getGoalList().stream()
                        .map(goal -> GoalDto.fromGoalWhenMakeReport(dailyCheckList, goal))
                        .collect(Collectors.toList()))
                .build();
    }

    public static PlanDto toDtoWhenMakeList(Plan plan) {
        List<GoalDto> goalDtoList = plan.getGoalList().stream()
                .map(GoalDto::fromGoal)
                .collect(Collectors.toList());

        return PlanDto.builder()
                .id(plan.getId())
                .title(plan.getTitle())
                .description(plan.getDescription())
                .goalList(goalDtoList)
                .memberId(plan.getMember().getId())
                .build();
    }
}
