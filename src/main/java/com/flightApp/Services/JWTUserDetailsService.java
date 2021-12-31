package com.flightApp.Services;

import java.util.ArrayList;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.flightApp.DTOs.UserDto;
import com.flightApp.Entities.AuthEntity;
import com.flightApp.repositories.AuthRepository;

@Service
public class JWTUserDetailsService implements UserDetailsService {

	private AuthRepository authRepository;

	private ModelMapper modelMapper;

	@Autowired
	public JWTUserDetailsService(AuthRepository authRepository, ModelMapper modelMapper) {
		super();
		this.authRepository = authRepository;
		this.modelMapper = modelMapper;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<AuthEntity> resp = authRepository.findByUserName(username);
		if (resp.isPresent()) {
			AuthEntity user = resp.get();
			return new User(user.getUserName(), bcryptPassword(user.getPassword()), new ArrayList<>());
		} else {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}

	}

	public static String bcryptPassword(String password) {

		PasswordEncoder encoder = new BCryptPasswordEncoder();

		return encoder.encode(password);
	}

	public UserDto findUser(String userName) {
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		Optional<AuthEntity> authEntity = authRepository.findByUserName(userName);

		if (authEntity.isPresent()) {
			return modelMapper.map(authEntity.get(), UserDto.class);
		} else {
			return null;
		}
	}
}
