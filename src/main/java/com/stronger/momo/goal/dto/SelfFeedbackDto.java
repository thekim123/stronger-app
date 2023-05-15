package com.stronger.momo.goal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SelfFeedbackDto {
    private Long id;
    private String reason;
    private String measure;
    private String checkDate;
}
