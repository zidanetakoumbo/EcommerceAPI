package com.zizou.EcommerceAPI.Service;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import com.zizou.EcommerceAPI.Dto.IntentionPaiementResponse;
import com.zizou.EcommerceAPI.Dto.PaiementDto;
import com.zizou.EcommerceAPI.Entity.Command;
import com.zizou.EcommerceAPI.Entity.CommandStatus;
import com.zizou.EcommerceAPI.Entity.Paiement;
import com.zizou.EcommerceAPI.Entity.StatutPaiement;
import com.zizou.EcommerceAPI.Exception.ResourceNotFoundException;
import com.zizou.EcommerceAPI.Repository.CommandRepository;
import com.zizou.EcommerceAPI.Repository.PaiementRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

/**
 * Service de paiement utilisant l'API Stripe.
 *
 * ============================================================
 *  COMMENT FONCTIONNE LE PAIEMENT STRIPE (vue d'ensemble)
 * ============================================================
 *
 * ÉTAPE 1 – Création de l'intention de paiement (backend)
 *   L'utilisateur clique "Payer" → notre backend appelle Stripe
 *   → Stripe crée un "PaymentIntent" et renvoie un "clientSecret"
 *   → On sauvegarde le paiement en base avec le statut EN_ATTENTE
 *   → On renvoie le clientSecret au frontend
 *
 * ÉTAPE 2 – Collecte de la carte (frontend, côté client)
 *   Le frontend utilise Stripe.js + le clientSecret pour afficher
 *   un formulaire de carte sécurisé. Les données bancaires ne
 *   transitent JAMAIS par notre serveur (conformité PCI DSS).
 *
 * ÉTAPE 3 – Notification automatique via webhook (backend)
 *   Stripe traite le paiement et envoie une requête HTTP POST
 *   vers /api/paiement/webhook pour nous dire si c'est OK ou KO.
 *   → Succès  : on met à jour le paiement (REUSSI) + la commande (CONFIRMED)
 *   → Échec   : on met à jour le paiement (ECHOUE)
 *
 * ============================================================
 */
@Service
public class PaiementService {

    /**
     * Clé secrète Stripe (sk_test_xxx en mode test, sk_live_xxx en production).
     * Lue depuis application.properties : stripe.secret.key
     * NE JAMAIS la partager ou la committer sur Git !
     */
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    /**
     * Clé de signature du webhook Stripe (whsec_xxx).
     * Lue depuis application.properties : stripe.webhook.secret
     * Permet de s'assurer que les webhooks reçus viennent bien de Stripe.
     */
    @Value("${stripe.webhook.secret}")
    private String stripeWebhookSecret;

    // Les repositories permettent d'accéder à la base de données
    private final PaiementRepository paiementRepository;
    private final CommandRepository commandRepository;

    /**
     * Injection des dépendances via le constructeur.
     * C'est la façon recommandée par Spring (évite les problèmes avec @Autowired).
     */
    public PaiementService(PaiementRepository paiementRepository,
                           CommandRepository commandRepository) {
        this.paiementRepository = paiementRepository;
        this.commandRepository = commandRepository;
    }

    /**
     * Initialise la clé API Stripe au démarrage de l'application.
     * @PostConstruct = exécuté automatiquement après que Spring a injecté
     * les dépendances (@Value, constructeur, etc.)
     */
    @PostConstruct
    public void initialiserStripe() {
        Stripe.apiKey = stripeSecretKey;
    }

    // ================================================================
    //  MÉTHODE PRINCIPALE : Créer une intention de paiement
    // ================================================================

