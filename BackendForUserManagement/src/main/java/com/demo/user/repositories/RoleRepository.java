package com.demo.user.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.demo.user.entities.RoleEntity;

public interface RoleRepository extends CrudRepository<RoleEntity, Long> {

	Optional<RoleEntity> findByRoleName(String roleName);

}
