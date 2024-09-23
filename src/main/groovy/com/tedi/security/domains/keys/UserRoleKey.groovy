package com.tedi.security.domains.keys

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class UserRoleKey implements  Serializable {
    @Column(name = "id_users")
    Long userId

    @Column(name = "id_roles")
    Long roleId

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        UserRoleKey that = (UserRoleKey) o

        if (roleId != that.roleId) return false
        if (userId != that.userId) return false

        return true
    }

    int hashCode() {
        int result
        result = (userId != null ? userId.hashCode() : 0)
        result = 31 * result + (roleId != null ? roleId.hashCode() : 0)
        return result
    }
}
