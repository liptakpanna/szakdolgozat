package com.dna.application.backend.model;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
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
        final Set<GrantedAuthority> _grntdAuths = new HashSet<GrantedAuthority>();

        Collection<Role> roles = null;

        if (user!=null) {
            roles = user.getRoles();
        }

        /*if (roles!=null) {
            for (Role _role : roles) {
                //TODO
                //_grntdAuths.add(new GrantedAuthorityImpl(_role.getRole()));
            }
        }*/

        return _grntdAuths;
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