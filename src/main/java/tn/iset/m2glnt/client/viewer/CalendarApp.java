package tn.iset.m2glnt.client.viewer;

import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import tn.iset.m2glnt.client.model.Calendar;
import tn.iset.m2glnt.client.model.SimpleCalendar;
import tn.iset.m2glnt.client.model.User;
import tn.iset.m2glnt.client.service.SalleRestService;
import tn.iset.m2glnt.client.service.dao.SimpleCalendarServiceDAO;
import tn.iset.m2glnt.client.service.dao.UserService;

// AJOUTER LES IMPORTS DES SERVICES
import tn.iset.m2glnt.client.service.dao.EnseignantService;
import tn.iset.m2glnt.client.service.dao.EtudiantService;

import tn.iset.m2glnt.client.viewer.controller.CalendarViewConfiguration;
import tn.iset.m2glnt.client.viewer.controller.CalendarViewController;
import tn.iset.m2glnt.client.viewer.controller.CalendarWeekViewConfiguration;
import tn.iset.m2glnt.client.viewer.controller.PeriodCalendarViewController;
import tn.iset.m2glnt.client.viewer.view.LoginView;
import tn.iset.m2glnt.client.viewer.view.ProfileView;
import tn.iset.m2glnt.client.viewer.view.RegisterView;
import tn.iset.m2glnt.client.viewer.view.SlotView;

public class CalendarApp extends Application {
        // Variables statiques pour stocker les informations de l'utilisateur connecté
        private static String currentUserEmail;
        private static String currentUserNom;
        private static String currentUserPrenom;
        private static String currentUserType;
        private static String currentUserTel;
        private static String currentUserCin;
        private static String currentUserPhoto;

        // Instance statique pour accéder aux méthodes non-statiques
        private static CalendarApp instance;

        // AJOUTER LES RÉFÉRENCES AUX SERVICES
        private static EnseignantService enseignantService;
        private static SalleRestService salleService;
        private static EtudiantService etudiantService;

        private Stage primaryStage;
        private CalendarViewController calendarController;
        private LoginView loginView;
        private RegisterView registerView;

        @Override
        public void start(Stage primaryStage) {
                instance = this;
                this.primaryStage = primaryStage;

                // Initialiser les vues
                this.loginView = new LoginView();
                this.registerView = new RegisterView();

                showLoginView();

                // Configuration de la fenêtre principale
                primaryStage.setTitle("Application de Gestion - Connexion");
                primaryStage.setMinWidth(800);
                primaryStage.setMinHeight(600);
        }

        // === MÉTHODES POUR CONFIGURER LES SERVICES ===

        /**
         * Configure les services nécessaires pour l'application
         */
        public static void setServices(EnseignantService enseignantService,
                                       SalleRestService salleService,
                                       EtudiantService etudiantService) {
                CalendarApp.enseignantService = enseignantService;
                CalendarApp.salleService = salleService;
                CalendarApp.etudiantService = etudiantService;

                System.out.println("✅ Services configurés dans CalendarApp:");
                System.out.println("   - EnseignantService: " + (enseignantService != null ? "✓" : "✗"));
                System.out.println("   - SalleService: " + (salleService != null ? "✓" : "✗"));
                System.out.println("   - EtudiantService: " + (etudiantService != null ? "✓" : "✗"));
        }

        /**
         * Vérifie si les services sont configurés
         */
        public static boolean areServicesConfigured() {
                return enseignantService != null && salleService != null && etudiantService != null;
        }

        // === MÉTHODES NON-STATIQUES (pour usage interne) ===

        // Méthode pour afficher la vue du calendrier avec toutes les informations utilisateur
        private void showCalendarViewInternal(String userEmail, String nom, String prenom, String type, String tel, String cin) {
                setCurrentUserFull(userEmail, nom, prenom, type, tel, cin, "");
                showCalendarViewInternal();
        }

        // Méthode pour afficher la vue du calendrier (sans paramètre)
        private void showCalendarViewInternal() {
                try {
                        System.out.println("📅 Chargement du calendrier pour: " + getCurrentUserFullName());

                        Calendar calendar = new SimpleCalendar(new SimpleCalendarServiceDAO());

                        // CORRECTION : Créer le contrôleur avec les services
                        calendarController = createPeriodCalendarViewController(calendar);

                        primaryStage.setScene(calendarController.getScene());
                        primaryStage.setTitle("Calendrier - " + getCurrentUserFullName());
                        primaryStage.setWidth(1200);
                        primaryStage.setHeight(700);
                        primaryStage.centerOnScreen();
                        primaryStage.show();

                        System.out.println("✅ Calendrier affiché avec succès");
                } catch (Exception e) {
                        System.err.println("❌ Erreur lors du chargement du calendrier: " + e.getMessage());
                        e.printStackTrace();
                        showError("Erreur lors du chargement du calendrier: " + e.getMessage());
                }
        }

