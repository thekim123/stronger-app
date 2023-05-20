package com.stronger.momo.goal.entity;

import com.stronger.momo.team.entity.TeamMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Plan {

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
    private List<Goal> goal = new ArrayList<>();
    @OneToMany(mappedBy = "plan")
    @Builder.Default
    private List<SelfFeedback> selfFeedbackList = new ArrayList<>();
    @OneToMany(mappedBy = "plan")
    @Builder.Default
    private List<Feedback> feedbackList = new ArrayList<>();

}
