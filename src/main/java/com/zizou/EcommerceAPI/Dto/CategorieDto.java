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
public class CategorieDto {

	private Long id;
	private String nomCat;
	private List<LivreDto> livres = new ArrayList<>();

	// ---- Getters ----

	public Long getId() {
		return id;
	}

	public String getNomCat() {
		return nomCat;
	}

	public List<LivreDto> getLivres() {
		return livres;
	}

	// ---- Setters ----

	public void setId(Long id) {
		this.id = id;
	}

	public void setNomCat(String nomCat) {
		this.nomCat = nomCat;
	}

	public void setLivres(List<LivreDto> livres) {
		this.livres = livres;
	}
}
