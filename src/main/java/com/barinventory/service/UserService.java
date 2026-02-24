package com.barinventory.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.barinventory.entity.Role;
import com.barinventory.entity.User;
import com.barinventory.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void createUser(String name, String email, String rawPassword, Role role) {

        User user = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))  // ✅ IMPORTANT
                .role(role)
                .active(true)
                .build();

        userRepository.save(user);
    }
}