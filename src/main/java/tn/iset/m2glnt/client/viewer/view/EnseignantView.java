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
import tn.iset.m2glnt.client.model.Enseignant;
import tn.iset.m2glnt.client.service.dao.EnseignantService;
import tn.iset.m2glnt.client.service.dto.EnseignantRequest;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class EnseignantView extends BorderPane {
    private final EnseignantService enseignantService;
    private TableView<Map<String, Object>> tableView;
    private ObservableList<Map<String, Object>> enseignantsData;
    private Label titleLabel;
    private Label statsLabel;

    public EnseignantView() {
        this.enseignantService = new EnseignantService();
        this.enseignantsData = FXCollections.observableArrayList();
        initializeView();
        loadEnseignants();
    }

    private void initializeView() {
        // Style général avec gradient moderne
        this.setStyle("-fx-background-color: linear-gradient(135deg, #667eea 0%, #764ba2 100%);");

        // En-tête moderne
        VBox header = createModernHeader();

        // Table avec design moderne
        VBox tableContainer = createModernTable();

        // Layout principal
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(20));
        mainContainer.getChildren().addAll(header, tableContainer);

        setCenter(mainContainer);
    }

    private VBox createModernHeader() {
        VBox header = new VBox(15);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(20, 20, 30, 20));

        // Titre principal
        titleLabel = new Label("👨‍🏫 Gestion des Enseignants");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);");

        // Statistiques
        statsLabel = new Label("Chargement...");
        statsLabel.setFont(Font.font("System", FontWeight.NORMAL, 16));
        statsLabel.setTextFill(Color.web("#E0E0E0"));

        // Boutons d'action avec design moderne
        HBox buttonContainer = createModernButtonContainer();

        header.getChildren().addAll(titleLabel, statsLabel, buttonContainer);
        return header;
    }

    private HBox createModernButtonContainer() {
        HBox buttonContainer = new HBox(15);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(10));

        Button addButton = createModernButton("➕ Ajouter", "#10B981", "#059669");
        addButton.setOnAction(e -> showAddEnseignantDialog());

        Button editButton = createModernButton("✏️ Modifier", "#3B82F6", "#2563EB");
        editButton.setOnAction(e -> showEditEnseignantDialog());

        Button deleteButton = createModernButton("🗑️ Supprimer", "#EF4444", "#DC2626");
        deleteButton.setOnAction(e -> deleteEnseignant());

        Button refreshButton = createModernButton("🔄 Actualiser", "#6B7280", "#4B5563");
        refreshButton.setOnAction(e -> loadEnseignants());

        buttonContainer.getChildren().addAll(addButton, editButton, deleteButton, refreshButton);
        return buttonContainer;
    }

    private Button createModernButton(String text, String color, String hoverColor) {
        Button button = new Button(text);
        button.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        button.setPrefHeight(40);
        button.setMinWidth(120);

        String baseStyle = "-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 10; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 6, 0, 0, 2); " +
                "-fx-padding: 8 16;";

        String hoverStyle = "-fx-background-color: " + hoverColor + "; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 10; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3);";

        button.setStyle(baseStyle);

        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));

        return button;
    }

    private VBox createModernTable() {
        VBox tableContainer = new VBox();
        tableContainer.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 25, 0, 0, 8);");
        tableContainer.setPadding(new Insets(20));

        // Configuration de la table avec style moderne
        tableView = new TableView<>();
        tableView.setStyle("-fx-background-color: transparent; -fx-border-radius: 10; " +
                "-fx-background-radius: 10; -fx-border-color: #E2E8F0;");
        tableView.setItems(enseignantsData);

        // Style des lignes avec effet de hover
        tableView.setRowFactory(tv -> new TableRow<Map<String, Object>>() {
            @Override
            protected void updateItem(Map<String, Object> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else {
                    setStyle("-fx-border-color: #F1F5F9; -fx-border-width: 0 0 1 0; -fx-background-color: white;");
                    setOnMouseEntered(e -> setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #F1F5F9; -fx-border-width: 0 0 1 0;"));
                    setOnMouseExited(e -> setStyle("-fx-background-color: white; -fx-border-color: #F1F5F9; -fx-border-width: 0 0 1 0;"));
                }
            }
        });

        // Création des colonnes avec style moderne
        TableColumn<Map<String, Object>, Long> idCol = createStyledColumn("ID", "id", 80);
        TableColumn<Map<String, Object>, String> nomCol = createStyledColumn("Nom", "nom", 150);
        TableColumn<Map<String, Object>, String> prenomCol = createStyledColumn("Prénom", "prenom", 150);
        TableColumn<Map<String, Object>, String> emailCol = createStyledColumn("Email", "email", 200);
        TableColumn<Map<String, Object>, String> cinCol = createStyledColumn("CIN", "cin", 120);
        TableColumn<Map<String, Object>, String> telCol = createStyledColumn("Téléphone", "tel", 130);

        tableView.getColumns().addAll(idCol, nomCol, prenomCol, emailCol, cinCol, telCol);

        // Message quand la table est vide
        Label placeholder = new Label("📭 Aucun enseignant disponible");
        placeholder.setFont(Font.font("System", FontWeight.NORMAL, 14));
        placeholder.setTextFill(Color.web("#718096"));
        placeholder.setPadding(new Insets(30));
        tableView.setPlaceholder(placeholder);

        tableContainer.getChildren().add(tableView);
        return tableContainer;
    }

    private <T> TableColumn<Map<String, Object>, T> createStyledColumn(String title, String property, double width) {
        TableColumn<Map<String, Object>, T> column = new TableColumn<>(title);
        column.setPrefWidth(width);

        // Style de l'en-tête
        column.setStyle("-fx-background-color: #4F46E5; -fx-border-color: #3730A3; " +
                "-fx-font-weight: bold; -fx-text-fill: white; -fx-alignment: CENTER;");

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

        // Style des cellules
        column.setCellFactory(tc -> new TableCell<Map<String, Object>, T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    setStyle("-fx-alignment: CENTER; -fx-font-family: 'System'; " +
                            "-fx-text-fill: #4A5568; -fx-font-size: 13;");
                    setPadding(new Insets(10, 5, 10, 5));
                }
            }
        });

        return column;
    }

    private void loadEnseignants() {
        System.out.println("🔄 Chargement des enseignants...");

        // Animation de chargement
        statsLabel.setText("⏳ Chargement en cours...");
        statsLabel.setTextFill(Color.web("#FBBF24"));

        if (enseignantService == null) {
            System.err.println("❌ EnseignantService null !");
            statsLabel.setText("❌ Service non disponible");
            statsLabel.setTextFill(Color.web("#EF4444"));
            return;
        }

        try {
            CompletableFuture<List<Enseignant>> futureEnseignants = enseignantService.getAllEnseignants();

            futureEnseignants.thenAccept(enseignants -> {
                Platform.runLater(() -> {
                    if (enseignants == null || enseignants.isEmpty()) {
                        System.out.println("⚠️ Aucun enseignant trouvé dans la base de données");
                        enseignantsData.clear();
                        updateStats(0);
                        return;
                    }

                    // Convertir les enseignants en Map pour la table
                    List<Map<String, Object>> enseignantMaps = enseignants.stream()
                            .map(enseignant -> {
                                Map<String, Object> map = new java.util.HashMap<>();
                                try {
                                    map.put("id", enseignant.getId());
                                    map.put("nom", safeToString(enseignant.getNom()));
                                    map.put("prenom", safeToString(enseignant.getPrenom()));
                                    map.put("email", safeToString(enseignant.getEmail()));
                                    map.put("cin", safeToString(enseignant.getCin()));
                                    map.put("tel", safeToString(enseignant.getTel()));
                                } catch (Exception e) {
                                    System.err.println("❌ Erreur lors de la conversion de l'enseignant: " + e.getMessage());
                                    map.put("id", -1L);
                                    map.put("nom", "ERREUR");
                                    map.put("prenom", "Données corrompues");
                                    map.put("email", "");
                                    map.put("cin", "");
                                    map.put("tel", "");
                                }
                                return map;
                            })
                            .toList();

                    enseignantsData.setAll(enseignantMaps);
                    updateStats(enseignantMaps.size());
                    System.out.println("✅ " + enseignantMaps.size() + " enseignants chargés dans la table");
                });
            }).exceptionally(throwable -> {
                System.err.println("❌ Erreur lors du chargement des enseignants: " + throwable.getMessage());
                throwable.printStackTrace();
                Platform.runLater(() -> {
                    statsLabel.setText("❌ Erreur de chargement");
                    statsLabel.setTextFill(Color.web("#EF4444"));
                    showStyledAlert("Erreur", "Erreur lors du chargement des enseignants: " + throwable.getMessage(), Alert.AlertType.ERROR);
                });
                return null;
            });

        } catch (Exception e) {
            System.err.println("❌ Exception lors du chargement des enseignants: " + e.getMessage());
            e.printStackTrace();
            statsLabel.setText("❌ Erreur de chargement");
            statsLabel.setTextFill(Color.web("#EF4444"));
            showStyledAlert("Erreur", "Erreur lors du chargement des enseignants: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateStats(int count) {
        String statText;
        if (count == 0) {
            statText = "📭 Aucun enseignant disponible";
            statsLabel.setTextFill(Color.web("#EF4444"));
        } else if (count == 1) {
            statText = "📊 1 enseignant disponible";
            statsLabel.setTextFill(Color.web("#10B981"));
        } else {
            statText = "📊 " + count + " enseignants disponibles";
            statsLabel.setTextFill(Color.web("#10B981"));
        }
        statsLabel.setText(statText);
    }

    private void showAddEnseignantDialog() {
        Dialog<EnseignantRequest> dialog = createStyledEnseignantDialog("➕ Ajouter un enseignant", null);

        Optional<EnseignantRequest> result = dialog.showAndWait();
        result.ifPresent(enseignant -> {
            System.out.println("➕ Ajout d'un nouvel enseignant: " + enseignant);
            statsLabel.setText("⏳ Ajout en cours...");
            statsLabel.setTextFill(Color.web("#F59E0B"));

            // HACHER LE MOT DE PASSE AVANT L'ENVOI
            String originalPassword = enseignant.getPassword();
            if (originalPassword != null && !originalPassword.trim().isEmpty()) {
                String hashedPassword = hashPassword(originalPassword);
                enseignant.setPassword(hashedPassword);
                System.out.println("🔐 Mot de passe haché avant envoi");
            }

            enseignantService.addEnseignant(enseignant)
                    .thenAccept(message -> {
                        System.out.println("✅ " + message);
                        Platform.runLater(() -> {
                            showStyledAlert("Succès", "✅ " + message, Alert.AlertType.INFORMATION);
                            loadEnseignants();
                        });
                    })
                    .exceptionally(throwable -> {
                        System.err.println("❌ Erreur lors de l'ajout: " + throwable.getMessage());
                        Platform.runLater(() -> {
                            showStyledAlert("Erreur", "❌ Erreur lors de l'ajout: " + throwable.getMessage(), Alert.AlertType.ERROR);
                            loadEnseignants();
                        });
                        return null;
                    });
        });
    }

    private void showEditEnseignantDialog() {
        Map<String, Object> selectedEnseignant = tableView.getSelectionModel().getSelectedItem();

        if (selectedEnseignant == null) {
            showStyledAlert("Avertissement", "Veuillez sélectionner un enseignant à modifier", Alert.AlertType.WARNING);
            return;
        }

        System.out.println("✏️ Modification de l'enseignant: " + selectedEnseignant);

        // Convertir Map en EnseignantRequest
        EnseignantRequest enseignantRequest = mapToEnseignantRequest(selectedEnseignant);

        // S'assurer que l'ID est bien extrait
        Long enseignantId = ((Number) selectedEnseignant.get("id")).longValue();
        enseignantRequest.setId(enseignantId);

        System.out.println("🆔 Enseignant à modifier - ID: " + enseignantId + ", Nom: " + enseignantRequest.getNom());

        Dialog<EnseignantRequest> dialog = createStyledEnseignantDialog("✏️ Modifier l'enseignant", enseignantRequest);

        Optional<EnseignantRequest> result = dialog.showAndWait();
        result.ifPresent(enseignant -> {
            // S'assurer que l'ID est conservé
            enseignant.setId(enseignantId);
            statsLabel.setText("⏳ Modification en cours...");
            statsLabel.setTextFill(Color.web("#F59E0B"));

            // HACHER LE NOUVEAU MOT DE PASSE SI FOURNI
            String newPassword = enseignant.getPassword();
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                String hashedPassword = hashPassword(newPassword);
                enseignant.setPassword(hashedPassword);
                System.out.println("🔐 Nouveau mot de passe haché avant envoi");
            } else {
                // Conserver l'ancien mot de passe (ne pas le modifier)
                enseignant.setPassword(null);
                System.out.println("ℹ️ Mot de passe non modifié");
            }

            System.out.println("🔄 Envoi de la modification - ID: " + enseignant.getId() + ", Nom: " + enseignant.getNom());

            enseignantService.updateEnseignant(enseignant)
                    .thenAccept(message -> {
                        System.out.println("✅ " + message);
                        Platform.runLater(() -> {
                            showStyledAlert("Succès", "✅ " + message, Alert.AlertType.INFORMATION);
                            loadEnseignants();
                        });
                    })
                    .exceptionally(throwable -> {
                        System.err.println("❌ Erreur lors de la modification: " + throwable.getMessage());
                        Platform.runLater(() -> {
                            showStyledAlert("Erreur", "❌ Erreur lors de la modification: " + throwable.getMessage(), Alert.AlertType.ERROR);
                            loadEnseignants();
                        });
                        return null;
                    });
        });
    }

    private void deleteEnseignant() {
        Map<String, Object> selectedEnseignant = tableView.getSelectionModel().getSelectedItem();

        if (selectedEnseignant == null) {
            showStyledAlert("Avertissement", "Veuillez sélectionner un enseignant à supprimer", Alert.AlertType.WARNING);
            return;
        }

        Long enseignantId = ((Number) selectedEnseignant.get("id")).longValue();
        String enseignantNom = (String) selectedEnseignant.get("nom");
        String enseignantPrenom = (String) selectedEnseignant.get("prenom");

        // Boîte de dialogue de confirmation stylée
        Alert confirmation = createStyledAlert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer l'enseignant \"" + enseignantPrenom + " " + enseignantNom + "\"");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cet enseignant ?\n\n" +
                "• Cette action est irréversible\n" +
                "• Les données associées seront perdues");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            statsLabel.setText("⏳ Suppression en cours...");
            statsLabel.setTextFill(Color.web("#F59E0B"));

            enseignantService.deleteEnseignant(enseignantId)
                    .thenAccept(message -> {
                        Platform.runLater(() -> {
                            showStyledAlert("Succès", "✅ " + message, Alert.AlertType.INFORMATION);
                            loadEnseignants();
                        });
                    })
                    .exceptionally(throwable -> {
                        Platform.runLater(() -> {
                            showStyledAlert("Erreur", "❌ Erreur lors de la suppression: " + throwable.getMessage(), Alert.AlertType.ERROR);
                            loadEnseignants();
                        });
                        return null;
                    });
        }
    }

    private Dialog<EnseignantRequest> createStyledEnseignantDialog(String title, EnseignantRequest enseignant) {
        Dialog<EnseignantRequest> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);

        // Style du dialog
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                "-fx-border-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 5);");

        // Boutons stylés
        ButtonType saveButtonType = new ButtonType("💾 Sauvegarder", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Style des boutons
        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-font-weight: bold;");

        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButton.setStyle("-fx-background-color: #6B7280; -fx-text-fill: white;");

        // Formulaire stylé
        VBox form = new VBox(15);
        form.setPadding(new Insets(25));
        form.setStyle("-fx-background-color: white;");

        // Champs avec labels stylés
        TextField nomField = createStyledTextField();
        TextField prenomField = createStyledTextField();
        TextField emailField = createStyledTextField();
        TextField cinField = createStyledTextField();
        TextField telField = createStyledTextField();
        PasswordField passwordField = createStyledPasswordField();

        // Pré-remplir si modification
        if (enseignant != null) {
            nomField.setText(enseignant.getNom());
            prenomField.setText(enseignant.getPrenom());
            emailField.setText(enseignant.getEmail());
            cinField.setText(enseignant.getCin());
            telField.setText(enseignant.getTel());
            passwordField.setPromptText("Laisser vide pour conserver l'actuel");
        } else {
            passwordField.setPromptText("Mot de passe (obligatoire)");
        }

        form.getChildren().addAll(
                createFormLabel("Nom:"), nomField,
                createFormLabel("Prénom:"), prenomField,
                createFormLabel("Email:"), emailField,
                createFormLabel("CIN:"), cinField,
                createFormLabel("Téléphone:"), telField,
                createFormLabel("Mot de passe:"), passwordField
        );

        dialog.getDialogPane().setContent(form);

        // Validation
        Button saveButtonFinal = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButtonFinal.setDisable(true);

        // Validation simple
        javafx.beans.value.ChangeListener<String> simpleValidation = (obs, oldVal, newVal) -> {
            boolean isValid = !nomField.getText().trim().isEmpty()
                    && !prenomField.getText().trim().isEmpty()
                    && !emailField.getText().trim().isEmpty();

            // Pour nouvel enseignant, le mot de passe est obligatoire
            if (enseignant == null) {
                isValid = isValid && !passwordField.getText().trim().isEmpty();
            }

            saveButtonFinal.setDisable(!isValid);
        };

        nomField.textProperty().addListener(simpleValidation);
        prenomField.textProperty().addListener(simpleValidation);
        emailField.textProperty().addListener(simpleValidation);
        passwordField.textProperty().addListener(simpleValidation);

        // Convertir le résultat
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    EnseignantRequest newEnseignant = new EnseignantRequest();

                    if (enseignant != null && enseignant.getId() != null) {
                        newEnseignant.setId(enseignant.getId());
                    }

                    newEnseignant.setNom(nomField.getText().trim());
                    newEnseignant.setPrenom(prenomField.getText().trim());
                    newEnseignant.setEmail(emailField.getText().trim());
                    newEnseignant.setCin(cinField.getText().trim());
                    newEnseignant.setTel(telField.getText().trim());

                    String password = passwordField.getText();
                    if (password != null && !password.trim().isEmpty()) {
                        newEnseignant.setPassword(password);
                    } else if (enseignant == null) {
                        newEnseignant.setPassword(generateDefaultPassword());
                    }

                    return newEnseignant;

                } catch (Exception e) {
                    showStyledAlert("Erreur", "❌ Erreur lors de la création de l'enseignant: " + e.getMessage(), Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        return dialog;
    }

    private TextField createStyledTextField() {
        TextField textField = new TextField();
        textField.setStyle("-fx-background-color: #F7FAFC; -fx-background-radius: 8; " +
                "-fx-border-color: #E2E8F0; -fx-border-radius: 8; -fx-border-width: 1; " +
                "-fx-padding: 10; -fx-font-size: 14;");
        textField.setPrefHeight(40);
        return textField;
    }

    private PasswordField createStyledPasswordField() {
        PasswordField passwordField = new PasswordField();
        passwordField.setStyle("-fx-background-color: #F7FAFC; -fx-background-radius: 8; " +
                "-fx-border-color: #E2E8F0; -fx-border-radius: 8; -fx-border-width: 1; " +
                "-fx-padding: 10; -fx-font-size: 14;");
        passwordField.setPrefHeight(40);
        return passwordField;
    }

    private Label createFormLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        label.setTextFill(Color.web("#4A5568"));
        return label;
    }

    // === MÉTHODES EXISTANTES (conservées) ===

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
        String defaultPassword = "Enseignant123!";
        System.out.println("🔑 Génération d'un mot de passe par défaut sécurisé");
        return defaultPassword;
    }

    private String safeToString(Object obj) {
        if (obj == null) {
            return "";
        }
        try {
            return obj.toString();
        } catch (Exception e) {
            System.err.println("❌ Impossible de convertir l'objet en String: " + obj);
            return "";
        }
    }

    private EnseignantRequest mapToEnseignantRequest(Map<String, Object> map) {
        EnseignantRequest enseignant = new EnseignantRequest();

        Object idObj = map.get("id");
        if (idObj instanceof Number) {
            enseignant.setId(((Number) idObj).longValue());
        }

        enseignant.setNom((String) map.get("nom"));
        enseignant.setPrenom((String) map.get("prenom"));
        enseignant.setEmail((String) map.get("email"));
        enseignant.setCin((String) map.get("cin"));
        enseignant.setTel((String) map.get("tel"));

        System.out.println("🗺️ Conversion Map -> EnseignantRequest: " + enseignant);
        return enseignant;
    }

    private void showStyledAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Style de l'alerte
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                "-fx-border-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 5);");

        alert.showAndWait();
    }

    private Alert createStyledAlert(Alert.AlertType type) {
        Alert alert = new Alert(type);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
        return alert;
    }

    // Classe interne pour les items de ComboBox avec ID
    public static class EnseignantItem {
        private final Long id;
        private final String nomComplet;

        public EnseignantItem(Long id, String nomComplet) {
            this.id = id;
            this.nomComplet = nomComplet;
        }

        public Long getId() { return id; }
        public String getNomComplet() { return nomComplet; }

        @Override
        public String toString() {
            return nomComplet;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            EnseignantItem that = (EnseignantItem) obj;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
}
/*package tn.iset.m2glnt.client.viewer.view;
import org.mindrot.jbcrypt.BCrypt;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import tn.iset.m2glnt.client.model.Enseignant;
import tn.iset.m2glnt.client.service.dao.EnseignantService;
import tn.iset.m2glnt.client.service.dto.EnseignantRequest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class EnseignantView extends BorderPane {
    private final EnseignantService enseignantService;
    private TableView<Map<String, Object>> tableView;
    private ObservableList<Map<String, Object>> enseignantsData;

    public EnseignantView() {
        this.enseignantService = new EnseignantService();
        this.enseignantsData = FXCollections.observableArrayList();
        initializeView();
        loadEnseignants();
    }

    private void initializeView() {
        // Configuration de la table
        tableView = new TableView<>();
        tableView.setItems(enseignantsData);

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

        TableColumn<Map<String, Object>, String> telCol = new TableColumn<>("Téléphone");
        telCol.setCellValueFactory(data -> {
            Map<String, Object> row = data.getValue();
            Object value = row.get("tel");
            return new javafx.beans.property.SimpleStringProperty(value != null ? value.toString() : "");
        });

        // Ajouter les colonnes à la table
        tableView.getColumns().addAll(idCol, nomCol, prenomCol, emailCol, cinCol, telCol);

        // Boutons CRUD
        Button addButton = new Button("Ajouter");
        addButton.setOnAction(e -> showAddEnseignantDialog());

        Button editButton = new Button("Modifier");
        editButton.setOnAction(e -> showEditEnseignantDialog());

        Button deleteButton = new Button("Supprimer");
        deleteButton.setOnAction(e -> deleteEnseignant());

        Button refreshButton = new Button("Actualiser");
        refreshButton.setOnAction(e -> loadEnseignants());

        // Layout des boutons
        HBox buttonBox = new HBox(10, addButton, editButton, deleteButton, refreshButton);
        buttonBox.setPadding(new Insets(10));

        // Layout principal
        VBox mainBox = new VBox(10, buttonBox, tableView);
        mainBox.setPadding(new Insets(10));

        setCenter(mainBox);
    }



import java.util.Objects;

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
        String defaultPassword = "Enseignant123!";
        System.out.println("🔑 Génération d'un mot de passe par défaut sécurisé");
        return defaultPassword;
    }

    // Méthode pour charger les enseignants dans la table (sans paramètre)
    private void loadEnseignants() {
        System.out.println("🔍 Chargement des enseignants pour la table...");

        if (enseignantService == null) {
            System.err.println("❌ EnseignantService null !");
            return;
        }

        try {
            CompletableFuture<List<Enseignant>> futureEnseignants = enseignantService.getAllEnseignants();

            futureEnseignants.thenAccept(enseignants -> {
                Platform.runLater(() -> {
                    if (enseignants == null || enseignants.isEmpty()) {
                        System.out.println("⚠️ Aucun enseignant trouvé dans la base de données");
                        enseignantsData.clear();
                        return;
                    }

                    // Convertir les enseignants en Map pour la table - AVEC GESTION D'ERREUR
                    List<Map<String, Object>> enseignantMaps = enseignants.stream()
                            .map(enseignant -> {
                                Map<String, Object> map = new java.util.HashMap<>();
                                try {
                                    map.put("id", enseignant.getId());
                                    map.put("nom", safeToString(enseignant.getNom()));
                                    map.put("prenom", safeToString(enseignant.getPrenom()));
                                    map.put("email", safeToString(enseignant.getEmail()));
                                    map.put("cin", safeToString(enseignant.getCin()));
                                    map.put("tel", safeToString(enseignant.getTel()));
                                } catch (Exception e) {
                                    System.err.println("❌ Erreur lors de la conversion de l'enseignant: " + e.getMessage());
                                    // Créer une entrée d'erreur
                                    map.put("id", -1L);
                                    map.put("nom", "ERREUR");
                                    map.put("prenom", "Données corrompues");
                                    map.put("email", "");
                                    map.put("cin", "");
                                    map.put("tel", "");
                                }
                                return map;
                            })
                            .toList();

                    enseignantsData.setAll(enseignantMaps);
                    System.out.println("✅ " + enseignantMaps.size() + " enseignants chargés dans la table");
                });
            }).exceptionally(throwable -> {
                System.err.println("❌ Erreur lors du chargement des enseignants: " + throwable.getMessage());
                throwable.printStackTrace();
                Platform.runLater(() -> {
                    showAlert("Erreur", "Erreur lors du chargement des enseignants: " + throwable.getMessage());
                });
                return null;
            });

        } catch (Exception e) {
            System.err.println("❌ Exception lors du chargement des enseignants: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des enseignants: " + e.getMessage());
        }
    }

    // Méthode utilitaire pour conversion sécurisée
    private String safeToString(Object obj) {
        if (obj == null) {
            return "";
        }
        try {
            return obj.toString();
        } catch (Exception e) {
            System.err.println("❌ Impossible de convertir l'objet en String: " + obj);
            return "";
        }
    }

    private void showAddEnseignantDialog() {
        Dialog<EnseignantRequest> dialog = createEnseignantDialog("Ajouter un enseignant", null);

        Optional<EnseignantRequest> result = dialog.showAndWait();
        result.ifPresent(enseignant -> {
            System.out.println("➕ Ajout d'un nouvel enseignant: " + enseignant);

            // HACHER LE MOT DE PASSE AVANT L'ENVOI
            String originalPassword = enseignant.getPassword();
            if (originalPassword != null && !originalPassword.trim().isEmpty()) {
                String hashedPassword = hashPassword(originalPassword);
                enseignant.setPassword(hashedPassword);
                System.out.println("🔐 Mot de passe haché avant envoi");
            }

            enseignantService.addEnseignant(enseignant)
                    .thenAccept(message -> {
                        System.out.println("✅ " + message);
                        Platform.runLater(() -> {
                            showAlert("Succès", message);
                            loadEnseignants(); // Recharger la liste
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

    private void showEditEnseignantDialog() {
        Map<String, Object> selectedEnseignant = tableView.getSelectionModel().getSelectedItem();

        if (selectedEnseignant == null) {
            showAlert("Avertissement", "Veuillez sélectionner un enseignant à modifier");
            return;
        }

        System.out.println("✏️ Modification de l'enseignant: " + selectedEnseignant);

        // Convertir Map en EnseignantRequest
        EnseignantRequest enseignantRequest = mapToEnseignantRequest(selectedEnseignant);

        // S'assurer que l'ID est bien extrait
        Long enseignantId = ((Number) selectedEnseignant.get("id")).longValue();
        enseignantRequest.setId(enseignantId);

        System.out.println("🆔 Enseignant à modifier - ID: " + enseignantId + ", Nom: " + enseignantRequest.getNom());

        Dialog<EnseignantRequest> dialog = createEnseignantDialog("Modifier l'enseignant", enseignantRequest);

        Optional<EnseignantRequest> result = dialog.showAndWait();
        result.ifPresent(enseignant -> {
            // S'assurer que l'ID est conservé
            enseignant.setId(enseignantId);

            // HACHER LE NOUVEAU MOT DE PASSE SI FOURNI
            String newPassword = enseignant.getPassword();
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                String hashedPassword = hashPassword(newPassword);
                enseignant.setPassword(hashedPassword);
                System.out.println("🔐 Nouveau mot de passe haché avant envoi");
            } else {
                // Conserver l'ancien mot de passe (ne pas le modifier)
                enseignant.setPassword(null);
                System.out.println("ℹ️ Mot de passe non modifié");
            }

            System.out.println("🔄 Envoi de la modification - ID: " + enseignant.getId() + ", Nom: " + enseignant.getNom());

            enseignantService.updateEnseignant(enseignant)
                    .thenAccept(message -> {
                        System.out.println("✅ " + message);
                        Platform.runLater(() -> {
                            showAlert("Succès", message);
                            loadEnseignants();
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

    private void deleteEnseignant() {
        Map<String, Object> selectedEnseignant = tableView.getSelectionModel().getSelectedItem();

        if (selectedEnseignant == null) {
            showAlert("Avertissement", "Veuillez sélectionner un enseignant à supprimer");
            return;
        }

        // Confirmation de suppression
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer l'enseignant");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cet enseignant ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Long enseignantId = ((Number) selectedEnseignant.get("id")).longValue();

            enseignantService.deleteEnseignant(enseignantId)
                    .thenAccept(message -> {
                        Platform.runLater(() -> {
                            showAlert("Succès", message);
                            loadEnseignants();
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
    private Dialog<EnseignantRequest> createEnseignantDialog(String title, EnseignantRequest enseignant) {
        Dialog<EnseignantRequest> dialog = new Dialog<>();
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
        TextField telField = new TextField();
        PasswordField passwordField = new PasswordField();

        // Pré-remplir si modification
        if (enseignant != null) {
            nomField.setText(enseignant.getNom());
            prenomField.setText(enseignant.getPrenom());
            emailField.setText(enseignant.getEmail());
            cinField.setText(enseignant.getCin());
            telField.setText(enseignant.getTel());
            passwordField.setPromptText("Laisser vide pour conserver l'actuel");
        } else {
            passwordField.setPromptText("Mot de passe (obligatoire)");
        }

        form.getChildren().addAll(
                new Label("Nom:"), nomField,
                new Label("Prénom:"), prenomField,
                new Label("Email:"), emailField,
                new Label("CIN:"), cinField,
                new Label("Téléphone:"), telField,
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

            // Pour nouvel enseignant, le mot de passe est obligatoire
            if (enseignant == null) {
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
                    EnseignantRequest newEnseignant = new EnseignantRequest();

                    if (enseignant != null && enseignant.getId() != null) {
                        newEnseignant.setId(enseignant.getId());
                    }

                    newEnseignant.setNom(nomField.getText());
                    newEnseignant.setPrenom(prenomField.getText());
                    newEnseignant.setEmail(emailField.getText());
                    newEnseignant.setCin(cinField.getText());
                    newEnseignant.setTel(telField.getText());

                    String password = passwordField.getText();
                    if (password != null && !password.trim().isEmpty()) {
                        newEnseignant.setPassword(password);
                    } else if (enseignant == null) {
                        newEnseignant.setPassword(generateDefaultPassword());
                    }

                    return newEnseignant;

                } catch (Exception e) {
                    showAlert("Erreur", "Erreur lors de la création de l'enseignant: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        return dialog;
    }
    private EnseignantRequest mapToEnseignantRequest(Map<String, Object> map) {
        EnseignantRequest enseignant = new EnseignantRequest();

        // Extraire l'ID
        Object idObj = map.get("id");
        if (idObj instanceof Number) {
            enseignant.setId(((Number) idObj).longValue());
        }

        enseignant.setNom((String) map.get("nom"));
        enseignant.setPrenom((String) map.get("prenom"));
        enseignant.setEmail((String) map.get("email"));
        enseignant.setCin((String) map.get("cin"));
        enseignant.setTel((String) map.get("tel"));

        System.out.println("🗺️ Conversion Map -> EnseignantRequest: " + enseignant);
        return enseignant;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Classe interne pour les items de ComboBox avec ID
    public static class EnseignantItem {
        private final Long id;
        private final String nomComplet;

        public EnseignantItem(Long id, String nomComplet) {
            this.id = id;
            this.nomComplet = nomComplet;
        }

        public Long getId() { return id; }
        public String getNomComplet() { return nomComplet; }

        @Override
        public String toString() {
            return nomComplet;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            EnseignantItem that = (EnseignantItem) obj;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }}*/