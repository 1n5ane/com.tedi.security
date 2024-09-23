package com.tedi.security.configuration

import com.tedi.security.utils.security.KeyLoader
import jakarta.annotation.PostConstruct
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey


//TODO: NOT GENERATE DIFFERENT EACH TIME -> GENERATE SSL KEYS AND PUT UNDER RESOURCES
@Configuration
@ConfigurationProperties(prefix = "application.security.jwt")
class JwtConfiguration {
    String publicKeyPath
    String privateKeyPath
    Long expirationTime
    Long refreshExpirationTime
    KeyPair keyPair

    @PostConstruct
    public void init() {
        PublicKey publicKey = KeyLoader.loadPublicKey(publicKeyPath)
        PrivateKey privateKey = KeyLoader.loadPrivateKey(privateKeyPath)
        this.keyPair = new KeyPair(publicKey, privateKey)
    }
}
