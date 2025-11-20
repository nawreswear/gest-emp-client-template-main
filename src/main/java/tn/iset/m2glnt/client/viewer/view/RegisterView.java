package tn.iset.m2glnt.client.viewer.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.concurrent.Service;
import tn.iset.m2glnt.client.model.SignupRequest;
import tn.iset.m2glnt.client.viewer.CalendarApp;
import tn.iset.m2glnt.client.model.JwtResponse;
import tn.iset.m2glnt.client.model.UserSession;
import tn.iset.m2glnt.client.service.dao.ApiClient;
import tn.iset.m2glnt.client.service.dao.AuthService;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RegisterView {
    private Scene scene;
    private ComboBox<String> typeComboBox;
    private ProgressIndicator progressIndicator;
    private Button registerButton;
    private Button cancelButton;

    // Champs du formulaire
    private TextField nomField;
    private TextField prenomField;
    private TextField emailField;
    private TextField telField;
    private TextField cinField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private TextArea adresseField;

    // Styles constants
    private final String LABEL_STYLE = "-fx-font-weight: bold; -fx-text-fill: #2D2D2D; -fx-font-size: 12px; -fx-padding: 0 0 3 0;";
    private final String FIELD_STYLE = "-fx-background-color: #F8F9FA; -fx-border-color: #E0E0E0; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 8; -fx-font-size: 12px;";
    private final String ERROR_FIELD_STYLE = "-fx-background-color: #F8F9FA; -fx-border-color: #dc3545; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 8; -fx-font-size: 12px;";

    public RegisterView() {
        System.out.println("🏗️ Construction de RegisterView...");
        initializeView();
    }

    private void initializeView() {
        try {
            // Container principal avec fond gradiant
            StackPane root = new StackPane();
            root.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea, #764ba2);");

            // Container du formulaire - HAUTEUR FORTEMENT RÉDUITE
            VBox mainContainer = new VBox(20);
            mainContainer.setAlignment(Pos.CENTER);
            mainContainer.setPadding(new Insets(20));
            mainContainer.setMaxWidth(1000);
            mainContainer.setMaxHeight(650);

            // Header
            Label headerLabel = new Label("Créer un compte");
            headerLabel.setFont(Font.font("System", FontWeight.BOLD, 30));
            headerLabel.setTextFill(Color.WHITE);
            headerLabel.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);");

            // Formulaire d'inscription
            VBox registerForm = createRegisterForm();

            mainContainer.getChildren().addAll(headerLabel, registerForm);
            root.getChildren().add(mainContainer);

            // SCÈNE - HAUTEUR FORTEMENT RÉDUITE
            this.scene = new Scene(root, 1300, 650);
            System.out.println("✅ RegisterView initialisé avec succès");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'initialisation de RegisterView: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur critique dans RegisterView", e);
        }
    }

    private VBox createRegisterForm() {
        VBox formContainer = new VBox(12);
        formContainer.setPadding(new Insets(25, 35, 25, 35));
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 5);");
        formContainer.setMinWidth(900);

        // Titre du formulaire
        Label titleLabel = new Label("Inscription");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#2D2D2D"));
        titleLabel.setPadding(new Insets(0, 0, 12, 0));

        // Grille pour les champs - ESPACEMENT FORTEMENT RÉDUIT
        GridPane formGrid = new GridPane();
        formGrid.setHgap(12);
        formGrid.setVgap(10);
        formGrid.setAlignment(Pos.CENTER);
        formGrid.setPadding(new Insets(8));

        // === PREMIÈRE LIGNE : Nom et Prénom ===
        Label nomLabel = new Label("Nom :");
        nomLabel.setStyle(LABEL_STYLE);

        nomField = new TextField();
        nomField.setPromptText("Votre nom");
        nomField.setStyle(FIELD_STYLE);
        nomField.setPrefWidth(300);
        nomField.setPrefHeight(32);

        Label prenomLabel = new Label("Prénom :");
        prenomLabel.setStyle(LABEL_STYLE);

        prenomField = new TextField();
        prenomField.setPromptText("Votre prénom");
        prenomField.setStyle(FIELD_STYLE);
        prenomField.setPrefWidth(300);
        prenomField.setPrefHeight(32);

        // === DEUXIÈME LIGNE : Email et Téléphone ===
        Label emailLabel = new Label("Email :");
        emailLabel.setStyle(LABEL_STYLE);

        emailField = new TextField();
        emailField.setPromptText("votre@email.com");
        emailField.setStyle(FIELD_STYLE);
        emailField.setPrefWidth(300);
        emailField.setPrefHeight(32);

        Label telLabel = new Label("Téléphone :");
        telLabel.setStyle(LABEL_STYLE);

        telField = new TextField();
        telField.setPromptText("XX XXX XXX");
        telField.setStyle(FIELD_STYLE);
        telField.setPrefWidth(300);
        telField.setPrefHeight(32);

        // === TROISIÈME LIGNE : CIN et Type d'utilisateur ===
        Label cinLabel = new Label("CIN :");
        cinLabel.setStyle(LABEL_STYLE);

        cinField = new TextField();
        cinField.setPromptText("Numéro CIN");
        cinField.setStyle(FIELD_STYLE);
        cinField.setPrefWidth(300);
        cinField.setPrefHeight(32);

        Label typeLabel = new Label("Type :");
        typeLabel.setStyle(LABEL_STYLE);

        typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Étudiant", "Enseignant", "Admin");
        typeComboBox.setValue("Étudiant");
        typeComboBox.setStyle(FIELD_STYLE + " -fx-font-size: 11px;");
        typeComboBox.setPrefWidth(300);
        typeComboBox.setPrefHeight(32);

        // === QUATRIÈME LIGNE : Mot de passe et Confirmation ===
        Label passwordLabel = new Label("Mot de passe :");
        passwordLabel.setStyle(LABEL_STYLE);

        passwordField = new PasswordField();
        passwordField.setPromptText("Minimum 6 caractères");
        passwordField.setStyle(FIELD_STYLE);
        passwordField.setPrefWidth(300);
        passwordField.setPrefHeight(32);

        Label confirmPasswordLabel = new Label("Confirmation :");
        confirmPasswordLabel.setStyle(LABEL_STYLE);

        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Répétez le mot de passe");
        confirmPasswordField.setStyle(FIELD_STYLE);
        confirmPasswordField.setPrefWidth(300);
        confirmPasswordField.setPrefHeight(32);

        // === ADRESSE (ligne optionnelle) ===
        Label adresseLabel = new Label("Adresse :");
        adresseLabel.setStyle(LABEL_STYLE);

        adresseField = new TextArea();
        adresseField.setPromptText("Adresse complète (optionnel)");
        adresseField.setStyle(FIELD_STYLE);
        adresseField.setPrefWidth(615);
        adresseField.setPrefHeight(50);
        adresseField.setWrapText(true);

        // POSITIONNEMENT DANS LA GRILLE
        // Ligne 1 - Nom et Prénom
        formGrid.add(nomLabel, 0, 0);
        formGrid.add(nomField, 1, 0);
        formGrid.add(prenomLabel, 2, 0);
        formGrid.add(prenomField, 3, 0);

        // Ligne 2 - Email et Téléphone
        formGrid.add(emailLabel, 0, 1);
        formGrid.add(emailField, 1, 1);
        formGrid.add(telLabel, 2, 1);
        formGrid.add(telField, 3, 1);

        // Ligne 3 - CIN et Type
        formGrid.add(cinLabel, 0, 2);
        formGrid.add(cinField, 1, 2);
        formGrid.add(typeLabel, 2, 2);
        formGrid.add(typeComboBox, 3, 2);

        // Ligne 4 - Mot de passe et Confirmation
        formGrid.add(passwordLabel, 0, 3);
        formGrid.add(passwordField, 1, 3);
        formGrid.add(confirmPasswordLabel, 2, 3);
        formGrid.add(confirmPasswordField, 3, 3);

        // Ligne 5 - Adresse (sur toute la largeur)
        formGrid.add(adresseLabel, 0, 4);
        formGrid.add(adresseField, 1, 4, 3, 1);

        // Indicateur de progression
        progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);
        progressIndicator.setMaxSize(25, 25);

        // BOUTONS - TRÈS COMPACTS
        HBox buttonsContainer = new HBox(12);
        buttonsContainer.setAlignment(Pos.CENTER);
        buttonsContainer.setPadding(new Insets(15, 0, 0, 0));

        registerButton = new Button("Créer le compte");
        registerButton.setStyle("-fx-background-color: #E85B8A; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 5; -fx-padding: 10 35; -fx-cursor: hand;");

        cancelButton = new Button("Annuler");
        cancelButton.setStyle("-fx-background-color: #6C757D; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 5; -fx-padding: 10 35; -fx-cursor: hand;");

        // Effets hover
        setupButtonHoverEffects();

        HBox progressContainer = new HBox(8, registerButton, progressIndicator);
        progressContainer.setAlignment(Pos.CENTER);

        buttonsContainer.getChildren().addAll(progressContainer, cancelButton);

        // LIEN DE CONNEXION - TRÈS COMPACT
        Label loginLink = new Label("Déjà un compte ? Se connecter");
        loginLink.setStyle("-fx-text-fill: #E85B8A; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-underline: true; -fx-padding: 8 0 0 0;");
        loginLink.setOnMouseClicked(e -> navigateToLogin());

        // Gestion des événements
        registerButton.setOnAction(e -> handleRegistration());
        cancelButton.setOnAction(e -> navigateToLogin());

        // Raccourci Entrée
        setupEnterKeyNavigation();

        // Configuration des validations en temps réel
        setupRealTimeValidation();

        // Configuration des tooltips
        showPasswordTips();

        formContainer.getChildren().addAll(
                titleLabel,
                formGrid,
                buttonsContainer,
                loginLink
        );

        return formContainer;
    }

    private void setupButtonHoverEffects() {
        registerButton.setOnMouseEntered(e ->
                registerButton.setStyle("-fx-background-color: #D64A7A; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 5; -fx-padding: 10 35; -fx-cursor: hand;")
        );
        registerButton.setOnMouseExited(e ->
                registerButton.setStyle("-fx-background-color: #E85B8A; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 5; -fx-padding: 10 35; -fx-cursor: hand;")
        );

        cancelButton.setOnMouseEntered(e ->
                cancelButton.setStyle("-fx-background-color: #5A6268; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 5; -fx-padding: 10 35; -fx-cursor: hand;")
        );
        cancelButton.setOnMouseExited(e ->
                cancelButton.setStyle("-fx-background-color: #6C757D; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 5; -fx-padding: 10 35; -fx-cursor: hand;")
        );
    }

    private void setupEnterKeyNavigation() {
        nomField.setOnAction(e -> prenomField.requestFocus());
        prenomField.setOnAction(e -> emailField.requestFocus());
        emailField.setOnAction(e -> telField.requestFocus());
        telField.setOnAction(e -> cinField.requestFocus());
        cinField.setOnAction(e -> passwordField.requestFocus());
        passwordField.setOnAction(e -> confirmPasswordField.requestFocus());
        confirmPasswordField.setOnAction(e -> handleRegistration());
    }

    private void verifyFieldsInitialization() {
        System.out.println("=== VERIFICATION CHAMPS ===");
        System.out.println("nomField: " + (nomField != null ? "✓" : "✗ NULL"));
        System.out.println("prenomField: " + (prenomField != null ? "✓" : "✗ NULL"));
        System.out.println("emailField: " + (emailField != null ? "✓" : "✗ NULL"));
        System.out.println("telField: " + (telField != null ? "✓" : "✗ NULL"));
        System.out.println("cinField: " + (cinField != null ? "✓" : "✗ NULL"));
        System.out.println("typeComboBox: " + (typeComboBox != null ? "✓" : "✗ NULL"));
        System.out.println("passwordField: " + (passwordField != null ? "✓" : "✗ NULL"));
        System.out.println("confirmPasswordField: " + (confirmPasswordField != null ? "✓" : "✗ NULL"));
        System.out.println("===========================");
    }

    private void navigateToCalendar(String userEmail) {
        System.out.println("🚀 Navigation vers le calendrier après inscription: " + userEmail);
        CalendarApp.showCalendarView(userEmail);
    }

    private void navigateToLogin() {
        System.out.println("🔙 Retour à la page de connexion...");
        CalendarApp.showLoginView();
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur d'inscription");
            alert.setHeaderText(null);
            alert.setContentText(message);

            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.getDialogPane().setPrefSize(400, 200);

            alert.showAndWait();
        });
    }

    private void showSuccess(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Inscription réussie");
            alert.setHeaderText(null);
            alert.setContentText(message);

            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.getDialogPane().setPrefSize(400, 200);

            alert.showAndWait();
        });
    }

    public Scene constructScene() {
        return this.scene;
    }

    private void handleRegistration() {
        System.out.println("📝 Traitement de l'inscription...");

        // Vérifier l'initialisation des champs
        verifyFieldsInitialization();

        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String email = emailField.getText();
        String tel = telField.getText();
        String cin = cinField.getText();
        String type = typeComboBox.getValue();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // DEBUG: Afficher les valeurs brutes
        System.out.println("=== DONNÉES BRUTES ===");
        System.out.println("Nom: " + nom);
        System.out.println("Prénom: " + prenom);
        System.out.println("Email: " + email);
        System.out.println("Tél: " + tel);
        System.out.println("CIN: " + cin);
        System.out.println("Type: " + type);
        System.out.println("Password: " + (password != null ? "***" : "null"));
        System.out.println("Confirm: " + (confirmPassword != null ? "***" : "null"));
        System.out.println("======================");

        // Validation des champs
        if (!validateFields(nom, prenom, email, tel, cin, password, confirmPassword)) {
            return;
        }

        // Convertir le type en minuscules
        String finalType = convertUserTypeToLowerCase(type);
        System.out.println("🎯 Type converti: '" + finalType + "'");

        // Créer la requête finale
        SignupRequest signupRequest = createValidatedSignupRequest(nom, prenom, email, tel, finalType, cin, password);

        // Afficher l'indicateur de progression
        showProgressIndicator(true);

        // Appeler le service d'inscription
        Service<JwtResponse> registerService = AuthService.register(signupRequest);

        registerService.setOnSucceeded(event -> {
            showProgressIndicator(false);
            try {
                JwtResponse response = registerService.getValue();
                handleRegistrationSuccess(response, nom, prenom, email, finalType);
            } catch (Exception e) {
                handleRegistrationError(e, "traitement de la réponse");
            }
        });

        registerService.setOnFailed(event -> {
            showProgressIndicator(false);
            handleRegistrationError(registerService.getException(), "appel API");
        });

        registerService.start();
    }

    private void showProgressIndicator(boolean show) {
        Platform.runLater(() -> {
            progressIndicator.setVisible(show);
            registerButton.setDisable(show);
            cancelButton.setDisable(show);
        });
    }

    private String convertUserTypeToLowerCase(String frenchType) {
        if (frenchType == null) return "etudiant";

        switch (frenchType.toLowerCase()) {
            case "étudiant":
            case "etudiant":
            case "étudiante":
            case "etudiante": return "etudiant";
            case "enseignant":
            case "enseignante": return "enseignant";
            case "admin":
            case "administrateur": return "admin";
            default: return "etudiant";
        }
    }

    private SignupRequest createValidatedSignupRequest(String nom, String prenom, String email,
                                                       String tel, String type, String cin, String password) {
        SignupRequest request = new SignupRequest();
        request.setNom(nom != null ? nom.trim() : "");
        request.setPrenom(prenom != null ? prenom.trim() : "");
        request.setEmail(email != null ? email.trim().toLowerCase() : "");
        request.setTel(tel != null ? tel.trim() : "");
        request.setType(type != null ? type.trim().toLowerCase() : "etudiant");
        request.setCin(cin != null ? cin.trim() : "");
        request.setPassword(password != null ? password : "");

        System.out.println("✅ Requête FINALE créée:");
        System.out.println("  Nom: '" + request.getNom() + "'");
        System.out.println("  Prénom: '" + request.getPrenom() + "'");
        System.out.println("  Email: '" + request.getEmail() + "'");
        System.out.println("  Tél: '" + request.getTel() + "'");
        System.out.println("  Type: '" + request.getType() + "'");
        System.out.println("  CIN: '" + request.getCin() + "'");
        System.out.println("  Password: " + (request.getPassword() != null ? "***" : "null"));

        return request;
    }

    private boolean validateFields(String nom, String prenom, String email, String tel,
                                   String cin, String password, String confirmPassword) {
        StringBuilder errors = new StringBuilder();

        if (nom == null || nom.trim().isEmpty()) {
            errors.append("• Le nom est obligatoire\n");
        } else if (nom.trim().length() < 2) {
            errors.append("• Le nom doit contenir au moins 2 caractères\n");
        }

        if (prenom == null || prenom.trim().isEmpty()) {
            errors.append("• Le prénom est obligatoire\n");
        } else if (prenom.trim().length() < 2) {
            errors.append("• Le prénom doit contenir au moins 2 caractères\n");
        }

        if (email == null || email.trim().isEmpty()) {
            errors.append("• L'email est obligatoire\n");
        } else if (!isValidEmail(email)) {
            errors.append("• Veuillez saisir une adresse email valide\n");
        }

        if (tel == null || tel.trim().isEmpty()) {
            errors.append("• Le numéro de téléphone est obligatoire\n");
        } else if (tel.trim().length() < 8) {
            errors.append("• Le numéro de téléphone doit contenir au moins 8 chiffres\n");
        }

        if (cin == null || cin.trim().isEmpty()) {
            errors.append("• Le numéro CIN est obligatoire\n");
        } else if (cin.trim().length() < 4) {
            errors.append("• Le numéro CIN doit contenir au moins 4 caractères\n");
        }

        if (password == null || password.length() < 6) {
            errors.append("• Le mot de passe doit contenir au moins 6 caractères\n");
        }

        if (!password.equals(confirmPassword)) {
            errors.append("• Les mots de passe ne correspondent pas\n");
        }

        if (errors.length() > 0) {
            showError("Veuillez corriger les erreurs suivantes:\n\n" + errors.toString());
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email != null && email.matches(emailRegex);
    }

    private void handleRegistrationSuccess(JwtResponse response, String nom, String prenom, String email, String type) {
        System.out.println("✅ Inscription réussie pour: " + email);

        // Stocker les informations utilisateur
        UserSession.getInstance().setCurrentUser(
                response.getId(),
                response.getEmail(),
                response.getType(),
                response.getToken()
        );

        // Définir le token d'authentification
        ApiClient.setAuthToken(response.getToken());

        showSuccess("Compte créé avec succès !\n\n" +
                "Nom: " + nom + " " + prenom + "\n" +
                "Email: " + email + "\n" +
                "Type: " + type + "\n\n" +
                "Redirection vers le calendrier...");

        // Rediriger vers le calendrier
        navigateToCalendar(email);
    }

    private void handleRegistrationError(Throwable exception, String context) {
        String errorMessage = exception != null ? exception.getMessage() : "Erreur inconnue";
        System.err.println("❌ Échec de l'inscription (" + context + "): " + errorMessage);

        if (exception != null) {
            exception.printStackTrace();
        }

        String userMessage = "Erreur lors de l'inscription.\n";
        userMessage += "Veuillez vérifier que :\n";
        userMessage += "• Tous les champs sont correctement remplis\n";
        userMessage += "• L'email n'est pas déjà utilisé\n";
        userMessage += "• Les données respectent les formats requis\n";
        userMessage += "• Les noms contiennent au moins 2 caractères\n\n";

        // Ajouter des détails spécifiques selon le type d'erreur
        if (errorMessage.contains("409") || errorMessage.contains("Conflict")) {
            userMessage += "Détail: Email ou CIN déjà utilisé\n";
        } else if (errorMessage.contains("400") || errorMessage.contains("Bad Request")) {
            userMessage += "Détail: Données invalides envoyées au serveur\n";
        } else if (errorMessage.contains("500") || errorMessage.contains("Internal Server Error")) {
            userMessage += "Détail: Erreur interne du serveur\n";
        } else if (errorMessage.contains("Connection") || errorMessage.contains("timeout")) {
            userMessage += "Détail: Problème de connexion au serveur\n";
        }

        userMessage += "Détail technique: " + errorMessage;

        showError(userMessage);
    }

    private String readResponse(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "{}";
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    }

    // Méthodes supplémentaires pour la gestion des données

    /**
     * Réinitialise tous les champs du formulaire
     */
    public void resetForm() {
        Platform.runLater(() -> {
            nomField.clear();
            prenomField.clear();
            emailField.clear();
            telField.clear();
            cinField.clear();
            passwordField.clear();
            confirmPasswordField.clear();
            adresseField.clear();
            typeComboBox.setValue("Étudiant");
            showProgressIndicator(false);

            // Réinitialiser les styles
            emailField.setStyle(FIELD_STYLE);
            confirmPasswordField.setStyle(FIELD_STYLE);
        });
    }

    /**
     * Vérifie si le formulaire est vide
     */
    public boolean isFormEmpty() {
        return nomField.getText().trim().isEmpty() &&
                prenomField.getText().trim().isEmpty() &&
                emailField.getText().trim().isEmpty() &&
                telField.getText().trim().isEmpty() &&
                cinField.getText().trim().isEmpty() &&
                passwordField.getText().isEmpty() &&
                confirmPasswordField.getText().isEmpty() &&
                adresseField.getText().trim().isEmpty();
    }

    /**
     * Récupère les données du formulaire sous forme de SignupRequest
     */
    public SignupRequest getFormData() {
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String email = emailField.getText();
        String tel = telField.getText();
        String cin = cinField.getText();
        String type = typeComboBox.getValue();
        String password = passwordField.getText();
        String finalType = convertUserTypeToLowerCase(type);

        return createValidatedSignupRequest(nom, prenom, email, tel, finalType, cin, password);
    }

    /**
     * Teste la connexion au serveur d'inscription
     */
    public boolean testServerConnection() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:3003/signup").openConnection();
            connection.setRequestMethod("OPTIONS");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);

            int responseCode = connection.getResponseCode();
            connection.disconnect();

            return responseCode == 200 || responseCode == 204;
        } catch (Exception e) {
            System.err.println("❌ Test de connexion échoué: " + e.getMessage());
            return false;
        }
    }

    /**
     * Valide la force du mot de passe
     */
    private boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }

        // Vérifier la présence d'au moins une majuscule, une minuscule et un chiffre
        boolean hasUpperCase = !password.equals(password.toLowerCase());
        boolean hasLowerCase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");

        return hasUpperCase && hasLowerCase && hasDigit;
    }

    /**
     * Affiche des conseils pour le mot de passe
     */
    private void showPasswordTips() {
        String tips = "Pour un mot de passe sécurisé :\n" +
                "• Au moins 6 caractères\n" +
                "• Au moins une majuscule\n" +
                "• Au moins une minuscule\n" +
                "• Au moins un chiffre\n" +
                "• Évitez les mots courants";

        Tooltip tooltip = new Tooltip(tips);
        tooltip.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        passwordField.setTooltip(tooltip);
    }

    /**
     * Configure les validateurs en temps réel
     */
    private void setupRealTimeValidation() {
        // Validation email en temps réel
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !isValidEmail(newValue)) {
                emailField.setStyle(ERROR_FIELD_STYLE);
            } else {
                emailField.setStyle(FIELD_STYLE);
            }
        });

        // Validation confirmation mot de passe en temps réel
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !newValue.equals(passwordField.getText())) {
                confirmPasswordField.setStyle(ERROR_FIELD_STYLE);
            } else {
                confirmPasswordField.setStyle(FIELD_STYLE);
            }
        });

        // Validation force du mot de passe
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !isPasswordStrong(newValue)) {
                passwordField.setStyle(ERROR_FIELD_STYLE);
            } else {
                passwordField.setStyle(FIELD_STYLE);
            }

            // Mettre à jour aussi la confirmation
            if (!confirmPasswordField.getText().isEmpty()) {
                if (!confirmPasswordField.getText().equals(newValue)) {
                    confirmPasswordField.setStyle(ERROR_FIELD_STYLE);
                } else {
                    confirmPasswordField.setStyle(FIELD_STYLE);
                }
            }
        });
    }

    /**
     * Méthode pour fermer proprement la vue
     */
    public void close() {
        // Nettoyage des ressources si nécessaire
        resetForm();
        System.out.println("🔒 RegisterView fermée");
    }
}