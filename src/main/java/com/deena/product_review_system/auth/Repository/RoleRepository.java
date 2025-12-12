package com.deena.product_review_system.auth.Repository;

import com.deena.product_review_system.auth.model.Role;
import com.deena.product_review_system.auth.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {

    Optional<Role> findByName(RoleType name);
}
