package com.zizou.EcommerceAPI.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.zizou.EcommerceAPI.Entity.Categorie;
import com.zizou.EcommerceAPI.Exception.ResourceNotFoundException;
import com.zizou.EcommerceAPI.Repository.CategorieRepository;

import jakarta.transaction.Transactional;

/**
 * Service de gestion des catégories de livres.
 * Fournit les opérations CRUD sur l'entité Categorie.
 */
@Service
public class CategorieService {

	private final CategorieRepository catRepos;

	public CategorieService(CategorieRepository repos) {
		this.catRepos = repos;
	}

	/** Retourne la liste complète de toutes les catégories */
	public List<Categorie> getAll() {
		return catRepos.findAll();
	}

	/**
	 * Recherche une catégorie par son identifiant.
	 * Lance une exception si la catégorie est introuvable (plus safe que .get()).
	 */
	public Categorie getById(Long id) {
		return catRepos.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable avec l'id : " + id));
	}

	/** Crée et persiste une nouvelle catégorie */
	@Transactional
	public Categorie create(Categorie c) {
		return catRepos.save(c);
	}

	/**
	 * Met à jour le nom d'une catégorie existante.
	 * Lance une exception si la catégorie est introuvable.
	 */
	@Transactional
	public Categorie update(Long id, Categorie categorieUpdated) {
		Categorie existing = catRepos.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable avec l'id : " + id));

		// Mise à jour du nom de la catégorie
		existing.setNomCat(categorieUpdated.getNomCat());

		return catRepos.save(existing);
	}

	/**
	 * Supprime une catégorie par son identifiant.
	 * Retourne true si supprimée, false si introuvable.
	 */
	@Transactional
	public boolean delete(Long id) {
		if (!catRepos.existsById(id)) {
			return false;
		}
		catRepos.deleteById(id);
		return true;
	}
}
