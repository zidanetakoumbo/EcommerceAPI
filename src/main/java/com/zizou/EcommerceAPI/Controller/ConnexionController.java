package com.zizou.EcommerceAPI.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zizou.EcommerceAPI.Configuration.JwtService;
import com.zizou.EcommerceAPI.Service.AppUserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/connexion")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ConnexionController {
	
	
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final AppUserService userService; 

	public ConnexionController(AuthenticationManager auth, AppUserService userServ,  JwtService jwt) {

		this.authenticationManager = auth;
		this.userService = userServ ; 
		this.jwtService = jwt;
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request) {

		authenticationManager
		.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));

		String token = jwtService.generateToken(request.getEmail());
		String userName = userService.getUserByEmail(request.getEmail()).get().getNom(); 

		return ResponseEntity.ok(new LoginResponse(token,request.getEmail(),userName));
	}
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletRequest request) {
		
		return ResponseEntity.ok("Déconnexion Réussie") ; 
	}
}
