package tn.iset.m2glnt.client.viewer.presenter.dialog;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import tn.iset.m2glnt.client.model.Slot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Factory pour créer des boîtes de dialogue permettant de créer ou éditer un créneau (Slot).
 * Utilise un style visuel cohérent, professionnel et responsive avec animations fluides.
 * Supporte les modes clair et sombre.
 */
public class SimpleSlotFormFactory implements SlotFormFactory {

    // Constantes pour animations et styles
    private static final Duration FADE_DURATION = Duration.millis(300);
    private static final Duration SCALE_DURATION = Duration.millis(400);
    private static final double SCALE_START = 0.95;
    private static final double SCALE_END = 1.0;

    public enum ThemeMode {
        LIGHT, DARK
    }

    // ==================== LIGHT MODE STYLES ====================
    private static final String LIGHT_DIALOG_STYLE = """
    -fx-background-color: linear-gradient(to bottom, #bbc8d8, #bbc8d8);
    -fx-border-color: #e5e7eb;
    -fx-border-radius: 20;
    -fx-background-radius: 20;
    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 20, 0, 0, 8);
    """;

    private static final String LIGHT_FIELD_STYLE = """
        -fx-background-color: #ffffff;
        -fx-background-radius: 12;
        -fx-border-radius: 12;
        -fx-border-color: #e5e7eb;
        -fx-border-width: 1.5;
        -fx-padding: 12;
        -fx-font-size: 13;
        -fx-text-fill: #1f2937;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 1);
        """;

    private static final String LIGHT_FIELD_FOCUSED_STYLE = """
        -fx-background-color: #ffffff;
        -fx-background-radius: 12;
        -fx-border-radius: 12;
        -fx-border-color: #3b82f6;
        -fx-border-width: 2;
        -fx-padding: 12;
        -fx-font-size: 13;
        -fx-text-fill: #1f2937;
        -fx-effect: dropshadow(gaussian, rgba(59,130,246,0.3), 12, 0, 0, 4);
        """;

    private static final String LIGHT_CONFIRM_BUTTON_STYLE = """
        -fx-background-color: linear-gradient(to bottom, #3b82f6, #2563eb);
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-font-size: 13;
        -fx-background-radius: 10;
        -fx-cursor: hand;
        -fx-padding: 12 28 12 28;
        -fx-effect: dropshadow(gaussian, rgba(59,130,246,0.4), 10, 0, 0, 3);
        """;

    private static final String LIGHT_CONFIRM_BUTTON_HOVER_STYLE = """
        -fx-background-color: linear-gradient(to bottom, #2563eb, #1d4ed8);
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-font-size: 13;
        -fx-background-radius: 10;
        -fx-cursor: hand;
        -fx-padding: 12 28 12 28;
        -fx-effect: dropshadow(gaussian, rgba(59,130,246,0.6), 14, 0, 0, 5);
        """;

    private static final String LIGHT_DELETE_BUTTON_STYLE = """
        -fx-background-color: linear-gradient(to bottom, #ef4444, #dc2626);
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-font-size: 13;
        -fx-background-radius: 10;
        -fx-cursor: hand;
        -fx-padding: 12 28 12 28;
        -fx-effect: dropshadow(gaussian, rgba(239,68,68,0.4), 10, 0, 0, 3);
        """;

    private static final String LIGHT_DELETE_BUTTON_HOVER_STYLE = """
        -fx-background-color: linear-gradient(to bottom, #dc2626, #b91c1c);
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-font-size: 13;
        -fx-background-radius: 10;
        -fx-cursor: hand;
        -fx-padding: 12 28 12 28;
        -fx-effect: dropshadow(gaussian, rgba(239,68,68,0.6), 14, 0, 0, 5);
        """;

    private static final String LIGHT_CANCEL_BUTTON_STYLE = """
        -fx-background-color: #f3f4f6;
        -fx-text-fill: #374151;
        -fx-font-weight: bold;
        -fx-font-size: 13;
        -fx-background-radius: 10;
        -fx-cursor: hand;
        -fx-padding: 12 28 12 28;
        -fx-border-color: #d1d5db;
        -fx-border-width: 1.5;
        """;