        private PeriodCalendarViewController createPeriodCalendarViewController(Calendar calendar) {
                // ✅ CORRECTION AMÉLIORÉE : Vérification robuste des services
                System.out.println("🔍 Vérification des services pour PeriodCalendarViewController...");

                if (!areServicesConfigured()) {
                        System.err.println("❌ CRITIQUE: Les services ne sont pas configurés!");
                        System.err.println("   Tentative de réinitialisation...");
                        initializeServices(); // Tenter de réinitialiser

                        if (!areServicesConfigured()) {
                                System.err.println("❌ ÉCHEC: Impossible d'initialiser les services");
                                // Continuer quand même mais avec des valeurs null
                        }
                }

                System.out.println("✅ Services pour PeriodCalendarViewController:");
                System.out.println("   - EnseignantService: " + (enseignantService != null ? "✓" : "✗"));
                System.out.println("   - SalleService: " + (salleService != null ? "✗" : "✗"));
                System.out.println("   - EtudiantService: " + (etudiantService != null ? "✓" : "✗"));

                // CORRECTION : Utiliser CalendarWeekViewConfiguration qui est concrète
                CalendarWeekViewConfiguration config = new CalendarWeekViewConfiguration(
                        java.time.LocalTime.of(8,0),    // heure de début
                        java.time.LocalTime.of(19,0),   // heure de fin
                        java.time.Duration.ofMinutes(60), // durée des créneaux
                        java.time.Duration.ofHours(4),   // durée de vue
                        java.time.Duration.ofHours(1),   // durée de défilement
                        javafx.scene.paint.Color.AQUA    // couleur
                );

                // CORRECTION : Créer le contrôleur avec tous les paramètres requis
                return new PeriodCalendarViewController(
                        calendar,
                        config,  // ← CalendarWeekViewConfiguration concrète
                        enseignantService,
                        salleService,
                        etudiantService
                );
        }
        /**
         * Initialise les services nécessaires pour l'application
         */
        private void initializeServices() {
                System.out.println("🔧 ==========================================");
                System.out.println("🔧 INITIALISATION DES SERVICES");
                System.out.println("🔧 ==========================================");

                try {
                        // Créer les instances des services
                        EnseignantService enseignantService = new EnseignantService();
                        SalleRestService salleService = new SalleRestService();
                        EtudiantService etudiantService = new EtudiantService();
                        SlotView.setEnseignantService(enseignantService);
                        SlotView.setSalleRestService(salleService);
                        System.out.println("✅ Instances créées:");
                        System.out.println("   - EnseignantService: " + (enseignantService != null));
                        System.out.println("   - SalleService: " + (salleService != null));
                        System.out.println("   - EtudiantService: " + (etudiantService != null));

                        // Les configurer dans CalendarApp
                        CalendarApp.setServices(enseignantService, salleService, etudiantService);

                        System.out.println("✅ Services configurés dans CalendarApp");

                        // Test de fonctionnement basique
                        System.out.println("🔍 Test de fonctionnement des services...");
                        if (enseignantService != null) {
                                System.out.println("   - EnseignantService: Opérationnel");
                        }
                        if (salleService != null) {
                                System.out.println("   - SalleService: Opérationnel");
                        }

                        System.out.println("🎉 Initialisation des services terminée avec succès!");

                } catch (Exception e) {
                        System.err.println("❌ ERREUR lors de l'initialisation des services: " + e.getMessage());
                        e.printStackTrace();
                }

                System.out.println("🔧 ==========================================");
        }
        private void showLoginViewInternal() {
                System.out.println("🔐 Affichage de la vue de connexion");
                try {
                        if (loginView == null) {
                                loginView = new LoginView();
                        }
                        primaryStage.setScene(loginView.constructScene());
                        primaryStage.setTitle("Connexion - Application de Gestion");
                        primaryStage.centerOnScreen();
                        primaryStage.show();
                } catch (Exception e) {
                        System.err.println("❌ Erreur lors de l'affichage de la vue de connexion: " + e.getMessage());
                        e.printStackTrace();
                }
        }

        // Méthode pour afficher la vue d'inscription
        private void showRegisterViewInternal() {
                System.out.println("📝 Affichage de la vue d'inscription");
                try {
                        if (registerView == null) {
                                registerView = new RegisterView();
                        }
                        primaryStage.setScene(registerView.constructScene());
                        primaryStage.setTitle("Inscription - Application de Gestion");
                        primaryStage.centerOnScreen();
                        primaryStage.show();
                } catch (Exception e) {
                        System.err.println("❌ Erreur lors de l'affichage de la vue d'inscription: " + e.getMessage());
                        e.printStackTrace();
                }
        }

