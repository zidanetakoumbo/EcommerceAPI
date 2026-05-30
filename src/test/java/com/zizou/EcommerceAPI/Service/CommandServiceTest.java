package com.zizou.EcommerceAPI.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.zizou.EcommerceAPI.Entity.AppUser;
import com.zizou.EcommerceAPI.Entity.Command;
import com.zizou.EcommerceAPI.Entity.CommandStatus;
import com.zizou.EcommerceAPI.Entity.Livre;
import com.zizou.EcommerceAPI.Entity.Panier;
import com.zizou.EcommerceAPI.Entity.PanierItem;
import com.zizou.EcommerceAPI.Repository.AppUserRepository;
import com.zizou.EcommerceAPI.Repository.CommandRepository;
import com.zizou.EcommerceAPI.Repository.LivreRepository;
import com.zizou.EcommerceAPI.Repository.PanierRepository;

/**
 * Tests unitaires du service CommandService.
 */
@ExtendWith(MockitoExtension.class)
class CommandServiceTest {

    @Mock private CommandRepository commandRepository;
    @Mock private PanierRepository panierRepository;
    @Mock private LivreRepository livreRepository;
    @Mock private AppUserRepository userRepository;

    @InjectMocks
    private CommandService commandService;

    private AppUser user;
    private Livre livre;
    private Panier panier;
    private PanierItem panierItem;

    @BeforeEach
    void setUp() {
        user = new AppUser();
        user.setId("user-123");

        livre = new Livre();
        livre.setTitre("Dune");
        livre.setPrix(20.0);
        livre.setQuantiteStock(10);
        livre.setQuantiteVendue(0);
        livre.setId(1L);

        panier = new Panier();
        panier.setUser(user);
        panier.setItems(new ArrayList<>());
        panier.setId(1L);

        panierItem = new PanierItem();
        panierItem.setLivre(livre);
        panierItem.setQuantite(2);
        panierItem.setPanier(panier);
        panier.getItems().add(panierItem);
    }

    // ---- Tests passerCommande ----

    @Test
    @DisplayName("passerCommande : doit créer une commande à partir du panier")
    void passerCommande_doitCreerUneCommande() {
        when(panierRepository.findByUserId("user-123")).thenReturn(Optional.of(panier));
        when(userRepository.findById("user-123")).thenReturn(Optional.of(user));
        when(livreRepository.save(any(Livre.class))).thenReturn(livre);
        when(commandRepository.save(any(Command.class))).thenAnswer(i -> i.getArgument(0));
        when(panierRepository.save(any(Panier.class))).thenReturn(panier);

        Command result = commandService.passerCommande("user-123", "12 rue de Paris");

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(CommandStatus.PENDING);
        assertThat(result.getAdresseLivraison()).isEqualTo("12 rue de Paris");
        // Prix total : 2 * 20.0 = 40.0
        assertThat(result.getTotalPrice()).isEqualTo(40.0);
        // Le stock doit avoir été décrémenté (10 - 2 = 8)
        assertThat(livre.getQuantiteStock()).isEqualTo(8);
        // Les ventes doivent avoir été incrémentées (0 + 2 = 2)
        assertThat(livre.getQuantiteVendue()).isEqualTo(2);
    }

