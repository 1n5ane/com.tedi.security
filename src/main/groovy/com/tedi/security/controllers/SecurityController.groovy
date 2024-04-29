package com.tedi.security.controllers

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import java.security.Principal

//TODO: add compose yml for postgres
@RestController
@RequestMapping("/api/v1/auth")
class SecurityController {
    private static final Logger log = LogManager.getLogger(SecurityController.class)

//    @RequestMapping(value = "/echo/{message}", produces = "application/json",method = RequestMethod.GET)
//    def test(@PathVariable(value="message") String message){
//        log.trace("ECHOING ${message}")
//        return new ResponseEntity(["echo":message], HttpStatus.OK)
//    }
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


}
