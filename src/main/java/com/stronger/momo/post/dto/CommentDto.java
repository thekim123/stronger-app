package com.stronger.momo.post.dto;

import lombok.Data;

@Data
public class CommentDto {
    private Long id;
    private Long snsId;
    private String comment;
}
