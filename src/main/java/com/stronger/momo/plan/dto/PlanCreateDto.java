package com.stronger.momo.plan.dto;

import com.stronger.momo.plan.entity.Plan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PlanCreateDto {

    private Long id;
    private String title;
    private String content;
    private Integer goalCount;

    public Plan toEntity() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return mapper.map(this, Plan.class);
    }

}
