package tn.iset.m2glnt.client.util;

import javafx.scene.Scene;
import javafx.stage.Stage;
import tn.iset.m2glnt.client.model.Calendar;
import tn.iset.m2glnt.client.model.SimpleCalendar;
import tn.iset.m2glnt.client.service.SalleRestService;
import tn.iset.m2glnt.client.service.dao.SimpleCalendarServiceDAO;
import tn.iset.m2glnt.client.viewer.controller.CalendarViewController;
import tn.iset.m2glnt.client.viewer.controller.CalendarWeekViewConfiguration;

// AJOUTER CES IMPORTS
import tn.iset.m2glnt.client.service.dao.EnseignantService;
import tn.iset.m2glnt.client.service.dao.EtudiantService;
import tn.iset.m2glnt.client.service.SalleRestService;
import java.time.Duration;
import java.time.LocalTime;
import javafx.scene.paint.Color;
import tn.iset.m2glnt.client.viewer.controller.PeriodCalendarViewController;

public class SceneManager {
    private static Stage primaryStage;
    private static Scene currentScene;
    private static CalendarViewController calendarViewController;

    // AJOUTER LES RÉFÉRENCES AUX SERVICES
    private static EnseignantService enseignantService;
    private static SalleRestService salleService;
    private static EtudiantService etudiantService;

