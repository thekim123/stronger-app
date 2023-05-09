package com.stronger.momo.plan.entity;

import com.stronger.momo.common.BaseTimeEntity;
import com.stronger.momo.user.entity.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 계획  Entity
 * 초기에는 무조건 1주일 단위로 설정된다.
 */


@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Plan extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    private String content;

    // 목표 횟수
    @Column(nullable = false)
    private Integer goalCount;

    // 실천 횟수
    @Column(nullable = false)
    private Integer actionCount;

    private Integer currentWeeks;

    private Integer totalWeeks;
    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne
    private User owner;

    @ManyToOne
    private User Instructor;

    @OneToMany
    @Builder.Default
    private List<DailyCheck> dailyCheck = new ArrayList<>();
    @OneToMany
    @Builder.Default
    private List<SelfFeedback> selfFeedbackList = new ArrayList<>();
    @OneToMany
    @Builder.Default
    private List<Feedback> feedbackList = new ArrayList<>();

}
