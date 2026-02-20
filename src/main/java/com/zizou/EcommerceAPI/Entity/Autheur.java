package com.zizou.EcommerceAPI.Entity;

import java.util.ArrayList;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Autheur {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id ; 
	private String nom ; 
	private String prenom ; 
	
	@OneToMany(mappedBy = "autheur")
	private ArrayList<Livre> livres ; 
	

}
