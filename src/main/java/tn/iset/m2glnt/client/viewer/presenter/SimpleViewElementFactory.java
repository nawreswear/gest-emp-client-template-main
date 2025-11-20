package tn.iset.m2glnt.client.viewer.presenter;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.animation.ScaleTransition;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import tn.iset.m2glnt.client.util.TimeInterval;
import tn.iset.m2glnt.client.viewer.view.ButtonConfiguration;
import tn.iset.m2glnt.client.viewer.view.GridCalendarView;
import tn.iset.m2glnt.client.viewer.view.SlotView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SimpleViewElementFactory implements ViewElementFactory {

    // Couleurs mode clair (initiales)
    private static final Color PRIMARY_PINK = Color.web("#E85B8A");
    private static final Color PRIMARY_PINK_LIGHT = Color.web("#F0A5C0");
    private static final Color PRIMARY_PINK_DARK = Color.web("#D64A7A");
    private static final Color UPDATE_BLUE = Color.web("#4285F4");
    private static final Color UPDATE_BLUE_DARK = Color.web("#3367D6");
    private static final Color UPDATE_BLUE_DARKER = Color.web("#2851A3");
    private static final Color DELETE_RED = Color.web("#EA4335");
    private static final Color DELETE_RED_DARK = Color.web("#D33426");
    private static final Color DELETE_RED_DARKER = Color.web("#BA2B1E");
    private static final Color SURFACE_WHITE = Color.web("#FFFFFF");
    private static final Color BACKGROUND_WARM = Color.web("#F5F3F0");
    private static final Color TEXT_PRIMARY = Color.web("#2D2D2D");
    private static final Color TEXT_SECONDARY = Color.web("#9B9B9B");
    private static final Color BORDER_SOFT = Color.web("#E8E8E8");
    private static final Color HEADER_GRAY = Color.web("#C5C5C5");

    // Couleurs mode sombre
    private static final Color DARK_SURFACE = Color.web("#1E1E1E");
    private static final Color DARK_BACKGROUND = Color.web("#121212");
    private static final Color DARK_TEXT_PRIMARY = Color.web("#E0E0E0");
    private static final Color DARK_TEXT_SECONDARY = Color.web("#A0A0A0");
    private static final Color DARK_BORDER = Color.web("#333333");
    private static final Color DARK_HEADER = Color.web("#2D2D2D");
    private static final Color DARK_CARD = Color.web("#252525");

    // Mode actuel (par défaut: clair)
    private boolean darkMode = false;

    // Méthodes pour changer le mode
    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public void toggleDarkMode() {
        this.darkMode = !this.darkMode;
    }

    // Méthodes utilitaires pour obtenir les couleurs selon le mode
    private Color getSurfaceColor() {
        return darkMode ? DARK_SURFACE : SURFACE_WHITE;
    }

    private Color getBackgroundColor() {
        return darkMode ? DARK_BACKGROUND : BACKGROUND_WARM;
    }

    private Color getTextPrimaryColor() {
        return darkMode ? DARK_TEXT_PRIMARY : TEXT_PRIMARY;
    }

    private Color getTextSecondaryColor() {
        return darkMode ? DARK_TEXT_SECONDARY : TEXT_SECONDARY;
    }

    private Color getBorderColor() {
        return darkMode ? DARK_BORDER : BORDER_SOFT;
    }

    private Color getHeaderColor() {
        return darkMode ? DARK_HEADER : HEADER_GRAY;
    }

    private Color getCardColor() {
        return darkMode ? DARK_CARD : SURFACE_WHITE;
    }

    @Override
    public Label createDateLabel(LocalDate date) {
        // FORMAT COMPLET DE LA DATE : Jour de la semaine + Mois complet + Jour
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEEE\nMMMM d");
        Label label = new Label(date.format(dayFormatter));

        label.setPadding(new Insets(12, 8, 12, 8)); // Padding réduit pour flexibilité
        label.setTextAlignment(TextAlignment.CENTER);
        label.setTextFill(getTextPrimaryColor());
        label.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        label.setBackground(new Background(new BackgroundFill(getCardColor(), new CornerRadii(6), null)));
        label.setBorder(new Border(new BorderStroke(getBorderColor(), BorderStrokeStyle.SOLID, new CornerRadii(6), new BorderWidths(1))));
        label.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 4, 0, 0, 1);");

        // Permettre le wrap du texte et rendre responsive
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE); // Largeur maximale flexible
        label.setMinHeight(Region.USE_PREF_SIZE);

        if (date.equals(LocalDate.now())) {
            label.setBackground(new Background(new BackgroundFill(PRIMARY_PINK, new CornerRadii(50), null)));
            label.setTextFill(SURFACE_WHITE);
            label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            label.setStyle("-fx-effect: dropshadow(gaussian, rgba(232,91,138,0.3), 6, 0, 0, 1.5);");
        }

        label.setOnMouseEntered(e -> {
            if (!date.equals(LocalDate.now())) {
                label.setBackground(new Background(new BackgroundFill(
                        darkMode ? Color.web("#2A2A2A") : Color.web("#F9F9F9"),
                        new CornerRadii(6), null)));
                label.setTextFill(PRIMARY_PINK);
                label.setBorder(new Border(new BorderStroke(PRIMARY_PINK_LIGHT, BorderStrokeStyle.SOLID, new CornerRadii(6), new BorderWidths(1.5))));

                ScaleTransition scale = new ScaleTransition(Duration.millis(150), label);
                scale.setToX(1.03);
                scale.setToY(1.03);
                scale.play();
            }
        });

        label.setOnMouseExited(e -> {
            if (!date.equals(LocalDate.now())) {
                label.setBackground(new Background(new BackgroundFill(getCardColor(), new CornerRadii(6), null)));
                label.setTextFill(getTextPrimaryColor());
                label.setBorder(new Border(new BorderStroke(getBorderColor(), BorderStrokeStyle.SOLID, new CornerRadii(6), new BorderWidths(1))));

                ScaleTransition scale = new ScaleTransition(Duration.millis(150), label);
                scale.setToX(1.0);
                scale.setToY(1.0);
                scale.play();
            }
        });

        GridPane.setHalignment(label, HPos.CENTER);
        GridPane.setHgrow(label, Priority.ALWAYS); // Permet l'expansion
        return label;
    }

    @Override
    public Label createTimeIntervalLabel(TimeInterval timeInterval) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String startTime = timeInterval.start().format(timeFormatter);
        String endTime = timeInterval.end().format(timeFormatter);
        Label label = new Label(startTime + " - " + endTime);

        label.setTextFill(getTextSecondaryColor());
        label.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
        label.setPadding(new Insets(8, 6, 8, 6));
        label.setStyle("-fx-letter-spacing: 0.3px;");

        // Rendre responsive
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMinHeight(Region.USE_PREF_SIZE);

        GridPane.setHalignment(label, HPos.CENTER);
        GridPane.setHgrow(label, Priority.ALWAYS); // Permet l'expansion
        return label;
    }

    @Override
    public SlotView createSlotView(SlotViewData slotViewData, Color backgroundColor) {
        if (slotViewData == null) {
            System.err.println("❌ ERREUR: SlotViewData est null!");
            return null;
        }

        System.out.println("🎯 === CRÉATION SLOT VIEW ===");
        System.out.println("📅 Date du slot: " + slotViewData.getDate());
        System.out.println("⏰ Heure du slot: " + slotViewData.getTimeInterval());
        System.out.println("📍 Position dans grille - Colonne: " + slotViewData.getColumn() +
                ", Ligne: " + slotViewData.getRow());
        System.out.println("📝 Description: " + slotViewData.description());
        System.out.println("🆔 ID: " + slotViewData.id());
        System.out.println("🎯 ==========================");

        SlotView slotView = new SlotView(slotViewData, backgroundColor);

        // Rendre les slots responsives
        slotView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setHgrow(slotView, Priority.ALWAYS);
        GridPane.setVgrow(slotView, Priority.ALWAYS);

        return slotView;
    }

    @Override
    public GridCalendarView createGrid(int columns, int rows,
                                       int widthFirstColumn,
                                       int widthSecondToLastColumn,
                                       int heightFirstRow,
                                       int heightSecondToLastRow,
                                       Color linesColor,
                                       Color backgroundColor) {
        GridCalendarView grid = new GridCalendarView();

        // Style moderne et responsive
        grid.setBackground(new Background(new BackgroundFill(getBackgroundColor(), new CornerRadii(12), null)));
        grid.setHgap(1);
        grid.setVgap(1);
        grid.setPadding(new Insets(12));
        grid.setStyle(
                "-fx-border-color: " + toHex(getBorderColor()) + ";" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 1;" +
                        "-fx-background-radius: 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);"
        );

        // DIMENSIONS FLEXIBLES ET RESPONSIVES
        // Toutes les colonnes ont la même largeur flexible
        for(int i = 0; i < columns; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setMinWidth(80); // Largeur minimale
            column.setPrefWidth(120); // Largeur préférée
            column.setMaxWidth(200); // Largeur maximale
            column.setHgrow(Priority.ALWAYS); // Permet l'expansion
            column.setFillWidth(true); // Remplit l'espace disponible
            grid.getColumnConstraints().add(column);
        }

        // Toutes les lignes ont la même hauteur flexible
        for(int i = 0; i < rows; i++) {
            RowConstraints row = new RowConstraints();
            row.setMinHeight(40); // Hauteur minimale
            row.setPrefHeight(60); // Hauteur préférée
            row.setMaxHeight(100); // Hauteur maximale
            row.setVgrow(Priority.ALWAYS); // Permet l'expansion
            row.setFillHeight(true); // Remplit l'espace disponible
            grid.getRowConstraints().add(row);
        }

        // Création des cellules de la grille avec styles responsives
        for(int col = 0; col < columns; col++) {
            for(int row = 0; row < rows; row++) {
                Pane cell = new Pane();
                cell.setBackground(new Background(new BackgroundFill(
                        darkMode ? DARK_CARD : backgroundColor,
                        new CornerRadii(4), null)));

                // Style de base responsive
                String baseCellStyle =
                        "-fx-border-color: " + toHex(getBorderColor()) + ";" +
                                "-fx-border-width: 0.5;" +
                                "-fx-border-radius: 4;" +
                                "-fx-background-radius: 4;";

                // Style spécial pour les en-têtes
                if (col == 0 || row == 0) {
                    cell.setStyle(baseCellStyle +
                            "-fx-background-color: " + (darkMode ? "#2D2D2D" : "#F8F9FA") + ";" +
                            "-fx-border-color: " + toHex(getHeaderColor()) + ";" +
                            "-fx-border-width: 1;");
                }
                // Style pour les cellules normales
                else {
                    cell.setStyle(baseCellStyle);
                }

                // Rendre les cellules responsives
                cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                GridPane.setHgrow(cell, Priority.ALWAYS);
                GridPane.setVgrow(cell, Priority.ALWAYS);

                grid.add(cell, col, row);
            }
        }

        System.out.println("✅ Grille responsive créée: " + columns + "x" + rows + " cellules flexibles");
        return grid;
    }

    @Override
    public HBox createButtonBox(List<ButtonConfiguration> buttons) {
        HBox hBox = new HBox();
        hBox.setSpacing(12);
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(16, 24, 16, 24));

        // Style moderne et responsive selon le mode
        String backgroundColor = darkMode ?
                "linear-gradient(to right, #252525, #2A2A2A)" :
                "linear-gradient(to right, #FFFFFF, #FAFAF8)";

        hBox.setStyle(
                "-fx-background-color: " + backgroundColor + ";" +
                        "-fx-border-color: " + toHex(getBorderColor()) + ";" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 16;" +
                        "-fx-background-radius: 16;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 3);"
        );

        // Rendre le HBox responsive
        hBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(hBox, Priority.ALWAYS);

        for (ButtonConfiguration buttonConfiguration : buttons) {
            Button button = createStyledButton(buttonConfiguration);
            hBox.getChildren().add(button);
        }
        return hBox;
    }

    private Button createStyledButton(ButtonConfiguration buttonConfiguration) {
        Button button = new Button(buttonConfiguration.Label());

        // Design responsive pour les boutons
        button.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        button.setMinSize(44, 44);
        button.setPrefSize(48, 48);
        button.setMaxSize(52, 52);

        // Styles responsives
        String baseStyle =
                "-fx-background-color: linear-gradient(to bottom, #E85B8A, #D64A7A);" +
                        "-fx-text-fill: white;" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 0;" +
                        "-fx-font-weight: bold;" +
                        "-fx-effect: dropshadow(gaussian, rgba(232,91,138,0.2), 6, 0, 0, 1.5);" +
                        "-fx-font-size: 14;";

        String hoverStyle =
                "-fx-background-color: linear-gradient(to bottom, #D64A7A, #C53970);" +
                        "-fx-text-fill: white;" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 0;" +
                        "-fx-font-weight: bold;" +
                        "-fx-effect: dropshadow(gaussian, rgba(232,91,138,0.3), 8, 0, 0, 2);" +
                        "-fx-font-size: 14;";

        // Appliquer le style par défaut
        button.setStyle(baseStyle);

        // Déterminer le style en fonction du type de bouton
        String buttonLabel = buttonConfiguration.Label().toLowerCase();
        if (buttonLabel.contains("update") || buttonLabel.contains("modifier") || buttonLabel.contains("edit")) {
            button.setStyle(baseStyle.replace("#E85B8A", "#4285F4").replace("#D64A7A", "#3367D6"));
            hoverStyle = hoverStyle.replace("#D64A7A", "#3367D6").replace("#C53970", "#2851A3");
        } else if (buttonLabel.contains("delete") || buttonLabel.contains("supprimer") || buttonLabel.contains("remove")) {
            button.setStyle(baseStyle.replace("#E85B8A", "#EA4335").replace("#D64A7A", "#D33426"));
            hoverStyle = hoverStyle.replace("#D64A7A", "#D33426").replace("#C53970", "#BA2B1E");
        }

        // Animations fluides
        setupButtonAnimations(button, buttonConfiguration, baseStyle, hoverStyle);

        return button;
    }

    private void setupButtonAnimations(Button button, ButtonConfiguration buttonConfiguration, String baseStyle, String hoverStyle) {
        String pressedStyle = hoverStyle.replace("0.3", "0.15").replace("8", "6");

        button.setOnMouseEntered(e -> {
            button.setStyle(hoverStyle);
            ScaleTransition scale = new ScaleTransition(Duration.millis(120), button);
            scale.setToX(1.08);
            scale.setToY(1.08);
            scale.play();
        });

        button.setOnMouseExited(e -> {
            button.setStyle(baseStyle);
            ScaleTransition scale = new ScaleTransition(Duration.millis(120), button);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });

        button.setOnMousePressed(e -> button.setStyle(pressedStyle));
        button.setOnMouseReleased(e -> button.setStyle(baseStyle));

        // Gestion du clic
        button.setOnMouseClicked(event -> {
            ScaleTransition clickScale = new ScaleTransition(Duration.millis(80), button);
            clickScale.setToX(0.92);
            clickScale.setToY(0.92);
            clickScale.setAutoReverse(true);
            clickScale.setCycleCount(2);
            clickScale.play();

            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.millis(50));
            pause.setOnFinished(e -> {
                if (buttonConfiguration.buttonAction() != null) {
                    javafx.application.Platform.runLater(buttonConfiguration.buttonAction()::action);
                }
            });
            pause.play();
        });
    }

    private String toHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}