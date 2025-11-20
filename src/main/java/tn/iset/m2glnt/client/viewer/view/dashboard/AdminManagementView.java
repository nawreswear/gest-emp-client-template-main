package tn.iset.m2glnt.client.viewer.view.dashboard;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import tn.iset.m2glnt.client.service.AdminRestService;

import java.util.List;
import java.util.Map;


public class AdminManagementView {
    private final AdminRestService adminService;
    private final TableView<Map<String, Object>> adminTable;
    private final ObservableList<Map<String, Object>> adminData;
    private ProgressIndicator progressIndicator;

    // Constructeur corrigé
    public AdminManagementView(AdminRestService adminService) {
        this.adminService = adminService;
        this.adminData = FXCollections.observableArrayList();
        this.adminTable = new TableView<>();
    }

    // Méthode createView() corrigée
    public VBox createView() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));

        // Titre
        Label title = new Label("Gestion des Administrateurs");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        // Boutons d'action
        HBox buttonBox = new HBox(10);
        Button refreshBtn = new Button("🔄 Actualiser");
        Button deleteBtn = new Button("🗑️ Supprimer");

        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

        buttonBox.getChildren().addAll(refreshBtn, deleteBtn);

        // Indicateur de progression
        progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);

        // Configuration du tableau
        setupAdminTable();

        // Actions des boutons
        refreshBtn.setOnAction(e -> loadAdmins());
        deleteBtn.setOnAction(e -> deleteSelectedAdmin());

        // Layout principal
        VBox contentBox = new VBox(10);
        contentBox.getChildren().addAll(buttonBox, progressIndicator, adminTable);
        VBox.setVgrow(adminTable, Priority.ALWAYS);

        container.getChildren().addAll(title, contentBox);

        // Chargement initial
        loadAdmins();

        return container;
    }

    private void setupAdminTable() {
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

        TableColumn<Map<String, Object>, String> cinCol = new TableColumn<>("CIN");
        cinCol.setCellValueFactory(data -> {
            Map<String, Object> row = data.getValue();
            Object value = row.get("cin");
            return new javafx.beans.property.SimpleStringProperty(value != null ? value.toString() : "");
        });

        adminTable.getColumns().addAll(idCol, nomCol, prenomCol, emailCol, cinCol);
        adminTable.setItems(adminData);
        adminTable.setStyle("-fx-font-size: 14px;");
    }

    private void loadAdmins() {
        showProgress(true);

        adminService.getAllAdmins().whenComplete((admins, throwable) -> {
            javafx.application.Platform.runLater(() -> {
                showProgress(false);

                if (throwable != null) {
                    String errorMessage = "Erreur lors du chargement des administrateurs";
                    if (throwable.getCause() != null) {
                        errorMessage += ": " + throwable.getCause().getMessage();
                    } else {
                        errorMessage += ": " + throwable.getMessage();
                    }
                    showError(errorMessage);
                    return;
                }

                if (admins != null && !admins.isEmpty()) {
                    adminData.clear();
                    adminData.addAll(admins);
                    showSuccess("Chargement réussi: " + admins.size() + " administrateur(s) trouvé(s)");
                } else {
                    adminData.clear();
                    showInfo("Aucun administrateur trouvé");
                }
            });
        });
    }

    private void deleteSelectedAdmin() {
        Map<String, Object> selected = adminTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner un administrateur à supprimer");
            return;
        }

        Long adminId = ((Number) selected.get("id")).longValue();

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer l'administrateur");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cet administrateur ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                showProgress(true);
                adminService.deleteAdmin(adminId).whenComplete((result, error) -> {
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
                            loadAdmins();
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