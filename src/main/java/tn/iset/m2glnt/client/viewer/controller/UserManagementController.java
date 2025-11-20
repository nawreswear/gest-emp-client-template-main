package tn.iset.m2glnt.client.viewer.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.iset.m2glnt.client.model.User;
import tn.iset.m2glnt.client.model.UserSession;
import tn.iset.m2glnt.client.service.dao.UserService;

public class UserManagementController {
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Long> idColumn;
    @FXML private TableColumn<User, String> nomColumn;
    @FXML private TableColumn<User, String> prenomColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> typeColumn;
    @FXML private ProgressIndicator progressIndicator;

    private ObservableList<User> usersList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        setupTableColumns();
        loadUsers();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        usersTable.setItems(usersList);
    }

    private void loadUsers() {
        if (!UserSession.getInstance().isAdmin()) {
            showAlert("Accès refusé", "Seuls les administrateurs peuvent gérer les utilisateurs");
            return;
        }

        progressIndicator.setVisible(true);

        var userService = UserService.getAllUsers();
        userService.setOnSucceeded(event -> {
            progressIndicator.setVisible(false);
            usersList.setAll(userService.getValue());
        });

        userService.setOnFailed(event -> {
            progressIndicator.setVisible(false);
            showAlert("Erreur", "Impossible de charger les utilisateurs: " +
                    userService.getException().getMessage());
        });

        userService.start();
    }

    @FXML
    private void handleRefresh() {
        loadUsers();
    }

    @FXML
    private void handleDeleteUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation de suppression");
            confirmation.setHeaderText("Supprimer l'utilisateur");
            confirmation.setContentText("Êtes-vous sûr de vouloir supprimer " + selectedUser.getNom() + " " + selectedUser.getPrenom() + " ?");

            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    deleteUser(selectedUser.getId());
                }
            });
        } else {
            showAlert("Sélection requise", "Veuillez sélectionner un utilisateur à supprimer");
        }
    }

    private void deleteUser(Long userId) {
        var deleteService = UserService.deleteUser(userId);
        deleteService.setOnSucceeded(event -> {
            showAlert("Succès", "Utilisateur supprimé avec succès");
            loadUsers();
        });

        deleteService.setOnFailed(event -> {
            showAlert("Erreur", "Échec de la suppression: " + deleteService.getException().getMessage());
        });

        deleteService.start();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}