package com.zizou.EcommerceAPI.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zizou.EcommerceAPI.Entity.Livre;

@Repository
public interface LivreRepository extends JpaRepository<Livre,String >{

}
