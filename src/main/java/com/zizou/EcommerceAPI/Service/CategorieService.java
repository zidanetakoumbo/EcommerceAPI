package com.zizou.EcommerceAPI.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.zizou.EcommerceAPI.Entity.Categorie;
import com.zizou.EcommerceAPI.Repository.CategorieRepository;

import jakarta.transaction.Transactional;

@Service
public class CategorieService {

	private final CategorieRepository catRepos;

	public CategorieService(CategorieRepository repos) {
		this.catRepos = repos;
	}

	public List<Categorie> getAll() {
		return catRepos.findAll();
	}

	public Categorie getById(Long id) {
		return catRepos.findById(id).get();
	}

	@Transactional
	public void delete(Long id) {
		catRepos.deleteById(id);
	}

	@Transactional
	public Categorie create(Categorie c) {
		return catRepos.save(c);
	}

}
