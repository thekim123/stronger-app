package com.stronger.momo.team.dto;

import com.stronger.momo.team.entity.Team;
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
                .teamName(team.getName())
                .description(team.getDescription())
                .build();
    }

}
