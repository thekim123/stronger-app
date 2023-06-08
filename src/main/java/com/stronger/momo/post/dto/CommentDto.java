package com.stronger.momo.post.dto;

import lombok.Data;

@Data
public class CommentDto {
    private Long id;
    private Long postId;
    private String comment;
}
