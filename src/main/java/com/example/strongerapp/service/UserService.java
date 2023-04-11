package com.example.strongerapp.service;

import com.example.strongerapp.dto.UserDto;
import com.example.strongerapp.entity.User;
import com.example.strongerapp.repository.UserRepository;
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
