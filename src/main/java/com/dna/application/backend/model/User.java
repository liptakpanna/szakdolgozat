package com.dna.application.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
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

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    private String createdBy;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private Set<Alignment> ownedAlignments = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_x_alignment",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "alignment_id"))
    private Set<Alignment> alignmentAccess = new HashSet<>();

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        final Set<GrantedAuthority> grntdAuths = new HashSet<GrantedAuthority>();
        grntdAuths.add(new SimpleGrantedAuthority("ROLE_" + this.getRole().toString()));
        return grntdAuths;
    }

    public User(Long _id, String _username, String _email, Role _role, Date _createdAt,
                Date _updatedAt, Status _status, String _createdBy, String _updatedBy,
                Set<Alignment> _ownedAlignments, Set<Alignment> _alignmentAcces) {
        id = _id;
        username = _username;
        email = _email;
        role = _role;
        updatedBy = _updatedBy;
        createdAt = _createdAt;
        updatedAt = _updatedAt;
        createdBy = _createdBy;
        status = _status;
        ownedAlignments = _ownedAlignments;
        alignmentAccess = _alignmentAcces;
    }

    public User(Long _id, String _username, String _email, String _password, Role _role, String _updatedBy) {
        id = _id;
        username = _username;
        email = _email;
        password = _password;
        role = _role;
        updatedBy = _updatedBy;
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
        return status == Status.ENABLED;
    }

    public enum Role {ADMIN, RESEARCHER, GUEST}

    public enum Status {ENABLED, DELETED}
}
