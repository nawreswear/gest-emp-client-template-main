package tn.iset.m2glnt.client.viewer.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.BlurType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import tn.iset.m2glnt.client.viewer.CalendarApp;

public class ProfileView {
    private Scene scene;
    private boolean isDarkMode = false;

    public ProfileView() {
        System.out.println("🏗️ Construction de ProfileView Premium...");
        initializeView();
    }

    private void initializeView() {
        try {
            // Container principal avec fond animé moderne
            StackPane root = new StackPane();
            applyAnimatedBackground(root);

            // ScrollPane pour le contenu avec style moderne
            ScrollPane scrollPane = createModernScrollPane();

            // Container principal
            VBox mainContainer = new VBox(30);
            mainContainer.setAlignment(Pos.TOP_CENTER);
            mainContainer.setPadding(new Insets(50, 20, 50, 20));
            mainContainer.setStyle("-fx-background-color: transparent;");

            // Header avec avatar animé
            VBox header = createPremiumHeader();

            // Carte de profil principale avec effets
            VBox profileCard = createPremiumProfileCard();

            // Statistiques rapides
            HBox statsContainer = createStatsContainer();

            mainContainer.getChildren().addAll(header, statsContainer, profileCard);
            scrollPane.setContent(mainContainer);
            root.getChildren().add(scrollPane);

            this.scene = new Scene(root, 1000, 800);
            applySceneEffects();

            // Animation d'entrée spectaculaire
            applyEntranceAnimations(mainContainer);

            System.out.println("✅ ProfileView Premium initialisé avec succès");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'initialisation de ProfileView: " + e.getMessage());
            e.printStackTrace();
            showError("Erreur lors du chargement du profil: " + e.getMessage());
        }
    }

    private void applyAnimatedBackground(StackPane root) {
        // Fond gradient animé
        root.setStyle("-fx-background-color: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);");

        // Effet de particules subtiles
        Pane particles = createParticlesEffect();
        root.getChildren().add(particles);
    }

    private Pane createParticlesEffect() {
        Pane particles = new Pane();
        particles.setMouseTransparent(true);

        // Créer quelques cercles pour l'effet de particules
        for (int i = 0; i < 8; i++) {
            javafx.scene.shape.Circle circle = new javafx.scene.shape.Circle(
                    Math.random() * 1000,
                    Math.random() * 800,
                    2 + Math.random() * 3
            );
            circle.setFill(Color.WHITE);
            circle.setOpacity(0.1 + Math.random() * 0.2);

            // Animation flottante
            TranslateTransition floatAnim = new TranslateTransition(
                    Duration.seconds(3 + Math.random() * 4), circle
            );
            floatAnim.setByY(-20 - Math.random() * 30);
            floatAnim.setByX(Math.random() * 40 - 20);
            floatAnim.setCycleCount(javafx.animation.Animation.INDEFINITE);
            floatAnim.setAutoReverse(true);
            floatAnim.play();

            particles.getChildren().add(circle);
        }

        return particles;
    }

