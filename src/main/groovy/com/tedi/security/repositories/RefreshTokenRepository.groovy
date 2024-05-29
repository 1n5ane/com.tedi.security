package com.tedi.security.repositories

import com.tedi.security.domains.RefreshToken
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{

    @Query("select r from RefreshToken r where r.token=:token")
    Optional<RefreshToken> findByToken(@Param("token") String token)
}