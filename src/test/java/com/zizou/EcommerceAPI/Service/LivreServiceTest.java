package com.zizou.EcommerceAPI.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.zizou.EcommerceAPI.Entity.Livre;
import com.zizou.EcommerceAPI.Repository.LivreRepository;

/**
 * Tests unitaires du service LivreService.
 * Les dépendances sont mockées avec Mockito.
 */
@ExtendWith(MockitoExtension.class)
class LivreServiceTest {

    @Mock
    private LivreRepository livreRepository;

    @InjectMocks
    private LivreService livreService;

    private Livre livre;

    @BeforeEach
    void setUp() {
        // Livre de test réutilisé dans tous les tests
        livre = new Livre();
        livre.setTitre("Le Petit Prince");
        livre.setResume("Un conte poétique");
        livre.setPrix(12.99);
        livre.setQuantiteStock(10);
        livre.setQuantiteVendue(0);
        livre.setId(1L);
    }

    // ---- Tests getAll ----

    @Test
    @DisplayName("getAll : doit retourner la liste complète des livres")
    void getAll_doitRetournerTousLesLivres() {
        // Préparation
        when(livreRepository.findAll()).thenReturn(List.of(livre));

        // Exécution
        List<Livre> result = livreService.getAll();

        // Vérification
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitre()).isEqualTo("Le Petit Prince");
        verify(livreRepository).findAll();
    }

    @Test
    @DisplayName("getAll : doit retourner une liste vide si aucun livre en base")
    void getAll_doitRetournerListeVide() {
        when(livreRepository.findAll()).thenReturn(List.of());

        List<Livre> result = livreService.getAll();

        assertThat(result).isEmpty();
    }

    // ---- Tests getById ----

    @Test
    @DisplayName("getById : doit retourner le livre correspondant à l'id")
    void getById_doitRetournerLeLivre() {
        when(livreRepository.findById(1L)).thenReturn(Optional.of(livre));

        Livre result = livreService.getById(1L);

        assertThat(result.getTitre()).isEqualTo("Le Petit Prince");
        assertThat(result.getPrix()).isEqualTo(12.99);
    }

    @Test
    @DisplayName("getById : doit lancer une exception si le livre est introuvable")
    void getById_doitLancerExceptionSiIntrouvable() {
        when(livreRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> livreService.getById(99L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("99");
    }

    // ---- Tests create ----

    @Test
    @DisplayName("create : doit sauvegarder et retourner le livre créé")
    void create_doitSauvegarderEtRetournerLeLivre() {
        when(livreRepository.save(any(Livre.class))).thenReturn(livre);

        Livre result = livreService.create(livre);

        assertThat(result).isNotNull();
        assertThat(result.getTitre()).isEqualTo("Le Petit Prince");
        verify(livreRepository).save(livre);
    }

    // ---- Tests update ----

    @Test
    @DisplayName("update : doit mettre à jour le livre existant et le retourner")
    void update_doitMettreAJourLeLivre() {
        Livre livreModifie = new Livre();
        livreModifie.setTitre("Le Petit Prince - Édition collector");
        livreModifie.setPrix(19.99);
        livreModifie.setQuantiteStock(5);

        when(livreRepository.findById(1L)).thenReturn(Optional.of(livre));
        when(livreRepository.save(any(Livre.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Livre result = livreService.update(1L, livreModifie);

        assertThat(result.getTitre()).isEqualTo("Le Petit Prince - Édition collector");
        assertThat(result.getPrix()).isEqualTo(19.99);
        // L'id original doit être préservé
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("update : doit lancer une exception si le livre est introuvable")
    void update_doitLancerExceptionSiLivreIntrouvable() {
        when(livreRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> livreService.update(99L, livre))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("99");
    }

    // ---- Tests delete ----

    @Test
    @DisplayName("delete : doit supprimer le livre et retourner true")
    void delete_doitSupprimerEtRetournerTrue() {
        when(livreRepository.existsById(1L)).thenReturn(true);

        boolean result = livreService.delete(1L);

        assertThat(result).isTrue();
        verify(livreRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete : doit retourner false si le livre n'existe pas")
    void delete_doitRetournerFalseSiIntrouvable() {
        when(livreRepository.existsById(99L)).thenReturn(false);

        boolean result = livreService.delete(99L);

        assertThat(result).isFalse();
        verify(livreRepository, never()).deleteById(any());
    }
}
