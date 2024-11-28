package com.example.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Modèle représentant une sous-catégorie.
 * Chaque sous-catégorie est associée à une catégorie parent et peut inclure une couleur pour l'affichage.
 */
public class SousCategorie {

    // === Attributs ===
    private int id; // Identifiant unique de la sous-catégorie
    private String nom; // Nom de la sous-catégorie
    private int idCategorie; // Identifiant de la catégorie parent
    private final StringProperty couleur; // Couleur CSS associée à la sous-catégorie

    /**
     * Constructeur de la classe SousCategorie.
     *
     * @param id          Identifiant de la sous-catégorie.
     * @param nom         Nom de la sous-catégorie.
     * @param idCategorie Identifiant de la catégorie parent.
     * @param couleur     Couleur CSS associée.
     */
    public SousCategorie(int id, String nom, int idCategorie, String couleur) {
        this.id = id;
        this.nom = nom;
        this.idCategorie = idCategorie;
        this.couleur = new SimpleStringProperty(couleur); // Initialisation de la couleur
    }

    // === Getters et Setters ===

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getIdCategorie() {
        return idCategorie;
    }

    public void setIdCategorie(int idCategorie) {
        this.idCategorie = idCategorie;
    }

    public String getCouleur() {
        return couleur.get();
    }

    public void setCouleur(String couleur) {
        this.couleur.set(couleur);
    }

    public StringProperty couleurProperty() {
        return couleur;
    }

    /**
     * Redéfinit la méthode toString pour retourner le nom de la sous-catégorie.
     * Cela permet d'afficher directement le nom dans les composants JavaFX comme les ListView.
     *
     * @return Nom de la sous-catégorie.
     */
    @Override
    public String toString() {
        return nom;
    }
}