    private static final String LIGHT_CANCEL_BUTTON_HOVER_STYLE = """
        -fx-background-color: #e5e7eb;
        -fx-text-fill: #1f2937;
        -fx-font-weight: bold;
        -fx-font-size: 13;
        -fx-background-radius: 10;
        -fx-cursor: hand;
        -fx-padding: 12 28 12 28;
        -fx-border-color: #9ca3af;
        -fx-border-width: 1.5;
        """;

    private static final String LIGHT_LABEL_STYLE = """
        -fx-text-fill: #374151;
        -fx-font-weight: bold;
        -fx-font-size: 13;
        """;

    private static final String LIGHT_TITLE_STYLE = """
        -fx-text-fill: #111827;
        -fx-font-weight: bold;
        -fx-font-size: 18;
        """;

    private static final String LIGHT_SUBTITLE_STYLE = """
        -fx-text-fill: #6b7280;
        -fx-font-size: 12;
        """;

    private static final String LIGHT_ICON_STYLE = """
        -fx-fill: #6b7280;
        -fx-font-size: 14;
        """;

    private static final String LIGHT_FIELD_ERROR_STYLE = """
        -fx-background-color: #fef2f2;
        -fx-background-radius: 12;
        -fx-border-radius: 12;
        -fx-border-color: #f87171;
        -fx-border-width: 2;
        -fx-padding: 12;
        -fx-font-size: 13;
        -fx-text-fill: #1f2937;
        -fx-effect: dropshadow(gaussian, rgba(248,113,113,0.3), 12, 0, 0, 4);
        """;

    // ==================== DARK MODE STYLES ====================
    private static final String DARK_DIALOG_STYLE = """
    -fx-background-color: linear-gradient(to bottom, #1f2937, #111827);
    -fx-border-color: #374151;
    -fx-border-radius: 20;
    -fx-background-radius: 20;
    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 20, 0, 0, 8);
    """;

    private static final String DARK_FIELD_STYLE = """
        -fx-background-color: #2d3748;
        -fx-background-radius: 12;
        -fx-border-radius: 12;
        -fx-border-color: #4b5563;
        -fx-border-width: 1.5;
        -fx-padding: 12;
        -fx-font-size: 13;
        -fx-text-fill: #e5e7eb;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 1);
        """;

    private static final String DARK_FIELD_FOCUSED_STYLE = """
        -fx-background-color: #2d3748;
        -fx-background-radius: 12;
        -fx-border-radius: 12;
        -fx-border-color: #60a5fa;
        -fx-border-width: 2;
        -fx-padding: 12;
        -fx-font-size: 13;
        -fx-text-fill: #e5e7eb;
        -fx-effect: dropshadow(gaussian, rgba(96,165,250,0.3), 12, 0, 0, 4);
        """;

    private static final String DARK_CONFIRM_BUTTON_STYLE = """
        -fx-background-color: linear-gradient(to bottom, #2563eb, #1d4ed8);
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-font-size: 13;
        -fx-background-radius: 10;
        -fx-cursor: hand;
        -fx-padding: 12 28 12 28;
        -fx-effect: dropshadow(gaussian, rgba(37,99,235,0.4), 10, 0, 0, 3);
        """;

    private static final String DARK_CONFIRM_BUTTON_HOVER_STYLE = """
        -fx-background-color: linear-gradient(to bottom, #1d4ed8, #1e40af);
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-font-size: 13;
        -fx-background-radius: 10;
        -fx-cursor: hand;
        -fx-padding: 12 28 12 28;
        -fx-effect: dropshadow(gaussian, rgba(37,99,235,0.6), 14, 0, 0, 5);
        """;

