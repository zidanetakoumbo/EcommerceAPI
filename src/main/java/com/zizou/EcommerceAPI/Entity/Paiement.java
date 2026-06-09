package com.zizou.EcommerceAPI.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

import java.util.Date;

/**
 * Entité représentant un paiement associé à une commande.
 *
 * Chaque paiement est lié à exactement une commande.
 * Il est créé quand l'utilisateur clique sur "Payer" et mis à jour
 * automatiquement via les webhooks Stripe (notifications de Stripe vers
 * notre serveur).
 *
 * === TABLE EN BASE DE DONNÉES ===
 * Nom de la table : paiement
 * Colonnes principales :
 *   - id                      : Identifiant auto-incrémenté
 *   - commande_id             : Référence vers la table "command"
 *   - stripe_payment_intent_id: L'ID côté Stripe (ex: pi_3OxxxXXXX)
 *   - montant                 : Montant en euros (ex: 29.99)
 *   - devise                  : Toujours "eur" pour notre app
 *   - statut                  : EN_ATTENTE, REUSSI, ECHOUE ou REMBOURSE
 *   - date_creation           : Quand le paiement a été initialisé
 *   - date_paiement           : Quand le paiement a été confirmé (nullable)
 */
@Entity
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * La commande que ce paiement concerne.
     * Relation one-to-one : une commande = un paiement.
     */
    @OneToOne
    @JoinColumn(name = "commande_id")
    private Command commande;

    /**
     * L'identifiant de l'intention de paiement chez Stripe.
     * Format : "pi_" suivi de caractères alphanumériques.
     * Exemple : "pi_3OxxxxxxxxxxxxxxxxxXXXX"
     *
     * Utilisé pour :
     * - Retrouver le paiement depuis les webhooks Stripe
     * - Identifier le paiement sur le dashboard Stripe
     * - Effectuer un remboursement
     */
    private String stripePaymentIntentId;

    /**
     * Le montant du paiement en euros.
     * Note : Stripe reçoit ce montant multiplié par 100 (en centimes).
     * Exemple : 29.99€ → Stripe reçoit 2999.
     */
    private double montant;

    /**
     * La devise du paiement. Toujours "eur" dans notre application.
     * Stripe supporte d'autres devises (usd, gbp, etc.) si besoin.
     */
    private String devise;

    /**
     * L'état actuel du paiement.
     * Stocké en base sous forme de texte (ex: "EN_ATTENTE").
     * Mis à jour automatiquement quand Stripe envoie un webhook.
     */
    @Enumerated(EnumType.STRING)
    private StatutPaiement statut;

    /**
     * La date à laquelle l'intention de paiement a été créée dans notre système.
     * Toujours renseignée dès la création.
     */
    private Date dateCreation;

    /**
     * La date à laquelle le paiement a été confirmé par Stripe.
     * Vaut null tant que le paiement n'a pas réussi.
     */
    private Date datePaiement;

    // ========== Constructeurs ==========

    /** Constructeur vide requis par JPA. */
    public Paiement() {}

    // ========== Getters et Setters ==========

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Command getCommande() { return commande; }
    public void setCommande(Command commande) { this.commande = commande; }

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
