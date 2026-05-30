package com.zizou.EcommerceAPI.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zizou.EcommerceAPI.Entity.CommandItem;

/**
 * Repository JPA pour les lignes de commande (CommandItem).
 * Permet notamment de récupérer tous les articles d'une commande donnée.
 */
@Repository
public interface CommandItemRepository extends JpaRepository<CommandItem, Long> {

    /**
     * Récupère tous les articles appartenant à une commande précise.
     * Utile pour afficher le détail d'une commande sans passer par la relation Command.items.
     */
    List<CommandItem> findByCommandId(Long commandId);
}
