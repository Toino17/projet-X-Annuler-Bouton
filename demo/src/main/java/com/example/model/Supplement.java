package com.example.model;

/**
 * Modèle représentant un supplément.
 * Chaque supplément est défini par un identifiant unique, un nom, et un prix.
 */
public class Supplement {

    // === Attributs ===
    private Integer id;       // Identifiant unique du supplément
    private String nom;       // Nom du supplément
    private double prix;      // Prix du supplément

    /**
     * Constructeur de la classe Supplement.
     *
     * @param id   Identifiant unique du supplément.
     * @param nom  Nom du supplément.
     * @param prix Prix du supplément.
     */
    public Supplement(Integer id, String nom, double prix) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
    }

    // === Getters ===

    public Integer getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public double getPrix() {
        return prix;
    }

    // === Setters ===

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    // === Méthode toString ===

    /**
     * Redéfinit la méthode toString pour retourner une description lisible.
     *
     * @return Description du supplément (nom et prix).
     */
    @Override
    public String toString() {
        return nom + " - " + prix + "€";
    }
}
