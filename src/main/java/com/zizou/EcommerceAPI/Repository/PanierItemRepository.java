package com.zizou.EcommerceAPI.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zizou.EcommerceAPI.Entity.PanierItem;

//PanierItemRepository.java
@Repository
public interface PanierItemRepository extends JpaRepository<PanierItem, Long> {
 List<PanierItem> findByPanierId(Long panierId);
}