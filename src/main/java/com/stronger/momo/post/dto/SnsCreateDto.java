package com.stronger.momo.post.dto;

import lombok.Data;

@Data
public class SnsCreateDto {

    private Long id;
    private String title;
    private String content;
    private Long writerId;
}
