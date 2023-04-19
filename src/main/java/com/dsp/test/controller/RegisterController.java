package com.dsp.test.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dsp.test.model.UserRegistrationRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/register")
public class RegisterController {

	@PostMapping
	public ResponseEntity<Void> registerUser(@RequestBody @Valid UserRegistrationRequest request) {
		return ResponseEntity.ok().build();
	}

}