package com.example;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import com.example.DAO.DataUser;
import com.example.model.UserSession;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    private static Scene scene; // Scène principale de l'application

    @Override
    public void start(Stage stage) throws IOException {
        // Charger le fichier FXML principal
        FXMLLoader loader = new FXMLLoader(getClass().getResource("primary.fxml"));
        Parent root = loader.load(); // Charger la vue depuis l'FXML

        // Récupérer le contrôleur principal
        PrimaryController controller = loader.getController();
        controller.setStage(stage); // Associer le stage au contrôleur

        // Créer et configurer la scène principale
        scene = new Scene(root, 450, 785);

        // Ajouter une icône à l'application
        Image icon = new Image(getClass().getResourceAsStream("/com/example/img/logoApp3.png"));
        stage.getIcons().add(icon);

        // Ajouter la feuille de style à la scène
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        // Configurer et afficher la scène principale
        stage.setScene(scene);
        stage.show();

        // Afficher une boîte de dialogue de connexion au démarrage
        showStartupDialog(stage);
    }

    /**
     * Affiche une boîte de dialogue de démarrage pour l'authentification utilisateur.
     *
     * @param stage Le stage principal de l'application.
     */
    private void showStartupDialog(Stage stage) {
        Dialog<String> dialog = new Dialog<>();
        dialog.initOwner(stage); // Associer la boîte de dialogue au stage principal

        dialog.setTitle("Connexion"); // Titre de la boîte de dialogue
        dialog.getDialogPane().getStyleClass().add("custom-dialog");
        
        // Création des éléments de la boîte de dialogue
        Label label = new Label("IDENTIFIANT");
        label.getStyleClass().add("labelIdentifiant"); // Ajouter un style personnalisé

        TextField textField = new TextField();
        textField.setPromptText("Entrez votre identifiant");

        // Bouton de validation
        Button buttonValider = new Button("Valider");
        buttonValider.setOnAction(e -> {
            String input = textField.getText();
            if (input.isEmpty()) {
                dialog.setHeaderText("Veuiller entrer votre identifiant");
            } else {
                try {
                    // Authentification via la base de données
                    if (DataUser.authenticate(input)) {
                        String nom = UserSession.getInstance().getNom();
                        System.out.println("Connexion réussie : " + nom);

                        dialog.setResult(input);
                        dialog.close();

                        // Mettre à jour dynamiquement le label d'utilisateur connecté
                        Platform.runLater(() -> {
                            String message = (nom != null) ? "Bienvenue, " + nom : "Utilisateur non connecté";
                            ((Label) scene.lookup("#idUtilisateur")).setText(message);
                        });
                    } else {
                        dialog.setHeaderText("Veuiller entrer VOTRE identifiant");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Contenu de la boîte de dialogue
        VBox vbox = new VBox(10, label, textField, buttonValider);
        vbox.getStyleClass().add("containerVBox");

        dialog.getDialogPane().setContent(vbox); // Ajouter le contenu
        dialog.getDialogPane().setPrefWidth(300);
        dialog.getDialogPane().setPrefHeight(200);

        // Bouton de fermeture (enlever içi pour obliger un utilisateur à se connecté, le boutton validé ferme la fenêtre si un identifiant est correct)

        // ButtonType closeButtonType = ButtonType.CANCEL;
        // dialog.getDialogPane().getButtonTypes().add(closeButtonType);
        // dialog.getDialogPane().lookupButton(closeButtonType).setVisible(false); // Masquer le bouton Annuler

        // dialog.setResultConverter(dialogButton -> {
        //     if (dialogButton == closeButtonType) {
        //         return null; // Aucun résultat en cas de fermeture
        //     }
        //     return null;
        // });

        // Affichage de la boîte de dialogue et traitement de son résultat
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            System.out.println("Utilisateur connecté : " + result.get());
        } else {
            System.out.println("La boîte de dialogue a été fermée sans action.");
        }
    }

    /**
     * Définit la racine de la scène principale.
     *
     * @param fxml Le fichier FXML à charger.
     * @throws IOException En cas d'erreur lors du chargement du fichier FXML.
     */
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Charge un fichier FXML et retourne son parent.
     *
     * @param fxml Le nom du fichier FXML sans extension.
     * @return Le parent chargé depuis le fichier FXML.
     * @throws IOException En cas d'erreur lors du chargement.
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * Point d'entrée principal de l'application.
     *
     * @param args Les arguments de ligne de commande.
     */
    public static void main(String[] args) {
        launch(); // Démarrage de l'application JavaFX
    }
}
