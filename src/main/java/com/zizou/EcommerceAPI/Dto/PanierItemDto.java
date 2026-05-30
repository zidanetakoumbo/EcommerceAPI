package com.zizou.EcommerceAPI.Dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PanierItemDto {

	private Long id;

	/** Identifiant du livre */
	private Long livreId;

	/** Titre du livre (dénormalisé pour l'affichage) */
	private String titreLivre;

	/** URL/Base64 de la couverture du livre */
	private String openCouverture;

	/** Prix unitaire du livre */
	private double prixUnitaire;

	/** Quantité dans le panier */
	private int quantite;

	/** Prix total de la ligne : quantite * prixUnitaire */
	private double prixTotal;

	// ---- Getters ----

	public Long getId() {
		return id;
	}

	public Long getLivreId() {
		return livreId;
	}

	public String getTitreLivre() {
		return titreLivre;
	}

	public String getOpenCouverture() {
		return openCouverture;
	}

	public double getPrixUnitaire() {
		return prixUnitaire;
	}

	public int getQuantite() {
		return quantite;
	}

	public double getPrixTotal() {
		return prixTotal;
	}

	// ---- Setters ----

	public void setId(Long id) {
		this.id = id;
	}

	public void setLivreId(Long livreId) {
		this.livreId = livreId;
	}

	public void setTitreLivre(String titreLivre) {
		this.titreLivre = titreLivre;
	}

	public void setOpenCouverture(String openCouverture) {
		this.openCouverture = openCouverture;
	}

	public void setPrixUnitaire(double prixUnitaire) {
		this.prixUnitaire = prixUnitaire;
	}

	public void setQuantite(int quantite) {
		this.quantite = quantite;
	}

	public void setPrixTotal(double prixTotal) {
		this.prixTotal = prixTotal;
	}
}
