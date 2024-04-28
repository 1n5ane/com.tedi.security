package com.tedi.security.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "application.security")
class SecurityConfigProperties {
    String username
    String password
}
