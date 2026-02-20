package com.zizou.EcommerceAPI.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zizou.EcommerceAPI.Entity.Livre;
import com.zizou.EcommerceAPI.Service.LivreService;

@RestController
@RequestMapping("/livres")

public class LivreController {

	private final LivreService lvService ; 

	public LivreController(LivreService lvs) {
		this.lvService = lvs;
	}

	@GetMapping("/")
	public List<Livre> getAll() {
		return lvService.getAll();
	}

	@GetMapping("/test")
	public String test() {
		return "hello";
	}

	@PostMapping("/create")
	public Livre create(@RequestBody Livre l) {
		return lvService.create(l);
	}

	@PostMapping("/{id}")
	public Livre findById(@PathVariable String id) {
		return lvService.getById(null);
	}

}
