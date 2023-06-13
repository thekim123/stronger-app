package com.stronger.momo.common.aws;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api/s3")
@RequiredArgsConstructor
public class AwsS3Controller {

    private final AwsS3Service awsS3Service;

    @PostMapping("/resource")
    public AwsS3 upload(@RequestPart("file") MultipartFile multipartFile) throws IOException {
        return awsS3Service.upload(multipartFile,"upload");
    }

    @DeleteMapping("/resource")
    public void remove(AwsS3 awsS3) {
        awsS3Service.remove(awsS3);
    }
}
