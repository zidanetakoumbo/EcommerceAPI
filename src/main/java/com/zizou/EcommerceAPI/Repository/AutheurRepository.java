package com.zizou.EcommerceAPI.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zizou.EcommerceAPI.Entity.Autheur;

@Repository
public interface AutheurRepository extends JpaRepository<Autheur,Long> {

}
