package com.example.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.example.model.Categorie;
import com.example.model.CommandItem;
import com.example.model.Produit;
import com.example.model.SousCategorie;
import com.example.model.Supplement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DataProduits {

    /**
     * Récupère toutes les catégories disponibles.
     * 
     * @return Une liste observable de catégories.
     * @throws SQLException En cas d'erreur SQL.
     */
    public static ObservableList<Categorie> getCategories() throws SQLException {
        ObservableList<Categorie> categories = FXCollections.observableArrayList();
        String query = "SELECT * FROM categorie;";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                categories.add(new Categorie(
                    rs.getInt("id_categorie"),
                    rs.getString("nom"),
                    rs.getString("couleur_css_hexadecimal")
                ));
            }
        }
        System.out.println(categories);
        return categories;
    }

    /**
     * Récupère les sous-catégories associées à une catégorie donnée.
     * 
     * @param categorieId L'identifiant de la catégorie.
     * @return Une liste observable de sous-catégories.
     * @throws SQLException En cas d'erreur SQL.
     */
    public static ObservableList<SousCategorie> getSousCategories(int categorieId) throws SQLException {
        ObservableList<SousCategorie> sousCategories = FXCollections.observableArrayList();
        String query = "SELECT * FROM sous_categorie WHERE id_categorie = ?";

        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, categorieId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                sousCategories.add(new SousCategorie(
                    rs.getInt("id_sous_categorie"),
                    rs.getString("nom"),
                    rs.getInt("id_categorie"),
                    rs.getString("couleur_css_hexadecimal")
                ));
            }
        }
        return sousCategories;
    }

    public static void deleteItemBDD(int idItem, Integer idAssocie) {
        String query;
    
        if (idAssocie == null) {
            query = "DELETE FROM `barmenu2`.`commande_produit_supplement` WHERE `id_commande_produit_supplement` = ?";
        } else {
            query = "DELETE FROM `barmenu2`.`commande_produit_supplement` WHERE `id_commande_produit_supplement` = ? AND `supplement_associer` = ?";
        }

        try (Connection connection = DataBase.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
    
            preparedStatement.setInt(1, idItem);
    
            if (idAssocie != null) {
                preparedStatement.setInt(2, idAssocie);
            }
    
            int rowsAffected = preparedStatement.executeUpdate();
    
            System.out.println(rowsAffected + " ligne(s) supprimée(s) dans la base de données.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la suppression de l'élément.");
        }
    }

    
    /**
     * Récupère les détails d'une commande, y compris les produits et leurs suppléments.
     * 
     * @param commandeId L'identifiant de la commande.
     * @return Une liste observable d'items de commande (produits et suppléments).
     * @throws SQLException En cas d'erreur SQL.
     */
    public static ObservableList<CommandItem> getCommandeDetails(int commandeId) throws SQLException {
        ObservableList<CommandItem> items = FXCollections.observableArrayList();
        String query = "SELECT cps.id_commande_produit_supplement, p.nom AS produit_nom, p.prix AS produit_prix, " +
                       "s.nom AS supplement_nom, s.prix AS supplement_prix, cps.supplement_associer, p.id_produit, s.id_supplement " +
                       "FROM commande_produit_supplement cps " +
                       "LEFT JOIN produit p ON cps.id_produit = p.id_produit " +
                       "LEFT JOIN supplement s ON cps.id_supplement = s.id_supplement " +
                       "WHERE cps.id_commande = ? " +
                       "ORDER BY cps.id_commande_produit_supplement ASC;";
    
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
    
            stmt.setInt(1, commandeId);
            ResultSet rs = stmt.executeQuery();
    
            while (rs.next()) {
                String produitNom = rs.getString("produit_nom");
                double produitPrix = rs.getDouble("produit_prix");
                int idItem = rs.getInt("id_commande_produit_supplement");
                String supplementNom = rs.getString("supplement_nom");
                double supplementPrix = rs.getDouble("supplement_prix");
                int supplementAssocier = rs.getInt("supplement_associer");
    
                if (supplementAssocier == 0) {
                    // Produit principal
                    items.add(new CommandItem(produitNom, produitPrix, idItem, null));
                } else {
                    // Supplément
                    items.add(new CommandItem("  + " + supplementNom, supplementPrix, idItem, supplementAssocier));
                }
            }
        }
        return items;
    }
    
    public static ObservableList<Produit> getProductsWithSupplementsBySousCategorie(int sousCategorieId) throws SQLException {
        ObservableList<Produit> produits = FXCollections.observableArrayList();
        String query = "SELECT p.id_produit, p.nom AS produit_nom, p.id_sous_categorie, p.couleur_css_hexadecimal, s.id_supplement, s.nom AS supplement_nom, s.prix "
                     + "FROM produit p "
                     + "LEFT JOIN produits_supplements ps ON p.id_produit = ps.id_produit "
                     + "LEFT JOIN supplement s ON ps.id_supplement = s.id_supplement "
                     + "WHERE p.id_sous_categorie = ?;";
    
        try (Connection conn = DataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
    
            stmt.setInt(1, sousCategorieId);
            ResultSet rs = stmt.executeQuery();
    
            Map<Integer, Produit> produitMap = new HashMap<>();
    
            while (rs.next()) {
                int produitId = rs.getInt("id_produit");
                String produitNom = rs.getString("produit_nom");
                int idProduitSousCategorie = rs.getInt("id_sous_categorie");
                String couleur = rs.getString("couleur_css_hexadecimal");
    
                Produit produit = produitMap.computeIfAbsent(produitId, id -> new Produit(
                    id,
                    produitNom,
                    idProduitSousCategorie,
                    couleur,
                    FXCollections.observableArrayList()
                ));
    
                int supplementId = rs.getInt("id_supplement");
                if (supplementId > 0) {
                    Supplement supplement = new Supplement(
                        supplementId,
                        rs.getString("supplement_nom"),
                        rs.getDouble("prix")
                    );
                    produit.getSupplements().add(supplement);
                }
            }
    
            produits.addAll(produitMap.values());
        }
    
        return produits;
    }
    

    public static void ajouterProduitSansSupplements(int idCommande, int idProduit) throws SQLException {
        String insertProduitSQL = "INSERT INTO commande_produit_supplement (id_commande, id_produit) VALUES (?, ?)";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement pstmtProduit = conn.prepareStatement(insertProduitSQL)) {
    
            pstmtProduit.setInt(1, idCommande);
            pstmtProduit.setInt(2, idProduit);
            pstmtProduit.executeUpdate();
        }
    }
    public static void ajouterProduitAvecSupplements(int idCommande, int idProduit, ObservableList<Supplement> supplements) throws SQLException {
        String insertProduitSQL = "INSERT INTO commande_produit_supplement (id_commande, id_produit) VALUES (?, ?)";
        try (Connection conn = DataBase.getConnection()) {
            conn.setAutoCommit(false); // Commencer une transaction
    
            try (PreparedStatement pstmtProduit = conn.prepareStatement(insertProduitSQL, Statement.RETURN_GENERATED_KEYS)) {
    
                // Insérer le produit principal
                pstmtProduit.setInt(1, idCommande);
                pstmtProduit.setInt(2, idProduit);
                pstmtProduit.executeUpdate();
    
                // Récupérer l'ID généré pour le produit principal
                ResultSet rs = pstmtProduit.getGeneratedKeys();
                int idCommandeProduitSupplement = -1;
                if (rs.next()) {
                    idCommandeProduitSupplement = rs.getInt(1);
                }
    
                // Insérer les suppléments associés
                String insertSupplementSQL = "INSERT INTO commande_produit_supplement (id_commande, id_produit, id_supplement, supplement_associer) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmtSupplement = conn.prepareStatement(insertSupplementSQL)) {
                    for (Supplement supplement : supplements) {
                        pstmtSupplement.setInt(1, idCommande);
                        pstmtSupplement.setInt(2, idProduit);
                        pstmtSupplement.setInt(3, supplement.getId());
                        pstmtSupplement.setInt(4, idCommandeProduitSupplement);
                        pstmtSupplement.addBatch();
                    }
                    pstmtSupplement.executeBatch();
                }
    
                conn.commit(); // Valider la transaction
    
            } catch (SQLException e) {
                conn.rollback(); // Annuler la transaction en cas d'erreur
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
    
    
    
}
