package com.stronger.momo.sns.entity;

import com.stronger.momo.common.BaseTimeEntity;
import com.stronger.momo.sns.dto.SnsDto;
import com.stronger.momo.user.entity.User;
import lombok.*;

import javax.persistence.*;

/**
 * SNS Entity
 * 계획 실천 중 공유할 때 사용
 */
@EqualsAndHashCode(callSuper = true)
@Entity(name = "Sns")
@Table(name = "sns")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Sns extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    @JoinColumn(name = "writerId")
    @ManyToOne
    private User writer;

    public void updateSns(SnsDto dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
    }

}
