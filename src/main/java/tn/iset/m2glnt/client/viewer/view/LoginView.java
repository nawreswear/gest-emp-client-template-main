package tn.iset.m2glnt.client.viewer.view;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import tn.iset.m2glnt.client.model.User;
import tn.iset.m2glnt.client.model.UserSession;
import tn.iset.m2glnt.client.service.dao.ApiClient;
import tn.iset.m2glnt.client.service.dao.AuthService;
import tn.iset.m2glnt.client.service.dao.UserService;
import tn.iset.m2glnt.client.viewer.CalendarApp;

public class LoginView {
    private Scene scene;
    private Button loginButton;
    private TextField emailField;
    private PasswordField passwordField;

    public LoginView() {
        System.out.println("🏗️ Construction de LoginView...");
        initializeView();
    }

    private void initializeView() {
        try {
            // Container principal avec fond gradiant
            StackPane root = new StackPane();
            root.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea, #764ba2);");

            // Container du formulaire
            VBox mainContainer = new VBox(30);
            mainContainer.setAlignment(Pos.CENTER);
            mainContainer.setPadding(new Insets(50));
            mainContainer.setMaxWidth(500);
            mainContainer.setMaxHeight(600);

            // Header
            Label headerLabel = new Label("Application de Gestion");
            headerLabel.setFont(Font.font("System", FontWeight.BOLD, 32));
            headerLabel.setTextFill(Color.WHITE);
            headerLabel.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);");

            // Formulaire de connexion
            VBox loginForm = createLoginForm();

            mainContainer.getChildren().addAll(headerLabel, loginForm);
            root.getChildren().add(mainContainer);

            this.scene = new Scene(root, 900, 700);
            System.out.println("✅ LoginView initialisé avec succès");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'initialisation de LoginView: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur critique dans LoginView", e);
        }
    }

    private VBox createLoginForm() {
        VBox formContainer = new VBox(20);
        formContainer.setPadding(new Insets(40, 50, 40, 50));
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 5);");

        // Titre du formulaire
        Label titleLabel = new Label("Connexion");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#2D2D2D"));
        titleLabel.setPadding(new Insets(0, 0, 20, 0));

        // Champ email
        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2D2D2D;");

        emailField = new TextField();
        emailField.setPromptText("votre@email.com");
        emailField.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #E0E0E0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12; -fx-font-size: 14;");
        emailField.setPrefWidth(300);
        emailField.setMaxWidth(300);

        // Champ mot de passe
        Label passwordLabel = new Label("Mot de passe:");
        passwordLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2D2D2D;");

        passwordField = new PasswordField();
        passwordField.setPromptText("Votre mot de passe");
        passwordField.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #E0E0E0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12; -fx-font-size: 14;");
        passwordField.setPrefWidth(300);
        passwordField.setMaxWidth(300);

        // Bouton de connexion
        loginButton = new Button("Se connecter");
        loginButton.setStyle("-fx-background-color: #E85B8A; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16; -fx-background-radius: 8; -fx-padding: 15 40; -fx-cursor: hand;");
        loginButton.setPrefWidth(300);
        loginButton.setMaxWidth(300);

        // Effet hover pour le bouton
        loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-background-color: #D64A7A; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16; -fx-background-radius: 8; -fx-padding: 15 40; -fx-cursor: hand;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle("-fx-background-color: #E85B8A; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16; -fx-background-radius: 8; -fx-padding: 15 40; -fx-cursor: hand;"));

        // Lien d'inscription
        Label registerLink = new Label("Créer un compte");
        registerLink.setStyle("-fx-text-fill: #E85B8A; -fx-font-weight: bold; -fx-cursor: hand; -fx-underline: true;");
        registerLink.setOnMouseClicked(e -> navigateToRegister());

        // Gestion des événements
        loginButton.setOnAction(e -> handleLogin(emailField.getText(), passwordField.getText()));

        // Raccourci Entrée
        emailField.setOnAction(e -> passwordField.requestFocus());
        passwordField.setOnAction(e -> handleLogin(emailField.getText(), passwordField.getText()));

        // Organisation des champs
        VBox emailContainer = new VBox(8);
        emailContainer.setAlignment(Pos.CENTER_LEFT);
        emailContainer.getChildren().addAll(emailLabel, emailField);

        VBox passwordContainer = new VBox(8);
        passwordContainer.setAlignment(Pos.CENTER_LEFT);
        passwordContainer.getChildren().addAll(passwordLabel, passwordField);

        formContainer.getChildren().addAll(
                titleLabel,
                emailContainer,
                passwordContainer,
                loginButton,
                registerLink
        );

        return formContainer;
    }

    private void handleLoginSuccess(String email, String token, User user) {
        setLoadingState(false);

        if (user != null) {
            System.out.println("✅ Utilisateur trouvé : " + user.getNom());

            // 🔥 STOCKER TOUTES LES INFORMATIONS DE L'UTILISATEUR
            CalendarApp.setCurrentUserFull(
                    user.getEmail(),
                    user.getNom(),
                    user.getPrenom(),
                    user.getType(),
                    user.getTel() != null ? user.getTel() : "",
                    user.getCin() != null ? user.getCin() : "",
                    user.getPhoto() != null ? user.getPhoto() : ""
            );

            UserSession.getInstance().setCurrentUser(
                    user.getId(),
                    user.getEmail(),
                    user.getType(),
                    token
            );
            ApiClient.setAuthToken(token);
            showTemporaryNotification("✅ Connexion réussie!", 1000);

            PauseTransition navigationDelay = new PauseTransition(Duration.millis(1200));
            navigationDelay.setOnFinished(e -> navigateToMainPage());
            navigationDelay.play();
        } else {
            showError("Utilisateur introuvable dans la base");
        }
    }

    private boolean isAuthenticating = false;

    private void handleLogin(String email, String password) {
        if (isAuthenticating) return; // empêche le double appel
        isAuthenticating = true;

        System.out.println("🔐 Tentative de connexion avec: " + email);

        if (email == null || email.trim().isEmpty()) {
            showError("Veuillez saisir votre email");
            isAuthenticating = false;
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            showError("Veuillez saisir votre mot de passe");
            isAuthenticating = false;
            return;
        }

        setLoadingState(true);
        authenticateUser(email, password);
    }


    private void authenticateUser(String email, String password) {
        try {
            System.out.println("🔐 Authentification en cours pour: " + email);

            // Utilisation du AuthService existant
            var authService = AuthService.login(email, password);

            authService.setOnSucceeded(event -> {
                try {
                    var jwtResponse = authService.getValue();
                    if (jwtResponse != null && jwtResponse.getToken() != null) {
                        System.out.println("✅ Authentification réussie, token reçu");

                        // 🔥 MAINTENANT ON RÉCUPÈRE LES DONNÉES UTILISATEUR APRÈS L'AUTH
                        var userService = UserService.getUserByEmail(email);
                        userService.setOnSucceeded(userEvent -> {
                            try {
                                User user = userService.getValue();
                                handleLoginSuccess(email, jwtResponse.getToken(), user);
                            } finally {
                                isAuthenticating = false; // ✅ Garantir la réinitialisation
                            }
                        });

                        userService.setOnFailed(userEvent -> {
                            try {
                                setLoadingState(false);
                                Throwable ex = userService.getException();
                                showError("Erreur lors de la récupération du profil: " + ex.getMessage());
                            } finally {
                                isAuthenticating = false; // ✅ Garantir la réinitialisation
                            }
                        });

                        userService.start();

                    } else {
                        setLoadingState(false);
                        showError("Échec de l'authentification: réponse invalide");
                        isAuthenticating = false; // ✅ Réinitialiser après échec
                    }
                } catch (Exception e) {
                    setLoadingState(false);
                    showError("Erreur lors du traitement de la réponse: " + e.getMessage());
                    isAuthenticating = false; // ✅ Réinitialiser en cas d'exception
                }
            });

            authService.setOnFailed(event -> {
                try {
                    setLoadingState(false);
                    Throwable ex = authService.getException();
                    System.err.println("❌ Erreur d'authentification: " + ex.getMessage());
                    showError("Échec de l'authentification: " + ex.getMessage());
                } finally {
                    isAuthenticating = false; // ✅ GARANTIR la réinitialisation
                }
            });

            authService.start();

        } catch (Exception e) {
            setLoadingState(false);
            isAuthenticating = false; // ✅ Réinitialiser en cas d'exception
            System.err.println("❌ Erreur d'authentification: " + e.getMessage());
            showError("Erreur lors de l'authentification: " + e.getMessage());
        }
    }

    private void setLoadingState(boolean isLoading) {
        if (isLoading) {
            loginButton.setText("Connexion...");
            loginButton.setDisable(true);
            loginButton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666; -fx-font-weight: bold; -fx-font-size: 16; -fx-background-radius: 8; -fx-padding: 15 40;");
        } else {
            loginButton.setText("Se connecter");
            loginButton.setDisable(false);
            loginButton.setStyle("-fx-background-color: #E85B8A; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16; -fx-background-radius: 8; -fx-padding: 15 40; -fx-cursor: hand;");
        }
    }

    private void showTemporaryNotification(String message, int durationMs) {
        // Création d'une notification simple
        Alert notification = new Alert(Alert.AlertType.INFORMATION);
        notification.setTitle("Information");
        notification.setHeaderText(null);
        notification.setContentText(message);

        // Fermeture automatique après la durée spécifiée
        PauseTransition delay = new PauseTransition(Duration.millis(durationMs));
        delay.setOnFinished(event -> notification.close());
        delay.play();

        notification.show();
    }

    private void navigateToMainPage() {
        System.out.println("🚀 Navigation vers la page principale...");
        CalendarApp.showCalendarView();
    }

    private void navigateToRegister() {
        System.out.println("📝 Navigation vers l'inscription...");
        CalendarApp.showRegisterView();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de connexion");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Scene constructScene() {
        return this.scene;
    }
}