package com.stronger.momo.sns.dto;

import lombok.Data;

@Data
public class CommentDto {
    private Long id;
    private Long snsId;
    private String comment;
}
