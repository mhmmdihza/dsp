package com.dsp.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dsp.test.entity.Users;

public interface UsersRepository extends JpaRepository<Users, String> {
}