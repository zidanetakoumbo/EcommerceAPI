package com.zizou.EcommerceAPI.Dto;

import com.zizou.EcommerceAPI.Entity.StatutPaiement;

import java.util.Date;

/**
 * DTO (Data Transfer Object) pour les informations de paiement.
 *
 * Pourquoi utiliser un DTO et pas l'entité directement ?
 * - Évite d'exposer la structure interne de la base de données
 * - Permet de sélectionner exactement les champs à envoyer au frontend
 * - Évite les problèmes de sérialisation JSON avec les relations JPA
 *
 * Cet objet est retourné par le controller quand on consulte
 * le statut d'un paiement.
 */
public class PaiementDto {

    /** Identifiant du paiement dans notre base de données. */
    private Long id;

    /** Identifiant de la commande associée à ce paiement. */
    private Long commandeId;

    /**
     * L'identifiant Stripe de l'intention de paiement.
     * Le client peut l'utiliser pour retrouver la transaction sur son relevé.
     * Exemple : "pi_3OxxxxxxxxxxxxxxxxxXXXX"
     */
    private String stripePaymentIntentId;

    /** Le montant payé en euros (ex: 29.99). */
    private double montant;

    /** La devise utilisée (toujours "eur" dans notre app). */
    private String devise;

    /**
     * L'état actuel du paiement.
     * Valeurs possibles : EN_ATTENTE, REUSSI, ECHOUE, REMBOURSE
     */
    private StatutPaiement statut;

    /** Date à laquelle l'intention de paiement a été créée. */
    private Date dateCreation;

    /**
     * Date à laquelle le paiement a été confirmé.
     * Vaut null si le paiement n'a pas encore réussi.
     */
    private Date datePaiement;

    // ========== Constructeurs ==========

    public PaiementDto() {}

    // ========== Getters et Setters ==========

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCommandeId() { return commandeId; }
    public void setCommandeId(Long commandeId) { this.commandeId = commandeId; }

    public String getStripePaymentIntentId() { return stripePaymentIntentId; }
    public void setStripePaymentIntentId(String stripePaymentIntentId) {
        this.stripePaymentIntentId = stripePaymentIntentId;
    }

    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }

    public String getDevise() { return devise; }
    public void setDevise(String devise) { this.devise = devise; }

    public StatutPaiement getStatut() { return statut; }
    public void setStatut(StatutPaiement statut) { this.statut = statut; }

    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }

    public Date getDatePaiement() { return datePaiement; }
    public void setDatePaiement(Date datePaiement) { this.datePaiement = datePaiement; }
}
