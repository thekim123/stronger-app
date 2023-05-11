package com.stronger.momo.goal.entity;

import com.stronger.momo.common.BaseTimeEntity;
import com.stronger.momo.goal.dto.SelfFeedbackDto;
import lombok.*;

import javax.persistence.*;

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

    @JoinColumn(name = "goalId")
    @ManyToOne
    private Goal goal;

    // 실패 이유
    private String reason;

    // 대책
    private String measure;

    public void update(SelfFeedbackDto dto) {
        this.reason = dto.getReason();
        this.measure = dto.getMeasure();
    }

}