    /**
     * Crée une intention de paiement Stripe pour une commande donnée.
     *
     * Appelée quand l'utilisateur veut payer sa commande.
     * Retourne un "clientSecret" que le frontend utilisera avec Stripe.js
     * pour afficher le formulaire de paiement.
     *
     * @param commandeId L'identifiant de la commande à payer
     * @param userId     L'identifiant de l'utilisateur connecté (extrait du JWT)
     * @return Un objet contenant le clientSecret et les détails du paiement
     * @throws ResourceNotFoundException  Si la commande n'existe pas
     * @throws IllegalArgumentException   Si la commande n'appartient pas à l'utilisateur
     * @throws IllegalStateException      Si la commande n'est pas en état PENDING
     *                                    ou si elle a déjà été payée avec succès
     * @throws RuntimeException           Si une erreur survient côté Stripe
     */
    @Transactional
    public IntentionPaiementResponse creerIntentionPaiement(Long commandeId, String userId) {

        // ── 1. Vérifier que la commande existe ──────────────────────────────
        Command commande = commandRepository.findById(commandeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Commande introuvable avec l'id : " + commandeId));

        // ── 2. Vérifier que la commande appartient à l'utilisateur connecté ─
        // Sécurité importante : un utilisateur ne doit pas pouvoir payer
        // la commande d'un autre utilisateur !
        if (!commande.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Cette commande ne vous appartient pas");
        }

        // ── 3. Vérifier que la commande est bien en attente ─────────────────
        // On ne peut payer qu'une commande PENDING (pas annulée, pas déjà livrée)
        if (commande.getStatus() != CommandStatus.PENDING) {
            throw new IllegalStateException(
                    "Seules les commandes en attente (PENDING) peuvent être payées. " +
                    "Statut actuel : " + commande.getStatus());
        }

        // ── 4. Vérifier qu'un paiement réussi n'existe pas déjà ─────────────
        Optional<Paiement> paiementExistant = paiementRepository.findByCommandeId(commandeId);
        if (paiementExistant.isPresent() && paiementExistant.get().getStatut() == StatutPaiement.REUSSI) {
            throw new IllegalStateException("Cette commande a déjà été payée avec succès");
        }

        // ── 5. Créer l'intention de paiement chez Stripe ────────────────────
        try {
            /*
             * Stripe utilise les CENTIMES (pas les euros).
             * Exemple : 29.99€ → on envoie 2999 à Stripe
             * Math.round() évite les erreurs d'arrondi des doubles
             * (ex: 29.99 * 100 = 2998.9999... → Math.round → 2999)
             */
            long montantEnCentimes = Math.round(commande.getTotalPrice() * 100);

            /*
             * Construction des paramètres de l'intention de paiement.
             * Les métadonnées permettent de retrouver la commande
             * directement depuis le dashboard Stripe.
             */
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(montantEnCentimes)
                    .setCurrency("eur")
                    .setDescription("Commande #" + commandeId + " - Librairie en ligne")
                    .putMetadata("commande_id", String.valueOf(commandeId))
                    .putMetadata("user_id", userId)
                    // Permet de collecter la méthode de paiement automatiquement
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .build();

            // Appel à l'API Stripe : crée le PaymentIntent sur leurs serveurs
            PaymentIntent paymentIntent = PaymentIntent.create(params);

            // ── 6. Sauvegarder le paiement dans notre base de données ────────
            Paiement paiement = new Paiement();
            paiement.setCommande(commande);
            paiement.setStripePaymentIntentId(paymentIntent.getId());
            paiement.setMontant(commande.getTotalPrice());
            paiement.setDevise("eur");
            paiement.setStatut(StatutPaiement.EN_ATTENTE);
            paiement.setDateCreation(new Date());
            // datePaiement reste null jusqu'à confirmation par webhook

            Paiement paiementSauvegarde = paiementRepository.save(paiement);

            // ── 7. Retourner le clientSecret au frontend ──────────────────────
            return new IntentionPaiementResponse(
            		
                    paymentIntent.getClientSecret(),   // Indispensable pour Stripe.js
                    paymentIntent.getId(),             // Pour référence
                    paiementSauvegarde.getId(),        // Notre ID interne
                    commande.getTotalPrice()           // Montant à afficher
            );

        } catch (StripeException e) {
            // StripeException peut survenir en cas de : clé API invalide,
            // problème réseau avec Stripe, paramètres incorrects, etc.
            throw new RuntimeException(
                    "Erreur lors de la communication avec Stripe : " + e.getMessage());
        }
    }

    // ================================================================
    //  TRAITEMENT DES WEBHOOKS STRIPE
    // ================================================================

    /**
     * Traite les événements envoyés automatiquement par Stripe via webhook.
     *
     * Stripe envoie une requête HTTP POST à notre endpoint /api/paiement/webhook
     * à chaque événement important (paiement réussi, paiement échoué, etc.).
     *
     * === SÉCURITÉ : VÉRIFICATION DE SIGNATURE ===
     * On vérifie que la requête vient bien de Stripe en validant la signature
     * dans l'en-tête "Stripe-Signature". Cela empêche les attaquants d'envoyer
     * de fausses notifications de paiement.
     *
     * === ÉVÉNEMENTS GÉRÉS ===
     * - "payment_intent.succeeded"       → Paiement réussi
     * - "payment_intent.payment_failed"  → Paiement échoué
     *
     * @param payload         Le corps JSON brut de la requête HTTP de Stripe
     * @param signatureHeader La valeur de l'en-tête HTTP "Stripe-Signature"
     * @throws IllegalArgumentException Si la signature est invalide
     * @throws RuntimeException         Si l'événement ne peut pas être décodé
     */
    @Transactional
    public void traiterWebhook(String payload, String signatureHeader) {

        Event event;

        // ── 1. Vérifier la signature de Stripe ──────────────────────────────
        // Cette étape est CRITIQUE pour la sécurité.
        // Elle garantit que le message vient bien de Stripe et n'a pas été modifié.
        try {
            event = Webhook.constructEvent(payload, signatureHeader, stripeWebhookSecret);
        } catch (SignatureVerificationException e) {
            // Signature invalide → probable tentative de fraude ou mauvaise config
            throw new IllegalArgumentException(
                    "Signature du webhook Stripe invalide. " +
                    "Vérifiez la valeur de stripe.webhook.secret dans application.properties. " +
                    "Détail : " + e.getMessage());
        }

        // ── 2. Extraire l'objet du payload Stripe ──────────────────────────
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();

        if (!dataObjectDeserializer.getObject().isPresent()) {
            // Cela peut arriver si la bibliothèque Stripe est plus vieille
            // que la version de l'API utilisée par Stripe pour générer l'événement
            throw new RuntimeException(
                    "Impossible de désérialiser l'événement Stripe. " +
                    "Vérifiez que stripe-java est à jour.");
        }

        // ── 3. Traiter selon le type d'événement ────────────────────────────
        String typeEvenement = event.getType();

        switch (typeEvenement) {
            case "payment_intent.succeeded":
                // Le paiement a été accepté par la banque du client
                PaymentIntent piReussi = (PaymentIntent) dataObjectDeserializer.getObject().get();
                traiterPaiementReussi(piReussi);
                break;

            case "payment_intent.payment_failed":
                // La carte a été refusée ou le paiement a échoué
                PaymentIntent piEchoue = (PaymentIntent) dataObjectDeserializer.getObject().get();
                traiterPaiementEchoue(piEchoue);
                break;

            default:
                // On ignore les autres types d'événements (charge.succeeded, etc.)
                // Stripe en envoie beaucoup, on ne traite que ce dont on a besoin
                break;
        }
    }

    /**
     * Met à jour la base de données quand Stripe confirme un paiement réussi.
     *
     * Actions effectuées :
     * 1. Passe le statut du paiement à REUSSI
     * 2. Enregistre la date du paiement
     * 3. Passe le statut de la commande à CONFIRMED
     *
     * @param paymentIntent L'objet PaymentIntent reçu dans le webhook Stripe
     */
    private void traiterPaiementReussi(PaymentIntent paymentIntent) {

        // Retrouver notre paiement en base grâce à l'ID Stripe
        Paiement paiement = paiementRepository
                .findByStripePaymentIntentId(paymentIntent.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Paiement introuvable pour l'ID Stripe : " + paymentIntent.getId()));

        // Mettre à jour le paiement
        paiement.setStatut(StatutPaiement.REUSSI);
        paiement.setDatePaiement(new Date());
        paiementRepository.save(paiement);

        // Mettre à jour la commande : PENDING → CONFIRMED
        // La commande est maintenant confirmée et peut être préparée
        Command commande = paiement.getCommande();
        commande.setStatus(CommandStatus.CONFIRMED);
        commandRepository.save(commande);
    }

    /**
     * Met à jour la base de données quand un paiement échoue.
     *
     * Actions effectuées :
     * 1. Passe le statut du paiement à ECHOUE
     * 2. La commande reste en PENDING → l'utilisateur peut réessayer de payer
     *
     * @param paymentIntent L'objet PaymentIntent reçu dans le webhook Stripe
     */
    private void traiterPaiementEchoue(PaymentIntent paymentIntent) {

        Paiement paiement = paiementRepository
                .findByStripePaymentIntentId(paymentIntent.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Paiement introuvable pour l'ID Stripe : " + paymentIntent.getId()));

        // On marque le paiement comme échoué
        // La commande reste en PENDING : l'utilisateur peut recréer une intention de paiement
        paiement.setStatut(StatutPaiement.ECHOUE);
        paiementRepository.save(paiement);
    }

    // ================================================================
    //  REMBOURSEMENT (admin seulement)
    // ================================================================

    /**
     * Rembourse un paiement déjà effectué.
     *
     * Réservé aux administrateurs. Le remboursement est total (montant complet).
     *
     * Le processus :
     * 1. Vérifie que le paiement existe et qu'il a bien réussi
     * 2. Envoie une demande de remboursement à Stripe
     * 3. Met le paiement à REMBOURSE dans notre base
     * 4. Annule la commande associée
     *
     * Note : Le remboursement peut prendre 5 à 10 jours ouvrables selon
     * la banque du client.
     *
     * @param paiementId L'identifiant du paiement à rembourser (dans notre base)
     * @return Le paiement mis à jour avec le statut REMBOURSE
     * @throws ResourceNotFoundException Si le paiement n'existe pas
     * @throws IllegalStateException     Si le paiement n'est pas dans l'état REUSSI
     * @throws RuntimeException          Si une erreur survient côté Stripe
     */
    @Transactional
    public PaiementDto rembourserPaiement(Long paiementId) {

        // ── 1. Trouver le paiement ──────────────────────────────────────────
        Paiement paiement = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Paiement introuvable avec l'id : " + paiementId));

        // ── 2. Vérifier qu'il est dans l'état REUSSI ────────────────────────
        // On ne peut rembourser que ce qui a été payé !
        if (paiement.getStatut() != StatutPaiement.REUSSI) {
            throw new IllegalStateException(
                    "Seuls les paiements avec le statut REUSSI peuvent être remboursés. " +
                    "Statut actuel : " + paiement.getStatut());
        }

        // ── 3. Créer le remboursement chez Stripe ───────────────────────────
        try {
            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(paiement.getStripePaymentIntentId())
                    // Sans setAmount() → remboursement TOTAL
                    // Avec setAmount(x) → remboursement partiel de x centimes
                    .build();

            Refund.create(params);

        } catch (StripeException e) {
            throw new RuntimeException(
                    "Erreur lors du remboursement Stripe : " + e.getMessage());
        }

        // ── 4. Mettre à jour notre base de données ──────────────────────────
        paiement.setStatut(StatutPaiement.REMBOURSE);
        paiementRepository.save(paiement);

        // La commande est annulée suite au remboursement
        Command commande = paiement.getCommande();
        commande.setStatus(CommandStatus.CANCELLED);
        commandRepository.save(commande);

        return convertirEnDto(paiement);
    }

    // ================================================================
    //  CONSULTER LE STATUT D'UN PAIEMENT
    // ================================================================

    /**
     * Récupère les informations de paiement d'une commande.
     *
     * Utilisé par le frontend pour afficher si une commande a été payée
     * et le détail du paiement.
     *
     * @param commandeId L'identifiant de la commande
     * @return Le DTO avec les informations du paiement
     * @throws ResourceNotFoundException Si aucun paiement n'existe pour cette commande
     */
    public PaiementDto getStatutPaiement(Long commandeId) {

        Paiement paiement = paiementRepository.findByCommandeId(commandeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aucun paiement trouvé pour la commande #" + commandeId));

        return convertirEnDto(paiement);
    }

    // ================================================================
    //  MÉTHODE UTILITAIRE : Convertir l'entité en DTO
    // ================================================================

    /**
     * Convertit une entité Paiement en PaiementDto pour l'envoi au frontend.
     *
     * On n'envoie jamais l'entité JPA directement (risque de boucle infinie
     * lors de la sérialisation JSON à cause des relations bidirectionnelles).
     *
     * @param paiement L'entité à convertir
     * @return Le DTO correspondant, sans les relations JPA
     */
    public PaiementDto convertirEnDto(Paiement paiement) {
        PaiementDto dto = new PaiementDto();
        dto.setId(paiement.getId());
        dto.setCommandeId(paiement.getCommande().getId());
        dto.setStripePaymentIntentId(paiement.getStripePaymentIntentId());
        dto.setMontant(paiement.getMontant());
        dto.setDevise(paiement.getDevise());
        dto.setStatut(paiement.getStatut());
        dto.setDateCreation(paiement.getDateCreation());
        dto.setDatePaiement(paiement.getDatePaiement());
        return dto;
    }
}
