package com.zizou.EcommerceAPI.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.zizou.EcommerceAPI.Dto.CommandDto;
import com.zizou.EcommerceAPI.Dto.CommandItemDto;
import com.zizou.EcommerceAPI.Entity.AppUser;
import com.zizou.EcommerceAPI.Entity.Command;
import com.zizou.EcommerceAPI.Entity.CommandItem;
import com.zizou.EcommerceAPI.Entity.CommandStatus;
import com.zizou.EcommerceAPI.Entity.Livre;
import com.zizou.EcommerceAPI.Entity.Panier;
import com.zizou.EcommerceAPI.Entity.PanierItem;
import com.zizou.EcommerceAPI.Repository.AppUserRepository;
import com.zizou.EcommerceAPI.Repository.CommandRepository;
import com.zizou.EcommerceAPI.Repository.LivreRepository;
import com.zizou.EcommerceAPI.Repository.PanierRepository;

import jakarta.transaction.Transactional;

/**
 * Service de gestion des commandes. Gère la création d'une commande depuis le
 * panier, le suivi du statut et l'annulation.
 */
@Service
@Transactional
public class CommandService {

	private final CommandRepository commandRepository;
	private final PanierRepository panierRepository;
	private final LivreRepository livreRepository;
	private final AppUserRepository userRepository;

	public CommandService(CommandRepository commandRepository, PanierRepository panierRepository,
			LivreRepository livreRepository, AppUserRepository userRepository) {
		this.commandRepository = commandRepository;
		this.panierRepository = panierRepository;
		this.livreRepository = livreRepository;
		this.userRepository = userRepository;
	}

	/**
	 * Crée une commande à partir du panier actif de l'utilisateur. - Vérifie que le
	 * panier n'est pas vide - Vérifie le stock de chaque livre - Décrémente le
	 * stock et incrémente les ventes - Vide le panier après validation
	 */
	@Transactional
	public Command passerCommande(String userId, String adresseLivraison) {
		Panier panier = panierRepository.findByUserId(userId)
				.orElseThrow(() -> new RuntimeException("Panier non trouvé pour l'utilisateur : " + userId));

		if (panier.getItems().isEmpty()) {
			throw new RuntimeException("Impossible de commander : le panier est vide");
		}

		AppUser user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'id : " + userId));

		// Initialisation de la commande
		Command command = new Command();
		command.setUser(user);
		command.setDateCommande(new Date());
		command.setStatus(CommandStatus.PENDING);
		command.setAdresseLivraison(adresseLivraison);

		// Conversion des articles du panier en lignes de commande
		double total = 0;
		for (PanierItem panierItem : panier.getItems()) {
			Livre livre = panierItem.getLivre();

			if (livre.getQuantiteStock() < panierItem.getQuantite()) {
				throw new RuntimeException("Stock insuffisant pour le livre : " + livre.getTitre() + " (disponible : "
						+ livre.getQuantiteStock() + ")");
			}

			// Création d'un article de commande à partir de l'article du panier
			CommandItem commandItem = new CommandItem();
			commandItem.setCommand(command);
			commandItem.setLivre(livre);
			commandItem.setQuantite(panierItem.getQuantite());
			commandItem.setPrixUnitaire(livre.getPrix());

			command.getItems().add(commandItem);

			// Mise à jour du stock et des ventes
			livre.setQuantiteStock(livre.getQuantiteStock() - panierItem.getQuantite());
			livre.setQuantiteVendue(livre.getQuantiteVendue() + panierItem.getQuantite());
			livreRepository.save(livre);

			total += commandItem.getPrixTotal();
		}

		command.setTotalPrice(total);
		Command savedCommand = commandRepository.save(command);

		// Vidage du panier après validation de la commande
		panier.getItems().clear();
		panierRepository.save(panier);

		return savedCommand;
	}

	/**
	 * Retourne les commandes d'un utilisateur avec pagination, triées par date de
	 * commande décroissante.
	 */
	public Page<Command> getMesCommandes(String userId, Pageable pageable) {
		return commandRepository.findByUserId(userId, pageable);
	}

	/**
	 * Retourne le détail d'une commande par son identifiant. Lance une exception si
	 * la commande est introuvable.
	 */
	public Command getCommandeById(Long id) {
		return commandRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Commande introuvable avec l'id : " + id));
	}

