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

import com.zizou.EcommerceAPI.Entity.Autheur;
import com.zizou.EcommerceAPI.Service.AutheurService;

/**
 * Contrôleur REST pour la gestion des auteurs.
 * Lecture publique, modifications réservées ADMIN (géré dans SecurityConfig).
 */
@RestController
@RequestMapping("/api/autheurs")
@CrossOrigin(origins = "*")
public class AutheurController {

	private final AutheurService authService;

	public AutheurController(AutheurService auths) {
		this.authService = auths;
	}

	/** GET /api/autheurs/all — liste tous les auteurs */
	@GetMapping("/all")
	public List<Autheur> getAll() {
		return authService.getAll();
	}

	/** GET /api/autheurs/{id} — retourne un auteur par son identifiant */
	@GetMapping("/{id}")
	public ResponseEntity<Autheur> findById(@PathVariable Long id) {
		return ResponseEntity.ok(authService.getById(id));
	}

	/** POST /api/autheurs/create — crée un nouvel auteur (ADMIN) */
	@PostMapping("/create")
	public ResponseEntity<Autheur> create(@RequestBody Autheur a) {
		return ResponseEntity.status(201).body(authService.create(a));
	}

	/**
	 * PUT /api/autheurs/{id} — met à jour nom et prénom d'un auteur (ADMIN).
	 * Retourne 404 si l'auteur est introuvable.
	 */
	@PutMapping("/{id}")
	public ResponseEntity<Autheur> update(@PathVariable Long id, @RequestBody Autheur autheur) {
		return ResponseEntity.ok(authService.update(id, autheur));
	}

	/**
	 * PUT /api/autheurs/update/{id} — alias pour la compatibilité avec le frontend Angular.
	 * Le frontend appelle /update/{id}, on délègue simplement à la méthode ci-dessus.
	 */
	@PutMapping("/update/{id}")
	public ResponseEntity<Autheur> updateAlias(@PathVariable Long id, @RequestBody Autheur autheur) {
		return update(id, autheur);
	}

	/**
	 * DELETE /api/autheurs/{id} — supprime un auteur (ADMIN).
	 * Retourne 204 si supprimé, 404 si introuvable.
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		boolean deleted = authService.delete(id);
		return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}

	/**
	 * DELETE /api/autheurs/delete/{id} — alias pour la compatibilité avec le frontend Angular.
	 */
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteAlias(@PathVariable Long id) {
		return delete(id);
	}
}