    @Test
    @DisplayName("passerCommande : doit lancer une exception si le panier est vide")
    void passerCommande_doitLancerExceptionSiPanierVide() {
        panier.getItems().clear(); // Panier vide
        when(panierRepository.findByUserId("user-123")).thenReturn(Optional.of(panier));

        assertThatThrownBy(() -> commandService.passerCommande("user-123", "12 rue de Paris"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("vide");
    }

    @Test
    @DisplayName("passerCommande : doit lancer une exception si le stock est insuffisant")
    void passerCommande_doitLancerExceptionSiStockInsuffisant() {
        livre.setQuantiteStock(1); // Stock insuffisant pour la quantité demandée (2)
        when(panierRepository.findByUserId("user-123")).thenReturn(Optional.of(panier));
        when(userRepository.findById("user-123")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> commandService.passerCommande("user-123", "12 rue de Paris"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("insuffisant");
    }

    // ---- Tests getCommandeById ----

    @Test
    @DisplayName("getCommandeById : doit retourner la commande correspondante")
    void getCommandeById_doitRetournerLaCommande() {
        Command command = new Command();
        command.setId(1L);
        command.setStatus(CommandStatus.PENDING);

        when(commandRepository.findById(1L)).thenReturn(Optional.of(command));

        Command result = commandService.getCommandeById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(CommandStatus.PENDING);
    }

    @Test
    @DisplayName("getCommandeById : doit lancer une exception si la commande est introuvable")
    void getCommandeById_doitLancerExceptionSiIntrouvable() {
        when(commandRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commandService.getCommandeById(99L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("99");
    }

    // ---- Tests updateStatut ----

    @Test
    @DisplayName("updateStatut : doit passer la commande en SHIPPED et enregistrer la date d'expédition")
    void updateStatut_doitEnregistrerDateExpedition() {
        Command command = new Command();
        command.setId(1L);
        command.setStatus(CommandStatus.PENDING);
        command.setItems(new ArrayList<>());

        when(commandRepository.findById(1L)).thenReturn(Optional.of(command));
        when(commandRepository.save(any(Command.class))).thenAnswer(i -> i.getArgument(0));

        Command result = commandService.updateStatut(1L, CommandStatus.SHIPPED);

        assertThat(result.getStatus()).isEqualTo(CommandStatus.SHIPPED);
        assertThat(result.getDateExpedition()).isNotNull();
    }

    @Test
    @DisplayName("updateStatut : doit restaurer le stock lors d'une annulation")
    void updateStatut_doitRestaurer_stockLorsAnnulation() {
        // Commande avec 1 livre (quantite 2)
        com.zizou.EcommerceAPI.Entity.CommandItem commandItem = new com.zizou.EcommerceAPI.Entity.CommandItem();
        commandItem.setLivre(livre);
        commandItem.setQuantite(2);
        commandItem.setPrixUnitaire(20.0);

        Command command = new Command();
        command.setId(1L);
        command.setStatus(CommandStatus.PENDING);
        command.setItems(new ArrayList<>());
        command.getItems().add(commandItem);
        commandItem.setCommand(command);
        livre.setQuantiteStock(8); // Déjà décrémenté lors de la commande
        livre.setQuantiteVendue(2);

        when(commandRepository.findById(1L)).thenReturn(Optional.of(command));
        when(livreRepository.save(any(Livre.class))).thenReturn(livre);
        when(commandRepository.save(any(Command.class))).thenAnswer(i -> i.getArgument(0));

        commandService.updateStatut(1L, CommandStatus.CANCELLED);

        // Le stock doit être restauré (8 + 2 = 10)
        assertThat(livre.getQuantiteStock()).isEqualTo(10);
        // Les ventes doivent être décrémentées (2 - 2 = 0)
        assertThat(livre.getQuantiteVendue()).isEqualTo(0);
    }

    // ---- Tests annulerCommande ----

    @Test
    @DisplayName("annulerCommande : doit annuler une commande PENDING appartenant à l'utilisateur")
    void annulerCommande_doitAnnulerCommandePending() {
        Command command = new Command();
        command.setId(1L);
        command.setUser(user);
        command.setStatus(CommandStatus.PENDING);
        command.setItems(new ArrayList<>());

        when(commandRepository.findById(1L)).thenReturn(Optional.of(command));
        when(commandRepository.save(any(Command.class))).thenAnswer(i -> i.getArgument(0));

        Command result = commandService.annulerCommande(1L, "user-123");

        assertThat(result.getStatus()).isEqualTo(CommandStatus.CANCELLED);
    }

    @Test
    @DisplayName("annulerCommande : doit lancer une exception si la commande n'est pas PENDING")
    void annulerCommande_doitLancerExceptionSiPasEnAttente() {
        Command command = new Command();
        command.setId(1L);
        command.setUser(user);
        command.setStatus(CommandStatus.SHIPPED); // Déjà expédiée

        when(commandRepository.findById(1L)).thenReturn(Optional.of(command));

        assertThatThrownBy(() -> commandService.annulerCommande(1L, "user-123"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Annulation impossible");
    }

    @Test
    @DisplayName("annulerCommande : doit lancer une exception si l'utilisateur n'est pas le propriétaire")
    void annulerCommande_doitLancerExceptionSiMauvaisUtilisateur() {
        AppUser autreUser = new AppUser();
        autreUser.setId("autre-user");

        Command command = new Command();
        command.setId(1L);
        command.setUser(autreUser); // Appartient à un autre utilisateur
        command.setStatus(CommandStatus.PENDING);

        when(commandRepository.findById(1L)).thenReturn(Optional.of(command));

        assertThatThrownBy(() -> commandService.annulerCommande(1L, "user-123"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("autorisé");
    }
}
