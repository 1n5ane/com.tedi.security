package com.tedi.security.domains

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.security.core.GrantedAuthority

@Entity
@Embeddable
@Table(name = "users")
class User extends org.springframework.security.core.userdetails.User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @NotEmpty
    @NotNull
    @Size(min=2, max= 25, message = "Username must be between 2 and 25 characters")
    @Column
    String username

    @NotEmpty
    @NotNull
    @Column
    String email

    @NotEmpty
    @NotNull
    @Size(min=8,max=255,message = "Password must be between 8 and 255 characters")
    @Column
    String password

    @Column
    String firstName

    @Column
    String lastName

    @Column
    Boolean locked

    @Column
    @Temporal(value = TemporalType.TIMESTAMP)
    Date createdAt

    @Column
    @Temporal(value = TemporalType.TIMESTAMP)
    Date updatedAt

    public User() {
        super("demo", "demo", [])
    }

    User(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities)
        this.username = username
        this.password = password
    }
    User(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities)
        this.id = id
        this.username = username
        this.password = password
    }

    User(Long id,
         String username,
         String password,
         Collection<? extends GrantedAuthority> authorities,
         String name,
         String lastname,
         String email,
         Boolean locked,
         Date createdAt,
         Date updatedAt
    ) {
        super(username, password, !locked ,true,true,!locked,authorities)
        this.id = id
        this.username = username
        this.password = password
        this.email = email
        this.lastName = lastname
        this.firstName = name
        this.locked = locked
        this.updatedAt = null
        this.locked = false
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false
        if (!super.equals(o)) return false

        User user = (User) o

        if (createdAt != user.createdAt) return false
        if (email != user.email) return false
        if (firstName != user.firstName) return false
        if (id != user.id) return false
        if (lastName != user.lastName) return false
        if (locked != user.locked) return false
        if (password != user.password) return false
        if (updatedAt != user.updatedAt) return false
        if (username != user.username) return false

        return true
    }

    int hashCode() {
        int result = super.hashCode()
        result = 31 * result + (id != null ? id.hashCode() : 0)
        result = 31 * result + (username != null ? username.hashCode() : 0)
        result = 31 * result + (email != null ? email.hashCode() : 0)
        result = 31 * result + (password != null ? password.hashCode() : 0)
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0)
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0)
        result = 31 * result + (locked != null ? locked.hashCode() : 0)
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0)
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0)
        return result
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", locked=" + locked +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
