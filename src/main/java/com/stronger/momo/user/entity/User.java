package com.stronger.momo.user.entity;

import com.stronger.momo.common.BaseTimeEntity;
import com.stronger.momo.config.security.PrincipalDetails;
import lombok.*;
import org.springframework.security.core.Authentication;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    private LocalDate birthday;

    public User getEntityByAuthentication(Authentication authentication) {
        return ((PrincipalDetails) authentication.getPrincipal()).getUser();
    }


}
