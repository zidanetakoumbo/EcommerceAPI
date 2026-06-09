package com.zizou.EcommerceAPI.Repository;

import com.zizou.EcommerceAPI.Entity.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository JPA pour l'entité Paiement.
 *
 * Spring Data JPA génère automatiquement les requêtes SQL à partir des
 * noms des méthodes. Pas besoin d'écrire du SQL manuellement pour les
 * opérations simples.
 *
 * Hérite de JpaRepository qui fournit déjà :
 * - save(paiement)         → INSERT ou UPDATE
 * - findById(id)           → SELECT WHERE id = ?
 * - findAll()              → SELECT *
 * - delete(paiement)       → DELETE WHERE id = ?
 * - count()                → SELECT COUNT(*)
 */
@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {

    /**
     * Trouve un paiement grâce à l'identifiant Stripe de l'intention de paiement.
     *
     * Utilisé principalement dans le traitement des webhooks Stripe :
     * quand Stripe nous notifie qu'un paiement a réussi, il nous envoie
     * l'ID de l'intention de paiement. On utilise cette méthode pour
     * retrouver le paiement correspondant dans notre base.
     *
     * SQL généré : SELECT * FROM paiement WHERE stripe_payment_intent_id = ?
     *
     * @param stripePaymentIntentId L'ID Stripe (ex: "pi_3OxxxxxxxxxxxxxxxxxXXXX")
     * @return Le paiement correspondant, ou Optional.empty() si introuvable
     */
    Optional<Paiement> findByStripePaymentIntentId(String stripePaymentIntentId);

    /**
     * Trouve le paiement associé à une commande.
     *
     * Utile pour vérifier si une commande a déjà un paiement en cours
     * ou pour afficher le statut du paiement d'une commande.
     *
     * SQL généré : SELECT * FROM paiement WHERE commande_id = ?
     *
     * @param commandeId L'identifiant de la commande dans notre base
     * @return Le paiement correspondant, ou Optional.empty() si aucun paiement
     */
    Optional<Paiement> findByCommandeId(Long commandeId);
}
