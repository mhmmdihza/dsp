package com.dsp.test.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserRegistrationRequest {

	@JsonProperty(value = "phone_number")
	@NotBlank
	@Size(min = 10, max = 13)
	@Pattern(regexp = "^08[0-9]*$", message = "must start with '08' and contain only digits")
	private String phoneNumber;

	@JsonProperty
	@NotBlank
	@Size(max = 60)
	private String name;

	@JsonProperty
	@NotBlank
	@Size(min = 6, max = 16)
	@Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).*$", message = "must contains at least 1 capital letter and 1 number")
	private String password;
}
