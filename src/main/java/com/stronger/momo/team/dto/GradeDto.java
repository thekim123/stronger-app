package com.stronger.momo.team.dto;

import lombok.Data;

/**
 * 직책 dto
 */
@Data
public class GradeDto {

    private Long id;
    private String positionName;
    private Long teamId;
    private Long memberId;
}
