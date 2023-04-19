package com.dsp.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	static final String[] ALLOWED_URLS = new String[] { "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
			"/health", "/register" };

	@Bean
	public SecurityFilterChain configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeHttpRequests((requests) -> requests.requestMatchers(ALLOWED_URLS).permitAll());
		return http.build();
	}
}