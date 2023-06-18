package com.stronger.momo.user.service;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.stronger.momo.common.aws.AwsS3;
import com.stronger.momo.common.aws.AwsS3Service;
import com.stronger.momo.config.security.PrincipalDetails;
import com.stronger.momo.user.dto.ProfileImageDto;
import com.stronger.momo.user.dto.UserDto;
import com.stronger.momo.user.entity.User;
import com.stronger.momo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
    private final AwsS3Service awsS3Service;


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
        AwsS3 uploadResult = awsS3Service.upload(dto.getProfileImage(), "profile");
        loginUser.updateProfileImageUrl(uploadResult);
        //TODO: 왜 save를 해야 수정이 되는거지?
        userRepository.save(loginUser);
        return uploadResult;
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

}
