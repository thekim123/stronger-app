package com.stronger.momo.post.entity;

import com.stronger.momo.common.BaseTimeEntity;
import com.stronger.momo.post.dto.SnsDto;
import com.stronger.momo.user.entity.User;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SNS Entity
 * 계획 실천 중 공유할 때 사용
 */
@EqualsAndHashCode(callSuper = true)
@Entity(name = "Post")
@Table(name = "post")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Post extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    @JoinColumn(name = "writerId")
    @ManyToOne
    private User writer;

    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "post", cascade = CascadeType.ALL)
    @OrderBy("id DESC")
    private List<Comment> comment = new ArrayList<>();

    public void updateSns(SnsDto dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
    }

}