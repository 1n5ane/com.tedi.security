package com.tedi.security.services

import com.tedi.security.domains.User
import com.tedi.security.dtos.LoginDto
import com.tedi.security.dtos.RefreshTokenDto
import com.tedi.security.dtos.UserDto
import com.tedi.security.services.validation.UserValidationService
import com.tedi.security.services.validation.ValidationService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

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
        if (refreshTokenDto.refreshToken == null || refreshTokenDto.refreshToken.isEmpty())
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

    Boolean checkUserExistsByUsernameOrEmail(String username,String email) throws Exception {
        //load user by username checks for username or email!
        def exists = null
        if(username && !username.isEmpty()) {
            try {
                userDetailsService.loadUserByUsername(username)
                return true
            } catch (UsernameNotFoundException ignored) {
                exists = false
            }
        }

        if(email && !email.isEmpty()) {
            try {
                userDetailsService.loadUserByUsername(email)
                return true
            } catch (UsernameNotFoundException ignored) {
                exists = false
            }
        }

        return exists
    }

    void updateUser(UserDto user, Authentication authentication) throws Exception {
//      TODO: Fill empty user fields from authentication (for partial update - only send fields to update)
//      validate provided user details
        (validationServiceMap["userValidationService"] as UserValidationService).validateUserUpdate(user)

        def currentLoggedInUserAuthorities = authentication.getAuthorities()

//      if role user
        if (!currentLoggedInUserAuthorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
//          user only updates himself
            user.id = (authentication.getPrincipal() as User).id
//          throw away authorities that may exist in request -> user can't update his autorities
            user.authorities = ['ROLE_USER']
        } else if (user.id == null) {
//      if role admin and no id provided update admin/own details
            user.id = (authentication.getPrincipal() as User).id
        }
        if (!user.locked) {
            user.locked = (authentication.getPrincipal() as User).locked
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

    User findUser(Long id) throws Exception {
        User user = userDetailsService.findUserById(id)
        return user
    }

    def findAllUsers(Integer page, Integer pageSize, String sortBy, String order) throws Exception {
        if (page < 0) throw new IllegalArgumentException("Page number can't be negative!")
        if (pageSize <= 0) throw new IllegalArgumentException("Page size can't be negative or zero")
        if (pageSize > 100) throw new IllegalArgumentException("Page size can't be more than 100")
        sortBy = sortBy.trim()
        if (!["id", "username", "firstName", "lastName", "email", "createdAt", "updatedAt", "locked"].contains(sortBy))
            throw new IllegalArgumentException("SortBy can only be one of [id, username, firstName, lastName, email, createdAt, updatedAt, locked]")

        order = order.trim()
        if (!["asc", "desc"].contains(order))
            throw new IllegalArgumentException("Order can only be 'asc' or 'desc'")

        def users = userDetailsService.listAllUsers(page, pageSize, sortBy, order)
        def userCount = userDetailsService.countAllUsers()
        def totalPages = Math.ceil(userCount / pageSize) as Integer
        def hasNextPage = page + 1 < totalPages
        return ["users": users, "hasNextPage": hasNextPage]
    }
}
