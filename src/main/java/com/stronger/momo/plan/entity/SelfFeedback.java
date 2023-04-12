package com.stronger.momo.plan.entity;

import com.stronger.momo.common.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

/**
 * 자가피드백 Entity
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SelfFeedback extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Plan plan;

    // 실패 이유
    private String reason;

    // 대책
    private String measure;

}
