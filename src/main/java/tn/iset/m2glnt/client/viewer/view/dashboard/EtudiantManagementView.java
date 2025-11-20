package tn.iset.m2glnt.client.viewer.view.dashboard;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import tn.iset.m2glnt.client.service.EtudiantRestService;

import java.util.Map;

public class EtudiantManagementView {
    private final EtudiantRestService etudiantService;
    private final TableView<Map<String, Object>> etudiantTable;
    private final ObservableList<Map<String, Object>> etudiantData;
    private ProgressIndicator progressIndicator;

    public EtudiantManagementView(EtudiantRestService etudiantService) {
        this.etudiantService = etudiantService;
        this.etudiantData = FXCollections.observableArrayList();
        this.etudiantTable = new TableView<>();
    }

    public VBox createView() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));

        Label title = new Label("Gestion des Étudiants");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        HBox buttonBox = new HBox(10);
        Button refreshBtn = new Button("🔄 Actualiser");
        Button deleteBtn = new Button("🗑️ Supprimer");

        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

        buttonBox.getChildren().addAll(refreshBtn, deleteBtn);

        progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);

        setupEtudiantTable();

        refreshBtn.setOnAction(e -> loadEtudiants());
        deleteBtn.setOnAction(e -> deleteSelectedEtudiant());

        VBox contentBox = new VBox(10);
        contentBox.getChildren().addAll(buttonBox, progressIndicator, etudiantTable);
        VBox.setVgrow(etudiantTable, Priority.ALWAYS);

        container.getChildren().addAll(title, contentBox);
        loadEtudiants();

        return container;
    }

    private void setupEtudiantTable() {
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

        etudiantTable.getColumns().addAll(idCol, nomCol, prenomCol, emailCol, cinCol);
        etudiantTable.setItems(etudiantData);
        etudiantTable.setStyle("-fx-font-size: 14px;");
    }

    private void loadEtudiants() {
        showProgress(true);
        etudiantService.getAllEtudiants().whenComplete((etudiants, throwable) -> {
            javafx.application.Platform.runLater(() -> {
                showProgress(false);
                if (throwable != null) {
                    showError("Erreur lors du chargement: " + getErrorMessage(throwable));
                    return;
                }
                if (etudiants != null && !etudiants.isEmpty()) {
                    etudiantData.clear();
                    etudiantData.addAll(etudiants);
                    showSuccess(etudiants.size() + " étudiant(s) trouvé(s)");
                } else {
                    etudiantData.clear();
                    showInfo("Aucun étudiant trouvé");
                }
            });
        });
    }

    private void deleteSelectedEtudiant() {
        Map<String, Object> selected = etudiantTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner un étudiant");
            return;
        }

        Long etudiantId = ((Number) selected.get("id")).longValue();

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setContentText("Supprimer cet étudiant ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                showProgress(true);
                etudiantService.deleteEtudiant(etudiantId).whenComplete((result, error) -> {
                    javafx.application.Platform.runLater(() -> {
                        showProgress(false);
                        if (error != null) {
                            showError("Erreur suppression: " + getErrorMessage(error));
                        } else {
                            showSuccess(result);
                            loadEtudiants();
                        }
                    });
                });
            }
        });
    }

    private String getErrorMessage(Throwable throwable) {
        if (throwable.getCause() != null) {
            return throwable.getCause().getMessage();
        }
        return throwable.getMessage();
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