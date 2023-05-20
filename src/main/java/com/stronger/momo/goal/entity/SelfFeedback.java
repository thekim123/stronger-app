package com.stronger.momo.goal.entity;

import com.stronger.momo.common.BaseTimeEntity;
import com.stronger.momo.goal.dto.SelfFeedbackDto;
import com.stronger.momo.team.entity.TeamMember;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * 자가피드백 Entity
 */
@EqualsAndHashCode(callSuper = true)
@Entity(name = "SelfFeedback")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "self_feedback")
public class SelfFeedback extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "planId")
    @ManyToOne
    private Plan plan;

    // 실패 이유
    private String reason;

    // 대책
    private String measure;

    private LocalDate checkDate;

    public void update(SelfFeedbackDto dto) {
        this.reason = dto.getReason();
        this.measure = dto.getMeasure();
    }

    public SelfFeedbackDto toDto() {
        if (id == null) {
            return new SelfFeedbackDto();
        }

        return SelfFeedbackDto.builder()
                .id(id)
                .reason(reason)
                .measure(measure)
                .checkDate(checkDate)
                .build();
    }
}
