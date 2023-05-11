package com.stronger.momo.goal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GoalCreateDto {

    private Long id;
    private String title;
    private String content;
    private Integer goalCount;
    private Long teamId;

}