        // Méthode pour afficher la vue profil
       /* private void showProfileViewInternal() {
                System.out.println("👤 Affichage de la vue profil");
                try {
                        ProfileView profileView = new ProfileView();
                        primaryStage.setScene(profileView.constructScene());
                        primaryStage.setTitle("Profil - " + getCurrentUserFullName());
                        primaryStage.centerOnScreen();
                        primaryStage.show();
                        System.out.println("✅ Profil affiché pour: " + getCurrentUserFullName());
                } catch (Exception e) {
                        System.err.println("❌ Erreur lors de l'affichage du profil: " + e.getMessage());
                        e.printStackTrace();
                        showError("Erreur lors du chargement du profil: " + e.getMessage());
                }
        }*/
        private void showProfileViewInternal() {
                System.out.println("👤 Affichage de la vue profil");
                try {
                        ProfileView profileView = new ProfileView();
                        primaryStage.setScene(profileView.getScene()); // ← CORRECTION : getScene() au lieu de constructScene()
                        primaryStage.setTitle("Profil - " + getCurrentUserFullName());
                        primaryStage.centerOnScreen();
                        primaryStage.show();
                        System.out.println("✅ Profil affiché pour: " + getCurrentUserFullName());
                } catch (Exception e) {
                        System.err.println("❌ Erreur lors de l'affichage du profil: " + e.getMessage());
                        e.printStackTrace();
                        showError("Erreur lors du chargement du profil: " + e.getMessage());
                }
        }

        // === MÉTHODES STATIQUES (pour appel depuis d'autres classes) ===

        public static void showCalendarView(String userEmail) {
                if (instance == null) {
                        System.err.println("❌ CalendarApp instance not initialized.");
                        return;
                }

                System.out.println("🔎 Chargement des informations utilisateur depuis l'API pour : " + userEmail);

                Service<User> userService = UserService.getUserByEmail(userEmail);

                // Quand la requête réussit :
                userService.setOnSucceeded(event -> {
                        User user = userService.getValue();
                        if (user != null) {
                                System.out.println("✅ Utilisateur trouvé : " + user.getPrenom() + " " + user.getNom());

                                // Met à jour les infos statiques dans CalendarApp
                                instance.setCurrentUserFull(
                                        user.getEmail(),
                                        user.getNom(),
                                        user.getPrenom(),
                                        user.getType(),
                                        user.getTel(),
                                        user.getCin(),
                                        user.getPhoto() != null ? user.getPhoto() : ""
                                );

                                // Puis affiche le calendrier
                                instance.showCalendarViewInternal();
                        } else {
                                System.err.println("⚠️ Aucun utilisateur trouvé avec cet email !");
                                instance.showError("Utilisateur introuvable pour l'email : " + userEmail);
                                showLoginView();
                        }
                });

                // En cas d'erreur :
                userService.setOnFailed(event -> {
                        Throwable ex = userService.getException();
                        System.err.println("❌ Erreur lors du chargement utilisateur : " + ex.getMessage());
                        ex.printStackTrace();
                        instance.showError("Erreur de connexion au serveur : " + ex.getMessage());
                        showLoginView();
                });

                // Lancer le service
                userService.start();
        }

        // Méthode pour afficher le calendrier (utilise les infos existantes)
        public static void showCalendarView() {
                if (instance != null) {
                        instance.showCalendarViewInternal();
                } else {
                        System.err.println("❌ CalendarApp instance not initialized.");
                }
        }

        // Méthode statique pour afficher la vue de connexion
        public static void showLoginView() {
                if (instance != null) {
                        instance.showLoginViewInternal();
                } else {
                        System.err.println("❌ CalendarApp instance not initialized.");
                }
        }

        // Méthode statique pour afficher la vue d'inscription
        public static void showRegisterView() {
                if (instance != null) {
                        instance.showRegisterViewInternal();
                } else {
                        System.err.println("❌ CalendarApp instance not initialized.");
                }
        }

        // Méthode statique pour afficher la vue profil
        public static void showProfileView() {
                if (instance != null) {
                        instance.showProfileViewInternal();
                } else {
                        System.err.println("❌ CalendarApp instance not initialized.");
                }
        }

        // === GESTION DES DONNÉES UTILISATEUR ===

        // Méthode pour définir toutes les informations utilisateur
        public static void setCurrentUserFull(String email, String nom, String prenom, String type,
                                              String tel, String cin, String photo) {
                currentUserEmail = email != null ? email : "";
                currentUserNom = nom != null ? nom : "";
                currentUserPrenom = prenom != null ? prenom : "";
                currentUserType = type != null ? type : "";
                currentUserTel = tel != null ? tel : "";
                currentUserCin = cin != null ? cin : "";
                currentUserPhoto = photo != null ? photo : "";

                System.out.println("👤 Utilisateur connecté avec toutes les informations:");
                System.out.println("   - Email: " + currentUserEmail);
                System.out.println("   - Nom: " + currentUserNom);
                System.out.println("   - Prénom: " + currentUserPrenom);
                System.out.println("   - Type: " + currentUserType);
                System.out.println("   - Téléphone: " + currentUserTel);
                System.out.println("   - CIN: " + currentUserCin);
                System.out.println("   - Photo: " + currentUserPhoto);
        }

