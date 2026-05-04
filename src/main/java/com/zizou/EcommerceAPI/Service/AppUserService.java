package com.zizou.EcommerceAPI.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.zizou.EcommerceAPI.Entity.AppUser;
import com.zizou.EcommerceAPI.Repository.AppUserRepository;

@Service
public class AppUserService {

	@Autowired
	private AppUserRepository userRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public AppUser register(AppUser user) {

		if (userRepo.findByEmail(user.getEmail()).isPresent()) {
			throw new RuntimeException("Email already exists");
		}

		// CRYPTER LE PASSWORD
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		return userRepo.save(user);
	}

	public List<AppUser> getAllUser() {
		return userRepo.findAll();
	}

	public Optional<AppUser> getUserByEmail(String email) {
		return userRepo.findByEmail(email);
	}
}
