package com.zizou.EcommerceAPI.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
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
import com.zizou.EcommerceAPI.Entity.Livre;
import com.zizou.EcommerceAPI.Entity.Panier;
import com.zizou.EcommerceAPI.Entity.PanierItem;
import com.zizou.EcommerceAPI.Repository.AppUserRepository;
import com.zizou.EcommerceAPI.Repository.LivreRepository;
import com.zizou.EcommerceAPI.Repository.PanierItemRepository;
import com.zizou.EcommerceAPI.Repository.PanierRepository;

/**
 * Tests unitaires du service PanierService.
 */
@ExtendWith(MockitoExtension.class)
class PanierServiceTest {

    @Mock private PanierRepository panierRepository;
    @Mock private PanierItemRepository panierItemRepository;
    @Mock private LivreRepository livreRepository;
    @Mock private AppUserRepository userRepository;

    @InjectMocks
    private PanierService panierService;

    private AppUser user;
    private Livre livre;
    private Panier panier;

    @BeforeEach
    void setUp() {
        user = new AppUser();
        user.setId("user-123");
        user.setEmail("test@test.com");

        livre = new Livre();
        livre.setTitre("Harry Potter");
        livre.setPrix(15.0);
        livre.setQuantiteStock(5);
        livre.setId(1L);

        panier = new Panier();
        panier.setUser(user);
        panier.setItems(new ArrayList<>());
        panier.setId(1L);
    }

    // ---- Tests addToCart ----

    @Test
    @DisplayName("addToCart : doit créer un nouvel article si le livre n'est pas dans le panier")
    void addToCart_doitCreerNouvelArticle() {
        when(panierRepository.findByUserId("user-123")).thenReturn(Optional.of(panier));
        when(livreRepository.findById(1L)).thenReturn(Optional.of(livre));
        PanierItem savedItem = new PanierItem();
        savedItem.setLivre(livre);
        savedItem.setQuantite(2);
        savedItem.setPanier(panier);
        when(panierItemRepository.save(any(PanierItem.class))).thenReturn(savedItem);

        PanierItem result = panierService.addToCart("user-123", 1L, 2);

        assertThat(result.getQuantite()).isEqualTo(2);
        assertThat(result.getLivre().getTitre()).isEqualTo("Harry Potter");
        verify(panierItemRepository).save(any(PanierItem.class));
    }

    @Test
    @DisplayName("addToCart : doit cumuler la quantité si le livre est déjà dans le panier")
    void addToCart_doitCumulerQuantiteSiDejaPresent() {
        // Livre déjà dans le panier avec quantité 1
        PanierItem existingItem = new PanierItem();
        existingItem.setLivre(livre);
        existingItem.setQuantite(1);
        existingItem.setPanier(panier);
        panier.getItems().add(existingItem);

        when(panierRepository.findByUserId("user-123")).thenReturn(Optional.of(panier));
        when(livreRepository.findById(1L)).thenReturn(Optional.of(livre));
        when(panierItemRepository.save(any(PanierItem.class))).thenAnswer(i -> i.getArgument(0));

        PanierItem result = panierService.addToCart("user-123", 1L, 2);

        // Quantité doit être 1 (existant) + 2 (ajouté) = 3
        assertThat(result.getQuantite()).isEqualTo(3);
    }

    @Test
    @DisplayName("addToCart : doit lancer une exception si le stock est insuffisant")
    void addToCart_doitLancerExceptionSiStockInsuffisant() {
        livre.setQuantiteStock(1); // Stock de 1

        when(panierRepository.findByUserId("user-123")).thenReturn(Optional.of(panier));
        when(livreRepository.findById(1L)).thenReturn(Optional.of(livre));

        // On essaie d'ajouter 5 exemplaires alors que le stock est de 1
        assertThatThrownBy(() -> panierService.addToCart("user-123", 1L, 5))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Stock insuffisant");
    }

    @Test
    @DisplayName("addToCart : doit créer un nouveau panier si l'utilisateur n'en a pas")
    void addToCart_doitCreerPanierSiAbsent() {
        when(panierRepository.findByUserId("user-123")).thenReturn(Optional.empty());
        when(userRepository.findById("user-123")).thenReturn(Optional.of(user));
        when(panierRepository.save(any(Panier.class))).thenReturn(panier);
        when(livreRepository.findById(1L)).thenReturn(Optional.of(livre));
        when(panierItemRepository.save(any(PanierItem.class))).thenAnswer(i -> i.getArgument(0));

        PanierItem result = panierService.addToCart("user-123", 1L, 1);

        assertThat(result).isNotNull();
        verify(panierRepository).save(any(Panier.class)); // Le panier a été créé
    }

    // ---- Tests removeFromCart ----

    @Test
    @DisplayName("removeFromCart : doit supprimer l'article du panier")
    void removeFromCart_doitSupprimerArticle() {
        when(panierItemRepository.existsById(10L)).thenReturn(true);

        panierService.removeFromCart(10L);

        verify(panierItemRepository).deleteById(10L);
    }

    @Test
    @DisplayName("removeFromCart : doit lancer une exception si l'article est introuvable")
    void removeFromCart_doitLancerExceptionSiIntrouvable() {
        when(panierItemRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> panierService.removeFromCart(99L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("99");
    }

    // ---- Tests clearCart ----

    @Test
    @DisplayName("clearCart : doit vider tous les articles du panier")
    void clearCart_doitViderLePanier() {
        PanierItem item = new PanierItem();
        item.setLivre(livre);
        item.setQuantite(2);
        item.setPanier(panier);
        panier.getItems().add(item);

        when(panierRepository.findByUserId("user-123")).thenReturn(Optional.of(panier));
        when(panierRepository.save(any(Panier.class))).thenReturn(panier);

        panierService.clearCart("user-123");

        assertThat(panier.getItems()).isEmpty();
        verify(panierRepository).save(panier);
    }

    // ---- Tests getCart ----

    @Test
    @DisplayName("getCart : doit retourner le panier existant")
    void getCart_doitRetournerPanierExistant() {
        when(panierRepository.findByUserId("user-123")).thenReturn(Optional.of(panier));

        Panier result = panierService.getCart("user-123");

        assertThat(result).isNotNull();
        assertThat(result.getUser().getId()).isEqualTo("user-123");
    }

    @Test
    @DisplayName("getCart : doit créer un panier vide si l'utilisateur n'en a pas")
    void getCart_doitCreerPanierSiAbsent() {
        when(panierRepository.findByUserId("user-123")).thenReturn(Optional.empty());
        when(userRepository.findById("user-123")).thenReturn(Optional.of(user));
        when(panierRepository.save(any(Panier.class))).thenReturn(panier);

        Panier result = panierService.getCart("user-123");

        assertThat(result).isNotNull();
        verify(panierRepository).save(any(Panier.class));
    }
}
