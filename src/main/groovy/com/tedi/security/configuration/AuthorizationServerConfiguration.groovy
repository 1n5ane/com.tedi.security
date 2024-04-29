package com.tedi.security.configuration

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

import javax.sql.DataSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class AuthorizationServerConfiguration {

    @Autowired
    DataSource dataSource

    @Bean
    @Autowired
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthenticationProvider authenticationProvider) throws Exception {
        http
//        CSRF DISABLED FOR DEVELOPMENT PURPOSES
//        TODO: enable
                .csrf().disable()
                .authorizeHttpRequests((auth) -> auth
//                permit all only on login/logout/register
                        .requestMatchers("/api/v1/auth/**").fullyAuthenticated()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider)
//                TODO: - implement JwtAuthenticationFiler utilizing corespondent JwtService(extends OncePerRequest....)
//                .addFilterBefore(jwtAuthenticationFilter,
//                                 UsernamePasswordAuthenticationFilter.class)
//              FORM LOGIN WILL NOT EXIST IN THE AUTHSERVER
//              TODO: login page will be implemented on resource server
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
                                            PasswordEncoder passwordEncoder){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider()
        provider.setUserDetailsService(userDetailsService)
        provider.setPasswordEncoder(passwordEncoder)
        return provider
    }

}
