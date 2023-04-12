package com.stronger.momo.user.service;

import com.stronger.momo.config.security.PrincipalDetails;
import com.stronger.momo.user.dto.UserDto;
import com.stronger.momo.user.entity.User;
import com.stronger.momo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void join(UserDto dto) {
        System.out.println(dto.getPassword());
        String encPassword = bCryptPasswordEncoder.encode(dto.getPassword());
        User user = dto.toEntity(encPassword);
        userRepository.save(user);
    }

}
