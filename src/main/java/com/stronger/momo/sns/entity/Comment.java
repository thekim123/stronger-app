package com.stronger.momo.sns.entity;

import com.stronger.momo.common.BaseTimeEntity;
import com.stronger.momo.sns.dto.CommentDto;
import com.stronger.momo.user.entity.User;
import lombok.*;

import javax.persistence.*;

/**
 * sns 댓글
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "Comment")
@Table(name = "comment")
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comment;
    @ManyToOne
    @JoinColumn(name = "snsId")
    private Sns sns;
    @ManyToOne
    private User writer;

    /**
     * comment dto -> entity 메서드
     *
     * @param dto comment dto
     */
    public void updateComment(CommentDto dto) {
        this.comment = dto.getComment();
    }

}
