package com.barinventory.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.barinventory.entity.User;
import com.barinventory.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
	                return new UsernameNotFoundException("User not found with email: " + email);
	            });

	        // ✅ Check if account is active
	        if (!user.getActive()) {
	            log.warn("Attempt to login with disabled account: {}", email);
	            throw new UsernameNotFoundException("Account is disabled");
	        }

	        log.info("User loaded successfully: {} with role: {}", email, user.getRole());
	        return user;
	    }

}