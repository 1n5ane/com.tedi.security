package com.tedi.security.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties(prefix = "application.security.jwt")
class JwtConfiguration {
    String secret
    Long expirationTime
    Long refreshExpirationTime
}
