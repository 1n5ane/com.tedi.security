package com.tedi.security.repositories

import com.tedi.security.domains.User
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository extends PagingAndSortingRepository<User, Long>,
                                 CrudRepository<User,Long> {

    Optional<User> findById(Long id)
    User findByUsername(String username)
    User findByEmail(String email)
//    List<User> findAll()

}