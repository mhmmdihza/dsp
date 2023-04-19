package com.dsp.test.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.dsp.test.entity.Users;
import com.dsp.test.model.UserRegistrationRequest;
import com.dsp.test.repository.UsersRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@SpringBootTest
@ActiveProfiles("test")
public class RegisterServiceTest {
	private static final Logger logger = LoggerFactory.getLogger(RegisterServiceTest.class);

	@Autowired
	RegisterService registerService;

	@Autowired
	UsersRepository usersRepository;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Test
	@Transactional
	public void testRegister() {
		List<RegisterScenario> sc = new ArrayList<>();
		// scenario 1
		sc.add(this.setScenario("success", new UserRegistrationRequest("081234567891", "name", "Abcd1234"), null));
		// scenario 2
		sc.add(this.setScenario("should return phone number already exists",
				new UserRegistrationRequest("081234567891", "name", "Abcd1234"), "Phone Number is already exists"));

		for (RegisterScenario r : sc) {
			try {
				UserRegistrationRequest req = r.getRequest();
				try {
					registerService.register(req);
				} catch (Exception e) {
					assertEquals(r.getErrorThrow(), e.getMessage());
				}
				Users users = usersRepository.getReferenceById(r.getRequest().getPhoneNumber());
				assertEquals(req.getPhoneNumber(), users.getPhoneNumber());
				assertEquals(req.getName(), users.getName());
				assertTrue(bCryptPasswordEncoder.matches(req.getPassword(), users.getPassword()));
				logger.info("test success :" + r.getDescription());
			} catch (AssertionError e) {
				logger.error("test failed :" + r.getDescription() + ", with error message: " + e.getMessage());
				throw e;
			}
		}

	}

	private RegisterScenario setScenario(String desc, UserRegistrationRequest req, String errorMessage) {
		return new RegisterScenario(desc, req, errorMessage);
	}

}

@Getter
@Setter
@AllArgsConstructor
class RegisterScenario {
	private String description;
	private UserRegistrationRequest request;
	private String errorThrow;
}