        // Méthodes pour récupérer les informations
        public static String getCurrentUserEmail() {
                return currentUserEmail != null ? currentUserEmail : "";
        }

        public static String getCurrentUserNom() {
                return currentUserNom != null ? currentUserNom : "";
        }

        public static String getCurrentUserPrenom() {
                return currentUserPrenom != null ? currentUserPrenom : "";
        }

        public static String getCurrentUserType() {
                return currentUserType != null ? currentUserType : "";
        }

        public static String getCurrentUserTel() {
                return currentUserTel != null ? currentUserTel : "";
        }

        public static String getCurrentUserCin() {
                return currentUserCin != null ? currentUserCin : "";
        }

        public static String getCurrentUserPhoto() {
                return currentUserPhoto != null ? currentUserPhoto : "";
        }

        // Méthode pour obtenir le nom complet
        public static String getCurrentUserFullName() {
                String nom = getCurrentUserNom();
                String prenom = getCurrentUserPrenom();

                if (nom.isEmpty() && prenom.isEmpty()) {
                        return "Utilisateur non connecté";
                } else if (nom.isEmpty()) {
                        return prenom;
                } else if (prenom.isEmpty()) {
                        return nom;
                } else {
                        return prenom + " " + nom;
                }
        }

        // Méthode pour vérifier si un utilisateur est connecté
        public static boolean isUserLoggedIn() {
                return currentUserEmail != null && !currentUserEmail.isEmpty();
        }

        // Méthode pour obtenir les informations complètes formatées
        public static String getCurrentUserInfo() {
                return String.format("%s %s (%s) - %s",
                        getCurrentUserPrenom(),
                        getCurrentUserNom(),
                        getCurrentUserEmail(),
                        getCurrentUserType());
        }

        // Méthode de déconnexion
        public static void logout() {
                String email = currentUserEmail;

                // Réinitialiser toutes les données
                currentUserEmail = null;
                currentUserNom = null;
                currentUserPrenom = null;
                currentUserType = null;
                currentUserTel = null;
                currentUserCin = null;
                currentUserPhoto = null;

                System.out.println("🔒 Utilisateur déconnecté: " + email);
                showLoginView();
        }

        // Méthode pour debugger les données utilisateur
        public static void debugUserData() {
                System.out.println("🐛 DEBUG User Data in CalendarApp:");
                System.out.println("   - Email: '" + currentUserEmail + "'");
                System.out.println("   - Nom: '" + currentUserNom + "'");
                System.out.println("   - Prénom: '" + currentUserPrenom + "'");
                System.out.println("   - Type: '" + currentUserType + "'");
                System.out.println("   - Téléphone: '" + currentUserTel + "'");
                System.out.println("   - CIN: '" + currentUserCin + "'");
                System.out.println("   - Connecté: " + isUserLoggedIn());
        }

        // === MÉTHODES UTILITAIRES ===

        private void showError(String message) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
        }

        // Méthode pour obtenir l'instance (singleton pattern)
        public static CalendarApp getInstance() {
                return instance;
        }

        // Méthodes pour accéder aux services depuis d'autres classes
        public static EnseignantService getEnseignantService() {
                return enseignantService;
        }

        public static SalleRestService getSalleService() {
                return salleService;
        }

        public static EtudiantService getEtudiantService() {
                return etudiantService;
        }

        /**
         * Affiche l'état de l'application (pour débogage)
         */
        public static void printAppStatus() {
                System.out.println("=== ÉTAT CALENDARAPP ===");
                System.out.println("Instance: " + (instance != null ? "✓ Initialisée" : "✗ Non initialisée"));
                System.out.println("Utilisateur connecté: " + (isUserLoggedIn() ? "✓ " + getCurrentUserFullName() : "✗ Non connecté"));
                System.out.println("Services configurés: " + (areServicesConfigured() ? "✓ Oui" : "✗ Non"));
                System.out.println("=========================");
        }

        public static void main(String[] args) {
                System.out.println("🚀 Lancement de l'application CalendarApp...");

                // ✅ CORRECTION : INITIALISER LES SERVICES DÈS LE DÉBUT
                System.out.println("🔧 Pré-initialisation des services...");
                CalendarApp.setServices(
                        new EnseignantService(),
                        new SalleRestService(),
                        new EtudiantService() // ou null
                );

                // Afficher l'état initial
                printAppStatus();

                launch(args);
        }
}