    /**
     * Initialise le SceneManager avec la stage principale
     */
    public static void initialize(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Configure les services nécessaires pour les contrôleurs
     */
    public static void setServices(EnseignantService enseignantService,
                                   SalleRestService salleService,
                                   EtudiantService etudiantService) {
        SceneManager.enseignantService = enseignantService;
        SceneManager.salleService = salleService;
        SceneManager.etudiantService = etudiantService;
    }

    /**
     * Bascule vers une scène spécifique
     */
    public static void switchToScene(Scene scene) {
        if (primaryStage == null) {
            throw new IllegalStateException("SceneManager non initialisé. Appelez initialize() d'abord.");
        }

        currentScene = scene;
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Bascule vers la vue calendrier principale
     */
    public static void switchToCalendarView() {
        if (primaryStage == null) {
            throw new IllegalStateException("SceneManager non initialisé.");
        }

        // Vérifier que les services sont configurés
        if (enseignantService == null || salleService == null || etudiantService == null) {
            System.err.println("⚠️ Les services ne sont pas configurés. Appelez SceneManager.setServices() d'abord.");
            // Vous pouvez soit throw une exception, soit utiliser des valeurs null
        }

        // Créer ou réutiliser le contrôleur de calendrier
        if (calendarViewController == null) {
            Calendar calendar = new SimpleCalendar(new SimpleCalendarServiceDAO());
            calendarViewController = createCalendarViewController(calendar);
        }

        Scene calendarScene = calendarViewController.getScene();
        switchToScene(calendarScene);
    }

    /**
     * Crée un contrôleur de vue calendrier avec configuration
     */
    private static CalendarViewController createCalendarViewController(Calendar calendar) {
        return new CalendarViewController() {
            @Override
            public void handleNext() {
                // Implémentation par défaut pour navigation suivante
                System.out.println("➡️ Navigation vers la période suivante");
            }

            @Override
            public void handlePrevious() {
                // Implémentation par défaut pour navigation précédente
                System.out.println("⬅️ Navigation vers la période précédente");
            }

            @Override
            public void handleSlotEdition(int idSlot) {
                // Implémentation par défaut pour édition de créneau
                System.out.println("✏️ Édition du créneau ID: " + idSlot);
            }

            @Override
            public void handleSlotCreation() {
                // Implémentation par défaut pour création de créneau
                System.out.println("➕ Création d'un nouveau créneau");
            }

            @Override
            public Scene getScene() {
                // CORRECTION : Passer les services au constructeur
                CalendarViewController realController = new PeriodCalendarViewController(
                        calendar,
                        new CalendarWeekViewConfiguration(
                                LocalTime.of(8,0),
                                LocalTime.of(19,0),
                                Duration.ofMinutes(60),
                                Duration.ofHours(4),
                                Duration.ofHours(1),
                                Color.AQUA
                        ),
                        enseignantService,  // AJOUTER
                        salleService,       // AJOUTER
                        etudiantService     // AJOUTER
                );
                return realController.getScene();
            }
        };
    }

    /**
     * Bascule vers différentes vues par nom
     */
    public static void switchTo(String viewName) {
        if (viewName == null || viewName.trim().isEmpty()) {
            System.err.println("❌ Nom de vue non spécifié");
            return;
        }

        switch (viewName.toLowerCase()) {
            case "calendar":
            case "main":
            case "home":
                switchToCalendarView();
                break;
            case "register":
            case "signup":
            case "inscription":
                switchToRegisterView();
                break;
            case "login":
            case "signin":
            case "connexion":
                switchToLoginView();
                break;
            case "settings":
            case "parametres":
                switchToSettingsView();
                break;
            case "profile":
            case "profil":
                switchToProfileView();
                break;
            default:
                System.err.println("❌ Vue inconnue: " + viewName);
                showUnknownViewError(viewName);
                break;
        }
    }

    /**
     * Vue d'inscription
     */
    private static void switchToRegisterView() {
        System.out.println("🔧 Navigation vers la vue d'inscription");

        // TODO: Implémenter la création de la scène d'inscription
        // Pour l'instant, on affiche un message et on reste sur la vue actuelle
        showNotImplementedMessage("Vue d'inscription");
    }

    /**
     * Vue de connexion
     */
    private static void switchToLoginView() {
        System.out.println("🔧 Navigation vers la vue de connexion");

        // TODO: Implémenter la création de la scène de connexion
        // Pour l'instant, on affiche un message et on reste sur la vue actuelle
        showNotImplementedMessage("Vue de connexion");
    }

    /**
     * Vue des paramètres
     */
    private static void switchToSettingsView() {
        System.out.println("🔧 Navigation vers la vue des paramètres");

        // TODO: Implémenter la création de la scène des paramètres
        showNotImplementedMessage("Vue des paramètres");
    }

    /**
     * Vue du profil utilisateur
     */
    private static void switchToProfileView() {
        System.out.println("🔧 Navigation vers la vue du profil");

        // TODO: Implémenter la création de la scène du profil
        showNotImplementedMessage("Vue du profil");
    }

    /**
     * Affiche un message pour les vues non implémentées
     */
    private static void showNotImplementedMessage(String viewName) {
        // Dans une application réelle, vous pourriez afficher une alerte
        // ou une page temporaire indiquant que la fonctionnalité est en cours de développement

        System.out.println("🚧 " + viewName + " - Fonctionnalité en cours de développement");

        // Optionnel: Créer une scène temporaire avec un message
        /*
        Label messageLabel = new Label(viewName + " - Disponible prochainement");
        messageLabel.setStyle("-fx-font-size: 16px; -fx-padding: 20px;");
        StackPane tempRoot = new StackPane(messageLabel);
        Scene tempScene = new Scene(tempRoot, 400, 300);
        switchToScene(tempScene);
        */
    }

    /**
     * Gère les vues inconnues
     */
    private static void showUnknownViewError(String viewName) {
        // Dans une application réelle, vous pourriez logger l'erreur
        // et/ou afficher une page d'erreur

        System.err.println("🚨 Tentative d'accès à une vue inconnue: " + viewName);

        // Optionnel: Créer une scène d'erreur
        /*
        Label errorLabel = new Label("Vue '" + viewName + "' non trouvée");
        errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red; -fx-padding: 20px;");
        Button backButton = new Button("Retour au calendrier");
        backButton.setOnAction(e -> switchToCalendarView());
        VBox errorRoot = new VBox(10, errorLabel, backButton);
        errorRoot.setAlignment(Pos.CENTER);
        Scene errorScene = new Scene(errorRoot, 400, 300);
        switchToScene(errorScene);
        */
    }

    /**
     * Retourne à la scène précédente (si disponible)
     */
    public static void goBack() {
        // Implémentation basique - dans une vraie application,
        // vous pourriez maintenir une pile de navigation

        System.out.println("↩️ Retour à la vue précédente");
        switchToCalendarView(); // Retour par défaut au calendrier
    }

    /**
     * Rafraîchit la scène actuelle
     */
    public static void refreshCurrentScene() {
        if (currentScene != null && primaryStage != null) {
            primaryStage.sizeToScene(); // Redimensionne si nécessaire
            System.out.println("🔄 Scène rafraîchie");
        }
    }

    /**
     * Ferme l'application
     */
    public static void exitApplication() {
        System.out.println("👋 Fermeture de l'application");
        if (primaryStage != null) {
            primaryStage.close();
        }
        System.exit(0);
    }

    // ==================== MÉTHODES D'ACCÈS ====================

    /**
     * Retourne la scène actuelle
     */
    public static Scene getCurrentScene() {
        return currentScene;
    }

    /**
     * Retourne la stage principale
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Définit un contrôleur de calendrier personnalisé
     */
    public static void setCalendarViewController(CalendarViewController controller) {
        calendarViewController = controller;
    }

    /**
     * Retourne le contrôleur de calendrier actuel
     */
    public static CalendarViewController getCalendarViewController() {
        return calendarViewController;
    }

    // ==================== MÉTHODES DE VÉRIFICATION ====================

    /**
     * Vérifie si le SceneManager est initialisé
     */
    public static boolean isInitialized() {
        return primaryStage != null;
    }

    /**
     * Vérifie si les services sont configurés
     */
    public static boolean areServicesConfigured() {
        return enseignantService != null && salleService != null && etudiantService != null;
    }

    /**
     * Affiche l'état du SceneManager (pour le débogage)
     */
    public static void printStatus() {
        System.out.println("=== ÉTAT SCENEMANAGER ===");
        System.out.println("Stage principal: " + (primaryStage != null ? "✓ Initialisé" : "✗ Non initialisé"));
        System.out.println("Scène actuelle: " + (currentScene != null ? "✓ Définie" : "✗ Non définie"));
        System.out.println("Contrôleur calendrier: " + (calendarViewController != null ? "✓ Créé" : "✗ Non créé"));
        System.out.println("Services configurés: " + (areServicesConfigured() ? "✓ Oui" : "✗ Non"));
        System.out.println("=========================");
    }

    // ==================== MÉTHODES UTILITAIRES POUR LES SERVICES ====================

    /**
     * Méthode utilitaire pour obtenir le service enseignant
     */
    public static EnseignantService getEnseignantService() {
        return enseignantService;
    }

    /**
     * Méthode utilitaire pour obtenir le service salle
     */
    public static SalleRestService getSalleService() {
        return salleService;
    }

    /**
     * Méthode utilitaire pour obtenir le service étudiant
     */
    public static EtudiantService getEtudiantService() {
        return etudiantService;
    }

    /**
     * Vérifie et initialise les services si nécessaire
     */
    public static void ensureServicesInitialized() {
        if (!areServicesConfigured()) {
            System.err.println("⚠️ Attention: Les services ne sont pas configurés.");
            System.err.println("💡 Utilisez SceneManager.setServices() pour les configurer.");

            // Dans une application réelle, vous pourriez tenter de les initialiser automatiquement
            // ou lancer une exception selon votre architecture
        }
    }
}