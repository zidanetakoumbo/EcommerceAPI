package com.zizou.EcommerceAPI.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.zizou.EcommerceAPI.Entity.Autheur;
import com.zizou.EcommerceAPI.Exception.ResourceNotFoundException;
import com.zizou.EcommerceAPI.Repository.AutheurRepository;

import jakarta.transaction.Transactional;

/**
 * Service de gestion des auteurs.
 * Fournit les opérations CRUD sur l'entité Autheur.
 */
@Service
public class AutheurService {

	private final AutheurRepository autheurRepos;

	public AutheurService(AutheurRepository repos) {
		this.autheurRepos = repos;
	}

	/** Retourne la liste complète de tous les auteurs */
	public List<Autheur> getAll() {
		return autheurRepos.findAll();
	}

	/**
	 * Recherche un auteur par son identifiant.
	 * Lance une exception si l'auteur est introuvable (plus safe que .get()).
	 */
	public Autheur getById(Long id) {
		return autheurRepos.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Auteur introuvable avec l'id : " + id));
	}

	/** Crée et persiste un nouvel auteur */
	@Transactional
	public Autheur create(Autheur auth) {
		return autheurRepos.save(auth);
	}

	/**
	 * Met à jour le nom et prénom d'un auteur existant.
	 * Lance une exception si l'auteur est introuvable.
	 */
	@Transactional
	public Autheur update(Long id, Autheur autheurUpdated) {
		Autheur existing = autheurRepos.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Auteur introuvable avec l'id : " + id));

		// Mise à jour uniquement des champs éditables
		existing.setNom(autheurUpdated.getNom());
		existing.setPrenom(autheurUpdated.getPrenom());

		return autheurRepos.save(existing);
	}

	/**
	 * Supprime un auteur par son identifiant.
	 * Retourne true si supprimé, false si introuvable.
	 */
	@Transactional
	public boolean delete(Long id) {
		if (!autheurRepos.existsById(id)) {
			return false;
		}
		autheurRepos.deleteById(id);
		return true;
	}
}
