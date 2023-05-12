package com.stronger.momo.team.dto;

import com.stronger.momo.team.entity.Team;
import com.stronger.momo.team.entity.TeamMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class TeamDto {
    private Long id;
    private String teamName;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isOpen;

    private boolean isOwner;

    /**
     * TeamDto 객체를 Team 객체로 변환하는 정적 팩터리 메서드
     *
     * @param team Team 엔티티
     * @return TeamDto 객체
     * @author thekim123
     * @since version 1.0
     */
    public static TeamDto from(Team team) {
        return TeamDto.builder()
                .id(team.getId())
                .teamName(team.getGroupName())
                .description(team.getDescription())
                .isOwner(true)
                .build();
    }

    public static TeamDto fromOwner(Team team) {
        return TeamDto.builder()
                .id(team.getId())
                .teamName(team.getGroupName())
                .description(team.getDescription())
                .isOwner(true)
                .build();
    }

    public static TeamDto fromMember(TeamMember teamMember) {
        Team team = teamMember.getTeam();
        return TeamDto.builder()
                .id(team.getId())
                .teamName(team.getGroupName())
                .description(team.getDescription())
                .isOwner(false)
                .build();
    }
}
