package com.zizou.EcommerceAPI.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.zizou.EcommerceAPI.Dto.PanierDto;
import com.zizou.EcommerceAPI.Dto.PanierItemDto;
import com.zizou.EcommerceAPI.Entity.AppUser;
import com.zizou.EcommerceAPI.Entity.Livre;
import com.zizou.EcommerceAPI.Entity.Panier;
import com.zizou.EcommerceAPI.Entity.PanierItem;
import com.zizou.EcommerceAPI.Repository.AppUserRepository;
import com.zizou.EcommerceAPI.Repository.LivreRepository;
import com.zizou.EcommerceAPI.Repository.PanierItemRepository;
import com.zizou.EcommerceAPI.Repository.PanierRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Service de gestion du panier d'achat. Permet d'ajouter, supprimer et
 * consulter les articles du panier.
 */
@Service
@Transactional
public class PanierService {

	private final PanierRepository panierRepos;
	private final PanierItemRepository panierItemRepos;
	private final LivreRepository livreRepos;
	private final AppUserRepository 
	userRepos;

	public PanierService(PanierRepository panierRepos, PanierItemRepository panierItemRepos, LivreRepository livreRepos,
			AppUserRepository userRepos) {
		super();
		this.panierRepos = panierRepos;
		this.panierItemRepos = panierItemRepos;
		this.livreRepos = livreRepos;
		this.userRepos = userRepos;
	}

	/**
	 * Ajoute un livre au panier de l'utilisateur. Si le livre est déjà dans le
	 * panier, la quantité est augmentée. Crée un panier automatiquement si
	 * l'utilisateur n'en a pas encore.
	 */
	public PanierItem addToCart(String userId, Long livreId, int quantite) {
		// Récupérer ou créer le panier de l'utilisateur
		Panier panier = panierRepos.findByUserId(userId).orElseGet(() -> createPanierForUser(userId));

		Livre livre = livreRepos.findById(livreId)
				.orElseThrow(() -> new RuntimeException("Livre non trouvé avec l'id : " + livreId));

		// Vérifier que le stock est suffisant
		if (livre.getQuantiteStock() < quantite) {
			throw new RuntimeException("Stock insuffisant pour le livre : " + livre.getTitre());
		}

		// Si le livre est déjà dans le panier, on incrémente la quantité
		Optional<PanierItem> existingItem = panier.getItems().stream()
				.filter(item -> item.getLivre().getId().equals(livreId)).findFirst();

		if (existingItem.isPresent()) {
			existingItem.get().setQuantite(existingItem.get().getQuantite() + quantite);
			return panierItemRepos.save(existingItem.get());
		}

		// Sinon, on crée un nouvel article dans le panier
		PanierItem newItem = PanierItem.builder().panier(panier).livre(livre).quantite(quantite).build();

		panier.getItems().add(newItem);
		return panierItemRepos.save(newItem);
	}

	/**
	 * Supprime un article du panier par son identifiant.
	 */
	public void removeFromCart(Long panierItemId) {
		if (!panierItemRepos.existsById(panierItemId)) {
			throw new RuntimeException("Article de panier introuvable avec l'id : " + panierItemId);
		}
		panierItemRepos.deleteById(panierItemId);
	}

	/**
	 * Vide complètement le panier de l'utilisateur.
	 */
	public void clearCart(String userId) {
		Panier panier = panierRepos.findByUserId(userId)
				.orElseThrow(() -> new RuntimeException("Panier non trouvé pour l'utilisateur : " + userId));
		panier.getItems().clear();
		panierRepos.save(panier);
	}

	/**
	 * Crée un nouveau panier vide pour un utilisateur.
	 */
	private Panier createPanierForUser(String userId) {
		AppUser user = userRepos.findById(userId)
				.orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id : " + userId));
		Panier panier = Panier.builder().user(user).build();
		return panierRepos.save(panier);
	}

	/**
	 * Récupère le panier d'un utilisateur. Crée un panier vide s'il n'en a pas
	 * encore.
	 */
	public Panier getCart(String userId) {
		return panierRepos.findByUserId(userId).orElseGet(() -> createPanierForUser(userId));
	}

	/**
	 * Met à jour la quantité d'un article du panier. Supprime l'article si la
	 * quantité est 0 ou négative.
	 */
	public PanierItem updateQuantite(Long panierItemId, int quantite) {
		if (quantite <= 0) {
			removeFromCart(panierItemId);
			return null;
		}
		PanierItem item = panierItemRepos.findById(panierItemId)
				.orElseThrow(() -> new RuntimeException("Article de panier introuvable avec l'id : " + panierItemId));
		if (item.getLivre().getQuantiteStock() < quantite) {
			throw new RuntimeException("Stock insuffisant pour le livre : " + item.getLivre().getTitre());
		}
		item.setQuantite(quantite);
		return panierItemRepos.save(item);
	}

	public PanierDto mapToDto(Panier panier) {
		// 1. On garde la transformation de la liste d'items (Stream)
		List<PanierItemDto> itemDtos = panier.getItems().stream().map(this::mapItemToDto).collect(Collectors.toList());

		// 2. Instanciation normale avec le constructeur par défaut
		PanierDto dto = new PanierDto();

		// 3. Attribution des valeurs via les setters classiques
		dto.setId(panier.getId());

		// Condition de sécurité au cas où l'utilisateur (User) serait null dans le
		// panier
		if (panier.getUser() != null) {
			dto.setUserId(panier.getUser().getId());
		}

		dto.setTotalPrice(panier.getTotalPrice());
		dto.setItems(itemDtos);

		// 4. Retour de l'objet instancié
		return dto;
	}

	public PanierItemDto mapItemToDto(PanierItem item) {
		// 1. Instanciation normale avec le constructeur par défaut
		PanierItemDto dto = new PanierItemDto();

		// 2. Attribution des valeurs via les setters classiques
		dto.setId(item.getId());
		dto.setLivreId(item.getLivre().getId());
		dto.setTitreLivre(item.getLivre().getTitre());
		dto.setOpenCouverture(item.getLivre().getOpenCouverture());
		dto.setPrixUnitaire(item.getLivre().getPrix());
		dto.setQuantite(item.getQuantite());
		dto.setPrixTotal(item.getPrixTotal());

		// 3. Retour de l'objet instancié
		return dto;
	}
}
