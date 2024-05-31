package com.tedi.security.controllers

import com.tedi.security.domains.User
import com.tedi.security.dtos.LoginDto
import com.tedi.security.dtos.RefreshTokenDto
import com.tedi.security.dtos.UserDto
import com.tedi.security.services.SecurityIntegrationService
import com.tedi.security.utils.data.validation.exception.ValidationException
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

//TODO: add compose yml for postgres
@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
class SecurityController {

    @Autowired
    SecurityIntegrationService securityIntegrationService


    @PostMapping(value = "/refresh_token", produces = "application/json;charset=UTF-8")
    @ResponseBody
    def tokensFromRefreshToken(@RequestBody RefreshTokenDto refreshTokenDto) {
//      Generate new access and refresh token
//      Refresh token will have a long expiry time
//      Access token will have a short one
        def response = [
                "token_type"   : null,
                "token"        : null,
                "refresh_token": null
        ]
        try {
            def tokens = securityIntegrationService.generateTokensFromRefreshToken(refreshTokenDto)
            response["token"] = tokens['bearerToken']
            response["token_type"] = tokens['type']
            response["refresh_token"] = tokens['refreshToken']
        } catch (IllegalArgumentException exception) {
            response["error"] = exception.getMessage()
            return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED)
        } catch (Exception exception) {
            log.trace(exception.getMessage())
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED)
        }
        return new ResponseEntity<>(response, HttpStatus.OK)
    }

    @PostMapping(value = "/login", produces = "application/json;charset=UTF-8")
    @ResponseBody
    def login(@RequestBody LoginDto loginDto) {
        def response = ["success"      : true,
                        "token_type"   : null,
                        "token"        : null,
                        "refresh_token": null,
                        "error"        : ""]
        try {
            def tokens = securityIntegrationService.login(loginDto)
            response["token"] = tokens['bearerToken']
            response["token_type"] = tokens['type']
            response["refresh_token"] = tokens['refreshToken']
        } catch (Exception exception) {
            response.success = false
            response.error = exception.getMessage()
        }
        def retStatus = HttpStatus.OK
        if(!response.success)
            retStatus = HttpStatus.UNAUTHORIZED
        return new ResponseEntity<>(response, retStatus)
    }

    @PostMapping(value = "/user", produces = "application/json;charset=UTF-8")
    @ResponseBody
    def registerUser(@RequestBody UserDto user) {
        def response = ["success": true, "error": ""]
        try {
            securityIntegrationService.registerUser(user)
        } catch (ValidationException exception) {
            response.success = false
            response.error = exception.getMessage()
            log.warn("Failed to register user: ${response.error}")
        }
        return new ResponseEntity<>(response, HttpStatus.OK)
    }

    @PutMapping(value = "/user", produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    def updateUser(@RequestBody UserDto user, Authentication authentication) {
//      For obvious reasons a user should only update HIS details and not someone elses
//      If user is not admin -> check if principal (current logged in user) id match with user id
//      The above is not true for admins though as admins do what they like.
        def response = ["success": true, "error": ""]
        try {
            securityIntegrationService.updateUser(user, authentication)
        } catch (ValidationException exception) {
            response.success = false
            response.error = exception.getMessage()
            log.warn("Failed to update user: ${response.error}")
        }
        return new ResponseEntity<>(response, HttpStatus.OK)
    }

    @DeleteMapping(value = "/user", produces = "application/json;charset=UTF-8")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    def deleteUser(@RequestBody UserDto user, Authentication authentication) {
        def response = ["success": true, "error": ""]
        try {
            securityIntegrationService.deleteUser(user, authentication)
        } catch (Exception exception) {
            response.success = false
            response.error = exception.getMessage()
        }
        return new ResponseEntity<>(response, HttpStatus.OK)
    }


}
