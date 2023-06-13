package com.stronger.momo.common.aws;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AwsS3 {
    private String key;
    private String path;
}
