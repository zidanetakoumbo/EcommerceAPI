package com.zizou.EcommerceAPI.Controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zizou.EcommerceAPI.Dto.CommandDto;
import com.zizou.EcommerceAPI.Entity.CommandStatus;
import com.zizou.EcommerceAPI.Service.AppUserService;
import com.zizou.EcommerceAPI.Service.CommandService;

/**
 * Contrôleur REST pour la gestion des commandes.
 * Retourne des CommandDto pour éviter la sérialisation récursive des entités.
 *
 * Endpoints utilisateur : passer une commande, voir ses commandes, annuler.
 * Endpoints admin : changer le statut, filtrer par statut.
 */
@RestController
@RequestMapping("/api/commandes")
@CrossOrigin(origins = "http://localhost:4200")
public class CommandController {

    private final CommandService commandService;
    private final AppUserService userService;

    /**
     * Injection des services via le constructeur.
     * Spring détecte automatiquement le constructeur unique et injecte les beans.
     */
    public CommandController(CommandService commandService, AppUserService userService) {
        this.commandService = commandService;
        this.userService = userService;
    }

    /** Extrait l'identifiant de l'utilisateur connecté à partir du token JWT */
    private String getUserId(UserDetails userDetails) {
        return userService.getUserByEmail(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"))
            .getId();
    }

    /**
     * POST /api/commandes/passer?adresseLivraison=...
     * Valide le panier et crée une commande. Décrémente le stock automatiquement.
     * Retourne 201 Created avec le CommandDto de la commande créée.
     */
    @PostMapping("/passer")
    public ResponseEntity<CommandDto> passerCommande(
            @RequestParam String adresseLivraison,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userId = getUserId(userDetails);
        CommandDto dto = commandService.mapToDto(commandService.passerCommande(userId, adresseLivraison));
        return ResponseEntity.status(201).body(dto);
    }

    /**
     * GET /api/commandes/mes-commandes
     * Retourne les commandes de l'utilisateur connecté avec pagination (triées par date décroissante).
     */
    @GetMapping("/mes-commandes")
    public ResponseEntity<Page<CommandDto>> getMesCommandes(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10, sort = "dateCommande", direction = Sort.Direction.DESC) Pageable pageable) {
        String userId = getUserId(userDetails);
        Page<CommandDto> page = commandService.getMesCommandes(userId, pageable)
            .map(commandService::mapToDto);
        return ResponseEntity.ok(page);
    }

    /**
     * GET /api/commandes/{id}
     * Retourne le détail complet d'une commande par son identifiant.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CommandDto> getCommandeById(@PathVariable Long id) {
        return ResponseEntity.ok(commandService.mapToDto(commandService.getCommandeById(id)));
    }

    /**
     * PUT /api/commandes/{id}/annuler
     * Permet à l'utilisateur d'annuler sa commande si elle est encore en statut PENDING.
     */
    @PutMapping("/{id}/annuler")
    public ResponseEntity<CommandDto> annulerCommande(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userId = getUserId(userDetails);
        return ResponseEntity.ok(commandService.mapToDto(commandService.annulerCommande(id, userId)));
    }

    /**
     * PUT /api/commandes/{id}/statut?newStatus=SHIPPED
     * (Réservé admin) Met à jour le statut d'une commande.
     * Les dates d'expédition et livraison sont enregistrées automatiquement.
     *
     * NOTE : le paramètre s'appelle "newStatus" pour correspondre au frontend Angular.
     */
    @PutMapping("/{id}/statut")
    public ResponseEntity<CommandDto> updateStatut(
            @PathVariable Long id,
            @RequestParam(name = "newStatus") CommandStatus newStatus) {
        return ResponseEntity.ok(commandService.mapToDto(commandService.updateStatut(id, newStatus)));
    }

    /**
     * GET /api/commandes/statut/{statut}?page=0&size=20
     * (Réservé admin) Filtre les commandes par statut avec pagination.
     * Avant : retournait une List → maintenant : retourne une Page (pagination).
     */
    @GetMapping("/statut/{statut}")
    public ResponseEntity<Page<CommandDto>> getCommandesByStatut(
            @PathVariable CommandStatus statut,
            @PageableDefault(size = 20, sort = "dateCommande", direction = Sort.Direction.DESC) Pageable pageable) {
        // .map() transforme chaque Command en CommandDto dans la page
        Page<CommandDto> page = commandService.getCommandesByStatus(statut, pageable)
            .map(commandService::mapToDto);
        return ResponseEntity.ok(page);
    }

    /**
     * GET /api/commandes/non-livrees?page=0&size=20
     * (Réservé admin) Retourne toutes les commandes non encore livrées ni annulées
     * (statuts : PENDING, CONFIRMED, SHIPPED) — triées par date décroissante.
     */
    @GetMapping("/non-livrees")
    public ResponseEntity<Page<CommandDto>> getCommandesNonLivrees(
            @PageableDefault(size = 20, sort = "dateCommande", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<CommandDto> page = commandService.getCommandesNonLivrees(pageable)
            .map(commandService::mapToDto);
        return ResponseEntity.ok(page);
    }
}
