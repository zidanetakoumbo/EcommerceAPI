package com.zizou.EcommerceAPI.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.zizou.EcommerceAPI.Dto.AppUserDto;
import com.zizou.EcommerceAPI.Entity.AppUser;
import com.zizou.EcommerceAPI.Exception.ResourceNotFoundException;
import com.zizou.EcommerceAPI.Repository.AppUserRepository;

@Service
public class AppUserService {

	@Autowired
	private AppUserRepository userRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public AppUserDto register(AppUser user) {
		if (userRepo.findByEmail(user.getEmail()).isPresent()) {
			throw new IllegalStateException("Cet email est déjà utilisé");
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		if (user.getRoles() == null || user.getRoles().isEmpty()) {
			user.setRoles(List.of("ROLE_USER"));
		}
		return mapToDto(userRepo.save(user));
	}

	public List<AppUserDto> getAllUser() {
		return userRepo.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
	}

	public Optional<AppUser> getUserByEmail(String email) {
		return userRepo.findByEmail(email);
	}

	public AppUserDto getProfile(String userId) {
		AppUser user = userRepo.findById(userId)
			.orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
		return mapToDto(user);
	}

	public AppUserDto updateProfile(String userId, AppUserDto dto) {
		AppUser user = userRepo.findById(userId)
			.orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
		user.setNom(dto.getNom());
		user.setPrenom(dto.getPrenom());
		user.setAdresse(dto.getAdresse());
		user.setDateNaissance(dto.getDateNaissance());
		return mapToDto(userRepo.save(user));
	}

	public AppUserDto mapToDto(AppUser user) {
		AppUserDto dto = new AppUserDto();
		dto.setId(user.getId());
		dto.setNom(user.getNom());
		dto.setPrenom(user.getPrenom());
		dto.setAdresse(user.getAdresse());
		dto.setEmail(user.getEmail());
		dto.setDateNaissance(user.getDateNaissance());
		dto.setRoles(user.getRoles());
		return dto;
	}
}
