package com.demo.user.app.services;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.demo.user.app.config.dtos.LoginUserDto;
import com.demo.user.app.config.dtos.RegisterUserDto;
import com.demo.user.entities.RoleEntity;
import com.demo.user.entities.UserEntity;
import com.demo.user.repositories.RoleRepository;
import com.demo.user.repositories.UserRepository;

@Service
public class AuthenticationService {

	private final UserRepository userRepository;

	private final RoleRepository roleRepository;

	private final PasswordEncoder passwordEncoder;

	private final AuthenticationManager authenticationManager;

	public AuthenticationService(UserRepository userRepository, RoleRepository roleRepository,
			AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
		this.authenticationManager = authenticationManager;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.roleRepository = roleRepository;
	}

	public UserEntity signup(RegisterUserDto input, Set<String> roles) {
		UserEntity userEntity = new UserEntity();

		userEntity.setFullName(input.getFullName());
		userEntity.setEmail(input.getEmail());
		userEntity.setPassword(passwordEncoder.encode(input.getPassword()));

		// Fetch roles from the database
	    Set<RoleEntity> roleEntities = roles.stream()
	        .map(roleName -> roleRepository.findByRoleName(roleName)
	            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
	        .collect(Collectors.toSet());
		userEntity.setRoles(roleEntities); // Set the role when creating the user

		return userRepository.save(userEntity);
	}

	public UserEntity authenticate(LoginUserDto input) {
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword()));

		return userRepository.findByEmail(input.getEmail()).orElseThrow();
	}
}
