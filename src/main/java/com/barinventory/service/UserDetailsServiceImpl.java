package com.barinventory.service;

import com.barinventory.entity.User;
import com.barinventory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        log.info("Attempting to load user by email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found");
                });

        if (!user.getActive()) {
            log.warn("Disabled account login attempt: {}", email);
            throw new UsernameNotFoundException("Account is disabled");
        }

        log.info("User loaded successfully: {} with role: {}", email, user.getRole());

        // ✅ RETURN YOUR ENTITY DIRECTLY
        return user;
    }
}