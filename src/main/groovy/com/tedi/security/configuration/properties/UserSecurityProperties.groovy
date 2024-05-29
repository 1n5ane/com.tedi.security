package com.tedi.security.configuration.properties

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import com.tedi.security.domains.User as MyUser
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder

@Primary
@Configuration
class UserSecurityProperties extends SecurityProperties {
    @Value('${spring.security.user.email}')
    String email

    @Autowired
    PasswordEncoder passwordEncoder

    MyUser getUserDetails() {
        User u = super.getUser()
        def authorities = new ArrayList<GrantedAuthority>()
        u.roles.each {
            role ->
                authorities.push(
                        new SimpleGrantedAuthority(role)
                )
        }
        return new MyUser(u.name, passwordEncoder.encode(u.password), authorities, email)
    }
}
