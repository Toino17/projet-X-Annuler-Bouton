package com.example;

import java.io.IOException;
import java.sql.SQLException;

import com.example.DAO.DataUser;
import com.example.model.UserSession;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class SecondaryController {

    // FXML Binding pour la liste des commandes
    @FXML
    private ListView<String> commandListView;

    /**
     * Initialisation du contrôleur
     * Chargement des tables associées à l'utilisateur connecté
     */
    @FXML
    public void initialize() {
        int userId = UserSession.getInstance().getIdUser(); // Récupère l'ID de l'utilisateur connecté
        try {
            // Appeler la méthode DAO pour récupérer les tables associées
            ObservableList<String> tables = DataUser.getUserTables(userId);

            // Vérifier si l'utilisateur n'a pas de tables associées
            if (tables.isEmpty()) {
                tables.add("Aucune table associée !");
            }

            // Charger les tables dans la ListView
            commandListView.setItems(tables);

        } catch (SQLException e) {
            e.printStackTrace();
            // En cas d'erreur, afficher un message dans la liste
            commandListView.setItems(FXCollections.observableArrayList("Erreur lors du chargement des tables."));
        }
    }

    /**
     * Méthode pour valider une sélection et basculer vers la vue principale
     */
    @FXML
    private void validateAndSwitchToPrimary() {
        // Obtenir la table sélectionnée dans la ListView
        String selectedTable = commandListView.getSelectionModel().getSelectedItem();

        // Vérifier si une table valide est sélectionnée
        if (selectedTable != null && !selectedTable.equals("Aucune table associée !")) {
            try {
                // Extraire l'ID de la table à partir du texte sélectionné
                int tableId = Integer.parseInt(selectedTable.replaceAll("[^0-9]", ""));
                int userId = UserSession.getInstance().getIdUser(); // ID de l'utilisateur connecté

                // Récupérer l'ID de la commande associée à cette table et cet utilisateur
                Integer commandeId = DataUser.getCommandeIdByTableAndUser(tableId, userId);

                if (commandeId != null) {
                    // Charger la vue principale
                    FXMLLoader loader = new FXMLLoader(App.class.getResource("primary.fxml"));
                    Parent root = loader.load();

                    // Configurer le contrôleur principal avec la commandeId
                    PrimaryController primaryController = loader.getController();
                    primaryController.setCommandeId(commandeId);

                    // Basculer vers la vue principale
                    Stage stage = (Stage) commandListView.getScene().getWindow();
                    stage.getScene().setRoot(root);
                } else {
                    System.out.println("Aucune commande trouvée pour la table " + tableId + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Erreur : Numéro de table invalide. Veuillez sélectionner une table valide.");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Erreur lors de la récupération de la commande.");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Erreur lors du chargement de la vue principale.");
            }
        } else {
            System.out.println("Veuillez sélectionner une table valide.");
        }
    }

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }

    @FXML
    private void switchToTroisiemary() throws IOException {
        App.setRoot("troisiemary");
    }
}
