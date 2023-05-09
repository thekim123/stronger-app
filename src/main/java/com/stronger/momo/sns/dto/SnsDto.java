package com.stronger.momo.sns.dto;

import lombok.Data;

@Data
public class SnsDto {

    private Long id;
    private String title;
    private String content;
    private Long writerId;
}
