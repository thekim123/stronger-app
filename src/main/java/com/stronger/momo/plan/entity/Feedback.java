package com.stronger.momo.plan.entity;

import com.stronger.momo.common.BaseTimeEntity;
import com.stronger.momo.plan.dto.FeedbackDto;
import com.stronger.momo.user.entity.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    public void update(FeedbackDto dto) {
        this.comment = dto.getComment();
    }


}




