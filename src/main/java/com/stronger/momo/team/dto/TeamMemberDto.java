package com.stronger.momo.team.dto;

import com.stronger.momo.team.entity.TeamMember;
import lombok.Builder;
import lombok.Data;

/**
 * 직책 dto
 */
@Data
@Builder
public class TeamMemberDto {

    private Long id;
    private String gradeName;
    private Long teamId;
    private Long userId;

    public static TeamMemberDto fromTeamMember(TeamMember teamMember) {
           return TeamMemberDto.builder()
                    .id(teamMember.getId())
                    .gradeName(teamMember.getGrade().name())
                    .teamId(teamMember.getTeam().getId())
                    .userId(teamMember.getUser().getId())
                    .build();
    }
}
