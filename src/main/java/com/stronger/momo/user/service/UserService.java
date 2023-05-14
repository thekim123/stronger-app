package com.stronger.momo.user.service;

import com.stronger.momo.config.security.PrincipalDetails;
import com.stronger.momo.user.dto.UserDto;
import com.stronger.momo.user.entity.User;
import com.stronger.momo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

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
    public UserDto getProfile(Authentication authentication) {
        String username = ((PrincipalDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("해당 유저가 없습니다."));
        return UserDto.from(user);
    }

    public void updateProfile(Authentication authentication, UserDto dto) {
        String username = ((PrincipalDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("해당 유저가 없습니다."));
        String encPassword = bCryptPasswordEncoder.encode(dto.getPassword());
        user.update(dto, encPassword);
    }
}
