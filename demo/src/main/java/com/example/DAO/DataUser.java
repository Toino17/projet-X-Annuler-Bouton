package com.example.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.model.UserSession;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DataUser {

    /**
     * Authentifie un utilisateur en fonction de son identifiant.
     *
     * @param identifiant L'identifiant de l'utilisateur.
     * @return true si l'utilisateur est authentifié avec succès, sinon false.
     * @throws SQLException En cas d'erreur SQL.
     */
    public static boolean authenticate(String identifiant) throws SQLException {
        String query = "SELECT id_user, nom FROM user WHERE identifiant = ?";
    
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
    
            stmt.setString(1, identifiant);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                // Mise à jour de la session utilisateur
                UserSession session = UserSession.getInstance();
                session.setIdUser(rs.getInt("id_user"));
                // session.setIdentifiant(identifiant);
                session.setNom(rs.getString("nom"));
                return true; // Authentification réussie
            }
        }
        return false; // Identifiant invalide
    }

    /**
     * Associe une table spécifique à un utilisateur, si elle est disponible.
     *
     * @param tableId L'identifiant de la table.
     * @param userId  L'identifiant de l'utilisateur.
     * @return true si la table a été associée avec succès, sinon false.
     * @throws SQLException En cas d'erreur SQL.
     */
    public static boolean createTableWithSpecificId(int tableId, int userId) throws SQLException {
        // Vérification de la disponibilité de la table
        String checkQuery = "SELECT id_table_client FROM table_client WHERE id_table_client = ? AND id_table_client NOT IN (SELECT id_table_client FROM commande)";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {

            checkStmt.setInt(1, tableId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // La table est disponible, insertion dans la commande
                String insertQuery = "INSERT INTO commande (id_table_client, id_user) VALUES (?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setInt(1, tableId);
                    insertStmt.setInt(2, userId);
                    insertStmt.executeUpdate();
                    return true; // Succès
                }
            } else {
                return false; // La table n'est pas disponible
            }
        }
    }

    /**
     * Récupère les tables associées à un utilisateur.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @return Une liste observable contenant les identifiants des tables associées.
     * @throws SQLException En cas d'erreur SQL.
     */
    public static ObservableList<String> getUserTables(int userId) throws SQLException {
        ObservableList<String> tables = FXCollections.observableArrayList();
        String query = "SELECT id_table_client FROM commande WHERE id_user = ?";
    
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int tableId = rs.getInt("id_table_client");
                tables.add("Table " + tableId);
            }
        }
        return tables;
    }

    /**
     * Récupère les détails d'une commande associée à une table.
     *
     * @param tableId L'identifiant de la table.
     * @return Une liste observable contenant les détails des produits de la commande.
     * @throws SQLException En cas d'erreur SQL.
     */
    public static ObservableList<String> getCommandeDetailsByTableId(int tableId) throws SQLException {
        ObservableList<String> details = FXCollections.observableArrayList();
        String query = "SELECT produit.nom AS product_name, produit.prix AS product_price "
                     + "FROM commande "
                     + "LEFT JOIN commande_produit_supplement c1 ON commande.id_commande = c1.id_commande "
                     + "LEFT JOIN produit ON produit.id_produit = c1.id_produit "
                     + "WHERE commande.id_table_client = ?";
    
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, tableId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String productName = rs.getString("product_name");
                double productPrice = rs.getDouble("product_price");
                details.add(productName + " - " + productPrice + "€");
            }
        }
        return details;
    }

    /**
     * Récupère l'identifiant de la commande associée à une table et un utilisateur.
     *
     * @param tableId L'identifiant de la table.
     * @param userId  L'identifiant de l'utilisateur.
     * @return L'identifiant de la commande si elle existe, sinon null.
     * @throws SQLException En cas d'erreur SQL.
     */
    public static Integer getCommandeIdByTableAndUser(int tableId, int userId) throws SQLException {
        String query = "SELECT id_commande FROM commande WHERE id_table_client = ? AND id_user = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, tableId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_commande");
            }
        }
        return null; // Aucune commande trouvée
    }
}
