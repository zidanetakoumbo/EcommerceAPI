package com.zizou.EcommerceAPI.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zizou.EcommerceAPI.Entity.Categorie;
import com.zizou.EcommerceAPI.Service.CategorieService;

/**
 * Contrôleur REST pour la gestion des catégories.
 * Lecture publique, modifications réservées ADMIN (géré dans SecurityConfig).
 */
@CrossOrigin(origins = "*")
@RequestMapping("/api/categories")
@RestController
public class CategoryController {

	private final CategorieService cateService;

	public CategoryController(CategorieService catserv) {
		this.cateService = catserv;
	}

	/** GET /api/categories/all — liste toutes les catégories */
	@GetMapping("/all")
	public List<Categorie> getAll() {
		return cateService.getAll();
	}

	/** GET /api/categories/{id} — retourne une catégorie par son identifiant */
	@GetMapping("/{id}")
	public ResponseEntity<Categorie> findById(@PathVariable Long id) {
		return ResponseEntity.ok(cateService.getById(id));
	}

	/** POST /api/categories/create — crée une nouvelle catégorie (ADMIN) */
	@PostMapping("/create")
	public ResponseEntity<Categorie> create(@RequestBody Categorie c) {
		return ResponseEntity.status(201).body(cateService.create(c));
	}

	/**
	 * PUT /api/categories/{id} — met à jour le nom d'une catégorie (ADMIN).
	 * Retourne 404 si la catégorie est introuvable.
	 */
	@PutMapping("/{id}")
	public ResponseEntity<Categorie> update(@PathVariable Long id, @RequestBody Categorie categorie) {
		return ResponseEntity.ok(cateService.update(id, categorie));
	}

	/**
	 * DELETE /api/categories/{id} — supprime une catégorie (ADMIN).
	 * Retourne 204 si supprimée, 404 si introuvable.
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		boolean deleted = cateService.delete(id);
		return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}
}
