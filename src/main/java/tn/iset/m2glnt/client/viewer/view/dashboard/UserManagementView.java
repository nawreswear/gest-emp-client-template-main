package tn.iset.m2glnt.client.viewer.view.dashboard;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import tn.iset.m2glnt.client.model.SignupRequest;
import tn.iset.m2glnt.client.service.dao.UserRestService;

import java.util.List;
import java.util.Map;

public class UserManagementView {
    private final UserRestService userService;
    private final TableView<Map<String, Object>> userTable;
    private final ObservableList<Map<String, Object>> userData;
    private ProgressIndicator progressIndicator;

    public UserManagementView(UserRestService userService) {
        this.userService = userService;
        this.userData = FXCollections.observableArrayList();
        this.userTable = new TableView<>();
    }

    public VBox createView() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));

        // Titre
        Label title = new Label("Gestion des Utilisateurs");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        // Boutons d'action
        HBox buttonBox = new HBox(10);
        Button refreshBtn = new Button("🔄 Actualiser");
        Button addBtn = new Button("➕ Ajouter");
        Button editBtn = new Button("✏️ Modifier");
        Button deleteBtn = new Button("🗑️ Supprimer");

        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        editBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

        buttonBox.getChildren().addAll(refreshBtn, addBtn, editBtn, deleteBtn);

        // Indicateur de progression
        progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);

        // Configuration du tableau
        setupUserTable();

        // Actions des boutons
        refreshBtn.setOnAction(e -> loadUsers());
        addBtn.setOnAction(e -> showAddUserDialog());
        editBtn.setOnAction(e -> showEditUserDialog());
        deleteBtn.setOnAction(e -> deleteSelectedUser());

        // Layout principal
        VBox contentBox = new VBox(10);
        contentBox.getChildren().addAll(buttonBox, progressIndicator, userTable);
        VBox.setVgrow(userTable, Priority.ALWAYS);

        container.getChildren().addAll(title, contentBox);

        // Chargement initial
        loadUsers();

        return container;
    }

    private void setupUserTable() {
        // Configuration des colonnes
        TableColumn<Map<String, Object>, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> {
            Map<String, Object> row = data.getValue();
            Object value = row.get("id");
            if (value instanceof Number) {
                return new javafx.beans.property.SimpleObjectProperty<>(((Number) value).longValue());
            }
            return new javafx.beans.property.SimpleObjectProperty<>(null);
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

        TableColumn<Map<String, Object>, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(data -> {
            Map<String, Object> row = data.getValue();
            Object value = row.get("type");
            return new javafx.beans.property.SimpleStringProperty(value != null ? value.toString() : "");
        });

        userTable.getColumns().addAll(idCol, nomCol, prenomCol, emailCol, typeCol);
        userTable.setItems(userData);

        // Style du tableau
        userTable.setStyle("-fx-font-size: 14px;");
    }

    private void loadUsers() {
        showProgress(true);

        userService.getAllUsers().whenComplete((users, throwable) -> {
            javafx.application.Platform.runLater(() -> {
                showProgress(false);

                if (throwable != null) {
                    // Gestion d'erreur améliorée
                    String errorMessage = "Erreur lors du chargement des utilisateurs";
                    if (throwable.getCause() != null) {
                        errorMessage += ": " + throwable.getCause().getMessage();
                    } else {
                        errorMessage += ": " + throwable.getMessage();
                    }
                    showError(errorMessage);
                    return;
                }

                if (users != null && !users.isEmpty()) {
                    userData.clear();
                    userData.addAll(users);
                    showSuccess("Chargement réussi: " + users.size() + " utilisateur(s) trouvé(s)");
                } else {
                    userData.clear();
                    showInfo("Aucun utilisateur trouvé");
                }
            });
        });
    }

    private void showAddUserDialog() {
        Dialog<SignupRequest> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un utilisateur");
        dialog.setHeaderText("Créer un nouvel utilisateur");

        // Configuration des boutons
        ButtonType createButtonType = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // Formulaire
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nomField = new TextField();
        TextField prenomField = new TextField();
        TextField emailField = new TextField();
        PasswordField passwordField = new PasswordField();
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("admin", "enseignant", "etudiant");

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Prénom:"), 0, 1);
        grid.add(prenomField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Mot de passe:"), 0, 3);
        grid.add(passwordField, 1, 3);
        grid.add(new Label("Type:"), 0, 4);
        grid.add(typeCombo, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Conversion du résultat
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                SignupRequest request = new SignupRequest();
                request.setNom(nomField.getText());
                request.setPrenom(prenomField.getText());
                request.setEmail(emailField.getText());
                request.setPassword(passwordField.getText());
                request.setType(typeCombo.getValue());
                return request;
            }
            return null;
        });

        // Affichage du dialogue
        dialog.showAndWait().ifPresent(userRequest -> {
            showProgress(true);
            userService.addUser(userRequest).whenComplete((result, error) -> {
                javafx.application.Platform.runLater(() -> {
                    showProgress(false);
                    if (error != null) {
                        String errorMsg = "Erreur lors de l'ajout";
                        if (error.getCause() != null) {
                            errorMsg += ": " + error.getCause().getMessage();
                        } else {
                            errorMsg += ": " + error.getMessage();
                        }
                        showError(errorMsg);
                    } else {
                        showSuccess(result);
                        loadUsers(); // Recharger la liste
                    }
                });
            });
        });
    }

    private void showEditUserDialog() {
        Map<String, Object> selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner un utilisateur à modifier");
            return;
        }

        // Implémentation similaire à showAddUserDialog mais avec pré-remplissage
        Dialog<SignupRequest> dialog = new Dialog<>();
        dialog.setTitle("Modifier l'utilisateur");
        dialog.setHeaderText("Modifier les informations de l'utilisateur");

        // Configuration des boutons
        ButtonType updateButtonType = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // Formulaire pré-rempli
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nomField = new TextField(selected.get("nom").toString());
        TextField prenomField = new TextField(selected.get("prenom").toString());
        TextField emailField = new TextField(selected.get("email").toString());
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Laisser vide pour ne pas changer");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("admin", "enseignant", "etudiant");
        typeCombo.setValue(selected.get("type").toString());

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Prénom:"), 0, 1);
        grid.add(prenomField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Mot de passe:"), 0, 3);
        grid.add(passwordField, 1, 3);
        grid.add(new Label("Type:"), 0, 4);
        grid.add(typeCombo, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Conversion du résultat
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                SignupRequest request = new SignupRequest();
                request.setNom(nomField.getText());
                request.setPrenom(prenomField.getText());
                request.setEmail(emailField.getText());
                if (!passwordField.getText().isEmpty()) {
                    request.setPassword(passwordField.getText());
                }
                request.setType(typeCombo.getValue());
                return request;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(userRequest -> {
            Long userId = ((Number) selected.get("id")).longValue();
            showProgress(true);
            userService.updateUser(userId, userRequest).whenComplete((result, error) -> {
                javafx.application.Platform.runLater(() -> {
                    showProgress(false);
                    if (error != null) {
                        String errorMsg = "Erreur lors de la modification";
                        if (error.getCause() != null) {
                            errorMsg += ": " + error.getCause().getMessage();
                        } else {
                            errorMsg += ": " + error.getMessage();
                        }
                        showError(errorMsg);
                    } else {
                        showSuccess(result);
                        loadUsers();
                    }
                });
            });
        });
    }

    private void deleteSelectedUser() {
        Map<String, Object> selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner un utilisateur à supprimer");
            return;
        }

        Long userId = ((Number) selected.get("id")).longValue();

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer l'utilisateur");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cet utilisateur ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                showProgress(true);
                userService.deleteUser(userId).whenComplete((result, error) -> {
                    javafx.application.Platform.runLater(() -> {
                        showProgress(false);
                        if (error != null) {
                            String errorMsg = "Erreur lors de la suppression";
                            if (error.getCause() != null) {
                                errorMsg += ": " + error.getCause().getMessage();
                            } else {
                                errorMsg += ": " + error.getMessage();
                            }
                            showError(errorMsg);
                        } else {
                            showSuccess(result);
                            loadUsers();
                        }
                    });
                });
            }
        });
    }

    private void showProgress(boolean show) {
        progressIndicator.setVisible(show);
    }

    private void showError(String message) {
        showAlert(Alert.AlertType.ERROR, "Erreur", message);
    }

    private void showSuccess(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Succès", message);
    }

    private void showInfo(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Information", message);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}