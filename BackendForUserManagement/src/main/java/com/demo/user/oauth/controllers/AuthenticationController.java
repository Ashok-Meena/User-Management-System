package com.demo.user.oauth.controllers;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.user.app.config.dtos.LoginUserDto;
import com.demo.user.app.config.dtos.RegisterUserDto;
import com.demo.user.app.services.AuthenticationService;
import com.demo.user.entities.UserEntity;
import com.demo.user.model.LoginResponse;
import com.demo.user.token.JwtService;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
	private final JwtService jwtService;

	private final AuthenticationService authenticationService;

	public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
		this.jwtService = jwtService;
		this.authenticationService = authenticationService;
	}

	@PostMapping("/signup")
	public ResponseEntity<UserEntity> register(@RequestBody RegisterUserDto registerUserDto) {
		
		Set<String> roles = registerUserDto.getRoles();
		UserEntity registeredUser = authenticationService.signup(registerUserDto, roles);

		return ResponseEntity.ok(registeredUser);
	}

	@PostMapping("/loginUser")
	public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
		
		UserEntity authenticatedUser = authenticationService.authenticate(loginUserDto);

		String jwtToken = jwtService.generateToken(authenticatedUser);

		LoginResponse loginResponse = new LoginResponse();

		loginResponse.setToken(jwtToken);
		loginResponse.setExpiresIn(jwtService.getExpirationTime());

		return ResponseEntity.ok(loginResponse);
	}
}