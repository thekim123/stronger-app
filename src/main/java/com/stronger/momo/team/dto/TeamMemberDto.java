package com.stronger.momo.team.dto;

import com.stronger.momo.goal.dto.PlanDto;
import com.stronger.momo.team.entity.TeamMember;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


@Data
@Builder
public class TeamMemberDto {

    private Long id;
    private String gradeName;
    private Long teamId;
    private String teamName;
    private String teamDescription;
    private Long userId;
    private String nickname;
    private String startDate;
    private String endDate;
    private List<PlanDto> planDtoList;


    public static TeamMemberDto from(TeamMember teamMember) {
        List<PlanDto> planDtoList =
                teamMember.getPlanList().stream()
                        .map(PlanDto::from).collect(Collectors.toList());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM.dd");
        return TeamMemberDto.builder()
                .id(teamMember.getId())
                .gradeName(teamMember.getGrade().name())
                .teamId(teamMember.getTeam().getId())
                .teamName(teamMember.getTeam().getName())
                .teamDescription(teamMember.getTeam().getDescription())
                .userId(teamMember.getUser().getId())
                .nickname(teamMember.getUser().getNickname())
                .startDate(teamMember.getTeam().getStartDate().format(formatter))
                .endDate(teamMember.getTeam().getEndDate().format(formatter))
                .planDtoList(planDtoList)
                .build();
    }

}
