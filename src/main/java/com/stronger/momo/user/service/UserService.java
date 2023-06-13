package com.stronger.momo.user.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.stronger.momo.common.aws.AwsS3;
import com.stronger.momo.config.security.PrincipalDetails;
import com.stronger.momo.user.dto.ProfileImageDto;
import com.stronger.momo.user.dto.UserDto;
import com.stronger.momo.user.entity.User;
import com.stronger.momo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    /**
     * 회원가입 서비스 메서드
     *
     * @param dto 회원 dto
     */
    @Transactional
    public void join(UserDto dto) {
        System.out.println(dto.getPassword());
        String encPassword = bCryptPasswordEncoder.encode(dto.getPassword());
        User user = dto.toEntity(encPassword);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserDto getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 유저가 없습니다."));
        return UserDto.from(user);
    }

    @Transactional
    public void updateProfile(Authentication authentication, UserDto dto) {
        String username = ((PrincipalDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("해당 유저가 없습니다."));
        String encPassword = bCryptPasswordEncoder.encode(dto.getPassword());
        user.update(dto, encPassword);
    }

    @Transactional
    public AwsS3 updateProfileImage(Authentication authentication, ProfileImageDto dto) throws IOException {
        User loginUser = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        validateOwner(loginUser.getId(), dto.getUserId());
        AwsS3 uploadResult = uploadProfileImageToS3(dto);
        System.out.println(dto);
        loginUser.updateProfileImageUrl(uploadResult);
        //TODO: 왜 save를 해야 수정이 되는거지?
        userRepository.save(loginUser);
        return uploadResult;
    }

    // 프로필 이미지 업로드 함수 시작
    public AwsS3 uploadProfileImageToS3(ProfileImageDto dto) throws IOException {
        String dirName = "profile";
        File profileImage = convertMultipartFileToFile(dto.getProfileImage());
        return upload(profileImage, dirName);
    }

    public void validateOwner(Long loginUserId, Long profileId) {
        if (!isProfileOwner(loginUserId, profileId)) {
            throw new IllegalArgumentException("로그인한 유저와 프로필을 변경하려는 유저가 일치하지 않습니다." +
                    " (로그인한 유저: " + loginUserId + ", 프로필 변경하려는 유저: " + profileId + " )");
        }
    }

    public boolean isProfileOwner(Long loginUserId, Long profileId) {
        return Objects.equals(loginUserId, profileId);
    }

    public File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(multipartFile.getOriginalFilename());
        try (OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(multipartFile.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    private AwsS3 upload(File file, String dirName) {
        String key = randomFileName(file, dirName);
        String path = putS3(file, key);
        removeFile(file);

        return AwsS3
                .builder()
                .key(key)
                .path(path)
                .build();
    }

    private String randomFileName(File file, String dirName) {
        return dirName + "/" + UUID.randomUUID() + file.getName();
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return getS3(bucket, fileName);
    }

    private String getS3(String bucket, String fileName) {
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private void removeFile(File file) {
        file.delete();
    }
    // 프로필 이미지 업로드 함수 종류
}