	/**
	 * Met à jour le statut d'une commande (usage admin). - SHIPPED : enregistre la
	 * date d'expédition - DELIVERED : enregistre la date de livraison - CANCELLED :
	 * restaure le stock des livres
	 */
	@Transactional
	public Command updateStatut(Long commandId, CommandStatus newStatus) {
		Command command = getCommandeById(commandId);
		CommandStatus currentStatus = command.getStatus();

		// Enregistrement automatique des dates selon le statut
		if (newStatus == CommandStatus.SHIPPED && command.getDateExpedition() == null) {
			command.setDateExpedition(new Date());
		}
		if (newStatus == CommandStatus.DELIVERED && command.getDateLivraison() == null) {
			command.setDateLivraison(new Date());
		}

		// Restauration du stock en cas d'annulation
		if (newStatus == CommandStatus.CANCELLED && currentStatus != CommandStatus.CANCELLED) {
			for (CommandItem item : command.getItems()) {
				Livre livre = item.getLivre();
				livre.setQuantiteStock(livre.getQuantiteStock() + item.getQuantite());
				livre.setQuantiteVendue(Math.max(0, livre.getQuantiteVendue() - item.getQuantite()));
				livreRepository.save(livre);
			}
		}

		command.setStatus(newStatus);
		return commandRepository.save(command);
	}

	/**
	 * Permet à un utilisateur d'annuler sa propre commande. L'annulation n'est
	 * possible que si la commande est en statut PENDING.
	 */
	@Transactional
	public Command annulerCommande(Long commandId, String userId) {
		Command command = getCommandeById(commandId);

		// Vérification que la commande appartient bien à l'utilisateur
		if (!command.getUser().getId().equals(userId)) {
			throw new RuntimeException("Vous n'êtes pas autorisé à annuler cette commande");
		}

		if (command.getStatus() != CommandStatus.PENDING) {
			throw new RuntimeException("Annulation impossible : la commande est déjà en statut " + command.getStatus());
		}

		return updateStatut(commandId, CommandStatus.CANCELLED);
	}

	/**
	 * Retourne toutes les commandes d'un statut donné - version simple (usage interne).
	 */
	public List<Command> getCommandesByStatus(CommandStatus status) {
		return commandRepository.findByStatus(status);
	}

	/**
	 * Retourne les commandes d'un statut donné avec pagination (usage admin).
	 * Exemple : getCommandesByStatus(PENDING, pageable) → liste des commandes en attente
	 */
	public Page<Command> getCommandesByStatus(CommandStatus status, Pageable pageable) {
		return commandRepository.findByStatus(status, pageable);
	}

	/**
	 * Retourne toutes les commandes qui ne sont pas encore livrées ni annulées.
	 * C'est-à-dire : PENDING, CONFIRMED, SHIPPED (avec pagination).
	 * Utilisé par l'admin pour voir les commandes à traiter.
	 */
	public Page<Command> getCommandesNonLivrees(Pageable pageable) {
		// Arrays.asList(...) crée une liste Java à partir de valeurs fixes
		// findByStatusNotIn : Spring Data génère WHERE status NOT IN ('DELIVERED', 'CANCELLED')
		return commandRepository.findByStatusNotIn(
			Arrays.asList(CommandStatus.DELIVERED, CommandStatus.CANCELLED),
			pageable
		);
	}

	// ---- Mapping Entity → DTO ----

	/**
	 * Convertit un CommandItem en CommandItemDto. Dénormalise le titre du livre
	 * pour faciliter l'affichage côté front.
	 */
	public CommandItemDto mapItemToDto(CommandItem item) {
		// 1. Instanciation normale de l'objet DTO
		CommandItemDto dto = new CommandItemDto();

		// 2. Attribution des valeurs de base de l'item
		dto.setId(item.getId());
		dto.setQuantite(item.getQuantite());
		dto.setPrixUnitaire(item.getPrixUnitaire());
		dto.setPrixTotal(item.getPrixTotal());

		// 3. Sécurité : Vérification que l'objet Livre existe bien dans l'item
		if (item.getLivre() != null) {
			dto.setLivreId(item.getLivre().getId());
			dto.setTitreLivre(item.getLivre().getTitre());
		}

		// 4. Retour du DTO configuré
		return dto;
	}

	/**
	 * Convertit une Command en CommandDto. Évite la sérialisation récursive Command
	 * → AppUser → List<Command>.
	 */
	public CommandDto mapToDto(Command command) {
		// 1. Instanciation normale de l'objet DTO
		CommandDto dto = new CommandDto();

		// 2. Attribution des valeurs simples
		dto.setId(command.getId());
		dto.setDateCommande(command.getDateCommande());
		dto.setDateExpedition(command.getDateExpedition());
		dto.setDateLivraison(command.getDateLivraison());
		dto.setStatus(command.getStatus());
		dto.setTotalPrice(command.getTotalPrice());
		dto.setAdresseLivraison(command.getAdresseLivraison());

		// 3. Sécurité pour éviter un NullPointerException si l'objet User est null
		if (command.getUser() != null) {
			dto.setUserId(command.getUser().getId());
			dto.setUserNom(command.getUser().getNom());
		}

		// 4. Transformation et assignation de la liste d'items
		if (command.getItems() != null) {
			List<CommandItemDto> itemDtos = command.getItems().stream().map(this::mapItemToDto)
					.collect(Collectors.toList());
			dto.setItems(itemDtos);
		}

		// 5. Retour du DTO configuré
		return dto;
	}
}
