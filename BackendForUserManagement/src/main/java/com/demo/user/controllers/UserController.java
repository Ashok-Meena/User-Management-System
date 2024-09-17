package com.demo.user.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.user.app.config.dtos.RegisterUserDto;
import com.demo.user.entities.UserEntity;

import java.util.List;

@RequestMapping("app/users")
@RestController
public class UserController {
	
	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

//	@GetMapping("/me")
//	public ResponseEntity<User> authenticatedUser() {
//		
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//		User currentUser = (User) authentication.getPrincipal();
//
//		return ResponseEntity.ok(currentUser);
//	}
//
//	@GetMapping("/getAllUsers")
//	public ResponseEntity<List<User>> allUsers() {
//		List<User> users = userService.allUsers();
//
//		return ResponseEntity.ok(users);
//	}
	
	@PostMapping("/create")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<UserEntity> createUser(@RequestBody RegisterUserDto registerUserDto) {
	    UserEntity createdUser = userService.createUser(registerUserDto);
	    return ResponseEntity.ok(createdUser);
	}

	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<String> deleteUser(@PathVariable Long id) {
	    
	    return userService.deleteUser(id);
	}

	@GetMapping("/getAllUsers")
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
	public ResponseEntity<List<UserEntity>> allUsers() {
	    List<UserEntity> userEntities = userService.allUsers();
	    return ResponseEntity.ok(userEntities);
	}

}
