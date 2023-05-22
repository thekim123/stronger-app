package com.stronger.momo.goal.dto;

import com.stronger.momo.goal.entity.Plan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanSaveDto {
    private Long id;
    private String title;
    private String description;
    private Long memberId;


    public static PlanSaveDto from(Plan plan) {
        return PlanSaveDto.builder()
                .id(plan.getId())
                .title(plan.getTitle())
                .description(plan.getDescription())
                .memberId(plan.getMember().getId())
                .build();
    }
}
