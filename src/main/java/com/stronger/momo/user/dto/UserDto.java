package com.stronger.momo.user.dto;

import com.stronger.momo.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String nickname;
    private String password;
    private String email;
    private LocalDate birthday;

    public User toEntity(String encPassword) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        User userEntity = mapper.map(this, User.class);
        userEntity.setPassword(encPassword);
        return userEntity;
    }
}
