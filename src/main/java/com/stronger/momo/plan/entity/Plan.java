package com.stronger.momo.plan.entity;

import com.stronger.momo.common.BaseTimeEntity;
import com.stronger.momo.plan.dto.PlanUpdateDto;
import com.stronger.momo.team.entity.Team;
import com.stronger.momo.team.entity.TeamMember;
import com.stronger.momo.user.entity.User;
import lombok.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 계획  Entity
 * 초기에는 무조건 1주일 단위로 설정된다.
 */
@EqualsAndHashCode(callSuper = true)
@Entity(name = "Plan")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "plan")
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
    @Column
    private Integer actionCount;

    private Integer currentWeeks;

    private LocalDate startDate;
    private LocalDate endDate;

    @JoinColumn(name = "ownerId")
    @ManyToOne
    private TeamMember owner;
    @JoinColumn(name = "groupId")
    @ManyToOne
    private Team team;
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
    @Builder.Default
    private List<DailyCheck> dailyCheckList = new ArrayList<>();
    @OneToMany(mappedBy = "plan")
    @Builder.Default
    private List<SelfFeedback> selfFeedbackList = new ArrayList<>();
    @OneToMany(mappedBy = "plan")
    @Builder.Default
    private List<Feedback> feedbackList = new ArrayList<>();

    public void update(PlanUpdateDto dto) {
        this.goalCount = dto.getGoalCount();
        this.title = dto.getTitle();
        this.content = dto.getContent();
    }


}
