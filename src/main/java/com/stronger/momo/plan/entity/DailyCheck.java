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

    private boolean isCompleted;

    @ManyToOne
    private Plan plan;


}
