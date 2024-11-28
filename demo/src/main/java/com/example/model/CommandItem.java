package com.example.model;

/**
 * Modèle représentant un élément de commande.
 * Cet élément peut être un produit ou un supplément avec son prix associé.
 */
public class CommandItem {

    private Integer id;

    private Integer idAssocie;
    // Nom du produit ou supplément
    private String produitOuSupplement;

    // Prix associé au produit ou supplément
    private double prix;

    /**
     * Constructeur de la classe CommandItem.
     *
     * @param produitOuSupplement Nom du produit ou supplément.
     * @param prix                Prix du produit ou supplément.
     */
    public CommandItem(String produitOuSupplement, double prix, Integer id, Integer idAssocie) {
        this.id = id;
        this.idAssocie = idAssocie;
        this.produitOuSupplement = produitOuSupplement;
        this.prix = prix;
    }

    // === Getters et Setters ===

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getProduitOuSupplement() {
        return produitOuSupplement;
    }

    /**
     * Définit le nom du produit ou supplément.
     *
     * @param produitOuSupplement Nom du produit ou supplément.
     */
    public void setProduitOuSupplement(String produitOuSupplement) {
        this.produitOuSupplement = produitOuSupplement;
    }

    public double getPrix() {
        return prix;
    }

    /**
     * Définit le prix du produit ou supplément.
     *
     * @param prix Prix du produit ou supplément.
     */
    public void setPrix(double prix) {
        this.prix = prix;
    }

    // === Méthodes Utilitaires ===

    /**
     * Fournit une représentation textuelle de l'objet pour un affichage simplifié.
     *
     * @return Représentation en chaîne de l'objet.
     */
    @Override
    public String toString() {
        return produitOuSupplement + " - " + prix + "€";
    }

    public Integer getIdAssocie() {
        return idAssocie;
    }

    public void setIdAssocie(Integer idAssocie) {
        this.idAssocie = idAssocie;
    }
}
