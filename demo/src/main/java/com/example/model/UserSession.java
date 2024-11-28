package com.example.model;

/**
 * Classe singleton représentant une session utilisateur.
 * Gère les informations relatives à l'utilisateur connecté.
 */
public class UserSession {

    // === Attributs ===
    private static UserSession instance; // Instance unique pour le pattern Singleton

    private int idUser;                 // Identifiant de l'utilisateur
    private String identifiant;         // Identifiant (nom de connexion) de l'utilisateur
    private String nom;                 // Nom complet de l'utilisateur

    /**
     * Constructeur privé pour garantir le singleton.
     * Empêche l'instanciation directe de la classe.
     */
    private UserSession() {}

    /**
     * Retourne l'instance unique de la session utilisateur.
     *
     * @return L'instance unique de UserSession.
     */
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }
    
    // === Getters et Setters ===

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    // === Méthodes Utilitaires ===

    /**
     * Réinitialise la session utilisateur, par exemple lors de la déconnexion.
     * Efface les informations utilisateur pour garantir la sécurité.
     */
    public void clearSession() {
        idUser = 0;
        identifiant = null;
        nom = null;
    }
}
