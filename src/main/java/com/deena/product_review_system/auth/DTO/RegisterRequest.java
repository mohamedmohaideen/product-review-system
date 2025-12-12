package com.deena.product_review_system.auth.DTO;

import com.deena.product_review_system.auth.model.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {  // API Level

    private String name;
    private String username;
    private String password;
    private String email;
    private Set<RoleType> roles;

}