    private static final String DARK_DELETE_BUTTON_STYLE = """
        -fx-background-color: linear-gradient(to bottom, #dc2626, #b91c1c);
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-font-size: 13;
        -fx-background-radius: 10;
        -fx-cursor: hand;
        -fx-padding: 12 28 12 28;
        -fx-effect: dropshadow(gaussian, rgba(220,38,38,0.4), 10, 0, 0, 3);
        """;

    private static final String DARK_DELETE_BUTTON_HOVER_STYLE = """
        -fx-background-color: linear-gradient(to bottom, #b91c1c, #991b1b);
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-font-size: 13;
        -fx-background-radius: 10;
        -fx-cursor: hand;
        -fx-padding: 12 28 12 28;
        -fx-effect: dropshadow(gaussian, rgba(220,38,38,0.6), 14, 0, 0, 5);
        """;

    private static final String DARK_CANCEL_BUTTON_STYLE = """
        -fx-background-color: #374151;
        -fx-text-fill: #e5e7eb;
        -fx-font-weight: bold;
        -fx-font-size: 13;
        -fx-background-radius: 10;
        -fx-cursor: hand;
        -fx-padding: 12 28 12 28;
        -fx-border-color: #4b5563;
        -fx-border-width: 1.5;
        """;

    private static final String DARK_CANCEL_BUTTON_HOVER_STYLE = """
        -fx-background-color: #4b5563;
        -fx-text-fill: #f3f4f6;
        -fx-font-weight: bold;
        -fx-font-size: 13;
        -fx-background-radius: 10;
        -fx-cursor: hand;
        -fx-padding: 12 28 12 28;
        -fx-border-color: #6b7280;
        -fx-border-width: 1.5;
        """;

    private static final String DARK_LABEL_STYLE = """
        -fx-text-fill: #d1d5db;
        -fx-font-weight: bold;
        -fx-font-size: 13;
        """;

    private static final String DARK_TITLE_STYLE = """
        -fx-text-fill: #f3f4f6;
        -fx-font-weight: bold;
        -fx-font-size: 18;
        """;

    private static final String DARK_SUBTITLE_STYLE = """
        -fx-text-fill: #9ca3af;
        -fx-font-size: 12;
        """;

    private static final String DARK_ICON_STYLE = """
        -fx-fill: #9ca3af;
        -fx-font-size: 14;
        """;

    private static final String DARK_FIELD_ERROR_STYLE = """
        -fx-background-color: #7f1d1d;
        -fx-background-radius: 12;
        -fx-border-radius: 12;
        -fx-border-color: #f87171;
        -fx-border-width: 2;
        -fx-padding: 12;
        -fx-font-size: 13;
        -fx-text-fill: #fee2e2;
        -fx-effect: dropshadow(gaussian, rgba(248,113,113,0.3), 12, 0, 0, 4);
        """;

    private final List<LocalTime> possibleTimes;
    private final List<java.time.Duration> possibleDurations;
    private ThemeMode currentTheme = ThemeMode.LIGHT; // Added theme mode property

    public SimpleSlotFormFactory(List<LocalTime> possibleTimes, List<java.time.Duration> possibleDurations) {
        this.possibleTimes = possibleTimes;
        this.possibleDurations = possibleDurations;
    }

    public void setThemeMode(ThemeMode theme) {
        this.currentTheme = theme;
    }

    public ThemeMode getThemeMode() {
        return currentTheme;
    }

    @Override
    public Dialog<SlotFormResult> createCalendarEventDialog(Slot slotInfo) {
        return createModernCalendarEventDialog(slotInfo);
    }

    @Override
    public Dialog<SlotFormResult> createCalendarEventDialog(LocalDateTime defaultDateTime, int defaultDurationIndex) {
        return createModernCalendarEventDialog(defaultDateTime, defaultDurationIndex);
    }

