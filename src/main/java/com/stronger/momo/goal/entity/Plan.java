package com.stronger.momo.goal.entity;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.stronger.momo.common.BaseTimeEntity;
import com.stronger.momo.goal.dto.PlanSaveDto;
import com.stronger.momo.report.entity.Feedback;
import com.stronger.momo.report.entity.SelfFeedback;
import com.stronger.momo.team.entity.TeamMember;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString(exclude = {"member", "goalList", "selfFeedbackList", "feedbackList"})
public class Plan extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @JsonIncludeProperties({"id", "planList"})
    @ManyToOne
    @JoinColumn(name = "memberId")
    private TeamMember member;

    @JsonIncludeProperties({"plan"})
    @OneToMany(mappedBy = "plan", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<Goal> goalList = new ArrayList<>();
    @JsonIncludeProperties({"plan"})
    @OneToMany(mappedBy = "plan", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<SelfFeedback> selfFeedbackList = new ArrayList<>();
    @JsonIncludeProperties({"plan"})
    @OneToMany(mappedBy = "plan", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<Feedback> feedbackList = new ArrayList<>();

    public static Plan toCreate(PlanSaveDto plan) {
        return Plan.builder()
                .id(plan.getId())
                .title(plan.getTitle())
                .description(plan.getDescription())
                .build();
    }

    public void setMember(TeamMember member) {
        this.member = member;
    }

    public void update(PlanSaveDto dto) {
        this.title = dto.getTitle();
        this.description = dto.getDescription();
    }

}