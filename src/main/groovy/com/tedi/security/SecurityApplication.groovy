package com.tedi.security

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties

@SpringBootApplication
@EnableConfigurationProperties
class SecurityApplication {

    static void main(String[] args) {
        SpringApplication.run(SecurityApplication, args)
    }

}
