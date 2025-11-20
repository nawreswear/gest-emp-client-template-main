package tn.iset.m2glnt.client.viewer.presenter.dialog;
import javafx.application.Platform;
import tn.iset.m2glnt.client.model.Salle;
import tn.iset.m2glnt.client.service.dao.EnseignantService;
import tn.iset.m2glnt.client.service.dao.EtudiantService;
import tn.iset.m2glnt.client.model.Enseignant;

// PAR
import tn.iset.m2glnt.client.service.SalleRestService;
import tn.iset.m2glnt.client.model.Etudiant;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
import tn.iset.m2glnt.client.model.Slot;
import tn.iset.m2glnt.client.viewer.CalendarApp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Factory pour créer des boîtes de dialogue permettant de créer ou éditer un créneau (Slot).
 * Version complète avec scroll et tous les attributs du slot.
 */
public class SimpleSlotFormFactory implements SlotFormFactory {

    // Constantes pour animations et styles
    private static final Duration FADE_DURATION = Duration.millis(400);
    private static final Duration SCALE_DURATION = Duration.millis(500);
    private static final Duration BUTTON_ANIMATION_DURATION = Duration.millis(200);
    private static final double SCALE_START = 0.92;
    private static final double SCALE_END = 1.0;

    // Hauteurs maximales pour le scroll
    private static final double MAX_DIALOG_HEIGHT = 700;
    private static final double MAX_CONTENT_HEIGHT = 500;

    public enum ThemeMode {
        LIGHT, DARK
    }

    // ==================== LIGHT MODE STYLES ====================
    private static final String LIGHT_DIALOG_STYLE = """
    -fx-background-color: linear-gradient(to bottom right, #ffffff 0%, #f8fafc 50%, #f1f5f9 100%);
    -fx-border-color: linear-gradient(to bottom, #e2e8f0, #cbd5e1);
    -fx-border-width: 1.5;
    -fx-border-radius: 24;
    -fx-background-radius: 24;
    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 25, 0, 0, 12);
    """;

    private static final String LIGHT_SCROLL_STYLE = """
        -fx-background: transparent;
        -fx-background-color: transparent;
        -fx-border-color: transparent;
        """;

    private static final String LIGHT_SCROLL_BAR_STYLE = """
        -fx-background-color: transparent;
        -fx-background: transparent;
        """;

    private static final String LIGHT_SCROLL_THUMB_STYLE = """
        -fx-background-color: linear-gradient(to bottom, #cbd5e1, #94a3b8);
        -fx-background-radius: 8;
        """;

    private static final String LIGHT_FIELD_STYLE = """
        -fx-background-color: linear-gradient(to bottom, #ffffff, #fafbfc);
        -fx-background-radius: 14;
        -fx-border-radius: 14;
        -fx-border-color: #e2e8f0;
        -fx-border-width: 1.8;
        -fx-padding: 14 16;
        -fx-font-size: 14;
        -fx-text-fill: #1e293b;
        -fx-font-family: 'Segoe UI', 'System', sans-serif;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);
        """;

    private static final String LIGHT_FIELD_FOCUSED_STYLE = """
        -fx-background-color: linear-gradient(to bottom, #ffffff, #fafbfc);
        -fx-background-radius: 14;
        -fx-border-radius: 14;
        -fx-border-color: linear-gradient(to right, #3b82f6, #6366f1);
        -fx-border-width: 2.2;
        -fx-padding: 14 16;
        -fx-font-size: 14;
        -fx-text-fill: #1e293b;
        -fx-font-family: 'Segoe UI', 'System', sans-serif;
        -fx-effect: dropshadow(gaussian, rgba(59,130,246,0.25), 15, 0, 0, 6);
        """;

    private static final String LIGHT_CONFIRM_BUTTON_STYLE = """
        -fx-background-color: linear-gradient(to bottom right, #3b82f6, #6366f1, #8b5cf6);
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-font-size: 14;
        -fx-font-family: 'Segoe UI', 'System', sans-serif;
        -fx-background-radius: 12;
        -fx-cursor: hand;
        -fx-padding: 14 32;
        -fx-effect: dropshadow(gaussian, rgba(59,130,246,0.35), 12, 0, 0, 4);
        -fx-border-color: linear-gradient(to bottom, #60a5fa, #3b82f6);
        -fx-border-width: 1;
        -fx-border-radius: 12;
        """;

    private static final String LIGHT_CONFIRM_BUTTON_HOVER_STYLE = """
        -fx-background-color: linear-gradient(to bottom right, #2563eb, #4f46e5, #7c3aed);
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-font-size: 14;
        -fx-font-family: 'Segoe UI', 'System', sans-serif;
        -fx-background-radius: 12;
        -fx-cursor: hand;
        -fx-padding: 14 32;
        -fx-effect: dropshadow(gaussian, rgba(59,130,246,0.5), 18, 0, 0, 8);
        -fx-border-color: linear-gradient(to bottom, #93c5fd, #60a5fa);
        -fx-border-width: 1;
        -fx-border-radius: 12;
        """;

    private static final String LIGHT_DELETE_BUTTON_STYLE = """
        -fx-background-color: linear-gradient(to bottom right, #ef4444, #dc2626, #b91c1c);
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-font-size: 14;
        -fx-font-family: 'Segoe UI', 'System', sans-serif;
        -fx-background-radius: 12;
        -fx-cursor: hand;
        -fx-padding: 14 32;
        -fx-effect: dropshadow(gaussian, rgba(239,68,68,0.35), 12, 0, 0, 4);
        -fx-border-color: linear-gradient(to bottom, #fca5a5, #ef4444);
        -fx-border-width: 1;
        -fx-border-radius: 12;
        """;

    private static final String LIGHT_DELETE_BUTTON_HOVER_STYLE = """
        -fx-background-color: linear-gradient(to bottom right, #dc2626, #b91c1c, #991b1b);
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-font-size: 14;
        -fx-font-family: 'Segoe UI', 'System', sans-serif;
        -fx-background-radius: 12;
        -fx-cursor: hand;
        -fx-padding: 14 32;
        -fx-effect: dropshadow(gaussian, rgba(239,68,68,0.5), 18, 0, 0, 8);
        -fx-border-color: linear-gradient(to bottom, #fecaca, #fca5a5);
        -fx-border-width: 1;
        -fx-border-radius: 12;
        """;

    private static final String LIGHT_CANCEL_BUTTON_STYLE = """
        -fx-background-color: linear-gradient(to bottom, #f8fafc, #f1f5f9);
        -fx-text-fill: #475569;
        -fx-font-weight: bold;
        -fx-font-size: 14;
        -fx-font-family: 'Segoe UI', 'System', sans-serif;
        -fx-background-radius: 12;
        -fx-cursor: hand;
        -fx-padding: 14 32;
        -fx-border-color: linear-gradient(to bottom, #cbd5e1, #94a3b8);
        -fx-border-width: 1.8;
        -fx-border-radius: 12;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 3);
        """;

    private static final String LIGHT_CANCEL_BUTTON_HOVER_STYLE = """
        -fx-background-color: linear-gradient(to bottom, #e2e8f0, #cbd5e1);
        -fx-text-fill: #334155;
        -fx-font-weight: bold;
        -fx-font-size: 14;
        -fx-font-family: 'Segoe UI', 'System', sans-serif;
        -fx-background-radius: 12;
        -fx-cursor: hand;
        -fx-padding: 14 32;
        -fx-border-color: linear-gradient(to bottom, #94a3b8, #64748b);
        -fx-border-width: 1.8;
        -fx-border-radius: 12;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 10, 0, 0, 4);
        """;

    private static final String LIGHT_LABEL_STYLE = """
        -fx-text-fill: #475569;
        -fx-font-weight: 600;
        -fx-font-size: 14;
        -fx-font-family: 'Segoe UI', 'System', sans-serif;
        -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.8), 0, 0, 0, 1);
        """;

    private static final String LIGHT_TITLE_STYLE = """
        -fx-text-fill: #0f172a;
        -fx-font-weight: 700;
        -fx-font-size: 20;
        -fx-font-family: 'Segoe UI', 'System', sans-serif;
        -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.9), 0, 0, 0, 1);
        """;

    private static final String LIGHT_SUBTITLE_STYLE = """
        -fx-text-fill: #64748b;
        -fx-font-size: 13;
        -fx-font-family: 'Segoe UI', 'System', sans-serif;
        -fx-font-style: italic;
        -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.8), 0, 0, 0, 1);
        """;

    private static final String LIGHT_ICON_STYLE = """
        -fx-fill: linear-gradient(to bottom, #64748b, #475569);
        -fx-font-size: 16;
        -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.8), 0, 0, 0, 1);
        """;

    private static final String LIGHT_FIELD_ERROR_STYLE = """
        -fx-background-color: linear-gradient(to bottom, #fef2f2, #fecaca);
        -fx-background-radius: 14;
        -fx-border-radius: 14;
        -fx-border-color: linear-gradient(to bottom, #f87171, #ef4444);
        -fx-border-width: 2.2;
        -fx-padding: 14 16;
        -fx-font-size: 14;
        -fx-text-fill: #7f1d1d;
        -fx-font-family: 'Segoe UI', 'System', sans-serif;
        -fx-effect: dropshadow(gaussian, rgba(248,113,113,0.25), 15, 0, 0, 6);
        """;

    private static final String LIGHT_TOOLTIP_STYLE = """
        -fx-background-color: linear-gradient(to bottom, #ffffff, #f8fafc);
        -fx-text-fill: #475569;
        -fx-font-size: 12;
        -fx-font-family: 'Segoe UI', 'System', sans-serif;
        -fx-background-radius: 8;
        -fx-border-color: #e2e8f0;
        -fx-border-width: 1;
        -fx-border-radius: 8;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 3);
        -fx-padding: 8 12;
        """;

