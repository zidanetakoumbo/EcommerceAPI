package com.zizou.EcommerceAPI.Service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.zizou.EcommerceAPI.Entity.Livre;
import com.zizou.EcommerceAPI.Repository.LivreRepository;

import jakarta.transaction.Transactional;

@Service
public class LivreService {

	private final LivreRepository livreRepos;

	public LivreService(LivreRepository repos) {

		this.livreRepos = repos;

	}

	public List<Livre> getAll() {
		return livreRepos.findAll();
	}

	public Livre getById(Long id) {
		return livreRepos.findById(id).get();
	}

	@Transactional
	public void delete(Long id) {
		livreRepos.deleteById(id);
	}

	@Transactional
	public Livre create(Livre l) {
		return livreRepos.save(l);
	}

	// Modifier un livre (PUT)
	@Transactional
	public Livre update(Long id, Livre livreUpadted) {
		// 1. On vérifie si le livre existe en base
		Livre existingLivre = this.livreRepos.findById(id)
				.orElseThrow(() -> new RuntimeException("pas d'id trouvé avec avec la valeur " + id));

		// Copie TOUT de 'livreDetails' vers 'existingLivre'
		// ça nous evite de modifeir ligne par ligne
		// On exclut l'ID pour être sûr de ne pas écraser l'ID de la base de données
		BeanUtils.copyProperties(livreUpadted, existingLivre, "id");

		return this.livreRepos.save(existingLivre);

	}


}
