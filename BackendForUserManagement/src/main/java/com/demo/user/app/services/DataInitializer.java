package com.demo.user.app.services;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Component;
import com.demo.user.app.config.dtos.RegisterUserDto;
import com.demo.user.entities.RoleEntity;
import com.demo.user.repositories.RoleRepository;
import com.demo.user.repositories.UserRepository;

import jakarta.annotation.PostConstruct;

@Component
public class DataInitializer {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public DataInitializer(AuthenticationService authenticationService, UserRepository userRepository, RoleRepository roleRepository) {
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void initialize() {
        try {
            if (userRepository.count() == 0) {
                // Create roles if they do not exist
                createRoleIfNotExists("ROLE_ADMIN");
                createRoleIfNotExists("ROLE_USER");

                // Create a default admin user
                RegisterUserDto adminDto = new RegisterUserDto();
                adminDto.setEmail("superadmin@example.com");
                adminDto.setFullName("Super User");
                adminDto.setPassword("Pass@123");

                Set<String> roles = new HashSet<>();
                roles.add("ROLE_ADMIN");
                roles.add("ROLE_USER");

                adminDto.setRoles(roles);

                // Signup method should handle both password encoding and saving the user
                authenticationService.signup(adminDto, roles);
            }
        } catch (Exception e) {
            System.err.println("Error during initialization: " + e.getMessage()); // Debug log
            e.printStackTrace(); // Print stack trace for more details
        }

    private void createRoleIfNotExists(String roleName) {
        RoleEntity roleEntity = roleRepository.findByRoleName(roleName)
            .orElseGet(() -> {
                RoleEntity newRole = new RoleEntity();
                newRole.setRoleName(roleName);
                return roleRepository.save(newRole);
            });
    }
}
