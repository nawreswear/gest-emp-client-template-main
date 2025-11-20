package tn.iset.m2glnt.client.viewer.view.dashboard;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import tn.iset.m2glnt.client.service.SalleRestService;
import tn.iset.m2glnt.client.service.dto.SalleRequest;

import java.util.Map;

public class SalleManagementView {
    private final SalleRestService salleService;
    private final TableView<Map<String, Object>> salleTable;
    private final ObservableList<Map<String, Object>> salleData;
    private ProgressIndicator progressIndicator;

    public SalleManagementView(SalleRestService salleService) {
        this.salleService = salleService;
        this.salleData = FXCollections.observableArrayList();
        this.salleTable = new TableView<>();
    }

    public VBox createView() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));

        Label title = new Label("Gestion des Salles");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

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

        progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);

        setupSalleTable();

        refreshBtn.setOnAction(e -> loadSalles());
        addBtn.setOnAction(e -> showAddSalleDialog());
        editBtn.setOnAction(e -> showEditSalleDialog());
        deleteBtn.setOnAction(e -> deleteSelectedSalle());

        VBox contentBox = new VBox(10);
        contentBox.getChildren().addAll(buttonBox, progressIndicator, salleTable);
        VBox.setVgrow(salleTable, Priority.ALWAYS);

        container.getChildren().addAll(title, contentBox);
        loadSalles();

        return container;
    }

    private void setupSalleTable() {
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

        TableColumn<Map<String, Object>, String> capaciteCol = new TableColumn<>("Capacité");
        capaciteCol.setCellValueFactory(data -> {
            Map<String, Object> row = data.getValue();
            Object value = row.get("capacite");
            return new javafx.beans.property.SimpleStringProperty(value != null ? value.toString() : "");
        });

        TableColumn<Map<String, Object>, String> equipementCol = new TableColumn<>("Équipement");
        equipementCol.setCellValueFactory(data -> {
            Map<String, Object> row = data.getValue();
            Object value = row.get("equipement");
            return new javafx.beans.property.SimpleStringProperty(value != null ? value.toString() : "");
        });

        salleTable.getColumns().addAll(idCol, nomCol, capaciteCol, equipementCol);
        salleTable.setItems(salleData);
        salleTable.setStyle("-fx-font-size: 14px;");
    }

    private void loadSalles() {
        showProgress(true);
        salleService.getAllSalles().whenComplete((salles, throwable) -> {
            javafx.application.Platform.runLater(() -> {
                showProgress(false);
                if (throwable != null) {
                    showError("Erreur lors du chargement: " + getErrorMessage(throwable));
                    return;
                }
                if (salles != null && !salles.isEmpty()) {
                    salleData.clear();
                    salleData.addAll(salles);
                    showSuccess(salles.size() + " salle(s) trouvée(s)");
                } else {
                    salleData.clear();
                    showInfo("Aucune salle trouvée");
                }
            });
        });
    }

    private void showAddSalleDialog() {
        Dialog<SalleRequest> dialog = new Dialog<>();
        dialog.setTitle("Ajouter une salle");

        ButtonType createButtonType = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nomField = new TextField();
        TextField batimentField = new TextField(); // 🔥 Changé de "equipementField" à "batimentField"
        TextField capaciteField = new TextField();

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Bâtiment:"), 0, 1); // 🔥 Changé de "Équipement" à "Bâtiment"
        grid.add(batimentField, 1, 1);
        grid.add(new Label("Capacité:"), 0, 2);
        grid.add(capaciteField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                try {
                    SalleRequest request = new SalleRequest();
                    request.setNom(nomField.getText());
                    request.setBatiment(batimentField.getText()); // 🔥 Changé de setEquipement à setBatiment

                    // 🔥 Conversion de String vers Integer pour la capacité
                    String capaciteText = capaciteField.getText();
                    if (capaciteText != null && !capaciteText.trim().isEmpty()) {
                        request.setCapacite(Integer.parseInt(capaciteText));
                    } else {
                        request.setCapacite(0); // Valeur par défaut
                    }

                    return request;
                } catch (NumberFormatException e) {
                    // 🔥 Gestion d'erreur pour la conversion de capacité
                    showError("La capacité doit être un nombre valide");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(salleRequest -> {
            if (salleRequest != null) { // 🔥 Vérification null ajoutée
                showProgress(true);
                salleService.addSalle(salleRequest).whenComplete((result, error) -> {
                    javafx.application.Platform.runLater(() -> {
                        showProgress(false);
                        if (error != null) {
                            showError("Erreur ajout: " + getErrorMessage(error));
                        } else {
                            showSuccess(result);
                            loadSalles();
                        }
                    });
                });
            }
        });
    }

    private void showEditSalleDialog() {
        Map<String, Object> selected = salleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner une salle");
            return;
        }

        // Implémentation similaire à showAddSalleDialog avec pré-remplissage
    }

    private void deleteSelectedSalle() {
        Map<String, Object> selected = salleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner une salle");
            return;
        }

        Long salleId = ((Number) selected.get("id")).longValue();

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setContentText("Supprimer cette salle ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                showProgress(true);
                salleService.deleteSalle(salleId).whenComplete((result, error) -> {
                    javafx.application.Platform.runLater(() -> {
                        showProgress(false);
                        if (error != null) {
                            showError("Erreur suppression: " + getErrorMessage(error));
                        } else {
                            showSuccess(result);
                            loadSalles();
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