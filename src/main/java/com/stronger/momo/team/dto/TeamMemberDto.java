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
    private String teamName;
    private Long userId;
    private String nickname;

    public static TeamMemberDto from(TeamMember teamMember) {
        return TeamMemberDto.builder()
                .id(teamMember.getId())
                .gradeName(teamMember.getGrade().name())
                .teamId(teamMember.getTeam().getId())
                .teamName(teamMember.getTeam().getName())
                .userId(teamMember.getUser().getId())
                .nickname(teamMember.getUser().getNickname())
                .build();
    }

}
