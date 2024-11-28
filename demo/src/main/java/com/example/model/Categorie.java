package com.example.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Modèle représentant une catégorie avec ses propriétés.
 * Chaque catégorie contient un ID, un nom et une couleur hexadécimale.
 */
public class Categorie {
    // Propriétés pour l'ID, le nom et la couleur de la catégorie
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty couleur; // Propriété pour la couleur hexadécimale

    /**
     * Constructeur de la classe Categorie.
     *
     * @param id      Identifiant de la catégorie.
     * @param name    Nom de la catégorie.
     * @param couleur Couleur CSS hexadécimale associée à la catégorie.
     */
    public Categorie(int id, String name, String couleur) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.couleur = new SimpleStringProperty(couleur); // Initialisation de la couleur
    }

    // === Getters ===

    public int getId() {
        return id.get();
    }

    public String getName() {
        return name.get();
    }

    public String getCouleur() {
        return couleur.get();
    }

    // === Propriétés pour l'utilisation dans JavaFX ===

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty couleurProperty() {
        return couleur;
    }
}
