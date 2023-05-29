package com.stronger.momo.report.entity;

import com.stronger.momo.common.BaseTimeEntity;
import com.stronger.momo.goal.entity.Goal;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Report extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double actionRate;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer currentWeek;

}
