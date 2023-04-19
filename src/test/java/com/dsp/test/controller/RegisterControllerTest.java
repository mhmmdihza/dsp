package com.dsp.test.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.dsp.test.model.CommonError;
import com.dsp.test.model.UserRegistrationRequest;
import com.dsp.test.service.RegisterService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class RegisterControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(RegisterControllerTest.class);

	@InjectMocks
	private RegisterController controller;

	@Mock
	private RegisterService registerService;

	private static final String name_exceed_limit = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

	@Test
	public void testRegister() throws Exception {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		List<RegisterScenario> sc = new ArrayList<>();
		// scenario 1
		sc.add(this.setScenario("success", new UserRegistrationRequest("081234567890", "name", "Abcd1234"),
				new ArrayList<>(), HttpStatus.OK, null));
		// scenario 2
		sc.add(this.setScenario("should return error phone already exists",
				new UserRegistrationRequest("089999999999", "name", "Abcd1234"), new ArrayList<>(),
				HttpStatus.BAD_REQUEST, "Phone Number is already exists"));
		// scenario 3
		sc.add(this.setScenario("invalid phone", new UserRegistrationRequest("a", "name", "Abcd1234"),
				new ArrayList<>(Arrays.asList("phoneNumber-must start with '08' and contain only digits",
						"phoneNumber-size must be between 10 and 13")),
				HttpStatus.OK, null));
		// scenario 4
		sc.add(this.setScenario("name exceeded limit",
				new UserRegistrationRequest("081234567890", name_exceed_limit, "Abcd1234"),
				new ArrayList<>(Arrays.asList("name-size must be between 0 and 60")), HttpStatus.OK, null));
		// scenario 5
		sc.add(this.setScenario("invalid password", new UserRegistrationRequest("081234567890", "name", "a"),
				new ArrayList<>(Arrays.asList("password-must contains at least 1 capital letter and 1 number",
						"password-size must be between 6 and 16")),
				HttpStatus.OK, null));

		for (RegisterScenario r : sc) {
			if (r.getRequest().getPhoneNumber().equals("089999999999")) {
				doThrow(new Exception("Phone Number is already exists")).when(registerService).register(Mockito.any());
			}
			// throw the AssertionError to mark the test case as failed
			try {
				Set<ConstraintViolation<UserRegistrationRequest>> finalResult = validator.validate(r.getRequest());
				assertEquals(r.getErrorValidation().size(), finalResult.size());
				Iterator<ConstraintViolation<UserRegistrationRequest>> iterator = finalResult.iterator();
				List<String> errorResult = new ArrayList<>();
				while (iterator.hasNext()) {
					ConstraintViolation<UserRegistrationRequest> violation = iterator.next();
					String errorMessage = violation.getMessage();
					String fieldName = violation.getPropertyPath().toString();
					errorResult.add(fieldName + "-" + errorMessage);
				}
				if (errorResult.size() > 0) {
					Collections.sort(errorResult, (v1, v2) -> v1.compareTo(v2));
					assertEquals(r.getErrorValidation(), errorResult);
				}
				if (r.getErrorValidation().size() == 0) {
					ResponseEntity<?> resp = controller.registerUser(r.getRequest());
					assertEquals(r.getExpectedStatusCode(), resp.getStatusCode());
					if (r.getErrorService() != null) {
						CommonError commonError = (CommonError) resp.getBody();
						assertEquals(r.getErrorService(), commonError.getError().get(0));
					}
				}
				logger.info("test success :" + r.getDescription());
			} catch (AssertionError e) {
				logger.error("test failed :" + r.getDescription() + ", with error message: " + e.getMessage());
				throw e;
			}

		}
	}

	private RegisterScenario setScenario(String desc, UserRegistrationRequest req, List<String> errorValidation,
			HttpStatusCode expected, String errorService) {
		return new RegisterScenario(errorValidation, desc, req, expected, errorService);
	}
}

@Getter
@Setter
@AllArgsConstructor
class RegisterScenario {
	private List<String> errorValidation;
	private String description;
	private UserRegistrationRequest request;
	private HttpStatusCode expectedStatusCode;
	private String errorService;
}