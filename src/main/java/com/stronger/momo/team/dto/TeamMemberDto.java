package com.stronger.momo.team.dto;

import com.stronger.momo.goal.dto.PlanDto;
import com.stronger.momo.team.entity.TeamMember;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class TeamMemberDto {

    private Long id;
    private String gradeName;
    private Long teamId;
    private String teamName;
    private Long userId;
    private String nickname;

    List<PlanDto> planList;


    public static TeamMemberDto from(TeamMember teamMember) {
        List<PlanDto> planDtoList = teamMember.getPlanList().stream()
                .map(PlanDto::from)
                .collect(Collectors.toList());

        return TeamMemberDto.builder()
                .id(teamMember.getId())
                .gradeName(teamMember.getGrade().name())
                .teamId(teamMember.getTeam().getId())
                .teamName(teamMember.getTeam().getName())
                .userId(teamMember.getUser().getId())
                .nickname(teamMember.getUser().getNickname())
                .planList(planDtoList)
                .build();
    }

}
