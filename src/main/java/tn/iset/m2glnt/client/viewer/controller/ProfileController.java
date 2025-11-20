package tn.iset.m2glnt.client.viewer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import tn.iset.m2glnt.client.model.User;
import tn.iset.m2glnt.client.model.UserSession;
import tn.iset.m2glnt.client.service.dao.UserService;

public class ProfileController {
    @FXML private Label nomLabel;
    @FXML private Label prenomLabel;
    @FXML private Label emailLabel;
    @FXML private Label typeLabel;
    @FXML private Label telLabel;
    @FXML private ProgressIndicator progressIndicator;

    @FXML
    private void initialize() {
        loadUserProfile();
    }

    private void loadUserProfile() {
        UserSession session = UserSession.getInstance();
        if (session.isLoggedIn()) {
            progressIndicator.setVisible(true);

            var userService = UserService.getUserById(session.getUserId());
            userService.setOnSucceeded(event -> {
                progressIndicator.setVisible(false);
                User user = userService.getValue();
                displayUserInfo(user);
            });

            userService.setOnFailed(event -> {
                progressIndicator.setVisible(false);
                // Afficher les informations de session en cache
                displaySessionInfo();
            });

            userService.start();
        } else {
            displaySessionInfo();
        }
    }

    private void displayUserInfo(User user) {
        nomLabel.setText(user.getNom() != null ? user.getNom() : "Non renseigné");
        prenomLabel.setText(user.getPrenom() != null ? user.getPrenom() : "Non renseigné");
        emailLabel.setText(user.getEmail() != null ? user.getEmail() : "Non renseigné");
        typeLabel.setText(user.getType() != null ? user.getType() : "Non renseigné");
        telLabel.setText(user.getTel() != null ? user.getTel() : "Non renseigné");
    }

    private void displaySessionInfo() {
        UserSession session = UserSession.getInstance();
        emailLabel.setText(session.getEmail() != null ? session.getEmail() : "Non renseigné");
        typeLabel.setText(session.getUserType() != null ? session.getUserType() : "Non renseigné");
    }

    @FXML
    private void handleLogout() {
        UserSession.getInstance().clearSession();
        // Retour à la page de login
        tn.iset.m2glnt.client.viewer.CalendarApp.showLoginView();
    }
}