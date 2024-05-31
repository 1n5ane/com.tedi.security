package com.tedi.security.services

import com.tedi.security.domains.User
import com.tedi.security.dtos.LoginDto
import com.tedi.security.dtos.RefreshTokenDto
import com.tedi.security.dtos.UserDto
import com.tedi.security.services.validation.ValidationService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service

//TODO: ADD LOGGING

@Service
@Slf4j
class SecurityIntegrationService {
    @Autowired
    Map<String, ValidationService> validationServiceMap

    @Autowired
    UserDetailsServiceImpl userDetailsService

    @Autowired
    JwtService jwtService

    @Autowired
    AuthenticationProvider authenticationProvider

    def login(LoginDto loginDto) throws Exception {
        User user = authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.usernameOrEmail, loginDto.password)
        ).getPrincipal() as User

        def bearerToken = jwtService.generateToken(user)
        def refreshToken = jwtService.generateRefreshToken(user)

        return ["type": "bearer", "bearerToken": bearerToken, "refreshToken": refreshToken]
    }

    def generateTokensFromRefreshToken(RefreshTokenDto refreshTokenDto) throws Exception {
        if(refreshTokenDto.refreshToken==null || refreshTokenDto.refreshToken.isEmpty())
            throw new IllegalArgumentException("No refresh token provided")
//      Extract username (if token expired or invalid exception will be thrown)
        String username = jwtService.extractUsername(refreshTokenDto.refreshToken)
        User user = userDetailsService.loadUserByUsername(username) as User
        def bearerToken = jwtService.generateToken(user)
        def refreshToken = jwtService.generateRefreshToken(user)
        return ["type": "bearer", "bearerToken": bearerToken, "refreshToken": refreshToken]
    }

    void registerUser(UserDto user) throws Exception {
//      if data are valid
        validationServiceMap["userValidationService"].validate(user)

        def dbUser = userDetailsService.createUser(user.username,
                user.email,
                user.password,
                user.name,
                user.surname,
                user.authorities)

        log.info("Successfully created user: ${dbUser}")
    }

    void updateUser(UserDto user, Authentication authentication) throws Exception {
//      TODO: Fill empty user fields from authentication (for partial update - only send fields to update)
//      validate provided user details
        validationServiceMap["userValidationService"].validate(user)

        def currentLoggedInUserAuthorities = authentication.getAuthorities()

//      if role user
        if (!currentLoggedInUserAuthorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
//          user only updates himself
            user.id = (authentication.getPrincipal() as User).id
//          throw away authorities that may exist in request -> user can't update his autorities
            user.authorities = ['ROLE_USER']
            user.locked = false
        } else if (user.id == null) {
//      if role admin and no id provided update admin/own details
            user.id = (authentication.getPrincipal() as User).id
        }

        def updatedUser = userDetailsService.updateUser(user.id, user.username,
                user.email,
                user.password,
                user.name,
                user.surname,
                user.authorities,
                user.locked)

        if (updatedUser != null)
            log.info("Successfully updated user details: ${updatedUser}")
        else
            log.trace("User '${user.username}' with id ${user.id} wasn't updated - no changes made")

    }

    void deleteUser(UserDto userDto, Authentication authentication) throws Exception {
        if (userDto.id == null) {
            log.error("Faield to delete user: User's id can't be empty")
            throw new Exception("No id provided")
        }
        userDetailsService.deleteUser(userDto.id)
    }

//  listAll users (for admin)
}
