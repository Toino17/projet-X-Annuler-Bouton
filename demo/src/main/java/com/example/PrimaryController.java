package com.example;

import java.io.IOException;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Function;

import com.example.DAO.DataProduits;
import com.example.DAO.DataUser;
import com.example.model.Categorie;
import com.example.model.CommandItem;
import com.example.model.Produit;
import com.example.model.SousCategorie;
import com.example.model.Supplement;
import com.example.model.UserSession;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class PrimaryController {

    // Champs de classe
    private Integer commandeId = null; // ID de la commande en cours
    private Stage stage; // Référence à la fenêtre principale

    // Méthodes de gestion de l'état
    public void setCommandeId(Integer commandeId) {
        this.commandeId = commandeId;
        reloadCommandeDetails(); // Recharger les détails après avoir défini commandeId
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // FXML Bindings
    @FXML private Label idUtilisateur;
    @FXML private TableView<CommandItem> tableView;
    @FXML private TableColumn<CommandItem, String> column1;
    @FXML private TableColumn<CommandItem, Double> column2;
    @FXML private FlowPane categoriesContainer;
    @FXML private VBox detailsContainer;

    // Collections
    private final ObservableList<Categorie> categories = FXCollections.observableArrayList();
    private final ObservableList<SousCategorie> sousCategories = FXCollections.observableArrayList();
    private final ObservableList<Produit> produits = FXCollections.observableArrayList();
    private final ObservableList<Supplement> supplements = FXCollections.observableArrayList();
    private final ObservableList<Produit> productsSelection = FXCollections.observableArrayList();

    // Initialisation du contrôleur
    @FXML
    public void initialize() {
        // Mise à jour du menu

        updateMenu();

        // Gestion de la session utilisateur
        UserSession session = UserSession.getInstance();
        String nom = session.getNom();
        String message = nom != null ? "Bienvenue, " + nom : "Utilisateur non connecté";
        idUtilisateur.setText(message);

        System.out.println("Initialisation de PrimaryController avec commandeId : " + commandeId);

        // Placeholder par défaut pour le TableView
        tableView.setItems(FXCollections.observableArrayList());
        tableView.setPlaceholder(new Label("Aucune commande sélectionnée ou aucun produit ajouter."));
    }

    // Recharger les détails de la commande
    public void reloadCommandeDetails() {
        
        if (commandeId != null && commandeId > 0) {
            System.out.println("Rechargement des détails pour commandeId : " + commandeId);
            initializeTableView(commandeId);
        } else {
            System.out.println("Aucune commande à charger.");
            tableView.setItems(FXCollections.observableArrayList());
            tableView.setPlaceholder(new Label("Aucune commande sélectionnée."));
        }
    }

    // Initialiser le TableView avec les détails de la commande
    public void initializeTableView(int commandeId) {

        column1.setCellValueFactory(new PropertyValueFactory<>("produitOuSupplement"));
        column2.setCellValueFactory(new PropertyValueFactory<>("prix"));
    
        try {
            ObservableList<CommandItem> items = DataProduits.getCommandeDetails(commandeId);
            tableView.setItems(items);
        } catch (SQLException e) {
            e.printStackTrace();
            tableView.setPlaceholder(new Label("Erreur lors du chargement des commandes."));
        }
    }
    

    // Charger les détails de la commande
    public void loadCommandeDetails(int commandeId) {
        try {
            ObservableList<CommandItem> items = DataProduits.getCommandeDetails(commandeId);
            tableView.setItems(items);
        } catch (SQLException e) {
            e.printStackTrace();
            tableView.setPlaceholder(new Label("Erreur lors du chargement des détails de la commande."));
        }
    }

    // Gestion des boutons de navigation
    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    @FXML
    private void switchToTroisiemary() throws IOException {
        App.setRoot("troisiemary");
    }

    //Supprimer item

    @FXML
    private void deleteItem(){
        CommandItem toDeletItem = tableView.getSelectionModel().getSelectedItem();
        DataProduits.deleteItemBDD(toDeletItem.getId(), toDeletItem.getIdAssocie());
        reloadCommandeDetails();
        System.err.println("id item = " + toDeletItem.getId() + ", idAssocie= " + toDeletItem.getIdAssocie());
    }

    // Création d'une nouvelle table associée à un utilisateur
    @FXML
    private void nouvelleTable(ActionEvent event) {
        Dialog<String> dialog = new Dialog<>();
        Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        dialog.initOwner(currentStage);

        dialog.setTitle("Nouvelle commande");
        Label label = new Label("Numéro de table");
        TextField textField = new TextField();
        ButtonType validerButtonType = new ButtonType("Valider");
        dialog.getDialogPane().getButtonTypes().addAll(validerButtonType, ButtonType.CANCEL);

        VBox vbox = new VBox(10, label, textField);
        dialog.getDialogPane().setContent(vbox);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == validerButtonType) {
                return textField.getText();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(response -> {
            try {
                int tableId = Integer.parseInt(response);
                int userId = UserSession.getInstance().getIdUser();
                boolean success = DataUser.createTableWithSpecificId(tableId, userId);

                if (success) {
                    System.out.println("Table " + tableId + " associée avec succès !");
                } else {
                    System.out.println("Table " + tableId + " déjà occupée ou inexistante.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un numéro de table valide.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    // Mise à jour du menu principal
    public void updateMenu() {
        categoriesContainer.getChildren().clear();

        try {
            categories.setAll(DataProduits.getCategories());
            for (Categorie category : categories) {
                Button button = new Button(category.getName());
                button.setMinWidth(90);
                button.setMinHeight(45);

                String hexColor = category.getCouleur();
                button.setStyle("-fx-base: " + hexColor + ";");

                button.setOnAction(e -> updateDetailsForCategory(category.getId()));
                categoriesContainer.getChildren().add(button);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Mise à jour des détails pour une catégorie
// Méthode générique pour afficher une grille
private <T> void displayItemsInGrid(
        ObservableList<T> items,
        Function<T, String> getNameFunction,
        Function<T, String> getColorFunction,
        Consumer<T> onClickHandler) {

    // Vider le conteneur principal
    detailsContainer.getChildren().clear();

    // Créer un GridPane pour organiser les boutons
    GridPane grid = createGrid();

    // Configurer les colonnes pour s'assurer qu'elles s'étendent uniformément
    for (int i = 0; i < 3; i++) { // 3 colonnes max
        ColumnConstraints colConstraints = new ColumnConstraints();
        colConstraints.setPercentWidth(33.33); // Largeur égale pour chaque colonne
        colConstraints.setHalignment(HPos.CENTER); // Centrer horizontalement
        grid.getColumnConstraints().add(colConstraints);
    }

    // Configurer les lignes pour s'assurer qu'elles s'étendent uniformément
    RowConstraints rowConstraints = new RowConstraints();
    rowConstraints.setValignment(VPos.CENTER); // Centrer verticalement
    grid.getRowConstraints().add(rowConstraints);

    int row = 0;
    int col = 0;

    // Ajouter des boutons au GridPane
    for (T item : items) {
        Button button = new Button(getNameFunction.apply(item));
        button.setPrefWidth(150); // Largeur préférée du bouton
        button.setMinHeight(60); // Hauteur minimale du bouton
        button.setAlignment(Pos.CENTER); // Centrer le texte dans le bouton
        button.setWrapText(true); // Active le retour à la ligne
        button.setTextAlignment(TextAlignment.CENTER); // Centre le texte

        // Appliquer la couleur si disponible
        String hexColor = getColorFunction.apply(item);
        if (hexColor != null) {
            button.setStyle("-fx-base: " + hexColor + ";");
        }

        // Action sur clic
        button.setOnAction(e -> onClickHandler.accept(item));

        // Ajouter le bouton au GridPane
        grid.add(button, col, row);

        // Passer à la cellule suivante
        col++;
        if (col == 3) { // 3 colonnes max
            col = 0;
            row++;
            // Ajouter une nouvelle ligne uniquement si nécessaire
            grid.getRowConstraints().add(new RowConstraints(60)); // Hauteur de chaque ligne
        }
    }

    // Encapsuler le GridPane dans un ScrollPane
    ScrollPane scrollPane = new ScrollPane();
    scrollPane.setContent(grid); // Ajouter le GridPane au ScrollPane
    scrollPane.setFitToWidth(true); // Ajuster la largeur automatiquement
    scrollPane.setPannable(true); // Permet de scroller en glissant avec votre pannard
    scrollPane.setPrefViewportHeight(200); // Définir la hauteur visible
    // scrollPane.setPrefViewportWidth(800); // Définir la largeur visible

    // Ajouter le ScrollPane à une VBox pour garantir le centrage global
    VBox outerContainer = new VBox(scrollPane);
    outerContainer.setAlignment(Pos.CENTER); // Centrer globalement
    outerContainer.setPadding(new Insets(10)); // Espacement autour du contenu

    // Ajouter la VBox au conteneur principal
    detailsContainer.getChildren().add(outerContainer);
}

// Mise à jour des détails pour une sous-catégorie
public void updateDetailsForSousCategory(int sousCategorieId) {
    try {
        produits.setAll(DataProduits.getProductsWithSupplementsBySousCategorie(sousCategorieId));
        displayItemsInGrid(
                produits,
                Produit::getName,
                Produit::getCouleur,
                produit -> {
                    // Vérifier si le produit a des suppléments
                    if (produit.getSupplements() == null || produit.getSupplements().isEmpty()) {
                        // Ajouter le produit directement sans suppléments
                        insertLocalProduct(produit.getId(), null);
                    } else {
                        // Afficher la boîte de dialogue pour choisir des suppléments
                        showSupplement(produit.getSupplements(), produit.getId());
                    }
                }
        );
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


// Mise à jour des détails pour une catégorie avec sous-catégories
public void updateDetailsForCategory(int categoryId) {
    try {
        sousCategories.setAll(DataProduits.getSousCategories(categoryId));
        if (!sousCategories.isEmpty()) {
            displayItemsInGrid(
                    sousCategories,
                    SousCategorie::getNom,
                    SousCategorie::getCouleur,
                    sousCategorie -> updateDetailsForSousCategory(sousCategorie.getId())
            );
        } 
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    // Afficher un dialog pour les suppléments
    @FXML
    private void showSupplementDialog(ObservableList<Supplement> supplements, String produit) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(stage);
        dialog.setTitle("Suppléments pour " + produit);

        TableView<Supplement> tableView = createSupplementTableView();
        ObservableList<Supplement> supplementsSelectionnes = FXCollections.observableArrayList();
        tableView.setItems(supplementsSelectionnes);

        HBox hbox = createSupplementButtons(supplements, supplementsSelectionnes);
        VBox vbox = new VBox(10, new Label("Produit : " + produit), tableView, hbox);
        vbox.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.APPLY);
        dialog.showAndWait();
    }

    // Créer une table pour les suppléments
    private TableView<Supplement> createSupplementTableView() {
        TableView<Supplement> tableView = new TableView<>();
        tableView.setEditable(false);
        TableColumn<Supplement, String> nomColonne = new TableColumn<>("Supplément");
        nomColonne.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNom()));
        tableView.getColumns().add(nomColonne);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return tableView;
    }

    // Créer les boutons pour ajouter des suppléments
    private HBox createSupplementButtons(ObservableList<Supplement> supplements, ObservableList<Supplement> supplementsSelectionnes) {
        HBox hbox = new HBox(10);
        hbox.setStyle("-fx-alignment: center;");
        for (Supplement supplement : supplements) {
            Button button = new Button(supplement.getNom());
            button.setOnAction(e -> {
                supplementsSelectionnes.add(supplement);
                System.out.println("Supplément ajouté : " + supplement.getNom());
            });
            hbox.getChildren().add(button);
        }
        return hbox;
    }

    @FXML
    private void showSupplement(ObservableList<Supplement> supplements, int idProduit) {
    ObservableList<Supplement> supplementsSelectionnes = FXCollections.observableArrayList();
    Dialog<Void> dialog = new Dialog<>();
    dialog.initOwner(stage);

    dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
    dialog.getDialogPane().getButtonTypes().add(ButtonType.APPLY);

    Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
    cancelButton.setText("Annuler");
    cancelButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 8px;");
    cancelButton.setOnAction(e -> {
        System.out.println("Aucun supplément sélectionné");
        dialog.close();
    });

        Label produitLabel = new Label("Produit : " + idProduit);
        produitLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        TableView<Supplement> tableView = new TableView<>();
        tableView.setEditable(false);

        TableColumn<Supplement, String> nomColonne = new TableColumn<>("Supplément");
        nomColonne.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNom()));

        tableView.getColumns().add(nomColonne);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tableView.setMaxHeight(200);
        tableView.setPrefHeight(200);
        tableView.setMaxWidth(300);
        tableView.setPrefWidth(300);


        tableView.setItems(supplementsSelectionnes);


    Label label = new Label("Ajoutez des suppléments :");
    label.getStyleClass().add("labelNewTable");

    HBox hbox = new HBox(10);
    hbox.setStyle("-fx-alignment: center;");

    for (Supplement supplement : supplements) {
        String nomSupplement = supplement.getNom();
        Button buttonSupplement = new Button(nomSupplement);
        hbox.getChildren().add(buttonSupplement);

        buttonSupplement.setOnAction(e -> {
            System.out.println("Supplément ajouté : " + nomSupplement);
            supplementsSelectionnes.add(supplement);
        });
    }

    Button validateButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.APPLY);
    validateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 8px;");
    validateButton.setOnAction(e -> {
    insertLocalProduct(idProduit, supplementsSelectionnes);
    System.out.println(productsSelection);
    System.out.println("Validation des suppléments sélectionnés :");
    supplementsSelectionnes.forEach(supplement -> 
        System.out.println("- " + supplement.getNom())
    );
    dialog.close();
    });

    VBox vbox = new VBox(10, produitLabel, tableView, label, hbox);
    vbox.setPadding(new Insets(10));
    vbox.setStyle("-fx-alignment: center;");

    dialog.getDialogPane().setContent(vbox);
    dialog.showAndWait();
}

private void insertLocalProduct(int idProduit, ObservableList<Supplement> supplementsSelectionnes) {
    int idCommande = this.commandeId; // Assurez-vous que commandeId est correctement initialisé

    try {
        if (supplementsSelectionnes == null || supplementsSelectionnes.isEmpty()) {
            // Produit sans suppléments
            DataProduits.ajouterProduitSansSupplements(idCommande, idProduit);
        } else {
            // Produit avec suppléments
            DataProduits.ajouterProduitAvecSupplements(idCommande, idProduit, supplementsSelectionnes);
        }

        // Recharger les détails de la commande après insertion
        reloadCommandeDetails();

    } catch (SQLException e) {
        e.printStackTrace();
        // Afficher un message d'erreur à l'utilisateur si nécessaire
    }
}



// private void afficherProductsSelection() {
//     if (productsSelection.isEmpty()) {
//         System.out.println("Aucun produit dans la sélection.");
//         return;
//     }

//     System.out.println("Liste des produits sélectionnés :");
//     for (Produit produit : productsSelection) {
//         System.out.println("Nom: " + produit.getName());

//         if (produit.getSupplements().isEmpty()) {
//             System.out.println("Aucun supplément associé.");
//         } else {
//             System.out.println("Suppléments associés :");
//             for (Supplement supplement : produit.getSupplements()) {
//                 System.out.println("    - " + supplement.getNom() + " (Prix: " + supplement.getPrix() + ")");
//             }
//         }
//         System.out.println();
//     }
// }

    private GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        return grid;
    }
}
