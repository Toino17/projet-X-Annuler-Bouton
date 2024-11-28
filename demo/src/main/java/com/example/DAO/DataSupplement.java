package com.example.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.model.Supplement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DataSupplement {

    /**
     * Récupère tous les suppléments disponibles dans la base de données.
     *
     * @return Une liste observable de tous les suppléments.
     * @throws SQLException En cas d'erreur SQL.
     */
    public static ObservableList<Supplement> getAllSupplements() throws SQLException {
        ObservableList<Supplement> allSupplements = FXCollections.observableArrayList();
        String query = "SELECT * FROM supplement";

        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                allSupplements.add(new Supplement(
                    rs.getInt("id_supplement"), // Identifiant du supplément
                    rs.getString("nom"),       // Nom du supplément
                    rs.getDouble("prix")       // Prix du supplément
                ));
            }
        }
        return allSupplements;
    }

    /**
     * Récupère les suppléments associés à un produit donné.
     *
     * @param produitId L'identifiant du produit.
     * @return Une liste observable des suppléments associés au produit.
     * @throws SQLException En cas d'erreur SQL.
     */
    public static ObservableList<Supplement> getSupplementByProduit(int produitId) throws SQLException {
        ObservableList<Supplement> supplementByProduit = FXCollections.observableArrayList();
        String query = "SELECT supplement.id_supplement, supplement.nom, supplement.prix "
                     + "FROM supplement "
                     + "INNER JOIN produits_supplements ON produits_supplements.id_supplement = supplement.id_supplement "
                     + "INNER JOIN produit ON produits_supplements.id_produit = produit.id_produit "
                     + "WHERE produit.id_produit = ?";

        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, produitId); // Définit le produit cible dans la requête
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                supplementByProduit.add(new Supplement(
                    rs.getInt("id_supplement"), // Identifiant du supplément
                    rs.getString("nom"),       // Nom du supplément
                    rs.getDouble("prix")       // Prix du supplément
                ));
            }
        }
        return supplementByProduit;
    }
}
