package com.zizou.EcommerceAPI.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.zizou.EcommerceAPI.Entity.Livre;

@Repository
public interface LivreRepository extends JpaRepository<Livre, Long> {

    Page<Livre> findAll(Pageable pageable);

    @Query("""
        SELECT DISTINCT l FROM Livre l
        LEFT JOIN l.categories c
        WHERE (:titre IS NULL OR LOWER(l.titre) LIKE LOWER(CONCAT('%', :titre, '%')))
          AND (:autheurId IS NULL OR l.autheur.id = :autheurId)
          AND (:categorieId IS NULL OR c.id = :categorieId)
          AND (:prixMin IS NULL OR l.prix >= :prixMin)
          AND (:prixMax IS NULL OR l.prix <= :prixMax)
        """)
    Page<Livre> rechercher(
        @Param("titre") String titre,
        @Param("autheurId") Long autheurId,
        @Param("categorieId") Long categorieId,
        @Param("prixMin") Double prixMin,
        @Param("prixMax") Double prixMax,
        Pageable pageable
    );
    
    @Query("SELECT DISTINCT l FROM Livre l LEFT JOIN FETCH l.categories")
    List<Livre> findAllWithCategories();
}