    // ==================== DARK MODE STYLES ====================
    private static final String DARK_DIALOG_STYLE = """
    -fx-background-color: linear-gradient(to bottom right, #1e293b 0%, #0f172a 50%, #020617 100%);
    -fx-border-color: linear-gradient(to bottom, #334155, #475569);
    -fx-border-width: 1.5;
    -fx-border-radius: 24;
    -fx-background-radius: 24;
    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 25, 0, 0, 12);
    """;

    private static final String DARK_SCROLL_STYLE = """
        -fx-background: transparent;
        -fx-background-color: transparent;
        -fx-border-color: transparent;
        """;

    private static final String DARK_SCROLL_BAR_STYLE = """
        -fx-background-color: transparent;
        -fx-background: transparent;
        """;

    private static final String DARK_SCROLL_THUMB_STYLE = """
        -fx-background-color: linear-gradient(to bottom, #475569, #64748b);
        -fx-background-radius: 8;
        """;

    private static final String DARK_FIELD_STYLE = """
        -fx-background-color: linear-gradient(to bottom, #334155, #2d3748);
        -fx-background-radius: 14;
        -fx-border-radius: 14;
        -fx-border-color: #475569;
        -fx-border-width: 1.8;
        -fx-padding: 14 16;
        -fx-font-size: 14;
        -fx-text-fill: #f1f5f9;
        -fx-font-family: 'Segoe UI', 'System', sans-serif;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0, 0, 2);
        """;

    private static final String DARK_FIELD_FOCUSED_STYLE = """
        -fx-background-color: linear-gradient(to bottom, #334155, #2d3748);
        -fx-background-radius: 14;
        -fx-border-radius: 14;
        -fx-border-color: linear-gradient(to right, #60a5fa, #818cf8);
        -fx-border-width: 2.2;
        -fx-padding: 14 16;
        -fx-font-size: 14;
        -fx-text-fill: #f1f5f9;
        -fx-font-family: 'Segoe UI', 'System', sans-serif;
        -fx-effect: dropshadow(gaussian, rgba(96,165,250,0.3), 15, 0, 0, 6);
        """;

    private static final String DARK_CONFIRM_BUTTON_STYLE = """
        -fx-background-color: linear-gradient(to bottom right, #2563eb, #4f46e5, #7c3aed);
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-font-size: 14;
        -fx-font-family: 'Segoe UI', 'System', sans-serif;
        -fx-background-radius: 12;
        -fx-cursor: hand;
        -fx-padding: 14 32;
        -fx-effect: dropshadow(gaussian, rgba(37,99,235,0.4), 12, 0, 0, 4);
        -fx-border-color: linear-gradient(to bottom, #93c5fd, #60a5fa);
        -fx-border-width: 1;
        -fx-border-radius: 12;
        """;

    private static final String DARK_CONFIRM_BUTTON_HOVER_STYLE = """
        -fx-background-color: linear-gradient(to bottom right, #1d4ed8, #4338ca, #6d28d9);
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-font-size: 14;
        -fx-font-family: 'Segoe UI', 'System', sans-serif;
        -fx-background-radius: 12;
        -fx-cursor: hand;
        -fx-padding: 14 32;
        -fx-effect: dropshadow(gaussian, rgba(37,99,235,0.6), 18, 0, 0, 8);
        -fx-border-color: linear-gradient(to bottom, #bfdbfe, #93c5fd);
        -fx-border-width: 1;
        -fx-border-radius: 12;
        """;

    private static final String DARK_DELETE_BUTTON_STYLE = """
        -fx-background-color: linear-gradient(to bottom right, #dc2626, #b91c1c, #991b1b);
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-font-size: 14;
        -fx-font-family: 'Segoe UI', 'System', sans-serif;
        -fx-background-radius: 12;
        -fx-cursor: hand;
        -fx-padding: 14 32;
        -fx-effect: dropshadow(gaussian, rgba(220,38,38,0.4), 12, 0, 0, 4);
        -fx-border-color: linear-gradient(to bottom, #fecaca, #fca5a5);
        -fx-border-width: 1;
        -fx-border-radius: 12;
        """;

    private static final String DARK_DELETE_BUTTON_HOVER_STYLE = """
        -fx-background-color: linear-gradient(to bottom right, #b91c1c, #991b1b, #7f1d1d);
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-font-size: 14;
        -fx-font-family: 'Segoe UI', 'System', sans-serif;
        -fx-background-radius: 12;
        -fx-cursor: hand;
        -fx-padding: 14 32;
        -fx-effect: dropshadow(gaussian, rgba(220,38,38,0.6), 18, 0, 0, 8);
        -fx-border-color: linear-gradient(to bottom, #fed7d7, #fecaca);
        -fx-border-width: 1;
        -fx-border-radius: 12;
        """;

    private static final String DARK_CANCEL_BUTTON_STYLE = """
        -fx-background-color: linear-gradient(to bottom, #475569, #374151);
        -fx-text-fill: #e2e8f0;
        -fx-font-weight: bold;
        -fx-font-size: 14;
        -fx-font-family: 'Segoe UI', 'System', sans-serif;
        -fx-background-radius: 12;
        -fx-cursor: hand;
        -fx-padding: 14 32;
        -fx-border-color: linear-gradient(to bottom, #64748b, #475569);
        -fx-border-width: 1.8;
        -fx-border-radius: 12;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3);
        """;

    private static final String DARK_CANCEL_BUTTON_HOVER_STYLE = """
        -fx-background-color: linear-gradient(to bottom, #64748b, #475569);
        -fx-text-fill: #f8fafc;
        -fx-font-weight: bold;
        -fx-font-size: 14;
        -fx-font-family: 'Segoe UI', 'System', sans-serif;
        -fx-background-radius: 12;
        -fx-cursor: hand;
        -fx-padding: 14 32;
        -fx-border-color: linear-gradient(to bottom, #94a3b8, #64748b);
        -fx-border-width: 1.8;
        -fx-border-radius: 12;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 4);
        """;

    private static final String DARK_LABEL_STYLE = """
        -fx-text-fill: #cbd5e1;
        -fx-font-weight: 600;
        -fx-font-size: 14;
        -fx-font-family: 'Segoe UI', 'System', sans-serif;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 0, 0, 0, 1);
        """;

    private static final String DARK_TITLE_STYLE = """
        -fx-text-fill: #f8fafc;
        -fx-font-weight: 700;
        -fx-font-size: 20;
        -fx-font-family: 'Segoe UI', 'System', sans-serif;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 0, 0, 0, 1);
        """;

    private static final String DARK_SUBTITLE_STYLE = """
        -fx-text-fill: #94a3b8;
        -fx-font-size: 13;
        -fx-font-family: 'Segoe UI', 'System', sans-serif;
        -fx-font-style: italic;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 0, 0, 0, 1);
        """;

    private static final String DARK_ICON_STYLE = """
        -fx-fill: linear-gradient(to bottom, #94a3b8, #64748b);
        -fx-font-size: 16;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 0, 0, 0, 1);
        """;

    private static final String DARK_FIELD_ERROR_STYLE = """
        -fx-background-color: linear-gradient(to bottom, #7f1d1d, #991b1b);
        -fx-background-radius: 14;
        -fx-border-radius: 14;
        -fx-border-color: linear-gradient(to bottom, #f87171, #ef4444);
        -fx-border-width: 2.2;
        -fx-padding: 14 16;
        -fx-font-size: 14;
        -fx-text-fill: #fecaca;
        -fx-font-family: 'Segoe UI', 'System', sans-serif;
        -fx-effect: dropshadow(gaussian, rgba(248,113,113,0.3), 15, 0, 0, 6);
        """;

    private static final String DARK_TOOLTIP_STYLE = """
        -fx-background-color: linear-gradient(to bottom, #374151, #1f2937);
        -fx-text-fill: #e5e7eb;
        -fx-font-size: 12;
        -fx-font-family: 'Segoe UI', 'System', sans-serif;
        -fx-background-radius: 8;
        -fx-border-color: #4b5563;
        -fx-border-width: 1;
        -fx-border-radius: 8;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0, 0, 3);
        -fx-padding: 8 12;
        """;

    private final List<LocalTime> possibleTimes;
    private final List<java.time.Duration> possibleDurations;
    private ThemeMode currentTheme = ThemeMode.LIGHT;

    // Dans SimpleSlotFormFactory.java
    private final EnseignantService enseignantService;
    private final SalleRestService SalleRestService;
    private final EtudiantService etudiantService;

    public SimpleSlotFormFactory(
            List<LocalTime> possibleTimes,
            List<java.time.Duration> possibleDurations,
            EnseignantService enseignantService,
            SalleRestService salleService, // ← CORRIGÉ : nom cohérent
            EtudiantService etudiantService
    ) {
        this.possibleTimes = possibleTimes;
        this.possibleDurations = possibleDurations;

        // CORRECTION : Assignation correcte avec vérification
        this.enseignantService = enseignantService;
        this.SalleRestService = salleService; // ← Utilisez le même nom
        this.etudiantService = etudiantService;

        System.out.println("🎉 SimpleSlotFormFactory initialisé !");
        System.out.println(" - enseignantService = " + (this.enseignantService != null));
        System.out.println(" - salleService      = " + (this.SalleRestService != null));
        System.out.println(" - etudiantService   = " + (this.etudiantService != null));
    }
    public void setThemeMode(ThemeMode theme) {
        this.currentTheme = theme;
    }

    public ThemeMode getThemeMode() {
        return currentTheme;
    }


    public void toggleTheme() {
        this.currentTheme = (currentTheme == ThemeMode.LIGHT) ? ThemeMode.DARK : ThemeMode.LIGHT;
    }
    private boolean hasSlotModificationPermission() {
        String userType = CalendarApp.getCurrentUserType();
        boolean isAdmin = userType != null && userType.equalsIgnoreCase("admin");

        System.out.println("🔐 Vérification des permissions - Type: " + userType + ", admin: " + isAdmin);
        return isAdmin;
    }

