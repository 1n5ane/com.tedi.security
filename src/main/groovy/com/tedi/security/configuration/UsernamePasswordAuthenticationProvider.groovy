//package com.tedi.security.configuration
//
//import com.tedi.security.domains.User
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.security.authentication.AuthenticationProvider
//import org.springframework.security.authentication.BadCredentialsException
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
//import org.springframework.security.core.Authentication
//import org.springframework.security.core.AuthenticationException
//import org.springframework.security.core.userdetails.UserDetailsService
//import org.springframework.security.crypto.password.PasswordEncoder
//import org.springframework.stereotype.Service
//
//@Service
//class UsernamePasswordAuthenticationProvider implements AuthenticationProvider{
//
//    @Autowired
//    UserDetailsService userDetailsService
//
//    @Autowired
//    PasswordEncoder passwordEncoder
//
//    @Override
//    Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        def providedUsername = authentication.name
//        def providedPassword = authentication.getCredentials().toString()
//
////if in memory user
////if db user
//        User user = userDetailsService.loadUserByUsername(providedUsername)
//        if(user==null || !passwordEncoder.matches(providedPassword,user.password)){
//            throw new BadCredentialsException("Invalid username or password")
//        }
//
//        return null
//    }
//
//    @Override
//    boolean supports(Class<?> authentication) {
//        return authentication == UsernamePasswordAuthenticationToken.class;
//    }
//}
