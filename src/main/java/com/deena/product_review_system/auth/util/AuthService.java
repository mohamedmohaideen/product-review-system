package com.deena.product_review_system.auth.util;

import com.deena.product_review_system.auth.DTO.AuthResponse;
import com.deena.product_review_system.auth.DTO.LoginRequest;
import com.deena.product_review_system.auth.DTO.RegisterRequest;
import com.deena.product_review_system.auth.Repository.RoleRepository;
import com.deena.product_review_system.auth.Repository.UserRepository;
import com.deena.product_review_system.auth.model.Role;
import com.deena.product_review_system.auth.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    // Helper method for existence check (using email is fine here)
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        // Simple check to ensure basic role is present if not provided (Example logic)
        Set<Role> userRoles = registerRequest.getRoles().stream()
                .map(roleType ->
                        roleRepository.findByName(roleType)
                                .orElseThrow(() -> new RuntimeException("Role does not exist: " + roleType))
                )
                .collect(Collectors.toSet());
        User user = User.builder()
                .name(registerRequest.getName())
                .username(registerRequest.getUsername().trim())
                .password(passwordEncoder.encode(registerRequest.getPassword())) // HASH the password
                .email(registerRequest.getEmail().trim())
                .createdAt(LocalDate.now())
                .roles(userRoles) // Ensure this Set contains persisted Role entities
                .build();

        var savedUser = userRepository.save(user);
        var accessToken = jwtService.generateToken(savedUser);

        // Note: Converting roles Set<Role> to String for DTO is often complex;
        // typically, you return a List of role names.
        return AuthResponse.builder()
                .accessToken(accessToken)
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRoles().stream().map(role -> role.getName().toString()).collect(java.util.stream.Collectors.joining(",")))
                .build();
    }

    public AuthResponse login(LoginRequest loginRequest) {
        // 1. Authenticate the user credentials
        // We use the provided 'username' (which is the user's principal)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(), // Using username for consistency with UserDetailsService
                        loginRequest.getPassword()
                )
        );

        // 2. Load the authenticated user object
        User savedUser = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + loginRequest.getEmail()));

        // 3. Generate JWT
        String accessToken = jwtService.generateToken(savedUser);

        // 4. Return response
        return AuthResponse.builder()
                .accessToken(accessToken)
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRoles().stream().map(role -> role.getName().toString()).collect(java.util.stream.Collectors.joining(",")))
                .build();
    }
}
