package com.zizou.EcommerceAPI.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zizou.EcommerceAPI.Entity.Panier;

//PanierRepository.java
@Repository
public interface PanierRepository extends JpaRepository<Panier, Long> {
	Optional<Panier> findByUserId(String userId);
}
