package com.deena.product_review_system.auth.Controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/test")
public class TestController {
    // --- Access control via SecurityFilterChain (URL Level) ---
    // Access: /api/admin - Requires ROLE_ADMIN (defined in SecurityConfiguration)

    @GetMapping("/admin")
    public String adminEndpoint() {
        return "Welcome, Admin! Access granted by SecurityFilterChain.";
    }

    // --- Access control via @PreAuthorize (Method Level) ---

    // Requires the user to have the specific permission "USER_READ"
    @GetMapping("/users")
    @PreAuthorize("hasAuthority('USER_READ')")
    public String userReadEndpoint() {
        return "Allowed to read users (USER_READ permission).";
    }

    // Requires the user to have the specific permission "PRODUCT_DELETE"
    @DeleteMapping("/product/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_DELETE')")
    public String productDeleteEndpoint(@PathVariable Long id) {
        return "Allowed to delete product " + id + " (PRODUCT_DELETE permission).";
    }
}
