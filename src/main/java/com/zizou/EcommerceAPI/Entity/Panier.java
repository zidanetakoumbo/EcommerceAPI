package com.zizou.EcommerceAPI.Entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entité représentant le panier d'achat d'un utilisateur.
 * Chaque utilisateur possède un seul panier actif.
 *
 * Note : on utilise un Builder manuel (pas @Builder Lombok) pour éviter le conflit
 * entre la classe interne Builder et la classe PanierBuilder générée par Lombok.
 */
@Entity
@Data
@NoArgsConstructor
public class Panier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Propriétaire du panier — relation one-to-one avec AppUser */
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private AppUser user;

    /** Liste des articles dans le panier */
    @OneToMany(mappedBy = "panier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PanierItem> items = new ArrayList<>();

    /** Calcule le prix total de tous les articles du panier */
    public double getTotalPrice() {
        return items.stream().mapToDouble(PanierItem::getPrixTotal).sum();
    }

    // ---- Getters ----

    public Long getId() {
        return id;
    }

    public AppUser getUser() {
        return user;
    }

    public List<PanierItem> getItems() {
        return items;
    }

    // ---- Setters ----

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public void setItems(List<PanierItem> items) {
        this.items = items;
    }

    // ---- Builder manuel ----

    /**
     * Point d'entrée du Builder. Utilisé comme : Panier.builder().user(user).build()
     * Retourne une nouvelle instance du Builder interne.
     */
    public static PanierBuilder builder() {
        return new PanierBuilder();
    }

    /**
     * Builder manuel pour construire un Panier champ par champ.
     * Nommé PanierBuilder pour éviter toute ambiguïté avec d'autres classes Builder.
     *
     * Exemple d'utilisation :
     *   Panier p = Panier.builder().user(user).build();
     */
    public static class PanierBuilder {

        private AppUser user;
        // items est toujours initialisé à une liste vide par défaut
        private List<PanierItem> items = new ArrayList<>();

        /** Définit le propriétaire du panier */
        public PanierBuilder user(AppUser user) {
            this.user = user;
            return this; // retourne "this" pour permettre le chaînage des appels
        }

        /** Définit la liste des articles (optionnel — vide par défaut) */
        public PanierBuilder items(List<PanierItem> items) {
            this.items = items;
            return this;
        }

        /**
         * Construit et retourne l'objet Panier final.
         * Utilise le constructeur no-args de JPA (@NoArgsConstructor) puis les setters.
         */
        public Panier build() {
            Panier panier = new Panier();
            panier.setUser(this.user);
            panier.setItems(this.items);
            return panier;
        }
    }
}
