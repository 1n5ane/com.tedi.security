package com.tedi.security.services

import com.tedi.security.domains.RefreshToken
import com.tedi.security.repositories.RefreshTokenRepository
import com.tedi.security.repositories.UserRepository
import groovy.util.logging.Slf4j
import jakarta.annotation.PostConstruct
import jdk.jshell.spi.ExecutionControl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@Slf4j
class RefreshTokenService {
    @Autowired
    RefreshTokenRepository refreshTokenRepository

    @Autowired
    UserRepository userRepository

    public Optional<RefreshToken> findByToken(String token) {
        log.info("searching for token:${token}")
        return refreshTokenRepository.findByToken(token)
    }

    public RefreshToken createRefreshToken(String username) throws Exception {
        def user = userRepository.findByUsername(username)
        if (user == null) {
            log.warn("username ${username} not found on create refresh token")
            throw new Exception("User '${username}' not found!")
        }

//      TODO:implement createToken logic
        return null
    }
}
