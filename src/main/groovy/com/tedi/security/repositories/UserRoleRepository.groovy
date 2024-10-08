package com.tedi.security.repositories

import com.tedi.security.domains.UserRole
import com.tedi.security.domains.keys.UserRoleKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRoleRepository extends JpaRepository<UserRole, UserRoleKey>{

    @Query("select ur from UserRole ur where ur.id.userId=:userId")
    Set<UserRole> findByUserId(@Param("userId") Long id)

    @Query("select ur from UserRole ur where ur.id.userId in :userIdList")
    Set<UserRole> findByUserIdIn(@Param("userIdList") List<Long> userIdList)

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from UserRole ur where ur.id.userId=:userId")
    void deleteAllRolesByUserId(@Param("userId") Long id)
}
