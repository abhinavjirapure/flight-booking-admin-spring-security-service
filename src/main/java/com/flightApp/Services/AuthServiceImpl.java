package com.flightApp.Services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flightApp.DTOs.AuthDTO;
import com.flightApp.Entities.AuthEntity;
import com.flightApp.repositories.AuthRepository;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private AuthRepository userRepository;

	@Override
	public AuthEntity validateUser(AuthDTO authDTO) {
		Optional<AuthEntity> resp = userRepository.findByUserName(authDTO.getUserName());

		if (resp.isPresent()) {
			AuthEntity user = resp.get();
			if (user.getPassword().equals(authDTO.getPassword())) {
				return user;
			} else {
				return null;
			}
		} else {
			return null;

		}
	}

}
