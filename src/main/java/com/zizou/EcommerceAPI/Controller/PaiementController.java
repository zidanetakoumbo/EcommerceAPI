package com.zizou.EcommerceAPI.Controller;

import com.zizou.EcommerceAPI.Dto.IntentionPaiementResponse;
import com.zizou.EcommerceAPI.Dto.PaiementDto;
import com.zizou.EcommerceAPI.Entity.AppUser;
import com.zizou.EcommerceAPI.Service.PaiementService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur REST pour la gestion des paiements via Stripe.
 *
 * ============================================================
 *  ENDPOINTS DISPONIBLES
 * ============================================================
 *
 *  POST /api/paiement/creer-intention/{commandeId}
 *    → Crée une intention de paiement pour une commande
 *    → Requiert : JWT token (utilisateur connecté)
 *    → Retourne : { clientSecret, paymentIntentId, paiementId, montant }
 *
 *  POST /api/paiement/webhook
 *    → Reçoit les notifications automatiques de Stripe
 *    → Public (pas de JWT), mais sécurisé par signature Stripe
 *    → Ne jamais appeler manuellement, Stripe l'appelle tout seul
 *
 *  GET /api/paiement/statut/{commandeId}
 *    → Consulte le statut de paiement d'une commande
 *    → Requiert : JWT token (utilisateur connecté)
 *
 *  POST /api/paiement/rembourser/{paiementId}
 *    → Rembourse un paiement (ADMIN seulement)
 *    → Requiert : JWT token avec rôle ADMIN
 */
@RestController
@RequestMapping("/api/paiement")
@CrossOrigin(origins = "http://localhost:4200")
public class PaiementController {

    private final PaiementService paiementService;

    /**
     * Injection du service de paiement via le constructeur.
     */
    public PaiementController(PaiementService paiementService) {
        this.paiementService = paiementService;
    }

    // ================================================================
    //  CRÉER UNE INTENTION DE PAIEMENT
    // ================================================================

    /**
     * Crée une intention de paiement Stripe pour une commande.
     *
     * === FLUX D'UTILISATION ===
     *
     * 1. Frontend : POST /api/paiement/creer-intention/42
     *    (avec le JWT dans l'en-tête Authorization)
     *
     * 2. Backend : Crée le PaymentIntent chez Stripe, sauvegarde en base
     *    Retourne → { "clientSecret": "pi_xxx_secret_yyy", "montant": 29.99, ... }
     *
     * 3. Frontend : Utilise Stripe.js pour collecter la carte et payer :
     *    stripe.confirmCardPayment(clientSecret, { payment_method: { card: element } })
     *
     * 4. Stripe : Traite le paiement et notifie le backend via webhook
     *
     * === ERREURS POSSIBLES ===
     * - 404 : La commande n'existe pas
     * - 400 : La commande n'appartient pas à l'utilisateur, ou n'est pas PENDING
     * - 409 : La commande a déjà été payée avec succès
     * - 500 : Erreur de communication avec Stripe
     *
     * @param commandeId  L'ID de la commande à payer (dans l'URL)
     * @param userDetails L'utilisateur connecté (injecté automatiquement par Spring Security)
     * @return Le clientSecret et les infos du paiement (HTTP 200)
     */
    @PostMapping("/creer-intention/{commandeId}")
    public ResponseEntity<IntentionPaiementResponse> creerIntention(
            @PathVariable Long commandeId,
            @AuthenticationPrincipal UserDetails userDetails) {

        // Cast de UserDetails vers AppUser pour accéder à l'ID de l'utilisateur
        // AppUser implémente UserDetails, ce cast est donc toujours sûr
        String userId = ((AppUser) userDetails).getId();

        IntentionPaiementResponse response = paiementService.creerIntentionPaiement(commandeId, userId);

        return ResponseEntity.ok(response);
    }

    // ================================================================
    //  WEBHOOK STRIPE
    // ================================================================

