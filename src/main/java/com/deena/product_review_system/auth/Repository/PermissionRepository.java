package com.deena.product_review_system.auth.Repository;

import com.deena.product_review_system.auth.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission,Long> {

    Optional<Permission> findByName(String name);
}
