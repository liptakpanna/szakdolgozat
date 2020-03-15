package com.dna.application.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="users")
public class User extends BaseEntityAudit implements UserDetails {
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Role role;

    private String createdBy;

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    @Fetch(value= FetchMode.SELECT)
    private Set<Alignment> ownedAlignments = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_x_alignment",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "alignment_id"))
    @Fetch(value= FetchMode.SELECT)
    private Set<Alignment> alignmentAccess = new HashSet<>();


    public enum Role {ADMIN, RESEARCHER, GUEST}

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        final Set<GrantedAuthority> grntdAuths = new HashSet<GrantedAuthority>();
        grntdAuths.add(new SimpleGrantedAuthority("ROLE_" + this.getRole().toString()));
        return grntdAuths;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
