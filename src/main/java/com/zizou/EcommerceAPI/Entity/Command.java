package com.zizou.EcommerceAPI.Entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Command {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;
    
    private Date dateCommande;
    private Date dateExpedition;
    private Date dateLivraison;
    
    @Enumerated(EnumType.STRING)
    private CommandStatus status; // PENDING, SHIPPED, DELIVERED, CANCELLED
    
    private double totalPrice;
    private String adresseLivraison;
    
    @OneToMany(mappedBy = "command", cascade = CascadeType.ALL)
    private List<CommandItem> items = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AppUser getUser() {
		return user;
	}

	public void setUser(AppUser user) {
		this.user = user;
	}

	public Date getDateCommande() {
		return dateCommande;
	}

	public void setDateCommande(Date dateCommande) {
		this.dateCommande = dateCommande;
	}

	public Date getDateExpedition() {
		return dateExpedition;
	}

	public void setDateExpedition(Date dateExpedition) {
		this.dateExpedition = dateExpedition;
	}

	public Date getDateLivraison() {
		return dateLivraison;
	}

	public void setDateLivraison(Date dateLivraison) {
		this.dateLivraison = dateLivraison;
	}

	public CommandStatus getStatus() {
		return status;
	}

	public void setStatus(CommandStatus status) {
		this.status = status;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getAdresseLivraison() {
		return adresseLivraison;
	}

	public void setAdresseLivraison(String adresseLivraison) {
		this.adresseLivraison = adresseLivraison;
	}

	public List<CommandItem> getItems() {
		return items;
	}

	public void setItems(List<CommandItem> items) {
		this.items = items;
	}
    
    
    
    
}
