package com.tedi.security.controller

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class SecurityController {
    private static final Logger log = LogManager.getLogger(SecurityController.class)

//    @RequestMapping(value = "/echo/{message}", produces = "application/json",method = RequestMethod.GET)
//    def test(@PathVariable(value="message") String message){
//        log.trace("ECHOING ${message}")
//        return new ResponseEntity(["echo":message], HttpStatus.OK)
//    }
    @GetMapping("user")
    public String helloUser() {
        return "Hello User";
    }

    @GetMapping("admin")
    public String helloAdmin() {
        return "Hello Admin";
    }


}
