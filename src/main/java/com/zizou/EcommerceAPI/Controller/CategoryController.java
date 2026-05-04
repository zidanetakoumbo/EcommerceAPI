package com.zizou.EcommerceAPI.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zizou.EcommerceAPI.Entity.Categorie;
import com.zizou.EcommerceAPI.Service.CategorieService;

@CrossOrigin(origins = "*")
@RequestMapping("/api/categories")
@RestController
public class CategoryController {

	private final CategorieService cateService; 

	public CategoryController(CategorieService catserv) {
		this.cateService = catserv ; 
	}

	@GetMapping("/all")
	public List<Categorie> getAll() {
		System.out.println("test get categories");
		return cateService.getAll(); 
	}

	@GetMapping("/test")
	public String test() {
		return "hello";
	}

	@PostMapping("/create")
	public Categorie create(@RequestBody Categorie c ) {
		return cateService.create(c); 
	}

	@PostMapping("/{id}")
	public Categorie findById(@PathVariable Long id) {
		return this.cateService.getById(id) ; 
	}

}
