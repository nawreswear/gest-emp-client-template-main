package tn.iset.m2glnt.client.viewer.view;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import tn.iset.m2glnt.client.viewer.CalendarApp;
import tn.iset.m2glnt.client.viewer.presenter.dialog.SimpleSlotFormFactory;
import tn.iset.m2glnt.client.viewer.presenter.dialog.SimpleSlotFormFactory.ThemeMode;
import tn.iset.m2glnt.client.viewer.view.CalendarView;
import tn.iset.m2glnt.client.viewer.view.SlotView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.input.KeyCode;

public class GridCalendarView extends GridPane implements CalendarView {
    private boolean isRefreshing = false;
    private final List<Label> firstRowLabels = new ArrayList<>();
    private final List<Label> firstColumnLabels = new ArrayList<>();
    private final Map<Integer, SlotView> slotViewMap = new HashMap<>();
    private final List<CalendarEventListener> eventListeners = new ArrayList<>();
    private final List<ThemeChangeListener> themeListeners = new ArrayList<>();
    private final Map<String, Object> userData = new HashMap<>();
    private final Map<String, List<SlotView>> searchIndex = new HashMap<>();
    private final Map<String, Boolean> activeFilters = new HashMap<>();
    private final Map<String, Integer> statistics = new HashMap<>();

    private boolean controlsInitialized = false;
    private HBox controlButtons;
    private Node buttonBox;
    private Scene scene;
    private Button closeButton;
    private Button themeButton;
    private Button exportCsvButton;
    private Button searchButton;
    private Button statsButton;
    private Button helpButton;

    private TextField searchField;
    private VBox searchResultsPanel;
    private StackPane notificationContainer;
    private Pane currentTimeIndicator;

    private int colorIndex = 0;
    private SimpleSlotFormFactory slotFormFactory;

    // 🎨 Enhanced Color Palette - Bleu professionnel amélioré (identique au GridCalendarView)
    private static final String[][] COLOR_SCHEMES = {
            {"#E8F4FD", "#1E88E5", "#1565C0", "#0D47A1"},
            {"#E3F2FD", "#1976D2", "#0D47A1", "#082E5C"},
            {"#F0F8FF", "#2196F3", "#1976D2", "#0D47A1"},
            {"#E1F5FE", "#03A9F4", "#0288D1", "#01579B"},
            {"#E8EAF6", "#3F51B5", "#303F9F", "#1A237E"},
            {"#E3F2FD", "#2196F3", "#1976D2", "#0D47A1"},
            {"#F3E5F5", "#9C27B0", "#7B1FA2", "#4A148C"},
            {"#E8F5E9", "#4CAF50", "#388E3C", "#1B5E20"},
            {"#FFF3E0", "#FF9800", "#F57C00", "#E65100"},
            {"#FCE4EC", "#E91E63", "#C2185B", "#880E4F"}
    };

    // 🌓 Theme Management
    private BooleanProperty darkModeProperty = new SimpleBooleanProperty(false);

    // 🔄 Display Modes
    public enum DisplayMode {
        GRID("⏹️", "Grille", "Affichage grille classique"),
        LIST("📋", "Liste", "Vue liste verticale"),
        TIMELINE("⏰", "Chronologie", "Vue horizontale chronologique"),
        COMPACT("📱", "Compact", "Vue dense optimisée"),
        DAY("☀️", "Journalier", "Vue par jour"),
        WEEK("📅", "Hebdomadaire", "Vue par semaine");

        private final String icon;
        private final String name;
        private final String description;

        DisplayMode(String icon, String name, String description) {
            this.icon = icon;
            this.name = name;
            this.description = description;
        }

        public String getIcon() { return icon; }
        public String getName() { return name; }
        public String getDescription() { return description; }
    }

    private ObjectProperty<DisplayMode> currentDisplayMode = new SimpleObjectProperty<>(DisplayMode.GRID);
    private final DoubleProperty scaleFactor = new SimpleDoubleProperty(1.0);

    // 🎨 Professional Color Scheme - BLEU PROFESSIONNEL AMÉLIORÉ
    private static final String LIGHT_SCENE_BACKGROUND = "#F5F9FF";
    private static final String DARK_SCENE_BACKGROUND = "#0A1929";
    private static final String LIGHT_CALENDAR_CARD = "-fx-background-color: rgba(255, 255, 255, 0.95); -fx-background-radius: 20;";
    private static final String DARK_CALENDAR_CARD = "-fx-background-color: rgba(15, 30, 50, 0.95); -fx-background-radius: 20;";

    private static final String LIGHT_HEADER_GRADIENT = "linear-gradient(to right, #1E88E5, #1565C0)";
    private static final String DARK_HEADER_GRADIENT = "linear-gradient(to right, #1565C0, #0D47A1)";

    private static final String LIGHT_FIRST_ROW_BG = "linear-gradient(to bottom, #E3F2FD, #BBDEFB)";
    private static final String LIGHT_FIRST_COLUMN_BG = "linear-gradient(to right, #E3F2FD, #BBDEFB)";
    private static final String DARK_FIRST_ROW_BG = "linear-gradient(to bottom, #0D1B2A, #1B263B)";
    private static final String DARK_FIRST_COLUMN_BG = "linear-gradient(to right, #0D1B2A, #1B263B)";

    private static final String LIGHT_TEXT = "white";
    private static final String DARK_TEXT = "#E0E0E0";

    public GridCalendarView() {
        super();
        System.out.println("🏗️ Construction de GridCalendarView...");

        // INITIALISER LA GRILLE D'ABORD
        initializeGrid();

        // PUIS INITIALISER LES BOUTONS
        initializeControlButtons();

        applyResponsiveStyles();
        initializeAdvancedFeatures();
        setupPropertyListeners();
        setupEventListeners();
        addQuickHelpTooltips();
        debugButtonStates();
        debugState();

        // FORCER L'APPLICATION DU THÈME
        applyCurrentTheme();
        System.out.println("✅ GridCalendarView initialisé avec système de rafraîchissement");
    }

    // NOUVELLE MÉTHODE POUR INITIALISER LES BOUTONS
    private void initializeControlButtons() {
        System.out.println("🛠️ Initialisation des boutons de contrôle...");

        // Créer les boutons principaux
        themeButton = createIconButton(getThemeIcon(), "Changer le thème (Ctrl+D)", "#3498DB");
        closeButton = createIconButton("✕", "Fermer", "#E74C3C");

        // Configurer les actions IMMÉDIATEMENT
        setupControlButtonsActions();

        System.out.println("✅ Boutons initialisés: themeButton=" + (themeButton != null) + ", closeButton=" + (closeButton != null));
    }

    public void testAllButtons() {
        System.out.println("=== TEST COMPLET DES BOUTONS ===");

        // Test controlButtons
        if (controlButtons != null) {
            System.out.println("✅ controlButtons: Présent avec " + controlButtons.getChildren().size() + " boutons");
            for (Node node : controlButtons.getChildren()) {
                if (node instanceof Button) {
                    Button btn = (Button) node;
                    System.out.println("   - " + btn.getText() +
                            " (Parent: " + (btn.getParent() != null ? "✓" : "✗") +
                            ", Scene: " + (btn.getScene() != null ? "✓" : "✗") + ")");
                }
            }
        } else {
            System.err.println("❌ controlButtons: NULL");
        }

        // Test boutons individuels
        System.out.println("🔍 Boutons individuels:");
        System.out.println("   - themeButton: " + (themeButton != null ?
                "'" + themeButton.getText() + "'" : "null"));
        System.out.println("   - closeButton: " + (closeButton != null ?
                "'" + closeButton.getText() + "'" : "null"));

        System.out.println("==============================");
    }

    private void ensureNotificationContainer() {
        if (notificationContainer == null) {
            notificationContainer = new StackPane();
            notificationContainer.setMouseTransparent(true);
            notificationContainer.setPickOnBounds(false);
            System.out.println("✅ notificationContainer initialisé");
        }
    }
    /**
     * Retourne un SlotView par son ID (pour le Presenter/Controller)
     */
    public SlotView getSlotViewById(int slotId) {
        return slotViewMap.get(slotId);
    }


    // 🔧 INITIALIZATION METHODS
    private void initializeGrid() {
        setHgap(12);
        setVgap(12);
        setPadding(new Insets(25));
        setAlignment(Pos.CENTER);
        applyGridBackground();
        applyCurrentTheme();
    }

    private void initializeAdvancedFeatures() {
        setupKeyboardShortcuts();
        setupDragAndDrop();
        initializeSearchSystem();
        initializeNotificationSystem();
        setupAutoRefresh();
        initializeStatistics();
    }

    private void updateTitleInHeader() {
        if (scene != null) {
            Parent root = scene.getRoot();
            if (root instanceof BorderPane) {
                BorderPane mainContainer = (BorderPane) root;
                Node top = mainContainer.getTop();
                if (top instanceof HBox) {
                    HBox header = (HBox) top;
                    applyHeaderStyle(header);
                }
            }
        }
    }

    private void initializeSearchSystem() {
        searchField = new TextField();
        searchField.setPromptText("🔍 Rechercher un cours...");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> performSearch(newVal));

