package com.stronger.momo.goal.dto;

import com.stronger.momo.goal.entity.Feedback;
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
    private Long memberId;
    private Long userId;
    private String comment;
    private String checkDate;

    public static FeedbackDto fromFeedback(Feedback feedback) {
        return FeedbackDto.builder()
                .id(feedback.getId())
                .memberId(feedback.getMember().getId())
                .userId(feedback.getUser().getId())
                .comment(feedback.getComment())
                .checkDate(feedback.getCheckDate().toString())
                .build();
    }

}
