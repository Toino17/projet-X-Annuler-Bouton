package com.example;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TroisiemaryController {

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    @FXML
    private void ToutEncaisser(ActionEvent event) {
        // Création d'une boîte de dialogue
        Dialog<String> dialog = new Dialog<>();

        // Récupération de la fenêtre courante pour attacher la boîte de dialogue
        Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        dialog.initOwner(currentStage);

        // Configuration de la boîte de dialogue
        dialog.setTitle("Confirmation");
        // dialog.setHeaderText("");

        // Ajout des boutons "Valider" et "Annuler"
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL); // Ajout du bouton "Annuler" par défaut
        Button buttonValider = new Button("Valider");
        Button buttonAnnuler = new Button("Annuler");

        // Action pour le bouton "Valider"
        buttonValider.setOnAction(e -> {
            dialog.setResult("Validé");
            dialog.close();
        });

        // Action pour le bouton "Annuler"
        buttonAnnuler.setOnAction(e -> {
            dialog.setResult("Annulé");
            dialog.close();
        });

        // Conteneur pour les boutons
        HBox hbox = new HBox(10, buttonValider, buttonAnnuler);
        hbox.setStyle("-fx-alignment: center;");

        // Conteneur principal de la boîte de dialogue
        VBox vbox = new VBox(10, new Label("Confirmez l'encaissement des commandes."), hbox);
        dialog.getDialogPane().setContent(vbox);

        // Désactiver le bouton par défaut "Annuler"
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setVisible(false);

        // Configuration du convertisseur de résultat pour la boîte de dialogue
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.CANCEL) {
                return "Annulé par fermeture";
            }
            return dialog.getResult();
        });

        // Affichage de la boîte de dialogue et gestion de la réponse
        dialog.showAndWait().ifPresent(response -> {
            System.out.println("Résultat du Dialog : " + response);
        });
    }
}
