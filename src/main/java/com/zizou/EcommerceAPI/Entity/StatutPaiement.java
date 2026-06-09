package com.zizou.EcommerceAPI.Entity;

/**
 * Enumération des états possibles d'un paiement.
 *
 * Un paiement passe par ces états dans cet ordre typique :
 *   EN_ATTENTE → REUSSI
 *         ↓
 *       ECHOUE (l'utilisateur peut réessayer)
 *
 * Un paiement REUSSI peut ensuite devenir REMBOURSE si l'admin le décide.
 */
public enum StatutPaiement {

    /**
     * L'intention de paiement a été créée chez Stripe,
     * mais l'utilisateur n'a pas encore complété le paiement.
     * C'est l'état initial.
     */
    EN_ATTENTE,

    /**
     * Stripe a confirmé que le paiement a été accepté par la banque.
     * La commande associée passe automatiquement à CONFIRMED.
     */
    REUSSI,

    /**
     * Le paiement a été refusé (carte invalide, fonds insuffisants, etc.).
     * La commande reste en PENDING. L'utilisateur peut réessayer.
     */
    ECHOUE,

    /**
     * L'administrateur a initié un remboursement complet.
     * La commande associée passe automatiquement à CANCELLED.
     * Le remboursement prend 5 à 10 jours ouvrables.
     */
    REMBOURSE
}
