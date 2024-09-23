package com.tedi.security.configuration

import jakarta.annotation.PostConstruct
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "server.ssl")
class SslConfiguration {
    String keyStore
    String keyStorePassword
    String keyAlias
    String keyPassword
    String enabled
}
