package com.stronger.momo.user.entity;

import com.stronger.momo.common.BaseTimeEntity;
import com.stronger.momo.team.entity.Team;
import com.stronger.momo.team.entity.TeamMember;
import com.stronger.momo.sns.entity.Sns;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "User")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "user")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    private LocalDate birthday;

    @OneToMany(mappedBy = "owner")
    private List<Team> teamList;

    @OneToMany(mappedBy = "member")
    private List<TeamMember> teamMemberList;

    @OneToMany(mappedBy = "writer")
    private List<Sns> snsList;

}
