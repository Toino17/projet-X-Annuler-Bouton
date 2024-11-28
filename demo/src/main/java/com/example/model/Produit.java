package com.example.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

/**
 * Modèle représentant un produit.
 * Ce modèle inclut un identifiant, un nom, une catégorie associée, et une couleur (par exemple, pour l'affichage).
 */
public class Produit {

    // === Attributs ===
    private final IntegerProperty id; // Identifiant unique du produit
    private final StringProperty name; // Nom du produit
    private final IntegerProperty categoryId; // Identifiant de la catégorie associée
    private final StringProperty couleur; // Couleur CSS associée au produit
    private final ObservableList<Supplement> listSupplements;
    

    public ObservableList<Supplement> getSupplements() {return listSupplements;}
    public void setSupplements(ObservableList<Supplement> listSupplements) {this.listSupplements.setAll(listSupplements);}
    public ObservableList<Supplement> supplementsProperty() {return listSupplements;}
    



    /**
     * Constructeur de la classe Produit.
     *
     * @param id         Identifiant du produit.
     * @param name       Nom du produit.
     * @param categoryId Identifiant de la catégorie associée.
     * @param couleur    Couleur CSS associée au produit.
     */
    public Produit(int id, String name, int categoryId, String couleur, ObservableList<Supplement> listSupplements) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.categoryId = new SimpleIntegerProperty(categoryId);
        this.couleur = new SimpleStringProperty(couleur);  // Initialisation de la couleur
        this.listSupplements = new SimpleListProperty<Supplement>(listSupplements);
    }

    // === Getters et Setters ===

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public int getCategoryId() {
        return categoryId.get();
    }

    public void setCategoryId(int categoryId) {
        this.categoryId.set(categoryId);
    }

    public IntegerProperty categoryIdProperty() {
        return categoryId;
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
}
