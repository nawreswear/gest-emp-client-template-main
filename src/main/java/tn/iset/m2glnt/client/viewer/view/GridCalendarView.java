package tn.iset.m2glnt.client.viewer.view;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.iset.m2glnt.client.viewer.presenter.dialog.SimpleSlotFormFactory.ThemeMode;

import java.util.*;

/**
 * Premium Calendar View - Elegant Design with Enhanced Spacing
 * Features: Beautiful gradients, larger spacing, close button, Dark/Light Mode with toggle button
 */
public class GridCalendarView extends GridPane implements CalendarView {

    private final List<Label> firstRowLabels = new ArrayList<>();
    private final List<Label> firstColumnLabels = new ArrayList<>();
    private final Map<Integer, SlotView> slotViewMap = new HashMap<>();
    private Node buttonBox;
    private Scene scene;
    private Button closeButton;
    private Button themeButton;

    // 🎨 Sophisticated Color Palette
    private static final String[][] COLOR_SCHEMES = {
            {"#E8EAF6", "#5C6BC0", "#3F51B5"}, // Indigo
            {"#F3E5F5", "#AB47BC", "#9C27B0"}, // Purple
            {"#E1F5FE", "#29B6F6", "#039BE5"}, // Light Blue
            {"#E8F5E9", "#66BB6A", "#43A047"}, // Green
            {"#FFF3E0", "#FFA726", "#FB8C00"}, // Orange
            {"#FCE4EC", "#EC407A", "#E91E63"}, // Pink
            {"#FFF9C4", "#FFEB3B", "#FBC02D"}, // Yellow
            {"#FFEBEE", "#EF5350", "#E53935"}  // Red
    };

    // 🌓 Thème : clair / sombre
    private boolean isDarkMode = false;

    // 🔵 Nouveau : dégradé bleu pour le fond clair
    private static final String LIGHT_SCENE_BACKGROUND_GRADIENT = "linear-gradient(to bottom right, #E3F2FD, #BBDEFB)";
    private static final String DARK_SCENE_BACKGROUND = "#1E1E1E";

    // 📦 Carte semi-transparente (mode clair)
    private static final String LIGHT_CALENDAR_CARD = "-fx-background-color: rgba(255, 255, 255, 0.85);";
    private static final String DARK_CALENDAR_CARD = "-fx-background-color: rgba(30, 30, 30, 0.95);";

    // 📏 Couleurs des lignes de grille subtiles
    private static final String LIGHT_GRID_LINE = "rgba(224, 224, 224, 0.7)";
    private static final String DARK_GRID_LINE = "rgba(66, 66, 66, 0.6)";

    private static final String LIGHT_HEADER_START = "#2C3E50";
    private static final String LIGHT_HEADER_END = "#34495E";
    private static final String DARK_HEADER_START = "#0D1B2A";
    private static final String DARK_HEADER_END = "#1B263B";
    private static final String LIGHT_TEXT = "white";
    private static final String DARK_TEXT = "#E0E0E0";

    private int colorIndex = 0;
    private final DoubleProperty scaleFactor = new SimpleDoubleProperty(1.0);

    // 🔄 Listeners pour la synchronisation du thème
    private final List<ThemeChangeListener> themeListeners = new ArrayList<>();

    public GridCalendarView() {
        super();
        initializeGrid();
        applyResponsiveStyles();
    }

    private void initializeGrid() {
        setHgap(15);
        setVgap(15);
        setPadding(new Insets(30));
        setAlignment(Pos.CENTER);
        applyGridBackground();
        applyCurrentTheme();
    }

    private void applyGridBackground() {
        String gridBg = isDarkMode ? "#2D2D2D" : "#FFFFFF";

        // Style simplifié compatible avec JavaFX
        String gridStyle = String.format(
                "-fx-background-color: %s;",
                gridBg
        );

        setStyle(gridStyle);
    }

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

    @Override
    public void removeSlot(int slotId) {
        SlotView eventView = slotViewMap.remove(slotId);
        if (eventView != null) {
            FadeTransition fade = new FadeTransition(Duration.millis(300), eventView);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fade.setOnFinished(e -> getChildren().remove(eventView));
            fade.play();
        }
    }

