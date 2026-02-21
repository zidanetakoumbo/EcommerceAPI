package com.zizou.EcommerceAPI.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.zizou.EcommerceAPI.Entity.Autheur;
import com.zizou.EcommerceAPI.Service.AutheurService;

@RestController
public class AutheurController {

	private final AutheurService authService ; 

	public AutheurController(AutheurService auths) {
		this.authService = auths;
	}

	@GetMapping("/")
	public List<Autheur> getAll() {
		return authService.getAll(); 
	}

	@GetMapping("/test")
	public String test() {
		return "hello";
	}

	@PostMapping("/create")
	public Autheur create(@RequestBody Autheur a) {
		return authService.create(a); 
	}

	@PostMapping("/{id}")
	public Autheur findById(@PathVariable Long id) {
		return authService.getById(id) ; 
	}

}
