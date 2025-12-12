package com.deena.product_review_system.auth.services;

import com.deena.product_review_system.auth.Repository.PermissionRepository;
import com.deena.product_review_system.auth.Repository.RoleRepository;
import com.deena.product_review_system.auth.model.Permission;
import com.deena.product_review_system.auth.model.Role;
import com.deena.product_review_system.auth.model.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    // Assume you have a UserRepository and PasswordEncoder for creating a test user

    // 1. Define all permissions as constants for consistency
    public static final String PRODUCT_CREATE = "PRODUCT:CREATE";
    public static final String PRODUCT_UPDATE = "PRODUCT:UPDATE";
    public static final String PRODUCT_READ_ALL = "PRODUCT:READ_ALL";
    public static final String PRODUCT_DELETE_ALL = "PRODUCT:DELETE_ALL";
    public static final String REVIEW_CREATE = "REVIEW:CREATE";
    public static final String USER_MANAGE = "USER:MANAGE";

    @Override
    public void run(String... args) throws Exception {

        // --- 1. Create and Save Permissions ---
        Permission create = savePermissionIfNotFound(PRODUCT_CREATE);
        Permission update = savePermissionIfNotFound(PRODUCT_UPDATE);
        Permission readAll = savePermissionIfNotFound(PRODUCT_READ_ALL);
        Permission deleteAll = savePermissionIfNotFound(PRODUCT_DELETE_ALL);
        Permission reviewCreate = savePermissionIfNotFound(REVIEW_CREATE);
        Permission userManage = savePermissionIfNotFound(USER_MANAGE);

        // --- 2. Define Permissions Sets for Each Role ---

        // ADMIN: All Permissions
        Set<Permission> adminPermissions = new HashSet<>(Set.of(create, update, readAll, deleteAll, reviewCreate, userManage));

        // PRODUCT_OWNER: Create, Update, ReadAll
        Set<Permission> ownerPermissions = new HashSet<>(Set.of(create, update, readAll));

        // PRODUCT_REVIEWER: Review Create, Product ReadAll
        Set<Permission> reviewerPermissions = new HashSet<>(Set.of(reviewCreate, readAll));

        // USER: Only Review Create (ReadAll is implicitly handled for all authenticated users)
        Set<Permission> userPermissions = new HashSet<>(Set.of(reviewCreate));

        // --- 3. Create and Save Roles with their Permissions ---
        saveRoleWithPermissions(RoleType.ADMIN, adminPermissions);
        saveRoleWithPermissions(RoleType.PRODUCT_OWNER, ownerPermissions);
        saveRoleWithPermissions(RoleType.PRODUCT_REVIEWER, reviewerPermissions);
        saveRoleWithPermissions(RoleType.USER, userPermissions);

        // --- 4. OPTIONAL: Create a Test User ---
        // (Not shown, but here you would create an admin/owner user for Postman testing)
    }

    // Helper method to ensure we don't duplicate permissions
    private Permission savePermissionIfNotFound(String name) {
        return permissionRepository.findByName(name)
                .orElseGet(() -> permissionRepository.save(new Permission(null, name)));
    }

    // Helper method to ensure we update or create roles
    private void saveRoleWithPermissions(RoleType roleType, Set<Permission> permissions) {
        Role role = roleRepository.findByName(roleType)
                .orElse(new Role(null, roleType, new HashSet<>()));

        role.setPermissions(permissions); // Assign the calculated permissions
        roleRepository.save(role);
    }
}