    @Override
    public Scene constructScene() {
        if (scene == null) {
            VBox mainContainer = new VBox(0);
            mainContainer.setStyle("-fx-background-color: " + (isDarkMode ? DARK_SCENE_BACKGROUND : LIGHT_SCENE_BACKGROUND_GRADIENT) + ";");

            HBox header = createHeader();

            StackPane calendarWrapper = new StackPane(this);
            calendarWrapper.setPadding(new Insets(20));
            calendarWrapper.setStyle((isDarkMode ? DARK_CALENDAR_CARD : LIGHT_CALENDAR_CARD) +
                    "-fx-background-radius: 24;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 8);");
            VBox.setVgrow(calendarWrapper, Priority.ALWAYS);

            mainContainer.getChildren().addAll(header, calendarWrapper);

            scene = new Scene(mainContainer, 1200, 700);
            scene.setFill(Color.TRANSPARENT);
            applyCurrentTheme();
        }
        return scene;
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(16, 30, 16, 30));
        header.setAlignment(Pos.CENTER_LEFT);

        HBox titleContainer = new HBox(12);
        titleContainer.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label("📅");
        icon.setFont(Font.font("System", FontWeight.BOLD, 24));
        icon.setTextFill(Color.web(isDarkMode ? DARK_TEXT : LIGHT_TEXT));

