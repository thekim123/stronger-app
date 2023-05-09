package com.stronger.momo.team.entity;

import com.stronger.momo.common.BaseTimeEntity;
import com.stronger.momo.user.entity.User;
import lombok.*;

import javax.persistence.*;


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
public class TeamMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    @JoinColumn(name = "teamId")
    @ManyToOne
    private Team team;

    @JoinColumn(name = "memberId")
    @ManyToOne
    private User member;


    /**
     * 직책 수정시 값 변경 함수
     *
     * @param user         변경 유저
     * @param team         그룹
     * @param positionName 직책 코드
     */
    public void update(User user, Team team, String positionName) {
        this.team = team;
        this.member = user;
        this.grade = Grade.valueOf(positionName);
    }

}