    /**
     * Endpoint webhook appelé AUTOMATIQUEMENT par Stripe.
     *
     * === NE PAS APPELER MANUELLEMENT ===
     * Cet endpoint est appelé par les serveurs Stripe, pas par le frontend.
     * Il reçoit les notifications d'événements de paiement.
     *
     * === SÉCURITÉ ===
     * Bien que cet endpoint soit public (sans JWT), il est sécurisé par
     * la signature Stripe dans l'en-tête "Stripe-Signature".
     * Le service vérifie cette signature avant de traiter l'événement.
     *
     * === CONFIGURATION STRIPE REQUISE ===
     * Dans le dashboard Stripe (dashboard.stripe.com) :
     * 1. Aller dans Développeurs > Webhooks
     * 2. Ajouter un endpoint : https://votre-domaine.com/api/paiement/webhook
     * 3. Sélectionner les événements : payment_intent.succeeded,
     *    payment_intent.payment_failed
     * 4. Copier le "Signing secret" dans application.properties (stripe.webhook.secret)
     *
     * === TEST EN LOCAL ===
     * Utiliser Stripe CLI : stripe listen --forward-to localhost:8080/api/paiement/webhook
     *
     * @param request         La requête HTTP complète (pour lire le corps brut)
     * @param stripeSignature L'en-tête "Stripe-Signature" envoyé par Stripe
     * @return HTTP 200 si traité avec succès, HTTP 400 si signature invalide
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(
            HttpServletRequest request,
            @RequestHeader("Stripe-Signature") String stripeSignature) {

        try {
            /*
             * IMPORTANT : On lit le corps de la requête comme des bytes bruts.
             *
             * La vérification de signature Stripe nécessite le corps EXACT de la requête.
             * Si Spring parsait/reformatait le JSON, la signature ne correspondrait plus.
             * C'est pourquoi on lit directement depuis l'InputStream de la requête HTTP.
             */
            byte[] corpsBrut = request.getInputStream().readAllBytes();
            String payload = new String(corpsBrut);

            paiementService.traiterWebhook(payload, stripeSignature);

            // Stripe attend un HTTP 200 pour savoir que le webhook a bien été traité
            return ResponseEntity.ok("Webhook traité avec succès");

        } catch (IllegalArgumentException e) {
            // Signature invalide → Stripe retentera l'envoi plusieurs fois
            // avant d'alerter dans le dashboard
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Signature invalide : " + e.getMessage());

        } catch (Exception e) {
            // Erreur inattendue → Stripe retentera l'envoi
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors du traitement du webhook : " + e.getMessage());
        }
    }

    // ================================================================
    //  CONSULTER LE STATUT D'UN PAIEMENT
    // ================================================================

    /**
     * Retourne les informations de paiement pour une commande donnée.
     *
     * Utile pour afficher à l'utilisateur si sa commande a été payée,
     * le montant, et la date du paiement.
     *
     * === EXEMPLE DE RÉPONSE ===
     * {
     *   "id": 1,
     *   "commandeId": 42,
     *   "stripePaymentIntentId": "pi_3OxxxxxxxxxxxxxxxxxXXXX",
     *   "montant": 29.99,
     *   "devise": "eur",
     *   "statut": "REUSSI",
     *   "dateCreation": "2025-06-02T10:30:00.000+00:00",
     *   "datePaiement": "2025-06-02T10:31:15.000+00:00"
     * }
     *
     * @param commandeId  L'ID de la commande dont on veut voir le paiement
     * @param userDetails L'utilisateur connecté (pour vérification future)
     * @return Les infos du paiement (HTTP 200) ou 404 si aucun paiement
     */
    @GetMapping("/statut/{commandeId}")
    public ResponseEntity<PaiementDto> getStatut(
            @PathVariable Long commandeId,
            @AuthenticationPrincipal UserDetails userDetails) {

        PaiementDto dto = paiementService.getStatutPaiement(commandeId);
        return ResponseEntity.ok(dto);
    }

    // ================================================================
    //  REMBOURSEMENT (ADMIN)
    // ================================================================

    /**
     * Rembourse un paiement (accessible aux administrateurs uniquement).
     *
     * Effectue un remboursement total du montant payé.
     * La commande associée est automatiquement annulée.
     *
     * Le client verra le remboursement apparaître sur son relevé bancaire
     * dans un délai de 5 à 10 jours ouvrables (selon sa banque).
     *
     * === PRÉCONDITIONS ===
     * - Le paiement doit avoir le statut REUSSI
     * - L'utilisateur appelant doit avoir le rôle ADMIN
     *
     * @param paiementId L'ID du paiement à rembourser (dans notre base)
     * @return Le paiement mis à jour avec statut REMBOURSE (HTTP 200)
     */
    @PostMapping("/rembourser/{paiementId}")
    public ResponseEntity<PaiementDto> rembourser(@PathVariable Long paiementId) {
        PaiementDto dto = paiementService.rembourserPaiement(paiementId);
        return ResponseEntity.ok(dto);
    }
}
