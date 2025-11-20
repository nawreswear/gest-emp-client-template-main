package tn.iset.m2glnt.client.viewer.view;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import tn.iset.m2glnt.client.model.Slot;
import tn.iset.m2glnt.client.viewer.presenter.SlotViewData;
import tn.iset.m2glnt.client.util.TimeInterval;
import tn.iset.m2glnt.client.service.dao.EnseignantService;
import tn.iset.m2glnt.client.service.SalleRestService;
import tn.iset.m2glnt.client.model.Enseignant;
import tn.iset.m2glnt.client.model.Salle;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SlotView extends StackPane {
    private Slot slot;
    private int slotId;
    private Label contentLabel;

    // CORRECTION : Propriétés pour stocker les heures de début et fin
    private LocalTime heureDebut;
    private LocalTime heureFin;

    // SERVICES pour récupérer les données
    private static EnseignantService enseignantService;
    private static SalleRestService salleRestService;

    // Constructeur par défaut
    public SlotView() {
        initializeView();
    }

    // Constructeur avec Slot
    public SlotView(Slot slot) {
        this.slot = slot;
        this.slotId = slot.id();
        // CORRECTION : Initialiser les propriétés d'heure
        this.heureDebut = slot.getHeureDebut();
        this.heureFin = slot.getHeureFin();
        initializeView();
        updateContent();
    }

    // CORRECTION : Constructeur avec SlotViewData et Color (optionnel)
    public SlotView(SlotViewData slotViewData, Color backgroundColor) {
        initializeView();
        initializeWithData(slotViewData, backgroundColor);
    }

    // MÉTHODES STATIQUES POUR INITIALISER LES SERVICES
    public static void setEnseignantService(EnseignantService service) {
        enseignantService = service;
    }

    public static void setSalleRestService(SalleRestService service) {
        salleRestService = service;
    }

    // Getters pour accéder aux propriétés du Slot
    public Slot getSlot() {
        return slot;
    }

    public LocalDate getDate() {
        return slot != null ? slot.getDate() : null;
    }

    // CORRECTION : Getters cohérents pour les heures
    public LocalTime getHeureDebut() {
        if (slot != null) {
            return slot.getHeureDebut();
        }
        return this.heureDebut;
    }

    public LocalTime getHeureFin() {
        if (slot != null) {
            return slot.getHeureFin();
        }
        return this.heureFin;
    }

    public String getMatiere() {
        return slot != null ? slot.getMatiere() : null;
    }

    public String getEnseignant() {
        return slot != null ? slot.getEnseignant() : null;
    }

    public String getSalle() {
        return slot != null ? slot.getSalle() : null;
    }

    public String getTypeCours() {
        return slot != null ? slot.getTypeCours() : null;
    }

    public String getGroupes() {
        return slot != null ? slot.getGroupes() : null;
    }

    public int getSlotId() {
        return slotId;
    }

    // CORRECTION : Ajout des méthodes pour récupérer les IDs
    public Long getEnseignantId() {
        return slot != null ? slot.enseignantId() : null;
    }

    public Long getSalleId() {
        return slot != null ? slot.salleId() : null;
    }

    public Duration getDuration() {
        return slot != null ? slot.duration() : null;
    }

    private void initializeView() {
        // Style de base pour le slot
        this.setStyle("-fx-background-color: #E8F4FD; " +
                "-fx-border-color: #1E88E5; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 14; " +
                "-fx-background-radius: 14; " +
                "-fx-padding: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 12, 0, 0, 6); " +
                "-fx-cursor: hand;");

        this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Créer le label pour le contenu
        contentLabel = new Label();
        contentLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        contentLabel.setWrapText(true);
        contentLabel.setAlignment(Pos.TOP_LEFT);
        contentLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        this.getChildren().add(contentLabel);
        StackPane.setAlignment(contentLabel, Pos.TOP_LEFT);
        StackPane.setMargin(contentLabel, new Insets(8));
    }

    // CORRECTION : Méthode updateContent modifiée pour récupérer les données depuis les services
    private void updateContent() {
        if (slot == null) {
            contentLabel.setText("Créneau vide");
            return;
        }

        // Afficher d'abord les informations de base
        displayBasicInfo();

        // Récupérer les données détaillées depuis les services
        loadDetailedData();
    }

    private void displayBasicInfo() {
        StringBuilder content = new StringBuilder();

        // Matière (titre principal) - TOUJOURS AFFICHÉ
        if (slot.getMatiere() != null && !slot.getMatiere().isEmpty()) {
            content.append(slot.getMatiere()).append("\n");
        }

        contentLabel.setText(content.toString());
    }

    private void loadDetailedData() {
        // Récupérer le nom de l'enseignant si un ID est disponible
        if (slot.enseignantId() != null && enseignantService != null) {
            loadEnseignantName(slot.enseignantId());
        }

        // Récupérer le nom de la salle si un ID est disponible
        if (slot.salleId() != null && salleRestService != null) {
            loadSalleName(slot.salleId());
        }
    }

    private void loadEnseignantName(Long enseignantId) {
        if (enseignantService == null) return;

        try {
            CompletableFuture<List<Enseignant>> futureEnseignants = enseignantService.getAllEnseignants();

            futureEnseignants.thenAccept(enseignants -> {
                if (enseignants != null) {
                    enseignants.stream()
                            .filter(enseignant -> enseignant.getId().equals(enseignantId))
                            .findFirst()
                            .ifPresent(enseignant -> {
                                String nomComplet = (enseignant.getNom() + " " + enseignant.getPrenom()).trim();
                                updateContentWithEnseignant(nomComplet);
                            });
                }
            }).exceptionally(throwable -> {
                System.err.println("❌ Erreur chargement enseignant: " + throwable.getMessage());
                return null;
            });

        } catch (Exception e) {
            System.err.println("❌ Exception chargement enseignant: " + e.getMessage());
        }
    }

    private void loadSalleName(Long salleId) {
        if (salleRestService == null) return;

        try {
            CompletableFuture<List<Map<String, Object>>> futureSalles = salleRestService.getAllSalles();

            futureSalles.thenAccept(salles -> {
                if (salles != null) {
                    salles.stream()
                            .filter(salleMap -> {
                                Object idObj = salleMap.get("id");
                                if (idObj instanceof Number) {
                                    return ((Number) idObj).longValue() == salleId;
                                }
                                return false;
                            })
                            .findFirst()
                            .ifPresent(salleMap -> {
                                Object nomObj = salleMap.get("nom");
                                if (nomObj != null) {
                                    updateContentWithSalle(nomObj.toString());
                                }
                            });
                }
            }).exceptionally(throwable -> {
                System.err.println("❌ Erreur chargement salle: " + throwable.getMessage());
                return null;
            });

        } catch (Exception e) {
            System.err.println("❌ Exception chargement salle: " + e.getMessage());
        }
    }

    private void updateContentWithEnseignant(String enseignantNom) {
        javafx.application.Platform.runLater(() -> {
            String currentText = contentLabel.getText();
            String newText = currentText + "\n👨‍🏫 " + enseignantNom;
            contentLabel.setText(newText);
        });
    }

    private void updateContentWithSalle(String salleNom) {
        javafx.application.Platform.runLater(() -> {
            String currentText = contentLabel.getText();
            String newText = currentText + "\n🏫 " + salleNom;
            contentLabel.setText(newText);
        });
    }

    // CORRECTION : Nouvelle méthode pour obtenir le contenu détaillé (utilisé dans les dialogues, pas dans le grid)
    public String getDetailedContent() {
        if (slot == null) {
            return "Créneau vide";
        }

        StringBuilder content = new StringBuilder();

        // Matière (titre principal)
        if (slot.getMatiere() != null && !slot.getMatiere().isEmpty()) {
            content.append(slot.getMatiere()).append("\n");
        }

        // Date - UNIQUEMENT dans le contenu détaillé
        if (slot.getDate() != null) {
            content.append("📅 ").append(formatDate(slot.getDate())).append("\n");
        }

        // Horaire - UNIQUEMENT dans le contenu détaillé
        LocalTime debut = getHeureDebut();
        LocalTime fin = getHeureFin();
        if (debut != null && fin != null) {
            content.append("🕐 ")
                    .append(formatTime(debut))
                    .append(" - ")
                    .append(formatTime(fin))
                    .append("\n");
        }

        // Enseignant
        if (slot.getEnseignant() != null && !slot.getEnseignant().isEmpty()) {
            content.append("👨‍🏫 ").append(slot.getEnseignant()).append("\n");
        }

        // Salle
        if (slot.getSalle() != null && !slot.getSalle().isEmpty()) {
            content.append("🏫 ").append(slot.getSalle()).append("\n");
        }

        // Type de cours
        if (slot.getTypeCours() != null && !slot.getTypeCours().isEmpty()) {
            content.append(getTypeIcon(slot.getTypeCours()))
                    .append(" ")
                    .append(slot.getTypeCours())
                    .append("\n");
        }

        // Groupes - UNIQUEMENT dans le contenu détaillé
        if (slot.getGroupes() != null && !slot.getGroupes().isEmpty()) {
            content.append("👥 ").append(slot.getGroupes()).append("\n");
        }

        // Durée - UNIQUEMENT dans le contenu détaillé
        if (slot.duration() != null) {
            java.time.Duration duration = slot.duration();
            long hours = duration.toHours();
            long minutes = duration.toMinutes() % 60;
            content.append("⏱️ ").append(String.format("%dh%02d", hours, minutes));
        }

        return content.toString();
    }

    // CORRECTION : Nouvelle méthode pour obtenir un affichage minimal (utilisé dans le grid)
    public String getMinimalDisplay() {
        if (slot == null) {
            return "Créneau vide";
        }

        StringBuilder content = new StringBuilder();

        // Matière seulement (affichage minimal)
        if (slot.getMatiere() != null && !slot.getMatiere().isEmpty()) {
            content.append(slot.getMatiere());
        } else {
            content.append("Sans titre");
        }

        // Optionnel : ajouter le type de cours si court
        if (slot.getTypeCours() != null && !slot.getTypeCours().isEmpty() && slot.getTypeCours().length() <= 10) {
            content.append("\n").append(getTypeIcon(slot.getTypeCours())).append(" ").append(slot.getTypeCours());
        }

        return content.toString();
    }

    // CORRECTION : Méthode pour mettre à jour l'affichage en mode minimal
    public void setMinimalDisplay(boolean minimal) {
        if (minimal) {
            contentLabel.setText(getMinimalDisplay());
        } else {
            updateContent(); // Retour à l'affichage normal
        }
    }

    public void initializeWithData(SlotViewData slotViewData, Color backgroundColor) {
        // Créer l'adaptateur
        this.slot = createSlotAdapter(slotViewData);
        this.slotId = slotViewData.id();

        // CORRECTION : Initialiser les propriétés d'heure
        this.heureDebut = extractTimeFromInterval(slotViewData.getTimeInterval());
        this.heureFin = extractEndTimeFromInterval(slotViewData.getTimeInterval());

        updateContent();

        // Appliquer la couleur
        if (backgroundColor != null) {
            applyBackgroundColor(backgroundColor);
        }
    }

    private Slot createSlotAdapter(SlotViewData slotViewData) {
        return new Slot() {
            @Override
            public String description() {
                return slotViewData.description() != null ? slotViewData.description() : "Sans description";
            }

            @Override
            public int id() {
                return slotViewData.id();
            }

            @Override
            public LocalDateTime startDateTime() {
                LocalDate date = slotViewData.getDate();
                LocalTime time = extractTimeFromInterval(slotViewData.getTimeInterval());
                return LocalDateTime.of(date, time);
            }

            @Override
            public Duration duration() {
                return extractDurationFromInterval(slotViewData.getTimeInterval());
            }

            @Override
            public LocalDateTime getEndDateTime() {
                return startDateTime().plus(duration());
            }

            @Override
            public int versionNumber() {
                return 1;
            }

            @Override
            public String nom() {
                return slotViewData.description() != null ? slotViewData.description() : "Sans nom";
            }

            @Override
            public Long enseignantId() {
                return null; // À implémenter selon vos besoins
            }

            @Override
            public Long salleId() {
                return null; // À implémenter selon vos besoins
            }

            // CORRECTION : Implémentation des getters d'heure cohérents
            @Override
            public LocalTime getHeureDebut() {
                return extractTimeFromInterval(slotViewData.getTimeInterval());
            }

            @Override
            public LocalTime getHeureFin() {
                return extractEndTimeFromInterval(slotViewData.getTimeInterval());
            }

            // CORRECTION : Ajout des méthodes manquantes nécessaires pour l'interface Slot
            @Override
            public LocalDate getDate() {
                return slotViewData.getDate();
            }

            @Override
            public String getMatiere() {
                return slotViewData.description();
            }

            @Override
            public String getEnseignant() {
                return "Enseignant non spécifié";
            }

            @Override
            public String getSalle() {
                return "Salle non spécifiée";
            }

            @Override
            public String getTypeCours() {
                return "Cours";
            }

            @Override
            public String getGroupes() {
                return "Groupe non spécifié";
            }
        };
    }

    private void applyBackgroundColor(Color color) {
        if (color != null) {
            String colorStyle = String.format("-fx-background-color: #%02X%02X%02X; ",
                    (int)(color.getRed() * 255),
                    (int)(color.getGreen() * 255),
                    (int)(color.getBlue() * 255));

            String currentStyle = getStyle();
            if (currentStyle != null && currentStyle.contains("-fx-background-color:")) {
                setStyle(currentStyle.replaceFirst("-fx-background-color: #[0-9A-Fa-f]{6};", colorStyle));
            } else {
                setStyle(colorStyle + currentStyle);
            }
        }
    }

    private LocalTime extractTimeFromInterval(TimeInterval timeInterval) {
        if (timeInterval == null) {
            return LocalTime.of(8, 0);
        }

        try {
            // Essayer getStartTime()
            try {
                java.lang.reflect.Method method = timeInterval.getClass().getMethod("getStartTime");
                Object result = method.invoke(timeInterval);
                if (result instanceof LocalTime) {
                    return (LocalTime) result;
                }
            } catch (Exception e) {
                // Ignorer et essayer autre chose
            }

            // Essayer getStart()
            try {
                java.lang.reflect.Method method = timeInterval.getClass().getMethod("getStart");
                Object result = method.invoke(timeInterval);
                if (result instanceof LocalTime) {
                    return (LocalTime) result;
                }
            } catch (Exception e) {
                // Ignorer
            }

            // Fallback: utiliser toString()
            String intervalString = timeInterval.toString();
            if (intervalString.contains("-")) {
                String startTime = intervalString.split("-")[0].trim();
                return LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"));
            }

            return LocalTime.of(8, 0);

        } catch (Exception e) {
            System.err.println("❌ Erreur parsing time from TimeInterval: " + timeInterval);
            return LocalTime.of(8, 0);
        }
    }

    private Duration extractDurationFromInterval(TimeInterval timeInterval) {
        if (timeInterval == null) {
            return Duration.ofHours(2);
        }

        try {
            // Essayer getDuration()
            try {
                java.lang.reflect.Method method = timeInterval.getClass().getMethod("getDuration");
                Object result = method.invoke(timeInterval);
                if (result instanceof Duration) {
                    return (Duration) result;
                }
            } catch (Exception e) {
                // Ignorer
            }

            // Calculer à partir de start et end
            LocalTime start = extractTimeFromInterval(timeInterval);
            LocalTime end = extractEndTimeFromInterval(timeInterval);
            if (start != null && end != null && end.isAfter(start)) {
                return Duration.between(start, end);
            }

            return Duration.ofHours(2);

        } catch (Exception e) {
            System.err.println("❌ Erreur parsing duration from TimeInterval: " + timeInterval);
            return Duration.ofHours(2);
        }
    }

    private LocalTime extractEndTimeFromInterval(TimeInterval timeInterval) {
        if (timeInterval == null) {
            return LocalTime.of(10, 0);
        }

        try {
            // Essayer getEndTime()
            try {
                java.lang.reflect.Method method = timeInterval.getClass().getMethod("getEndTime");
                Object result = method.invoke(timeInterval);
                if (result instanceof LocalTime) {
                    return (LocalTime) result;
                }
            } catch (Exception e) {
                // Ignorer
            }

            // Essayer getEnd()
            try {
                java.lang.reflect.Method method = timeInterval.getClass().getMethod("getEnd");
                Object result = method.invoke(timeInterval);
                if (result instanceof LocalTime) {
                    return (LocalTime) result;
                }
            } catch (Exception e) {
                // Ignorer
            }

            // Fallback: utiliser toString()
            String intervalString = timeInterval.toString();
            if (intervalString.contains("-")) {
                String endTime = intervalString.split("-")[1].trim();
                return LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"));
            }

            return LocalTime.of(10, 0);

        } catch (Exception e) {
            System.err.println("❌ Erreur parsing end time from TimeInterval: " + timeInterval);
            return LocalTime.of(10, 0);
        }
    }

    private String formatDate(LocalDate date) {
        return date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Date inconnue";
    }

    private String formatTime(LocalTime time) {
        return time != null ? time.format(DateTimeFormatter.ofPattern("HH:mm")) : "??:??";
    }

    private String getTypeIcon(String typeCours) {
        if (typeCours == null) return "📚";

        switch (typeCours.toLowerCase()) {
            case "cours": return "📖";
            case "td": return "✏️";
            case "tp": return "🔬";
            case "projet": return "💼";
            case "examen": return "📝";
            default: return "📚";
        }
    }

    public void updateSlot(Slot newSlot) {
        this.slot = newSlot;
        this.slotId = newSlot.id();
        // CORRECTION : Mettre à jour les propriétés d'heure
        this.heureDebut = newSlot.getHeureDebut();
        this.heureFin = newSlot.getHeureFin();
        updateContent();
    }

    public void highlight(boolean highlight) {
        if (highlight) {
            this.setStyle(getStyle() + " -fx-border-color: #FF6B35; -fx-border-width: 3;");
        } else {
            this.setStyle(getStyle().replace("-fx-border-color: #FF6B35; -fx-border-width: 3;",
                    "-fx-border-color: #1E88E5; -fx-border-width: 2;"));
        }
    }

    // CORRECTION : Getter pour contentLabel
    public Label getContentLabel() {
        return contentLabel;
    }

    // CORRECTION : Méthode pour mettre à jour les données depuis le formulaire
    public void updateFromFormData(LocalTime heureDebut, LocalDate date, Duration duration,
                                   String matiere, Long enseignantId, Long salleId, String typeCours) {
        // Mettre à jour les propriétés du slot
        if (this.slot != null) {
            // Créer un nouveau slot avec les données mises à jour
            this.slot = createUpdatedSlot(heureDebut, date, duration, matiere, enseignantId, salleId, typeCours);
            this.heureDebut = heureDebut;
            this.heureFin = heureDebut != null && duration != null ? heureDebut.plus(duration) : null;

            // Mettre à jour l'affichage
            updateContent();
        }
    }

    // CORRECTION : Créer un slot mis à jour
    private Slot createUpdatedSlot(LocalTime heureDebut, LocalDate date, Duration duration,
                                   String matiere, Long enseignantId, Long salleId, String typeCours) {
        return new Slot() {
            @Override
            public String description() {
                return matiere != null ? matiere : "Sans description";
            }

            @Override
            public int id() {
                return slotId;
            }

            @Override
            public LocalDateTime startDateTime() {
                return LocalDateTime.of(date, heureDebut);
            }

            @Override
            public Duration duration() {
                return duration;
            }

            @Override
            public LocalDateTime getEndDateTime() {
                return startDateTime().plus(duration);
            }

            @Override
            public int versionNumber() {
                return 1;
            }

            @Override
            public String nom() {
                return matiere != null ? matiere : "Sans nom";
            }

            @Override
            public Long enseignantId() {
                return enseignantId;
            }

            @Override
            public Long salleId() {
                return salleId;
            }

            @Override
            public LocalTime getHeureDebut() {
                return heureDebut;
            }

            @Override
            public LocalTime getHeureFin() {
                return getEndDateTime().toLocalTime();
            }

            @Override
            public LocalDate getDate() {
                return date;
            }

            @Override
            public String getMatiere() {
                return matiere;
            }

            @Override
            public String getEnseignant() {
                return "Enseignant ID: " + enseignantId; // À remplacer par le nom réel
            }

            @Override
            public String getSalle() {
                return "Salle ID: " + salleId; // À remplacer par le nom réel
            }

            @Override
            public String getTypeCours() {
                return typeCours;
            }

            @Override
            public String getGroupes() {
                return "Groupe non spécifié";
            }
        };
    }
}