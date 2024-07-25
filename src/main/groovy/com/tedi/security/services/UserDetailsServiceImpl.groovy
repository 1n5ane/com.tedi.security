package com.tedi.security.services

import com.tedi.security.configuration.properties.UserSecurityProperties
import com.tedi.security.domains.Role
import com.tedi.security.domains.User
import com.tedi.security.domains.UserRole
import com.tedi.security.domains.keys.UserRoleKey
import com.tedi.security.repositories.RoleRepository
import com.tedi.security.repositories.UserRepository
import com.tedi.security.repositories.UserRoleRepository
import com.tedi.security.utils.data.validation.exception.UserEmailExistsException
import com.tedi.security.utils.data.validation.exception.UserRoleNotExistsException
import com.tedi.security.utils.data.validation.exception.UserUsernameExistsException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.text.SimpleDateFormat

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
    UserSecurityProperties securityProperties

    @Autowired
    PasswordEncoder passwordEncoder


    @Override
    UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
//      CHECK IF USER IS CONFIG USER
        def securityPropertiesUser = securityProperties.getUserDetails()
        if (securityPropertiesUser.username == usernameOrEmail || securityPropertiesUser.email == usernameOrEmail) {
            return securityPropertiesUser
        }

//      CHECK DB FOR USER
        def user = userRepository.findByUsername(usernameOrEmail)
        if (user == null)
//          search user by email
            user = userRepository.findByEmail(usernameOrEmail)

