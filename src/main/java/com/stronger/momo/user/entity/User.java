package com.stronger.momo.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.stronger.momo.common.BaseTimeEntity;
import com.stronger.momo.common.aws.AwsS3;
import com.stronger.momo.team.entity.Team;
import com.stronger.momo.team.entity.TeamMember;
import com.stronger.momo.post.entity.Post;
import com.stronger.momo.user.dto.UserDto;
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
@ToString(exclude = {"teamList", "teamMemberList", "postList"})
@JsonIgnoreProperties({"teamList", "teamMemberList", "postList"})
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String nickname;

    private String password;

    private String profileImageUrl;
    private String profileImageKey;

    @Column(unique = true)
    private String email;

    private LocalDate birthday;

    @OneToMany(mappedBy = "owner")
    private List<Team> teamList;

    @OneToMany(mappedBy = "user")
    private List<TeamMember> teamMemberList;

    @OneToMany(mappedBy = "writer")
    private List<Post> postList;

    public void update(UserDto dto, String encPassword) {
        this.nickname = dto.getNickname();
        this.email = dto.getEmail();
        this.birthday = dto.getBirthday();
        this.password = encPassword;
    }

    public void updateProfileImageUrl(AwsS3 awsS3) {
        this.profileImageUrl = awsS3.getPath();
        this.profileImageKey = awsS3.getKey();
    }
}
