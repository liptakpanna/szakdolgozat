package com.dna.application.backend.model;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private  User user;

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        final Set<GrantedAuthority> grntdAuths = new HashSet<GrantedAuthority>();

        User.Role role = null;
        if (user!=null) {
            role = user.getRole();
        }

        if (role == User.Role.ADMIN) grntdAuths.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        else if (role == User.Role.RESEARCHER) grntdAuths.add(new SimpleGrantedAuthority("ROLE_RESEARCHER"));
        else if (role == User.Role.GUEST) grntdAuths.add(new SimpleGrantedAuthority("ROLE_GUEST"));

        return grntdAuths;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }
    @Override
    public String getUsername() {
        if (this.user == null) {
            return null;
        }
        return this.user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        //return this.user.isAccountNonExpired(); TODO
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        //return this.user.isAccountNonLocked(); TODO
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        //return this.user.isCredentialsNonExpired(); TODO
        return true;
    }

    @Override
    public boolean isEnabled() {
        //return this.user.isEnabled(); TODO
        return true;
    }

    @Override
    public String toString() {
        return "CustomUserDetails [user=" + user + "]";
    }
}