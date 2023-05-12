package com.stronger.momo.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.stronger.momo.user.dto.LoginRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stronger.momo.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        ObjectMapper om = new ObjectMapper();
        LoginRequestDto dto;
        try {
            dto = om.readValue(request.getInputStream(), LoginRequestDto.class);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());

            return authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) {

        PrincipalDetails principalDetailis = (PrincipalDetails) authResult.getPrincipal();
        User loginUser = principalDetailis.getUser();

        String jwtToken = JWT.create()
                .withSubject(principalDetailis.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .withClaim("id", loginUser.getId())
                .withClaim("username", loginUser.getUsername())
                .withClaim("nickname", loginUser.getNickname())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);
    }

}