    /**
     * Affiche un message d'erreur de permission
     */
    private void showPermissionError() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Permission refusée");
        alert.setHeaderText("Action non autorisée");
        alert.setContentText("Seuls les administrateurs peuvent créer ou modifier des créneaux.\n\n" +
                "Veuillez contacter un administrateur si vous avez besoin d'ajouter un créneau.");

        // Appliquer le thème
        DialogPane dialogPane = alert.getDialogPane();
        String alertStyle = currentTheme == ThemeMode.DARK ? DARK_DIALOG_STYLE : LIGHT_DIALOG_STYLE;
        dialogPane.setStyle(alertStyle);

        alert.showAndWait();
    }


    public void setDarkTheme() {
        this.currentTheme = ThemeMode.DARK;
    }

    /**
     * Applique le thème clair
     */
    public void setLightTheme() {
        this.currentTheme = ThemeMode.LIGHT;
    }

    /**
     * Vérifie si le thème sombre est activé
     */
    public boolean isDarkTheme() {
        return currentTheme == ThemeMode.DARK;
    }

    /**
     * Vérifie si le thème clair est activé
     */
    public boolean isLightTheme() {
        return currentTheme == ThemeMode.LIGHT;
    }

    @Override
    public Dialog<SlotFormResult> createCalendarEventDialog(Slot slotInfo) {
        return createModernCalendarEventDialog(slotInfo);
    }
    private Dialog<SlotFormResult> createModernCalendarEventDialog(LocalDateTime defaultDateTime, int defaultDurationIndex) {
        Dialog<SlotFormResult> dialog = new Dialog<>();
        dialog.setHeaderText(null);
        initializeModernDialogStyle(dialog);

        // Champs pour tous les attributs du slot (vides pour création) - SANS type de cours
        TextField matiereField = createStyledTextField("", "Ex: Mathématiques, Physique...");
        ComboBox<EnseignantItem> enseignantComboBox = createEnseignantComboBox("Sélectionnez un enseignant");
        ComboBox<SalleItem> salleComboBox = createSalleComboBox("Sélectionnez une salle");

        DatePicker datePicker = createStyledDatePicker(defaultDateTime.toLocalDate());
        ComboBox<LocalTime> timeBox = createModernTimeComboBox(defaultDateTime.toLocalTime());
        ComboBox<java.time.Duration> durationBox = createModernDurationComboBox(null);
        if (defaultDurationIndex >= 0 && defaultDurationIndex < possibleDurations.size()) {
            durationBox.getSelectionModel().select(defaultDurationIndex);
        }

        // ✅ CORRECTION : Utiliser la version avec scroll SANS type de cours
        ScrollPane content = buildCompleteFormGridWithScroll(
                matiereField,
                enseignantComboBox,
                salleComboBox,
                datePicker,
                timeBox,
                durationBox,
                "Créer un nouveau créneau",
                "Remplissez tous les détails du créneau"
        );

        dialog.getDialogPane().setContent(content);
        applyEnhancedEntranceAnimation(content);

        // 🔥 CORRECTION : Pour la CRÉATION, seulement Annuler et Créer
        ButtonType cancelBtn = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType confirmBtn = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(cancelBtn, confirmBtn);

        applyEnhancedButtonStylesWithAnimations(dialog, confirmBtn, null, cancelBtn);

        // 🔥 CORRECTION : Pour la CRÉATION, pas de bouton Supprimer
        dialog.setResultConverter(buttonType -> {
            if (buttonType == confirmBtn) {
                if (!validateRequiredFields(matiereField, enseignantComboBox, salleComboBox,
                        datePicker, timeBox, durationBox)) {
                    return null;
                }

                // Récupérer les IDs
                Long enseignantId = enseignantComboBox.getValue() != null ?
                        enseignantComboBox.getValue().getId() : null;
                Long salleId = salleComboBox.getValue() != null ?
                        salleComboBox.getValue().getId() : null;

                // Convertir en LocalDateTime
                LocalDate selectedDate = datePicker.getValue();
                LocalTime selectedTime = timeBox.getValue();
                java.time.Duration selectedDuration = durationBox.getValue();

                LocalDateTime timeBegin = LocalDateTime.of(selectedDate, selectedTime);
                LocalDateTime timeEnd = timeBegin.plus(selectedDuration);

                return new SlotFormResult(
                        SlotFormAction.CONFIRM,
                        new ExtendedSlotFormContent(
                                timeBegin,
                                timeEnd,
                                selectedDuration,
                                matiereField.getText(),
                                enseignantId,
                                salleId
                        )
                );
            }
            // ❌ PAS de case DELETE ici car c'est une création
            return null;
        });

        return dialog;
    }
    @Override
    public Dialog<SlotFormResult> createCalendarEventDialog(LocalDateTime defaultDateTime, int defaultDurationIndex) {
        return createModernCalendarEventDialog(defaultDateTime, defaultDurationIndex);
    }

    /**
     * Crée une boîte de dialogue avec un thème spécifique
     */
    public Dialog<SlotFormResult> createCalendarEventDialog(Slot slotInfo, ThemeMode theme) {
        this.currentTheme = theme;
        return createModernCalendarEventDialog(slotInfo);
    }

    /**
     * Crée une boîte de dialogue avec un thème spécifique
     */
    public Dialog<SlotFormResult> createCalendarEventDialog(LocalDateTime defaultDateTime, int defaultDurationIndex, ThemeMode theme) {
        this.currentTheme = theme;
        return createModernCalendarEventDialog(defaultDateTime, defaultDurationIndex);
    }

    /**
     * Applique le thème à une boîte de dialogue existante
     */
    public void applyThemeToDialog(Dialog<?> dialog, ThemeMode theme) {
        this.currentTheme = theme;

        DialogPane pane = dialog.getDialogPane();
        String dialogStyle = currentTheme == ThemeMode.DARK ? DARK_DIALOG_STYLE : LIGHT_DIALOG_STYLE;
        pane.setStyle(dialogStyle);

        // Mettre à jour tous les composants enfants
        if (pane.getContent() instanceof VBox) {
            updateContentTheme((VBox) pane.getContent());
        }

        // Mettre à jour les boutons
        updateDialogButtonsTheme(dialog);
    }

    private Dialog<SlotFormResult> createModernCalendarEventDialog(Slot slotInfo) {
        // 🔥 VÉRIFICATION DES PERMISSIONS - Si non-admin, retourner null
        if (!hasSlotModificationPermission()) {
            System.out.println("⛔ Factory: Permission refusée pour la modification");
            return null; // La GridCalendarView gérera l'affichage en lecture seule
        }

        // CODE EXISTANT POUR LES ADMINS
        Dialog<SlotFormResult> dialog = new Dialog<>();
        dialog.setHeaderText(null);
        initializeModernDialogStyle(dialog);

        // Champs pour tous les attributs du slot (SAUF type de cours)
        TextField matiereField = createStyledTextField(slotInfo.getMatiere(), "Ex: Mathématiques, Physique...");

        // ✅ CORRECTION : Utiliser les nouveaux ComboBox avec IDs
        ComboBox<EnseignantItem> enseignantComboBox = createEnseignantComboBox("Sélectionnez un enseignant");
        ComboBox<SalleItem> salleComboBox = createSalleComboBox("Sélectionnez une salle");

        // Pré-remplir les valeurs existantes si disponibles (avec IDs)
        if (slotInfo.enseignantId() != null) {
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                    Platform.runLater(() -> {
                        enseignantComboBox.getItems().stream()
                                .filter(item -> item.getId().equals(slotInfo.enseignantId()))
                                .findFirst()
                                .ifPresent(enseignantComboBox::setValue);
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }

        if (slotInfo.salleId() != null) {
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                    Platform.runLater(() -> {
                        salleComboBox.getItems().stream()
                                .filter(item -> item.getId().equals(slotInfo.salleId()))
                                .findFirst()
                                .ifPresent(salleComboBox::setValue);
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }

        DatePicker datePicker = createStyledDatePicker(slotInfo.getDate());
        ComboBox<LocalTime> timeBox = createModernTimeComboBox(slotInfo.getHeureDebut());
        ComboBox<java.time.Duration> durationBox = createModernDurationComboBox(slotInfo.duration());

        // Utiliser la version avec scroll (SANS type de cours)
        ScrollPane content = buildCompleteFormGridWithScroll(
                matiereField,
                enseignantComboBox,
                salleComboBox,
                datePicker,
                timeBox,
                durationBox,
                "Modifier le créneau",
                "Mettez à jour tous les détails du créneau"
        );

        dialog.getDialogPane().setContent(content);
        applyEnhancedEntranceAnimation(content);

        // 🔥 CORRECTION : Pour la MODIFICATION, ajouter le bouton Supprimer
        ButtonType deleteBtn = new ButtonType("Supprimer", ButtonBar.ButtonData.LEFT);
        ButtonType cancelBtn = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType confirmBtn = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(deleteBtn, cancelBtn, confirmBtn);

        applyEnhancedButtonStylesWithAnimations(dialog, confirmBtn, deleteBtn, cancelBtn);

        // 🔥 CORRECTION : Configurer l'action de suppression avec confirmation
        Node deleteButtonNode = dialog.getDialogPane().lookupButton(deleteBtn);
        if (deleteButtonNode instanceof Button) {
            Button deleteButton = (Button) deleteButtonNode;
            deleteButton.setOnAction(event -> {
                // Empêcher la fermeture immédiate pour afficher la confirmation
                event.consume();
                handleDeleteConfirmation(dialog, deleteBtn);
            });
        }

        // Dans createModernCalendarEventDialog (version modification)
        dialog.setResultConverter(buttonType -> {
            if (buttonType == confirmBtn) {
                if (!validateRequiredFields(matiereField, enseignantComboBox, salleComboBox,
                        datePicker, timeBox, durationBox)) {
                    return null;
                }

                // Récupérer les IDs
                Long enseignantId = enseignantComboBox.getValue() != null ?
                        enseignantComboBox.getValue().getId() : null;
                Long salleId = salleComboBox.getValue() != null ?
                        salleComboBox.getValue().getId() : null;

                // Convertir en LocalDateTime
                LocalDate selectedDate = datePicker.getValue();
                LocalTime selectedTime = timeBox.getValue();
                java.time.Duration selectedDuration = durationBox.getValue();

                LocalDateTime timeBegin = LocalDateTime.of(selectedDate, selectedTime);
                LocalDateTime timeEnd = timeBegin.plus(selectedDuration);

                return new SlotFormResult(
                        SlotFormAction.CONFIRM,
                        new ExtendedSlotFormContent(
                                timeBegin,
                                timeEnd,
                                selectedDuration,
                                matiereField.getText(),
                                enseignantId,
                                salleId
                        )
                );
            }
            // 🔥 CORRECTION : Ajouter le cas DELETE (seulement pour la modification)
            else if (buttonType == deleteBtn) {
                System.out.println("🗑️ Action DELETE détectée dans le formulaire");
                return new SlotFormResult(SlotFormAction.DELETE, null);
            }
            return null;
        });

        // Ajouter des écouteurs pour la validation en temps réel (SANS type de cours)
        addRealTimeValidation(
                matiereField,
                enseignantComboBox,
                salleComboBox,
                datePicker,
                timeBox,
                durationBox
        );

        return dialog;
    }
    /**
     * Gère la confirmation de suppression
     */
    private void handleDeleteConfirmation(Dialog<SlotFormResult> dialog, ButtonType deleteBtn) {
        boolean confirmed = showDeleteConfirmation();
        if (confirmed) {
            System.out.println("✅ Suppression confirmée par l'utilisateur");
            // Fermer le dialogue avec le résultat DELETE
            dialog.setResult(new SlotFormResult(SlotFormAction.DELETE, null));
        } else {
            System.out.println("❌ Suppression annulée par l'utilisateur");
            // Ne rien faire, le dialogue reste ouvert
        }
    }
    private boolean validateRequiredFields(TextField matiere,
                                           ComboBox<EnseignantItem> enseignant, ComboBox<SalleItem> salle,
                                           DatePicker date, ComboBox<LocalTime> time,
                                           ComboBox<java.time.Duration> duration) {

        boolean isValid = true;
        if (matiere.getText() == null || matiere.getText().trim().isEmpty()) {
            showFieldError(matiere, "La matière est obligatoire");
            isValid = false;
        } else {
            clearFieldError(matiere);
        }
        if (enseignant.getValue() == null) {
            showFieldError(enseignant, "Veuillez sélectionner un enseignant");
            isValid = false;
        } else {
            clearFieldError(enseignant);
        }
        if (salle.getValue() == null) {
            showFieldError(salle, "Veuillez sélectionner une salle");
            isValid = false;
        } else {
            clearFieldError(salle);
        }

        // Validation de la date
        if (date.getValue() == null) {
            showFieldError(date, "Veuillez sélectionner une date");
            isValid = false;
        } else {
            clearFieldError(date);
        }

        // Validation de l'heure
        if (time.getValue() == null) {
            showFieldError(time, "Veuillez sélectionner une heure de début");
            isValid = false;
        } else {
            clearFieldError(time);
        }

        // Validation de la durée
        if (duration.getValue() == null) {
            showFieldError(duration, "Veuillez sélectionner une durée");
            isValid = false;
        } else {
            clearFieldError(duration);
        }

        return isValid;
    }
    private void showFieldError(Control field, String message) {
        String errorStyle = currentTheme == ThemeMode.DARK ? DARK_FIELD_ERROR_STYLE : LIGHT_FIELD_ERROR_STYLE;
        field.setStyle(errorStyle);

        // Ajouter un tooltip d'erreur
        Tooltip errorTooltip = new Tooltip(message);
        applyTooltipTheme(errorTooltip);
        errorTooltip.setStyle(errorTooltip.getStyle() + " -fx-text-fill: #dc2626;");
        field.setTooltip(errorTooltip);

        // Afficher le tooltip
        errorTooltip.show(field, field.getScene().getWindow().getX() + field.getLayoutX() + 10,
                field.getScene().getWindow().getY() + field.getLayoutY() + field.getHeight() + 5);
    }

    /**
     * Efface l'erreur d'un champ
     */
    private void clearFieldError(Control field) {
        if (field instanceof TextField) {
            applyTextFieldTheme((TextField) field);
        } else if (field instanceof ComboBox) {
            applyComboBoxTheme((ComboBox<?>) field);
        } else if (field instanceof DatePicker) {
            applyDatePickerTheme((DatePicker) field);
        }

        // Rétablir le tooltip original
        if (field instanceof TextField && ((TextField) field).getPromptText() != null) {
            Tooltip originalTooltip = new Tooltip(((TextField) field).getPromptText());
            applyTooltipTheme(originalTooltip);
            field.setTooltip(originalTooltip);
        }
    }

    /**
     * Affiche un message d'erreur général
     */

    /**
     * Affiche une confirmation de suppression
     */
    private boolean showDeleteConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le créneau");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce créneau ? Cette action est irréversible.");

        // Styliser l'alerte de confirmation
        DialogPane alertPane = alert.getDialogPane();
        String alertStyle = currentTheme == ThemeMode.DARK ? DARK_DIALOG_STYLE : LIGHT_DIALOG_STYLE;
        alertPane.setStyle(alertStyle);

        // Appliquer le thème aux boutons
        Button okButton = (Button) alertPane.lookupButton(ButtonType.OK);
        Button cancelButton = (Button) alertPane.lookupButton(ButtonType.CANCEL);

        String deleteStyle = currentTheme == ThemeMode.DARK ? DARK_DELETE_BUTTON_STYLE : LIGHT_DELETE_BUTTON_STYLE;
        String cancelStyle = currentTheme == ThemeMode.DARK ? DARK_CANCEL_BUTTON_STYLE : LIGHT_CANCEL_BUTTON_STYLE;

        if (okButton != null) okButton.setStyle(deleteStyle);
        if (cancelButton != null) cancelButton.setStyle(cancelStyle);

        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }

    /**
     * Ajoute la validation en temps réel
     */
    private void addRealTimeValidation(TextField matiere,
                                       // ❌ SUPPRIMÉ : TextField typeCours,
                                       ComboBox<EnseignantItem> enseignant, ComboBox<SalleItem> salle,
                                       DatePicker date, ComboBox<LocalTime> time,
                                       ComboBox<java.time.Duration> duration) {

        // Écouteurs pour les TextField
        matiere.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.trim().isEmpty()) {
                clearFieldError(matiere);
            }
        });

        // ✅ CORRECTION : Écouteurs pour les ComboBox avec IDs
        enseignant.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                clearFieldError(enseignant);
            }
        });

        salle.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                clearFieldError(salle);
            }
        });

        // Écouteurs pour DatePicker et autres ComboBox
        date.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                clearFieldError(date);
            }
        });

        time.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                clearFieldError(time);
            }
        });

        duration.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                clearFieldError(duration);
            }
        });
    }
    private ScrollPane buildCompleteFormGridWithScroll(TextField matiere,
                                                       ComboBox<EnseignantItem> enseignant,
                                                       ComboBox<SalleItem> salle,
                                                       DatePicker datePicker,
                                                       ComboBox<LocalTime> timeBox,
                                                       ComboBox<java.time.Duration> durationBox,
                                                       String title, String subtitle) {

        // Créer le contenu principal SANS type de cours
        VBox content = buildCompleteFormGrid(matiere, enseignant, salle,
                // ❌ SUPPRIMÉ : typeCours,
                datePicker, timeBox, durationBox, title, subtitle);

        // Créer le ScrollPane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Définir la hauteur maximale
        scrollPane.setMaxHeight(MAX_CONTENT_HEIGHT);
        scrollPane.setPrefHeight(MAX_CONTENT_HEIGHT);

        // Appliquer le style selon le thème
        applyScrollPaneTheme(scrollPane);

        return scrollPane;
    }
    private VBox buildCompleteFormGrid(TextField matiere, ComboBox<EnseignantItem> enseignant,
                                       ComboBox<SalleItem> salle,
                                       // ❌ SUPPRIMÉ : TextField typeCours,
                                       DatePicker datePicker, ComboBox<LocalTime> timeBox,
                                       ComboBox<java.time.Duration> durationBox,
                                       String title, String subtitle) {
        VBox container = new VBox();
        container.setSpacing(28);

        // Header with enhanced styling
        VBox headerBox = new VBox();
        headerBox.setSpacing(8);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(title);
        applyTitleTheme(titleLabel);

        Label subtitleLabel = new Label(subtitle);
        applySubtitleTheme(subtitleLabel);

        headerBox.getChildren().addAll(titleLabel, subtitleLabel);

        // Enhanced Grid with better spacing
        GridPane grid = new GridPane();
        grid.setVgap(20);
        grid.setHgap(24);
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setPadding(new Insets(8, 0, 0, 0));

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(35);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(65);
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);

        // Ajout de tous les champs (SAUF type de cours)
        HBox matiereLabelBox = createEnhancedLabelWithIcon("📚", "Matière");
        HBox enseignantLabelBox = createEnhancedLabelWithIcon("👨‍🏫", "Enseignant");
        HBox salleLabelBox = createEnhancedLabelWithIcon("🏫", "Salle");
        // ❌ SUPPRIMÉ : HBox typeCoursLabelBox = createEnhancedLabelWithIcon("📝", "Type de cours");
        HBox dateLabelBox = createEnhancedLabelWithIcon("📅", "Date");
        HBox timeLabelBox = createEnhancedLabelWithIcon("⏰", "Heure de début");
        HBox durationLabelBox = createEnhancedLabelWithIcon("⏳", "Durée");

        grid.add(matiereLabelBox, 0, 0);
        grid.add(matiere, 1, 0);
        grid.add(enseignantLabelBox, 0, 1);
        grid.add(enseignant, 1, 1);
        grid.add(salleLabelBox, 0, 2);
        grid.add(salle, 1, 2);
        // ❌ SUPPRIMÉ : grid.add(typeCoursLabelBox, 0, 3);
        // ❌ SUPPRIMÉ : grid.add(typeCours, 1, 3);
        grid.add(dateLabelBox, 0, 3); // Décalé d'une ligne
        grid.add(datePicker, 1, 3);
        grid.add(timeLabelBox, 0, 4); // Décalé d'une ligne
        grid.add(timeBox, 1, 4);
        grid.add(durationLabelBox, 0, 5); // Décalé d'une ligne
        grid.add(durationBox, 1, 5);

        container.getChildren().addAll(headerBox, grid);
        return container;
    }
    private ComboBox<String> createStyledComboBox(String prompt, String type) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPromptText(prompt);
        applyComboBoxTheme(comboBox);

        // SOLUTION TEMPORAIRE : Chargement direct
        System.out.println("🔄 Chargement des " + type + " depuis la base de données...");
        try {
            if ("enseignant".equalsIgnoreCase(type)) {
                loadEnseignants(comboBox);
            } else if ("salle".equalsIgnoreCase(type)) {
                loadSalles(comboBox);
            } else {
                System.err.println("❌ Type de ComboBox non reconnu: " + type);
                comboBox.setPromptText("Type non reconnu");
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement: " + e.getMessage());
            comboBox.getItems().clear();
            comboBox.setPromptText("Erreur de chargement");
        }

        Tooltip tooltip = new Tooltip(prompt);
        applyTooltipTheme(tooltip);
        comboBox.setTooltip(tooltip);

        return comboBox;
    }
    private ComboBox<String> createStyledComboBox(String prompt) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPromptText(prompt);
        applyComboBoxTheme(comboBox);

        // ✅ CORRECTION : Détection automatique du type basée sur le prompt
        String lowerPrompt = prompt.toLowerCase();

        if (lowerPrompt.contains("enseignant") || lowerPrompt.contains("prof")) {
            System.out.println("🔄 Détection automatique: ComboBox Enseignant");
            loadEnseignants(comboBox);
        } else if (lowerPrompt.contains("salle") || lowerPrompt.contains("room")) {
            System.out.println("🔄 Détection automatique: ComboBox Salle");
            loadSalles(comboBox);
        } else if (lowerPrompt.contains("groupe") || lowerPrompt.contains("group")) {
            System.out.println("🔄 Détection automatique: ComboBox Groupe");
            // loadGroupes(comboBox); // Décommentez si vous avez cette méthode
        } else {
            System.out.println("ℹ️ ComboBox sans chargement automatique: " + prompt);
        }

        Tooltip tooltip = new Tooltip(prompt);
        applyTooltipTheme(tooltip);
        comboBox.setTooltip(tooltip);

        return comboBox;
    }
    private void loadSalles(ComboBox<String> comboBox) {
        System.out.println("🔍 Vérification de SalleService: " + (SalleRestService != null));

        if (SalleRestService == null) {
            System.err.println("❌ SalleService null !");
            handleEmptySalles(comboBox);
            return;
        }

        try {
            System.out.println("🔄 Appel de getAllSalles()...");

            // Utiliser le bon type - List<Map<String, Object>>
            CompletableFuture<List<Map<String, Object>>> futureSalles = SalleRestService.getAllSalles();

            futureSalles.thenAccept(sallesMap -> {
                System.out.println("📥 Données salles reçues: " + (sallesMap != null ? sallesMap.size() : "null"));

                Platform.runLater(() -> {
                    if (sallesMap == null || sallesMap.isEmpty()) {
                        System.out.println("⚠️ Aucune salle trouvée dans la base de données");
                        handleEmptySalles(comboBox);
                        return;
                    }

                    // Convertir les Map en noms de salles
                    List<String> noms = sallesMap.stream()
                            .map(salleMap -> {
                                // Extraire le nom de la salle depuis la Map
                                Object nomObj = salleMap.get("nom");
                                return nomObj != null ? nomObj.toString() : null;
                            })
                            .filter(nom -> nom != null && !nom.trim().isEmpty())
                            .distinct()
                            .sorted()
                            .toList();

                    if (!noms.isEmpty()) {
                        comboBox.getItems().setAll(noms);
                        System.out.println("✅ " + noms.size() + " salles chargées depuis la base de données");
                        System.out.println("📋 Salles: " + noms);

                        // Sélectionner le premier élément si disponible
                        comboBox.getSelectionModel().selectFirst();
                    } else {
                        System.out.println("⚠️ Aucun nom de salle valide trouvé");
                        handleEmptySalles(comboBox);
                    }
                });
            }).exceptionally(throwable -> {
                System.err.println("❌ Erreur lors du chargement des salles: " + throwable.getMessage());
                throwable.printStackTrace();
                Platform.runLater(() -> {
                    System.err.println("❌ Échec du chargement des salles depuis la base");
                    handleEmptySalles(comboBox);
                });
                return null;
            });

        } catch (Exception e) {
            System.err.println("❌ Exception lors du chargement des salles: " + e.getMessage());
            e.printStackTrace();
            handleEmptySalles(comboBox);
        }
    }
    // Assurez-vous que les services sont correctement initialisés
    private void initializeServices() {
        // Initialiser les services
        EnseignantService enseignantService = new EnseignantService();
        SalleRestService salleService = new SalleRestService();
        EtudiantService etudiantService = new EtudiantService(); // ou null si pas utilisé

        // Configurer CalendarApp
        CalendarApp.setServices(enseignantService, salleService, etudiantService);

        // Vérifier que les services sont bien configurés
        System.out.println("🔧 Services configurés:");
        System.out.println(" - EnseignantService: " + (enseignantService != null));
        System.out.println(" - SalleService: " + (salleService != null));
        System.out.println(" - EtudiantService: " + (etudiantService != null));
    }
    public void diagnoseServices() {
        System.out.println("🔍 DIAGNOSTIC COMPLET SimpleSlotFormFactory:");
        System.out.println(" - this.enseignantService: " + (this.enseignantService != null));
        System.out.println(" - this.salleService: " + (this.SalleRestService != null));
        System.out.println(" - this.etudiantService: " + (this.etudiantService != null));

        // Vérifiez l'état de CalendarApp
        System.out.println("🔍 DIAGNOSTIC CalendarApp:");
        System.out.println(" - CalendarApp.getEnseignantService(): " + (CalendarApp.getEnseignantService() != null));
        System.out.println(" - CalendarApp.getSalleService(): " + (CalendarApp.getSalleService() != null));
        System.out.println(" - CalendarApp.getEtudiantService(): " + (CalendarApp.getEtudiantService() != null));

        // Testez les services directement
        if (this.enseignantService != null) {
            try {
                System.out.println("🔍 Test EnseignantService...");
                List<Enseignant> enseignants = this.enseignantService.getAllEnseignantsSync();
                System.out.println("✅ EnseignantService fonctionnel: " + enseignants.size() + " enseignants");
            } catch (Exception e) {
                System.err.println("❌ EnseignantService erroné: " + e.getMessage());
            }
        }

        if (this.SalleRestService != null) {
            try {
                System.out.println("🔍 Test SalleService...");
                // Test simple de connexion
                System.out.println("✅ SalleService initialisé");
            } catch (Exception e) {
                System.err.println("❌ SalleService erroné: " + e.getMessage());
            }
        }
    }
    private void loadEnseignants(ComboBox<String> comboBox) {
        System.out.println("🔍 Vérification de EnseignantService: " + (enseignantService != null));

        if (enseignantService == null) {
            System.err.println("❌ EnseignantService null !");
            handleEmptyEnseignants(comboBox);
            return;
        }

        try {
            System.out.println("🔄 Appel de getAllEnseignants()...");

            CompletableFuture<List<Enseignant>> futureEnseignants = enseignantService.getAllEnseignants();

            futureEnseignants.thenAccept(enseignants -> {
                System.out.println("📥 Données enseignants reçues: " + (enseignants != null ? enseignants.size() : "null"));

                Platform.runLater(() -> {
                    if (enseignants == null || enseignants.isEmpty()) {
                        System.out.println("⚠️ Aucun enseignant trouvé dans la base de données");
                        handleEmptyEnseignants(comboBox);
                        return;
                    }

                    List<String> nomsComplets = enseignants.stream()
                            .map(enseignant -> {
                                String nom = enseignant.getNom() != null ? enseignant.getNom() : "";
                                String prenom = enseignant.getPrenom() != null ? enseignant.getPrenom() : "";
                                return (nom + " " + prenom).trim();
                            })
                            .filter(nomComplet -> !nomComplet.isEmpty())
                            .distinct()
                            .sorted()
                            .toList();

                    if (!nomsComplets.isEmpty()) {
                        comboBox.getItems().setAll(nomsComplets);
                        System.out.println("✅ " + nomsComplets.size() + " enseignants chargés depuis la base de données");
                        System.out.println("📋 Enseignants: " + nomsComplets);

                        // Sélectionner le premier élément si disponible
                        comboBox.getSelectionModel().selectFirst();
                    } else {
                        System.out.println("⚠️ Aucun nom d'enseignant valide trouvé");
                        handleEmptyEnseignants(comboBox);
                    }
                });
            }).exceptionally(throwable -> {
                System.err.println("❌ Erreur lors du chargement des enseignants: " + throwable.getMessage());
                throwable.printStackTrace();
                Platform.runLater(() -> {
                    System.err.println("❌ Échec du chargement des enseignants depuis la base");
                    handleEmptyEnseignants(comboBox);
                });
                return null;
            });

        } catch (Exception e) {
            System.err.println("❌ Exception lors du chargement des enseignants: " + e.getMessage());
            e.printStackTrace();
            handleEmptyEnseignants(comboBox);
        }
    }

    /**
     * Gère le cas où aucun enseignant n'est trouvé
     */
    private void handleEmptyEnseignants(ComboBox<String> comboBox) {
        Platform.runLater(() -> {
            comboBox.getItems().clear();
            comboBox.setPromptText("Aucun enseignant disponible");
            comboBox.getItems().add("Aucun enseignant trouvé");
            comboBox.getSelectionModel().selectFirst();

            Tooltip tooltip = new Tooltip("Aucun enseignant n'a été trouvé dans la base de données.\nVeuillez vérifier la connexion ou contacter l'administrateur.");
            applyTooltipTheme(tooltip);
            comboBox.setTooltip(tooltip);
        });
    }
    /**
     * Convertit une Map en objet Salle
     */
    private Salle mapToSalle(Map<String, Object> salleMap) {
        Salle salle = new Salle();

        // Convertir l'ID
        Object idObj = salleMap.get("id");
        if (idObj instanceof Number) {
            salle.setId(((Number) idObj).longValue());
        }

        // Convertir le nom
        Object nomObj = salleMap.get("nom");
        if (nomObj != null) {
            salle.setNom(nomObj.toString());
        }

        // Convertir le bâtiment
        Object batimentObj = salleMap.get("batiment");
        if (batimentObj != null) {
            salle.setBatiment(batimentObj.toString());
        }

        // Convertir la capacité
        Object capaciteObj = salleMap.get("capacite");
        if (capaciteObj instanceof Number) {
            salle.setCapacite(((Number) capaciteObj).intValue());
        }

        return salle;
    }

    // Version modifiée de loadSalles utilisant la conversion
    private void loadSallesWithConversion(ComboBox<String> comboBox) {
        System.out.println("🔍 Vérification de SalleService: " + (SalleRestService != null));

        if (SalleRestService == null) {
            System.err.println("❌ SalleService null !");
            handleEmptySalles(comboBox);
            return;
        }

        try {
            System.out.println("🔄 Appel de getAllSalles()...");

            CompletableFuture<List<Map<String, Object>>> futureSalles = SalleRestService.getAllSalles();

            futureSalles.thenAccept(sallesMap -> {
                System.out.println("📥 Données salles reçues: " + (sallesMap != null ? sallesMap.size() : "null"));

                Platform.runLater(() -> {
                    if (sallesMap == null || sallesMap.isEmpty()) {
                        System.out.println("⚠️ Aucune salle trouvée dans la base de données");
                        handleEmptySalles(comboBox);
                        return;
                    }

                    // Convertir les Map en objets Salle puis extraire les noms
                    List<String> noms = sallesMap.stream()
                            .map(this::mapToSalle) // Convertir Map -> Salle
                            .map(Salle::getNom)   // Extraire le nom
                            .filter(nom -> nom != null && !nom.trim().isEmpty())
                            .distinct()
                            .sorted()
                            .toList();

                    if (!noms.isEmpty()) {
                        comboBox.getItems().setAll(noms);
                        System.out.println("✅ " + noms.size() + " salles chargées depuis la base de données");
                        System.out.println("📋 Salles: " + noms);

                        // Sélectionner le premier élément si disponible
                        comboBox.getSelectionModel().selectFirst();
                    } else {
                        System.out.println("⚠️ Aucun nom de salle valide trouvé");
                        handleEmptySalles(comboBox);
                    }
                });
            }).exceptionally(throwable -> {
                System.err.println("❌ Erreur lors du chargement des salles: " + throwable.getMessage());
                throwable.printStackTrace();
                Platform.runLater(() -> {
                    System.err.println("❌ Échec du chargement des salles depuis la base");
                    handleEmptySalles(comboBox);
                });
                return null;
            });

        } catch (Exception e) {
            System.err.println("❌ Exception lors du chargement des salles: " + e.getMessage());
            e.printStackTrace();
            handleEmptySalles(comboBox);
        }
    }
    private void handleEmptySalles(ComboBox<String> comboBox) {
        Platform.runLater(() -> {
            comboBox.getItems().clear();
            comboBox.setPromptText("Aucune salle disponible");
            comboBox.getItems().add("Aucune salle trouvée");
            comboBox.getSelectionModel().selectFirst();

            Tooltip tooltip = new Tooltip("Aucune salle n'a été trouvée dans la base de données.\nVeuillez vérifier la connexion ou contacter l'administrateur.");
            applyTooltipTheme(tooltip);
            comboBox.setTooltip(tooltip);
        });
    }


    private String getEnseignantNom(Enseignant enseignant) {
        try {
            // Essayer différentes méthodes pour obtenir le nom
            if (enseignant.getNom() != null) {
                return enseignant.getNom();
            }

            // Si getNom() ne fonctionne pas, essayer par réflexion
            try {
                java.lang.reflect.Method method = enseignant.getClass().getMethod("getNom");
                Object result = method.invoke(enseignant);
                return result != null ? result.toString() : "Inconnu";
            } catch (Exception e) {
                return "Inconnu";
            }
        } catch (Exception e) {
            return "Inconnu";
        }
    }

    /**
     * Méthode utilitaire pour obtenir le prénom d'un enseignant
     */
    private String getEnseignantPrenom(Enseignant enseignant) {
        try {
            // Essayer différentes méthodes pour obtenir le prénom
            if (enseignant.getPrenom() != null) {
                return enseignant.getPrenom();
            }

            // Si getPrenom() ne fonctionne pas, essayer par réflexion
            try {
                java.lang.reflect.Method method = enseignant.getClass().getMethod("getPrenom");
                Object result = method.invoke(enseignant);
                return result != null ? result.toString() : "";
            } catch (Exception e) {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Méthode utilitaire pour obtenir le nom d'une salle
     */
    private String getSalleNom(Salle salle) {
        try {
            // Essayer différentes méthodes pour obtenir le nom
            if (salle.getNom() != null) {
                return salle.getNom();
            }

            // Si getNom() ne fonctionne pas, essayer par réflexion
            try {
                java.lang.reflect.Method method = salle.getClass().getMethod("getNom");
                Object result = method.invoke(salle);
                return result != null ? result.toString() : "Salle Inconnue";
            } catch (Exception e) {
                return "Salle Inconnue";
            }
        } catch (Exception e) {
            return "Salle Inconnue";
        }
    }


    private void debugObjects(List<Object> objects, String type) {
        System.out.println("🐛 DEBUG " + type + " reçus:");
        if (objects == null) {
            System.out.println("   - Liste null");
            return;
        }

        System.out.println("   - Nombre d'objets: " + objects.size());
        for (int i = 0; i < objects.size(); i++) {
            Object obj = objects.get(i);
            System.out.println("   - Objet " + i + ": " + obj);
            System.out.println("     Type: " + (obj != null ? obj.getClass().getName() : "null"));

            if (obj != null) {
                // Afficher les méthodes disponibles pour le débogage
                try {
                    java.lang.reflect.Method[] methods = obj.getClass().getMethods();
                    System.out.println("     Méthodes disponibles:");
                    for (java.lang.reflect.Method method : methods) {
                        if (method.getName().startsWith("get") && method.getParameterCount() == 0) {
                            try {
                                Object result = method.invoke(obj);
                                System.out.println("       " + method.getName() + ": " + result);
                            } catch (Exception e) {
                                System.out.println("       " + method.getName() + ": ERREUR");
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("     Impossible d'obtenir les méthodes: " + e.getMessage());
                }
            }
        }
    }

  /*  private String extractGroupeFromEtudiant(Etudiant etudiant) {

        try {
            // Essayez d'appeler getGroupe() par réflexion
            java.lang.reflect.Method method = etudiant.getClass().getMethod("getGroupe");
            Object result = method.invoke(etudiant);
            return result != null ? result.toString() : null;
        } catch (Exception e) {
            // Si getGroupe() n'existe pas, essayez d'autres méthodes courantes
            try {
                java.lang.reflect.Method method = etudiant.getClass().getMethod("getClasse");
                Object result = method.invoke(etudiant);
                return result != null ? result.toString() : null;
            } catch (Exception e2) {
                try {
                    java.lang.reflect.Method method = etudiant.getClass().getMethod("getFiliere");
                    Object result = method.invoke(etudiant);
                    return result != null ? result.toString() : null;
                } catch (Exception e3) {
                    // Si aucune méthode n'est trouvée, retournez une valeur par défaut basée sur l'ID
                    return "Groupe " + (Math.abs(etudiant.hashCode()) % 10 + 1);
                }
            }
        }
    }*/

    private void applyScrollPaneTheme(ScrollPane scrollPane) {
        String scrollStyle = currentTheme == ThemeMode.DARK ? DARK_SCROLL_STYLE : LIGHT_SCROLL_STYLE;
        String scrollBarStyle = currentTheme == ThemeMode.DARK ? DARK_SCROLL_BAR_STYLE : LIGHT_SCROLL_BAR_STYLE;
        String scrollThumbStyle = currentTheme == ThemeMode.DARK ? DARK_SCROLL_THUMB_STYLE : LIGHT_SCROLL_THUMB_STYLE;

        scrollPane.setStyle(scrollStyle);

        // Appliquer le style avec vérification de null pour éviter l'erreur
        applyScrollBarStyleSafely(scrollPane, scrollBarStyle, scrollThumbStyle);
    }

    // NOUVELLE MÉTHODE : Application sécurisée des styles des barres de défilement
    private void applyScrollBarStyleSafely(ScrollPane scrollPane, String scrollBarStyle, String scrollThumbStyle) {
        // Appliquer le style après que le ScrollPane soit rendu
        scrollPane.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) {
                applyScrollBarStylesImmediately(scrollPane, scrollBarStyle, scrollThumbStyle);
            }
        });

        // Essayer d'appliquer immédiatement aussi
        applyScrollBarStylesImmediately(scrollPane, scrollBarStyle, scrollThumbStyle);
    }

    // NOUVELLE MÉTHODE : Application immédiate avec vérifications de null
    private void applyScrollBarStylesImmediately(ScrollPane scrollPane, String scrollBarStyle, String scrollThumbStyle) {
        // Barre de défilement verticale
        Node verticalScrollBar = scrollPane.lookup(".scroll-bar:vertical");
        if (verticalScrollBar != null) {
            verticalScrollBar.setStyle(scrollBarStyle);

            Node verticalTrack = scrollPane.lookup(".scroll-bar:vertical .track");
            if (verticalTrack != null) {
                verticalTrack.setStyle(scrollBarStyle);
            }

            Node verticalThumb = scrollPane.lookup(".scroll-bar:vertical .thumb");
            if (verticalThumb != null) {
                verticalThumb.setStyle(scrollThumbStyle);
            }
        }

        // Barre de défilement horizontale
        Node horizontalScrollBar = scrollPane.lookup(".scroll-bar:horizontal");
        if (horizontalScrollBar != null) {
            horizontalScrollBar.setStyle(scrollBarStyle);

            Node horizontalTrack = scrollPane.lookup(".scroll-bar:horizontal .track");
            if (horizontalTrack != null) {
                horizontalTrack.setStyle(scrollBarStyle);
            }

            Node horizontalThumb = scrollPane.lookup(".scroll-bar:horizontal .thumb");
            if (horizontalThumb != null) {
                horizontalThumb.setStyle(scrollThumbStyle);
            }
        }

        // Viewport
        Node viewport = scrollPane.lookup(".viewport");
        if (viewport != null) {
            viewport.setStyle("-fx-background-color: transparent;");
        }
    }

    // ==================== MÉTHODES D'INITIALISATION ET STYLE ====================

    private void initializeModernDialogStyle(Dialog<?> dialog) {
        DialogPane pane = dialog.getDialogPane();
        String dialogStyle = currentTheme == ThemeMode.DARK ? DARK_DIALOG_STYLE : LIGHT_DIALOG_STYLE;
        pane.setStyle(dialogStyle);
        pane.setPadding(new Insets(25, 35, 25, 35)); // Padding réduit pour gagner de l'espace

        // Définir la taille préférée de la boîte de dialogue
        pane.setPrefSize(600, MAX_DIALOG_HEIGHT);
        pane.setMaxSize(600, MAX_DIALOG_HEIGHT);
    }

    private TextField createStyledTextField(String text, String prompt) {
        TextField field = new TextField(text);
        field.setPromptText(prompt);
        applyTextFieldTheme(field);

        Tooltip tooltip = new Tooltip(prompt);
        applyTooltipTheme(tooltip);
        field.setTooltip(tooltip);

        return field;
    }

    private DatePicker createStyledDatePicker(LocalDate date) {
        DatePicker picker = new DatePicker(date);
        applyDatePickerTheme(picker);

        Tooltip tooltip = new Tooltip("Sélectionnez la date du créneau.");
        applyTooltipTheme(tooltip);
        picker.setTooltip(tooltip);

        return picker;
    }

    private ComboBox<LocalTime> createModernTimeComboBox(LocalTime defaultValue) {
        ComboBox<LocalTime> box = new ComboBox<>();
        box.getItems().addAll(possibleTimes);
        box.setValue(defaultValue);
        applyComboBoxTheme(box);

        Tooltip tooltip = new Tooltip("Choisissez l'heure de début.");
        applyTooltipTheme(tooltip);
        box.setTooltip(tooltip);

        return box;
    }

    private ComboBox<java.time.Duration> createModernDurationComboBox(java.time.Duration defaultValue) {
        ComboBox<java.time.Duration> box = new ComboBox<>();
        box.getItems().addAll(possibleDurations);
        if (defaultValue != null) box.setValue(defaultValue);
        applyComboBoxTheme(box);

        Tooltip tooltip = new Tooltip("Sélectionnez la durée du créneau.");
        applyTooltipTheme(tooltip);
        box.setTooltip(tooltip);

        return box;
    }

    private HBox createEnhancedLabelWithIcon(String iconText, String labelText) {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);

        Text icon = new Text(iconText);
        applyIconTheme(icon);

        Label label = new Label(labelText);
        applyLabelTheme(label);

        box.getChildren().addAll(icon, label);
        return box;
    }

    // ==================== MÉTHODES D'ANIMATION ====================

    private void applyEnhancedEntranceAnimation(ScrollPane content) {
        content.setOpacity(0);
        content.setScaleX(SCALE_START);
        content.setScaleY(SCALE_START);
        content.setTranslateY(20);

        FadeTransition fadeIn = new FadeTransition(FADE_DURATION, content);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setInterpolator(Interpolator.EASE_OUT);

        ScaleTransition scaleIn = new ScaleTransition(SCALE_DURATION, content);
        scaleIn.setFromX(SCALE_START);
        scaleIn.setFromY(SCALE_START);
        scaleIn.setToX(SCALE_END);
        scaleIn.setToY(SCALE_END);
        scaleIn.setInterpolator(Interpolator.EASE_OUT);

        TranslateTransition translateUp = new TranslateTransition(FADE_DURATION, content);
        translateUp.setFromY(20);
        translateUp.setToY(0);
        translateUp.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition entrance = new ParallelTransition(fadeIn, scaleIn, translateUp);
        entrance.play();
    }

    private void applyEnhancedButtonStylesWithAnimations(Dialog<?> dialog, ButtonType confirm, ButtonType delete, ButtonType cancel) {
        if (confirm != null) {
            Button btn = (Button) dialog.getDialogPane().lookupButton(confirm);
            String confirmStyle = currentTheme == ThemeMode.DARK ? DARK_CONFIRM_BUTTON_STYLE : LIGHT_CONFIRM_BUTTON_STYLE;
            String confirmHoverStyle = currentTheme == ThemeMode.DARK ? DARK_CONFIRM_BUTTON_HOVER_STYLE : LIGHT_CONFIRM_BUTTON_HOVER_STYLE;
            setupButtonAnimation(btn, confirmStyle, confirmHoverStyle);
        }
        if (delete != null) {
            Button btn = (Button) dialog.getDialogPane().lookupButton(delete);
            String deleteStyle = currentTheme == ThemeMode.DARK ? DARK_DELETE_BUTTON_STYLE : LIGHT_DELETE_BUTTON_STYLE;
            String deleteHoverStyle = currentTheme == ThemeMode.DARK ? DARK_DELETE_BUTTON_HOVER_STYLE : LIGHT_DELETE_BUTTON_HOVER_STYLE;
            setupButtonAnimation(btn, deleteStyle, deleteHoverStyle);
        }
        if (cancel != null) {
            Button btn = (Button) dialog.getDialogPane().lookupButton(cancel);
            String cancelStyle = currentTheme == ThemeMode.DARK ? DARK_CANCEL_BUTTON_STYLE : LIGHT_CANCEL_BUTTON_STYLE;
            String cancelHoverStyle = currentTheme == ThemeMode.DARK ? DARK_CANCEL_BUTTON_HOVER_STYLE : LIGHT_CANCEL_BUTTON_HOVER_STYLE;
            setupButtonAnimation(btn, cancelStyle, cancelHoverStyle);
        }
    }

    private void setupButtonAnimation(Button button, String normalStyle, String hoverStyle) {
        button.setStyle(normalStyle);

        ScaleTransition scaleUp = new ScaleTransition(BUTTON_ANIMATION_DURATION, button);
        scaleUp.setToX(1.02);
        scaleUp.setToY(1.02);

        ScaleTransition scaleDown = new ScaleTransition(BUTTON_ANIMATION_DURATION, button);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        button.setOnMouseEntered(e -> {
            button.setStyle(hoverStyle);
            scaleDown.stop();
            scaleUp.playFromStart();
        });

        button.setOnMouseExited(e -> {
            button.setStyle(normalStyle);
            scaleUp.stop();
            scaleDown.playFromStart();
        });

        // Press animation
        button.setOnMousePressed(e -> {
            ScaleTransition press = new ScaleTransition(Duration.millis(100), button);
            press.setToX(0.98);
            press.setToY(0.98);
            press.play();
        });

        button.setOnMouseReleased(e -> {
            ScaleTransition release = new ScaleTransition(Duration.millis(100), button);
            release.setToX(1.02);
            release.setToY(1.02);
            release.play();
        });
    }

    // ==================== MÉTHODES DE GESTION DE THÈME ====================

    private void updateContentTheme(VBox content) {
        content.getChildren().forEach(node -> {
            if (node instanceof GridPane) {
                updateGridPaneTheme((GridPane) node);
            } else if (node instanceof VBox) {
                updateVBoxTheme((VBox) node);
            }
        });
    }

    private void updateGridPaneTheme(GridPane grid) {
        grid.getChildren().forEach(node -> {
            if (node instanceof TextField) {
                applyTextFieldTheme((TextField) node);
            } else if (node instanceof DatePicker) {
                applyDatePickerTheme((DatePicker) node);
            } else if (node instanceof ComboBox) {
                applyComboBoxTheme((ComboBox<?>) node);
            } else if (node instanceof HBox) {
                updateHBoxTheme((HBox) node);
            }
        });
    }

    private void updateHBoxTheme(HBox hbox) {
        hbox.getChildren().forEach(node -> {
            if (node instanceof Text) {
                applyIconTheme((Text) node);
            } else if (node instanceof Label) {
                applyLabelTheme((Label) node);
            }
        });
    }

    private void updateVBoxTheme(VBox vbox) {
        vbox.getChildren().forEach(node -> {
            if (node instanceof Label) {
                Label label = (Label) node;
                if (label.getStyle() != null && label.getStyle().contains("font-weight: 700")) {
                    applyTitleTheme(label);
                } else if (label.getStyle() != null && label.getStyle().contains("font-style: italic")) {
                    applySubtitleTheme(label);
                } else {
                    applyLabelTheme(label);
                }
            }
        });
    }

    private void updateDialogButtonsTheme(Dialog<?> dialog) {
        DialogPane pane = dialog.getDialogPane();

        pane.getButtonTypes().forEach(buttonType -> {
            Button button = (Button) pane.lookupButton(buttonType);
            if (button != null) {
                String buttonText = button.getText();

                if (buttonText.equals("Enregistrer") || buttonText.equals("Créer")) {
                    String confirmStyle = currentTheme == ThemeMode.DARK ? DARK_CONFIRM_BUTTON_STYLE : LIGHT_CONFIRM_BUTTON_STYLE;
                    String confirmHoverStyle = currentTheme == ThemeMode.DARK ? DARK_CONFIRM_BUTTON_HOVER_STYLE : LIGHT_CONFIRM_BUTTON_HOVER_STYLE;
                    setupButtonAnimation(button, confirmStyle, confirmHoverStyle);
                } else if (buttonText.equals("Supprimer")) {
                    String deleteStyle = currentTheme == ThemeMode.DARK ? DARK_DELETE_BUTTON_STYLE : LIGHT_DELETE_BUTTON_STYLE;
                    String deleteHoverStyle = currentTheme == ThemeMode.DARK ? DARK_DELETE_BUTTON_HOVER_STYLE : LIGHT_DELETE_BUTTON_HOVER_STYLE;
                    setupButtonAnimation(button, deleteStyle, deleteHoverStyle);
                } else if (buttonText.equals("Annuler")) {
                    String cancelStyle = currentTheme == ThemeMode.DARK ? DARK_CANCEL_BUTTON_STYLE : LIGHT_CANCEL_BUTTON_STYLE;
                    String cancelHoverStyle = currentTheme == ThemeMode.DARK ? DARK_CANCEL_BUTTON_HOVER_STYLE : LIGHT_CANCEL_BUTTON_HOVER_STYLE;
                    setupButtonAnimation(button, cancelStyle, cancelHoverStyle);
                }
            }
        });
    }

    // ==================== MÉTHODES D'APPLICATION DE THÈME ====================

    private void applyTextFieldTheme(TextField field) {
        String fieldStyle = currentTheme == ThemeMode.DARK ? DARK_FIELD_STYLE : LIGHT_FIELD_STYLE;
        String focusedStyle = currentTheme == ThemeMode.DARK ? DARK_FIELD_FOCUSED_STYLE : LIGHT_FIELD_FOCUSED_STYLE;
        String errorStyle = currentTheme == ThemeMode.DARK ? DARK_FIELD_ERROR_STYLE : LIGHT_FIELD_ERROR_STYLE;

        if (field.isFocused()) {
            field.setStyle(focusedStyle);
        } else if (field.getText().isEmpty()) {
            field.setStyle(errorStyle);
        } else {
            field.setStyle(fieldStyle);
        }

        Tooltip tooltip = field.getTooltip();
        if (tooltip != null) {
            applyTooltipTheme(tooltip);
        }
    }

    private void applyDatePickerTheme(DatePicker picker) {
        String fieldStyle = currentTheme == ThemeMode.DARK ? DARK_FIELD_STYLE : LIGHT_FIELD_STYLE;
        String focusedStyle = currentTheme == ThemeMode.DARK ? DARK_FIELD_FOCUSED_STYLE : LIGHT_FIELD_FOCUSED_STYLE;

        if (picker.isFocused()) {
            picker.setStyle(focusedStyle);
        } else {
            picker.setStyle(fieldStyle);
        }

        Tooltip tooltip = picker.getTooltip();
        if (tooltip != null) {
            applyTooltipTheme(tooltip);
        }
    }

    private void applyComboBoxTheme(ComboBox<?> combo) {
        String fieldStyle = currentTheme == ThemeMode.DARK ? DARK_FIELD_STYLE : LIGHT_FIELD_STYLE;
        String focusedStyle = currentTheme == ThemeMode.DARK ? DARK_FIELD_FOCUSED_STYLE : LIGHT_FIELD_FOCUSED_STYLE;

        if (combo.isFocused()) {
            combo.setStyle(focusedStyle);
        } else {
            combo.setStyle(fieldStyle);
        }

        Tooltip tooltip = combo.getTooltip();
        if (tooltip != null) {
            applyTooltipTheme(tooltip);
        }
    }

    private void applyTitleTheme(Label label) {
        String titleStyle = currentTheme == ThemeMode.DARK ? DARK_TITLE_STYLE : LIGHT_TITLE_STYLE;
        label.setStyle(titleStyle);
    }

    private void applySubtitleTheme(Label label) {
        String subtitleStyle = currentTheme == ThemeMode.DARK ? DARK_SUBTITLE_STYLE : LIGHT_SUBTITLE_STYLE;
        label.setStyle(subtitleStyle);
    }

    private void applyLabelTheme(Label label) {
        String labelStyle = currentTheme == ThemeMode.DARK ? DARK_LABEL_STYLE : LIGHT_LABEL_STYLE;
        label.setStyle(labelStyle);
    }

    private void applyIconTheme(Text icon) {
        String iconStyle = currentTheme == ThemeMode.DARK ? DARK_ICON_STYLE : LIGHT_ICON_STYLE;
        icon.setStyle(iconStyle);
    }

    private void applyTooltipTheme(Tooltip tooltip) {
        String tooltipStyle = currentTheme == ThemeMode.DARK ? DARK_TOOLTIP_STYLE : LIGHT_TOOLTIP_STYLE;
        tooltip.setStyle(tooltipStyle);
    }
    private ComboBox<EnseignantItem> createEnseignantComboBox(String prompt) {
        ComboBox<EnseignantItem> comboBox = new ComboBox<>();
        comboBox.setPromptText(prompt);
        applyComboBoxTheme(comboBox);

        loadEnseignantsWithId(comboBox);

        Tooltip tooltip = new Tooltip(prompt);
        applyTooltipTheme(tooltip);
        comboBox.setTooltip(tooltip);

        return comboBox;
    }
    private void loadEnseignantsWithId(ComboBox<EnseignantItem> comboBox) {
        System.out.println("🔍 Chargement des enseignants avec IDs...");

        if (enseignantService == null) {
            System.err.println("❌ EnseignantService null !");
            return;
        }

        try {
            CompletableFuture<List<Enseignant>> futureEnseignants = enseignantService.getAllEnseignants();

            futureEnseignants.thenAccept(enseignants -> {
                Platform.runLater(() -> {
                    if (enseignants == null || enseignants.isEmpty()) {
                        System.out.println("⚠️ Aucun enseignant trouvé");
                        return;
                    }

                    List<EnseignantItem> enseignantItems = enseignants.stream()
                            .map(enseignant -> {
                                String nomComplet = (enseignant.getNom() + " " + enseignant.getPrenom()).trim();
                                return new EnseignantItem(enseignant.getId(), nomComplet);
                            })
                            .filter(item -> item.getId() != null)
                            .sorted((a, b) -> a.toString().compareToIgnoreCase(b.toString()))
                            .toList();

                    comboBox.getItems().setAll(enseignantItems);
                    System.out.println("✅ " + enseignantItems.size() + " enseignants chargés avec IDs");
                });
            });

        } catch (Exception e) {
            System.err.println("❌ Erreur chargement enseignants: " + e.getMessage());
        }
    }
    private ComboBox<SalleItem> createSalleComboBox(String prompt) {
        ComboBox<SalleItem> comboBox = new ComboBox<>();
        comboBox.setPromptText(prompt);
        applyComboBoxTheme(comboBox);

        loadSallesWithId(comboBox);

        Tooltip tooltip = new Tooltip(prompt);
        applyTooltipTheme(tooltip);
        comboBox.setTooltip(tooltip);

        return comboBox;
    }
    private void loadSallesWithId(ComboBox<SalleItem> comboBox) {
        System.out.println("🔍 Chargement des salles avec IDs...");

        if (SalleRestService == null) {
            System.err.println("❌ SalleService null !");
            return;
        }

        try {
            CompletableFuture<List<Map<String, Object>>> futureSalles = SalleRestService.getAllSalles();

            futureSalles.thenAccept(sallesMap -> {
                Platform.runLater(() -> {
                    if (sallesMap == null || sallesMap.isEmpty()) {
                        System.out.println("⚠️ Aucune salle trouvée");
                        return;
                    }

                    List<SalleItem> salleItems = sallesMap.stream()
                            .map(salleMap -> {
                                Object idObj = salleMap.get("id");
                                Object nomObj = salleMap.get("nom");

                                Long id = null;
                                if (idObj instanceof Number) {
                                    id = ((Number) idObj).longValue();
                                }

                                String nom = nomObj != null ? nomObj.toString() : "Salle Inconnue";

                                return new SalleItem(id, nom);
                            })
                            .filter(item -> item.getId() != null && item.getNom() != null)
                            .sorted((a, b) -> a.getNom().compareToIgnoreCase(b.getNom()))
                            .toList();

                    comboBox.getItems().setAll(salleItems);
                    System.out.println("✅ " + salleItems.size() + " salles chargées avec IDs");
                });
            });

        } catch (Exception e) {
            System.err.println("❌ Erreur chargement salles: " + e.getMessage());
        }
    }
    private static class EnseignantItem {
        private final Long id;
        private final String nomComplet;

        public EnseignantItem(Long id, String nomComplet) {
            this.id = id;
            this.nomComplet = nomComplet;
        }

        public Long getId() { return id; }

        @Override
        public String toString() {
            return nomComplet;
        }
    }

    private static class SalleItem {
        private final Long id;
        private final String nom;

        public SalleItem(Long id, String nom) {
            this.id = id;
            this.nom = nom;
        }

        public Long getId() { return id; }

        // ✅ CORRECTION : Ajout de la méthode getNom
        public String getNom() { return nom; }

        @Override
        public String toString() {
            return nom;
        }
    }
}
