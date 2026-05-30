package com.zizou.EcommerceAPI.Dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommandItemDto {

	private Long id;

	/** Identifiant du livre concerné */
	private Long livreId;

	/** Titre du livre (dénormalisé pour éviter une requête supplémentaire côté front) */
	private String titreLivre;

	/** Quantité commandée */
	private int quantite;

	/** Prix unitaire au moment de la commande */
	private double prixUnitaire;

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

	public int getQuantite() {
		return quantite;
	}

	public double getPrixUnitaire() {
		return prixUnitaire;
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

	public void setQuantite(int quantite) {
		this.quantite = quantite;
	}

	public void setPrixUnitaire(double prixUnitaire) {
		this.prixUnitaire = prixUnitaire;
	}

	public void setPrixTotal(double prixTotal) {
		this.prixTotal = prixTotal;
	}
}
