package com.zizou.EcommerceAPI.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entité représentant un article dans le panier.
 * Lie un Livre à un Panier avec une quantité.
 *
 * Note : Builder manuel utilisé à la place de @Builder Lombok
 * pour éviter le conflit avec @AllArgsConstructor.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PanierItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "panier_id")
    private Panier panier;

    @ManyToOne
    @JoinColumn(name = "livre_id")
    private Livre livre;

    private int quantite;

    /** Calcule le prix total de la ligne : prix unitaire * quantité */
    public double getPrixTotal() {
        return livre.getPrix() * quantite;
    }

    // ---- Getters ----

    public Long getId() {
        return id;
    }

    public Panier getPanier() {
        return panier;
    }

    public Livre getLivre() {
        return livre;
    }

    public int getQuantite() {
        return quantite;
    }

    // ---- Setters ----

    public void setId(Long id) {
        this.id = id;
    }

    public void setPanier(Panier panier) {
        this.panier = panier;
    }

    public void setLivre(Livre livre) {
        this.livre = livre;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    // ---- Builder manuel ----

    /**
     * Point d'entrée du Builder. Utilisé comme :
     *   PanierItem.builder().panier(p).livre(l).quantite(2).build()
     */
    public static PanierItemBuilder builder() {
        return new PanierItemBuilder();
    }

    /**
     * Builder manuel pour construire un PanierItem champ par champ.
     *
     * Exemple :
     *   PanierItem item = PanierItem.builder()
     *       .panier(panier)
     *       .livre(livre)
     *       .quantite(2)
     *       .build();
     */
    public static class PanierItemBuilder {

        private Panier panier;
        private Livre livre;
        private int quantite;

        /** Définit le panier auquel appartient cet article */
        public PanierItemBuilder panier(Panier panier) {
            this.panier = panier;
            return this;
        }

        /** Définit le livre correspondant à cet article */
        public PanierItemBuilder livre(Livre livre) {
            this.livre = livre;
            return this;
        }

        /** Définit la quantité commandée */
        public PanierItemBuilder quantite(int quantite) {
            this.quantite = quantite;
            return this;
        }

        /**
         * Construit et retourne le PanierItem final.
         * L'id est laissé à null — il sera généré automatiquement par JPA.
         */
        public PanierItem build() {
            PanierItem item = new PanierItem();
            item.setPanier(this.panier);
            item.setLivre(this.livre);
            item.setQuantite(this.quantite);
            return item;
        }
    }
}
