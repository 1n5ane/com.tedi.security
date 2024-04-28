package com.tedi.security.domains

import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType

//@Entity
//@Table(name = "refresh_tokens")
class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Column
    String token

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_users")
    User user

    @Column(name = "created_at")
    @Temporal(value= TemporalType.TIMESTAMP)
    Date createdAt

    @Column
    @Temporal(value= TemporalType.TIMESTAMP)
    Date expiresAt

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        RefreshToken that = (RefreshToken) o

        if (createdAt != that.createdAt) return false
        if (id != that.id) return false
        if (token != that.token) return false
        if (expiresAt != that.expiresAt) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (token != null ? token.hashCode() : 0)
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0)
        result = 31 * result + (expiresAt != null ? expiresAt.hashCode() : 0)
        return result
    }
}
