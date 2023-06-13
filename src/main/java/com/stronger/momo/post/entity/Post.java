package com.stronger.momo.post.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.stronger.momo.common.BaseTimeEntity;
import com.stronger.momo.post.dto.SnsCreateDto;
import com.stronger.momo.team.entity.Team;
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
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"password"})
    private User writer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teamId")
    private Team team;

    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OrderBy("id DESC")
    private List<Comment> comment = new ArrayList<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Likes> likes = new ArrayList<>();

    @Transient
    private boolean likeState;
    @Transient
    private int likeCount;

    public void updateSns(SnsCreateDto dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
    }

}
