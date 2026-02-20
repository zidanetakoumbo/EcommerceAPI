package com.zizou.EcommerceAPI.Entity;

import java.util.ArrayList;
import java.util.Date;

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
	@GeneratedValue(strategy = GenerationType.UUID)
	private String Id ; 
	private String titre ; 
	private String resume ; 
	private double prix ; 
	private Date outDate ; 
	private String openCouverture ; 
	private String closeCouverture ; 
	
	@ManyToOne
	@JoinColumn(name = "autheur_id")
	private Autheur autheur ; 
	
	@ManyToMany
	@JoinTable(
			name ="livre_cat",
			joinColumns = @JoinColumn(name="livre_id"),
			inverseJoinColumns = @JoinColumn(name="cat_id")		
	)
	public ArrayList<Categorie> categories = new ArrayList<>() ; 
	

}
