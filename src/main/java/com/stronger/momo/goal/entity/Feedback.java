package com.stronger.momo.goal.entity;

import com.stronger.momo.common.BaseTimeEntity;
import com.stronger.momo.goal.dto.FeedbackDto;
import com.stronger.momo.team.entity.TeamMember;
import com.stronger.momo.user.entity.User;
import lombok.*;

import javax.persistence.*;

/**
 * 선생님 피드백 Entity
 */
@Entity(name = "Feedback")
@Table(name = "feedback")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Data
public class Feedback extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "userId")
    @ManyToOne
    private User user;

    @JoinColumn(name = "memberId")
    @ManyToOne
    private TeamMember member;

    private String comment;

    public void update(FeedbackDto dto) {
        this.comment = dto.getComment();
    }


}




