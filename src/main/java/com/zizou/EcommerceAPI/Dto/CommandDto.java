package com.zizou.EcommerceAPI.Dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zizou.EcommerceAPI.Entity.CommandStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommandDto {

	private Long id;
	private String userId;
	private String userNom;
	private Date dateCommande;
	private Date dateExpedition;
	private Date dateLivraison;
	private CommandStatus status;
	private double totalPrice;
	private String adresseLivraison;
	private List<CommandItemDto> items = new ArrayList<>();

	// ---- Getters ----

	public Long getId() {
		return id;
	}

	public String getUserId() {
		return userId;
	}

	public String getUserNom() {
		return userNom;
	}

	public Date getDateCommande() {
		return dateCommande;
	}

	public Date getDateExpedition() {
		return dateExpedition;
	}

	public Date getDateLivraison() {
		return dateLivraison;
	}

	public CommandStatus getStatus() {
		return status;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public String getAdresseLivraison() {
		return adresseLivraison;
	}

	public List<CommandItemDto> getItems() {
		return items;
	}

	// ---- Setters ----

	public void setId(Long id) {
		this.id = id;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setUserNom(String userNom) {
		this.userNom = userNom;
	}

	public void setDateCommande(Date dateCommande) {
		this.dateCommande = dateCommande;
	}

	public void setDateExpedition(Date dateExpedition) {
		this.dateExpedition = dateExpedition;
	}

	public void setDateLivraison(Date dateLivraison) {
		this.dateLivraison = dateLivraison;
	}

	public void setStatus(CommandStatus status) {
		this.status = status;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public void setAdresseLivraison(String adresseLivraison) {
		this.adresseLivraison = adresseLivraison;
	}

	public void setItems(List<CommandItemDto> items) {
		this.items = items;
	}
}
