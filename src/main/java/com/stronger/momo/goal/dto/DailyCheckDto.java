package com.stronger.momo.goal.dto;

import com.stronger.momo.goal.entity.DailyCheck;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Data
public class DailyCheckDto {
    private Long id;
    private Integer weeks;
    private boolean completed;
    private LocalDate checkDate;

    public static List<DailyCheckDto> from(List<DailyCheck> dailyChecks) {
        return dailyChecks.stream()
                .map(DailyCheck::toDto)
                .collect(Collectors.toList());
    }

}
