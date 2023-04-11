package com.example.strongerapp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 계획  Entity
 * 초기에는 무조건 1주일 단위로 설정된다.
 */


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;

    // 목표 횟수
    private Integer goalCount;

    // 실천 횟수
    private Integer actionCount;

    @OneToMany
    private List<DailyCheck> dailyCheck;
    @OneToMany
    private List<SelfFeedback> selfFeedbackList = new ArrayList<>();

    @OneToMany
    private List<Feedback> feedbackList = new ArrayList<>();

}
