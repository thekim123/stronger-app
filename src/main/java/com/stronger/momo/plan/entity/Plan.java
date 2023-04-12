package com.stronger.momo.plan.entity;

import com.stronger.momo.common.BaseTimeEntity;
import com.stronger.momo.user.entity.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
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

    @ManyToOne
    private User owner;

    @ManyToOne
    private User Instructor;

    @OneToMany
    private List<DailyCheck> dailyCheck;
    @OneToMany
    private List<SelfFeedback> selfFeedbackList = new ArrayList<>();
    @OneToMany
    private List<Feedback> feedbackList = new ArrayList<>();

}
