package tn.iset.m2glnt.client.viewer.view;

import org.mindrot.jbcrypt.BCrypt;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import tn.iset.m2glnt.client.service.EtudiantRestService;
import tn.iset.m2glnt.client.service.dto.EtudiantRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EtudiantView extends BorderPane {
    private final EtudiantRestService etudiantService;
    private TableView<Map<String, Object>> tableView;
    private ObservableList<Map<String, Object>> etudiantsData;
    private Label titleLabel;
    private Label statsLabel;
    private ProgressIndicator loadingIndicator;

    public EtudiantView() {
        this.etudiantService = new EtudiantRestService();
        this.etudiantsData = FXCollections.observableArrayList();
        initializeView();
        loadEtudiants();
    }

    private void initializeView() {
        // Style général avec gradient premium
        this.setStyle("-fx-background-color: linear-gradient(135deg, #667eea 0%, #764ba2 100%);");

        // En-tête premium
        VBox header = createPremiumHeader();

        // Table avec design premium
        VBox tableContainer = createPremiumTable();

        // Layout principal
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(25));
        mainContainer.getChildren().addAll(header, tableContainer);

        setCenter(mainContainer);
    }

    private VBox createPremiumHeader() {
        VBox header = new VBox(20);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(25, 25, 35, 25));

        // Container du titre avec fond accentué
        StackPane titleContainer = new StackPane();
        titleContainer.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 20; " +
                "-fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 5);");
        titleContainer.setMaxWidth(500);

        // Titre principal avec icône
        HBox titleBox = new HBox(15);
        titleBox.setAlignment(Pos.CENTER);

        Label iconLabel = new Label("👨‍🎓");
        iconLabel.setFont(Font.font("System", 36));
        iconLabel.setTextFill(Color.WHITE);

        titleLabel = new Label("Gestion des Étudiants");
        titleLabel.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 34));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 12, 0, 0, 4);");

        titleBox.getChildren().addAll(iconLabel, titleLabel);
        titleContainer.getChildren().add(titleBox);

        // Container des statistiques
        HBox statsContainer = new HBox(20);
        statsContainer.setAlignment(Pos.CENTER);

        // Indicateur de chargement
        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setVisible(false);
        loadingIndicator.setStyle("-fx-progress-color: white;");
        loadingIndicator.setPrefSize(25, 25);

        // Statistiques avec badge
        StackPane statsBadge = new StackPane();
        statsBadge.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-background-radius: 15; " +
                "-fx-padding: 12 25; -fx-border-color: rgba(255,255,255,0.3); -fx-border-radius: 15; -fx-border-width: 1;");

        statsLabel = new Label("Chargement...");
        statsLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 16));
        statsLabel.setTextFill(Color.web("#E0E0E0"));

        statsBadge.getChildren().add(statsLabel);
        statsContainer.getChildren().addAll(loadingIndicator, statsBadge);

        // Boutons d'action avec design premium
        HBox buttonContainer = createPremiumButtonContainer();

        header.getChildren().addAll(titleContainer, statsContainer, buttonContainer);
        return header;
    }

    private HBox createPremiumButtonContainer() {
        HBox buttonContainer = new HBox(20);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(15));

        Button addButton = createPremiumButton("➕ Ajouter", "#10B981", "#059669", "#047857");
        addButton.setOnAction(e -> showAddEtudiantDialog());

        Button editButton = createPremiumButton("✏️ Modifier", "#3B82F6", "#2563EB", "#1D4ED8");
        editButton.setOnAction(e -> showEditEtudiantDialog());

        Button deleteButton = createPremiumButton("🗑️ Supprimer", "#EF4444", "#DC2626", "#B91C1C");
        deleteButton.setOnAction(e -> deleteEtudiant());

        Button refreshButton = createPremiumButton("🔄 Actualiser", "#8B5CF6", "#7C3AED", "#6D28D9");
        refreshButton.setOnAction(e -> loadEtudiants());

        buttonContainer.getChildren().addAll(addButton, editButton, deleteButton, refreshButton);
        return buttonContainer;
    }

    private Button createPremiumButton(String text, String color, String hoverColor, String pressedColor) {
        Button button = new Button(text);
        button.setFont(Font.font("System", FontWeight.BOLD, 14));
        button.setPrefHeight(48);
        button.setMinWidth(140);

        String baseStyle = "-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 12; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 8, 0, 0, 3); " +
                "-fx-padding: 12 20; " +
                "-fx-font-weight: bold; " +
                "-fx-border-color: transparent; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 12;";

        String hoverStyle = "-fx-background-color: " + hoverColor + "; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 12; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 12, 0, 0, 5); " +
                "-fx-border-color: rgba(255,255,255,0.3);";

        String pressedStyle = "-fx-background-color: " + pressedColor + "; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0, 0, 1); " +
                "-fx-border-color: rgba(255,255,255,0.5);";

        button.setStyle(baseStyle);

        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));
        button.setOnMousePressed(e -> button.setStyle(pressedStyle));
        button.setOnMouseReleased(e -> button.setStyle(hoverStyle));

        return button;
    }

    private VBox createPremiumTable() {
        VBox tableContainer = new VBox();
        tableContainer.setStyle("-fx-background-color: white; -fx-background-radius: 20; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 30, 0, 0, 10);");
        tableContainer.setPadding(new Insets(25));

        // En-tête de table
        HBox tableHeader = new HBox();
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        tableHeader.setPadding(new Insets(0, 0, 15, 0));

        Label tableTitle = new Label("📋 Liste des Étudiants");
        tableTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        tableTitle.setTextFill(Color.web("#2D3748"));

        tableHeader.getChildren().add(tableTitle);

        // Configuration de la table avec style premium
        tableView = new TableView<>();
        tableView.setStyle("-fx-background-color: transparent; -fx-border-radius: 12; " +
                "-fx-background-radius: 12; -fx-border-color: #E2E8F0; -fx-border-width: 2;");
        tableView.setItems(etudiantsData);

        // Style des lignes avec effets avancés
        tableView.setRowFactory(tv -> new TableRow<Map<String, Object>>() {
            @Override
            protected void updateItem(Map<String, Object> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else {
                    setStyle("-fx-border-color: #F1F5F9; -fx-border-width: 0 0 1 0; " +
                            "-fx-background-color: white; -fx-background-radius: 0;");
                    setOnMouseEntered(e -> setStyle("-fx-background-color: #F8FAFC; " +
                            "-fx-border-color: #F1F5F9; -fx-border-width: 0 0 1 0; " +
                            "-fx-effect: dropshadow(gaussian, rgba(59,130,246,0.1), 10, 0, 0, 2);"));
                    setOnMouseExited(e -> setStyle("-fx-background-color: white; " +
                            "-fx-border-color: #F1F5F9; -fx-border-width: 0 0 1 0; " +
                            "-fx-effect: null;"));
                }
            }
        });

        // Création des colonnes avec style premium
        TableColumn<Map<String, Object>, Long> idCol = createPremiumColumn("ID", "id", 80);
        TableColumn<Map<String, Object>, String> nomCol = createPremiumColumn("Nom", "nom", 180);
        TableColumn<Map<String, Object>, String> prenomCol = createPremiumColumn("Prénom", "prenom", 180);
        TableColumn<Map<String, Object>, String> emailCol = createPremiumColumn("Email", "email", 250);
        TableColumn<Map<String, Object>, String> cinCol = createPremiumColumn("CIN", "cin", 120);

        tableView.getColumns().addAll(idCol, nomCol, prenomCol, emailCol, cinCol);

        // Message quand la table est vide avec design amélioré
        StackPane placeholderContainer = new StackPane();
        placeholderContainer.setPadding(new Insets(40));

        VBox placeholderContent = new VBox(15);
        placeholderContent.setAlignment(Pos.CENTER);

        Label placeholderIcon = new Label("👨‍🎓");
        placeholderIcon.setFont(Font.font("System", 48));

        Label placeholderText = new Label("Aucun étudiant disponible");
        placeholderText.setFont(Font.font("System", FontWeight.MEDIUM, 16));
        placeholderText.setTextFill(Color.web("#718096"));

        Label placeholderSubtext = new Label("Cliquez sur 'Ajouter' pour créer un nouvel étudiant");
        placeholderSubtext.setFont(Font.font("System", FontWeight.NORMAL, 14));
        placeholderSubtext.setTextFill(Color.web("#A0AEC0"));

        placeholderContent.getChildren().addAll(placeholderIcon, placeholderText, placeholderSubtext);
        placeholderContainer.getChildren().add(placeholderContent);

        tableView.setPlaceholder(placeholderContainer);

        tableContainer.getChildren().addAll(tableHeader, tableView);
        return tableContainer;
    }

    private <T> TableColumn<Map<String, Object>, T> createPremiumColumn(String title, String property, double width) {
        TableColumn<Map<String, Object>, T> column = new TableColumn<>(title);
        column.setPrefWidth(width);

        // Style premium de l'en-tête
        column.setStyle("-fx-background-color: linear-gradient(to bottom, #4F46E5, #3730A3); " +
                "-fx-border-color: #312E81; -fx-font-weight: bold; " +
                "-fx-text-fill: white; -fx-alignment: CENTER; " +
                "-fx-font-size: 13; -fx-padding: 15 5;");

        // Configuration de la valeur selon le type
        if ("id".equals(property)) {
            TableColumn<Map<String, Object>, Long> idColumn = (TableColumn<Map<String, Object>, Long>) column;
            idColumn.setCellValueFactory(data -> {
                Map<String, Object> row = data.getValue();
                Object idValue = row.get("id");
                if (idValue instanceof Number) {
                    return new javafx.beans.property.SimpleLongProperty(((Number) idValue).longValue()).asObject();
                }
                return new javafx.beans.property.SimpleLongProperty(0L).asObject();
            });
        } else {
            TableColumn<Map<String, Object>, String> stringColumn = (TableColumn<Map<String, Object>, String>) column;
            stringColumn.setCellValueFactory(data -> {
                Map<String, Object> row = data.getValue();
                Object value = row.get(property);
                return new javafx.beans.property.SimpleStringProperty(value != null ? value.toString() : "");
            });
        }

        // Style premium des cellules
        column.setCellFactory(tc -> new TableCell<Map<String, Object>, T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    setGraphic(null);
                } else {
                    setText(item.toString());
                    setStyle("-fx-alignment: CENTER; -fx-font-family: 'System'; " +
                            "-fx-text-fill: #4A5568; -fx-font-size: 13; " +
                            "-fx-font-weight: 500; -fx-padding: 12 5;");

                    // Mise en valeur des emails
                    if ("email".equals(property)) {
                        setStyle("-fx-alignment: CENTER-LEFT; -fx-font-family: 'System'; " +
                                "-fx-text-fill: #3B82F6; -fx-font-size: 13; " +
                                "-fx-font-weight: 500; -fx-padding: 12 5;");
                    }
                }
            }
        });

        return column;
    }

    private void loadEtudiants() {
        System.out.println("🔄 Chargement des étudiants...");

        // Animation de chargement améliorée
        statsLabel.setText("⏳ Chargement en cours...");
        statsLabel.setTextFill(Color.web("#FBBF24"));
        loadingIndicator.setVisible(true);
        loadingIndicator.setProgress(-1);

        etudiantService.getAllEtudiants()
                .thenAccept(etudiants -> {
                    System.out.println("✅ " + etudiants.size() + " étudiants chargés");
                    Platform.runLater(() -> {
                        etudiantsData.clear();
                        etudiantsData.addAll(etudiants);

                        // Mise à jour des statistiques
                        updateStats(etudiants.size());
                        loadingIndicator.setVisible(false);

                        System.out.println("📊 Données affichées dans la table: " + etudiantsData.size() + " éléments");

                        // Debug: afficher les IDs
                        etudiants.forEach(etudiant -> {
                            Object id = etudiant.get("id");
                            Object nom = etudiant.get("nom");
                            Object prenom = etudiant.get("prenom");
                            Object email = etudiant.get("email");
                            System.out.println("   - Étudiant ID: " + id + ", Nom: " + nom + " " + prenom + ", Email: " + email);
                        });
                    });
                })
                .exceptionally(throwable -> {
                    System.err.println("❌ Erreur lors du chargement: " + throwable.getMessage());
                    Platform.runLater(() -> {
                        statsLabel.setText("❌ Erreur de chargement");
                        statsLabel.setTextFill(Color.web("#EF4444"));
                        loadingIndicator.setVisible(false);
                        showStyledAlert("Erreur", "Erreur lors du chargement des étudiants: " + throwable.getMessage(), Alert.AlertType.ERROR);
                    });
                    return null;
                });
    }

    private void updateStats(int count) {
        String statText;
        Color textColor;

        if (count == 0) {
            statText = "📭 Aucun étudiant disponible";
            textColor = Color.web("#EF4444");
        } else if (count == 1) {
            statText = "✅ 1 étudiant inscrit";
            textColor = Color.web("#10B981");
        } else {
            statText = "👨‍🎓 " + count + " étudiants inscrits";
            textColor = Color.web("#10B981");
        }

        statsLabel.setText(statText);
        statsLabel.setTextFill(textColor);
    }

    private void showAddEtudiantDialog() {
        Dialog<EtudiantRequest> dialog = createStyledEtudiantDialog("➕ Ajouter un étudiant", null);

        Optional<EtudiantRequest> result = dialog.showAndWait();
        result.ifPresent(etudiant -> {
            System.out.println("➕ Ajout d'un nouvel étudiant: " + etudiant);
            statsLabel.setText("⏳ Ajout en cours...");
            statsLabel.setTextFill(Color.web("#F59E0B"));
            loadingIndicator.setVisible(true);

            // HACHER LE MOT DE PASSE AVANT L'ENVOI
            String originalPassword = etudiant.getPassword();
            if (originalPassword != null && !originalPassword.trim().isEmpty()) {
                String hashedPassword = hashPassword(originalPassword);
                etudiant.setPassword(hashedPassword);
                System.out.println("🔐 Mot de passe haché avant envoi");
            }

            etudiantService.addEtudiant(etudiant)
                    .thenAccept(message -> {
                        System.out.println("✅ " + message);
                        Platform.runLater(() -> {
                            loadingIndicator.setVisible(false);
                            showStyledAlert("Succès", "✅ Étudiant ajouté avec succès\n\n" +
                                    "• Nom: " + etudiant.getNom() + "\n" +
                                    "• Prénom: " + etudiant.getPrenom() + "\n" +
                                    "• Email: " + etudiant.getEmail() + "\n" +
                                    "• CIN: " + etudiant.getCin() + "\n\n" +
                                    message, Alert.AlertType.INFORMATION);
                            loadEtudiants();
                        });
                    })
                    .exceptionally(throwable -> {
                        System.err.println("❌ Erreur lors de l'ajout: " + throwable.getMessage());
                        Platform.runLater(() -> {
                            loadingIndicator.setVisible(false);
                            showStyledAlert("Erreur", "❌ Erreur lors de l'ajout: " + throwable.getMessage(), Alert.AlertType.ERROR);
                            loadEtudiants();
                        });
                        return null;
                    });
        });
    }

    private void showEditEtudiantDialog() {
        Map<String, Object> selectedEtudiant = tableView.getSelectionModel().getSelectedItem();

        if (selectedEtudiant == null) {
            showStyledAlert("Avertissement", "Veuillez sélectionner un étudiant à modifier", Alert.AlertType.WARNING);
            return;
        }

        System.out.println("✏️ Modification de l'étudiant: " + selectedEtudiant);

        // Convertir Map en EtudiantRequest
        EtudiantRequest etudiantRequest = mapToEtudiantRequest(selectedEtudiant);

        // S'assurer que l'ID est bien extrait
        Long etudiantId = ((Number) selectedEtudiant.get("id")).longValue();
        etudiantRequest.setId(etudiantId);

        System.out.println("🆔 Étudiant à modifier - ID: " + etudiantId + ", Nom: " + etudiantRequest.getNom());

        Dialog<EtudiantRequest> dialog = createStyledEtudiantDialog("✏️ Modifier l'étudiant", etudiantRequest);

        Optional<EtudiantRequest> result = dialog.showAndWait();
        result.ifPresent(etudiant -> {
            // S'assurer que l'ID est conservé
            etudiant.setId(etudiantId);
            statsLabel.setText("⏳ Modification en cours...");
            statsLabel.setTextFill(Color.web("#F59E0B"));
            loadingIndicator.setVisible(true);

            // HACHER LE NOUVEAU MOT DE PASSE SI FOURNI
            String newPassword = etudiant.getPassword();
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                String hashedPassword = hashPassword(newPassword);
                etudiant.setPassword(hashedPassword);
                System.out.println("🔐 Nouveau mot de passe haché avant envoi");
            } else {
                // Conserver l'ancien mot de passe (ne pas le modifier)
                etudiant.setPassword(null);
                System.out.println("ℹ️ Mot de passe non modifié");
            }

            System.out.println("🔄 Envoi de la modification - ID: " + etudiant.getId() + ", Nom: " + etudiant.getNom());

            etudiantService.updateEtudiant(etudiant)
                    .thenAccept(message -> {
                        System.out.println("✅ " + message);
                        Platform.runLater(() -> {
                            loadingIndicator.setVisible(false);
                            showStyledAlert("Succès", "✅ Étudiant modifié avec succès\n\n" +
                                    "• Nom: " + etudiant.getNom() + "\n" +
                                    "• Prénom: " + etudiant.getPrenom() + "\n" +
                                    "• Email: " + etudiant.getEmail() + "\n" +
                                    "• CIN: " + etudiant.getCin() + "\n\n" +
                                    message, Alert.AlertType.INFORMATION);
                            loadEtudiants();
                        });
                    })
                    .exceptionally(throwable -> {
                        System.err.println("❌ Erreur lors de la modification: " + throwable.getMessage());
                        Platform.runLater(() -> {
                            loadingIndicator.setVisible(false);
                            showStyledAlert("Erreur", "❌ Erreur lors de la modification: " + throwable.getMessage(), Alert.AlertType.ERROR);
                            loadEtudiants();
                        });
                        return null;
                    });
        });
    }

    private void deleteEtudiant() {
        Map<String, Object> selectedEtudiant = tableView.getSelectionModel().getSelectedItem();

        if (selectedEtudiant == null) {
            showStyledAlert("Avertissement", "Veuillez sélectionner un étudiant à supprimer", Alert.AlertType.WARNING);
            return;
        }

        Long etudiantId = ((Number) selectedEtudiant.get("id")).longValue();
        String etudiantNom = (String) selectedEtudiant.get("nom");
        String etudiantPrenom = (String) selectedEtudiant.get("prenom");
        String etudiantEmail = (String) selectedEtudiant.get("email");
        String etudiantCin = (String) selectedEtudiant.get("cin");

        // Boîte de dialogue de confirmation premium
        Alert confirmation = createStyledAlert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer l'étudiant \"" + etudiantPrenom + " " + etudiantNom + "\"");

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        Label detailsLabel = new Label("Détails de l'étudiant:");
        detailsLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #4B5563;");

        Label infoLabel = new Label("• Nom: " + etudiantNom +
                "\n• Prénom: " + etudiantPrenom +
                "\n• Email: " + etudiantEmail +
                "\n• CIN: " + etudiantCin);
        infoLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 13;");

        Label warningLabel = new Label("⚠️  Cette action est irréversible et supprimera toutes les données associées.");
        warningLabel.setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold; -fx-font-size: 12;");

        content.getChildren().addAll(detailsLabel, infoLabel, warningLabel);
        confirmation.getDialogPane().setContent(content);

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            statsLabel.setText("⏳ Suppression en cours...");
            statsLabel.setTextFill(Color.web("#F59E0B"));
            loadingIndicator.setVisible(true);

            etudiantService.deleteEtudiant(etudiantId)
                    .thenAccept(message -> {
                        Platform.runLater(() -> {
                            loadingIndicator.setVisible(false);
                            showStyledAlert("Succès", "✅ Étudiant supprimé avec succès\n\n" +
                                    "• Étudiant: " + etudiantPrenom + " " + etudiantNom + "\n" +
                                    "• Email: " + etudiantEmail + "\n" +
                                    "• " + message, Alert.AlertType.INFORMATION);
                            loadEtudiants();
                        });
                    })
                    .exceptionally(throwable -> {
                        Platform.runLater(() -> {
                            loadingIndicator.setVisible(false);
                            showStyledAlert("Erreur", "❌ Erreur lors de la suppression: " + throwable.getMessage(), Alert.AlertType.ERROR);
                            loadEtudiants();
                        });
                        return null;
                    });
        }
    }

    private Dialog<EtudiantRequest> createStyledEtudiantDialog(String title, EtudiantRequest etudiant) {
        Dialog<EtudiantRequest> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);

        // Style premium du dialog
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white; -fx-background-radius: 20; " +
                "-fx-border-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 25, 0, 0, 8);");

        // Boutons premium
        ButtonType saveButtonType = new ButtonType("💾 Sauvegarder", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Style des boutons
        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setStyle(createPremiumButtonStyle("#10B981", "#059669", "#047857"));

        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButton.setStyle(createPremiumButtonStyle("#6B7280", "#4B5563", "#374151"));

        // Formulaire premium
        VBox form = new VBox(20);
        form.setPadding(new Insets(30));
        form.setStyle("-fx-background-color: white;");

        // En-tête du formulaire
        Label formTitle = new Label(title.contains("Ajouter") ? "Nouvel Étudiant" : "Modifier l'Étudiant");
        formTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        formTitle.setTextFill(Color.web("#1F2937"));
        formTitle.setPadding(new Insets(0, 0, 10, 0));

        // Champs avec labels stylés
        VBox fieldsContainer = new VBox(15);

        TextField nomField = createPremiumTextField();
        TextField prenomField = createPremiumTextField();
        TextField emailField = createPremiumTextField();
        TextField cinField = createPremiumTextField();
        PasswordField passwordField = createPremiumPasswordField();

        // Pré-remplir si modification
        if (etudiant != null) {
            nomField.setText(etudiant.getNom());
            prenomField.setText(etudiant.getPrenom());
            emailField.setText(etudiant.getEmail());
            cinField.setText(etudiant.getCin());
            passwordField.setPromptText("Laisser vide pour conserver l'actuel");
            System.out.println("📝 Pré-remplissage du formulaire - ID: " + etudiant.getId());
        } else {
            passwordField.setPromptText("Mot de passe (obligatoire)");
        }

        fieldsContainer.getChildren().addAll(
                createPremiumFormLabel("Nom *"), nomField,
                createPremiumFormLabel("Prénom *"), prenomField,
                createPremiumFormLabel("Email *"), emailField,
                createPremiumFormLabel("CIN *"), cinField,
                createPremiumFormLabel(etudiant == null ? "Mot de passe *" : "Nouveau mot de passe"), passwordField
        );

        // Note informative
        Label noteLabel = new Label("* Champs obligatoires" +
                (etudiant == null ? "\n🔒 Le mot de passe doit contenir au moins 6 caractères avec majuscules, minuscules et chiffres" : ""));
        noteLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12; -fx-font-style: italic;");

        form.getChildren().addAll(formTitle, fieldsContainer, noteLabel);
        dialog.getDialogPane().setContent(form);

        // Validation
        Button saveButtonFinal = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButtonFinal.setDisable(true);

        // Validation avancée
        javafx.beans.value.ChangeListener<String> validation = (obs, oldVal, newVal) -> {
            boolean isValid = !nomField.getText().trim().isEmpty()
                    && !prenomField.getText().trim().isEmpty()
                    && !emailField.getText().trim().isEmpty()
                    && !cinField.getText().trim().isEmpty();

            // Validation email basique
            if (isValid) {
                String email = emailField.getText().trim();
                isValid = email.contains("@") && email.contains(".");
            }

            // Pour nouvel étudiant, le mot de passe est obligatoire
            if (etudiant == null) {
                String password = passwordField.getText().trim();
                isValid = isValid && !password.isEmpty() && isPasswordValid(password);
            }

            saveButtonFinal.setDisable(!isValid);
        };

        nomField.textProperty().addListener(validation);
        prenomField.textProperty().addListener(validation);
        emailField.textProperty().addListener(validation);
        cinField.textProperty().addListener(validation);
        passwordField.textProperty().addListener(validation);

        // Focus initial
        Platform.runLater(() -> {
            if (etudiant == null) {
                nomField.requestFocus();
            }
        });

        // Convertir le résultat
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    EtudiantRequest newEtudiant = new EtudiantRequest();

                    // Conserver l'ID si modification
                    if (etudiant != null && etudiant.getId() != null) {
                        newEtudiant.setId(etudiant.getId());
                        System.out.println("💾 Conservation de l'ID: " + etudiant.getId());
                    }

                    newEtudiant.setNom(nomField.getText().trim());
                    newEtudiant.setPrenom(prenomField.getText().trim());
                    newEtudiant.setEmail(emailField.getText().trim());
                    newEtudiant.setCin(cinField.getText().trim());

                    // Gestion du mot de passe
                    String password = passwordField.getText();
                    if (password != null && !password.trim().isEmpty()) {
                        newEtudiant.setPassword(password);
                    } else if (etudiant == null) {
                        // Pour un nouvel étudiant, générer un mot de passe par défaut sécurisé
                        newEtudiant.setPassword(generateDefaultPassword());
                    }

                    System.out.println("📤 Résultat du formulaire: " + newEtudiant);
                    return newEtudiant;

                } catch (Exception e) {
                    System.err.println("❌ Erreur lors de la création de l'étudiant: " + e.getMessage());
                    showStyledAlert("Erreur", "❌ Erreur lors de la création de l'étudiant: " + e.getMessage(), Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        return dialog;
    }

    private String createPremiumButtonStyle(String color, String hoverColor, String pressedColor) {
        return "-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 10; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 6, 0, 0, 2); " +
                "-fx-padding: 10 20; " +
                "-fx-font-weight: bold; " +
                "-fx-border-color: transparent; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 10;";
    }

    private TextField createPremiumTextField() {
        TextField textField = new TextField();
        textField.setStyle("-fx-background-color: #F9FAFB; -fx-background-radius: 8; " +
                "-fx-border-color: #D1D5DB; -fx-border-radius: 8; -fx-border-width: 2; " +
                "-fx-padding: 12 15; -fx-font-size: 14; -fx-font-weight: 500;");
        textField.setPrefHeight(45);

        // Effet de focus
        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                textField.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 8; " +
                        "-fx-border-color: #3B82F6; -fx-border-radius: 8; -fx-border-width: 2; " +
                        "-fx-padding: 12 15; -fx-font-size: 14; -fx-font-weight: 500; " +
                        "-fx-effect: dropshadow(gaussian, rgba(59,130,246,0.2), 10, 0, 0, 0);");
            } else {
                textField.setStyle("-fx-background-color: #F9FAFB; -fx-background-radius: 8; " +
                        "-fx-border-color: #D1D5DB; -fx-border-radius: 8; -fx-border-width: 2; " +
                        "-fx-padding: 12 15; -fx-font-size: 14; -fx-font-weight: 500;");
            }
        });

        return textField;
    }

    private PasswordField createPremiumPasswordField() {
        PasswordField passwordField = new PasswordField();
        passwordField.setStyle("-fx-background-color: #F9FAFB; -fx-background-radius: 8; " +
                "-fx-border-color: #D1D5DB; -fx-border-radius: 8; -fx-border-width: 2; " +
                "-fx-padding: 12 15; -fx-font-size: 14; -fx-font-weight: 500;");
        passwordField.setPrefHeight(45);

        // Effet de focus
        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                passwordField.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 8; " +
                        "-fx-border-color: #3B82F6; -fx-border-radius: 8; -fx-border-width: 2; " +
                        "-fx-padding: 12 15; -fx-font-size: 14; -fx-font-weight: 500; " +
                        "-fx-effect: dropshadow(gaussian, rgba(59,130,246,0.2), 10, 0, 0, 0);");
            } else {
                passwordField.setStyle("-fx-background-color: #F9FAFB; -fx-background-radius: 8; " +
                        "-fx-border-color: #D1D5DB; -fx-border-radius: 8; -fx-border-width: 2; " +
                        "-fx-padding: 12 15; -fx-font-size: 14; -fx-font-weight: 500;");
            }
        });

        return passwordField;
    }

    private Label createPremiumFormLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        label.setTextFill(Color.web("#374151"));
        return label;
    }

    // === MÉTHODES EXISTANTES (améliorées) ===

    private String hashPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return null;
        }
        try {
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            System.out.println("🔐 Mot de passe haché avec BCrypt: " + hashedPassword);
            return hashedPassword;
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du hachage BCrypt: " + e.getMessage());
            return null;
        }
    }

    private boolean checkPassword(String password, String hashedPassword) {
        if (password == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(password, hashedPassword);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la vérification du mot de passe: " + e.getMessage());
            return false;
        }
    }

    private boolean isPasswordValid(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        boolean hasUpperCase = !password.equals(password.toLowerCase());
        boolean hasLowerCase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");
        return hasUpperCase && hasLowerCase && hasDigit;
    }

    private String generateDefaultPassword() {
        String defaultPassword = "Etudiant123!";
        System.out.println("🔑 Génération d'un mot de passe par défaut sécurisé");
        return defaultPassword;
    }

    private EtudiantRequest mapToEtudiantRequest(Map<String, Object> map) {
        EtudiantRequest etudiant = new EtudiantRequest();

        // Extraire l'ID
        Object idObj = map.get("id");
        if (idObj instanceof Number) {
            etudiant.setId(((Number) idObj).longValue());
        }

        etudiant.setNom((String) map.get("nom"));
        etudiant.setPrenom((String) map.get("prenom"));
        etudiant.setEmail((String) map.get("email"));
        etudiant.setCin((String) map.get("cin"));

        System.out.println("🗺️ Conversion Map -> EtudiantRequest: " + etudiant);
        return etudiant;
    }

    private void showStyledAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Style premium de l'alerte
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white; -fx-background-radius: 20; " +
                "-fx-border-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 25, 0, 0, 8); " +
                "-fx-padding: 20;");

        // Style des boutons dans l'alerte
        for (ButtonType buttonType : alert.getDialogPane().getButtonTypes()) {
            Button button = (Button) alert.getDialogPane().lookupButton(buttonType);
            if (buttonType.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                button.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 8 16;");
            } else {
                button.setStyle("-fx-background-color: #6B7280; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 8 16;");
            }
        }

        alert.showAndWait();
    }

    private Alert createStyledAlert(Alert.AlertType type) {
        Alert alert = new Alert(type);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white; -fx-background-radius: 20; " +
                "-fx-border-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 5);");
        return alert;
    }
}
/*package tn.iset.m2glnt.client.viewer.view;

import org.mindrot.jbcrypt.BCrypt;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import tn.iset.m2glnt.client.service.EtudiantRestService;
import tn.iset.m2glnt.client.service.dto.EtudiantRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EtudiantView extends BorderPane {
    private final EtudiantRestService etudiantService;
    private TableView<Map<String, Object>> tableView;
    private ObservableList<Map<String, Object>> etudiantsData;

    public EtudiantView() {
        this.etudiantService = new EtudiantRestService();
        this.etudiantsData = FXCollections.observableArrayList();
        initializeView();
        loadEtudiants();
    }

    private void initializeView() {
        // Configuration de la table
        tableView = new TableView<>();
        tableView.setItems(etudiantsData);

        // Colonnes de la table
        TableColumn<Map<String, Object>, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> {
            Map<String, Object> row = data.getValue();
            Object idValue = row.get("id");
            if (idValue instanceof Number) {
                return new javafx.beans.property.SimpleLongProperty(((Number) idValue).longValue()).asObject();
            }
            return new javafx.beans.property.SimpleLongProperty(0L).asObject();
        });

        TableColumn<Map<String, Object>, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(data -> {
            Map<String, Object> row = data.getValue();
            Object value = row.get("nom");
            return new javafx.beans.property.SimpleStringProperty(value != null ? value.toString() : "");
        });

        TableColumn<Map<String, Object>, String> prenomCol = new TableColumn<>("Prénom");
        prenomCol.setCellValueFactory(data -> {
            Map<String, Object> row = data.getValue();
            Object value = row.get("prenom");
            return new javafx.beans.property.SimpleStringProperty(value != null ? value.toString() : "");
        });

        TableColumn<Map<String, Object>, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> {
            Map<String, Object> row = data.getValue();
            Object value = row.get("email");
            return new javafx.beans.property.SimpleStringProperty(value != null ? value.toString() : "");
        });

        TableColumn<Map<String, Object>, String> cinCol = new TableColumn<>("CIN");
        cinCol.setCellValueFactory(data -> {
            Map<String, Object> row = data.getValue();
            Object value = row.get("cin");
            return new javafx.beans.property.SimpleStringProperty(value != null ? value.toString() : "");
        });

        // Ajouter les colonnes à la table
        tableView.getColumns().addAll(idCol, nomCol, prenomCol, emailCol, cinCol);

        // Boutons CRUD
        Button addButton = new Button("Ajouter");
        addButton.setOnAction(e -> showAddEtudiantDialog());

        Button editButton = new Button("Modifier");
        editButton.setOnAction(e -> showEditEtudiantDialog());

        Button deleteButton = new Button("Supprimer");
        deleteButton.setOnAction(e -> deleteEtudiant());

        Button refreshButton = new Button("Actualiser");
        refreshButton.setOnAction(e -> loadEtudiants());

        // Layout des boutons
        HBox buttonBox = new HBox(10, addButton, editButton, deleteButton, refreshButton);
        buttonBox.setPadding(new Insets(10));

        // Layout principal
        VBox mainBox = new VBox(10, buttonBox, tableView);
        mainBox.setPadding(new Insets(10));

        setCenter(mainBox);
    }


    private String hashPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return null;
        }

        try {
            // Générer un sel et hacher le mot de passe avec BCrypt
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            System.out.println("🔐 Mot de passe haché avec BCrypt: " + hashedPassword);
            return hashedPassword;

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du hachage BCrypt: " + e.getMessage());
            return null;
        }
    }


    private boolean checkPassword(String password, String hashedPassword) {
        if (password == null || hashedPassword == null) {
            return false;
        }

        try {
            return BCrypt.checkpw(password, hashedPassword);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la vérification du mot de passe: " + e.getMessage());
            return false;
        }
    }


    private boolean isPasswordValid(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }

        // Vérifier la présence d'au moins une majuscule, une minuscule et un chiffre
        boolean hasUpperCase = !password.equals(password.toLowerCase());
        boolean hasLowerCase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");

        return hasUpperCase && hasLowerCase && hasDigit;
    }


    private String generateDefaultPassword() {
        String defaultPassword = "Etudiant123!";
        System.out.println("🔑 Génération d'un mot de passe par défaut sécurisé");
        return defaultPassword;
    }

    private void loadEtudiants() {
        System.out.println("🔄 Chargement des étudiants...");
        etudiantService.getAllEtudiants()
                .thenAccept(etudiants -> {
                    System.out.println("✅ " + etudiants.size() + " étudiants chargés");
                    Platform.runLater(() -> {
                        etudiantsData.clear();
                        etudiantsData.addAll(etudiants);
                        System.out.println("📊 Données affichées dans la table: " + etudiantsData.size() + " éléments");

                        // Debug: afficher les IDs
                        etudiants.forEach(etudiant -> {
                            Object id = etudiant.get("id");
                            Object nom = etudiant.get("nom");
                            Object prenom = etudiant.get("prenom");
                            System.out.println("   - Étudiant ID: " + id + ", Nom: " + nom + " " + prenom);
                        });
                    });
                })
                .exceptionally(throwable -> {
                    System.err.println("❌ Erreur lors du chargement: " + throwable.getMessage());
                    Platform.runLater(() ->
                            showAlert("Erreur", "Erreur lors du chargement des étudiants: " + throwable.getMessage()));
                    return null;
                });
    }

    private void showAddEtudiantDialog() {
        Dialog<EtudiantRequest> dialog = createEtudiantDialog("Ajouter un étudiant", null);

        Optional<EtudiantRequest> result = dialog.showAndWait();
        result.ifPresent(etudiant -> {
            System.out.println("➕ Ajout d'un nouvel étudiant: " + etudiant);

            // HACHER LE MOT DE PASSE AVANT L'ENVOI
            String originalPassword = etudiant.getPassword();
            if (originalPassword != null && !originalPassword.trim().isEmpty()) {
                String hashedPassword = hashPassword(originalPassword);
                etudiant.setPassword(hashedPassword);
                System.out.println("🔐 Mot de passe haché avant envoi");
            }

            etudiantService.addEtudiant(etudiant)
                    .thenAccept(message -> {
                        System.out.println("✅ " + message);
                        Platform.runLater(() -> {
                            showAlert("Succès", message);
                            loadEtudiants(); // Recharger la liste
                        });
                    })
                    .exceptionally(throwable -> {
                        System.err.println("❌ Erreur lors de l'ajout: " + throwable.getMessage());
                        Platform.runLater(() -> {
                            showAlert("Erreur", "Erreur lors de l'ajout: " + throwable.getMessage());
                        });
                        return null;
                    });
        });
    }

    private void showEditEtudiantDialog() {
        Map<String, Object> selectedEtudiant = tableView.getSelectionModel().getSelectedItem();

        if (selectedEtudiant == null) {
            showAlert("Avertissement", "Veuillez sélectionner un étudiant à modifier");
            return;
        }

        System.out.println("✏️ Modification de l'étudiant: " + selectedEtudiant);

        // Convertir Map en EtudiantRequest
        EtudiantRequest etudiantRequest = mapToEtudiantRequest(selectedEtudiant);

        // S'assurer que l'ID est bien extrait
        Long etudiantId = ((Number) selectedEtudiant.get("id")).longValue();
        etudiantRequest.setId(etudiantId);

        System.out.println("🆔 Étudiant à modifier - ID: " + etudiantId + ", Nom: " + etudiantRequest.getNom());

        Dialog<EtudiantRequest> dialog = createEtudiantDialog("Modifier l'étudiant", etudiantRequest);

        Optional<EtudiantRequest> result = dialog.showAndWait();
        result.ifPresent(etudiant -> {
            // S'assurer que l'ID est conservé
            etudiant.setId(etudiantId);

            // HACHER LE NOUVEAU MOT DE PASSE SI FOURNI
            String newPassword = etudiant.getPassword();
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                String hashedPassword = hashPassword(newPassword);
                etudiant.setPassword(hashedPassword);
                System.out.println("🔐 Nouveau mot de passe haché avant envoi");
            } else {
                // Conserver l'ancien mot de passe (ne pas le modifier)
                etudiant.setPassword(null);
                System.out.println("ℹ️ Mot de passe non modifié");
            }

            System.out.println("🔄 Envoi de la modification - ID: " + etudiant.getId() + ", Nom: " + etudiant.getNom());

            etudiantService.updateEtudiant(etudiant)
                    .thenAccept(message -> {
                        System.out.println("✅ " + message);
                        Platform.runLater(() -> {
                            showAlert("Succès", message);
                            loadEtudiants();
                        });
                    })
                    .exceptionally(throwable -> {
                        System.err.println("❌ Erreur lors de la modification: " + throwable.getMessage());
                        Platform.runLater(() -> {
                            showAlert("Erreur", "Erreur lors de la modification: " + throwable.getMessage());
                        });
                        return null;
                    });
        });
    }

    private void deleteEtudiant() {
        Map<String, Object> selectedEtudiant = tableView.getSelectionModel().getSelectedItem();

        if (selectedEtudiant == null) {
            showAlert("Avertissement", "Veuillez sélectionner un étudiant à supprimer");
            return;
        }

        // Confirmation de suppression
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer l'étudiant");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cet étudiant ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Long etudiantId = ((Number) selectedEtudiant.get("id")).longValue();

            etudiantService.deleteEtudiant(etudiantId)
                    .thenAccept(message -> {
                        Platform.runLater(() -> {
                            showAlert("Succès", message);
                            loadEtudiants();
                        });
                    })
                    .exceptionally(throwable -> {
                        Platform.runLater(() -> {
                            showAlert("Erreur", "Erreur lors de la suppression: " + throwable.getMessage());
                        });
                        return null;
                    });
        }
    }

    private Dialog<EtudiantRequest> createEtudiantDialog(String title, EtudiantRequest etudiant) {
        Dialog<EtudiantRequest> dialog = new Dialog<>();
        dialog.setTitle(title);

        // Boutons
        ButtonType saveButtonType = new ButtonType("Sauvegarder", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Formulaire
        VBox form = new VBox(10);
        form.setPadding(new Insets(20));

        TextField nomField = new TextField();
        TextField prenomField = new TextField();
        TextField emailField = new TextField();
        TextField cinField = new TextField();
        PasswordField passwordField = new PasswordField();

        // Pré-remplir si modification
        if (etudiant != null) {
            nomField.setText(etudiant.getNom());
            prenomField.setText(etudiant.getPrenom());
            emailField.setText(etudiant.getEmail());
            cinField.setText(etudiant.getCin());
            passwordField.setPromptText("Laisser vide pour conserver l'actuel");
            System.out.println("📝 Pré-remplissage du formulaire - ID: " + etudiant.getId());
        } else {
            passwordField.setPromptText("Mot de passe (obligatoire)");
        }

        form.getChildren().addAll(
                new Label("Nom:"), nomField,
                new Label("Prénom:"), prenomField,
                new Label("Email:"), emailField,
                new Label("CIN:"), cinField,
                new Label("Mot de passe:"), passwordField
        );

        dialog.getDialogPane().setContent(form);

        // Validation basique - seulement les champs obligatoires
        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);

        // Désactiver initialement
        saveButton.setDisable(true);

        // Validation simple
        javafx.beans.value.ChangeListener<String> simpleValidation = (obs, oldVal, newVal) -> {
            boolean isValid = !nomField.getText().trim().isEmpty()
                    && !prenomField.getText().trim().isEmpty()
                    && !emailField.getText().trim().isEmpty();

            // Pour nouvel étudiant, le mot de passe est obligatoire
            if (etudiant == null) {
                isValid = isValid && !passwordField.getText().trim().isEmpty();
            }

            saveButton.setDisable(!isValid);
        };

        nomField.textProperty().addListener(simpleValidation);
        prenomField.textProperty().addListener(simpleValidation);
        emailField.textProperty().addListener(simpleValidation);
        passwordField.textProperty().addListener(simpleValidation);

        // Convertir le résultat
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    EtudiantRequest newEtudiant = new EtudiantRequest();

                    // Conserver l'ID si modification
                    if (etudiant != null && etudiant.getId() != null) {
                        newEtudiant.setId(etudiant.getId());
                        System.out.println("💾 Conservation de l'ID: " + etudiant.getId());
                    }

                    newEtudiant.setNom(nomField.getText());
                    newEtudiant.setPrenom(prenomField.getText());
                    newEtudiant.setEmail(emailField.getText());
                    newEtudiant.setCin(cinField.getText());

                    // Gestion du mot de passe
                    String password = passwordField.getText();
                    if (password != null && !password.trim().isEmpty()) {
                        newEtudiant.setPassword(password);
                    } else if (etudiant == null) {
                        // Pour un nouvel étudiant, générer un mot de passe par défaut sécurisé
                        newEtudiant.setPassword(generateDefaultPassword());
                    }

                    System.out.println("📤 Résultat du formulaire: " + newEtudiant);
                    return newEtudiant;

                } catch (Exception e) {
                    System.err.println("❌ Erreur lors de la création de l'étudiant: " + e.getMessage());
                    showAlert("Erreur", "Erreur lors de la création de l'étudiant: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        return dialog;
    }

    // Méthode utilitaire pour convertir Map en EtudiantRequest
    private EtudiantRequest mapToEtudiantRequest(Map<String, Object> map) {
        EtudiantRequest etudiant = new EtudiantRequest();

        // Extraire l'ID
        Object idObj = map.get("id");
        if (idObj instanceof Number) {
            etudiant.setId(((Number) idObj).longValue());
        }

        etudiant.setNom((String) map.get("nom"));
        etudiant.setPrenom((String) map.get("prenom"));
        etudiant.setEmail((String) map.get("email"));
        etudiant.setCin((String) map.get("cin"));

        System.out.println("🗺️ Conversion Map -> EtudiantRequest: " + etudiant);
        return etudiant;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}*/