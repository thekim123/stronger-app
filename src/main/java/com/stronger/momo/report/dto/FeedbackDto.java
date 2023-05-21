package com.stronger.momo.report.dto;

import com.stronger.momo.report.entity.Feedback;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackDto {
    private Long id;
    private Long planId;
    private Long userId;
    private String comment;
    private String checkDate;

    public static FeedbackDto fromFeedback(Feedback feedback) {
        return FeedbackDto.builder()
                .id(feedback.getId())
                .userId(feedback.getUser().getId())
                .planId(feedback.getPlan().getId())
                .comment(feedback.getComment())
                .checkDate(feedback.getCheckDate().toString())
                .build();
    }

}
