package com.zizou.EcommerceAPI.Dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AutheurDto {

	private Long id;
	private String nom;
	private String prenom;
	private List<LivreDto> livres = new ArrayList<>();

	// ---- Getters ----

	public Long getId() {
		return id;
	}

	public String getNom() {
		return nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public List<LivreDto> getLivres() {
		return livres;
	}

	// ---- Setters ----

	public void setId(Long id) {
		this.id = id;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public void setLivres(List<LivreDto> livres) {
		this.livres = livres;
	}
}
