package com.stronger.momo.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@Builder
@Data
public class ProfileImageDto {
    private Long userId;
    private MultipartFile profileImage;
}
