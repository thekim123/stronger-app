package com.stronger.momo.goal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalUpdateDto {
    private Long id;
    private String title;
    private String content;
    private Integer goalCount;

}
