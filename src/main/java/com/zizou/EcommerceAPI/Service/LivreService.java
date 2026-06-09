package com.zizou.EcommerceAPI.Service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.zizou.EcommerceAPI.Entity.Livre;
import com.zizou.EcommerceAPI.Exception.ResourceNotFoundException;
import com.zizou.EcommerceAPI.Repository.LivreRepository;

import jakarta.transaction.Transactional;

/**
 * Service de gestion des livres du catalogue.
 */
@Service
public class LivreService {

    private final LivreRepository livreRepos;

    public LivreService(LivreRepository repos) {
        this.livreRepos = repos;
    }

    /** Retourne tous les livres sans pagination */
    public List<Livre> getAll() {
        return livreRepos.findAllWithCategories();
    }

    /** Retourne les livres avec pagination et tri */
    public Page<Livre> getAllPaginated(Pageable pageable) {
        return livreRepos.findAll(pageable);
    }

    /**
     * Recherche un livre par son identifiant.
     * Lance une exception si introuvable plutôt que de retourner null.
     */
    public Livre getById(Long id) {
        return livreRepos.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Livre introuvable avec l'id : " + id));
    }

    /** Supprime un livre par son identifiant. Retourne true si supprimé, false sinon. */
    @Transactional
    public boolean delete(Long id) {
        if (livreRepos.existsById(id)) {
            livreRepos.deleteById(id);
            return true;
        }
        return false;
    }

    /** Recherche des livres avec des filtres optionnels */
    public Page<Livre> rechercher(String titre, Long autheurId, Long categorieId,
                                  Double prixMin, Double prixMax, Pageable pageable) {
        return livreRepos.rechercher(
            titre != null && !titre.isBlank() ? titre : null,
            autheurId,
            categorieId,
            prixMin,
            prixMax,
            pageable
        );
    }

    /** Crée et persiste un nouveau livre */
    @Transactional
    public Livre create(Livre l) {
        return livreRepos.save(l);
    }

    /**
     * Met à jour un livre existant par copie de propriétés.
     * L'identifiant est exclu de la copie pour ne pas écraser la clé primaire.
     */
    @Transactional
    public Livre update(Long id, Livre livreUpdated) {
        Livre existingLivre = livreRepos.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Livre introuvable avec l'id : " + id));

        // Copie toutes les propriétés sauf l'id
        BeanUtils.copyProperties(livreUpdated, existingLivre, "id");

        return livreRepos.save(existingLivre);
    }
}
