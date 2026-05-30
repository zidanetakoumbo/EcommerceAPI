package com.zizou.EcommerceAPI.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zizou.EcommerceAPI.Dto.PanierDto;
import com.zizou.EcommerceAPI.Dto.PanierItemDto;
import com.zizou.EcommerceAPI.Entity.PanierItem;
import com.zizou.EcommerceAPI.Service.AppUserService;
import com.zizou.EcommerceAPI.Service.PanierService;

/**
 * Contrôleur REST pour la gestion du panier d'achat.
 * Retourne des PanierDto pour éviter la sérialisation récursive des entités.
 * Tous les endpoints nécessitent un token JWT valide.
 */
@RestController
@RequestMapping("/api/panier")
@CrossOrigin(origins = "http://localhost:4200")
public class PanierController {

    private final PanierService panierService;
    private final AppUserService userService;

    /**
     * Injection des services via le constructeur.
     * Spring détecte automatiquement le constructeur unique et injecte les beans.
     */
    public PanierController(PanierService panierService, AppUserService userService) {
        this.panierService = panierService;
        this.userService = userService;
    }

    /** Extrait l'identifiant de l'utilisateur connecté à partir du token JWT */
    private String getUserId(UserDetails userDetails) {
        return userService.getUserByEmail(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"))
            .getId();
    }

    /**
     * GET /api/panier
     * Retourne le panier actif de l'utilisateur connecté sous forme de PanierDto.
     * Crée un panier vide automatiquement s'il n'en a pas encore.
     */
    @GetMapping
    public ResponseEntity<PanierDto> getMonPanier(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = getUserId(userDetails);
        return ResponseEntity.ok(panierService.mapToDto(panierService.getCart(userId)));
    }

    /**
     * POST /api/panier/ajouter?livreId=1&quantite=2
     * Ajoute un livre au panier. Si le livre est déjà présent, la quantité est cumulée.
     * Vérifie que le stock est suffisant avant d'ajouter.
     */
    @PostMapping("/ajouter")
    public ResponseEntity<PanierItemDto> ajouterAuPanier(
            @RequestParam Long livreId,
            @RequestParam(defaultValue = "1") int quantite,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userId = getUserId(userDetails);
        PanierItem item = panierService.addToCart(userId, livreId, quantite);
        return ResponseEntity.ok(panierService.mapItemToDto(item));
    }

    /**
     * PUT /api/panier/modifier/{panierItemId}?quantite=3
     * Met à jour la quantité d'un article existant dans le panier.
     * Si quantite = 0, l'article est supprimé et on retourne 204 No Content.
     * Vérifie que le stock est suffisant pour la nouvelle quantité.
     */
    @PutMapping("/modifier/{panierItemId}")
    public ResponseEntity<PanierItemDto> modifierQuantite(
            @PathVariable Long panierItemId,
            @RequestParam int quantite) {
        PanierItem updated = panierService.updateQuantite(panierItemId, quantite);

        // Si la quantité était 0 ou négative, l'article a été supprimé → 204
        if (updated == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(panierService.mapItemToDto(updated));
    }

    /**
     * DELETE /api/panier/supprimer/{panierItemId}
     * Supprime un article précis du panier par l'identifiant de l'article.
     */
    @DeleteMapping("/supprimer/{panierItemId}")
    public ResponseEntity<Void> supprimerDuPanier(@PathVariable Long panierItemId) {
        panierService.removeFromCart(panierItemId);
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /api/panier/vider
     * Supprime tous les articles du panier de l'utilisateur connecté.
     */
    @DeleteMapping("/vider")
    public ResponseEntity<Void> viderPanier(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = getUserId(userDetails);
        panierService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
