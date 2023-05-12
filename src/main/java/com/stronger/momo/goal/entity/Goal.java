package com.stronger.momo.goal.entity;

import com.stronger.momo.common.BaseTimeEntity;
import com.stronger.momo.goal.dto.GoalUpdateDto;
import com.stronger.momo.team.entity.Team;
import com.stronger.momo.team.entity.TeamMember;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 계획  Entity
 * 초기에는 무조건 1주일 단위로 설정된다.
 */
@EqualsAndHashCode(callSuper = true)
@Entity(name = "Goal")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "goal")
public class Goal extends BaseTimeEntity {

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
    @Column
    private Integer actionCount;

    //TODO: 지워도될듯?
    private Integer currentWeeks;

    @JoinColumn(name = "ownerId")
    @ManyToOne
    private TeamMember owner;
    @JoinColumn(name = "teamId")
    @ManyToOne
    private Team team;
    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL)
    @Builder.Default
    private List<DailyCheck> dailyCheckList = new ArrayList<>();
    @OneToMany(mappedBy = "goal")
    @Builder.Default
    private List<SelfFeedback> selfFeedbackList = new ArrayList<>();
    @OneToMany(mappedBy = "goal")
    @Builder.Default
    private List<Feedback> feedbackList = new ArrayList<>();

    public void update(GoalUpdateDto dto) {
        this.goalCount = dto.getGoalCount();
        this.title = dto.getTitle();
        this.content = dto.getContent();
    }


}
