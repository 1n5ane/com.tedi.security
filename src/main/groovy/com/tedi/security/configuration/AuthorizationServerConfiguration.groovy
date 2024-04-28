package com.tedi.security.configuration

import com.tedi.security.configuration.properties.SecurityConfigProperties
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

import javax.sql.DataSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class AuthorizationServerConfiguration {

    @Autowired
    SecurityConfigProperties securityConfigProperties

    @Autowired
    DataSource dataSource

    @PostConstruct
    def test(){
        def h
    }

    @Bean
    @Autowired
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthenticationProvider authenticationProvider) throws Exception {
        http
                .authorizeHttpRequests((auth) -> auth
//                permit all only on login/logout/register
                        .requestMatchers("/api/v1/auth/**").fullyAuthenticated()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider)
//                TODO: - implement JwtAuthenticationFiler utilizing corespondent JwtService(extends OncePerRequest....)
//                .addFilterBefore(jwtAuthenticationFilter,
//                                 UsernamePasswordAuthenticationFilter.class)
                .formLogin (Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
        return http.build();
    }

    @Autowired
    public void configure(AuthenticationManagerBuilder auth,PasswordEncoder passwordEncoder) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(passwordEncoder)
                .rolePrefix("ROLE_")

        auth.inMemoryAuthentication()
                .passwordEncoder(passwordEncoder)
                .withUser(securityConfigProperties.username)
                .password(passwordEncoder.encode(securityConfigProperties.password))
                .roles("ADMIN")
    }

    @Bean
    @Autowired
    public AuthenticationProvider configure(UserDetailsService userDetailsService,
                                            PasswordEncoder passwordEncoder){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider()
        provider.setUserDetailsService(userDetailsService)
        provider.setPasswordEncoder(passwordEncoder)
        return provider
    }

}