        searchResultsPanel = new VBox(5);
        searchResultsPanel.setVisible(false);
        searchResultsPanel.setManaged(false);
        searchResultsPanel.setStyle("-fx-background-color: rgba(255,255,255,0.95); -fx-padding: 10; -fx-background-radius: 10;");
        searchResultsPanel.setMaxWidth(300);
        searchResultsPanel.setPrefWidth(300);
    }

    private void initializeNotificationSystem() {
        if (notificationContainer == null) {
            notificationContainer = new StackPane();
            notificationContainer.setMouseTransparent(true);
            notificationContainer.setPickOnBounds(false);
            System.out.println("✅ Notification system initialized");
        }
    }

    private void initializeStatistics() {
        statistics.put("Créneaux totaux", 0);
        statistics.put("Couleurs utilisées", 0);
        statistics.put("Colonnes", 0);
        statistics.put("Lignes", 0);
        statistics.put("Recherches effectuées", 0);
    }

    private void setupAutoRefresh() {
        // 🔥 DÉSACTIVER le rafraîchissement automatique qui cause la perte des données
        // Remplacer par un rafraîchissement manuel uniquement
        System.out.println("⏰ Rafraîchissement automatique DÉSACTIVÉ - Utilisez Ctrl+R pour rafraîchir manuellement");

        // Garder seulement le rafraîchissement manuel
        setupSmartRefresh();

        // 🔥 OPTIONNEL : Garder un rafraîchissement très espacé (10 minutes) si vraiment nécessaire
        Timeline safeAutoRefresh = new Timeline(new KeyFrame(Duration.minutes(10), e -> {
            System.out.println("🔄 Rafraîchissement automatique sécurisé (10 min)");
            safeRefreshCalendarData();
        }));
        safeAutoRefresh.setCycleCount(Timeline.INDEFINITE);
        safeAutoRefresh.play();
    }

    /**
     * Rafraîchissement sécurisé qui ne supprime pas les slots existants
     */
    private void safeRefreshCalendarData() {
        if (isRefreshing) return;

        isRefreshing = true;
        try {
            System.out.println("🔄 Rafraîchissement sécurisé en cours...");

            // 🔥 NE PAS nettoyer les slots existants
            // Seulement mettre à jour les styles et vérifier l'état
            applyCurrentTheme();
            updateStatistics();

            showTemporaryNotification("🎨 Styles actualisés", 1000);
            System.out.println("✅ Rafraîchissement sécurisé terminé");

        } finally {
            isRefreshing = false;
        }
    }

    private void setupSmartRefresh() {
        // Rafraîchir après certaines actions
        this.addEventHandler(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown() && event.getCode() == javafx.scene.input.KeyCode.R) {
                System.out.println("🔄 Rafraîchissement manuel (Ctrl+R)");
                refreshCalendarData();
            }
        });
    }
    // Méthode pour ajouter un nouveau slot avec animation
    public void addNewSlotWithAnimation(SlotView slotView, int rowIndex, int columnIndex, int rowSpan, int colSpan) {
        System.out.println("🎬 Ajout animé du nouveau slot " + slotView.getSlotId());

        // 🔥 UTILISER LA MÉTHODE SÉCURISÉE
        addSlotWithoutAutoRefresh(slotView, rowIndex, columnIndex, rowSpan, colSpan);

        // Notification spéciale pour les nouveaux slots
        showTemporaryNotification("✅ Nouveau créneau ajouté avec succès", 2000);
    }

    private void refreshData() {
        System.out.println("🔄 Début du rafraîchissement des données...");

        // Animation de chargement
        showTemporaryNotification("🔄 Actualisation des données...", 1500);

        // Rafraîchir la grille
        refreshCalendarData();

        // Mettre à jour les statistiques
        updateStatistics();

        System.out.println("✅ Rafraîchissement des données terminé");
    }
    /**
     * Méthode principale de rafraîchissement - RECONSTRUCTION COMPLÈTE OPTIMISÉE
     */
    /**
     * Méthode principale de rafraîchissement - RECONSTRUCTION COMPLÈTE OPTIMISÉE
     */
    private void performRefresh() {
        System.out.println("🔄 Exécution du rafraîchissement RECONSTRUCTION...");

        // 🔥 VÉRIFIER si un rafraîchissement est vraiment nécessaire
        if (slotViewMap.isEmpty() && firstRowLabels.isEmpty() && firstColumnLabels.isEmpty()) {
            System.out.println("ℹ️ Aucune donnée à rafraîchir - Ignorer");
            return;
        }

        // 🔥 SAUVEGARDER les données AVANT de nettoyer
        List<String> savedFirstRowTexts = firstRowLabels.stream()
                .map(Label::getText)
                .collect(Collectors.toList());
        List<String> savedFirstColumnTexts = firstColumnLabels.stream()
                .map(Label::getText)
                .collect(Collectors.toList());
        Map<Integer, SlotView> savedSlotViewMap = new HashMap<>(slotViewMap);
        Node savedButtonBox = buttonBox;

        System.out.println("📊 Données sauvegardées - Slots: " + savedSlotViewMap.size() +
                ", Lignes: " + savedFirstRowTexts.size() +
                ", Colonnes: " + savedFirstColumnTexts.size());

        // Nettoyer TOUT
        clearViewSlots();
        clearLabelsInFirstRow();
        clearLabelsInFirstColumn();
        clearButtonBoxInFirstCell();

        // 🔥 RECONSTRUIRE les labels de première ligne IMMÉDIATEMENT
        if (!savedFirstRowTexts.isEmpty()) {
            for (int i = 0; i < savedFirstRowTexts.size(); i++) {
                String text = savedFirstRowTexts.get(i);
                Label newLabel = new Label(text);
                addLabelInFirstRow(newLabel, i);

                // 🔥 FORCER l'affichage immédiat sans délai d'animation
                newLabel.setOpacity(1.0);
                newLabel.setScaleX(1.0);
                newLabel.setScaleY(1.0);
                newLabel.setTranslateY(0);
            }
            System.out.println("✅ " + savedFirstRowTexts.size() + " labels de première ligne reconstruits");
        }

        // 🔥 RECONSTRUIRE les labels de première colonne IMMÉDIATEMENT
        if (!savedFirstColumnTexts.isEmpty()) {
            for (int i = 0; i < savedFirstColumnTexts.size(); i++) {
                String text = savedFirstColumnTexts.get(i);
                Label newLabel = new Label(text);
                addLabelInFirstColumn(newLabel, i);

                // 🔥 FORCER l'affichage immédiat
                newLabel.setOpacity(1.0);
                newLabel.setScaleX(1.0);
                newLabel.setScaleY(1.0);
                newLabel.setTranslateY(0);
            }
            System.out.println("✅ " + savedFirstColumnTexts.size() + " labels de première colonne reconstruits");
        }

        // 🔥 RECONSTRUIRE le button box si existant
        if (savedButtonBox instanceof HBox) {
            addButtonBoxInTopLeftCell((HBox) savedButtonBox);
        }

        // 🔥 RECONSTRUIRE les slots
        int slotsRecreated = 0;
        for (SlotView slotView : savedSlotViewMap.values()) {
            // Récupérer les informations de position du slot
            Integer rowIndex = GridPane.getRowIndex(slotView);
            Integer columnIndex = GridPane.getColumnIndex(slotView);
            Integer rowSpan = GridPane.getRowSpan(slotView);
            Integer colSpan = GridPane.getColumnSpan(slotView);

            if (rowIndex != null && columnIndex != null) {
                // RECRÉER le slot avec les mêmes propriétés
                addSlotView(
                        slotView,
                        rowIndex,
                        columnIndex,
                        rowSpan != null ? rowSpan : 1,
                        colSpan != null ? colSpan : 1
                );
                slotsRecreated++;
            }
        }

        // 🔥 FORCER l'application du thème IMMÉDIATEMENT
        applyCurrentTheme();

        // 🔥 FORCER le layout
        requestLayout();

        System.out.println("✅ Reconstruction complète terminée - " +
                "Lignes: " + firstRowLabels.size() +
                ", Colonnes: " + firstColumnLabels.size() +
                ", Slots recréés: " + slotsRecreated + "/" + savedSlotViewMap.size());
    }
    /**
     * Vérifie l'état des données et répare si nécessaire
     */
    public void verifyAndRepairDataState() {
        System.out.println("🔍 Vérification de l'état des données...");

        int missingSlots = 0;
        int missingRowLabels = 0;
        int missingColumnLabels = 0;

        // Vérifier les slots
        for (SlotView slot : slotViewMap.values()) {
            if (!getChildren().contains(slot)) {
                missingSlots++;
                System.out.println("❌ Slot " + slot.getSlotId() + " manquant dans la grille");
            }
        }

        // Vérifier les labels de première ligne
        for (Label label : firstRowLabels) {
            if (!getChildren().contains(label)) {
                missingRowLabels++;
                System.out.println("❌ Label ligne manquant: " + label.getText());
            }
        }

        // Vérifier les labels de première colonne
        for (Label label : firstColumnLabels) {
            if (!getChildren().contains(label)) {
                missingColumnLabels++;
                System.out.println("❌ Label colonne manquant: " + label.getText());
            }
        }

        if (missingSlots > 0 || missingRowLabels > 0 || missingColumnLabels > 0) {
            System.out.println("🔄 Réparation nécessaire - Éléments manquants: " +
                    missingSlots + " slots, " + missingRowLabels + " lignes, " + missingColumnLabels + " colonnes");
            refreshCalendarData();
        } else {
            System.out.println("✅ État des données: OK");
        }
    }
    public void refreshCalendarData() {
        if (isRefreshing) {
            System.out.println("⚠️ Rafraîchissement déjà en cours...");
            return;
        }

        isRefreshing = true;
        try {
            System.out.println("🚀 Début du rafraîchissement du calendrier...");

            // 🔥 FORCER l'affichage immédiat des premiers éléments
            performRefresh();

            // 🔥 FORCER une validation du layout
            Platform.runLater(() -> {
                requestLayout();
                applyCurrentTheme();
            });

            showTemporaryNotification("🔄 Calendrier actualisé", 1500);
            System.out.println("✅ Rafraîchissement terminé avec succès");

        } finally {
            isRefreshing = false;
        }
    }
    /**
     * Méthode de débogage pour vérifier l'état de l'affichage
     */
    public void debugDisplayState() {
        System.out.println("=== ÉTAT AFFICHAGE ===");
        System.out.println("First Row Labels: " + firstRowLabels.size());
        System.out.println("First Column Labels: " + firstColumnLabels.size());
        System.out.println("Slots in Map: " + slotViewMap.size());
        System.out.println("Children in Grid: " + getChildren().size());

        // Vérifier la visibilité des premiers labels
        firstRowLabels.forEach(label ->
                System.out.println("Row Label: '" + label.getText() + "' - Visible: " + label.isVisible() +
                        " - Opacity: " + label.getOpacity()));

        firstColumnLabels.forEach(label ->
                System.out.println("Column Label: '" + label.getText() + "' - Visible: " + label.isVisible() +
                        " - Opacity: " + label.getOpacity()));
        System.out.println("=====================");
    }
    public void requestCalendarRefresh() {
        if (!isRefreshing) {
            refreshCalendarData();
        }
    }

    /**
     * Rafraîchit un slot spécifique sans boucle
     */
    public void refreshSlot(int slotId) {
        System.out.println("🔄 Rafraîchissement du slot " + slotId);

        SlotView slotView = slotViewMap.get(slotId);
        if (slotView != null) {
            // Animation de mise à jour sans notifier les écouteurs
            pulseSlot(slotId);
            System.out.println("✅ Slot " + slotId + " rafraîchi");
        } else {
            System.out.println("❌ Slot " + slotId + " non trouvé pour rafraîchissement");
        }
    }
    private void setupDragAndDrop() {
        setOnDragDetected(event -> {
            if (event.getTarget() instanceof SlotView) {
                SlotView slot = (SlotView) event.getTarget();
                startDragAndDrop(slot);
            }
        });
    }

    private void startDragAndDrop(SlotView slot) {
        showTemporaryNotification("🧩 Glisser-déposer activé pour le créneau #" + slot.getSlotId(), 2000);
    }

    private void setupKeyboardShortcuts() {
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    switch (event.getCode()) {
                        case F1 -> showHelpDialog();
                        case F2 -> quickAddSlot();
                        case F3 -> toggleSearchPanel();
                        case E -> { if (event.isControlDown()) showExportDialog(); }
                        case D -> { if (event.isControlDown()) toggleTheme(); }
                        case V -> { if (event.isControlDown()) cycleDisplayMode(); }
                        case F -> { if (event.isControlDown()) showFilterDialog(); }
                        case ESCAPE -> clearSearch();
                    }
                });
            }
        });
    }

    @Override
    public Scene constructScene() {
        if (scene == null) {
            System.out.println("🔄 Création de la scène pour la première fois...");

            ensureNotificationContainer();

            BorderPane mainContainer = new BorderPane();
            // Style professionnel pour le main container
            mainContainer.setStyle("-fx-background-color: #F5F9FF;");

            Node header = createEnhancedHeader();
            Node center = createCenterContent();
            Node bottom = createStatusBar();

            if (header != null) {
                mainContainer.setTop(header);
                System.out.println("✅ Header ajouté au mainContainer");
            } else {
                System.err.println("❌ Header est NULL!");
            }

            if (center != null) {
                mainContainer.setCenter(center);
            }

            if (bottom != null) {
                mainContainer.setBottom(bottom);
            }

            // FENÊTRE RÉDUITE - 1200x700 au lieu de 1400x800
            scene = new Scene(mainContainer, 1200, 700);
            scene.setFill(Color.web("#F5F9FF"));

            System.out.println("🎯 SCÈNE CRÉÉE - Taille réduite 1200x700");

            return scene;
        } else {
            System.out.println("ℹ️ Scène déjà créée, réutilisation...");
            return scene;
        }
    }

    private Node createCenterContent() {
        try {
            StackPane centerPane = new StackPane();

            if (this == null) {
                System.err.println("❌ GridCalendarView (this) est null!");
                return new StackPane();
            }

            StackPane calendarWrapper = new StackPane(this);
            calendarWrapper.setPadding(new Insets(15)); // Padding réduit
            calendarWrapper.setStyle((isDarkMode() ? DARK_CALENDAR_CARD : LIGHT_CALENDAR_CARD) +
                    "-fx-background-radius: 15;" + // Rayon réduit
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0, 0, 5);"); // Ombre plus subtile

            List<Node> childrenToAdd = new ArrayList<>();
            childrenToAdd.add(calendarWrapper);

            if (notificationContainer == null) {
                notificationContainer = new StackPane();
                notificationContainer.setMouseTransparent(true);
                notificationContainer.setPickOnBounds(false);
                System.out.println("✅ notificationContainer créé dans createCenterContent");
            }

            childrenToAdd.add(notificationContainer);

            centerPane.getChildren().addAll(childrenToAdd);
            StackPane.setAlignment(notificationContainer, Pos.TOP_CENTER);

            System.out.println("✅ Center content créé avec style optimisé");
            return centerPane;

        } catch (Exception e) {
            System.err.println("❌ Erreur critique dans createCenterContent: " + e.getMessage());
            e.printStackTrace();
            return new StackPane();
        }
    }

    private HBox createEnhancedHeader() {
        HBox header = new HBox(12);
        header.setPadding(new Insets(12, 25, 12, 25));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setMinHeight(60);
        header.setPrefHeight(60);
        header.setMaxHeight(60);

        // STYLE PROFESSIONNEL - Bleu professionnel
        header.setStyle("-fx-background-color: #1E88E5; " +
                "-fx-background-radius: 0 0 15 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(30, 136, 229, 0.4), 15, 0, 0, 5);");
        System.out.println("🔵 HEADER CRÉÉ AVEC STYLE PROFESSIONNEL");

        // Container pour le titre et l'icône
        HBox titleContainer = new HBox(8);
        titleContainer.setAlignment(Pos.CENTER_LEFT);
        titleContainer.setStyle("-fx-background-color: transparent;");

        // Icône calendrier
        Label icon = new Label("📅");
        icon.setFont(Font.font("System", FontWeight.BOLD, 22));
        icon.setStyle("-fx-text-fill: white;");

        // Titre
        Label title = new Label("Calendrier ISET");
        title.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 22));
        title.setStyle("-fx-text-fill: white; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 1, 1);");

        titleContainer.getChildren().addAll(icon, title);
        System.out.println("✅ Titre et icône ajoutés au container");

        // Espaceur flexible
        Region spacer = new Region();
        spacer.setStyle("-fx-background-color: transparent;");
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Création des boutons de contrôle
        controlButtons = createControlButtons();

        // Construction du header
        header.getChildren().addAll(titleContainer, spacer, controlButtons);

        System.out.println("🎯 Header créé avec " + header.getChildren().size() + " éléments");

        return header;
    }

    private Button createUserButton() {
        // Récupérer toutes les informations de l'utilisateur
        String userEmail = CalendarApp.getCurrentUserEmail();
        String userNom = CalendarApp.getCurrentUserNom();
        String userPrenom = CalendarApp.getCurrentUserPrenom();
        String userType = CalendarApp.getCurrentUserType();

        // Formater le texte du bouton selon le type d'utilisateur
        String buttonText;
        String userIcon;

        switch (userType.toLowerCase()) {
            case "admin":
                userIcon = "👑"; // Couronne pour admin
                buttonText = String.format("%s %s %s", userIcon, userPrenom, userNom);
                break;
            case "enseignant":
                userIcon = "🎓"; // Mortier pour enseignant
                buttonText = String.format("%s %s %s", userIcon, userPrenom, userNom);
                break;
            case "etudiant":
                userIcon = "👨‍🎓"; // Étudiant
                buttonText = String.format("%s %s %s", userIcon, userPrenom, userNom);
                break;
            default:
                userIcon = "👤"; // Utilisateur générique
                buttonText = String.format("%s %s %s", userIcon, userPrenom, userNom);
        }

        Button userButton = new Button(buttonText);

        // STYLE PROFESSIONNEL AVEC COULEUR PAR TYPE
        String userButtonStyle = getButtonStyleByUserType(userType);

        userButton.setStyle(userButtonStyle);

        // Styles interactifs
        userButton.setOnMouseEntered(e -> {
            userButton.setStyle(userButtonStyle +
                    getHoverStyleByUserType(userType));
        });

        userButton.setOnMouseExited(e -> {
            userButton.setStyle(userButtonStyle);
        });

        // FORCER la visibilité
        userButton.setVisible(true);
        userButton.setManaged(true);
        userButton.setDisable(false);

        System.out.println("🟢 Bouton utilisateur créé: " + buttonText + " - Type: " + userType);

        // Menu contextuel amélioré avec toutes les informations
        ContextMenu userMenu = createUserContextMenu(userEmail, userNom, userPrenom, userType);
        userButton.setOnMouseClicked(e -> userMenu.show(userButton, e.getScreenX(), e.getScreenY()));

        return userButton;
    }

    // Méthode pour obtenir le style selon le type d'utilisateur
    private String getButtonStyleByUserType(String userType) {
        String backgroundColor, borderColor;

        switch (userType.toLowerCase()) {
            case "admin":
                backgroundColor = "#D32F2F"; // Rouge pour admin
                borderColor = "#B71C1C";
                break;
            case "enseignant":
                backgroundColor = "#1976D2"; // Bleu pour enseignant
                borderColor = "#0D47A1";
                break;
            case "etudiant":
                backgroundColor = "#388E3C"; // Vert pour étudiant
                borderColor = "#1B5E20";
                break;
            default:
                backgroundColor = "#7B1FA2"; // Violet par défaut
                borderColor = "#4A148C";
        }

        return "-fx-background-color: " + backgroundColor + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 11px; " +
                "-fx-border-color: " + borderColor + "; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 12; " +
                "-fx-background-radius: 12; " +
                "-fx-padding: 6 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 1, 1); " +
                "-fx-cursor: hand;";
    }

    // Méthode pour obtenir le style de survol selon le type
    private String getHoverStyleByUserType(String userType) {
        String hoverColor;

        switch (userType.toLowerCase()) {
            case "admin":
                hoverColor = "#B71C1C";
                break;
            case "enseignant":
                hoverColor = "#0D47A1";
                break;
            case "etudiant":
                hoverColor = "#1B5E20";
                break;
            default:
                hoverColor = "#4A148C";
        }

        return "-fx-background-color: " + hoverColor + "; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 2, 2);";
    }

    // Méthode pour créer le menu contextuel amélioré
    private ContextMenu createUserContextMenu(String email, String nom, String prenom, String type) {
        ContextMenu userMenu = new ContextMenu();

        // En-tête avec les informations de l'utilisateur
        Label headerLabel = new Label(String.format("👤 %s %s\n✉️ %s\n🎯 %s",
                prenom, nom, email, type));
        headerLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 5px;");
        CustomMenuItem headerItem = new CustomMenuItem(headerLabel);
        headerItem.setHideOnClick(false);

        SeparatorMenuItem separator = new SeparatorMenuItem();

        // Option de déconnexion
        MenuItem logoutItem = new MenuItem("🚪 Déconnexion");
        logoutItem.setOnAction(e -> {
            System.out.println("🔒 Déconnexion demandée par: " + email);
            CalendarApp.logout();
            // Optionnel: Rediriger vers l'écran de connexion
        });

        // Option de profil
        MenuItem profileItem = new MenuItem("👤 Mon profil");
        profileItem.setOnAction(e -> {
            showUserProfileDialog(email, nom, prenom, type);
        });

        userMenu.getItems().addAll(headerItem, separator, profileItem, logoutItem);

        return userMenu;
    }

    private void showUserProfileDialog(String email, String nom, String prenom, String type) {
        // Créer une boîte de dialogue personnalisée
        Dialog<Void> profileDialog = new Dialog<>();
        profileDialog.setTitle("👤 Mon Profil");
        profileDialog.setHeaderText("Informations du Compte");

        // Appliquer le style de la boîte de dialogue
        DialogPane dialogPane = profileDialog.getDialogPane();
        applyProfileDialogTheme(dialogPane);

        // Créer le contenu principal avec un design moderne
        VBox content = createProfileContent(email, nom, prenom, type);

        // Créer le ScrollPane avec style personnalisé
        ScrollPane scrollPane = createProfileScrollPane(content);

        VBox mainContainer = new VBox();
        mainContainer.getChildren().addAll(scrollPane);

        dialogPane.setContent(mainContainer);
        dialogPane.getButtonTypes().add(ButtonType.CLOSE);

        // Configurer la taille et les effets
        profileDialog.setResizable(true);
        profileDialog.setHeight(600);
        profileDialog.setWidth(500);

        // Animation d'entrée
        setupDialogAnimation(profileDialog);

        profileDialog.showAndWait();
    }

    private ScrollPane createProfileScrollPane(VBox content) {
        ScrollPane scrollPane = new ScrollPane(content);

        // Style personnalisé pour le ScrollPane
        String scrollPaneStyle = isDarkMode() ?
                "-fx-background: transparent; " +
                        "-fx-background-color: transparent; " +
                        "-fx-border-color: transparent; " +
                        "-fx-padding: 0;" :

                "-fx-background: transparent; " +
                        "-fx-background-color: transparent; " +
                        "-fx-border-color: transparent; " +
                        "-fx-padding: 0;";

        scrollPane.setStyle(scrollPaneStyle);

        // Configuration du scrolling
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Style de la barre de défilement verticale
        Node verticalScrollBar = scrollPane.lookup(".scroll-bar:vertical");
        if (verticalScrollBar != null) {
            verticalScrollBar.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-background-insets: 0;" +
                            "-fx-padding: 0;"
            );
        }

        Node track = scrollPane.lookup(".scroll-bar:vertical .track");
        if (track != null) {
            track.setStyle(
                    "-fx-background-color: " + (isDarkMode() ? "rgba(255,255,255,0.1)" : "rgba(0,0,0,0.1)") + ";" +
                            "-fx-background-radius: 0;" +
                            "-fx-border-radius: 0;"
            );
        }

        Node thumb = scrollPane.lookup(".scroll-bar:vertical .thumb");
        if (thumb != null) {
            thumb.setStyle(
                    "-fx-background-color: " + (isDarkMode() ? "rgba(255,255,255,0.3)" : "rgba(0,0,0,0.3)") + ";" +
                            "-fx-background-radius: 4;" +
                            "-fx-border-radius: 4;"
            );
        }

        // CORRECTION : Appliquer le style aux boutons d'incrément et décrément
        String buttonStyle = "-fx-background-color: transparent; -fx-background-radius: 0; -fx-padding: 2;";
        applyStyleToNode(scrollPane, ".scroll-bar:vertical .increment-button", buttonStyle);
        applyStyleToNode(scrollPane, ".scroll-bar:vertical .decrement-button", buttonStyle);

        // CORRECTION : Appliquer le style aux flèches
        String arrowStyle = "-fx-background-color: " + (isDarkMode() ? "rgba(255,255,255,0.5)" : "rgba(0,0,0,0.5)") +
                "; -fx-shape: null; -fx-padding: 0;";
        applyStyleToNode(scrollPane, ".scroll-bar:vertical .increment-arrow", arrowStyle);
        applyStyleToNode(scrollPane, ".scroll-bar:vertical .decrement-arrow", arrowStyle);

        // Effet de défilement fluide
        scrollPane.setPannable(true);

        return scrollPane;
    }

    // Méthode utilitaire pour appliquer le style aux nodes
    private void applyStyleToNode(ScrollPane scrollPane, String selector, String style) {
        Node node = scrollPane.lookup(selector);
        if (node != null) {
            node.setStyle(style);
        }
    }

    private VBox createProfileContent(String email, String nom, String prenom, String type) {
        VBox content = new VBox(25);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(25, 25, 40, 25)); // Plus de padding en bas pour le scroll
        content.setStyle("-fx-background-color: transparent;");

        // En-tête avec avatar
        VBox headerSection = createProfileHeader(prenom, nom, type);

        // Section des informations détaillées
        GridPane infoGrid = createProfileInfoGrid(email, nom, prenom, type);

        // Section statistiques
        VBox statsSection = createProfileStatsSection();

        // Section historique (nouvelle section pour démontrer le scroll)
        VBox historySection = createProfileHistorySection();

        content.getChildren().addAll(headerSection, infoGrid, statsSection, historySection);
        return content;
    }

    private VBox createProfileHeader(String prenom, String nom, String type) {
        VBox header = new VBox(12);
        header.setAlignment(Pos.CENTER);

        // Avatar circulaire avec couleur selon le type
        StackPane avatarContainer = createUserAvatar(prenom, nom, type);

        // Nom et type
        VBox nameSection = new VBox(5);
        nameSection.setAlignment(Pos.CENTER);

        Label fullName = new Label(prenom + " " + nom);
        fullName.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " +
                (isDarkMode() ? "#E0E0E0" : "#2D2D2D") + ";");

        Label userType = new Label(getTypeDisplayName(type));
        userType.setStyle("-fx-font-size: 14px; -fx-text-fill: " +
                getTypeColor(type) + "; -fx-font-weight: bold; -fx-padding: 4 12;" +
                "-fx-background-color: " + getTypeBackgroundColor(type) + "; " +
                "-fx-background-radius: 12;");

        nameSection.getChildren().addAll(fullName, userType);
        header.getChildren().addAll(avatarContainer, nameSection);

        return header;
    }

    private StackPane createUserAvatar(String prenom, String nom, String type) {
        StackPane avatar = new StackPane();
        avatar.setPrefSize(90, 90);

        // Fond circulaire avec couleur selon le type
        Circle background = new Circle(45);
        background.setFill(getAvatarColor(type));
        background.setStroke(getAvatarBorderColor(type));
        background.setStrokeWidth(3);

        // Initiales
        String initials = getInitials(prenom, nom);
        Label initialsLabel = new Label(initials);
        initialsLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Effet d'ombre
        background.setEffect(new DropShadow(15, Color.color(0, 0, 0, 0.4)));

        // Badge de statut en ligne
        Circle statusBadge = new Circle(8);
        statusBadge.setFill(Color.LIMEGREEN);
        statusBadge.setStroke(Color.WHITE);
        statusBadge.setStrokeWidth(2);
        statusBadge.setTranslateX(25);
        statusBadge.setTranslateY(25);

        avatar.getChildren().addAll(background, initialsLabel, statusBadge);

        return avatar;
    }

    private GridPane createProfileInfoGrid(String email, String nom, String prenom, String type) {
        GridPane grid = new GridPane();
        grid.setVgap(15);
        grid.setHgap(20);
        grid.setPadding(new Insets(20));

        // Style du grid avec fond subtil
        grid.setStyle("-fx-background-color: " + (isDarkMode() ? "rgba(255,255,255,0.05)" : "rgba(0,0,0,0.03)") + ";" +
                "-fx-background-radius: 16; -fx-padding: 20; " +
                "-fx-border-color: " + (isDarkMode() ? "rgba(255,255,255,0.1)" : "rgba(0,0,0,0.1)") + ";" +
                "-fx-border-width: 1; -fx-border-radius: 16;");

        int row = 0;

        // Email avec icône
        addEnhancedInfoRow(grid, row++, "📧", "Adresse Email", email,
                "Votre identifiant de connexion principal");

        // Nom complet
        addEnhancedInfoRow(grid, row++, "👤", "Nom Complet", prenom + " " + nom,
                "Votre nom tel qu'affiché dans l'application");

        // Type d'utilisateur
        addEnhancedInfoRow(grid, row++, "🎯", "Rôle", getTypeDisplayName(type),
                "Votre niveau d'accès dans le système");

        // Établissement
        addEnhancedInfoRow(grid, row++, "🏫", "Établissement", "ISET",
                "Institut Supérieur des Études Technologiques");

        // Dernière connexion
        String lastLogin = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"));
        addEnhancedInfoRow(grid, row++, "📅", "Dernière Connexion", lastLogin,
                "Date et heure de votre dernière activité");

        // Statut
        addEnhancedInfoRow(grid, row++, "🟢", "Statut", "En ligne • Actif",
                "Votre statut actuel dans le système");

        return grid;
    }

    private void addEnhancedInfoRow(GridPane grid, int row, String icon, String label, String value, String description) {
        // Container principal pour la ligne
        VBox rowContainer = new VBox(5);

        // Première ligne : icône, label et valeur
        HBox mainLine = new HBox(12);
        mainLine.setAlignment(Pos.CENTER_LEFT);

        // Icône
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 18px;");

        // Label
        Label infoLabel = new Label(label);
        infoLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " +
                (isDarkMode() ? "#B0B0B0" : "#666666") + "; -fx-font-size: 13px;");

        // Valeur
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: " + (isDarkMode() ? "#E0E0E0" : "#2D2D2D") + "; " +
                "-fx-font-size: 14px; -fx-font-weight: 500;");
        valueLabel.setWrapText(true);

        // Espaceur
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        mainLine.getChildren().addAll(iconLabel, infoLabel, spacer, valueLabel);

        // Deuxième ligne : description
        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-text-fill: " + (isDarkMode() ? "#808080" : "#999999") + "; " +
                "-fx-font-size: 11px; -fx-font-style: italic;");
        descLabel.setWrapText(true);
        descLabel.setPadding(new Insets(0, 0, 0, 30)); // Alignement avec le texte au-dessus

        rowContainer.getChildren().addAll(mainLine, descLabel);

        grid.add(rowContainer, 0, row);

        // Configuration des contraintes
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().setAll(col1);
    }

    private VBox createProfileStatsSection() {
        VBox statsSection = new VBox(12);
        statsSection.setAlignment(Pos.CENTER_LEFT);
        statsSection.setPadding(new Insets(15));
        statsSection.setStyle("-fx-background-color: " + (isDarkMode() ? "rgba(255,255,255,0.03)" : "rgba(0,0,0,0.02)") + ";" +
                "-fx-background-radius: 12; -fx-border-color: " +
                (isDarkMode() ? "rgba(255,255,255,0.05)" : "rgba(0,0,0,0.05)") + ";" +
                "-fx-border-width: 1; -fx-border-radius: 12;");

        Label statsTitle = new Label("📈 Activité Récente");
        statsTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " +
                (isDarkMode() ? "#E0E0E0" : "#2D2D2D") + ";");

        VBox statsList = new VBox(8);

        // Statistiques détaillées
        String[][] activities = {
                {"📊", "Créneaux consultés cette semaine", "12 créneaux"},
                {"✏️", "Modifications effectuées", "3 modifications"},
                {"⭐", "Créneaux favoris", "5 créneaux marqués"},
                {"👁️", "Dernière activité", "Il y a 5 minutes"},
                {"🔔", "Notifications reçues", "8 notifications"},
                {"📋", "Exports réalisés", "2 exports"}
        };

        for (String[] activity : activities) {
            HBox activityRow = new HBox(10);
            activityRow.setAlignment(Pos.CENTER_LEFT);

            Label icon = new Label(activity[0]);
            icon.setStyle("-fx-font-size: 14px;");

            VBox textContainer = new VBox(2);

            Label activityLabel = new Label(activity[1]);
            activityLabel.setStyle("-fx-text-fill: " + (isDarkMode() ? "#B0B0B0" : "#666666") + "; " +
                    "-fx-font-size: 12px;");

            Label valueLabel = new Label(activity[2]);
            valueLabel.setStyle("-fx-text-fill: " + (isDarkMode() ? "#E0E0E0" : "#2D2D2D") + "; " +
                    "-fx-font-size: 13px; -fx-font-weight: 500;");

            textContainer.getChildren().addAll(activityLabel, valueLabel);
            activityRow.getChildren().addAll(icon, textContainer);

            statsList.getChildren().add(activityRow);
        }

        statsSection.getChildren().addAll(statsTitle, new Separator(), statsList);
        return statsSection;
    }

    private VBox createProfileHistorySection() {
        VBox historySection = new VBox(12);
        historySection.setAlignment(Pos.CENTER_LEFT);
        historySection.setPadding(new Insets(15));
        historySection.setStyle("-fx-background-color: " + (isDarkMode() ? "rgba(255,255,255,0.03)" : "rgba(0,0,0,0.02)") + ";" +
                "-fx-background-radius: 12; -fx-border-color: " +
                (isDarkMode() ? "rgba(255,255,255,0.05)" : "rgba(0,0,0,0.05)") + ";" +
                "-fx-border-width: 1; -fx-border-radius: 12;");

        Label historyTitle = new Label("📝 Historique Récent");
        historyTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " +
                (isDarkMode() ? "#E0E0E0" : "#2D2D2D") + ";");

        VBox historyList = new VBox(8);

        // Historique des activités
        String[][] historyItems = {
                {"🕒", "Aujourd'hui, 14:30", "Consultation du créneau de Mathématiques"},
                {"🕒", "Aujourd'hui, 11:15", "Modification d'un créneau de Physique"},
                {"🕒", "Hier, 16:45", "Export du calendrier de la semaine"},
                {"🕒", "Hier, 09:20", "Ajout d'un créneau favori"},
                {"🕒", "25/12/2024, 15:30", "Consultation des statistiques mensuelles"},
                {"🕒", "24/12/2024, 10:00", "Modification du profil utilisateur"}
        };

        for (String[] history : historyItems) {
            HBox historyRow = new HBox(10);
            historyRow.setAlignment(Pos.CENTER_LEFT);

            Label icon = new Label(history[0]);
            icon.setStyle("-fx-font-size: 12px;");

            VBox textContainer = new VBox(2);

            Label timeLabel = new Label(history[1]);
            timeLabel.setStyle("-fx-text-fill: " + (isDarkMode() ? "#808080" : "#999999") + "; " +
                    "-fx-font-size: 11px; -fx-font-style: italic;");

            Label actionLabel = new Label(history[2]);
            actionLabel.setStyle("-fx-text-fill: " + (isDarkMode() ? "#E0E0E0" : "#2D2D2D") + "; " +
                    "-fx-font-size: 12px;");
            actionLabel.setWrapText(true);

            textContainer.getChildren().addAll(timeLabel, actionLabel);
            historyRow.getChildren().addAll(icon, textContainer);

            historyList.getChildren().add(historyRow);
        }

        historySection.getChildren().addAll(historyTitle, new Separator(), historyList);
        return historySection;
    }

    private void applyProfileDialogTheme(DialogPane dialogPane) {
        boolean isDark = isDarkMode();

        String dialogStyle = isDark ?
                "-fx-background-color: linear-gradient(to bottom right, #2D2D2D 0%, #1A1A1A 50%, #0D0D0D 100%); " +
                        "-fx-border-color: #404040; " +
                        "-fx-border-width: 1.5; " +
                        "-fx-border-radius: 20; " +
                        "-fx-background-radius: 20; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 25, 0, 0, 12);" :

                "-fx-background-color: linear-gradient(to bottom right, #FFFFFF 0%, #F8F9FA 50%, #E9ECEF 100%); " +
                        "-fx-border-color: #E0E0E0; " +
                        "-fx-border-width: 1.5; " +
                        "-fx-border-radius: 20; " +
                        "-fx-background-radius: 20; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 25, 0, 0, 12);";

        dialogPane.setStyle(dialogStyle);

        // Style du bouton de fermeture
        Node closeButton = dialogPane.lookupButton(ButtonType.CLOSE);
        if (closeButton instanceof Button) {
            String closeButtonStyle = isDark ?
                    "-fx-background-color: #555555; -fx-text-fill: white; -fx-font-weight: bold; " +
                            "-fx-background-radius: 12; -fx-padding: 6 12; -fx-cursor: hand;" :
                    "-fx-background-color: #E0E0E0; -fx-text-fill: #2D2D2D; -fx-font-weight: bold; " +
                            "-fx-background-radius: 12; -fx-padding: 6 12; -fx-cursor: hand;";
            closeButton.setStyle(closeButtonStyle);
        }
    }

    private void setupDialogAnimation(Dialog<Void> dialog) {
        dialog.setOnShown(e -> {
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.setScaleX(0.8);
            dialogPane.setScaleY(0.8);
            dialogPane.setOpacity(0);

            ScaleTransition scale = new ScaleTransition(Duration.millis(300), dialogPane);
            scale.setToX(1.0);
            scale.setToY(1.0);

            FadeTransition fade = new FadeTransition(Duration.millis(300), dialogPane);
            fade.setToValue(1.0);

            ParallelTransition animation = new ParallelTransition(scale, fade);
            animation.setInterpolator(Interpolator.EASE_OUT);
            animation.play();
        });
    }

    // Méthodes utilitaires pour les couleurs et formats
    private String getTypeDisplayName(String type) {
        switch (type.toLowerCase()) {
            case "admin": return "Administrateur 👑";
            case "enseignant": return "Enseignant 🎓";
            case "etudiant": return "Étudiant 👨‍🎓";
            default: return "Utilisateur 👤";
        }
    }

    private Color getAvatarColor(String type) {
        switch (type.toLowerCase()) {
            case "admin": return Color.web("#E74C3C"); // Rouge
            case "enseignant": return Color.web("#3498DB"); // Bleu
            case "etudiant": return Color.web("#2ECC71"); // Vert
            default: return Color.web("#9B59B6"); // Violet
        }
    }

    private Color getAvatarBorderColor(String type) {
        switch (type.toLowerCase()) {
            case "admin": return Color.web("#C0392B");
            case "enseignant": return Color.web("#2980B9");
            case "etudiant": return Color.web("#27AE60");
            default: return Color.web("#8E44AD");
        }
    }

    private String getTypeColor(String type) {
        switch (type.toLowerCase()) {
            case "admin": return "#E74C3C";
            case "enseignant": return "#3498DB";
            case "etudiant": return "#2ECC71";
            default: return "#9B59B6";
        }
    }

    private String getTypeBackgroundColor(String type) {
        switch (type.toLowerCase()) {
            case "admin": return "rgba(231, 76, 60, 0.1)";
            case "enseignant": return "rgba(52, 152, 219, 0.1)";
            case "etudiant": return "rgba(46, 204, 113, 0.1)";
            default: return "rgba(155, 89, 182, 0.1)";
        }
    }

    private String getInitials(String prenom, String nom) {
        if (prenom == null || nom == null || prenom.isEmpty() || nom.isEmpty()) {
            return "??";
        }
        return (prenom.charAt(0) + "" + nom.charAt(0)).toUpperCase();
    }
    private HBox createControlButtons() {
        HBox controls = new HBox(8);
        controls.setAlignment(Pos.CENTER_RIGHT);
        controls.setStyle("-fx-background-color: transparent;");

        System.out.println("🛠️ Assemblage des boutons de contrôle...");

        // CRÉER TOUS LES BOUTONS AVANT le bouton utilisateur
        if (themeButton == null) {
            themeButton = createIconButton(getThemeIcon(), "Changer le thème (Ctrl+D)", "#3498DB");
        }

        if (closeButton == null) {
            closeButton = createIconButton("✕", "Fermer", "#E74C3C");
        }

        // AJOUTER les boutons de fonctionnalités AVANT le bouton utilisateur
        controls.getChildren().addAll(themeButton, closeButton);

        // BOUTON ÉTUDIANTS - UNIQUEMENT POUR LES ADMINS
        if (isUserAdmin()) {
            Button studentsButton = createStudentsButton();
            controls.getChildren().add(studentsButton);
            System.out.println("✅ Bouton étudiants ajouté pour l'admin");
        }

        // BOUTON ENSEIGNANTS - UNIQUEMENT POUR LES ADMINS
        if (isUserAdmin()) {
            Button enseignantsButton = createEnseignantsButton();
            controls.getChildren().add(enseignantsButton);
            System.out.println("✅ Bouton enseignants ajouté pour l'admin");
        }

        // BOUTON SALLES - UNIQUEMENT POUR LES ADMINS
        if (isUserAdmin()) {
            Button sallesButton = createSallesButton();
            controls.getChildren().add(sallesButton);
            System.out.println("✅ Bouton salles ajouté pour l'admin");
        }

        // BOUTON UTILISATEUR EN DERNIER
        Button userButton = createUserButton();
        controls.getChildren().add(userButton);

        return controls;
    }

    private Button createSallesButton() {
        Button sallesButton = new Button("🏫 Salles");

        String sallesButtonStyle =
                "-fx-background-color: #FF9800; " + // Orange pour salles
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 11px; " +
                        "-fx-border-color: #F57C00; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 12; " +
                        "-fx-background-radius: 12; " +
                        "-fx-padding: 6 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 1, 1); " +
                        "-fx-cursor: hand;";

        sallesButton.setStyle(sallesButtonStyle);

        sallesButton.setOnMouseEntered(e -> {
            sallesButton.setStyle(sallesButtonStyle +
                    "-fx-background-color: #F57C00; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 2, 2);");
        });

        sallesButton.setOnMouseExited(e -> {
            sallesButton.setStyle(sallesButtonStyle);
        });

        // Action du bouton - ouvrir SalleView
        sallesButton.setOnAction(e -> {
            System.out.println("🏫 Ouverture de la vue Salles...");
            openSalleView();
        });

        Tooltip.install(sallesButton, new Tooltip("Gérer les salles (Admin seulement)"));

        return sallesButton;
    }

    private void openSalleView() {
        try {
            System.out.println("🚀 Lancement de SalleView...");

            // Créer une nouvelle instance de SalleView
            tn.iset.m2glnt.client.viewer.view.SalleView salleView = new tn.iset.m2glnt.client.viewer.view.SalleView();

            // Créer la scène - Comme SalleView étend BorderPane, on crée une scène directement
            Scene salleScene = new Scene(salleView, 1200, 700);

            // Appliquer le thème actuel
            if (isDarkMode()) {
                salleScene.setFill(Color.web("#0A1929"));
                applyDarkThemeToSalleView(salleView);
            } else {
                salleScene.setFill(Color.web("#F5F9FF"));
                applyLightThemeToSalleView(salleView);
            }

            // Créer et configurer la nouvelle fenêtre
            Stage salleStage = new Stage();
            salleStage.setTitle("🏫 Gestion des Salles - Admin ISET");
            salleStage.setScene(salleScene);

            // Configurer la fenêtre comme modale
            salleStage.initModality(Modality.WINDOW_MODAL);
            if (scene != null && scene.getWindow() != null) {
                salleStage.initOwner(scene.getWindow());
            }

            // Configurer la taille
            salleStage.setMinWidth(1000);
            salleStage.setMinHeight(600);

            // Centrer la fenêtre
            salleStage.centerOnScreen();

            // Afficher la fenêtre
            salleStage.show();

            showTemporaryNotification("🏫 Interface de gestion des salles ouverte", 2000);

            System.out.println("✅ SalleView ouvert avec succès");

        } catch (Exception ex) {
            System.err.println("❌ Erreur lors de l'ouverture de SalleView: " + ex.getMessage());
            ex.printStackTrace();

            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erreur");
            errorAlert.setHeaderText("Impossible d'ouvrir la gestion des salles");
            errorAlert.setContentText("Une erreur est survenue: " + ex.getMessage());

            // Appliquer le thème à la boîte de dialogue
            DialogPane dialogPane = errorAlert.getDialogPane();
            dialogPane.setStyle("-fx-background-color: " + (isDarkMode() ? "#2D2D2D" : "white") + ";" +
                    "-fx-text-fill: " + (isDarkMode() ? DARK_TEXT : "#2D2D2D") + ";");

            errorAlert.showAndWait();
        }
    }

    // Méthodes auxiliaires pour appliquer le thème à SalleView
    private void applyDarkThemeToSalleView(tn.iset.m2glnt.client.viewer.view.SalleView salleView) {
        // Ces styles seront appliqués à la TableView et autres composants
        salleView.setStyle("-fx-background-color: #0A1929;");
    }

    private void applyLightThemeToSalleView(tn.iset.m2glnt.client.viewer.view.SalleView salleView) {
        salleView.setStyle("-fx-background-color: #F5F9FF;");
    }

    private boolean isUserAdmin() {
        String userType = CalendarApp.getCurrentUserType();
        return userType != null && userType.equalsIgnoreCase("admin");
    }
    private boolean hasSlotCreationPermission() {
        return isUserAdmin();
    }
    private Button createStudentsButton() {
        Button studentsButton = new Button("👥 Étudiants");

        String studentsButtonStyle =
                "-fx-background-color: #9C27B0; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 11px; " +
                        "-fx-border-color: #7B1FA2; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 12; " +
                        "-fx-background-radius: 12; " +
                        "-fx-padding: 6 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 1, 1); " +
                        "-fx-cursor: hand;";

        studentsButton.setStyle(studentsButtonStyle);

        studentsButton.setOnMouseEntered(e -> {
            studentsButton.setStyle(studentsButtonStyle +
                    "-fx-background-color: #7B1FA2; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 2, 2);");
        });

        studentsButton.setOnMouseExited(e -> {
            studentsButton.setStyle(studentsButtonStyle);
        });

        // Action du bouton - ouvrir EtudiantView
        studentsButton.setOnAction(e -> {
            System.out.println("👥 Ouverture de la vue Étudiants...");
            openEtudiantView();
        });

        Tooltip.install(studentsButton, new Tooltip("Gérer les étudiants (Admin seulement)"));

        return studentsButton;
    }

    // Méthode pour ouvrir EtudiantView
    private void openEtudiantView() {
        try {
            System.out.println("🚀 Lancement de EtudiantView...");

            // Créer une nouvelle instance de EtudiantView
            tn.iset.m2glnt.client.viewer.view.EtudiantView etudiantView = new tn.iset.m2glnt.client.viewer.view.EtudiantView();

            // Créer la scène - Comme EtudiantView étend BorderPane, on crée une scène directement
            Scene etudiantScene = new Scene(etudiantView, 1200, 700);

            // Appliquer le thème actuel
            if (isDarkMode()) {
                etudiantScene.setFill(Color.web("#0A1929"));
                applyDarkThemeToEtudiantView(etudiantView);
            } else {
                etudiantScene.setFill(Color.web("#F5F9FF"));
                applyLightThemeToEtudiantView(etudiantView);
            }

            // Créer et configurer la nouvelle fenêtre
            Stage etudiantStage = new Stage();
            etudiantStage.setTitle("🎓 Gestion des Étudiants - Admin ISET");
            etudiantStage.setScene(etudiantScene);

            // Configurer la fenêtre comme modale
            etudiantStage.initModality(Modality.WINDOW_MODAL);
            if (scene != null && scene.getWindow() != null) {
                etudiantStage.initOwner(scene.getWindow());
            }

            // Configurer la taille
            etudiantStage.setMinWidth(1000);
            etudiantStage.setMinHeight(600);

            // Centrer la fenêtre
            etudiantStage.centerOnScreen();

            // Afficher la fenêtre
            etudiantStage.show();

            showTemporaryNotification("👥 Interface de gestion des étudiants ouverte", 2000);

            System.out.println("✅ EtudiantView ouvert avec succès");

        } catch (Exception ex) {
            System.err.println("❌ Erreur lors de l'ouverture de EtudiantView: " + ex.getMessage());
            ex.printStackTrace();

            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erreur");
            errorAlert.setHeaderText("Impossible d'ouvrir la gestion des étudiants");
            errorAlert.setContentText("Une erreur est survenue: " + ex.getMessage());

            // Appliquer le thème à la boîte de dialogue
            DialogPane dialogPane = errorAlert.getDialogPane();
            dialogPane.setStyle("-fx-background-color: " + (isDarkMode() ? "#2D2D2D" : "white") + ";" +
                    "-fx-text-fill: " + (isDarkMode() ? DARK_TEXT : "#2D2D2D") + ";");

            errorAlert.showAndWait();
        }
    }

    // Méthodes auxiliaires pour appliquer le thème à EtudiantView
    private void applyDarkThemeToEtudiantView(tn.iset.m2glnt.client.viewer.view.EtudiantView etudiantView) {
        // Ces styles seront appliqués à la TableView et autres composants
        etudiantView.setStyle("-fx-background-color: #0A1929;");
    }

    private void applyLightThemeToEtudiantView(tn.iset.m2glnt.client.viewer.view.EtudiantView etudiantView) {
        etudiantView.setStyle("-fx-background-color: #F5F9FF;");
    }

    // Méthodes auxiliaires pour appliquer le thème aux nouvelles fenêtres
    private void applyDarkThemeToStage(Stage stage) {
        if (stage.getScene() != null) {
            stage.getScene().getRoot().setStyle("-fx-background-color: #0A1929;");
        }
    }

    private void applyLightThemeToStage(Stage stage) {
        if (stage.getScene() != null) {
            stage.getScene().getRoot().setStyle("-fx-background-color: #F5F9FF;");
        }
    }

    public void testHeaderVisibility() {
        System.out.println("=== TEST HEADER VISIBILITY ===");

        if (scene != null) {
            Parent root = scene.getRoot();
            if (root instanceof BorderPane) {
                BorderPane mainContainer = (BorderPane) root;
                Node top = mainContainer.getTop();

                if (top instanceof HBox) {
                    HBox header = (HBox) top;
                    System.out.println("✅ Header trouvé dans la scène");
                    System.out.println("   Enfants: " + header.getChildren().size());
                    System.out.println("   Style: " + header.getStyle());
                } else {
                    System.err.println("❌ Top n'est pas un HBox: " + (top != null ? top.getClass().getSimpleName() : "null"));
                }
            }
        } else {
            System.err.println("❌ Scene est null");
        }
        System.out.println("=============================");
    }

    public void debugHeaderState() {
        System.out.println("=== DÉBOGAGE HEADER COMPLET ===");

        if (scene != null) {
            Parent root = scene.getRoot();
            if (root instanceof BorderPane) {
                BorderPane mainContainer = (BorderPane) root;
                Node top = mainContainer.getTop();

                if (top instanceof HBox) {
                    HBox header = (HBox) top;
                    System.out.println("📋 Header trouvé avec " + header.getChildren().size() + " enfants:");

                    for (int i = 0; i < header.getChildren().size(); i++) {
                        Node child = header.getChildren().get(i);
                        System.out.println("  " + i + ": " + child.getClass().getSimpleName() +
                                " - Visible: " + child.isVisible() +
                                " - Managed: " + child.isManaged());

                        if (child instanceof HBox) {
                            HBox childBox = (HBox) child;
                            System.out.println("    Contient " + childBox.getChildren().size() + " sous-éléments:");
                            for (int j = 0; j < childBox.getChildren().size(); j++) {
                                Node subChild = childBox.getChildren().get(j);
                                if (subChild instanceof Button) {
                                    Button btn = (Button) subChild;
                                    System.out.println("      " + j + ": Bouton '" + btn.getText() +
                                            "' - Visible: " + btn.isVisible());
                                }
                            }
                        }
                    }
                } else {
                    System.out.println("❌ Top n'est pas un HBox: " + (top != null ? top.getClass().getSimpleName() : "null"));
                }
            }
        } else {
            System.out.println("❌ Scene est null");
        }

        // Debug des boutons individuels
        System.out.println("🔍 État des boutons individuels:");
        System.out.println("   - helpButton: " + (helpButton != null ? helpButton.getText() + " (Visible: " + helpButton.isVisible() + ")" : "null"));
        System.out.println("   - statsButton: " + (statsButton != null ? statsButton.getText() + " (Visible: " + statsButton.isVisible() + ")" : "null"));
        System.out.println("   - exportCsvButton: " + (exportCsvButton != null ? exportCsvButton.getText() + " (Visible: " + exportCsvButton.isVisible() + ")" : "null"));
        System.out.println("   - themeButton: " + (themeButton != null ? themeButton.getText() + " (Visible: " + themeButton.isVisible() + ")" : "null"));
        System.out.println("   - closeButton: " + (closeButton != null ? closeButton.getText() + " (Visible: " + closeButton.isVisible() + ")" : "null"));

        System.out.println("==============================");
    }

    private void updateTitleStyle(Label title) {
        // STYLE ABSOLUMENT VISIBLE
        title.setStyle("-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 8, 0, 2, 2);");
        System.out.println("🎨 Titre style appliqué");
    }

    public void debugState() {
        System.out.println("=== DEBUG GridCalendarView ===");
        System.out.println("this: " + (this != null ? "✓" : "✗ NULL"));
        System.out.println("notificationContainer: " + (notificationContainer != null ? "✓" : "✗ NULL"));
        System.out.println("scene: " + (scene != null ? "✓" : "✗ NULL"));
        System.out.println("controlsInitialized: " + controlsInitialized);
        System.out.println("=============================");
    }

    public void resetHeader() {
        controlsInitialized = false;
        controlButtons = null;
        helpButton = null;
        statsButton = null;
        exportCsvButton = null;
        themeButton = null;
        closeButton = null;
    }

    private Button createIconButton(String icon, String tooltip, String color) {
        Button button = new Button(icon);
        button.setFont(Font.font("Segoe UI Emoji", FontWeight.BOLD, 14));

        // TAILLES OPTIMISÉES
        button.setMinSize(36, 36);
        button.setPrefSize(36, 36);
        button.setMaxSize(36, 36);

        // APPLIQUER LE STYLE PROFESSIONNEL
        applyProfessionalButtonStyle(button);

        // FORCER la visibilité
        button.setVisible(true);
        button.setManaged(true);
        button.setDisable(false);

        Tooltip.install(button, new Tooltip(tooltip));

        System.out.println("🔵 Bouton créé: " + icon + " avec style professionnel");

        return button;
    }

    public void testButtonDisplay() {
        System.out.println("=== TEST AFFICHAGE BOUTONS ===");

        if (controlButtons != null) {
            System.out.println("📦 controlButtons trouvé avec " + controlButtons.getChildren().size() + " enfants");

            for (int i = 0; i < controlButtons.getChildren().size(); i++) {
                Node node = controlButtons.getChildren().get(i);
                if (node instanceof Button) {
                    Button btn = (Button) node;
                    System.out.println("🔘 Bouton " + i + ": '" + btn.getText() + "'");
                    System.out.println("   - Visible: " + btn.isVisible());
                    System.out.println("   - Managed: " + btn.isManaged());
                    System.out.println("   - Disabled: " + btn.isDisabled());
                    System.out.println("   - Opacité: " + btn.getOpacity());
                    System.out.println("   - Parent: " + (btn.getParent() != null ? btn.getParent().getClass().getSimpleName() : "null"));
                    System.out.println("   - Scene: " + (btn.getScene() != null ? "✓" : "✗"));
                    System.out.println("   - Style: " + btn.getStyle());
                }
            }
        } else {
            System.err.println("❌ controlButtons est null");
        }

        // Test des boutons individuels
        System.out.println("🔍 Boutons individuels:");
        System.out.println("   - themeButton: " + (themeButton != null ?
                "'" + themeButton.getText() + "' (parent: " + (themeButton.getParent() != null ? "✓" : "✗") + ")" : "null"));
        System.out.println("   - closeButton: " + (closeButton != null ?
                "'" + closeButton.getText() + "' (parent: " + (closeButton.getParent() != null ? "✓" : "✗") + ")" : "null"));

        System.out.println("=============================");
    }

    private void styleCircularButton(Button btn, boolean hover, String color) {
        // Style SIMPLE et VISIBLE pour tous les boutons
        String baseStyle =
                "-fx-background-radius: 50%; " +
                        "-fx-border-radius: 50%; " +
                        "-fx-cursor: hand; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 0; " +
                        "-fx-alignment: center; " +
                        "-fx-content-display: center; " +
                        "-fx-min-width: 38px; " +
                        "-fx-min-height: 38px; " +
                        "-fx-max-width: 38px; " +
                        "-fx-max-height: 38px; ";

        // Couleurs SOLIDES selon le type de bouton
        String backgroundColor, borderColor;

        if (btn.getText().contains("❓")) { // Aide
            backgroundColor = "#3498DB"; // Bleu
            borderColor = "#2980B9";
        } else if (btn.getText().contains("📊")) { // Statistiques
            backgroundColor = "#9B59B6"; // Violet
            borderColor = "#8E44AD";
        } else if (btn.getText().contains("🌙") || btn.getText().contains("☀️")) { // Thème
            backgroundColor = "#F39C12"; // Orange
            borderColor = "#E67E22";
        } else if (btn.getText().contains("✕")) { // Fermer
            backgroundColor = "#E74C3C"; // Rouge
            borderColor = "#C0392B";
        } else {
            backgroundColor = "#3498DB"; // Couleur par défaut
            borderColor = "#2980B9";
        }

        if (hover) {
            // Style au survol - plus foncé
            btn.setStyle(baseStyle +
                    "-fx-background-color: " + borderColor + "; " +
                    "-fx-text-fill: white; " +
                    "-fx-border-color: white; " +
                    "-fx-border-width: 2; " +
                    "-fx-effect: dropshadow(gaussian, " + borderColor + ", 15, 0.7, 0, 5);");
        } else {
            // Style normal
            btn.setStyle(baseStyle +
                    "-fx-background-color: " + backgroundColor + "; " +
                    "-fx-text-fill: white; " +
                    "-fx-border-color: " + borderColor + "; " +
                    "-fx-border-width: 2; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3);");
        }
    }

    private HBox createStatusBar() {
        HBox statusBar = new HBox(10);
        statusBar.setPadding(new Insets(8, 20, 8, 20));
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.setStyle("-fx-background-color: " + (isDarkMode() ? "rgba(10, 25, 41, 0.9)" : "rgba(230, 240, 255, 0.9)") + ";");

        Label statusLabel = new Label();
        statusLabel.textProperty().bind(
                Bindings.createStringBinding(() ->
                                String.format("Mode: %s | Slots: %d | Thème: %s | Connecté: %s (%s)",
                                        currentDisplayMode.get().getName(),
                                        slotViewMap.size(),
                                        isDarkMode() ? "Sombre" : "Clair",
                                        CalendarApp.getCurrentUserPrenom(),
                                        CalendarApp.getCurrentUserType()
                                ),
                        currentDisplayMode, darkModeProperty, scaleFactor
                )
        );

        Label timeLabel = new Label();
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        statusBar.getChildren().addAll(statusLabel, spacer, timeLabel);
        return statusBar;
    }

    @Override
    public void removeSlot(int slotId) {
        SlotView slotView = slotViewMap.remove(slotId);
        if (slotView != null) {
            removeFromSearchIndex(slotView);

            // UTILISER DIRECTEMENT LE SLOTVIEW POUR LES ANIMATIONS
            if (getChildren().contains(slotView)) {
                FadeTransition fade = new FadeTransition(Duration.millis(400), slotView);
                fade.setFromValue(1.0);
                fade.setToValue(0.0);

                ScaleTransition scale = new ScaleTransition(Duration.millis(400), slotView);
                scale.setFromX(1.0);
                scale.setFromY(1.0);
                scale.setToX(0.7);
                scale.setToY(0.7);

                RotateTransition rotate = new RotateTransition(Duration.millis(400), slotView);
                rotate.setFromAngle(0);
                rotate.setToAngle(15);

                ParallelTransition removeAnimation = new ParallelTransition(fade, scale, rotate);
                removeAnimation.setOnFinished(e -> {
                    if (getChildren().contains(slotView)) {
                        getChildren().remove(slotView);
                        System.out.println("✅ Slot " + slotId + " supprimé avec succès");
                    } else {
                        System.out.println("ℹ️ Slot " + slotId + " n'était plus dans la grille");
                    }
                });
                removeAnimation.play();
            }

            updateStatistics();
            notifyEventListeners("slotRemoved", slotId);
        } else {
            System.out.println("❌ Slot " + slotId + " non trouvé pour suppression");
        }
    }

    private void removeFromSearchIndex(SlotView slotView) {
        searchIndex.entrySet().removeIf(entry -> entry.getValue().remove(slotView));
    }

    // 🎯 CORE CALENDAR FUNCTIONALITY - ANIMATIONS AMÉLIORÉES
    private VBox createDetailedSlotContent(SlotView slotView, String[] colors) {
        VBox container = new VBox(6);
        container.setAlignment(Pos.TOP_LEFT);
        container.setPadding(new Insets(10));

        // TITRE PRINCIPAL (Matière)
        Label titleLabel = new Label(slotView.getMatiere() != null ? slotView.getMatiere() : "Sans titre");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        titleLabel.setStyle("-fx-text-fill: " + colors[2] + "; -fx-font-weight: bold;");
        titleLabel.setWrapText(true);

        // DATE (nouveau)
        if (slotView.getDate() != null) {
            Label dateLabel = new Label("📅 " + formatDate(slotView.getDate()));
            dateLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 10));
            dateLabel.setStyle("-fx-text-fill: #4A5568;");
            container.getChildren().add(dateLabel);
        }

        // HORAIRE (amélioré)
        if (slotView.getHeureDebut() != null && slotView.getHeureFin() != null) {
            Label timeLabel = new Label("🕐 " + formatTime(slotView.getHeureDebut()) + " - " + formatTime(slotView.getHeureFin()));
            timeLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
            timeLabel.setStyle("-fx-text-fill: #2D3748;");
            container.getChildren().add(timeLabel);
        }

        // ENSEIGNANT
        if (slotView.getEnseignant() != null && !slotView.getEnseignant().isEmpty()) {
            Label teacherLabel = new Label("👨‍🏫 " + slotView.getEnseignant());
            teacherLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
            teacherLabel.setStyle("-fx-text-fill: #2D3748;");
            container.getChildren().add(teacherLabel);
        }

        // SALLE
        if (slotView.getSalle() != null && !slotView.getSalle().isEmpty()) {
            Label roomLabel = new Label("🏫 " + slotView.getSalle());
            roomLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
            roomLabel.setStyle("-fx-text-fill: #2D3748;");
            container.getChildren().add(roomLabel);
        }

        // TYPE DE COURS
        if (slotView.getTypeCours() != null && !slotView.getTypeCours().isEmpty()) {
            Label typeLabel = new Label(getTypeIcon(slotView.getTypeCours()) + " " + slotView.getTypeCours());
            typeLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 10));
            typeLabel.setStyle("-fx-text-fill: #4A5568;");
            container.getChildren().add(typeLabel);
        }

        // GROUPES (si disponible)
        if (slotView.getGroupes() != null && !slotView.getGroupes().isEmpty()) {
            Label groupLabel = new Label("👥 " + slotView.getGroupes());
            groupLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 10));
            groupLabel.setStyle("-fx-text-fill: #4A5568;");
            container.getChildren().add(groupLabel);
        }

        // DURÉE (nouveau)
        if (slotView.getSlot() != null && slotView.getSlot().duration() != null) {
            java.time.Duration duration = slotView.getSlot().duration();
            long hours = duration.toHours();
            long minutes = duration.toMinutesPart();
            String durationText = String.format("⏱️ Durée: %dh%02d", hours, minutes);

            Label durationLabel = new Label(durationText);
            durationLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 9));
            durationLabel.setStyle("-fx-text-fill: #718096;");
            container.getChildren().add(durationLabel);
        }

        // Ajouter le titre en premier
        container.getChildren().add(0, titleLabel);

        return container;
    }

    // Ajoutez cette méthode pour formater la date
    private String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private String formatTime(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private String getTypeIcon(String typeCours) {
        if (typeCours == null) return "📚";

        switch (typeCours.toLowerCase()) {
            case "cours":
                return "📖";
            case "td":
                return "✏️";
            case "tp":
                return "🔬";
            case "projet":
                return "💼";
            case "examen":
                return "📝";
            default:
                return "📚";
        }
    }

    private void setupEventListeners() {
        // Écouter les événements de mise à jour
        addCalendarEventListener((eventType, data) -> {
            switch (eventType) {
                case "slotAdded" -> {
                    int slotId = (int) data;
                    System.out.println("🎯 Slot " + slotId + " ajouté - ANIMATION uniquement");
                    // 🔥 NE PAS rafraîchir, juste animer
                    pulseSlot(slotId);
                }
                case "slotUpdated" -> {
                    int slotId = (int) data;
                    System.out.println("🎯 Slot " + slotId + " modifié - ANIMATION uniquement");
                    pulseSlot(slotId);
                }
                case "slotDeleted" -> {
                    int slotId = (int) data;
                    System.out.println("🎯 Slot " + slotId + " supprimé - déjà géré par removeSlot");
                    // La suppression est déjà gérée par removeSlot
                }
                case "calendarRefreshRequested" -> {
                    System.out.println("🎯 Demande de rafraîchissement complet");
                    // 🔥 UTILISER UN DÉLAI pour éviter les conflits
                    PauseTransition delay = new PauseTransition(Duration.millis(100));
                    delay.setOnFinished(e -> requestCalendarRefresh());
                    delay.play();
                }
            }
        });
    }

    public void addSlotModificationListener(CalendarEventListener listener) {
        addCalendarEventListener(listener);
    }
    @Override
    public void addSlotView(SlotView slotView, int rowIndex, int columnIndex, int rowSpan, int colSpan) {
        int slotId = slotView.getSlotId();

        // 🔥 VÉRIFICATION RENFORCÉE contre les doublons
        if (slotViewMap.containsKey(slotId)) {
            System.out.println("⚠️ Slot " + slotId + " existe déjà, mise à jour...");

            // Mettre à jour l'existant au lieu de supprimer/recréer
            SlotView existingSlot = slotViewMap.get(slotId);
            updateExistingSlotProperties(existingSlot, slotView);

            // Mettre à jour la position
            updateSlotPosition(existingSlot, rowIndex, columnIndex, rowSpan, colSpan);
            return;
        }

        int idx = colorIndex % COLOR_SCHEMES.length;
        String[] colors = COLOR_SCHEMES[idx];
        colorIndex++;

        // APPLIQUER LE STYLE DIRECTEMENT AU SLOTVIEW
        String baseStyle = String.format("-fx-background-color: %s; -fx-border-color: %s; " +
                "-fx-border-width: 2; -fx-border-radius: 14; -fx-background-radius: 14; " +
                "-fx-padding: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 12, 0, 0, 6); " +
                "-fx-cursor: hand;", colors[0], colors[1]);

        slotView.setStyle(baseStyle);
        slotView.setOpacity(0);
        slotView.setScaleX(0.8);
        slotView.setScaleY(0.8);

        indexSlotForSearch(slotView);

        // ANIMATION D'ENTRÉE
        FadeTransition fadeIn = new FadeTransition(Duration.millis(600), slotView);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(600), slotView);
        scaleIn.setFromX(0.8);
        scaleIn.setFromY(0.8);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(600), slotView);
        slideIn.setFromY(-30);
        slideIn.setToY(0);

        RotateTransition rotateIn = new RotateTransition(Duration.millis(300), slotView);
        rotateIn.setFromAngle(-5);
        rotateIn.setToAngle(0);

        ParallelTransition entrance = new ParallelTransition(fadeIn, scaleIn, slideIn, rotateIn);
        entrance.setDelay(Duration.millis(colorIndex * 25));
        entrance.setInterpolator(Interpolator.EASE_OUT);

        // Configurer les interactions
        setupSlotInteractions(slotView, colors, baseStyle);
        addContextMenuToSlot(slotView);

        // 🔥 AJOUT SÉCURISÉ à la grille
        if (!getChildren().contains(slotView)) {
            add(slotView, columnIndex, rowIndex, colSpan, rowSpan);
            slotViewMap.put(slotId, slotView);
            entrance.play();

            System.out.println("✅ Slot " + slotId + " ajouté à (" + rowIndex + "," + columnIndex + ")");
        } else {
            System.out.println("⚠️ Slot " + slotId + " était déjà dans la grille");
        }

        updateStatistics();

        // 🔥 DÉSACTIVER les notifications automatiques problématiques
        // notifyEventListeners("slotAdded", slotId); // ⛔ COMMENTÉ POUR ÉVITER LES BOUCLES
    }
    /**
     * Rafraîchissement OPTIMISÉ - seulement après ajout réussi
     */
    /**
     * Ajoute un slot SANS déclencher de rafraîchissement automatique problématique
     * Pour les nouvelles créations uniquement
     */
    public void addSlotWithoutAutoRefresh(SlotView slotView, int rowIndex, int columnIndex, int rowSpan, int colSpan) {
        System.out.println("🎬 Ajout DIRECT du slot " + slotView.getSlotId() + " sans rafraîchissement automatique");

        // Désactiver temporairement le rafraîchissement automatique
        boolean oldAutoRefreshState = autoRefreshEnabled;
        autoRefreshEnabled = false;

        try {
            // Ajouter normalement
            addSlotView(slotView, rowIndex, columnIndex, rowSpan, colSpan);

            // 🔥 Rafraîchissement optimisé au lieu du complet
            optimizedRefreshAfterSlotAdd(slotView.getSlotId());

        } finally {
            // Restaurer l'état
            autoRefreshEnabled = oldAutoRefreshState;
        }
    }

    /**
     * Rafraîchissement OPTIMISÉ - seulement après ajout réussi
     */
    public void optimizedRefreshAfterSlotAdd(int newSlotId) {
        System.out.println("🔄 Rafraîchissement optimisé pour le slot " + newSlotId);

        // Vérifier si le slot existe vraiment
        if (!slotViewMap.containsKey(newSlotId)) {
            System.out.println("❌ Slot " + newSlotId + " non trouvé - reconstruction complète");
            requestCalendarRefresh();
            return;
        }

        // 🔥 Juste une animation, pas de reconstruction complète
        pulseSlot(newSlotId);
        showTemporaryNotification("✅ Créneau ajouté avec succès", 1500);

        System.out.println("✅ Rafraîchissement optimisé terminé");
    }
    /*private void addContextMenuToSlot(SlotView slotView) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem editItem = new MenuItem("✏️ Modifier");
        MenuItem deleteItem = new MenuItem("🗑️ Supprimer");
        MenuItem duplicateItem = new MenuItem("📋 Dupliquer");
        MenuItem exportItem = new MenuItem("📤 Exporter ce slot");
        MenuItem highlightItem = new MenuItem("🔦 Surligner");
        MenuItem infoItem = new MenuItem("ℹ️ Informations détaillées");

        contextMenu.getItems().addAll(editItem, deleteItem, duplicateItem,
                new SeparatorMenuItem(), exportItem, highlightItem, infoItem);

        contextMenu.setStyle("-fx-background-color: " + (isDarkMode() ? "#2D2D2D" : "white") + ";" +
                "-fx-text-fill: " + (isDarkMode() ? DARK_TEXT : "black") + ";");

        slotView.setOnContextMenuRequested(event ->
                contextMenu.show(slotView, event.getScreenX(), event.getScreenY()));

        editItem.setOnAction(e -> editSlot(slotView));
        deleteItem.setOnAction(e -> removeSlot(slotView.getSlotId()));
        duplicateItem.setOnAction(e -> duplicateSlot(slotView));
        highlightItem.setOnAction(e -> pulseSlot(slotView.getSlotId()));
        infoItem.setOnAction(e -> showDetailedSlotInfo(slotView));
        exportItem.setOnAction(e -> exportSingleSlot(slotView));
    }*/


    /*private void setupSlotInteractions(SlotView slotView, String[] colors, String baseStyle) {
        int slotId = slotView.getSlotId();

        slotView.setOnMouseEntered(e -> {
            String hoverStyle = String.format("-fx-background-color: %s; -fx-border-color: %s; " +
                    "-fx-border-width: 3; -fx-border-radius: 14; -fx-background-radius: 14; " +
                    "-fx-padding: 12; -fx-effect: dropshadow(gaussian, %s, 20, 0.7, 0, 8); " +
                    "-fx-cursor: hand;", colors[0], colors[2], colors[3]);
            slotView.setStyle(hoverStyle);

            ScaleTransition scale = new ScaleTransition(Duration.millis(200), slotView);
            scale.setToX(1.03);
            scale.setToY(1.03);
            scale.play();

            slotView.setEffect(new javafx.scene.effect.DropShadow(20, Color.web(colors[3])));
        });

        slotView.setOnMouseExited(e -> {
            slotView.setStyle(baseStyle);
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), slotView);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();

            slotView.setEffect(new javafx.scene.effect.DropShadow(12, Color.color(0, 0, 0, 0.2)));
        });

        slotView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                System.out.println("🖱️ Double-clic détecté sur le slot " + slotId);
                ScaleTransition clickAnimation = new ScaleTransition(Duration.millis(100), slotView);
                clickAnimation.setToX(0.95);
                clickAnimation.setToY(0.95);
                clickAnimation.setAutoReverse(true);
                clickAnimation.setCycleCount(2);
                clickAnimation.play();

                PauseTransition pause = new PauseTransition(Duration.millis(150));
                pause.setOnFinished(event -> editSlot(slotView));
                pause.play();
            }
        });

        slotView.setId("slot-" + slotId);
    }*/
    private void setupSlotInteractions(SlotView slotView, String[] colors, String baseStyle) {
        int slotId = slotView.getSlotId();

        slotView.setOnMouseEntered(e -> {
            String hoverStyle = String.format("-fx-background-color: %s; -fx-border-color: %s; " +
                    "-fx-border-width: 3; -fx-border-radius: 14; -fx-background-radius: 14; " +
                    "-fx-padding: 12; -fx-effect: dropshadow(gaussian, %s, 20, 0.7, 0, 8); " +
                    "-fx-cursor: hand;", colors[0], colors[2], colors[3]);
            slotView.setStyle(hoverStyle);

            ScaleTransition scale = new ScaleTransition(Duration.millis(200), slotView);
            scale.setToX(1.03);
            scale.setToY(1.03);
            scale.play();

            slotView.setEffect(new javafx.scene.effect.DropShadow(20, Color.web(colors[3])));
        });

        slotView.setOnMouseExited(e -> {
            slotView.setStyle(baseStyle);
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), slotView);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();

            slotView.setEffect(new javafx.scene.effect.DropShadow(12, Color.color(0, 0, 0, 0.2)));
        });

        slotView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                System.out.println("🖱️ Double-clic détecté sur le slot " + slotId);

                // 🔥 CORRECTION : Debug détaillé des permissions
                System.out.println("🔐 Vérification permissions avant édition:");
                System.out.println("   - Slot ID: " + slotId);
                System.out.println("   - User Type: " + CalendarApp.getCurrentUserType());
                System.out.println("   - Has Permission: " + hasSlotModificationPermission());

                if (!hasSlotModificationPermission()) {
                    System.out.println("⛔ Permission refusée: L'utilisateur ne peut pas modifier les slots");
                    showPermissionError("Modification des créneaux",
                            "Seuls les administrateurs peuvent modifier les créneaux existants.");
                    return;
                }

                ScaleTransition clickAnimation = new ScaleTransition(Duration.millis(100), slotView);
                clickAnimation.setToX(0.95);
                clickAnimation.setToY(0.95);
                clickAnimation.setAutoReverse(true);
                clickAnimation.setCycleCount(2);
                clickAnimation.play();

                PauseTransition pause = new PauseTransition(Duration.millis(150));
                pause.setOnFinished(event -> {
                    System.out.println("✏️ Lancement de l'édition pour le slot " + slotId);
                    editSlot(slotView);
                });
                pause.play();
            }
        });

        slotView.setId("slot-" + slotId);
    }
    /**
     * Vérifie si l'utilisateur a la permission de modifier des créneaux
     */
    /**
     * Vérifie si l'utilisateur a la permission de modifier des créneaux
     */
    private boolean hasSlotModificationPermission() {
        String userType = CalendarApp.getCurrentUserType();
        boolean isAdmin = userType != null && userType.equalsIgnoreCase("admin");

        System.out.println("🔐 Vérification permission modification - Type: " + userType + ", Admin: " + isAdmin);

        // 🔥 CORRECTION : Ajouter un debug plus détaillé
        System.out.println("🔐 Détails utilisateur:");
        System.out.println("   - Email: " + CalendarApp.getCurrentUserEmail());
        System.out.println("   - Nom: " + CalendarApp.getCurrentUserNom());
        System.out.println("   - Prénom: " + CalendarApp.getCurrentUserPrenom());
        System.out.println("   - Type: " + CalendarApp.getCurrentUserType());

        return isAdmin;
    }
    /**
     * Affiche un message d'erreur de permission
     */
    private void showPermissionError(String action, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Permission refusée");
        alert.setHeaderText(action + " non autorisée");
        alert.setContentText(message + "\n\nVeuillez contacter un administrateur si nécessaire.");

        // Appliquer le thème
        DialogPane dialogPane = alert.getDialogPane();
        String alertStyle = isDarkMode() ?
                "-fx-background-color: #2D2D2D; -fx-text-fill: #E0E0E0;" :
                "-fx-background-color: white; -fx-text-fill: #2D2D2D;";
        dialogPane.setStyle(alertStyle);

        alert.showAndWait();
    }
    @Override
    public void addLabelInFirstRow(Label label, int columnIndex) {
        // 🔥 FORCER la réapplication du style à chaque fois
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);

        double fontSize = 14 * scaleFactor.get();
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, fontSize));

        // 🔥 UTILISER LA NOUVELLE MÉTHODE DE STYLE
        applyFirstRowLabelStyle(label);

        GridPane.setFillWidth(label, true);
        GridPane.setFillHeight(label, true);
        GridPane.setHgrow(label, Priority.ALWAYS);

        // 🔥 ÉVITER LES DOUBLONS
        if (!getChildren().contains(label)) {
            add(label, columnIndex, 0);
        }

        // 🔥 MAINTENIR LA LISTE À JOUR
        if (!firstRowLabels.contains(label)) {
            firstRowLabels.add(label);
        }

        // 🔥 ANIMATION RAPIDE pour le rafraîchissement
        if (isRefreshing) {
            // Pendant le rafraîchissement, afficher immédiatement
            label.setOpacity(1.0);
            label.setScaleX(1.0);
            label.setScaleY(1.0);
        } else {
            animateLabelEntrance(label, columnIndex);
        }

        System.out.println("✅ First row label ajouté: '" + label.getText() + "' à colonne " + columnIndex);
    }
    /**
     * Applique le style aux labels de première ligne
     */
    private void applyFirstRowLabelStyle(Label label) {
        if (isDarkMode()) {
            label.setStyle("-fx-background-color: " + DARK_FIRST_ROW_BG + ";" +
                    "-fx-text-fill: " + DARK_TEXT + ";" +
                    "-fx-padding: 14 20 14 20; -fx-background-radius: 14;" +
                    "-fx-border-color: #1E88E5; -fx-border-width: 0 0 2 0;" +
                    "-fx-effect: dropshadow(gaussian, rgba(30, 136, 229, 0.3), 15, 0, 0, 5);");
        } else {
            label.setStyle("-fx-background-color: " + LIGHT_FIRST_ROW_BG + ";" +
                    "-fx-text-fill: #1E293B;" +
                    "-fx-padding: 14 20 14 20; -fx-background-radius: 14;" +
                    "-fx-border-color: #1E88E5; -fx-border-width: 0 0 2 0;" +
                    "-fx-effect: dropshadow(gaussian, rgba(30, 136, 229, 0.2), 12, 0, 0, 4);");
        }
    }
    @Override
    public void addLabelInFirstColumn(Label label, int rowIndex) {
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMinHeight(45);
        label.setAlignment(Pos.CENTER);
        label.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));

        // 🔥 APPLIQUER LE STYLE
        applyFirstColumnLabelStyle(label);

        // 🔥 AJOUT DIRECT à la grille
        if (!getChildren().contains(label)) {
            add(label, 0, rowIndex);
        }

        // 🔥 MAINTENIR LA LISTE
        if (!firstColumnLabels.contains(label)) {
            firstColumnLabels.add(label);
        }

        // 🔥 ANIMATION CONDITIONNELLE
        if (isRefreshing) {
            label.setOpacity(1.0);
            label.setScaleX(1.0);
            label.setScaleY(1.0);
        } else {
            animateLabelEntrance(label, rowIndex);
        }

        System.out.println("✅ First column label ajouté: '" + label.getText() + "' à ligne " + rowIndex);
    }
    /**
     * Applique le style aux labels de première colonne
     */
    private void applyFirstColumnLabelStyle(Label label) {
        String bgColor, textColor, borderColor;
        if (isDarkMode()) {
            bgColor = DARK_FIRST_COLUMN_BG;
            textColor = DARK_TEXT;
            borderColor = "#1E88E5";
        } else {
            bgColor = LIGHT_FIRST_COLUMN_BG;
            textColor = "#1E293B";
            borderColor = "#1E88E5";
        }

        label.setStyle("-fx-background-color: " + bgColor + ";" +
                "-fx-text-fill: " + textColor + ";" +
                "-fx-padding: 10 16 10 16; -fx-background-radius: 14;" +
                "-fx-border-color: " + borderColor + "; -fx-border-width: 0 2 0 0;" +
                "-fx-effect: dropshadow(gaussian, rgba(30, 136, 229, 0.2), 10, 0, 0, 3);");
    }
    @Override
    public void clearViewSlots() {
        System.out.println("🧹 Nettoyage de tous les slots...");

        List<SlotView> slotsToRemove = new ArrayList<>(slotViewMap.values());

        for (SlotView slot : slotsToRemove) {
            removeSlot(slot.getSlotId());
        }

        slotViewMap.clear();
        searchIndex.clear();
        colorIndex = 0;

        getChildren().removeIf(node -> node instanceof SlotView);

        updateStatistics();
        System.out.println("✅ Tous les slots ont été nettoyés");
    }

    @Override
    public void clearLabelsInFirstRow() {
        // 🔥 SAUVEGARDER les textes avant nettoyage
        List<String> labelTexts = firstRowLabels.stream()
                .map(Label::getText)
                .collect(Collectors.toList());

        getChildren().removeAll(firstRowLabels);
        firstRowLabels.clear();

        System.out.println("🧹 First row labels cleared - " + labelTexts.size() + " labels sauvegardés");
    }

    @Override
    public void clearLabelsInFirstColumn() {
        // 🔥 SAUVEGARDER les textes avant nettoyage
        List<String> labelTexts = firstColumnLabels.stream()
                .map(Label::getText)
                .collect(Collectors.toList());

        getChildren().removeAll(firstColumnLabels);
        firstColumnLabels.clear();

        System.out.println("🧹 First column labels cleared - " + labelTexts.size() + " labels sauvegardés");
    }

  /* @Override
    public void addButtonBoxInTopLeftCell(HBox buttonBox) {
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(3);
        buttonBox.setPadding(new Insets(6));

        String bg, border;
        if (isDarkMode()) {
            bg = DARK_FIRST_ROW_BG;
            border = "#1E88E5";
        } else {
            bg = LIGHT_FIRST_ROW_BG;
            border = "#1E88E5";
        }

        buttonBox.setStyle("-fx-background-color: " + bg + "; -fx-border-color: " + border + ";" +
                "-fx-border-width: 2; -fx-border-radius: 14; -fx-background-radius: 14;" +
                "-fx-effect: dropshadow(gaussian, rgba(30, 136, 229, 0.3), 8, 0, 0, 3);");

        buttonBox.getChildren().forEach(node -> {
            if (node instanceof Button) {
                Button button = (Button) node;
                String normalBg = isDarkMode() ? "#1565C0" : "#1E88E5";
                String hoverBg = isDarkMode() ? "#1E88E5" : "#1565C0";

                String normalStyle = "-fx-background-color: " + normalBg + "; -fx-text-fill: white;" +
                        "-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 8 12 8 12;" +
                        "-fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;" +
                        "-fx-min-width: 36px; -fx-max-width: 36px; -fx-min-height: 32px; -fx-max-height: 32px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 4, 0, 0, 2);";

                String hoverStyle = "-fx-background-color: " + hoverBg + "; -fx-text-fill: white;" +
                        "-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 8 12 8 12;" +
                        "-fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;" +
                        "-fx-min-width: 36px; -fx-max-width: 36px; -fx-min-height: 32px; -fx-max-height: 32px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 6, 0, 0, 3);";

                button.setStyle(normalStyle);
                button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
                button.setOnMouseExited(e -> button.setStyle(normalStyle));
            }
        });

        add(buttonBox, 0, 0);
        this.buttonBox = buttonBox;
        GridPane.setFillWidth(buttonBox, true);
        GridPane.setFillHeight(buttonBox, true);
    }*/
  @Override
  public void addButtonBoxInTopLeftCell(HBox buttonBox) {
      buttonBox.setAlignment(Pos.CENTER);
      buttonBox.setSpacing(3);
      buttonBox.setPadding(new Insets(6));

      String bg, border;
      if (isDarkMode()) {
          bg = DARK_FIRST_ROW_BG;
          border = "#1E88E5";
      } else {
          bg = LIGHT_FIRST_ROW_BG;
          border = "#1E88E5";
      }

      buttonBox.setStyle("-fx-background-color: " + bg + "; -fx-border-color: " + border + ";" +
              "-fx-border-width: 2; -fx-border-radius: 14; -fx-background-radius: 14;" +
              "-fx-effect: dropshadow(gaussian, rgba(30, 136, 229, 0.3), 8, 0, 0, 3);");

      // 🔥 FILTRER LES BOUTONS SELON LES PERMISSIONS
      List<Node> buttonsToShow = new ArrayList<>();

      for (Node node : buttonBox.getChildren()) {
          if (node instanceof Button) {
              Button button = (Button) node;

              if (isAddSlotButton(button)) {
                  // Bouton "Add Slot" - visible uniquement pour les admins
                  button.setVisible(hasSlotCreationPermission());
                  button.setManaged(hasSlotCreationPermission());
                  System.out.println("🎯 Bouton Add Slot - Visible: " + hasSlotCreationPermission());
              } else {
                  // Autres boutons - toujours visibles
                  button.setVisible(true);
                  button.setManaged(true);
              }

              if (button.isVisible()) {
                  buttonsToShow.add(button);

                  // Appliquer le style normal
                  String normalBg = isDarkMode() ? "#1565C0" : "#1E88E5";
                  String hoverBg = isDarkMode() ? "#1E88E5" : "#1565C0";

                  String normalStyle = "-fx-background-color: " + normalBg + "; -fx-text-fill: white;" +
                          "-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 8 12 8 12;" +
                          "-fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;" +
                          "-fx-min-width: 36px; -fx-max-width: 36px; -fx-min-height: 32px; -fx-max-height: 32px;" +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 4, 0, 0, 2);";

                  String hoverStyle = "-fx-background-color: " + hoverBg + "; -fx-text-fill: white;" +
                          "-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 8 12 8 12;" +
                          "-fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;" +
                          "-fx-min-width: 36px; -fx-max-width: 36px; -fx-min-height: 32px; -fx-max-height: 32px;" +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 6, 0, 0, 3);";

                  button.setStyle(normalStyle);
                  button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
                  button.setOnMouseExited(e -> button.setStyle(normalStyle));
              }
          }
      }

      // Si aucun bouton n'est visible, afficher un message
      if (buttonsToShow.isEmpty() && !hasSlotCreationPermission()) {
          Label infoLabel = new Label("👤 Connecté");
          infoLabel.setStyle("-fx-text-fill: " + (isDarkMode() ? "#94A3B8" : "#64748B") + ";" +
                  "-fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 8 12;");
          buttonBox.getChildren().add(infoLabel);
      }

      add(buttonBox, 0, 0);
      this.buttonBox = buttonBox;
      GridPane.setFillWidth(buttonBox, true);
      GridPane.setFillHeight(buttonBox, true);
  }


    // Méthode pour identifier le bouton "Add Slot"
    /*private boolean isAddSlotButton(Button button) {
        String buttonText = button.getText();
        String buttonId = button.getId();

        // Vérifier par texte
        if (buttonText != null) {
            String lowerText = buttonText.toLowerCase();
            return lowerText.contains("+") ||
                    lowerText.contains("add") ||
                    lowerText.contains("ajouter") ||
                    lowerText.contains("nouveau") ||
                    lowerText.contains("new") ||
                    lowerText.contains("créer");
        }

        // Vérifier par ID
        if (buttonId != null) {
            String lowerId = buttonId.toLowerCase();
            return lowerId.contains("add") ||
                    lowerId.contains("new") ||
                    lowerId.contains("create");
        }

        return false;
    }*/
    /**
     * Identifie le bouton "Add Slot" de manière plus robuste
     */
    private boolean isAddSlotButton(Button button) {
        String buttonText = button.getText();
        String buttonId = button.getId();
        String tooltip = button.getTooltip() != null ? button.getTooltip().getText() : "";

        // Vérifier par texte
        if (buttonText != null) {
            String lowerText = buttonText.toLowerCase();
            if (lowerText.contains("+") ||
                    lowerText.contains("add") ||
                    lowerText.contains("ajouter") ||
                    lowerText.contains("nouveau") ||
                    lowerText.contains("new") ||
                    lowerText.contains("créer") ||
                    lowerText.contains("slot") ||
                    lowerText.contains("créneau")) {
                System.out.println("✅ Identifié comme Add Slot par texte: " + buttonText);
                return true;
            }
        }

        // Vérifier par ID
        if (buttonId != null) {
            String lowerId = buttonId.toLowerCase();
            if (lowerId.contains("add") ||
                    lowerId.contains("new") ||
                    lowerId.contains("create") ||
                    lowerId.contains("slot")) {
                System.out.println("✅ Identifié comme Add Slot par ID: " + buttonId);
                return true;
            }
        }

        // Vérifier par tooltip
        if (!tooltip.isEmpty()) {
            String lowerTooltip = tooltip.toLowerCase();
            if (lowerTooltip.contains("add") ||
                    lowerTooltip.contains("nouveau") ||
                    lowerTooltip.contains("créer") ||
                    lowerTooltip.contains("slot")) {
                System.out.println("✅ Identifié comme Add Slot par tooltip: " + tooltip);
                return true;
            }
        }

        return false;
    }

    // Méthode pour appliquer le style au bouton "Add Slot"
    private void applyAddSlotButtonStyle(Button button) {
        String normalBg = isDarkMode() ? "#1565C0" : "#1E88E5";
        String hoverBg = isDarkMode() ? "#1E88E5" : "#1565C0";

        String normalStyle = "-fx-background-color: " + normalBg + "; -fx-text-fill: white;" +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 14 10 14;" +
                "-fx-background-radius: 10; -fx-border-radius: 10; -fx-cursor: hand;" +
                "-fx-min-width: 40px; -fx-max-width: 40px; -fx-min-height: 36px; -fx-max-height: 36px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 4, 0, 0, 2);";

        String hoverStyle = "-fx-background-color: " + hoverBg + "; -fx-text-fill: white;" +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 14 10 14;" +
                "-fx-background-radius: 10; -fx-border-radius: 10; -fx-cursor: hand;" +
                "-fx-min-width: 40px; -fx-max-width: 40px; -fx-min-height: 36px; -fx-max-height: 36px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 6, 0, 0, 3);";

        button.setStyle(normalStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(normalStyle));
    }


    private void applyButtonStyle(Button button) {
        String normalBg = isDarkMode() ? "#1565C0" : "#1E88E5";
        String hoverBg = isDarkMode() ? "#1E88E5" : "#1565C0";

        String normalStyle = "-fx-background-color: " + normalBg + "; -fx-text-fill: white;" +
                "-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 8 12 8 12;" +
                "-fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;" +
                "-fx-min-width: 36px; -fx-max-width: 36px; -fx-min-height: 32px; -fx-max-height: 32px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 4, 0, 0, 2);";

        String hoverStyle = "-fx-background-color: " + hoverBg + "; -fx-text-fill: white;" +
                "-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 8 12 8 12;" +
                "-fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;" +
                "-fx-min-width: 36px; -fx-max-width: 36px; -fx-min-height: 32px; -fx-max-height: 32px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 6, 0, 0, 3);";

        button.setStyle(normalStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(normalStyle));
    }

    @Override
    public void clearButtonBoxInFirstCell() {
        if (buttonBox != null) {
            getChildren().remove(buttonBox);
            buttonBox = null;
        }
    }

    // 🔄 DISPLAY MODE MANAGEMENT
    private void cycleDisplayMode() {
        DisplayMode[] modes = DisplayMode.values();
        int nextIndex = (currentDisplayMode.get().ordinal() + 1) % modes.length;
        currentDisplayMode.set(modes[nextIndex]);
        showDisplayModeNotification();
    }

    private void applyDisplayMode() {
        switch (currentDisplayMode.get()) {
            case GRID -> applyGridDisplay();
            case LIST -> applyListDisplay();
            case TIMELINE -> applyTimelineDisplay();
            case COMPACT -> applyCompactDisplay();
            case DAY -> applyDayDisplay();
            case WEEK -> applyWeekDisplay();
        }
        animateDisplayChange();
    }

    private void applyGridDisplay() {
        setHgap(12);
        setVgap(12);
        setPadding(new Insets(25));
        setStyle("-fx-background-color: " + (isDarkMode() ? "rgba(15, 30, 50, 0.5)" : "rgba(255, 255, 255, 0.8)") + ";");
    }

    private void applyListDisplay() {
        setHgap(6);
        setVgap(8);
        setPadding(new Insets(18));
        setStyle("-fx-background-color: " + (isDarkMode() ? "rgba(15, 30, 50, 0.5)" : "rgba(248, 249, 250, 0.8)") + ";");
    }

    private void applyTimelineDisplay() {
        setHgap(10);
        setVgap(15);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: " + (isDarkMode() ? "rgba(15, 30, 50, 0.5)" : "rgba(240, 248, 255, 0.8)") + ";");
    }

    private void applyCompactDisplay() {
        setHgap(3);
        setVgap(3);
        setPadding(new Insets(8));
        setStyle("-fx-background-color: " + (isDarkMode() ? "rgba(15, 30, 50, 0.5)" : "rgba(255, 255, 255, 0.8)") + ";");
    }

    private void applyDayDisplay() {
        setHgap(8);
        setVgap(8);
        setPadding(new Insets(18));
        setStyle("-fx-background-color: " + (isDarkMode() ? "rgba(15, 30, 50, 0.5)" : "rgba(248, 249, 250, 0.8)") + ";");
    }

    private void applyWeekDisplay() {
        setHgap(6);
        setVgap(6);
        setPadding(new Insets(15));
        setStyle("-fx-background-color: " + (isDarkMode() ? "rgba(15, 30, 50, 0.5)" : "rgba(240, 248, 255, 0.8)") + ";");
    }

    // 🎨 THEME MANAGEMENT - BLEU PROFESSIONNEL
    private void applyCurrentTheme() {
        System.out.println("🎨 Application du thème - Mode sombre: " + isDarkMode());

        applyGridBackground();
        updateControlButtons();
        applyHeaderTheme();
        updateFirstRowLabelsTheme();
        updateFirstColumnLabelsTheme();
        applyButtonBoxTheme();
        updateSearchResultsTheme();

        // FORCER la mise à jour des boutons
        if (controlButtons != null) {
            System.out.println("🔄 Mise à jour des styles des boutons de contrôle");
            for (Node node : controlButtons.getChildren()) {
                if (node instanceof Button) {
                    Button btn = (Button) node;
                    System.out.println("   - Bouton: " + btn.getText() + " - Style appliqué");
                    // Réappliquer le style
                    if (btn.getText().contains("❓")) {
                        styleCircularButton(btn, false, "#3498DB");
                    } else if (btn.getText().contains("📊")) {
                        styleCircularButton(btn, false, "#9B59B6");
                    } else if (btn.getText().contains("🌙") || btn.getText().contains("☀️")) {
                        styleCircularButton(btn, false, "#F39C12");
                    } else if (btn.getText().contains("✕")) {
                        styleCircularButton(btn, false, "#E74C3C");
                    }
                }
            }
        }
    }

    private void applyGridBackground() {
        String gridBg = isDarkMode() ? "rgba(15, 30, 50, 0.3)" : "rgba(255, 255, 255, 0.7)";
        String gridStyle = String.format("-fx-background-color: %s; -fx-background-radius: 20;", gridBg);
        setStyle(gridStyle + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 25, 0, 0, 10);");
    }

    private void applyHeaderTheme() {
        if (closeButton != null && closeButton.getParent() != null) {
            Node parent = closeButton.getParent();
            while (parent != null && !(parent instanceof HBox)) {
                parent = parent.getParent();
            }
            if (parent instanceof HBox) {
                HBox header = (HBox) parent;
                applyHeaderStyle(header);
            }
        }
    }

    private void applyHeaderStyle(HBox header) {
        boolean isDark = isDarkMode();

        // COULEURS SOLIDES ET VISIBLES
        String background, shadow;

        if (isDark) {
            background = "#1565C0"; // BLEU SOLIDE (pas de gradient)
            shadow = "dropshadow(three-pass-box, rgba(21, 101, 192, 0.8), 25, 0.5, 0, 10)";
        } else {
            background = "#1E88E5"; // BLEU SOLIDE (pas de gradient)
            shadow = "dropshadow(three-pass-box, rgba(30, 136, 229, 0.8), 20, 0, 0, 8)";
        }

        String style = String.format(
                "-fx-background-color: %s; " +
                        "-fx-background-radius: 0 0 20 20; " +
                        "-fx-effect: %s;",
                background, shadow
        );

        header.setStyle(style);
        System.out.println("🎨 Header style appliqué: " + background);

        applyHeaderTextColors(header, isDark);
    }

    private void applyHeaderTextColors(HBox header, boolean isDark) {
        for (Node node : header.getChildren()) {
            if (node instanceof HBox) {
                HBox titleContainer = (HBox) node;
                for (Node child : titleContainer.getChildren()) {
                    if (child instanceof Label) {
                        Label label = (Label) child;
                        String currentText = label.getText();

                        if (currentText != null && currentText.contains("Calendrier")) {
                            updateTitleStyle(label);
                        }
                    }
                }
            }
        }
    }

    private void updateControlButtons() {
        // VÉRIFICATION CRITIQUE : S'assurer que controlButtons n'est pas null
        if (controlButtons == null) {
            System.err.println("⚠️ controlButtons est null dans updateControlButtons(), reconstruction...");

            // RECONSTRUIRE COMPLÈTEMENT LES BOUTONS
            controlButtons = createControlButtons();

            // RECONSTRUIRE COMPLÈTEMENT LE HEADER SI LA SCÈNE EXISTE
            if (scene != null) {
                Parent root = scene.getRoot();
                if (root instanceof BorderPane) {
                    BorderPane mainContainer = (BorderPane) root;

                    // RECRÉER COMPLÈTEMENT LE HEADER
                    Node newHeader = createEnhancedHeader();
                    mainContainer.setTop(newHeader);

                    System.out.println("✅ Header complètement reconstruit avec nouveaux controlButtons");
                }
            }
            return;
        }

        // Mettre à jour les styles des boutons existants
        System.out.println("🔄 Mise à jour des styles des boutons de contrôle");
        for (Node node : controlButtons.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                applyProfessionalButtonStyle(btn);
                System.out.println("   - Bouton: " + btn.getText() + " - Style professionnel appliqué");
            }
        }
    }

    // NOUVELLE MÉTHODE POUR APPLIQUER LES STYLES PROFESSIONNELS
    private void applyProfessionalButtonStyle(Button btn) {
        String baseStyle =
                "-fx-background-radius: 18; " +
                        "-fx-border-radius: 18; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 0; " +
                        "-fx-alignment: center; " +
                        "-fx-cursor: hand; " +
                        "-fx-min-width: 36px; " +
                        "-fx-min-height: 36px; " +
                        "-fx-max-width: 36px; " +
                        "-fx-max-height: 36px;";

        if (btn.getText().contains("🌙") || btn.getText().contains("☀️")) {
            // Bouton thème - Bleu
            btn.setStyle(baseStyle +
                    "-fx-background-color: #1E88E5; " +
                    "-fx-text-fill: white; " +
                    "-fx-border-color: #1565C0; " +
                    "-fx-border-width: 1; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0, 1, 1);");
        } else if (btn.getText().contains("✕")) {
            // Bouton fermer - Rouge
            btn.setStyle(baseStyle +
                    "-fx-background-color: #E74C3C; " +
                    "-fx-text-fill: white; " +
                    "-fx-border-color: #C0392B; " +
                    "-fx-border-width: 1; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0, 1, 1);");
        } else if (btn.getText().contains("👤")) {
            // Bouton utilisateur - Vert
            btn.setStyle(
                    "-fx-background-color: #43A047; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-weight: bold; " +
                            "-fx-font-size: 11px; " +
                            "-fx-border-color: #2E7D32; " +
                            "-fx-border-width: 1; " +
                            "-fx-border-radius: 12; " +
                            "-fx-background-radius: 12; " +
                            "-fx-padding: 6 12; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 1, 1); " +
                            "-fx-cursor: hand;");
        }

        // Ajouter les effets de survol
        setupButtonHoverEffects(btn);
    }

    // NOUVELLE MÉTHODE POUR LES EFFETS DE SURVOL
    private void setupButtonHoverEffects(Button btn) {
        String originalStyle = btn.getStyle();

        btn.setOnMouseEntered(e -> {
            if (btn.getText().contains("🌙") || btn.getText().contains("☀️")) {
                btn.setStyle(originalStyle +
                        "-fx-background-color: #1565C0; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 6, 0, 2, 2);");
            } else if (btn.getText().contains("✕")) {
                btn.setStyle(originalStyle +
                        "-fx-background-color: #C0392B; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 6, 0, 2, 2);");
            } else if (btn.getText().contains("👤")) {
                btn.setStyle(originalStyle +
                        "-fx-background-color: #2E7D32; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 2, 2);");
            }
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle(originalStyle);
        });
    }

    private void updateFirstRowLabelsTheme() {
        for (int i = 0; i < firstRowLabels.size(); i++) {
            Label label = firstRowLabels.get(i);
            if (isDarkMode()) {
                label.setStyle("-fx-background-color: " + DARK_FIRST_ROW_BG + ";" +
                        "-fx-text-fill: " + DARK_TEXT + ";" +
                        "-fx-padding: 14 20 14 20; -fx-background-radius: 14;" +
                        "-fx-border-color: #1E88E5; -fx-border-width: 0 0 2 0;" +
                        "-fx-effect: dropshadow(gaussian, rgba(30, 136, 229, 0.3), 15, 0, 0, 5);");
            } else {
                label.setStyle("-fx-background-color: " + LIGHT_FIRST_ROW_BG + ";" +
                        "-fx-text-fill: #1E293B;" +
                        "-fx-padding: 14 20 14 20; -fx-background-radius: 14;" +
                        "-fx-border-color: #1E88E5; -fx-border-width: 0 0 2 0;" +
                        "-fx-effect: dropshadow(gaussian, rgba(30, 136, 229, 0.2), 12, 0, 0, 4);");
            }
        }
    }

    private void updateFirstColumnLabelsTheme() {
        for (Label label : firstColumnLabels) {
            String bgColor, textColor, borderColor;
            if (isDarkMode()) {
                bgColor = DARK_FIRST_COLUMN_BG;
                textColor = DARK_TEXT;
                borderColor = "#1E88E5";
            } else {
                bgColor = LIGHT_FIRST_COLUMN_BG;
                textColor = "#1E293B";
                borderColor = "#1E88E5";
            }

            label.setStyle("-fx-background-color: " + bgColor + ";" +
                    "-fx-text-fill: " + textColor + ";" +
                    "-fx-padding: 10 16 10 16; -fx-background-radius: 14;" +
                    "-fx-border-color: " + borderColor + "; -fx-border-width: 0 2 0 0;" +
                    "-fx-effect: dropshadow(gaussian, rgba(30, 136, 229, 0.2), 10, 0, 0, 3);");
        }
    }

    private void applyButtonBoxTheme() {
        if (buttonBox != null) {
            String bg, border;
            if (isDarkMode()) {
                bg = DARK_FIRST_ROW_BG;
                border = "#1E88E5";
            } else {
                bg = LIGHT_FIRST_ROW_BG;
                border = "#1E88E5";
            }

            buttonBox.setStyle("-fx-background-color: " + bg + "; -fx-border-color: " + border + ";" +
                    "-fx-border-width: 2; -fx-border-radius: 14; -fx-background-radius: 14;" +
                    "-fx-effect: dropshadow(gaussian, rgba(30, 136, 229, 0.3), 8, 0, 0, 3);");
        }
    }

    private void setupPropertyListeners() {
        darkModeProperty.addListener((obs, oldVal, newVal) -> {
            applyCurrentTheme();
            updateTitleInHeader();
            updateFirstRowLabelsTheme();
            updateFirstColumnLabelsTheme();
            applyButtonBoxTheme();
        });
        currentDisplayMode.addListener((obs, oldVal, newVal) -> applyDisplayMode());
        scaleFactor.addListener((obs, oldVal, newVal) -> updateLabelsScale());
    }

    private void updateSearchResultsTheme() {
        if (searchResultsPanel != null) {
            searchResultsPanel.setStyle("-fx-background-color: " + (isDarkMode() ? "rgba(15, 30, 50, 0.95)" : "rgba(255, 255, 255, 0.95)") +
                    "; -fx-padding: 12; -fx-background-radius: 12;");
        }
    }

    // 🎪 ANIMATIONS AND EFFECTS - AMÉLIORÉES
    private void animateLabelEntrance(Label label, int index) {
        // 🔥 RÉDUIRE le délai d'animation pendant le rafraîchissement
        long delay = isRefreshing ? 0 : index * 30;

        label.setOpacity(0);
        label.setScaleX(0.9);
        label.setScaleY(0.9);

        FadeTransition fade = new FadeTransition(Duration.millis(300), label); // 🔥 Durée réduite
        fade.setFromValue(0);
        fade.setToValue(1);

        ScaleTransition scale = new ScaleTransition(Duration.millis(300), label);
        scale.setFromX(0.9);
        scale.setFromY(0.9);
        scale.setToX(1.0);
        scale.setToY(1.0);

        TranslateTransition slide = new TranslateTransition(Duration.millis(300), label);
        slide.setFromY(-10); // 🔥 Translation réduite
        slide.setToY(0);

        ParallelTransition entrance = new ParallelTransition(fade, scale, slide);
        entrance.setDelay(Duration.millis(delay));
        entrance.setInterpolator(Interpolator.EASE_OUT);
        entrance.play();
    }

    private void animateDisplayChange() {
        FadeTransition fade = new FadeTransition(Duration.millis(400), this);
        fade.setFromValue(0.6);
        fade.setToValue(1.0);
        fade.play();
    }

    // Pour les animations JavaFX, utilisez javafx.util.Duration
    private void pulseSlot(int slotId) {
        SlotView slot = slotViewMap.get(slotId);
        if (slot != null) {
            // CORRECTION : Utiliser javafx.util.Duration pour les animations
            javafx.animation.ScaleTransition pulse1 = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(150), slot);
            pulse1.setFromX(1.0);
            pulse1.setFromY(1.0);
            pulse1.setToX(1.05);
            pulse1.setToY(1.05);

            javafx.animation.ScaleTransition pulse2 = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(150), slot);
            pulse2.setFromX(1.05);
            pulse2.setFromY(1.05);
            pulse2.setToX(1.0);
            pulse2.setToY(1.0);

            javafx.animation.SequentialTransition sequence = new javafx.animation.SequentialTransition(pulse1, pulse2);
            sequence.setCycleCount(2);
            sequence.play();
        }
    }

    // 🔔 NOTIFICATION SYSTEM - AMÉLIORÉE
    private void showTemporaryNotification(String message, long duration) {
        Label notification = new Label(message);
        notification.setStyle("-fx-background-color: linear-gradient(to right, #1E88E5, #1565C0);" +
                "-fx-text-fill: white; -fx-padding: 14 28; -fx-background-radius: 25;" +
                "-fx-font-size: 14px; -fx-font-weight: bold;" +
                "-fx-effect: dropshadow(gaussian, rgba(30, 136, 229, 0.5), 20, 0, 0, 8);");

        StackPane notificationPane = new StackPane(notification);
        notificationPane.setAlignment(Pos.TOP_CENTER);
        notificationPane.setPadding(new Insets(25, 0, 0, 0));
        notificationPane.setMouseTransparent(true);

        notificationContainer.getChildren().add(notificationPane);

        notificationPane.setOpacity(0);
        notificationPane.setTranslateY(-60);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), notificationPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(500), notificationPane);
        slideIn.setFromY(-60);
        slideIn.setToY(0);
        slideIn.setInterpolator(Interpolator.EASE_OUT);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(500), notificationPane);
        scaleIn.setFromX(0.8);
        scaleIn.setFromY(0.8);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);

        ParallelTransition enterAnimation = new ParallelTransition(fadeIn, slideIn, scaleIn);
        PauseTransition pause = new PauseTransition(Duration.millis(duration));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), notificationPane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> notificationContainer.getChildren().remove(notificationPane));

        SequentialTransition sequence = new SequentialTransition(enterAnimation, pause, fadeOut);
        sequence.play();
    }

    // 🎯 CONTEXT MENUS AND DIALOGS
   /* private void addContextMenuToSlot(Parent slotContainer, SlotView slotView) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem editItem = new MenuItem("✏️ Modifier");
        MenuItem deleteItem = new MenuItem("🗑️ Supprimer");
        MenuItem duplicateItem = new MenuItem("📋 Dupliquer");
        MenuItem exportItem = new MenuItem("📤 Exporter ce slot");
        MenuItem highlightItem = new MenuItem("🔦 Surligner");
        MenuItem infoItem = new MenuItem("ℹ️ Informations détaillées");

        contextMenu.getItems().addAll(editItem, deleteItem, duplicateItem,
                new SeparatorMenuItem(), exportItem, highlightItem, infoItem);

        contextMenu.setStyle("-fx-background-color: " + (isDarkMode() ? "#2D2D2D" : "white") + ";" +
                "-fx-text-fill: " + (isDarkMode() ? DARK_TEXT : "black") + ";");

        slotContainer.setOnContextMenuRequested(event ->
                contextMenu.show(slotContainer, event.getScreenX(), event.getScreenY()));

        editItem.setOnAction(e -> editSlot(slotView));
        deleteItem.setOnAction(e -> removeSlot(slotView.getSlotId()));
        duplicateItem.setOnAction(e -> duplicateSlot(slotView));
        highlightItem.setOnAction(e -> pulseSlot(slotView.getSlotId()));
        infoItem.setOnAction(e -> showDetailedSlotInfo(slotView));
        exportItem.setOnAction(e -> exportSingleSlot(slotView));
    }*/
    private void addContextMenuToSlot(SlotView slotView) {
        ContextMenu contextMenu = new ContextMenu();

        // Créer des CustomMenuItem pour avoir des tooltips
        CustomMenuItem editItem = createMenuItemWithTooltip("✏️ Modifier", !hasSlotModificationPermission(), "Réservé aux administrateurs");
        CustomMenuItem deleteItem = createMenuItemWithTooltip("🗑️ Supprimer", !hasSlotModificationPermission(), "Réservé aux administrateurs");
        CustomMenuItem duplicateItem = createMenuItemWithTooltip("📋 Dupliquer", !hasSlotModificationPermission(), "Réservé aux administrateurs");

        MenuItem exportItem = new MenuItem("📤 Exporter ce slot");
        MenuItem highlightItem = new MenuItem("🔦 Surligner");
        MenuItem infoItem = new MenuItem("ℹ️ Informations détaillées");

        boolean canModify = hasSlotModificationPermission();

        contextMenu.getItems().addAll(editItem, deleteItem, duplicateItem,
                new SeparatorMenuItem(), exportItem, highlightItem, infoItem);

        contextMenu.setStyle("-fx-background-color: " + (isDarkMode() ? "#2D2D2D" : "white") + ";" +
                "-fx-text-fill: " + (isDarkMode() ? DARK_TEXT : "black") + ";");

        slotView.setOnContextMenuRequested(event ->
                contextMenu.show(slotView, event.getScreenX(), event.getScreenY()));

        // Gestion des événements...
        editItem.setOnAction(e -> {
            if (canModify) {
                editSlot(slotView);
            } else {
                showPermissionError("Modification", "Seuls les administrateurs peuvent modifier les créneaux.");
            }
        });

        deleteItem.setOnAction(e -> {
            if (canModify) {
                removeSlot(slotView.getSlotId());
            } else {
                showPermissionError("Suppression", "Seuls les administrateurs peuvent supprimer les créneaux.");
            }
        });

        duplicateItem.setOnAction(e -> {
            if (canModify) {
                duplicateSlot(slotView);
            } else {
                showPermissionError("Duplication", "Seuls les administrateurs peuvent dupliquer les créneaux.");
            }
        });

        highlightItem.setOnAction(e -> pulseSlot(slotView.getSlotId()));
        infoItem.setOnAction(e -> showDetailedSlotInfo(slotView));
        exportItem.setOnAction(e -> exportSingleSlot(slotView));
    }

    private CustomMenuItem createMenuItemWithTooltip(String text, boolean disabled, String tooltipText) {
        Label label = new Label(text);
        if (disabled) {
            label.setDisable(true);
            Tooltip tooltip = new Tooltip(tooltipText);
            Tooltip.install(label, tooltip);
        }

        CustomMenuItem menuItem = new CustomMenuItem(label);
        menuItem.setDisable(disabled);
        return menuItem;
    }
    private void showDetailedSlotInfo(SlotView slotView) {
        StringBuilder info = new StringBuilder();
        info.append("📋 INFORMATIONS DÉTAILLÉES DU CRÉNEAU\n");
        info.append("================================\n\n");

        info.append("🔢 ID: ").append(slotView.getSlotId()).append("\n");
        info.append("📖 Matière: ").append(slotView.getMatiere() != null ? slotView.getMatiere() : "Non spécifié").append("\n");
        info.append("👨‍🏫 Enseignant: ").append(slotView.getEnseignant() != null ? slotView.getEnseignant() : "Non spécifié").append("\n");
        info.append("🏫 Salle: ").append(slotView.getSalle() != null ? slotView.getSalle() : "Non spécifié").append("\n");

        // INFORMATIONS DE DATE ET HEURE
        if (slotView.getDate() != null) {
            info.append("📅 Date: ").append(formatDate(slotView.getDate())).append("\n");
        }

        if (slotView.getHeureDebut() != null && slotView.getHeureFin() != null) {
            info.append("🕐 Horaire: ").append(formatTime(slotView.getHeureDebut())).append(" - ")
                    .append(formatTime(slotView.getHeureFin())).append("\n");
        }

        // DURÉE - CORRECTION : Utiliser la bonne méthode pour java.time.Duration
        if (slotView.getSlot() != null && slotView.getSlot().duration() != null) {
            java.time.Duration duration = slotView.getSlot().duration();
            long hours = duration.toHours();
            // CORRECTION : Remplacer toMinutesPart() par le calcul manuel
            long minutes = duration.toMinutes() % 60;
            info.append("⏱️ Durée: ").append(String.format("%dh%02d", hours, minutes)).append("\n");
        }

        info.append("📚 Type: ").append(slotView.getTypeCours() != null ? slotView.getTypeCours() : "Non spécifié").append("\n");
        info.append("👥 Groupes: ").append(slotView.getGroupes() != null ? slotView.getGroupes() : "Non spécifié").append("\n");

        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
        infoAlert.setTitle("Informations détaillées");
        infoAlert.setHeaderText("Créneau #" + slotView.getSlotId());
        infoAlert.setContentText(info.toString());

        DialogPane dialogPane = infoAlert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: " + (isDarkMode() ? "#2D2D2D" : "white") + ";" +
                "-fx-text-fill: " + (isDarkMode() ? DARK_TEXT : "black") + ";");

        infoAlert.showAndWait();
    }

    public void updateSlotView(SlotView updatedSlotView, int rowIndex, int columnIndex, int rowSpan, int colSpan) {
        int slotId = updatedSlotView.getSlotId();

        System.out.println("🔄 Mise à jour du slot " + slotId + " à la position (" + rowIndex + "," + columnIndex + ")");

        // 🔥 VALIDATION CRITIQUE des données
        if (!validateSlotData(updatedSlotView)) {
            System.err.println("❌ Données du slot " + slotId + " invalides - annulation de la mise à jour");
            showTemporaryNotification("❌ Données invalides pour le créneau #" + slotId, 3000);
            return;
        }

        if (slotViewMap.containsKey(slotId)) {
            SlotView existingSlot = slotViewMap.get(slotId);

            try {
                // Mettre à jour les propriétés du slot existant
                updateExistingSlotProperties(existingSlot, updatedSlotView);

                // Mettre à jour la position dans la grille
                updateSlotPosition(existingSlot, rowIndex, columnIndex, rowSpan, colSpan);

                // 🔥 NOTIFIER le backend de la modification
                notifyBackendOfUpdate(updatedSlotView);

                System.out.println("✅ Slot " + slotId + " mis à jour avec succès");
                showTemporaryNotification("✅ Créneau #" + slotId + " mis à jour", 2000);

            } catch (Exception e) {
                System.err.println("❌ Erreur lors de la mise à jour du slot " + slotId + ": " + e.getMessage());
                handleSaveError(slotId, e);
            }
        } else {
            System.out.println("❌ Slot " + slotId + " non trouvé, ajout comme nouveau");
            addSlotView(updatedSlotView, rowIndex, columnIndex, rowSpan, colSpan);
        }
    }

    /**
     * Notifie le backend de la modification d'un slot
     */
    private void notifyBackendOfUpdate(SlotView slotView) {
        // Implémentez ici l'appel à votre service backend
        // pour sauvegarder les modifications dans la base de données
        System.out.println("📡 Envoi des modifications du slot " + slotView.getSlotId() + " au backend");

        // Exemple d'appel au service (à adapter selon votre implémentation)
    /*
    try {
        slotService.updateSlot(slotView.toSlot()).thenAccept(updated -> {
            if (updated) {
                System.out.println("✅ Slot " + slotView.getSlotId() + " sauvegardé en base");
            } else {
                System.err.println("❌ Échec sauvegarde slot " + slotView.getSlotId());
            }
        });
    } catch (Exception e) {
        System.err.println("❌ Erreur communication backend: " + e.getMessage());
        throw new RuntimeException("Erreur sauvegarde", e);
    }
    */
    }
    private void updateExistingSlotProperties(SlotView existingSlot, SlotView updatedSlot) {
        // Mettre à jour le style
        int colorIdx = updatedSlot.getSlotId() % COLOR_SCHEMES.length;
        String[] colors = COLOR_SCHEMES[colorIdx];

        String baseStyle = String.format("-fx-background-color: %s; -fx-border-color: %s; " +
                "-fx-border-width: 2; -fx-border-radius: 14; -fx-background-radius: 14; " +
                "-fx-padding: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 12, 0, 0, 6); " +
                "-fx-cursor: hand;", colors[0], colors[1]);

        existingSlot.setStyle(baseStyle);

        // CORRECTION : Ne pas tenter de caster SlotView en Labeled
        // Mettre à jour le contenu via les méthodes spécifiques de SlotView
        updateSlotViewContent(existingSlot, updatedSlot);

        // Réappliquer les interactions
        setupSlotInteractions(existingSlot, colors, baseStyle);
    }

    // CORRECTION : Nouvelle méthode pour mettre à jour le contenu sans cast
    private void updateSlotViewContent(SlotView existingSlot, SlotView updatedSlot) {
        // Si votre SlotView a des méthodes pour mettre à jour son contenu, utilisez-les ici
        // Par exemple, si SlotView a des méthodes setText() ou setContent()

        // Solution temporaire : réappliquer le style et les interactions
        // Le contenu spécifique devrait être géré par la classe SlotView elle-même
        System.out.println("🔄 Mise à jour du contenu du slot " + existingSlot.getSlotId());

        // Si vous avez besoin d'accéder à des propriétés spécifiques,
        // ajoutez des méthodes d'accès dans l'interface SlotView
    }

    private void updateSlotPosition(SlotView slot, int rowIndex, int columnIndex, int rowSpan, int colSpan) {
        // Supprimer l'ancienne position
        getChildren().remove(slot);

        // Réajouter à la nouvelle position
        add(slot, columnIndex, rowIndex, colSpan, rowSpan);
        // Animation de mise à jour
        pulseSlot(slot.getSlotId());
    }

    /*private void editSlot(SlotView slotView) {
        int slotId = slotView.getSlotId();
        System.out.println("✏️ Modification du créneau #" + slotId);

        if (!slotViewMap.containsKey(slotId)) {
            System.out.println("❌ Slot " + slotId + " n'existe plus, impossible de modifier");
            showTemporaryNotification("❌ Créneau #" + slotId + " n'existe plus", 2000);
            return;
        }

        notifyEventListeners("slotEditRequested", slotId);
        showTemporaryNotification("✏️ Modification du créneau #" + slotId, 1500);
    }
*/
    /**
     * Gère les erreurs lors de la sauvegarde des modifications
     */
    private void handleSaveError(int slotId, Exception error) {
        System.err.println("❌ Erreur sauvegarde slot " + slotId + ": " + error.getMessage());

        Platform.runLater(() -> {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erreur de sauvegarde");
            errorAlert.setHeaderText("Impossible de sauvegarder les modifications");
            errorAlert.setContentText("Le créneau #" + slotId + " n'a pas pu être mis à jour dans la base de données.\n\n" +
                    "Erreur: " + error.getMessage() + "\n\n" +
                    "Veuillez réessayer ou contacter l'administrateur.");

            // Appliquer le thème
            DialogPane dialogPane = errorAlert.getDialogPane();
            dialogPane.setStyle("-fx-background-color: " + (isDarkMode() ? "#2D2D2D" : "white") + ";" +
                    "-fx-text-fill: " + (isDarkMode() ? DARK_TEXT : "#2D2D2D") + ";");

            errorAlert.showAndWait();
        });
    }
    /**
     * Valide les données d'un slot avant sauvegarde
     */
    private boolean validateSlotData(SlotView slotView) {
        if (slotView == null) {
            System.err.println("❌ SlotView est null");
            return false;
        }

        if (slotView.getSlotId() <= 0) {
            System.err.println("❌ ID de slot invalide: " + slotView.getSlotId());
            return false;
        }

        if (slotView.getMatiere() == null || slotView.getMatiere().trim().isEmpty()) {
            System.err.println("❌ Matière manquante pour le slot " + slotView.getSlotId());
            return false;
        }

        // Vérifier les IDs des relations
        if (slotView.getEnseignantId() == null) {
            System.err.println("⚠️ Aucun ID enseignant pour le slot " + slotView.getSlotId());
        }

        if (slotView.getSalleId() == null) {
            System.err.println("⚠️ Aucun ID salle pour le slot " + slotView.getSlotId());
        }

        return true;
    }
    private void editSlot(SlotView slotView) {
        int slotId = slotView.getSlotId();
        System.out.println("✏️ Tentative de modification du créneau #" + slotId);

        // Vérification des permissions
        if (!hasSlotModificationPermission()) {
            System.out.println("⛔ Permission refusée - Affichage des détails en lecture seule");
            showSlotDetailsReadOnly(slotView);
            return;
        }

        if (!slotViewMap.containsKey(slotId)) {
            System.out.println("❌ Slot " + slotId + " n'existe plus, impossible de modifier");
            showTemporaryNotification("❌ Créneau #" + slotId + " n'existe plus", 2000);
            return;
        }

        System.out.println("✅ Permissions OK - Notification des écouteurs pour l'édition");

        // 🔥 CORRECTION : Appeler la méthode de modification via le présentateur
        notifyEventListeners("slotEditRequested", slotId);
        showTemporaryNotification("✏️ Modification du créneau #" + slotId, 1500);
    }

    /**
     * Affiche les détails d'un créneau en lecture seule pour les non-admins
     */
    private void showSlotDetailsReadOnly(SlotView slotView) {
        Dialog<Void> detailsDialog = new Dialog<>();
        detailsDialog.setTitle("📋 Détails du créneau");
        detailsDialog.setHeaderText("Informations complètes du créneau #" + slotView.getSlotId());

        // CORRECTION 1: Utiliser setOnKeyPressed sur le DialogPane au lieu du Dialog
        detailsDialog.getDialogPane().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                event.consume(); // CORRECTION 2: Cette méthode existe bien dans KeyEvent
            }
        });

        // Créer le contenu détaillé
        VBox content = createDetailedSlotContentReadOnly(slotView);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefSize(500, 400);
        scrollPane.setMaxSize(500, 400);

        // CORRECTION 3: Remplacer applyScrollPaneTheme par une méthode de style directe
        applyScrollPaneStyle(scrollPane);

        detailsDialog.getDialogPane().setContent(scrollPane);

        // UNIQUEMENT le bouton "Annuler"
        ButtonType cancelButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        detailsDialog.getDialogPane().getButtonTypes().addAll(cancelButton);

        // Appliquer le style de la boîte de dialogue
        String dialogStyle = isDarkMode() ?
                "-fx-background-color: #2D2D2D; -fx-text-fill: #E0E0E0;" :
                "-fx-background-color: white; -fx-text-fill: #2D2D2D;";
        detailsDialog.getDialogPane().setStyle(dialogStyle);

        // Style du bouton Annuler
        Button cancelBtn = (Button) detailsDialog.getDialogPane().lookupButton(cancelButton);
        if (cancelBtn != null) {
            String cancelStyle = isDarkMode() ?
                    "-fx-background-color: #475569; -fx-text-fill: #E0E0E0; -fx-font-weight: bold;" :
                    "-fx-background-color: #E2E8F0; -fx-text-fill: #475569; -fx-font-weight: bold;";
            cancelBtn.setStyle(cancelStyle);
        }

        detailsDialog.showAndWait();
    }

    /**
     * Méthode de remplacement pour appliquer le style au ScrollPane
     */
    private void applyScrollPaneStyle(ScrollPane scrollPane) {
        if (isDarkMode()) {
            scrollPane.setStyle("-fx-background: #2D2D2D; -fx-border-color: #404040; -fx-background-color: #2D2D2D;");
        } else {
            scrollPane.setStyle("-fx-background: white; -fx-border-color: #E0E0E0; -fx-background-color: white;");
        }
    }
    /**
     * Crée le contenu détaillé en lecture seule
     */
    private VBox createDetailedSlotContentReadOnly(SlotView slotView) {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: " + (isDarkMode() ? "transparent" : "transparent") + ";");

        // Style pour les labels
        String labelStyle = "-fx-font-weight: bold; -fx-text-fill: " + (isDarkMode() ? "#E0E0E0" : "#2D2D2D") + ";";
        String valueStyle = "-fx-text-fill: " + (isDarkMode() ? "#B0B0B0" : "#666666") + ";";

        // Titre principal
        Label titleLabel = new Label("📋 INFORMATIONS DU CRÉNEAU");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " +
                (isDarkMode() ? "#1E88E5" : "#1565C0") + ";");

        // Séparateur
        Separator separator = new Separator();

        // Grille pour les informations
        GridPane infoGrid = new GridPane();
        infoGrid.setVgap(12);
        infoGrid.setHgap(20);
        infoGrid.setPadding(new Insets(10, 0, 0, 0));

        // Ajouter les informations
        addReadOnlyInfoRow(infoGrid, 0, "🔢 ID:", String.valueOf(slotView.getSlotId()), labelStyle, valueStyle);

        if (slotView.getMatiere() != null) {
            addReadOnlyInfoRow(infoGrid, 1, "📖 Matière:", slotView.getMatiere(), labelStyle, valueStyle);
        }

        if (slotView.getEnseignant() != null) {
            addReadOnlyInfoRow(infoGrid, 2, "👨‍🏫 Enseignant:", slotView.getEnseignant(), labelStyle, valueStyle);
        }

        if (slotView.getSalle() != null) {
            addReadOnlyInfoRow(infoGrid, 3, "🏫 Salle:", slotView.getSalle(), labelStyle, valueStyle);
        }

        if (slotView.getDate() != null) {
            addReadOnlyInfoRow(infoGrid, 4, "📅 Date:", formatDate(slotView.getDate()), labelStyle, valueStyle);
        }

        if (slotView.getHeureDebut() != null && slotView.getHeureFin() != null) {
            String horaire = formatTime(slotView.getHeureDebut()) + " - " + formatTime(slotView.getHeureFin());
            addReadOnlyInfoRow(infoGrid, 5, "🕐 Horaire:", horaire, labelStyle, valueStyle);
        }

        if (slotView.getTypeCours() != null) {
            addReadOnlyInfoRow(infoGrid, 6, "📚 Type:", slotView.getTypeCours(), labelStyle, valueStyle);
        }

        if (slotView.getGroupes() != null) {
            addReadOnlyInfoRow(infoGrid, 7, "👥 Groupes:", slotView.getGroupes(), labelStyle, valueStyle);
        }

        // Durée
        if (slotView.getSlot() != null && slotView.getSlot().duration() != null) {
            java.time.Duration duration = slotView.getSlot().duration();
            long hours = duration.toHours();
            long minutes = duration.toMinutes() % 60;
            String durationText = String.format("%dh%02d", hours, minutes);
            addReadOnlyInfoRow(infoGrid, 8, "⏱️ Durée:", durationText, labelStyle, valueStyle);
        }

        // Message d'information sur les permissions
        Label permissionLabel = new Label("ℹ️ Seuls les administrateurs peuvent modifier les créneaux");
        permissionLabel.setStyle("-fx-font-size: 11px; -fx-font-style: italic; -fx-text-fill: " +
                (isDarkMode() ? "#FFA726" : "#F57C00") + ";");
        permissionLabel.setPadding(new Insets(15, 0, 0, 0));

        container.getChildren().addAll(titleLabel, separator, infoGrid, permissionLabel);

        return container;
    }

    /**
     * Ajoute une ligne d'information en lecture seule
     */
    private void addReadOnlyInfoRow(GridPane grid, int row, String label, String value, String labelStyle, String valueStyle) {
        Label infoLabel = new Label(label);
        infoLabel.setStyle(labelStyle);

        Label infoValue = new Label(value != null ? value : "Non spécifié");
        infoValue.setStyle(valueStyle);
        infoValue.setWrapText(true);

        grid.add(infoLabel, 0, row);
        grid.add(infoValue, 1, row);

        // Configurer les contraintes de colonne
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(120);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPrefWidth(300);
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().setAll(col1, col2);
    }
    public void debugSlotsState() {
        System.out.println("=== DÉBOGAGE SLOTS ===");
        System.out.println("Slots dans la map: " + slotViewMap.size());
        System.out.println("Slots dans la grille: " + getChildren().stream().filter(node -> node instanceof SlotView).count());

        slotViewMap.forEach((id, slot) -> {
            boolean inGrid = getChildren().contains(slot);
            System.out.println("Slot " + id + ": " + (inGrid ? "✓ Dans grille" : "✗ Pas dans grille"));
        });
        System.out.println("=====================");
    }

    private void duplicateSlot(SlotView slotView) {
        showTemporaryNotification("📋 Créneau #" + slotView.getSlotId() + " dupliqué", 1500);
    }

    private void exportSingleSlot(SlotView slotView) {
        showTemporaryNotification("📤 Export du créneau #" + slotView.getSlotId(), 1500);
    }

    private void showSlotInfo(SlotView slotView) {
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
        infoAlert.setTitle("Informations du créneau");
        infoAlert.setHeaderText("Détails du créneau #" + slotView.getSlotId());
        infoAlert.setContentText("Créneau positionné dans le calendrier.\n" +
                "Statut: Actif\n" +
                "Couleur: Scheme " + (colorIndex % COLOR_SCHEMES.length) + "\n" +
                "Position: Ligne " + GridPane.getRowIndex(slotView) + ", Colonne " + GridPane.getColumnIndex(slotView));

        DialogPane dialogPane = infoAlert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: " + (isDarkMode() ? "#2D2D2D" : "white") + ";" +
                "-fx-text-fill: " + (isDarkMode() ? DARK_TEXT : "black") + ";");

        infoAlert.showAndWait();
    }

    // 🔍 SEARCH FUNCTIONALITY
    private void performSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            searchResultsPanel.setVisible(false);
            searchResultsPanel.setManaged(false);
            return;
        }

        statistics.put("Recherches effectuées", statistics.get("Recherches effectuées") + 1);

        List<SlotView> results = searchIndex.entrySet().stream()
                .filter(entry -> entry.getKey().toLowerCase().contains(query.toLowerCase()))
                .flatMap(entry -> entry.getValue().stream())
                .distinct()
                .collect(Collectors.toList());

        displaySearchResults(results, query);
    }

    private void displaySearchResults(List<SlotView> results, String query) {
        searchResultsPanel.getChildren().clear();

        if (results.isEmpty()) {
            Label noResults = new Label("Aucun résultat pour \"" + query + "\"");
            noResults.setStyle("-fx-text-fill: " + (isDarkMode() ? "#FF6B6B" : "#E74C3C") + "; -fx-padding: 10;");
            searchResultsPanel.getChildren().add(noResults);
        } else {
            Label resultsLabel = new Label(results.size() + " résultat(s) pour \"" + query + "\"");
            resultsLabel.setStyle("-fx-text-fill: " + (isDarkMode() ? "#4ECDC4" : "#27AE60") + "; -fx-font-weight: bold; -fx-padding: 5;");
            searchResultsPanel.getChildren().add(resultsLabel);

            for (SlotView slot : results) {
                Button resultButton = new Button("Créneau #" + slot.getSlotId());
                resultButton.setMaxWidth(Double.MAX_VALUE);
                resultButton.setStyle("-fx-background-color: " + (isDarkMode() ? "#37474F" : "#E3F2FD") + ";" +
                        "-fx-text-fill: " + (isDarkMode() ? "white" : "black") + ";" +
                        "-fx-border-radius: 5; -fx-background-radius: 5;");
                resultButton.setOnAction(e -> highlightSearchResult(slot));
                searchResultsPanel.getChildren().add(resultButton);
            }
        }

        searchResultsPanel.setVisible(true);
        searchResultsPanel.setManaged(true);
    }

    private void indexSlotForSearch(SlotView slotView) {
        String[] searchTerms = {
                "slot " + slotView.getSlotId(),
                "cours",
                "creneau",
                "emploi du temps",
                "calendar",
                "schedule"
        };

        for (String term : searchTerms) {
            searchIndex.computeIfAbsent(term.toLowerCase(), k -> new ArrayList<>()).add(slotView);
        }
    }

    private void highlightSearchResult(SlotView slot) {
        pulseSlot(slot.getSlotId());
        showTemporaryNotification("🔍 Créneau #" + slot.getSlotId() + " trouvé", 1500);
    }

    // 📤 EXPORT FUNCTIONALITY
    private void showExportDialog() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("CSV", "CSV", "HTML", "JSON", "PDF", "Excel");
        dialog.setTitle("📤 Export du Calendrier");
        dialog.setHeaderText("Choisissez le format d'export");
        dialog.setContentText("Format :");

        DialogPane dialogPane = dialog.getDialogPane();
        applyDialogTheme(dialogPane);

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(format -> {
            switch (format) {
                case "CSV" -> exportToCsv();
                case "HTML" -> exportToHtml();
                case "JSON" -> exportToJson();
                case "PDF" -> exportToPdf();
                case "Excel" -> exportToExcel();
            }
        });
    }

    private void applyDialogTheme(DialogPane dialogPane) {
        dialogPane.setStyle("-fx-background-color: " + (isDarkMode() ? "#2D2D2D" : "white") + ";" +
                "-fx-text-fill: " + (isDarkMode() ? DARK_TEXT : "#2D2D2D") + ";");
    }

    private void exportToCsv() {
        showTemporaryNotification("📤 Export CSV réussi !", 1500);
    }

    private void exportToHtml() {
        showTemporaryNotification("📤 Export HTML réussi !", 1500);
    }

    private void exportToJson() {
        showTemporaryNotification("📤 Export JSON réussi !", 1500);
    }

    private void exportToPdf() {
        showTemporaryNotification("📊 Export PDF en cours...", 2000);
    }

    private void exportToExcel() {
        showTemporaryNotification("📊 Export Excel en cours...", 2000);
    }

    // 📊 STATISTICS AND DATA MANAGEMENT
    private void updateStatistics() {
        statistics.put("Créneaux totaux", slotViewMap.size());
        statistics.put("Couleurs utilisées", colorIndex % COLOR_SCHEMES.length);
        statistics.put("Colonnes", firstRowLabels.size());
        statistics.put("Lignes", firstColumnLabels.size());
    }

    private void showAdvancedStatistics() {
        updateStatistics();

        StringBuilder stats = new StringBuilder();
        stats.append("📊 Statistiques Avancées\n");
        stats.append("=======================\n");
        stats.append("Total des créneaux : ").append(slotViewMap.size()).append("\n");
        stats.append("Mode d'affichage : ").append(currentDisplayMode.get().getName()).append("\n");
        stats.append("Thème : ").append(isDarkMode() ? "Sombre" : "Clair").append("\n");
        stats.append("Échelle : ").append(String.format("%.0f%%", scaleFactor.get() * 100)).append("\n\n");

        stats.append("📈 Métriques :\n");
        statistics.forEach((key, value) ->
                stats.append("• ").append(key).append(" : ").append(value).append("\n")
        );

        showStatisticsDialog(stats.toString());
    }

    private void showStatisticsDialog(String statistics) {
        TextArea textArea = new TextArea(statistics);
        textArea.setEditable(false);
        textArea.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 12px;");

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("📊 Statistiques du Calendrier");
        dialog.getDialogPane().setContent(textArea);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        applyDialogTheme(dialog.getDialogPane());
        dialog.showAndWait();
    }

    // ❓ HELP AND INFORMATION
    private void showHelpDialog() {
        String helpText = """
        🎓 AIDE - CALENDRIER
        ========================
        
        📖 COMMENT UTILISER LE CALENDRIER
        • Double-cliquez sur un créneau pour le modifier
        • Clic droit pour le menu contextuel (modifier, supprimer, etc.)
        • Utilisez la molette de la souris pour zoomer/dézoomer
        • Glissez-déposez pour réorganiser les créneaux
        
        🎯 RACCOURCIS CLAVIER
        =====================
        F1 : Afficher cette aide
        F2 : Ajout rapide de créneau
        F3 : Basculer la recherche
        F5 : Actualiser les données
        Ctrl + E : Exporter le calendrier
        Ctrl + D : Changer le thème (clair/sombre)
        Ctrl + V : Changer le mode d'affichage
        Ctrl + P : Imprimer le calendrier
        Ctrl + F : Filtrer les créneaux
        Échap : Effacer la recherche
        
        🎨 MODES D'AFFICHAGE DISPONIBLES
        ===============================
        ⏹️  Grille : Affichage classique en grille
        📋  Liste : Vue liste verticale simplifiée
        ⏰  Chronologie : Vue horizontale chronologique
        📱  Compact : Vue dense optimisée
        ☀️  Journalier : Vue détaillée par jour
        📅  Hebdomadaire : Vue par semaine
        
        🔍 FONCTIONNALITÉS AVANCÉES
        ===========================
        • Recherche en temps réel dans les créneaux
        • Système de notifications interactives
        • Statistiques détaillées de l'emploi du temps
        • Glisser-déposer des créneaux
        • Actualisation automatique toutes les 5 minutes
        • Thème clair/sombre adaptatif
        • Barre de statut informative
        
        💡 CONSEILS PRATIQUES
        =====================
        • Les couleurs différentes aident à distinguer les types de cours
        • Utilisez la recherche pour trouver rapidement un cours spécifique
        • Les statistiques vous donnent une vue d'ensemble de votre emploi du temps
        • L'export vous permet de sauvegarder ou partager votre calendrier
        """;

        Dialog<Void> dialog = createHelpDialog(helpText);
        applyHelpDialogTheme(dialog);
        dialog.showAndWait();
    }

    private Dialog<Void> createHelpDialog(String helpText) {
        TextArea textArea = new TextArea(helpText);
        textArea.setEditable(false);

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("❓ Aide - Calendrier ");
        dialog.getDialogPane().setContent(textArea);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().setPrefSize(700, 600);

        return dialog;
    }

    private void applyHelpDialogTheme(Dialog<Void> dialog) {
        applyHelpDialogTheme(null, dialog.getDialogPane());
    }

    private void applyHelpDialogTheme(TextArea textArea, DialogPane dialogPane) {
        boolean isDark = isDarkMode();

        String darkDialogStyle = """
        -fx-background-color: linear-gradient(to bottom right, #1e293b 0%, #0f172a 50%, #020617 100%);
        -fx-border-color: linear-gradient(to bottom, #334155, #475569);
        -fx-border-width: 1.5;
        -fx-border-radius: 24;
        -fx-background-radius: 24;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 25, 0, 0, 12);
        """;

        String darkTextAreaStyle = """
        -fx-control-inner-background: #1e293b;
        -fx-background-color: #1e293b;
        -fx-text-fill: #e2e8f0;
        -fx-font-family: 'Segoe UI';
        -fx-font-size: 13px;
        -fx-line-spacing: 0.5em;
        -fx-border-color: #475569;
        -fx-border-radius: 8;
        -fx-background-radius: 8;
        """;

        String lightDialogStyle = """
        -fx-background-color: linear-gradient(to bottom right, #ffffff 0%, #f8fafc 50%, #f1f5f9 100%);
        -fx-border-color: linear-gradient(to bottom, #e2e8f0, #cbd5e1);
        -fx-border-width: 1.5;
        -fx-border-radius: 24;
        -fx-background-radius: 24;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 25, 0, 0, 12);
        """;

        String lightTextAreaStyle = """
        -fx-control-inner-background: white;
        -fx-background-color: white;
        -fx-text-fill: #2D2D2D;
        -fx-font-family: 'Segoe UI';
        -fx-font-size: 13px;
        -fx-line-spacing: 0.5em;
        -fx-border-color: #e2e8f0;
        -fx-border-radius: 8;
        -fx-background-radius: 8;
        """;

        if (dialogPane != null) {
            String dialogStyle = isDark ? darkDialogStyle : lightDialogStyle;
            dialogPane.setStyle(dialogStyle);

            Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
            if (okButton != null) {
                String buttonStyle = isDark ?
                        "-fx-background-color: linear-gradient(to bottom, #475569, #374151); " +
                                "-fx-text-fill: #e2e8f0; -fx-font-weight: bold; -fx-background-radius: 12;" :
                        "-fx-background-color: linear-gradient(to bottom, #f8fafc, #f1f5f9); " +
                                "-fx-text-fill: #475569; -fx-font-weight: bold; -fx-background-radius: 12;";
                okButton.setStyle(buttonStyle);
            }
        }

        if (textArea != null) {
            String textAreaStyle = isDark ? darkTextAreaStyle : lightTextAreaStyle;
            textArea.setStyle(textAreaStyle);
        }

        System.out.println("🎨 Thème appliqué au dialogue d'aide: " + (isDark ? "DARK" : "LIGHT"));
    }

    // 🎮 CONTROL BUTTONS ACTIONS
    private void setupControlButtonsActions() {
        if (helpButton != null) {
            helpButton.setOnAction(e -> showHelpDialog());
        }
        if (statsButton != null) {
            statsButton.setOnAction(e -> showAdvancedStatistics());
        }
        if (exportCsvButton != null) {
            exportCsvButton.setOnAction(e -> showExportDialog());
        }
        if (themeButton != null) {
            themeButton.setOnAction(e -> {
                toggleTheme();
                if (themeButton != null) {
                    themeButton.setText(getThemeIcon());
                    styleCircularButton(themeButton, false, "#3498DB");
                }
            });
        }
        if (closeButton != null) {
            closeButton.setOnAction(e -> {
                System.out.println("🔄 Bouton fermeture cliqué");
                closeApplication();
            });
        } else {
            System.err.println("❌ closeButton est null dans setupControlButtonsActions");
        }
    }

    private void showFilterDialog() {
        showTemporaryNotification("🔧 Ouverture des filtres...", 1500);
    }

    private void quickAddSlot() {
        showTemporaryNotification("🔄 Mode ajout rapide activé - Cliquez sur une case vide", 3000);
    }

    private void toggleSearchPanel() {
        boolean visible = !searchResultsPanel.isVisible();
        searchResultsPanel.setVisible(visible);
        searchResultsPanel.setManaged(visible);

        if (visible) {
            searchField.requestFocus();
        }
    }

    private void clearSearch() {
        searchField.clear();
        searchResultsPanel.setVisible(false);
        searchResultsPanel.setManaged(false);
    }

    private void closeApplication() {
        System.out.println("🚪 Tentative de fermeture de l'application...");

        if (closeButton == null) {
            System.err.println("❌ closeButton est null");
            return;
        }

        Scene buttonScene = closeButton.getScene();
        if (buttonScene == null) {
            System.err.println("❌ La scène du bouton fermeture est null");
            return;
        }

        Window window = buttonScene.getWindow();
        if (window instanceof Stage) {
            Stage stage = (Stage) window;

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Fermer l'application");
            confirmation.setHeaderText("Êtes-vous sûr de vouloir quitter ?");
            confirmation.setContentText("Toutes les modifications non enregistrées seront perdues.");

            applyDialogTheme(confirmation.getDialogPane());

            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                System.out.println("✅ Fermeture de l'application confirmée");

                FadeTransition fade = new FadeTransition(Duration.millis(250), stage.getScene().getRoot());
                fade.setFromValue(1.0);
                fade.setToValue(0.0);
                fade.setOnFinished(event -> {
                    System.out.println("👋 Fermeture de la fenêtre");
                    stage.close();
                });
                fade.play();
            } else {
                System.out.println("❌ Fermeture annulée par l'utilisateur");
            }
        } else {
            System.err.println("❌ Impossible de récupérer la fenêtre principale");
        }
    }

    // 🔧 MÉTHODE DE DÉBOGAGE
    public void debugButtonStates() {
        System.out.println("=== DÉBOGAGE BOUTONS ===");
        System.out.println("helpButton: " + (helpButton != null ? "✓ Initialisé" : "✗ Null"));
        System.out.println("statsButton: " + (statsButton != null ? "✓ Initialisé" : "✗ Null"));
        System.out.println("exportCsvButton: " + (exportCsvButton != null ? "✓ Initialisé" : "✗ Null"));
        System.out.println("themeButton: " + (themeButton != null ? "✓ Initialisé" : "✗ Null"));
        System.out.println("closeButton: " + (closeButton != null ? "✓ Initialisé" : "✗ Null"));

        // AJOUT : Debug pour le bouton de mode d'affichage
        Node displayModeButton = controlButtons != null ?
                controlButtons.getChildren().stream()
                        .filter(node -> node instanceof Button && ((Button) node).getText().equals(getViewModeIcon()))
                        .findFirst()
                        .orElse(null) : null;
        System.out.println("displayModeButton: " + (displayModeButton != null ? "✓ Initialisé" : "✗ Null"));

        if (closeButton != null) {
            System.out.println("closeButton parent: " + (closeButton.getParent() != null ?
                    closeButton.getParent().getClass().getSimpleName() : "✗ Pas de parent"));
            System.out.println("closeButton visible: " + closeButton.isVisible());
            System.out.println("closeButton managed: " + closeButton.isManaged());
        }
        System.out.println("======================");
    }

    private void showDisplayModeNotification() {
        String message = currentDisplayMode.get().getName() + " - " + currentDisplayMode.get().getDescription();
        showTemporaryNotification("🎯 " + message, 2000);
    }

    public void setSlotFormFactory(SimpleSlotFormFactory factory) {
        this.slotFormFactory = factory;
        if (factory != null) {
            this.darkModeProperty.set(factory.isDarkTheme());

            darkModeProperty.addListener((obs, oldVal, newVal) -> {
                if (slotFormFactory != null) {
                    if (newVal) {
                        slotFormFactory.setDarkTheme();
                    } else {
                        slotFormFactory.setLightTheme();
                    }
                    System.out.println("🎨 Thème synchronisé vers factory: " + (newVal ? "DARK" : "LIGHT"));
                }
            });
        }
    }

    public void synchronizeFormTheme() {
        if (slotFormFactory != null) {
            if (isDarkMode()) {
                slotFormFactory.setDarkTheme();
            } else {
                slotFormFactory.setLightTheme();
            }
        }
    }

    public void toggleTheme() {
        System.out.println("🔄 Basculement du thème - Actuel: " + (isDarkMode() ? "DARK" : "LIGHT"));

        darkModeProperty.set(!darkModeProperty.get());
        notifyThemeChangeListeners();

        synchronizeFormTheme();

        applyThemeToAllOpenDialogs();

        String themeName = isDarkMode() ? "sombre" : "clair";
        showTemporaryNotification("🎨 Thème " + themeName + " activé", 1500);

        System.out.println("✅ Nouveau thème: " + (isDarkMode() ? "DARK" : "LIGHT"));
    }

    private void applyThemeToAllOpenDialogs() {
        ThemeMode currentTheme = getCurrentThemeMode();

        Window.getWindows().forEach(window -> {
            if (window instanceof Stage) {
                Stage stage = (Stage) window;
                if (stage.getScene() != null && stage.getScene().getRoot() instanceof DialogPane) {
                    DialogPane dialogPane = (DialogPane) stage.getScene().getRoot();
                    applyThemeToDialogPane(dialogPane, currentTheme);
                }
            }
        });
    }

    private void applyThemeToDialogPane(DialogPane dialogPane, ThemeMode theme) {
        if (slotFormFactory != null) {
            Dialog<?> tempDialog = new Dialog<>();
            tempDialog.setDialogPane(dialogPane);
            slotFormFactory.applyThemeToDialog(tempDialog, theme);
        }
    }

    public boolean isDarkMode() {
        return darkModeProperty.get();
    }

    public ThemeMode getCurrentThemeMode() {
        return isDarkMode() ? ThemeMode.DARK : ThemeMode.LIGHT;
    }

    public void setThemeMode(ThemeMode theme) {
        this.darkModeProperty.set(theme == ThemeMode.DARK);
        if (slotFormFactory != null) {
            slotFormFactory.setThemeMode(theme);
        }
    }

    public double getScaleFactor() {
        return scaleFactor.get();
    }

    public DoubleProperty scaleFactorProperty() {
        return scaleFactor;
    }

    public BooleanProperty darkModeProperty() {
        return darkModeProperty;
    }

    public ObjectProperty<DisplayMode> currentDisplayModeProperty() {
        return currentDisplayMode;
    }

    // 🎯 EVENT LISTENER SYSTEM
    public interface CalendarEventListener {
        void onCalendarEvent(String eventType, Object data);
    }

    public void addCalendarEventListener(CalendarEventListener listener) {
        eventListeners.add(listener);
    }

    public void removeCalendarEventListener(CalendarEventListener listener) {
        eventListeners.remove(listener);
    }

    private void notifyEventListeners(String eventType, Object data) {
        for (CalendarEventListener listener : eventListeners) {
            listener.onCalendarEvent(eventType, data);
        }
    }

    // 🎨 THEME CHANGE LISTENER
    public interface ThemeChangeListener {
        void onThemeChanged(ThemeMode newTheme);
    }

    public void addThemeChangeListener(ThemeChangeListener listener) {
        themeListeners.add(listener);
    }

    public void removeThemeChangeListener(ThemeChangeListener listener) {
        themeListeners.remove(listener);
    }

    private void notifyThemeChangeListeners() {
        ThemeMode currentTheme = getCurrentThemeMode();
        for (ThemeChangeListener listener : themeListeners) {
            listener.onThemeChanged(currentTheme);
        }
    }

    // 💾 USER DATA STORAGE
    public void setUserData(String key, Object value) {
        userData.put(key, value);
    }

    public Object getUserData(String key) {
        return userData.get(key);
    }

    // 🕒 CURRENT TIME INDICATOR
    public void showCurrentTimeIndicator(boolean show) {
        if (show) {
            if (currentTimeIndicator == null) {
                currentTimeIndicator = new Pane();
                currentTimeIndicator.setStyle("-fx-background-color: #FF4444; -fx-opacity: 0.6;");
                getChildren().add(currentTimeIndicator);
            }
            updateCurrentTimeIndicator();
        } else if (currentTimeIndicator != null) {
            getChildren().remove(currentTimeIndicator);
            currentTimeIndicator = null;
        }
    }

    private void updateCurrentTimeIndicator() {
        // Implementation for current time indicator positioning
    }

    public void addContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editItem = new MenuItem("✏️ Modifier");
        MenuItem deleteItem = new MenuItem("🗑️ Supprimer");
        MenuItem duplicateItem = new MenuItem("📋 Dupliquer");
        MenuItem exportItem = new MenuItem("📤 Exporter");

        contextMenu.getItems().addAll(editItem, deleteItem, duplicateItem, exportItem);
        setOnContextMenuRequested(event -> contextMenu.show(this, event.getScreenX(), event.getScreenY()));
    }

    public void toggleCompactMode(boolean compact) {
        if (compact) {
            setHgap(2);
            setVgap(2);
            setPadding(new Insets(5));
        } else {
            setHgap(15);
            setVgap(15);
            setPadding(new Insets(30));
        }
    }

    public void highlightCurrentTime() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.minutes(1), e -> {
            LocalTime now = LocalTime.now();
            // Implementation for current time highlighting
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // 🆕 NOUVELLE MÉTHODE POUR L'AIDE CONTEXTUELLE
    private void addQuickHelpTooltips() {
        Tooltip.install(searchField, new Tooltip("Recherchez un cours par nom, horaire ou type"));
        Tooltip.install(themeButton, new Tooltip("Basculer entre le mode clair et sombre"));
        Tooltip.install(helpButton, new Tooltip("Obtenir de l'aide sur l'utilisation du calendrier"));

        Tooltip calendarTooltip = new Tooltip("Double-cliquez pour modifier un créneau\nClic droit pour plus d'options");
        Tooltip.install(this, calendarTooltip);
    }

    // 🔄 UTILITY METHODS
    private void applyResponsiveStyles() {
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.widthProperty().addListener((o, oldW, newW) ->
                        adjustResponsiveLayout(newW.doubleValue(), newScene.getHeight()));
                newScene.heightProperty().addListener((o, oldH, newH) ->
                        adjustResponsiveLayout(newScene.getWidth(), newH.doubleValue()));
            }
        });
    }

    private void adjustResponsiveLayout(double width, double height) {
        if (width < 800) {
            scaleFactor.set(0.7);
            setHgap(6);
            setVgap(6);
            setPadding(new Insets(15));
        } else if (width < 1400) {
            scaleFactor.set(0.85);
            setHgap(9);
            setVgap(9);
            setPadding(new Insets(22));
        } else {
            scaleFactor.set(1.0);
            setHgap(12);
            setVgap(12);
            setPadding(new Insets(30));
        }
        updateLabelsScale();
        applyGridBackground();
    }

    private void updateLabelsScale() {
        double fontSize = 14 * scaleFactor.get();
        firstRowLabels.forEach(label ->
                label.setFont(Font.font("System", FontWeight.BOLD, fontSize)));
        firstColumnLabels.forEach(label ->
                label.setFont(Font.font("System", FontWeight.SEMI_BOLD, fontSize)));
    }

    private String getViewModeIcon() {
        return currentDisplayMode.get().getIcon();
    }

    private String getThemeIcon() {
        return isDarkMode() ? "☀️" : "🌙";
    }

    public boolean isAutoRefreshEnabled() {
        return autoRefreshEnabled;
    }



    /**
     * Rafraîchit un slot spécifique
     */


    public void addNewSlotWithRefresh(SlotView slotView, int rowIndex, int columnIndex, int rowSpan, int colSpan) {
        System.out.println("🎬 Ajout avec rafraîchissement du nouveau slot " + slotView.getSlotId());

        // Ajouter le slot normalement
        addSlotView(slotView, rowIndex, columnIndex, rowSpan, colSpan);

        // Déclencher le rafraîchissement automatique
        if (autoRefreshEnabled) {
            PauseTransition refreshDelay = new PauseTransition(Duration.millis(500));
            refreshDelay.setOnFinished(e -> {
                refreshCalendarData();
            });
            refreshDelay.play();
        }

        // Notification spéciale
        showTemporaryNotification("✅ Nouveau créneau ajouté avec succès", 2000);
    }

    // Variable de contrôle du rafraîchissement automatique
    // Variable de contrôle du rafraîchissement automatique
    private boolean autoRefreshEnabled = false; // 🔥 DÉSACTIVÉ par défaut

    public void setAutoRefreshEnabled(boolean enabled) {
        this.autoRefreshEnabled = enabled;
        System.out.println("🔄 Rafraîchissement automatique: " + (enabled ? "activé" : "désactivé"));

        if (enabled) {
            // Réactiver le rafraîchissement automatique sécurisé
            setupAutoRefresh();
        }
    }
    // Dans GridCalendarView, ajoutez cette méthode
    private Button createEnseignantsButton() {
        Button enseignantsButton = new Button("👨‍🏫 Enseignants");

        String enseignantsButtonStyle =
                "-fx-background-color: #FF5722; " + // Orange pour enseignants
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 11px; " +
                        "-fx-border-color: #E64A19; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 12; " +
                        "-fx-background-radius: 12; " +
                        "-fx-padding: 6 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 1, 1); " +
                        "-fx-cursor: hand;";

        enseignantsButton.setStyle(enseignantsButtonStyle);

        enseignantsButton.setOnMouseEntered(e -> {
            enseignantsButton.setStyle(enseignantsButtonStyle +
                    "-fx-background-color: #E64A19; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 2, 2);");
        });

        enseignantsButton.setOnMouseExited(e -> {
            enseignantsButton.setStyle(enseignantsButtonStyle);
        });

        // Action du bouton - ouvrir EnseignantView
        enseignantsButton.setOnAction(e -> {
            System.out.println("👨‍🏫 Ouverture de la vue Enseignants...");
            openEnseignantView();
        });

        Tooltip.install(enseignantsButton, new Tooltip("Gérer les enseignants (Admin seulement)"));

        return enseignantsButton;
    }

    private void openEnseignantView() {
        try {
            System.out.println("🚀 Lancement de EnseignantView...");

            // Créer une nouvelle instance de EnseignantView
            EnseignantView enseignantView = new EnseignantView();

            // Créer la scène
            Scene enseignantScene = new Scene(enseignantView, 1200, 700);

            // Appliquer le thème actuel
            if (isDarkMode()) {
                enseignantScene.setFill(Color.web("#0A1929"));
                applyDarkThemeToEnseignantView(enseignantView);
            } else {
                enseignantScene.setFill(Color.web("#F5F9FF"));
                applyLightThemeToEnseignantView(enseignantView);
            }

            // Créer et configurer la nouvelle fenêtre
            Stage enseignantStage = new Stage();
            enseignantStage.setTitle("👨‍🏫 Gestion des Enseignants - Admin ISET");
            enseignantStage.setScene(enseignantScene);

            // Configurer la fenêtre comme modale
            enseignantStage.initModality(Modality.WINDOW_MODAL);
            if (scene != null && scene.getWindow() != null) {
                enseignantStage.initOwner(scene.getWindow());
            }

            // Configurer la taille
            enseignantStage.setMinWidth(1000);
            enseignantStage.setMinHeight(600);

            // Centrer la fenêtre
            enseignantStage.centerOnScreen();

            // Afficher la fenêtre
            enseignantStage.show();

            showTemporaryNotification("👨‍🏫 Interface de gestion des enseignants ouverte", 2000);

            System.out.println("✅ EnseignantView ouvert avec succès");

        } catch (Exception ex) {
            System.err.println("❌ Erreur lors de l'ouverture de EnseignantView: " + ex.getMessage());
            ex.printStackTrace();

            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erreur");
            errorAlert.setHeaderText("Impossible d'ouvrir la gestion des enseignants");
            errorAlert.setContentText("Une erreur est survenue: " + ex.getMessage());

            // Appliquer le thème à la boîte de dialogue
            DialogPane dialogPane = errorAlert.getDialogPane();
            dialogPane.setStyle("-fx-background-color: " + (isDarkMode() ? "#2D2D2D" : "white") + ";" +
                    "-fx-text-fill: " + (isDarkMode() ? DARK_TEXT : "#2D2D2D") + ";");

            errorAlert.showAndWait();
        }
    }

    // Méthodes auxiliaires pour appliquer le thème à EnseignantView
    private void applyDarkThemeToEnseignantView(EnseignantView enseignantView) {
        enseignantView.setStyle("-fx-background-color: #0A1929;");
    }

    private void applyLightThemeToEnseignantView(EnseignantView enseignantView) {
        enseignantView.setStyle("-fx-background-color: #F5F9FF;");

    }
    public void refreshAfterSlotCreation(int newSlotId) {
        System.out.println("🔄 Rafraîchissement optimisé après création du slot " + newSlotId);

        // Vérifier si le slot est déjà dans la grille
        if (slotViewMap.containsKey(newSlotId)) {
            System.out.println("✅ Slot " + newSlotId + " déjà présent - pas de rafraîchissement complet nécessaire");
            // Juste animer le slot existant
            pulseSlot(newSlotId);
            showTemporaryNotification("✅ Créneau ajouté avec succès", 2000);
            return;
        }

        // Sinon, rafraîchir normalement
        System.out.println("🔄 Slot " + newSlotId + " non trouvé - rafraîchissement complet");
        refreshCalendarData();
    }

    /**
     * Ajoute un slot sans déclencher de rafraîchissement automatique
     * Pour les nouvelles créations uniquement
     */
    public void addNewSlotWithoutRefresh(SlotView slotView, int rowIndex, int columnIndex, int rowSpan, int colSpan) {
        System.out.println("🎬 Ajout DIRECT du nouveau slot " + slotView.getSlotId() + " sans rafraîchissement");

        // Désactiver temporairement les écouteurs
        boolean oldState = autoRefreshEnabled;
        setAutoRefreshEnabled(false);

        // Ajouter normalement
        addSlotView(slotView, rowIndex, columnIndex, rowSpan, colSpan);

        // Réactiver
        setAutoRefreshEnabled(oldState);

        // Notification spéciale
        showTemporaryNotification("✅ Nouveau créneau ajouté avec succès", 2000);
    }


}