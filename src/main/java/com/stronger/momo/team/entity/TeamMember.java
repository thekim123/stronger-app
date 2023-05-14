package com.stronger.momo.team.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.stronger.momo.common.BaseTimeEntity;
import com.stronger.momo.goal.entity.Feedback;
import com.stronger.momo.goal.entity.Goal;
import com.stronger.momo.goal.entity.SelfFeedback;
import com.stronger.momo.user.entity.User;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


/**
 * 그룹 내 직책 entity
 */
@EqualsAndHashCode(callSuper = true)
@Entity(name = "TeamMember")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "team_member")
@ToString(exclude = {"team", "user"})
public class TeamMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    private String introduce;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    @JsonIncludeProperties({"id", "name", "startDate", "endDate"})
    @JoinColumn(name = "teamId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    @JsonIncludeProperties({"id", "username", "email", "nickname"})
    @JoinColumn(name = "userId")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "owner")
    @Builder.Default
    private List<Goal> goal = new ArrayList<>();
    @OneToMany(mappedBy = "member")
    @Builder.Default
    private List<SelfFeedback> selfFeedbackList = new ArrayList<>();
    @OneToMany(mappedBy = "member")
    @Builder.Default
    private List<Feedback> feedbackList = new ArrayList<>();


    /**
     * 직책 수정시 값 변경 함수
     *
     * @param user         변경 유저
     * @param team         그룹
     * @param positionName 직책 코드
     */
    public void update(User user, Team team, String positionName) {
        this.team = team;
        this.user = user;
        this.grade = Grade.valueOf(positionName);
    }

}
