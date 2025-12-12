package com.deena.product_review_system.auth.services;

import com.deena.product_review_system.auth.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsServices implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
// Loads the UserDetails object (which is your User entity)
        return userRepository.findByEmail(username)
                .orElseThrow(()-> new UsernameNotFoundException("User not found: " + username));
    }
}
