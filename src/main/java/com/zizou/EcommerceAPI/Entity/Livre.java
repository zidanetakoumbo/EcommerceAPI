package com.zizou.EcommerceAPI.Entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Livre {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // auto s'adapte en fonction de l abd hors pb historique
	private Long Id ; 
	private String titre; 
	private String resume; 
	private double prix; 
	private Date outDate; 
	private String openCouverture; 
	private String closeCouverture; 
	
	@ManyToOne
	@JoinColumn(name = "autheur_id")
	@JsonIgnoreProperties("livres")
	private Autheur autheur; 
	
	@ManyToMany
	@JoinTable(
			name ="livre_cat",
			joinColumns = @JoinColumn(name="livre_id"),
			inverseJoinColumns = @JoinColumn(name="cat_id")		
	)
	@JsonIgnoreProperties("livres")
	public List<Categorie> categories = new ArrayList<>();

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public String getResume() {
		return resume;
	}

	public void setResume(String resume) {
		this.resume = resume;
	}

	public double getPrix() {
		return prix;
	}

	public void setPrix(double prix) {
		this.prix = prix;
	}

	public Date getOutDate() {
		return outDate;
	}

	public void setOutDate(Date outDate) {
		this.outDate = outDate;
	}

	public String getOpenCouverture() {
		return openCouverture;
	}

	public void setOpenCouverture(String openCouverture) {
		this.openCouverture = openCouverture;
	}

	public String getCloseCouverture() {
		return closeCouverture;
	}

	public void setCloseCouverture(String closeCouverture) {
		this.closeCouverture = closeCouverture;
	}

	public Autheur getAutheur() {
		return autheur;
	}

	public void setAutheur(Autheur autheur) {
		this.autheur = autheur;
	}

	public List<Categorie> getCategories() {
		return categories;
	}

	public void setCategories(List<Categorie> categories) {
		this.categories = categories;
	} 
	
	
	

}
