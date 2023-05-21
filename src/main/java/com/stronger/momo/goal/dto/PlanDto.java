package com.stronger.momo.goal.dto;

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


    public static PlanDto fromPlan(Plan plan) {
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
}
