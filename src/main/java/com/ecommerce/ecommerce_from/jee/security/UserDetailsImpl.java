package com.ecommerce.ecommerce_from.jee.security;

import com.ecommerce.ecommerce_from.jee.enums.Role;
import com.ecommerce.ecommerce_from.jee.entity.User;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private Long id;
    private String username;
    private String email;
    private String password;
    private boolean enabled;
    private Collection<? extends GrantedAuthority> authorities;

    // Construction à partir de l'entité User
    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = List.of(
                (GrantedAuthority) () -> "ROLE_" + user.getRole().name()
        );

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                authorities
        );
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return enabled; }
}
