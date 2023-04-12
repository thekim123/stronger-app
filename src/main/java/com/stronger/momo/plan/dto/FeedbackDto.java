package com.stronger.momo.plan.dto;

import com.stronger.momo.plan.entity.Feedback;
import com.stronger.momo.plan.entity.SelfFeedback;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackDto {
    private Long id;
    private String reason;
    private String measure;

    public Feedback toEntity() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return mapper.map(this, Feedback.class);
    }
}
