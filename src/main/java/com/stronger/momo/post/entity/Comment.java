package com.stronger.momo.post.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.stronger.momo.common.BaseTimeEntity;
import com.stronger.momo.post.dto.CommentDto;
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
@JsonIgnoreProperties({"post"})
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comment;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "postId")
    private Post post;
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIncludeProperties({"id", "nickname"})
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