    private ScrollPane createModernScrollPane() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; " +
                "-fx-border-color: transparent; -fx-padding: 0;");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Style de la barre de défilement
        scrollPane.lookup(".scroll-bar:vertical").setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-background-radius: 0; " +
                        "-fx-pref-width: 8px;"
        );
        scrollPane.lookup(".scroll-bar:vertical .thumb").setStyle(
                "-fx-background-color: rgba(255,255,255,0.3); " +
                        "-fx-background-radius: 4px;"
        );

        return scrollPane;
    }

    private VBox createPremiumHeader() {
        VBox header = new VBox(20);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(0, 0, 30, 0));

        // Avatar circulaire avec effets
        StackPane avatarContainer = createAnimatedAvatar();

        // Nom de l'utilisateur avec effet de brillance
        Label nameLabel = createNameLabel();

        // Badge de type d'utilisateur
        HBox badgeContainer = createUserBadge();

        header.getChildren().addAll(avatarContainer, nameLabel, badgeContainer);

        // Animation du header
        applyHeaderAnimations(header);

        return header;
    }

    private StackPane createAnimatedAvatar() {
        StackPane avatarContainer = new StackPane();
        avatarContainer.setStyle("-fx-background-color: linear-gradient(135deg, #ffffff, #f8fafc); " +
                "-fx-background-radius: 100; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 25, 0, 0, 8); " +
                "-fx-border-width: 4; " +
                "-fx-border-color: linear-gradient(135deg, #667eea, #764ba2); " +
                "-fx-border-radius: 100;");
        avatarContainer.setPrefSize(180, 180);
        avatarContainer.setMaxSize(180, 180);

        // Avatar principal
        Label avatar = new Label("👤");
        avatar.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 80));
        avatar.setStyle("-fx-text-fill: linear-gradient(135deg, #667eea, #764ba2);");

        // Effet de brillance
        javafx.scene.shape.Rectangle shine = new javafx.scene.shape.Rectangle(180, 180);
        shine.setFill(Color.TRANSPARENT);
        shine.setStyle("-fx-arc-width: 100; -fx-arc-height: 100;");

        avatarContainer.getChildren().addAll(shine, avatar);

        // CORRECTION: Animation de rotation de la brillance
        RotateTransition rotate = new RotateTransition(Duration.seconds(6), avatarContainer);
        rotate.setFromAngle(0);
        rotate.setToAngle(360);
        rotate.setCycleCount(javafx.animation.Animation.INDEFINITE);
        rotate.setInterpolator(javafx.animation.Interpolator.LINEAR);
        rotate.play();

        // CORRECTION: Animation de pulsation
        ScaleTransition pulse = new ScaleTransition(Duration.seconds(2), avatarContainer);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.05);
        pulse.setToY(1.05);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(javafx.animation.Animation.INDEFINITE);
        pulse.play();

        return avatarContainer;
    }

    private Label createNameLabel() {
        String fullName = CalendarApp.getCurrentUserFullName();
        Label nameLabel = new Label(fullName.isEmpty() ? "Utilisateur ISET" : fullName);
        nameLabel.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 36));
        nameLabel.setTextFill(Color.WHITE);
        nameLabel.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 8, 0, 2, 2);");

        // Animation de brillance du texte
        FadeTransition textGlow = new FadeTransition(Duration.seconds(2), nameLabel);
        textGlow.setFromValue(0.8);
        textGlow.setToValue(1.0);
        textGlow.setAutoReverse(true);
        textGlow.setCycleCount(javafx.animation.Animation.INDEFINITE);
        textGlow.play();

        return nameLabel;
    }

    private HBox createUserBadge() {
        HBox badgeContainer = new HBox(10);
        badgeContainer.setAlignment(Pos.CENTER);

        String type = CalendarApp.getCurrentUserType();
        String typeDisplay = type.isEmpty() ? "Utilisateur" :
                type.equalsIgnoreCase("ETUDIANT") ? "👨‍🎓 Étudiant" :
                        type.equalsIgnoreCase("ENSEIGNANT") ? "👨‍🏫 Enseignant" :
                                type.equalsIgnoreCase("ADMIN") ? "👑 Administrateur" : type;

        String badgeColor = type.equalsIgnoreCase("ADMIN") ? "#FF6B6B" :
                type.equalsIgnoreCase("ENSEIGNANT") ? "#4ECDC4" :
                        type.equalsIgnoreCase("ETUDIANT") ? "#45B7D1" : "#95E1D3";

        Label typeLabel = new Label(typeDisplay);
        typeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        typeLabel.setTextFill(Color.WHITE);
        typeLabel.setStyle("-fx-background-color: " + badgeColor + "; " +
                "-fx-background-radius: 20; -fx-padding: 10 25; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);");

        // Badge de statut en ligne
        Label statusBadge = new Label("● En ligne");
        statusBadge.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 12));
        statusBadge.setTextFill(Color.web("#90EE90"));
        statusBadge.setStyle("-fx-background-color: rgba(144, 238, 144, 0.2); " +
                "-fx-background-radius: 15; -fx-padding: 5 15;");

        badgeContainer.getChildren().addAll(typeLabel, statusBadge);
        return badgeContainer;
    }

    private HBox createStatsContainer() {
        HBox statsContainer = new HBox(20);
        statsContainer.setAlignment(Pos.CENTER);
        statsContainer.setPadding(new Insets(0, 0, 20, 0));

        // Statistique 1: Année académique
        VBox stat1 = createStatCard("📚", "Année", "2024-2025", "#667eea");

        // Statistique 2: Statut
        VBox stat2 = createStatCard("✅", "Statut", "Actif", "#4CAF50");

        // Statistique 3: Membre depuis
        VBox stat3 = createStatCard("🏫", "ISET", "Membre", "#FF9800");

        statsContainer.getChildren().addAll(stat1, stat2, stat3);

        // Animation des statistiques
        applyStatsAnimations(statsContainer);

        return statsContainer;
    }

    private VBox createStatCard(String icon, String title, String value, String color) {
        VBox statCard = new VBox(8);
        statCard.setAlignment(Pos.CENTER);
        statCard.setPadding(new Insets(20, 15, 20, 15));
        statCard.setStyle("-fx-background-color: rgba(255,255,255,0.1); " +
                "-fx-background-radius: 20; " +
                "-fx-border-color: rgba(255,255,255,0.2); " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 20;");
        statCard.setMinWidth(120);

        // Icône
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 24));
        iconLabel.setTextFill(Color.WHITE);

        // Titre
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        titleLabel.setTextFill(Color.web("#E0E0E0"));

        // Valeur
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 16));
        valueLabel.setTextFill(Color.WHITE);

        statCard.getChildren().addAll(iconLabel, titleLabel, valueLabel);
        return statCard;
    }

    private VBox createPremiumProfileCard() {
        VBox card = new VBox(0);
        card.setMaxWidth(800);
        card.setStyle("-fx-background-color: rgba(255,255,255,0.95); " +
                "-fx-background-radius: 30; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 40, 0, 0, 15); " +
                "-fx-border-color: rgba(255,255,255,0.3); " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 30;");

        // Section: Informations personnelles avec icône animée
        VBox personalInfo = createPremiumInfoSection("🎯 Informations Personnelles",
                createPersonalInfoGrid());

        // Section: Contact avec design moderne
        VBox contactInfo = createPremiumInfoSection("📞 Contact & Coordination",
                createContactInfoGrid());

        // Section: Système avec indicateurs
        VBox systemInfo = createPremiumInfoSection("⚙️ Environnement Système",
                createSystemInfoGrid());

        // Boutons d'action premium
        HBox buttonContainer = createPremiumButtonContainer();

        card.getChildren().addAll(
                personalInfo,
                createStyledSeparator(),
                contactInfo,
                createStyledSeparator(),
                systemInfo,
                createStyledSeparator(),
                buttonContainer
        );

        // Animation de la carte
        applyCardAnimations(card);

        return card;
    }

    private VBox createPremiumInfoSection(String title, GridPane content) {
        VBox section = new VBox(25);
        section.setPadding(new Insets(35, 45, 35, 45));

        // Titre de section avec icône
        HBox titleContainer = new HBox(15);
        titleContainer.setAlignment(Pos.CENTER_LEFT);

        Label iconLabel = new Label(title.split(" ")[0]);
        iconLabel.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 20));

        Label titleLabel = new Label(title.substring(title.indexOf(" ") + 1));
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titleLabel.setTextFill(Color.web("#2D3748"));
        titleLabel.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 1, 1);");

        titleContainer.getChildren().addAll(iconLabel, titleLabel);

        section.getChildren().addAll(titleContainer, content);
        return section;
    }

    private GridPane createPersonalInfoGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(25);
        grid.setVgap(20);

        String nom = CalendarApp.getCurrentUserNom();
        String prenom = CalendarApp.getCurrentUserPrenom();
        String cin = CalendarApp.getCurrentUserCin();

        addPremiumInfoRow(grid, 0, "👤", "Nom Complet",
                nom.isEmpty() || prenom.isEmpty() ? "Non défini" : prenom + " " + nom);
        addPremiumInfoRow(grid, 1, "🆔", "CIN / Identifiant", cin.isEmpty() ? "Non défini" : cin);
        addPremiumInfoRow(grid, 2, "🎂", "Profil", getProfileType());

        return grid;
    }

    private GridPane createContactInfoGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(25);
        grid.setVgap(20);

        String email = CalendarApp.getCurrentUserEmail();
        String tel = CalendarApp.getCurrentUserTel();

        addPremiumInfoRow(grid, 0, "📧", "Email Institutionnel",
                email.isEmpty() ? "Non défini" : email);
        addPremiumInfoRow(grid, 1, "📱", "Téléphone",
                tel.isEmpty() ? "Non défini" : formatPhoneNumber(tel));
        addPremiumInfoRow(grid, 2, "🏫", "Établissement", "ISET - Institut Supérieur des Études Technologiques");

        return grid;
    }

    private GridPane createSystemInfoGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(25);
        grid.setVgap(20);

        String statut = CalendarApp.isUserLoggedIn() ? "✅ Connecté" : "❌ Déconnecté";
        String derniereConnexion = java.time.LocalDate.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"));

        addPremiumInfoRow(grid, 0, "🔐", "Statut de Session", statut);
        addPremiumInfoRow(grid, 1, "📅", "Dernière Activité", derniereConnexion);
        addPremiumInfoRow(grid, 2, "🌐", "Environnement", "JavaFX " + System.getProperty("javafx.version"));

        return grid;
    }

    private void addPremiumInfoRow(GridPane grid, int row, String icon, String label, String value) {
        HBox rowContainer = new HBox(20);
        rowContainer.setAlignment(Pos.CENTER_LEFT);
        rowContainer.setPadding(new Insets(8, 0, 8, 0));

        // Icône dans un cercle
        StackPane iconContainer = new StackPane();
        iconContainer.setStyle("-fx-background-color: linear-gradient(135deg, #667eea, #764ba2); " +
                "-fx-background-radius: 12; -fx-padding: 10; -fx-min-width: 45; -fx-min-height: 45;");
        iconContainer.setAlignment(Pos.CENTER);

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 16));
        iconLabel.setTextFill(Color.WHITE);

        iconContainer.getChildren().add(iconLabel);

        // Label et valeur
        VBox textContainer = new VBox(4);
        textContainer.setAlignment(Pos.CENTER_LEFT);

        Label keyLabel = new Label(label);
        keyLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        keyLabel.setTextFill(Color.web("#718096"));

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 15));
        valueLabel.setTextFill(Color.web("#2D3748"));
        valueLabel.setStyle("-fx-background-color: #F7FAFC; -fx-background-radius: 10; " +
                "-fx-padding: 12 18; -fx-border-color: #E2E8F0; -fx-border-radius: 10; " +
                "-fx-border-width: 1;");
        valueLabel.setMaxWidth(350);
        valueLabel.setWrapText(true);

        textContainer.getChildren().addAll(keyLabel, valueLabel);
        rowContainer.getChildren().addAll(iconContainer, textContainer);

        grid.add(rowContainer, 0, row);
    }

    private Separator createStyledSeparator() {
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: linear-gradient(to right, transparent, #E2E8F0, transparent); " +
                "-fx-padding: 0; -fx-background-radius: 1;");
        separator.setPadding(new Insets(0, 45, 0, 45));
        return separator;
    }

    private HBox createPremiumButtonContainer() {
        HBox buttonContainer = new HBox(20);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(35, 45, 35, 45));

        Button backButton = createPremiumButton("📅 Retour Calendrier", "#10B981", "#059669");
        backButton.setOnAction(e -> navigateToCalendar());

        Button editButton = createPremiumButton("✏️ Modifier Profil", "#3B82F6", "#2563EB");
        editButton.setOnAction(e -> editProfile());

        Button settingsButton = createPremiumButton("⚙️ Paramètres", "#8B5CF6", "#7C3AED");
        settingsButton.setOnAction(e -> showSettings());

        Button logoutButton = createPremiumButton("🚪 Déconnexion", "#EF4444", "#DC2626");
        logoutButton.setOnAction(e -> logout());

        buttonContainer.getChildren().addAll(backButton, editButton, settingsButton, logoutButton);

        // Animation des boutons
        applyButtonAnimations(buttonContainer);

        return buttonContainer;
    }

    private Button createPremiumButton(String text, String color, String hoverColor) {
        Button button = new Button(text);
        button.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        button.setPrefHeight(50);
        button.setMinWidth(160);

        String baseStyle = "-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 15; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3); " +
                "-fx-border-color: derive(" + color + ", -20%); " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 15;";

        String hoverStyle = "-fx-background-color: " + hoverColor + "; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 15; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 5); " +
                "-fx-scale-x: 1.05; " +
                "-fx-scale-y: 1.05;";

        String pressedStyle = "-fx-background-color: derive(" + hoverColor + ", -20%); " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2); " +
                "-fx-translate-y: 1;";

        button.setStyle(baseStyle);

        // Animations avancées
        button.setOnMouseEntered(e -> {
            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(150), button);
            scaleIn.setToX(1.05);
            scaleIn.setToY(1.05);
            scaleIn.play();
            button.setStyle(baseStyle + hoverStyle);
        });

        button.setOnMouseExited(e -> {
            ScaleTransition scaleOut = new ScaleTransition(Duration.millis(150), button);
            scaleOut.setToX(1.0);
            scaleOut.setToY(1.0);
            scaleOut.play();
            button.setStyle(baseStyle);
        });

        button.setOnMousePressed(e -> {
            button.setStyle(baseStyle + pressedStyle);
        });

        button.setOnMouseReleased(e -> {
            button.setStyle(baseStyle + hoverStyle);
            button.setTranslateY(0);
        });

        return button;
    }

    // === ANIMATIONS AVANCÉES ===

    private void applyEntranceAnimations(VBox mainContainer) {
        // Animation d'entrée en cascade
        for (int i = 0; i < mainContainer.getChildren().size(); i++) {
            Node node = mainContainer.getChildren().get(i);

            node.setOpacity(0);
            node.setTranslateY(50);

            FadeTransition fade = new FadeTransition(Duration.millis(600), node);
            fade.setToValue(1);

            TranslateTransition translate = new TranslateTransition(Duration.millis(600), node);
            translate.setToY(0);

            ParallelTransition entrance = new ParallelTransition(fade, translate);
            entrance.setDelay(Duration.millis(i * 150));
            entrance.play();
        }
    }

    private void applyHeaderAnimations(VBox header) {
        for (Node node : header.getChildren()) {
            ScaleTransition scale = new ScaleTransition(Duration.millis(800), node);
            scale.setFromX(0.8);
            scale.setFromY(0.8);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.setInterpolator(javafx.animation.Interpolator.EASE_OUT);

            FadeTransition fade = new FadeTransition(Duration.millis(800), node);
            fade.setFromValue(0);
            fade.setToValue(1);

            new ParallelTransition(scale, fade).play();
        }
    }

    private void applyStatsAnimations(HBox statsContainer) {
        for (Node node : statsContainer.getChildren()) {
            RotateTransition rotate = new RotateTransition(Duration.millis(400), node);
            rotate.setFromAngle(-5);
            rotate.setToAngle(0);

            ScaleTransition scale = new ScaleTransition(Duration.millis(400), node);
            scale.setFromX(0.9);
            scale.setFromY(0.9);
            scale.setToX(1.0);
            scale.setToY(1.0);

            new ParallelTransition(rotate, scale).play();
        }
    }

    private void applyCardAnimations(VBox card) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(500), card);
        scale.setFromX(0.95);
        scale.setFromY(0.95);
        scale.setToX(1.0);
        scale.setToY(1.0);

        FadeTransition fade = new FadeTransition(Duration.millis(500), card);
        fade.setFromValue(0);
        fade.setToValue(1);

        new ParallelTransition(scale, fade).play();
    }

    private void applyButtonAnimations(HBox buttonContainer) {
        for (Node node : buttonContainer.getChildren()) {
            node.setScaleX(0.9);
            node.setScaleY(0.9);

            ScaleTransition scale = new ScaleTransition(Duration.millis(300), node);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.setDelay(Duration.millis(800));
            scale.play();
        }
    }

    private void applySceneEffects() {
        // Effet de brillance globale
        DropShadow globalShadow = new DropShadow();
        globalShadow.setColor(Color.rgb(0, 0, 0, 0.3));
        globalShadow.setRadius(20);
        globalShadow.setSpread(0.1);
    }

    // === MÉTHODES UTILITAIRES ===

    private String getProfileType() {
        String type = CalendarApp.getCurrentUserType();
        return type.equalsIgnoreCase("ADMIN") ? "Administrateur Système" :
                type.equalsIgnoreCase("ENSEIGNANT") ? "Enseignant-Chercheur" :
                        type.equalsIgnoreCase("ETUDIANT") ? "Étudiant" : "Utilisateur";
    }

    private String formatPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) return "Non défini";
        // Format basique pour les numéros tunisiens
        if (phone.startsWith("+216")) {
            return phone.replaceFirst("(\\+216)(\\d{2})(\\d{3})(\\d{3})", "$1 $2 $3 $4");
        }
        return phone;
    }

    // === NAVIGATION ET ACTIONS ===

    private void navigateToCalendar() {
        System.out.println("📅 Retour au calendrier...");
        // Animation de sortie
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), scene.getRoot());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> CalendarApp.showCalendarView());
        fadeOut.play();
    }

    private void editProfile() {
        System.out.println("✏️ Modification du profil...");
        showPremiumInfo("Fonctionnalité Premium",
                "La modification avancée du profil sera disponible dans la prochaine mise à jour.");
    }

    private void showSettings() {
        System.out.println("⚙️ Ouverture des paramètres...");
        showPremiumInfo("Paramètres Avancés",
                "Les paramètres système avancés seront accessibles prochainement.");
    }

    private void logout() {
        System.out.println("🚪 Déconnexion demandée...");

        Alert confirmation = createPremiumAlert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de Déconnexion");
        confirmation.setHeaderText("Déconnexion du Système");
        confirmation.setContentText("Êtes-vous sûr de vouloir vous déconnecter de votre session ?");

        // Personnalisation des boutons
        ButtonType yesButton = new ButtonType("Oui, Se Déconnecter", ButtonBar.ButtonData.OK_DONE);
        ButtonType noButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmation.getButtonTypes().setAll(yesButton, noButton);

        confirmation.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                // Animation de déconnexion
                FadeTransition fadeOut = new FadeTransition(Duration.millis(500), scene.getRoot());
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(e -> CalendarApp.logout());
                fadeOut.play();
            }
        });
    }

    // === MÉTHODES D'AFFICHAGE DES MESSAGES PREMIUM ===

    private void showError(String message) {
        Alert alert = createPremiumAlert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur Système");
        alert.setHeaderText("Une erreur est survenue");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showPremiumInfo(String title, String message) {
        Alert alert = createPremiumAlert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Alert createPremiumAlert(Alert.AlertType type) {
        Alert alert = new Alert(type);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white; " +
                "-fx-font-family: 'Segoe UI'; " +
                "-fx-background-radius: 15; " +
                "-fx-border-color: #E2E8F0; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 10);");

        return alert;
    }

    public Scene getScene() {
        return this.scene;
    }
}
/*package tn.iset.m2glnt.client.viewer.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import tn.iset.m2glnt.client.viewer.CalendarApp;

public class ProfileView {
    private Scene scene;

    public ProfileView() {
        System.out.println("🏗️ Construction de ProfileView...");
        initializeView();
    }

    private void initializeView() {
        try {
            // Container principal avec fond gradiant
            StackPane root = new StackPane();
            root.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea, #764ba2);");

            // Container du profil
            VBox mainContainer = new VBox(20);
            mainContainer.setAlignment(Pos.CENTER);
            mainContainer.setPadding(new Insets(40));
            mainContainer.setMaxWidth(600);
            mainContainer.setMaxHeight(700);

            // Carte de profil
            VBox profileCard = createProfileCard();

            mainContainer.getChildren().addAll(profileCard);
            root.getChildren().add(mainContainer);

            this.scene = new Scene(root, 800, 600);
            System.out.println("✅ ProfileView initialisé avec succès");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'initialisation de ProfileView: " + e.getMessage());
            e.printStackTrace();
            showError("Erreur lors du chargement du profil: " + e.getMessage());
        }
    }

    private VBox createProfileCard() {
        VBox card = new VBox(25);
        card.setPadding(new Insets(40, 50, 40, 50));
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 25, 0, 0, 10);");
        card.setMaxWidth(500);

        // Icône utilisateur
        Label userIcon = new Label("👤");
        userIcon.setFont(Font.font("System", FontWeight.BOLD, 48));
        userIcon.setPadding(new Insets(0, 0, 10, 0));

        // Titre
        Label titleLabel = new Label("Profil Utilisateur");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#2D2D2D"));

        // Informations utilisateur
        VBox infoContainer = createUserInfoContainer();

        // Séparateur
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 10, 0));

        // Boutons d'action
        HBox buttonContainer = createButtonContainer();

        card.getChildren().addAll(userIcon, titleLabel, infoContainer, separator, buttonContainer);

        return card;
    }

    private VBox createUserInfoContainer() {
        VBox infoContainer = new VBox(12);
        infoContainer.setAlignment(Pos.CENTER_LEFT);
        infoContainer.setPadding(new Insets(20, 0, 20, 0));
        infoContainer.setMaxWidth(450);

        // 🔥 DEBUG : Afficher les données actuelles
        CalendarApp.debugUserData();

        // Récupérer toutes les informations de l'utilisateur connecté
        String nom = CalendarApp.getCurrentUserNom();
        String prenom = CalendarApp.getCurrentUserPrenom();
        String email = CalendarApp.getCurrentUserEmail();
        String type = CalendarApp.getCurrentUserType();
        String tel = CalendarApp.getCurrentUserTel();
        String cin = CalendarApp.getCurrentUserCin();
        String fullName = CalendarApp.getCurrentUserFullName();

        System.out.println("🔍 Données dans ProfileView:");
        System.out.println("   - Nom: '" + nom + "'");
        System.out.println("   - Prénom: '" + prenom + "'");
        System.out.println("   - Email: '" + email + "'");
        System.out.println("   - Type: '" + type + "'");
        System.out.println("   - Téléphone: '" + tel + "'");
        System.out.println("   - CIN: '" + cin + "'");
        System.out.println("   - Nom complet: '" + fullName + "'");

        // Vérifier si l'utilisateur est connecté
        if (!CalendarApp.isUserLoggedIn()) {
            System.err.println("❌ Aucun utilisateur connecté!");
            showError("Aucun utilisateur connecté. Veuillez vous connecter.");

            // Afficher un message dans l'interface
            Label errorLabel = new Label("❌ Aucun utilisateur connecté");
            errorLabel.setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold; -fx-font-size: 16;");
            infoContainer.getChildren().add(errorLabel);
            return infoContainer;
        }

        // Vérifier si les données de base sont vides
        boolean donneesManquantes = (nom == null || nom.trim().isEmpty()) &&
                (prenom == null || prenom.trim().isEmpty());

        if (donneesManquantes) {
            System.err.println("⚠️  Données utilisateur manquantes dans ProfileView!");
            showWarning("Certaines informations de profil sont manquantes.");
        }

        // Nom complet
        Label nameLabel = createInfoLabel("Nom complet:",
                fullName.isEmpty() ? "Non défini" : fullName);

        // Email
        Label emailLabel = createInfoLabel("Email:",
                email.isEmpty() ? "Non défini" : email);

        // Type d'utilisateur
        String typeDisplay = type.isEmpty() ? "Non défini" :
                type.equals("ETUDIANT") ? "Étudiant" :
                        type.equals("ENSEIGNANT") ? "Enseignant" :
                                type.equals("ADMIN") ? "Administrateur" : type;

        Label typeLabel = createInfoLabel("Type:", typeDisplay);

        // Téléphone
        Label telLabel = createInfoLabel("Téléphone:",
                tel.isEmpty() ? "Non défini" : tel);

        // CIN
        Label cinLabel = createInfoLabel("CIN:",
                cin.isEmpty() ? "Non défini" : cin);

        // Statut
        String statut = CalendarApp.isUserLoggedIn() ? "Connecté" : "Déconnecté";
        Label statutLabel = createInfoLabel("Statut:", statut);

        // Dernière connexion
        String derniereConnexion = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Label connexionLabel = createInfoLabel("Dernière connexion:", derniereConnexion);

        infoContainer.getChildren().addAll(
                nameLabel, emailLabel, typeLabel, telLabel, cinLabel, statutLabel, connexionLabel
        );

        return infoContainer;
    }

    private Label createInfoLabel(String label, String value) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(5, 0, 5, 0));

        Label keyLabel = new Label(label);
        keyLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        keyLabel.setTextFill(Color.web("#666666"));
        keyLabel.setMinWidth(140);

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
        valueLabel.setTextFill(Color.web("#2D2D2D"));
        valueLabel.setStyle("-fx-background-color: #F8F9FA; -fx-background-radius: 5; -fx-padding: 8 12; -fx-border-color: #E0E0E0; -fx-border-radius: 5;");
        valueLabel.setMaxWidth(250);
        valueLabel.setWrapText(true);

        row.getChildren().addAll(keyLabel, valueLabel);

        // Créer un conteneur pour la ligne complète
        Label container = new Label();
        container.setGraphic(row);
        container.setContentDisplay(javafx.scene.control.ContentDisplay.GRAPHIC_ONLY);

        return container;
    }

    private HBox createButtonContainer() {
        HBox buttonContainer = new HBox(20);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(20, 0, 0, 0));

        // Bouton Retour au Calendrier
        Button backButton = createActionButton("📅 Retour au Calendrier", "#4CAF50");
        backButton.setOnAction(e -> navigateToCalendar());

        // Bouton Modifier le Profil
        Button editButton = createActionButton("✏️ Modifier le Profil", "#2196F3");
        editButton.setOnAction(e -> editProfile());

        // Bouton Déconnexion
        Button logoutButton = createActionButton("🚪 Déconnexion", "#F44336");
        logoutButton.setOnAction(e -> logout());

        buttonContainer.getChildren().addAll(backButton, editButton, logoutButton);

        return buttonContainer;
    }

    private Button createActionButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; -fx-background-radius: 8; -fx-padding: 12 20; -fx-cursor: hand;");
        button.setMinWidth(160);

        // Effet hover
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: " + darkenColor(color) + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; -fx-background-radius: 8; -fx-padding: 12 20; -fx-cursor: hand;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; -fx-background-radius: 8; -fx-padding: 12 20; -fx-cursor: hand;"));

        return button;
    }

    private String darkenColor(String color) {
        switch (color) {
            case "#4CAF50": return "#45a049"; // Vert
            case "#2196F3": return "#1976D2"; // Bleu
            case "#F44336": return "#D32F2F"; // Rouge
            default: return color;
        }
    }

    private void navigateToCalendar() {
        System.out.println("📅 Retour au calendrier...");
        CalendarApp.showCalendarView();
    }

    private void editProfile() {
        System.out.println("✏️ Modification du profil...");
        showInfo("Fonctionnalité de modification du profil à implémenter");
        // TODO: Implémenter EditProfileView
    }

    private void logout() {
        System.out.println("🚪 Déconnexion demandée...");

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de déconnexion");
        confirmation.setHeaderText("Déconnexion");
        confirmation.setContentText("Êtes-vous sûr de vouloir vous déconnecter ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                CalendarApp.logout();
            }
        });
    }

    // === MÉTHODES D'AFFICHAGE DES MESSAGES ===

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Avertissement");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Scene constructScene() {
        return this.scene;
    }
}*/