package com.tedi.security.controllers

import com.tedi.security.dtos.UserDto
import com.tedi.security.services.SecurityIntegrationService
import com.tedi.security.utils.data.validation.exception.ValidationException
import groovy.util.logging.Slf4j
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

import java.security.Principal

//TODO: add compose yml for postgres
@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
class SecurityController {

    @Autowired
    SecurityIntegrationService securityIntegrationService

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String helloUser(Principal principal) {
        def h
        return "Hello User";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String helloAdmin() {
        return "Hello Admin";
    }

    @PostMapping(value = "/user", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public registerUser(@RequestBody UserDto user) {
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
    public updateUser(@RequestBody UserDto user, Authentication authentication) {
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
    public deleteUser(@RequestBody UserDto user, Authentication authentication) {
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
