package com.stronger.momo.plan.entity;

import com.stronger.momo.common.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity(name = "DailyCheck")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "daily_check")
public class DailyCheck extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer weeks;

    private LocalDate checkDate;

    @Builder.Default
    private boolean isCompleted = true;

    @JoinColumn(name = "planId")
    @ManyToOne
    private Plan plan;


}
