package com.stronger.momo.post.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PostCreateDto {
    private Long id;
    private String title;
    private String content;
    private Long writerId;
    private Long planId;
    private MultipartFile postImage;
}
