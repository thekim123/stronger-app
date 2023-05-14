package com.stronger.momo.goal.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class DailyCheckDto {
    private Long id;
    private Integer weeks;
    private boolean completed;
    private LocalDate checkDate;

}
