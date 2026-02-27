package com.afadhitya.taskmanagement.infrastructure.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret;
    private Long accessTokenExpiration = 900000L; // 15 minutes in milliseconds
    private Long refreshTokenExpiration = 2592000000L; // 30 days in milliseconds
}
