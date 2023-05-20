package com.stronger.momo.goal.entity;

import com.stronger.momo.common.BaseTimeEntity;
import com.stronger.momo.goal.dto.FeedbackDto;
import com.stronger.momo.team.entity.TeamMember;
import com.stronger.momo.user.entity.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

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

    @JoinColumn(name = "planId")
    @ManyToOne
    private Plan plan;

    private String comment;

    private LocalDate checkDate;

    public void update(FeedbackDto dto) {
        this.comment = dto.getComment();
    }


    public FeedbackDto toDto() {
        if (id == null) {
            return new FeedbackDto();
        }

        return FeedbackDto.builder()
                .id(id)
                .comment(comment)
                .checkDate(checkDate.toString())
                .build();
    }
}




