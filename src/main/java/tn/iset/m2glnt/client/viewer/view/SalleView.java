package tn.iset.m2glnt.client.viewer.view;

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
import tn.iset.m2glnt.client.service.SalleRestService;
import tn.iset.m2glnt.client.service.dto.SalleRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SalleView extends BorderPane {
    private final SalleRestService salleService;
    private TableView<Map<String, Object>> tableView;
    private ObservableList<Map<String, Object>> sallesData;
    private Label titleLabel;
    private Label statsLabel;
    private ProgressIndicator loadingIndicator;

    public SalleView() {
        this.salleService = new SalleRestService();
        this.sallesData = FXCollections.observableArrayList();
        initializeView();
        loadSalles();
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

        Label iconLabel = new Label("🏢");
        iconLabel.setFont(Font.font("System", 36));
        iconLabel.setTextFill(Color.WHITE);

        titleLabel = new Label("Gestion des Salles");
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
        addButton.setOnAction(e -> showAddSalleDialog());

        Button editButton = createPremiumButton("✏️ Modifier", "#3B82F6", "#2563EB", "#1D4ED8");
        editButton.setOnAction(e -> showEditSalleDialog());

        Button deleteButton = createPremiumButton("🗑️ Supprimer", "#EF4444", "#DC2626", "#B91C1C");
        deleteButton.setOnAction(e -> deleteSalle());

        Button refreshButton = createPremiumButton("🔄 Actualiser", "#8B5CF6", "#7C3AED", "#6D28D9");
        refreshButton.setOnAction(e -> loadSalles());

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

        Label tableTitle = new Label("📋 Liste des Salles");
        tableTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        tableTitle.setTextFill(Color.web("#2D3748"));

        tableHeader.getChildren().add(tableTitle);

        // Configuration de la table avec style premium
        tableView = new TableView<>();
        tableView.setStyle("-fx-background-color: transparent; -fx-border-radius: 12; " +
                "-fx-background-radius: 12; -fx-border-color: #E2E8F0; -fx-border-width: 2;");
        tableView.setItems(sallesData);

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
        TableColumn<Map<String, Object>, String> nomCol = createPremiumColumn("Nom", "nom", 220);
        TableColumn<Map<String, Object>, String> batimentCol = createPremiumColumn("Bâtiment", "batiment", 180);
        TableColumn<Map<String, Object>, String> capaciteCol = createPremiumColumn("Capacité", "capacite", 120);

        tableView.getColumns().addAll(idCol, nomCol, batimentCol, capaciteCol);

        // Message quand la table est vide avec design amélioré
        StackPane placeholderContainer = new StackPane();
        placeholderContainer.setPadding(new Insets(40));

        VBox placeholderContent = new VBox(15);
        placeholderContent.setAlignment(Pos.CENTER);

        Label placeholderIcon = new Label("🏢");
        placeholderIcon.setFont(Font.font("System", 48));

        Label placeholderText = new Label("Aucune salle disponible");
        placeholderText.setFont(Font.font("System", FontWeight.MEDIUM, 16));
        placeholderText.setTextFill(Color.web("#718096"));

        Label placeholderSubtext = new Label("Cliquez sur 'Ajouter' pour créer une nouvelle salle");
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

                    // Ajouter des icônes conditionnelles pour certaines colonnes
                    if ("capacite".equals(property) && item instanceof String) {
                        try {
                            int capacity = Integer.parseInt(item.toString());
                            if (capacity > 50) {
                                setStyle("-fx-alignment: CENTER; -fx-font-family: 'System'; " +
                                        "-fx-text-fill: #059669; -fx-font-size: 13; " +
                                        "-fx-font-weight: 600; -fx-padding: 12 5;");
                            }
                        } catch (NumberFormatException e) {
                            // Ignorer si ce n'est pas un nombre
                        }
                    }
                }
            }
        });

        return column;
    }

    private void loadSalles() {
        System.out.println("🔄 Chargement des salles...");

        // Animation de chargement améliorée
        statsLabel.setText("⏳ Chargement en cours...");
        statsLabel.setTextFill(Color.web("#FBBF24"));
        loadingIndicator.setVisible(true);
        loadingIndicator.setProgress(-1); // Indéterminé

        salleService.getAllSalles()
                .thenAccept(salles -> {
                    System.out.println("✅ " + salles.size() + " salles chargées");
                    Platform.runLater(() -> {
                        sallesData.clear();
                        sallesData.addAll(salles);

                        // Mise à jour des statistiques
                        updateStats(salles.size());
                        loadingIndicator.setVisible(false);

                        System.out.println("📊 Données affichées dans la table: " + sallesData.size() + " éléments");

                        // Debug des IDs
                        salles.forEach(salle -> {
                            Object id = salle.get("id");
                            Object nom = salle.get("nom");
                            Object batiment = salle.get("batiment");
                            Object capacite = salle.get("capacite");
                            System.out.println("   - Salle ID: " + id + ", Nom: " + nom +
                                    ", Bâtiment: " + batiment + ", Capacité: " + capacite);
                        });
                    });
                })
                .exceptionally(throwable -> {
                    System.err.println("❌ Erreur lors du chargement: " + throwable.getMessage());
                    Platform.runLater(() -> {
                        statsLabel.setText("❌ Erreur de chargement");
                        statsLabel.setTextFill(Color.web("#EF4444"));
                        loadingIndicator.setVisible(false);
                        showStyledAlert("Erreur", "Erreur lors du chargement des salles: " + throwable.getMessage(), Alert.AlertType.ERROR);
                    });
                    return null;
                });
    }

    private void updateStats(int count) {
        String statText;
        Color textColor;

        if (count == 0) {
            statText = "📭 Aucune salle disponible";
            textColor = Color.web("#EF4444");
        } else if (count == 1) {
            statText = "✅ 1 salle configurée";
            textColor = Color.web("#10B981");
        } else {
            statText = "🏢 " + count + " salles disponibles";
            textColor = Color.web("#10B981");
        }

        statsLabel.setText(statText);
        statsLabel.setTextFill(textColor);
    }

    private void deleteSalle() {
        Map<String, Object> selectedSalle = tableView.getSelectionModel().getSelectedItem();

        if (selectedSalle == null) {
            showStyledAlert("Avertissement", "Veuillez sélectionner une salle à supprimer", Alert.AlertType.WARNING);
            return;
        }

        Long salleId = ((Number) selectedSalle.get("id")).longValue();
        String salleName = (String) selectedSalle.get("nom");
        String batiment = (String) selectedSalle.get("batiment");
        Object capaciteObj = selectedSalle.get("capacite");
        String capacite = capaciteObj != null ? capaciteObj.toString() : "N/A";

        System.out.println("🔍 Vérification avant suppression de la salle ID: " + salleId);

        // Boîte de dialogue de confirmation premium
        Alert confirmation = createStyledAlert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer la salle \"" + salleName + "\"");

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        Label detailsLabel = new Label("Détails de la salle:");
        detailsLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #4B5563;");

        Label infoLabel = new Label("• Nom: " + salleName +
                "\n• Bâtiment: " + batiment +
                "\n• Capacité: " + capacite + " places");
        infoLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 13;");

        Label warningLabel = new Label("⚠️  Cette action est irréversible et dissociera tous les créneaux associés.");
        warningLabel.setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold; -fx-font-size: 12;");

        content.getChildren().addAll(detailsLabel, infoLabel, warningLabel);
        confirmation.getDialogPane().setContent(content);

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.out.println("⏳ Suppression en cours...");
            statsLabel.setText("⏳ Suppression en cours...");
            statsLabel.setTextFill(Color.web("#F59E0B"));
            loadingIndicator.setVisible(true);

            salleService.deleteSalle(salleId)
                    .thenAccept(message -> {
                        Platform.runLater(() -> {
                            System.out.println("✅ Suppression terminée");
                            loadingIndicator.setVisible(false);
                            showStyledAlert("Succès", "✅ Salle supprimée avec succès\n\n" +
                                    "• Salle: " + salleName + "\n" +
                                    "• Bâtiment: " + batiment + "\n" +
                                    "• " + message, Alert.AlertType.INFORMATION);
                            loadSalles();
                        });
                    })
                    .exceptionally(throwable -> {
                        Platform.runLater(() -> {
                            System.out.println("❌ Erreur de suppression");
                            loadingIndicator.setVisible(false);
                            showStyledAlert("Erreur", "❌ Erreur lors de la suppression:\n" +
                                    throwable.getMessage(), Alert.AlertType.ERROR);
                            loadSalles(); // Recharger quand même pour actualiser
                        });
                        return null;
                    });
        }
    }

    private void showAddSalleDialog() {
        Dialog<SalleRequest> dialog = createStyledSalleDialog("➕ Ajouter une salle", null);

        Optional<SalleRequest> result = dialog.showAndWait();
        result.ifPresent(salle -> {
            System.out.println("➕ Ajout d'une nouvelle salle: " + salle);
            statsLabel.setText("⏳ Ajout en cours...");
            statsLabel.setTextFill(Color.web("#F59E0B"));
            loadingIndicator.setVisible(true);

            salleService.addSalle(salle)
                    .thenAccept(message -> {
                        Platform.runLater(() -> {
                            loadingIndicator.setVisible(false);
                            showStyledAlert("Succès", "✅ Salle ajoutée avec succès\n\n" +
                                    "• Nom: " + salle.getNom() + "\n" +
                                    "• Bâtiment: " + salle.getBatiment() + "\n" +
                                    "• Capacité: " + salle.getCapacite() + " places\n\n" +
                                    message, Alert.AlertType.INFORMATION);
                            loadSalles();
                        });
                    })
                    .exceptionally(throwable -> {
                        Platform.runLater(() -> {
                            loadingIndicator.setVisible(false);
                            showStyledAlert("Erreur", "❌ Erreur lors de l'ajout: " + throwable.getMessage(), Alert.AlertType.ERROR);
                            loadSalles();
                        });
                        return null;
                    });
        });
    }

    private void showEditSalleDialog() {
        Map<String, Object> selectedSalle = tableView.getSelectionModel().getSelectedItem();

        if (selectedSalle == null) {
            showStyledAlert("Avertissement", "Veuillez sélectionner une salle à modifier", Alert.AlertType.WARNING);
            return;
        }

        System.out.println("✏️ Modification de la salle: " + selectedSalle);

        SalleRequest salleRequest = mapToSalleRequest(selectedSalle);
        Long salleId = ((Number) selectedSalle.get("id")).longValue();
        salleRequest.setId(salleId);

        System.out.println("🆔 Salle à modifier - ID: " + salleId + ", Nom: " + salleRequest.getNom());

        Dialog<SalleRequest> dialog = createStyledSalleDialog("✏️ Modifier la salle", salleRequest);

        Optional<SalleRequest> result = dialog.showAndWait();
        result.ifPresent(salle -> {
            salle.setId(salleId);
            System.out.println("🔄 Envoi de la modification - ID: " + salle.getId() + ", Nom: " + salle.getNom());
            statsLabel.setText("⏳ Modification en cours...");
            statsLabel.setTextFill(Color.web("#F59E0B"));
            loadingIndicator.setVisible(true);

            salleService.updateSalle(salle)
                    .thenAccept(message -> {
                        System.out.println("✅ " + message);
                        Platform.runLater(() -> {
                            loadingIndicator.setVisible(false);
                            showStyledAlert("Succès", "✅ Salle modifiée avec succès\n\n" +
                                    "• Nom: " + salle.getNom() + "\n" +
                                    "• Bâtiment: " + salle.getBatiment() + "\n" +
                                    "• Capacité: " + salle.getCapacite() + " places\n\n" +
                                    message, Alert.AlertType.INFORMATION);
                            loadSalles();
                        });
                    })
                    .exceptionally(throwable -> {
                        System.err.println("❌ Erreur lors de la modification: " + throwable.getMessage());
                        Platform.runLater(() -> {
                            loadingIndicator.setVisible(false);
                            showStyledAlert("Erreur", "❌ Erreur lors de la modification: " + throwable.getMessage(), Alert.AlertType.ERROR);
                            loadSalles();
                        });
                        return null;
                    });
        });
    }

    private Dialog<SalleRequest> createStyledSalleDialog(String title, SalleRequest salle) {
        Dialog<SalleRequest> dialog = new Dialog<>();
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
        Label formTitle = new Label(title.contains("Ajouter") ? "Nouvelle Salle" : "Modifier la Salle");
        formTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        formTitle.setTextFill(Color.web("#1F2937"));
        formTitle.setPadding(new Insets(0, 0, 10, 0));

        // Champs avec labels stylés
        VBox fieldsContainer = new VBox(15);

        TextField nomField = createPremiumTextField();
        TextField batimentField = createPremiumTextField();
        TextField capaciteField = createPremiumTextField();

        // Pré-remplir si modification
        if (salle != null) {
            nomField.setText(salle.getNom());
            batimentField.setText(salle.getBatiment());
            capaciteField.setText(salle.getCapacite() != null ? salle.getCapacite().toString() : "");
        }

        fieldsContainer.getChildren().addAll(
                createPremiumFormLabel("Nom de la salle *"), nomField,
                createPremiumFormLabel("Bâtiment *"), batimentField,
                createPremiumFormLabel("Capacité (nombre de places) *"), capaciteField
        );

        // Note informative
        Label noteLabel = new Label("* Champs obligatoires");
        noteLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12; -fx-font-style: italic;");

        form.getChildren().addAll(formTitle, fieldsContainer, noteLabel);
        dialog.getDialogPane().setContent(form);

        // Validation
        Button saveButtonFinal = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButtonFinal.setDisable(true);

        // Validation avancée
        javafx.beans.value.ChangeListener<String> validation = (obs, oldVal, newVal) -> {
            boolean isValid = !nomField.getText().trim().isEmpty()
                    && !batimentField.getText().trim().isEmpty()
                    && !capaciteField.getText().trim().isEmpty();

            // Validation de la capacité
            if (isValid) {
                try {
                    int capaciteValue = Integer.parseInt(capaciteField.getText().trim());
                    isValid = capaciteValue > 0;
                } catch (NumberFormatException e) {
                    isValid = false;
                }
            }

            saveButtonFinal.setDisable(!isValid);
        };

        nomField.textProperty().addListener(validation);
        batimentField.textProperty().addListener(validation);
        capaciteField.textProperty().addListener(validation);

        // Focus initial
        Platform.runLater(() -> {
            if (salle == null) {
                nomField.requestFocus();
            }
        });

        // Convertir le résultat
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    SalleRequest newSalle = new SalleRequest();

                    if (salle != null && salle.getId() != null) {
                        newSalle.setId(salle.getId());
                    }

                    newSalle.setNom(nomField.getText().trim());
                    newSalle.setBatiment(batimentField.getText().trim());

                    String capaciteText = capaciteField.getText().trim();
                    if (!capaciteText.isEmpty()) {
                        newSalle.setCapacite(Integer.parseInt(capaciteText));
                    } else {
                        newSalle.setCapacite(0);
                    }

                    // Validation finale
                    if (newSalle.getNom().isEmpty()) {
                        showStyledAlert("Erreur", "Le nom de la salle est obligatoire", Alert.AlertType.ERROR);
                        return null;
                    }

                    if (newSalle.getBatiment().isEmpty()) {
                        showStyledAlert("Erreur", "Le bâtiment est obligatoire", Alert.AlertType.ERROR);
                        return null;
                    }

                    if (newSalle.getCapacite() <= 0) {
                        showStyledAlert("Erreur", "La capacité doit être un nombre positif", Alert.AlertType.ERROR);
                        return null;
                    }

                    System.out.println("📤 Résultat du formulaire: " + newSalle);
                    return newSalle;

                } catch (NumberFormatException e) {
                    showStyledAlert("Erreur", "La capacité doit être un nombre valide", Alert.AlertType.ERROR);
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

    private Label createPremiumFormLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        label.setTextFill(Color.web("#374151"));
        return label;
    }

    private SalleRequest mapToSalleRequest(Map<String, Object> map) {
        SalleRequest salle = new SalleRequest();

        Object idObj = map.get("id");
        if (idObj instanceof Number) {
            salle.setId(((Number) idObj).longValue());
        }

        salle.setNom((String) map.get("nom"));
        salle.setBatiment((String) map.get("batiment"));

        Object capaciteObj = map.get("capacite");
        if (capaciteObj instanceof Integer) {
            salle.setCapacite((Integer) capaciteObj);
        } else if (capaciteObj instanceof String) {
            try {
                salle.setCapacite(Integer.parseInt((String) capaciteObj));
            } catch (NumberFormatException e) {
                salle.setCapacite(0);
            }
        } else if (capaciteObj instanceof Number) {
            salle.setCapacite(((Number) capaciteObj).intValue());
        } else {
            salle.setCapacite(0);
        }

        System.out.println("🗺️ Conversion Map -> SalleRequest: " + salle);
        return salle;
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

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import tn.iset.m2glnt.client.service.SalleRestService;
import tn.iset.m2glnt.client.service.dto.SalleRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SalleView extends BorderPane {
    private final SalleRestService salleService;
    private TableView<Map<String, Object>> tableView;
    private ObservableList<Map<String, Object>> sallesData;

    public SalleView() {
        this.salleService = new SalleRestService();
        this.sallesData = FXCollections.observableArrayList();
        initializeView();
        loadSalles();
    }

    private void initializeView() {
        // Configuration de la table
        tableView = new TableView<>();
        tableView.setItems(sallesData);

        // Création des colonnes avec lambda expressions
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

        TableColumn<Map<String, Object>, String> batimentCol = new TableColumn<>("Bâtiment"); // 🔥 Changé de "Équipement" à "Bâtiment"
        batimentCol.setCellValueFactory(data -> {
            Map<String, Object> row = data.getValue();
            Object value = row.get("batiment"); // 🔥 Changé de "equipement" à "batiment"
            return new javafx.beans.property.SimpleStringProperty(value != null ? value.toString() : "");
        });

        TableColumn<Map<String, Object>, String> capaciteCol = new TableColumn<>("Capacité");
        capaciteCol.setCellValueFactory(data -> {
            Map<String, Object> row = data.getValue();
            Object value = row.get("capacite");
            return new javafx.beans.property.SimpleStringProperty(value != null ? value.toString() : "");
        });

        // 🔥 SUPPRIMEZ la colonne "Équipement" et AJOUTEZ "Bâtiment"
        // Ajouter les colonnes à la table
        tableView.getColumns().addAll(idCol, nomCol, batimentCol, capaciteCol);

        // Boutons CRUD
        Button addButton = new Button("Ajouter");
        addButton.setOnAction(e -> showAddSalleDialog());

        Button editButton = new Button("Modifier");
        editButton.setOnAction(e -> showEditSalleDialog());

        Button deleteButton = new Button("Supprimer");
        deleteButton.setOnAction(e -> deleteSalle());

        Button refreshButton = new Button("Actualiser");
        refreshButton.setOnAction(e -> loadSalles());

        // Layout des boutons
        HBox buttonBox = new HBox(10, addButton, editButton, deleteButton, refreshButton);
        buttonBox.setPadding(new Insets(10));

        // Layout principal
        VBox mainBox = new VBox(10, buttonBox, tableView);
        mainBox.setPadding(new Insets(10));

        setCenter(mainBox);
    }
// Dans SalleView.java - Mettez à jour la méthode deleteSalle()

    private void deleteSalle() {
        Map<String, Object> selectedSalle = tableView.getSelectionModel().getSelectedItem();

        if (selectedSalle == null) {
            showAlert("Avertissement", "Veuillez sélectionner une salle à supprimer");
            return;
        }

        Long salleId = ((Number) selectedSalle.get("id")).longValue();
        String salleName = (String) selectedSalle.get("nom");

        System.out.println("🔍 Vérification avant suppression de la salle ID: " + salleId);

        // Boîte de dialogue de confirmation simple
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer la salle \"" + salleName + "\"");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cette salle ?\n\n" +
                "Les créneaux associés seront automatiquement dissociés.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Indicateur de chargement simple
            System.out.println("⏳ Suppression en cours...");

            // Appeler l'API de suppression
            salleService.deleteSalle(salleId)
                    .thenAccept(message -> {
                        Platform.runLater(() -> {
                            System.out.println("✅ Suppression terminée");
                            showAlert("Succès", "Salle supprimée avec succès\n\n" + message);
                            loadSalles(); // Recharger la liste
                        });
                    })
                    .exceptionally(throwable -> {
                        Platform.runLater(() -> {
                            System.out.println("❌ Erreur de suppression");
                            showAlert("Erreur", "Erreur lors de la suppression:\n" + throwable.getMessage());
                        });
                        return null;
                    });
        }
    }
    private void loadSalles() {
        System.out.println("🔄 Chargement des salles...");
        salleService.getAllSalles()
                .thenAccept(salles -> {
                    System.out.println("✅ " + salles.size() + " salles chargées");
                    javafx.application.Platform.runLater(() -> {
                        sallesData.clear();
                        sallesData.addAll(salles);
                        System.out.println("📊 Données affichées dans la table: " + sallesData.size() + " éléments");

                        // 🔥 AFFICHER les IDs pour debug
                        salles.forEach(salle -> {
                            Object id = salle.get("id");
                            Object nom = salle.get("nom");
                            System.out.println("   - Salle ID: " + id + ", Nom: " + nom);
                        });
                    });
                })
                .exceptionally(throwable -> {
                    System.err.println("❌ Erreur lors du chargement: " + throwable.getMessage());
                    javafx.application.Platform.runLater(() ->
                            showAlert("Erreur", "Erreur lors du chargement des salles: " + throwable.getMessage()));
                    return null;
                });
    }

    private void showAddSalleDialog() {
        Dialog<SalleRequest> dialog = createSalleDialog("Ajouter une salle", null);

        Optional<SalleRequest> result = dialog.showAndWait();
        result.ifPresent(salle -> {
            System.out.println("➕ Ajout d'une nouvelle salle: " + salle);

            salleService.addSalle(salle)
                    .thenAccept(message -> {
                        showAlert("Succès", message);
                        loadSalles();
                    })
                    .exceptionally(throwable -> {
                        showAlert("Erreur", "Erreur lors de l'ajout: " + throwable.getMessage());
                        return null;
                    });
        });
    }

    private void showEditSalleDialog() {
        Map<String, Object> selectedSalle = tableView.getSelectionModel().getSelectedItem();

        if (selectedSalle == null) {
            showAlert("Avertissement", "Veuillez sélectionner une salle à modifier");
            return;
        }

        System.out.println("✏️ Modification de la salle: " + selectedSalle);

        // 🔥 CORRECTION : Convertir Map en SalleRequest avec l'ID
        SalleRequest salleRequest = mapToSalleRequest(selectedSalle);

        // 🔥 S'ASSURER que l'ID est bien extrait de la Map
        Long salleId = ((Number) selectedSalle.get("id")).longValue();
        salleRequest.setId(salleId); // 🔥 FORCER l'ID

        System.out.println("🆔 Salle à modifier - ID: " + salleId + ", Nom: " + salleRequest.getNom());

        Dialog<SalleRequest> dialog = createSalleDialog("Modifier la salle", salleRequest);

        Optional<SalleRequest> result = dialog.showAndWait();
        result.ifPresent(salle -> {
            // 🔥 CORRECTION : S'assurer que l'ID est conservé dans la requête de modification
            salle.setId(salleId); // 🔥 GARDER l'ID original

            System.out.println("🔄 Envoi de la modification - ID: " + salle.getId() + ", Nom: " + salle.getNom());

            salleService.updateSalle(salle)
                    .thenAccept(message -> {
                        System.out.println("✅ " + message);
                        showAlert("Succès", message);
                        loadSalles(); // Recharger la liste
                    })
                    .exceptionally(throwable -> {
                        System.err.println("❌ Erreur lors de la modification: " + throwable.getMessage());
                        showAlert("Erreur", "Erreur lors de la modification: " + throwable.getMessage());
                        return null;
                    });
        });
    }


    private Dialog<SalleRequest> createSalleDialog(String title, SalleRequest salle) {
        Dialog<SalleRequest> dialog = new Dialog<>();
        dialog.setTitle(title);

        // Boutons
        ButtonType saveButtonType = new ButtonType("Sauvegarder", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Formulaire
        VBox form = new VBox(10);
        form.setPadding(new Insets(20));

        TextField nomField = new TextField();
        TextField batimentField = new TextField();
        TextField capaciteField = new TextField();

        // Pré-remplir si modification
        if (salle != null) {
            nomField.setText(salle.getNom());
            batimentField.setText(salle.getBatiment());
            capaciteField.setText(salle.getCapacite() != null ? salle.getCapacite().toString() : "");

            // 🔥 AFFICHER l'ID pour debug (optionnel)
            System.out.println("📝 Pré-remplissage du formulaire - ID: " + salle.getId());
        }

        form.getChildren().addAll(
                new Label("Nom:"), nomField,
                new Label("Bâtiment:"), batimentField,
                new Label("Capacité:"), capaciteField
        );

        dialog.getDialogPane().setContent(form);

        // Convertir le résultat
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    SalleRequest newSalle = new SalleRequest();

                    // 🔥 CORRECTION : CONSERVER l'ID si modification
                    if (salle != null && salle.getId() != null) {
                        newSalle.setId(salle.getId());
                        System.out.println("💾 Conservation de l'ID: " + salle.getId());
                    }

                    newSalle.setNom(nomField.getText());
                    newSalle.setBatiment(batimentField.getText());

                    // Conversion String -> Integer pour la capacité
                    String capaciteText = capaciteField.getText();
                    if (capaciteText != null && !capaciteText.trim().isEmpty()) {
                        newSalle.setCapacite(Integer.parseInt(capaciteText));
                    } else {
                        newSalle.setCapacite(0); // Valeur par défaut
                    }

                    System.out.println("📤 Résultat du formulaire: " + newSalle);
                    return newSalle;

                } catch (NumberFormatException e) {
                    showAlert("Erreur", "La capacité doit être un nombre valide");
                    return null;
                }
            }
            return null;
        });

        return dialog;
    }
    // Méthode utilitaire pour convertir Map en SalleRequest
    private SalleRequest mapToSalleRequest(Map<String, Object> map) {
        SalleRequest salle = new SalleRequest();

        // 🔥 CORRECTION : EXTRAIRE l'ID de la Map
        Object idObj = map.get("id");
        if (idObj instanceof Number) {
            salle.setId(((Number) idObj).longValue());
        }

        salle.setNom((String) map.get("nom"));
        salle.setBatiment((String) map.get("batiment"));

        // Gestion de la capacité
        Object capaciteObj = map.get("capacite");
        if (capaciteObj instanceof Integer) {
            salle.setCapacite((Integer) capaciteObj);
        } else if (capaciteObj instanceof String) {
            try {
                salle.setCapacite(Integer.parseInt((String) capaciteObj));
            } catch (NumberFormatException e) {
                salle.setCapacite(0);
            }
        } else if (capaciteObj instanceof Number) {
            salle.setCapacite(((Number) capaciteObj).intValue());
        } else {
            salle.setCapacite(0); // Valeur par défaut
        }

        System.out.println("🗺️ Conversion Map -> SalleRequest: " + salle);
        return salle;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}*/