package com.zizou.EcommerceAPI.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.zizou.EcommerceAPI.Entity.Autheur;
import com.zizou.EcommerceAPI.Repository.AutheurRepository;

import jakarta.transaction.Transactional;

@Service
public class AutheurService {

	private final AutheurRepository autheurRepos;

	public AutheurService(AutheurRepository repos) {
		this.autheurRepos = repos;
	}

	public List<Autheur> getAll() {
		return autheurRepos.findAll();
	}

	public Autheur getById(Long id) {
		return autheurRepos.findById(id).get();
	}

	@Transactional
	public void delete(Long id) {
		autheurRepos.deleteById(id);
	}

	@Transactional
	public Autheur create(Autheur auth) {
		return autheurRepos.save(auth);
	}
}
