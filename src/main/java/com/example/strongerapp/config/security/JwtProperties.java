package com.example.strongerapp.config.security;

import lombok.Data;

public interface JwtProperties {

    String SECRET = "AngerMoM0!";
    int EXPIRATION_TIME = 864000000;
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";

}
