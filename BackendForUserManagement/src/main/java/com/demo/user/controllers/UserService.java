package com.demo.user.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.demo.user.app.config.dtos.RegisterUserDto;
import com.demo.user.entities.RoleEntity;
import com.demo.user.entities.UserEntity;
import com.demo.user.exceptions.ResourceNotFoundException;
import com.demo.user.repositories.RoleRepository;
import com.demo.user.repositories.UserRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

	private final UserRepository userRepository;
	
	private final RoleRepository roleRepository;

	public UserService(UserRepository userRepository, RoleRepository roleRepository) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
	}

	public List<UserEntity> allUsers() {
		List<UserEntity> userEntities = new ArrayList<>();

		userRepository.findAll().forEach(userEntities::add);

		return userEntities;
	}

	public ResponseEntity<String> deleteUser(Long id) {
		
		Optional<UserEntity> userEntity = userRepository.findById(id);

	    if (userEntity.isPresent()) {
	        userRepository.deleteById(id);
	        return ResponseEntity.ok("User deleted successfully with id: " + id); // Return message on successful deletion
	    } else {
	        throw new ResourceNotFoundException("User not found with id: " + id);
	    }
	}

	public UserEntity createUser(RegisterUserDto registerUserDto) {

		UserEntity userEntity = new UserEntity();

		userEntity.setFullName(registerUserDto.getFullName());
		userEntity.setEmail(registerUserDto.getEmail());
		userEntity.setPassword(registerUserDto.getPassword());
		
		Set<RoleEntity> roles = new HashSet<>();
	    for (String roleName : registerUserDto.getRoles()) {  
	        RoleEntity role = roleRepository.findByRoleName(roleName)
	                .orElseThrow(() -> new RuntimeException("Role not found"));
	        roles.add(role);
	    }

	    userEntity.setRoles(roles);
	    
		userEntity.setCreatedAt(new Date());
		userEntity.setUpdatedAt(new Date());

		return userRepository.save(userEntity);
	}
}