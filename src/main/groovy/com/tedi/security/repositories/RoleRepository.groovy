package com.tedi.security.repositories

import com.tedi.security.domains.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findById(Long id)
    Optional<Role> findByName(String name)
    List<Role> findAll()
}