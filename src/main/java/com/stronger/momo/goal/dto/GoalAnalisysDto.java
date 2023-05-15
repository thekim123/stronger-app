package com.stronger.momo.goal.dto;

import com.stronger.momo.goal.entity.DailyCheck;
import com.stronger.momo.goal.entity.Goal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalAnalisysDto {

    private Long id;
    private String title;
    private String content;
    private Integer goalCount;
    private Integer actionCount;
    private Integer currentWeeks;
    private Long teamId;

    private List<DailyCheckDto> dailyCheckDtoList;

    public static GoalAnalisysDto mapDailyCheck(Goal goal, List<DailyCheck> dailyCheckList) {
        List<DailyCheckDto> dtoList = dailyCheckList.stream()
                .map(dailyCheck -> DailyCheckDto.builder()
                        .id(dailyCheck.getId())
                        .completed(dailyCheck.isCompleted())
                        .checkDate(dailyCheck.getCheckDate())
                        .build())
                .collect(Collectors.toList());

        return GoalAnalisysDto.builder()
                .id(goal.getId())
                .title(goal.getTitle())
                .content(goal.getContent())
                .goalCount(goal.getGoalCount())
                .actionCount(goal.getActionCount())
                .dailyCheckDtoList(dtoList)
                .build();
    }


}
