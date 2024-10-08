package com.tedi.security.services

import com.tedi.security.configuration.JwtConfiguration
import com.tedi.security.configuration.properties.ServerProperties
import com.tedi.security.domains.User
import groovy.util.logging.Slf4j
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.security.PrivateKey
import java.security.PublicKey

@Service
@Slf4j
class JwtService {

    @Autowired
    JwtConfiguration jwtConfiguration

    @Autowired
    ServerProperties serverProperties

    String extractUsername(String token) {
        return extractClaim(token, "sub", String.class)
    }

    def extractClaim(String token, String key, Class type) {
        final Claims claims = extractAllClaims(token)
        return claims.get(key, type)
    }

    String generateToken(User user) {
        def authorityStringList = []
        user.authorities.each { authority ->
            authorityStringList.add(authority.toString())
        }
        def extraClaims = [
                "userId"     : user.id,
                "name"       : user.firstName,
                "surname"    : user.lastName,
                "email"      : user.email,
                "authorities": authorityStringList
        ]
        return generateToken(extraClaims, user)
    }

    String generateRefreshToken(User user) {
        return buildToken(["userId": user.id], user, jwtConfiguration.refreshExpirationTime)
    }

    String generateToken(Map extraClaims, User user) {
        return buildToken(extraClaims, user, jwtConfiguration.expirationTime);
    }

    private String buildToken(Map<String, ?> extraClaims, User user, Long expiration) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(user.username)
                .setIssuer(serverProperties.getServerUrl())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getPrivateKey())
                .compact()
    }

    Boolean isTokenValid(String token, User user) {
        String username = null
        Long userId = null
        def expired
        try {
            username = extractUsername(token)
            userId = (Long) extractClaim(token, "userId", Long.class)
            expired = isTokenExpired(token)
        } catch (Exception e) {
            log.trace("Token '${token}' is not valid: ${e.getMessage()}")
            expired = true
        }
        return (username == user.username && userId == user.id && !expired)
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date())
    }

    private Date extractExpiration(String token) {
        return (Date) extractClaim(token, "exp", Date.class)
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getPublicKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
    }

    private PrivateKey getPrivateKey() {
        return jwtConfiguration.keyPair.getPrivate()
    }

    public PublicKey getPublicKey() {
        return jwtConfiguration.keyPair.getPublic()
    }
}
