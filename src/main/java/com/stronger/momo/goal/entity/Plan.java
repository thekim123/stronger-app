package com.stronger.momo.goal.entity;

import com.stronger.momo.common.BaseTimeEntity;
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
@ToString
public class Plan extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @ManyToOne
    @JoinColumn(name = "memberId")
    private TeamMember member;

    @OneToMany(mappedBy = "plan")
    @Builder.Default
    private List<Goal> goalList = new ArrayList<>();
    @OneToMany(mappedBy = "plan")
    @Builder.Default
    private List<SelfFeedback> selfFeedbackList = new ArrayList<>();
    @OneToMany(mappedBy = "plan")
    @Builder.Default
    private List<Feedback> feedbackList = new ArrayList<>();


}
