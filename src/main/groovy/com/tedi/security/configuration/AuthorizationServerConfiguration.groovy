package com.tedi.security.configuration

import com.tedi.security.configuration.properties.ServerProperties
import com.tedi.security.filters.JwtAuthenticationFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.sql.DataSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class AuthorizationServerConfiguration {

    @Autowired
    DataSource dataSource

    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter

    @Autowired
    ServerProperties serverProperties

    @Bean
    @Autowired
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthenticationProvider authenticationProvider) throws Exception {
        http
//              disable csrf -> jwt token is used -> no cookies!
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests((auth) -> auth
//                permit all only on login/logout/register and MetadataConfiguration
                        .requestMatchers("/.well-known/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/user").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/refresh_token").permitAll()
                        .requestMatchers("/api/v1/auth/**").fullyAuthenticated()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//              FORM LOGIN WILL NOT EXIST IN THE AUTHSERVER
//              login page will be implemented on resource server
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
        return http.build()
    }

    @Autowired
    public void configure(AuthenticationManagerBuilder auth, PasswordEncoder passwordEncoder) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(passwordEncoder)
                .rolePrefix("ROLE_")
                .usersByUsernameQuery("select username,password,not coalesce(locked,false) as enabled from users where username = ?")
                .authoritiesByUsernameQuery("select u.username,r.name as authority\n" +
                        "from many_users_has_many_roles muhmr\n" +
                        "inner join public.users u on u.id = muhmr.id_users\n" +
                        "inner join public.roles r on muhmr.id_roles = r.id\n" +
                        "where username=?")
    }

    @Bean
    @Autowired
    public AuthenticationProvider configure(UserDetailsService userDetailsService,
                                            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider()
        provider.setUserDetailsService(userDetailsService)
        provider.setPasswordEncoder(passwordEncoder)
        return provider
    }

}
