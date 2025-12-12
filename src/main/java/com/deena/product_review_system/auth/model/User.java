package com.deena.product_review_system.auth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User implements UserDetails {       //Database Level User Model

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false,unique = true)
    @NotEmpty(message = "Name cannot be empty")
    private String name;
    @Column(unique = true, nullable = false)
    @NotEmpty(message = "Username cannot be empty")
    private String username;
    @Column(nullable = false)
    @NotEmpty(message = "Password cannot be empty")
    private String password; // Stored HASHED
    @Column(unique = true, nullable = false)
    @Email(message = "Email should be valid")
    private String email;
    @Column(nullable = false)
    private LocalDate createdAt;

    // M:N relationship with Role
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // NOTE: Removed the redundant 'private Set<Permission> permissions' field here.

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        this.roles.forEach(role -> {
            // 1. Add Role Name as an Authority (required for hasRole() checks)
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

            // 2. Add all Permissions associated with the role (for hasAuthority() checks)
            role.getPermissions().forEach(permission ->
                    authorities.add(new SimpleGrantedAuthority(permission.getName()))
            );
        });

        return authorities;
    }

    // --- Standard UserDetails methods ---
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
    @Override public String getUsername() {
        return this.email;
    }
    // Note: getPassword() and getUsername() are provided by @Data
}
