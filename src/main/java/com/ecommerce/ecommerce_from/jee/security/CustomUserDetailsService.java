package com.ecommerce.ecommerce_from.jee.security;

import com.ecommerce.ecommerce_from.jee.entity.User;
import com.ecommerce.ecommerce_from.jee.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec : " + usernameOrEmail));
        return UserDetailsImpl.build(user);
    }
}
