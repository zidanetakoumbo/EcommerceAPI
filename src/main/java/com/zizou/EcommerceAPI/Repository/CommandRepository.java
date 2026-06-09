package com.zizou.EcommerceAPI.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zizou.EcommerceAPI.Entity.Command;
import com.zizou.EcommerceAPI.Entity.CommandStatus;

@Repository
public interface CommandRepository extends JpaRepository<Command, Long> {

    // Toutes les commandes d'un utilisateur (paginées, pour "Mes commandes")
    Page<Command> findByUserId(String userId, Pageable pageable);

    // Liste simple par statut (usage interne)
    List<Command> findByStatus(CommandStatus status);

    // Version paginée par statut (utilisée par l'admin)
    Page<Command> findByStatus(CommandStatus status, Pageable pageable);

    // Toutes les commandes dont le statut N'EST PAS dans la liste fournie (pour "non livrées")
    // Spring Data génère automatiquement la requête SQL : WHERE status NOT IN (...)
    Page<Command> findByStatusNotIn(List<CommandStatus> statuses, Pageable pageable);
}
