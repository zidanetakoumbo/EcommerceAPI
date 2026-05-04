package com.zizou.EcommerceAPI.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zizou.EcommerceAPI.Entity.AppUser;
import com.zizou.EcommerceAPI.Service.AppUserService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/user")
public class AppUserController {

	@Autowired
	private AppUserService userService;

	public AppUserController() {
		// TODO Auto-generated constructor stub
	}

	@PostMapping("/register")
	public AppUser register(@RequestBody AppUser user) {
		return userService.register(user);
	}
	
	@GetMapping("/all")
	public List<AppUser> getAll(){
		return userService.getAllUser() ; 
	}

}
