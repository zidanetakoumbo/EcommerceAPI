package com.zizou.EcommerceAPI.Dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AppUserDto {

	private String id;
	private String nom;
	private String prenom;
	private String adresse;
	private String email;
	private String dateNaissance;
	private List<String> roles = new ArrayList<>();

	// ---- Getters ----

	public String getId() {
		return id;
	}

	public String getNom() {
		return nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public String getAdresse() {
		return adresse;
	}

	public String getEmail() {
		return email;
	}

	public String getDateNaissance() {
		return dateNaissance;
	}

	public List<String> getRoles() {
		return roles;
	}

	// ---- Setters ----

	public void setId(String id) {
		this.id = id;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setDateNaissance(String dateNaissance) {
		this.dateNaissance = dateNaissance;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
}