        Label title = new Label("Calendrier des Cours");
        title.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 24));
        title.setTextFill(Color.web(isDarkMode ? DARK_TEXT : LIGHT_TEXT));
        title.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 1, 1);");

        titleContainer.getChildren().addAll(icon, title);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        themeButton = new Button(getThemeIcon());
        themeButton.setFont(Font.font("System", FontWeight.BOLD, 18));
        themeButton.setMinSize(44, 44);
        themeButton.setPrefSize(44, 44);
        themeButton.setMaxSize(44, 44);
        styleThemeButton(themeButton, false);

        themeButton.setOnMouseEntered(e -> styleThemeButton(themeButton, true));
        themeButton.setOnMouseExited(e -> styleThemeButton(themeButton, false));

        themeButton.setOnAction(e -> {
            toggleTheme();
            themeButton.setText(getThemeIcon());
        });

        closeButton = new Button("✕");
        closeButton.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        closeButton.setMinSize(44, 44);
        closeButton.setPrefSize(44, 44);
        closeButton.setMaxSize(44, 44);
        styleCloseButton(closeButton, false);

        closeButton.setOnMouseEntered(e -> styleCloseButton(closeButton, true));
        closeButton.setOnMouseExited(e -> styleCloseButton(closeButton, false));

        closeButton.setOnAction(e -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            FadeTransition fade = new FadeTransition(Duration.millis(250), stage.getScene().getRoot());
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fade.setOnFinished(event -> stage.close());
            fade.play();
        });

        header.getChildren().addAll(titleContainer, spacer, themeButton, closeButton);
        header.setMinHeight(70);
        header.setPrefHeight(70);
        header.setMaxHeight(70);

        return header;
    }

    private String getThemeIcon() {
        return isDarkMode ? "☀️" : "🌙";
    }

    private void styleThemeButton(Button btn, boolean hover) {
        if (hover) {
            btn.setStyle(
                    "-fx-background-color: " + (isDarkMode ? "#4A6572" : "#3498DB") + ";" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 22;" +
                            "-fx-cursor: hand;" +
                            "-fx-font-weight: bold;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 2);" +
                            "-fx-scale-x: 1.05;" +
                            "-fx-scale-y: 1.05;"
            );
        } else {
            String borderColor = isDarkMode ? "#555555" : "#BDC3C7";
            btn.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-text-fill: " + borderColor + ";" +
                            "-fx-border-color: " + borderColor + ";" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 22;" +
                            "-fx-background-radius: 22;" +
                            "-fx-cursor: hand;" +
                            "-fx-font-weight: bold;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 1);"
            );
        }
    }

    private void styleCloseButton(Button btn, boolean hover) {
        if (hover) {
            btn.setStyle(
                    "-fx-background-color: #E74C3C;" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 22;" +
                            "-fx-cursor: hand;" +
                            "-fx-font-weight: bold;" +
                            "-fx-effect: dropshadow(gaussian, rgba(231,76,60,0.4), 8, 0, 0, 2);" +
                            "-fx-scale-x: 1.05;" +
                            "-fx-scale-y: 1.05;"
            );
        } else {
            String borderColor = isDarkMode ? "#555555" : "#BDC3C7";
            btn.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-text-fill: " + borderColor + ";" +
                            "-fx-border-color: " + borderColor + ";" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 22;" +
                            "-fx-background-radius: 22;" +
                            "-fx-cursor: hand;" +
                            "-fx-font-weight: bold;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 1);"
            );
        }
    }

    @Override
    public void addLabelInFirstColumn(Label label, int rowIndex) {
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMinHeight(40);
        label.setAlignment(Pos.CENTER);
        label.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));

        String bgColor = isDarkMode ? "#37474F" : "#546E7A";
        label.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                        "-fx-text-fill: " + (isDarkMode ? "#E0E0E0" : "white") + ";" +
                        "-fx-padding: 6 12 6 12;" +
                        "-fx-background-radius: 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.45), 6, 0, 0, 3);"
        );

        add(label, 0, rowIndex);
        firstColumnLabels.add(label);
        animateLabelEntrance(label, rowIndex);
    }

    @Override
    public void clearViewSlots() {
        getChildren().removeAll(slotViewMap.values());
        slotViewMap.clear();
        colorIndex = 0;
    }

    @Override
    public void addSlotView(SlotView slotView, int rowIndex, int columnIndex,
                            int rowSpan, int colSpan) {

        int idx = colorIndex % COLOR_SCHEMES.length;
        String bgColor = COLOR_SCHEMES[idx][0];
        String borderColor = COLOR_SCHEMES[idx][1];
        String hoverColor = COLOR_SCHEMES[idx][2];
        colorIndex++;

        String baseStyle = String.format(
                "-fx-background-color: %s;" +
                        "-fx-border-color: %s;" +
                        "-fx-border-width: 3;" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 15;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 8, 0, 0, 4);" +
                        "-fx-cursor: hand;",
                bgColor, borderColor
        );

        slotView.setStyle(baseStyle);
        slotView.setOpacity(0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(450), slotView);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(450), slotView);
        slideIn.setFromY(-25);
        slideIn.setToY(0);

        ParallelTransition entrance = new ParallelTransition(fadeIn, slideIn);
        entrance.setDelay(Duration.millis(colorIndex * 18));

        slotView.setOnMouseEntered(e -> {
            String hoverStyle = String.format(
                    "-fx-background-color: %s;" +
                            "-fx-border-color: %s;" +
                            "-fx-border-width: 4;" +
                            "-fx-border-radius: 12;" +
                            "-fx-background-radius: 12;" +
                            "-fx-padding: 15;" +
                            "-fx-effect: dropshadow(gaussian, %s, 18, 0.6, 0, 6);" +
                            "-fx-cursor: hand;",
                    bgColor, hoverColor, hoverColor
            );
            slotView.setStyle(hoverStyle);

            ScaleTransition scale = new ScaleTransition(Duration.millis(180), slotView);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();
        });

        slotView.setOnMouseExited(e -> {
            slotView.setStyle(baseStyle);
            ScaleTransition scale = new ScaleTransition(Duration.millis(180), slotView);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });

        slotViewMap.put(slotView.getSlotId(), slotView);
        add(slotView, columnIndex, rowIndex, colSpan, rowSpan);
        entrance.play();
    }

    @Override
    public void addLabelInFirstRow(Label label, int columnIndex) {
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);

        double fontSize = 14 * scaleFactor.get();
        label.setFont(Font.font("System", FontWeight.BOLD, fontSize));

        if (columnIndex == 0) {
            label.setStyle(
                    "-fx-background-color: " + (isDarkMode ? "#37474F" : "#546E7A") + ";" +
                            "-fx-text-fill: " + (isDarkMode ? "#E0E0E0" : "white") + ";" +
                            "-fx-padding: 10 16 10 16;" +
                            "-fx-background-radius: 12;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.45), 10, 0, 0, 3);"
            );
        } else {
            String bgColor = isDarkMode ? "#0D47A1" : "#1976D2";
            String shadowColor = isDarkMode ? "rgba(13,71,161,0.7)" : "rgba(25,118,210,0.6)";
            label.setStyle(
                    "-fx-background-color: " + bgColor + ";" +
                            "-fx-text-fill: " + (isDarkMode ? "#E3F2FD" : "white") + ";" +
                            "-fx-padding: 16 10 16 10;" +
                            "-fx-background-radius: 12;" +
                            "-fx-effect: dropshadow(gaussian, " + shadowColor + ", 12, 0, 0, 4);"
            );
        }

        GridPane.setFillWidth(label, true);
        GridPane.setFillHeight(label, true);
        add(label, columnIndex, 0);
        firstRowLabels.add(label);
        animateLabelEntrance(label, columnIndex);
    }

    private void animateLabelEntrance(Label label, int index) {
        label.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(350), label);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(Duration.millis(index * 22));
        fade.play();
    }

    @Override
    public void clearLabelsInFirstRow() {
        getChildren().removeAll(firstRowLabels);
        firstRowLabels.clear();
    }

    @Override
    public void clearLabelsInFirstColumn() {
        getChildren().removeAll(firstColumnLabels);
        firstColumnLabels.clear();
    }

    @Override
    public void addButtonBoxInTopLeftCell(HBox buttonBox) {
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(2);
        buttonBox.setPadding(new Insets(4));

        String bg = isDarkMode ? "#2D2D2D" : "#F5F5F5";
        String border = isDarkMode ? "#444444" : "#BDBDBD";
        buttonBox.setStyle(
                "-fx-background-color: " + bg + ";" +
                        "-fx-border-color: " + border + ";" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 6, 0, 0, 2);"
        );

        buttonBox.getChildren().forEach(node -> {
            if (node instanceof Button) {
                Button button = (Button) node;

                String normalBg = isDarkMode ? "#0D47A1" : "#1976D2";
                String hoverBg = isDarkMode ? "#0B3A85" : "#1565C0";

                String normalStyle =
                        "-fx-background-color: " + normalBg + ";" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 11px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-padding: 6 10 6 10;" +
                                "-fx-background-radius: 6;" +
                                "-fx-border-radius: 6;" +
                                "-fx-cursor: hand;" +
                                "-fx-min-width: 32px;" +
                                "-fx-max-width: 28px;" +
                                "-fx-min-height: 28px;" +
                                "-fx-max-height: 32px;";

                String hoverStyle =
                        "-fx-background-color: " + hoverBg + ";" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 11px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-padding: 6 10 6 10;" +
                                "-fx-background-radius: 6;" +
                                "-fx-border-radius: 6;" +
                                "-fx-cursor: hand;" +
                                "-fx-min-width: 32px;" +
                                "-fx-max-width: 28px;" +
                                "-fx-min-height: 28px;" +
                                "-fx-max-height: 32px;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0, 0, 2);";

                button.setStyle(normalStyle);
                button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
                button.setOnMouseExited(e -> button.setStyle(normalStyle));
            }
        });

        add(buttonBox, 0, 0);
        this.buttonBox = buttonBox;

        GridPane.setFillWidth(buttonBox, true);
        GridPane.setFillHeight(buttonBox, true);
    }

    @Override
    public void clearButtonBoxInFirstCell() {
        if (buttonBox != null) {
            getChildren().remove(buttonBox);
            buttonBox = null;
        }
    }

    public void pulseSlot(int slotId) {
        SlotView slot = slotViewMap.get(slotId);
        if (slot != null) {
            ScaleTransition pulse = new ScaleTransition(Duration.millis(350), slot);
            pulse.setFromX(1.0);
            pulse.setFromY(1.0);
            pulse.setToX(1.10);
            pulse.setToY(1.10);
            pulse.setCycleCount(2);
            pulse.setAutoReverse(true);
            pulse.play();
        }
    }

    public double getScaleFactor() {
        return scaleFactor.get();
    }

    // 🔄 NOUVELLES MÉTHODES POUR LA SYNCHRONISATION DU THÈME

    public void toggleTheme() {
        isDarkMode = !isDarkMode;
        applyCurrentTheme();
        notifyThemeChangeListeners();
    }

    public boolean isDarkMode() {
        return isDarkMode;
    }

    public ThemeMode getCurrentThemeMode() {
        return isDarkMode ? ThemeMode.DARK : ThemeMode.LIGHT;
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

    public interface ThemeChangeListener {
        void onThemeChanged(ThemeMode newTheme);
    }

    private void applyCurrentTheme() {
        applyGridBackground();

        String gridShadow = isDarkMode ?
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 25, 0, 0, 8);" :
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 25, 0, 0, 8);";

        setStyle(getStyle() + gridShadow);

        if (scene != null) {
            VBox mainContainer = (VBox) scene.getRoot();
            mainContainer.setStyle("-fx-background-color: " + (isDarkMode ? DARK_SCENE_BACKGROUND : LIGHT_SCENE_BACKGROUND_GRADIENT) + ";");

            StackPane calendarWrapper = (StackPane) mainContainer.getChildren().get(1);
            calendarWrapper.setStyle((isDarkMode ? DARK_CALENDAR_CARD : LIGHT_CALENDAR_CARD) +
                    "-fx-background-radius: 24;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 8);");
        }

        if (closeButton != null) {
            HBox header = (HBox) closeButton.getParent();
            String headerGradient = isDarkMode ?
                    "linear-gradient(to right, " + DARK_HEADER_START + ", " + DARK_HEADER_END + ")" :
                    "linear-gradient(to right, " + LIGHT_HEADER_START + ", " + LIGHT_HEADER_END + ")";
            header.setStyle(
                    "-fx-background-color: " + headerGradient + ";" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 15, 0, 0, 5);" +
                            "-fx-border-color: " + (isDarkMode ? "#0A111A" : "#1A252F") + ";" +
                            "-fx-border-width: 0 0 1 0;"
            );

            HBox titleContainer = (HBox) header.getChildren().get(0);
            Label icon = (Label) titleContainer.getChildren().get(0);
            Label title = (Label) titleContainer.getChildren().get(1);
            String textColor = isDarkMode ? DARK_TEXT : LIGHT_TEXT;
            icon.setTextFill(Color.web(textColor));
            title.setTextFill(Color.web(textColor));

            if (themeButton != null) {
                themeButton.setText(getThemeIcon());
                styleThemeButton(themeButton, false);
            }
            styleCloseButton(closeButton, false);
        }

        updateFirstRowLabelsTheme();
        updateFirstColumnLabelsTheme();

        if (buttonBox != null) {
            String bg = isDarkMode ? "#2D2D2D" : "#F5F5F5";
            String border = isDarkMode ? "#444444" : "#BDBDBD";
            buttonBox.setStyle(
                    "-fx-background-color: " + bg + ";" +
                            "-fx-border-color: " + border + ";" +
                            "-fx-border-width: 1;" +
                            "-fx-border-radius: 12;" +
                            "-fx-background-radius: 12;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 6, 0, 0, 2);"
            );
        }
    }

    private void updateFirstRowLabelsTheme() {
        for (int i = 0; i < firstRowLabels.size(); i++) {
            Label label = firstRowLabels.get(i);
            if (i == 0) {
                label.setStyle(
                        "-fx-background-color: " + (isDarkMode ? "#37474F" : "#546E7A") + ";" +
                                "-fx-text-fill: " + (isDarkMode ? "#E0E0E0" : "white") + ";" +
                                "-fx-padding: 10 16 10 16;" +
                                "-fx-background-radius: 12;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.45), 10, 0, 0, 3);"
                );
            } else {
                String bgColor = isDarkMode ? "#0D47A1" : "#1976D2";
                String shadowColor = isDarkMode ? "rgba(13,71,161,0.7)" : "rgba(25,118,210,0.6)";
                label.setStyle(
                        "-fx-background-color: " + bgColor + ";" +
                                "-fx-text-fill: " + (isDarkMode ? "#E3F2FD" : "white") + ";" +
                                "-fx-padding: 16 10 16 10;" +
                                "-fx-background-radius: 12;" +
                                "-fx-effect: dropshadow(gaussian, " + shadowColor + ", 12, 0, 0, 4);"
                );
            }
        }
    }

    private void updateFirstColumnLabelsTheme() {
        for (Label label : firstColumnLabels) {
            label.setStyle(
                    "-fx-background-color: " + (isDarkMode ? "#37474F" : "#546E7A") + ";" +
                            "-fx-text-fill: " + (isDarkMode ? "#E0E0E0" : "white") + ";" +
                            "-fx-padding: 6 12 6 12;" +
                            "-fx-background-radius: 12;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.45), 6, 0, 0, 3);"
            );
        }
    }
}