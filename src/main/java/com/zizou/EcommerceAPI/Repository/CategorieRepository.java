package com.zizou.EcommerceAPI.Repository;

import java.util.Locale.Category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zizou.EcommerceAPI.Entity.Categorie;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie,Long> {

}
