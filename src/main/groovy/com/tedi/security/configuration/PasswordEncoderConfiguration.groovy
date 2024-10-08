package com.tedi.security.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class PasswordEncoderConfiguration {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder()
    }

    @Bean
    public NoOpPasswordEncoder noOpPasswordEncoder(){
        return new NoOpPasswordEncoder()
    }

    @Bean
    @Primary
    public PasswordEncoder delegatingPasswordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }
}
