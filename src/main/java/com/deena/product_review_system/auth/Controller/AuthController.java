package com.deena.product_review_system.auth.Controller;

import com.deena.product_review_system.auth.DTO.AuthResponse;
import com.deena.product_review_system.auth.DTO.LoginRequest;
import com.deena.product_review_system.auth.DTO.RegisterRequest;
import com.deena.product_review_system.auth.util.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        if (authService.existsByEmail(request.getEmail())) {
            // 409 Conflict if user already exists
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        // The authenticationManager in AuthService handles bad credentials (throws exception)
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
