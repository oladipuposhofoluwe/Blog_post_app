package com.dee.blog_rest.repositories;

import com.dee.blog_rest.entities.role.Role;
import com.dee.blog_rest.entities.role.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByName(RoleName name);
}
