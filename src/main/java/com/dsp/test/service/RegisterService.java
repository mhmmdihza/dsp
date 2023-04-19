package com.dsp.test.service;

import com.dsp.test.model.UserRegistrationRequest;

public interface RegisterService {
	public void register(UserRegistrationRequest req) throws Exception;
}