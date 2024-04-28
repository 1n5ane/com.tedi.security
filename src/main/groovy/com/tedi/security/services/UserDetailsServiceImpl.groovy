package com.tedi.security.services

import com.tedi.security.domains.User
import com.tedi.security.repositories.RoleRepository
import com.tedi.security.repositories.UserRepository
import com.tedi.security.repositories.UserRoleRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service("userDetailsService")
class UserDetailsServiceImpl implements UserDetailsService {
    Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class)

    @Autowired
    UserRepository userRepository

    @Autowired
    UserRoleRepository userRoleRepository

    @Autowired
    RoleRepository roleRepository

    @Autowired
    SecurityProperties securityProperties

    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//      CHECK IF USER IS CONFIG USER
        if (securityProperties.getUser().name == username) {

            def authorities = new ArrayList<GrantedAuthority>();

            securityProperties.user.roles.each {
                role ->
                    authorities.push(
                            new SimpleGrantedAuthority(role)
                    )
            }

            return new User(
                    securityProperties.user.name,
                    securityProperties.user.password,
                    authorities
            )
        }

//      CHECK DB FOR USER
        def user = null
        user = userRepository.findByUsername(username)
        if (user == null)
            throw new UsernameNotFoundException(username)

        def authorities = new ArrayList<GrantedAuthority>()

        def userRolesSet = userRoleRepository.findByUserId(user.id)

        userRolesSet.each { userRole ->
            authorities.push(new SimpleGrantedAuthority(userRole.role.name))
        }
        return new User(user.id,
                        user.username,
                        user.password,
                        authorities,
                        user.firstName,
                        user.lastName,
                        user.email,
                        user.locked,
                        user.createdAt,
                        user.updatedAt)

    }
}