    // ==================== MÉTHODES MODERN ====================
    private Dialog<SlotFormResult> createModernCalendarEventDialog(Slot slotInfo) {
        Dialog<SlotFormResult> dialog = new Dialog<>();
        dialog.setHeaderText(null);
        initializeModernDialogStyle(dialog);

        TextField descriptionField = new TextField(slotInfo.description());
        descriptionField.setPromptText("Entrez une description détaillée...");
        DatePicker datePicker = new DatePicker(slotInfo.startDateTime().toLocalDate());
        ComboBox<LocalTime> timeBox = createModernTimeComboBox(slotInfo.startDateTime().toLocalTime());
        ComboBox<java.time.Duration> durationBox = createModernDurationComboBox(slotInfo.duration());

        VBox content = buildModernFormGrid(descriptionField, datePicker, timeBox, durationBox,
                "Modifier le créneau", "Mettez à jour les détails du créneau");
        dialog.getDialogPane().setContent(content);
        applyEntranceAnimation(content);

        ButtonType deleteBtn = new ButtonType("Supprimer", ButtonBar.ButtonData.LEFT);
        ButtonType cancelBtn = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType confirmBtn = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(deleteBtn, cancelBtn, confirmBtn);

        applyModernButtonStylesWithAnimations(dialog, confirmBtn, deleteBtn, cancelBtn);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == confirmBtn) {
                return new SlotFormResult(SlotFormAction.CONFIRM,
                        new SlotFormContent(timeBox.getValue(), datePicker.getValue(), durationBox.getValue(), descriptionField.getText()));
            } else if (buttonType == deleteBtn) {
                return new SlotFormResult(SlotFormAction.DELETE,
                        new SlotFormContent(slotInfo.startDateTime().toLocalTime(), slotInfo.startDateTime().toLocalDate(),
                                slotInfo.duration(), slotInfo.description()));
            }
            return null;
        });

        return dialog;
    }

    private Dialog<SlotFormResult> createModernCalendarEventDialog(LocalDateTime defaultDateTime, int defaultDurationIndex) {
        Dialog<SlotFormResult> dialog = new Dialog<>();
        dialog.setHeaderText(null);
        initializeModernDialogStyle(dialog);

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Entrez une description détaillée...");
        DatePicker datePicker = new DatePicker(defaultDateTime.toLocalDate());
        ComboBox<LocalTime> timeBox = createModernTimeComboBox(defaultDateTime.toLocalTime());
        ComboBox<java.time.Duration> durationBox = createModernDurationComboBox(null);
        if (defaultDurationIndex >= 0 && defaultDurationIndex < possibleDurations.size()) {
            durationBox.getSelectionModel().select(defaultDurationIndex);
        }

        VBox content = buildModernFormGrid(descriptionField, datePicker, timeBox, durationBox,
                "Créer un nouveau créneau", "Remplissez les détails du créneau");
        dialog.getDialogPane().setContent(content);
        applyEntranceAnimation(content);

        ButtonType cancelBtn = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType confirmBtn = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(cancelBtn, confirmBtn);

        applyModernButtonStylesWithAnimations(dialog, confirmBtn, null, cancelBtn);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == confirmBtn) {
                return new SlotFormResult(SlotFormAction.CONFIRM,
                        new SlotFormContent(timeBox.getValue(), datePicker.getValue(), durationBox.getValue(), descriptionField.getText()));
            }
            return null;
        });

        return dialog;
    }

    private void initializeModernDialogStyle(Dialog<?> dialog) {
        DialogPane pane = dialog.getDialogPane();
        String dialogStyle = currentTheme == ThemeMode.DARK ? DARK_DIALOG_STYLE : LIGHT_DIALOG_STYLE;
        pane.setStyle(dialogStyle);
        pane.setPadding(new Insets(30, 35, 30, 35));
    }

    private ComboBox<LocalTime> createModernTimeComboBox(LocalTime defaultValue) {
        ComboBox<LocalTime> box = new ComboBox<>();
        box.getItems().addAll(possibleTimes);
        box.setValue(defaultValue);
        String fieldStyle = currentTheme == ThemeMode.DARK ? DARK_FIELD_STYLE : LIGHT_FIELD_STYLE;
        String focusedStyle = currentTheme == ThemeMode.DARK ? DARK_FIELD_FOCUSED_STYLE : LIGHT_FIELD_FOCUSED_STYLE;
        box.setStyle(fieldStyle);
        box.setMaxWidth(Double.MAX_VALUE);
        box.focusedProperty().addListener((obs, oldVal, newVal) -> box.setStyle(newVal ? focusedStyle : fieldStyle));
        return box;
    }

    private ComboBox<java.time.Duration> createModernDurationComboBox(java.time.Duration defaultValue) {
        ComboBox<java.time.Duration> box = new ComboBox<>();
        box.getItems().addAll(possibleDurations);
        if (defaultValue != null) box.setValue(defaultValue);
        String fieldStyle = currentTheme == ThemeMode.DARK ? DARK_FIELD_STYLE : LIGHT_FIELD_STYLE;
        String focusedStyle = currentTheme == ThemeMode.DARK ? DARK_FIELD_FOCUSED_STYLE : LIGHT_FIELD_FOCUSED_STYLE;
        box.setStyle(fieldStyle);
        box.setMaxWidth(Double.MAX_VALUE);
        box.focusedProperty().addListener((obs, oldVal, newVal) -> box.setStyle(newVal ? focusedStyle : fieldStyle));
        return box;
    }

    private VBox buildModernFormGrid(TextField description, DatePicker datePicker,
                                     ComboBox<LocalTime> timeBox, ComboBox<java.time.Duration> durationBox,
                                     String title, String subtitle) {
        VBox container = new VBox();
        container.setSpacing(24);

        VBox headerBox = new VBox();
        headerBox.setSpacing(6);
        Label titleLabel = new Label(title);
        String titleStyle = currentTheme == ThemeMode.DARK ? DARK_TITLE_STYLE : LIGHT_TITLE_STYLE;
        String subtitleStyle = currentTheme == ThemeMode.DARK ? DARK_SUBTITLE_STYLE : LIGHT_SUBTITLE_STYLE;
        titleLabel.setStyle(titleStyle);
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setStyle(subtitleStyle);
        headerBox.getChildren().addAll(titleLabel, subtitleLabel);

        GridPane grid = new GridPane();
        grid.setVgap(20);
        grid.setHgap(20);
        grid.setAlignment(Pos.TOP_LEFT);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(35);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(65);
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);

        String labelStyle = currentTheme == ThemeMode.DARK ? DARK_LABEL_STYLE : LIGHT_LABEL_STYLE;
        String iconStyle = currentTheme == ThemeMode.DARK ? DARK_ICON_STYLE : LIGHT_ICON_STYLE;
        String fieldStyle = currentTheme == ThemeMode.DARK ? DARK_FIELD_STYLE : LIGHT_FIELD_STYLE;
        String focusedStyle = currentTheme == ThemeMode.DARK ? DARK_FIELD_FOCUSED_STYLE : LIGHT_FIELD_FOCUSED_STYLE;
        String errorStyle = currentTheme == ThemeMode.DARK ? DARK_FIELD_ERROR_STYLE : LIGHT_FIELD_ERROR_STYLE;

        HBox descLabelBox = createLabelWithIcon("📝", "Description", labelStyle, iconStyle);
        HBox dateLabelBox = createLabelWithIcon("📅", "Date", labelStyle, iconStyle);
        HBox timeLabelBox = createLabelWithIcon("⏰", "Heure", labelStyle, iconStyle);
        HBox durationLabelBox = createLabelWithIcon("⏰", "Durée", labelStyle, iconStyle);

        description.setTooltip(new Tooltip("Entrez une description claire et concise pour ce créneau."));
        datePicker.setTooltip(new Tooltip("Sélectionnez la date du créneau."));
        timeBox.setTooltip(new Tooltip("Choisissez l'heure de début."));
        durationBox.setTooltip(new Tooltip("Sélectionnez la durée du créneau."));

        description.setStyle(fieldStyle);
        datePicker.setStyle(fieldStyle);
        description.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                description.setStyle(focusedStyle);
            } else {
                description.setStyle(description.getText().isEmpty() ? errorStyle : fieldStyle);
            }
        });
        datePicker.focusedProperty().addListener((obs, oldVal, newVal) -> {
            datePicker.setStyle(newVal ? focusedStyle : fieldStyle);
        });

        grid.add(descLabelBox, 0, 0);
        grid.add(description, 1, 0);
        grid.add(dateLabelBox, 0, 1);
        grid.add(datePicker, 1, 1);
        grid.add(timeLabelBox, 0, 2);
        grid.add(timeBox, 1, 2);
        grid.add(durationLabelBox, 0, 3);
        grid.add(durationBox, 1, 3);

        container.getChildren().addAll(headerBox, grid);
        return container;
    }

    private HBox createLabelWithIcon(String iconText, String labelText, String labelStyle, String iconStyle) {
        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER_LEFT);
        Text icon = new Text(iconText);
        icon.setStyle(iconStyle);
        Label label = new Label(labelText);
        label.setStyle(labelStyle);
        box.getChildren().addAll(icon, label);
        return box;
    }

    private void applyEntranceAnimation(VBox content) {
        content.setOpacity(0);
        content.setScaleX(SCALE_START);
        content.setScaleY(SCALE_START);

        FadeTransition fadeIn = new FadeTransition(FADE_DURATION, content);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        ScaleTransition scaleIn = new ScaleTransition(SCALE_DURATION, content);
        scaleIn.setFromX(SCALE_START);
        scaleIn.setFromY(SCALE_START);
        scaleIn.setToX(SCALE_END);
        scaleIn.setToY(SCALE_END);

        ParallelTransition entrance = new ParallelTransition(fadeIn, scaleIn);
        entrance.play();
    }

    private void applyModernButtonStylesWithAnimations(Dialog<?> dialog, ButtonType confirm, ButtonType delete, ButtonType cancel) {
        if (confirm != null) {
            Button btn = (Button) dialog.getDialogPane().lookupButton(confirm);
            String confirmStyle = currentTheme == ThemeMode.DARK ? DARK_CONFIRM_BUTTON_STYLE : LIGHT_CONFIRM_BUTTON_STYLE;
            String confirmHoverStyle = currentTheme == ThemeMode.DARK ? DARK_CONFIRM_BUTTON_HOVER_STYLE : LIGHT_CONFIRM_BUTTON_HOVER_STYLE;
            btn.setStyle(confirmStyle);
            btn.setOnMouseEntered(e -> btn.setStyle(confirmHoverStyle));
            btn.setOnMouseExited(e -> btn.setStyle(confirmStyle));
        }
        if (delete != null) {
            Button btn = (Button) dialog.getDialogPane().lookupButton(delete);
            String deleteStyle = currentTheme == ThemeMode.DARK ? DARK_DELETE_BUTTON_STYLE : LIGHT_DELETE_BUTTON_STYLE;
            String deleteHoverStyle = currentTheme == ThemeMode.DARK ? DARK_DELETE_BUTTON_HOVER_STYLE : LIGHT_DELETE_BUTTON_HOVER_STYLE;
            btn.setStyle(deleteStyle);
            btn.setOnMouseEntered(e -> btn.setStyle(deleteHoverStyle));
            btn.setOnMouseExited(e -> btn.setStyle(deleteStyle));
        }
        if (cancel != null) {
            Button btn = (Button) dialog.getDialogPane().lookupButton(cancel);
            String cancelStyle = currentTheme == ThemeMode.DARK ? DARK_CANCEL_BUTTON_STYLE : LIGHT_CANCEL_BUTTON_STYLE;
            String cancelHoverStyle = currentTheme == ThemeMode.DARK ? DARK_CANCEL_BUTTON_HOVER_STYLE : LIGHT_CANCEL_BUTTON_HOVER_STYLE;
            btn.setStyle(cancelStyle);
            btn.setOnMouseEntered(e -> btn.setStyle(cancelHoverStyle));
            btn.setOnMouseExited(e -> btn.setStyle(cancelStyle));
        }
    }
}
