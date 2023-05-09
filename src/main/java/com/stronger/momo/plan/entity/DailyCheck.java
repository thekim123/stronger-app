package com.stronger.momo.plan.entity;

import com.stronger.momo.common.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.DayOfWeek;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
public class DailyCheck extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer weeks;


    private DayOfWeek dayOfWeek;

    @Builder.Default
    private boolean isCompleted = true;

    @ManyToOne
    private Plan plan;


}
