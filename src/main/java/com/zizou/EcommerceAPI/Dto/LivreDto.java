package com.zizou.EcommerceAPI.Dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LivreDto {

	private Long id;
	private String titre;
	private String resume;
	private double prix;
	private Date outDate;
	private int quantiteStock;
	private int quantiteVendue;
	private String autheurNom;
	private String autheurPrenom;
	private List<String> categoriesNoms;

	// ---- Getters ----

	public Long getId() {
		return id;
	}

	public String getTitre() {
		return titre;
	}

	public String getResume() {
		return resume;
	}

	public double getPrix() {
		return prix;
	}

	public Date getOutDate() {
		return outDate;
	}

	public int getQuantiteStock() {
		return quantiteStock;
	}

	public int getQuantiteVendue() {
		return quantiteVendue;
	}

	public String getAutheurNom() {
		return autheurNom;
	}

	public String getAutheurPrenom() {
		return autheurPrenom;
	}

	public List<String> getCategoriesNoms() {
		return categoriesNoms;
	}

	// ---- Setters ----

	public void setId(Long id) {
		this.id = id;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public void setResume(String resume) {
		this.resume = resume;
	}

	public void setPrix(double prix) {
		this.prix = prix;
	}

	public void setOutDate(Date outDate) {
		this.outDate = outDate;
	}

	public void setQuantiteStock(int quantiteStock) {
		this.quantiteStock = quantiteStock;
	}

	public void setQuantiteVendue(int quantiteVendue) {
		this.quantiteVendue = quantiteVendue;
	}

	public void setAutheurNom(String autheurNom) {
		this.autheurNom = autheurNom;
	}

	public void setAutheurPrenom(String autheurPrenom) {
		this.autheurPrenom = autheurPrenom;
	}

	public void setCategoriesNoms(List<String> categoriesNoms) {
		this.categoriesNoms = categoriesNoms;
	}
}