//      Username or Email not found
        if (user == null)
            throw new UsernameNotFoundException("User ${usernameOrEmail} not found!")


        def userRolesSet = userRoleRepository.findByUserId(user.id)
        def authorities = createUserGrantedAuthorities(userRolesSet)

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

    User findUserByEmail(String email) throws Exception {
        def user = userRepository.findByEmail(email)
        if (user == null)
            return user


        def userRolesSet = userRoleRepository.findByUserId(user.id)
        def authorities = createUserGrantedAuthorities(userRolesSet)

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

    User findUserByUsername(String username) throws Exception {
//      Only CHECK DB FOR USER -> no config user
        def user = userRepository.findByUsername(username)

        if (user == null)
            return user

        def userRolesSet = userRoleRepository.findByUserId(user.id)
        def authorities = createUserGrantedAuthorities(userRolesSet)

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

    User findUserById(Long id) {
        def securityPropertiesUser = securityProperties.getUserDetails()
        if (securityPropertiesUser.id == id) {
            return securityPropertiesUser
        }

        def user = userRepository.findById(id)

        if (user.isEmpty())
            return null
        user = user.get()
        def userRolesSet = userRoleRepository.findByUserId(id)
        def authorities = createUserGrantedAuthorities(userRolesSet)

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

    List<User> listAllUsers(Integer page, Integer pageSize, String sortBy, String order) {
        List<User> users = []
        Sort.Direction direction = Sort.Direction.DESC
        if (order == "asc")
            direction = Sort.Direction.ASC
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortBy))
        Page<User> pageUser = userRepository.findAll(pageable)
        users = pageUser.getContent()
        def userIdList = []
        users.each { u ->
            userIdList.add(u.id)
        }

        def userRolesSet = userRoleRepository.findByUserIdIn(userIdList)
        def resultUsers = []
        users.each { user ->
            def currentUserRoles = userRolesSet.findAll { userRole ->
                userRole.id.userId == user.id
            }

            def currentUserGrantedAuthorities = createUserGrantedAuthorities(currentUserRoles)
            resultUsers.add(new User(user.id,
                    user.username,
                    user.password,
                    currentUserGrantedAuthorities,
                    user.firstName,
                    user.lastName,
                    user.email,
                    user.locked,
                    user.createdAt,
                    user.updatedAt))
        }

        return resultUsers

    }

    Long countAllUsers() {
        return userRepository.count()
    }

    @Transactional(rollbackFor = Exception.class)
    User createUser(String username,
                    String email,
                    String password,
                    String firstName,
                    String lastName,
                    ArrayList<String> authorities = ["ROLE_USER"],
                    Boolean locked = Boolean.FALSE,
                    Date updated = null) throws Exception {


        password = passwordEncoder.encode(password)

//      CHECK IF ALREADY REGISTERED
        def dbUser = findUserByUsername(username)

        if (dbUser) {
            throw new UserUsernameExistsException("User '${username}' already registered")
        }

        dbUser = findUserByEmail(email)
        if (dbUser) {
            throw new UserEmailExistsException("Email '${email}' already registered")
        }

//      CREATE USER
        def userAuthoritiesList = []
        authorities.each { authority ->
            userAuthoritiesList.push(new SimpleGrantedAuthority(authority))
        }

        Date currentDate = new Date()
        def user = new User(null,
                username,
                password,
                userAuthoritiesList,
                firstName,
                lastName,
                email,
                locked,
                currentDate,
                updated)

        user = userRepository.save(user)

//      Create UserRoles also!
        authorities.each { authority ->
            Optional<Role> r = roleRepository.findByName(authority)
            if (r.isEmpty()) {
                throw new UserRoleNotExistsException("Role '${authority}' not exists")
            }
            UserRole userRole = new UserRole()
            userRole.id = new UserRoleKey()
            userRole.id.userId = user.id
            userRole.id.roleId = r.get().id
            userRole.user = user
            userRole.role = r.get()
            userRoleRepository.save(userRole)
        }
        return user
    }


    @Transactional(rollbackFor = Exception.class)
    User updateUser(String id,
                    String username,
                    String email,
                    String password,
                    String firstName,
                    String lastName,
                    ArrayList<String> authorities = ["ROLE_USER"],
                    Boolean locked = Boolean.FALSE) throws Exception {

//      fetch old user (using id)
        def userToBeUpdated = userRepository.findById(id.toLong())
        if (userToBeUpdated.isEmpty())
            throw new Exception("User with id ${id} not exists")

        userToBeUpdated = userToBeUpdated.get()
//      get users current roles
        Set<UserRole> userRoleSet = authorities?.size() > 0 ? userRoleRepository.findByUserId(userToBeUpdated.id) : null
        def userCurrentGrantedAuthorities = []
        userRoleSet.each { userRole ->
            userCurrentGrantedAuthorities.add(new SimpleGrantedAuthority(userRole.role.name))
        }
        userToBeUpdated = new User(
                userToBeUpdated.id,
                userToBeUpdated.username,
                userToBeUpdated.password,
                userCurrentGrantedAuthorities,
                userToBeUpdated.firstName,
                userToBeUpdated.lastName,
                userToBeUpdated.email,
                userToBeUpdated.locked,
                userToBeUpdated.createdAt,
                userToBeUpdated.updatedAt
        )

//      if user changed username -> need to check for uniqueness
        def fieldIsEmpty = (username == null || username.isEmpty())
        if (!fieldIsEmpty && userToBeUpdated.username != username) {
            def u = userRepository.findByUsername(username)
            if (u != null) {
                logger.error("Can't update users current username" +
                        " ('${userToBeUpdated.username}') to '${username}' as it already exists")
                throw new UserUsernameExistsException("Username already associated with another account")
            }
        }

//      if user changes email -> need to check for uniqueness
        fieldIsEmpty = (email == null || email.isEmpty())
        if (!fieldIsEmpty && userToBeUpdated.email != email) {
            def u = userRepository.findByEmail(email)
            if (u != null) {
                logger.error("Can't update users current email" +
                        " ('${userToBeUpdated.email}') to '${email}' as it already exists")
                throw new UserEmailExistsException("Email already associated with another account")
            }
        }

//      if password not changed keep the prev hash,
//      because if user makes no changes to his details,
//      passwordEncoder generates a different hash every time
//      so unnecessary updates will be done
        fieldIsEmpty = (password == null || password.isEmpty())
        if (fieldIsEmpty || passwordEncoder.matches(password, userToBeUpdated.password)) {
            password = userToBeUpdated.password
        } else {
            password = passwordEncoder.encode(password)
        }
        Date updatedDate = new Date()

        def userAuthoritiesList = []
        authorities.each { authority ->
            userAuthoritiesList.push(new SimpleGrantedAuthority(authority))
        }

        def updatedUser = new User(id.toLong(),
                username,
                password,
                userAuthoritiesList,
                firstName,
                lastName,
                email,
                locked,
                userToBeUpdated.createdAt,
                updatedDate)
        def updatedRolesSet = new HashSet()
        if (updatedUser != userToBeUpdated) {
            userRepository.save(updatedUser)
//         Get all roles
            List<Role> roleList = authorities?.size() > 0 ? roleRepository.findAll() : null
            //      update UserRoles (if changed)!
            authorities.each { authority ->
                Role r = roleList.find { r -> r.name == authority }
                if (r == null) {
                    throw new UserRoleNotExistsException("Role '${authority}' not exists")
                }

//              check if role is registered for user
                def currentUserRole = userRoleSet.find { userRole -> userRole.id.roleId == r.id }
                if (currentUserRole == null) {
//               create role as it doesn't exist for user
                    UserRole userRole = new UserRole()
                    userRole.id = new UserRoleKey()
                    userRole.id.userId = userToBeUpdated.id
                    userRole.id.roleId = r.id
                    userRole.user = userToBeUpdated
                    userRole.role = r
                    userRoleRepository.save(userRole)
//                  also add to userRoleSet
                    userRoleSet.add(userRole)
                    currentUserRole = userRole
                }
                updatedRolesSet.add(currentUserRole)
            }
            if (authorities?.size() > 0) {
//          delete roles that's not in the request
                def rolesToDeleteList = userRoleSet - updatedRolesSet
                rolesToDeleteList.each { userRole ->
                    userRoleRepository.delete(userRole)
                }

            }
            return updatedUser
        }

//      null indicates no changes
        return null
    }

    @Transactional(rollbackFor = Exception.class)
    void deleteUser(String id) {
        userRepository.deleteById(id.toLong())
        userRoleRepository.deleteAllRolesByUserId(id.toLong())
    }

    private static ArrayList<SimpleGrantedAuthority> createUserGrantedAuthorities(Set<UserRole> userRoleSet) {
        Set<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<SimpleGrantedAuthority>()
        userRoleSet.each { userRole ->
            grantedAuthorities.add(new SimpleGrantedAuthority(userRole.role.name))
        }
        return grantedAuthorities
    }
}
