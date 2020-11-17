package com.tja.bh.auth.persistence.repository;

import com.tja.bh.auth.persistence.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
