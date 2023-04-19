package com.dsp.test.controller;

import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dsp.test.model.CommonError;
import com.dsp.test.model.UserRegistrationRequest;
import com.dsp.test.service.RegisterService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/register")
public class RegisterController {

	private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

	@Autowired
	RegisterService registerService;

	@PostMapping
	public ResponseEntity<?> registerUser(@RequestBody @Valid UserRegistrationRequest request) {
		try {
			registerService.register(request);
		} catch (Exception e) {
			logger.error(e.getMessage());
			CommonError responseBody = new CommonError("failed registration",
					new ArrayList<>(Arrays.asList(e.getMessage())));
			return ResponseEntity.badRequest().body(responseBody);
		}
		return ResponseEntity.ok().build();
	}

}