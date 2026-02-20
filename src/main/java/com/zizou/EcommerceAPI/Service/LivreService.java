package com.zizou.EcommerceAPI.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.zizou.EcommerceAPI.Entity.Livre;
import com.zizou.EcommerceAPI.Repository.LivreRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // dans le constructeur initialise tout les final et notnull
public class LivreService {

	private final LivreRepository livreRepos;

	public LivreService(LivreRepository repos) {

		this.livreRepos = repos;

	}

	public List<Livre> getAll() {
		return livreRepos.findAll();
	}
	
	public Livre getById(String id ) {
		return livreRepos.findById(id).get() ; 
	}
	
	
	@Transactional
	public void  delete(String id) {
		livreRepos.deleteById(id); 
	}
	
	@Transactional
	public Livre  create(Livre l) {
		return livreRepos.save(l);
	}
	

}
