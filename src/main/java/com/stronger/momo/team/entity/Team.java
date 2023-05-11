package com.stronger.momo.team.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.stronger.momo.common.BaseTimeEntity;
import com.stronger.momo.team.dto.TeamDto;
import com.stronger.momo.user.entity.User;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "Team")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "team")
public class Team extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String groupName;

    private String description;

    @JsonIgnoreProperties({"teamList", "teamMemberList", "snsList"})
    @JoinColumn(name = "ownerId")
    @ManyToOne
    private User owner;

    private boolean isOpen;

    @JsonIgnoreProperties({"team", "user"})
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    @Builder.Default
    private List<TeamMember> teamMemberList = new ArrayList<>();

    public void update(TeamDto dto) {
        this.groupName = dto.getTeamName();
        this.description = dto.getDescription();
        this.isOpen = dto.isOpen();
    }

}
