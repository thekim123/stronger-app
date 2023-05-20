package com.stronger.momo.goal.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.stronger.momo.common.BaseTimeEntity;
import com.stronger.momo.goal.dto.GoalUpdateDto;
import com.stronger.momo.team.entity.Team;
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
@ToString(exclude = {"plan", "dailyCheckList"})
@JsonIgnoreProperties({"plan", "dailyCheckList"})
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

    @JoinColumn(name = "planId")
    @ManyToOne
    private Plan plan;
    @OneToMany(mappedBy = "goal", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<DailyCheck> dailyCheckList = new ArrayList<>();

    /**
     * 목표 수정시 값 변경 함수
     *
     * @param dto 변경할 값
     */
    public void update(GoalUpdateDto dto) {
        this.goalCount = dto.getGoalCount();
        this.title = dto.getTitle();
        this.content = dto.getContent();
    }

    public void addDailyCheck(DailyCheck dailyCheck) {
        this.dailyCheckList.add(dailyCheck);
    }
}
