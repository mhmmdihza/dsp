package com.dsp.test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.dsp.test.entity.Users;
import com.dsp.test.model.UserRegistrationRequest;
import com.dsp.test.repository.UsersRepository;

@Service
public class RegisterServiceImpl implements RegisterService {

	@Autowired
	UsersRepository usersRepository;

	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	@Override
	public void register(UserRegistrationRequest req) throws Exception {
		if (usersRepository.existsById(req.getPhoneNumber())) {
			throw new Exception("Phone Number is already exists");
		}
		String encodedPassword = passwordEncoder.encode(req.getPassword());
		Users users = new Users();
		users.setName(req.getName());
		users.setPassword(encodedPassword);
		users.setPhoneNumber(req.getPhoneNumber());
		usersRepository.save(users);
	}

}