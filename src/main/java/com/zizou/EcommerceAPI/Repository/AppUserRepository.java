package com.zizou.EcommerceAPI.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zizou.EcommerceAPI.Entity.AppUser;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, String> {
	
	Optional<AppUser> findByEmail(String email);
	
	

}
