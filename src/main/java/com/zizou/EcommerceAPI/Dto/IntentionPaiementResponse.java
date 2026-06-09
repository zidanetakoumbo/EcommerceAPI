package com.zizou.EcommerceAPI.Dto;

/**
 * Réponse envoyée au frontend après création d'une intention de paiement.
 *
 * === COMMENT LE FRONTEND UTILISE CETTE RÉPONSE ===
 *
 * 1. Le frontend reçoit cette réponse avec le "clientSecret"
 * 2. Il initialise Stripe.js avec la clé publique (pk_test_xxx)
 * 3. Il utilise le clientSecret pour confirmer le paiement :
 *
 *    const stripe = Stripe('pk_test_votre_cle_publique');
 *    const result = await stripe.confirmCardPayment(clientSecret, {
 *      payment_method: {
 *        card: cardElement,  // Élément de saisie de carte Stripe
 *        billing_details: { name: 'Jean Dupont' }
 *      }
 *    });
 *
 *    if (result.error) {
 *      // Afficher l'erreur à l'utilisateur
 *    } else {
 *      // Paiement réussi ! Notre webhook sera notifié automatiquement
 *    }
 *
 * === SÉCURITÉ ===
 * Le "clientSecret" est temporaire et spécifique à cette transaction.
 * Il ne permet que de confirmer CE paiement précis.
 * Les données de carte ne transitent JAMAIS par notre serveur.
 */
public class IntentionPaiementResponse {

    /**
     * La clé secrète client fournie par Stripe.
     * Format : "pi_xxxxx_secret_yyyyy"
     *
     * C'est la valeur la plus importante de cette réponse.
     * Le frontend en a absolument besoin pour finaliser le paiement
     * avec Stripe.js.
     */
    private String clientSecret;

    /**
     * L'identifiant Stripe de l'intention de paiement.
     * Format : "pi_xxxxxxxxxxxxxxxxxxxxx"
     * Utile pour le débogage ou pour référencer la transaction.
     */
    private String paymentIntentId;

    /**
     * L'identifiant du paiement dans notre base de données.
     * Le frontend peut l'utiliser pour consulter le statut via
     * GET /api/paiement/statut/{commandeId}
     */
    private Long paiementId;

    /**
     * Le montant total de la commande en euros.
     * Utile pour afficher "Vous allez payer 29.99€" à l'utilisateur.
     */
    private double montant;

    // ========== Constructeurs ==========

    public IntentionPaiementResponse() {}

    public IntentionPaiementResponse(String clientSecret, String paymentIntentId,
                                     Long paiementId, double montant) {
        this.clientSecret = clientSecret;
        this.paymentIntentId = paymentIntentId;
        this.paiementId = paiementId;
        this.montant = montant;
    }

    // ========== Getters et Setters ==========

    public String getClientSecret() { return clientSecret; }
    public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }

    public String getPaymentIntentId() { return paymentIntentId; }
    public void setPaymentIntentId(String paymentIntentId) { this.paymentIntentId = paymentIntentId; }

    public Long getPaiementId() { return paiementId; }
    public void setPaiementId(Long paiementId) { this.paiementId = paiementId; }

    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }
}
