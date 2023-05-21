package com.stronger.momo.report.dto;

import com.stronger.momo.report.entity.SelfFeedback;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SelfFeedbackDto {
    private Long id;
    private String reason;
    private String measure;
    private LocalDate checkDate;

    public static SelfFeedbackDto fromSelfFeedback(SelfFeedback selfFeedback) {
        return SelfFeedbackDto.builder()
                .id(selfFeedback.getId())
                .reason(selfFeedback.getReason())
                .measure(selfFeedback.getMeasure())
                .checkDate(selfFeedback.getCheckDate())
                .build();
    }
}
