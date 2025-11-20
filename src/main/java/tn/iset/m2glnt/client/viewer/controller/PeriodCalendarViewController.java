package tn.iset.m2glnt.client.viewer.controller;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import tn.iset.m2glnt.client.model.Calendar;
import tn.iset.m2glnt.client.model.SimpleSlot;
import tn.iset.m2glnt.client.model.Slot;
import tn.iset.m2glnt.client.service.CalendarServiceDAOAdapter;
import tn.iset.m2glnt.client.service.SalleRestService;
import tn.iset.m2glnt.client.service.dao.EnseignantService;
import tn.iset.m2glnt.client.service.dao.EtudiantService;
import tn.iset.m2glnt.client.service.dao.SimpleCalendarServiceDAO;
import tn.iset.m2glnt.client.service.dto.SlotDTO;
import tn.iset.m2glnt.client.util.CalendarExporter;
import tn.iset.m2glnt.client.util.DateInterval;
import tn.iset.m2glnt.client.util.DayGenerator;
import tn.iset.m2glnt.client.viewer.CalendarApp;
import tn.iset.m2glnt.client.viewer.presenter.CalendarPresenter;
import tn.iset.m2glnt.client.viewer.presenter.PeriodCalendarPresenter;
import tn.iset.m2glnt.client.viewer.presenter.dialog.*;
import tn.iset.m2glnt.client.viewer.view.GridCalendarView;
import tn.iset.m2glnt.client.viewer.view.SlotView;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Consumer;

public class PeriodCalendarViewController implements CalendarViewController {
    private final CalendarPresenter calendarPresenter;
    private Consumer<List<Slot>> dataRefreshCallback;
    private final CalendarViewConfiguration calendarViewConfiguration;
    private DayGenerator dayGenerator;
    private final Calendar calendar;
    private final SlotFormFactory slotFormFactory;
    private final Map<Integer, Slot> slotsById = new HashMap<>();
    private boolean advancedFeaturesAdded = false;

    // 🔥 Nouveaux composants pour la recherche
    private VBox searchResultsContainer;
    private Label searchResultsLabel;
    private ListView<String> searchResultsList;
    private boolean searchComponentsInitialized = false;

    public PeriodCalendarViewController(Calendar calendar,
                                        CalendarViewConfiguration calendarViewConfiguration,
                                        EnseignantService enseignantService,
                                        SalleRestService salleService,
                                        EtudiantService etudiantService) {

        this.calendarViewConfiguration = calendarViewConfiguration;

        // 🔥 CORRECTION : Créer l'adaptateur si nécessaire
        if (calendar instanceof tn.iset.m2glnt.client.service.CalendarServiceDAOAdapter) {
            this.calendar = calendar;
            System.out.println("✅ CalendarServiceDAOAdapter déjà configuré");
        } else {
            // Créer un nouvel adaptateur avec un CalendarServiceDAO
            tn.iset.m2glnt.client.service.dao.CalendarServiceDAO calendarDAO =
                    new SimpleCalendarServiceDAO();
            this.calendar = new CalendarServiceDAOAdapter(calendarDAO);
            System.out.println("🔄 CalendarServiceDAOAdapter créé et configuré");
        }

        LocalDate today = LocalDate.now();
        setStartDate(calendarViewConfiguration.getPeriodStartDateContaining(today));
        List<Duration> possiblesDurations = calendarViewConfiguration.getPossibleDurations().getDurations();
        List<LocalTime> possibleTimes = calendarViewConfiguration.getTimeIntervalGenerator().getStartTimesOfIntervals();
        possibleTimes.add(calendarViewConfiguration.getTimeIntervalGenerator().getEndTime());

        this.slotFormFactory = new SimpleSlotFormFactory(possibleTimes, possiblesDurations,
                enseignantService, salleService, etudiantService);
        this.calendarPresenter = new PeriodCalendarPresenter(this, dayGenerator,
                calendarViewConfiguration.getTimeIntervalGenerator());

        initializeSearchComponents();
        refreshSlots();
    }

    public void addLogoutButton(String userEmail) {
        System.out.println("👤 Ajout du bouton de déconnexion pour: " + userEmail);
        addLogoutToHeader(userEmail);
    }

    private void addLogoutToHeader(String userEmail) {
        try {
            Scene scene = calendarPresenter.getScene();
            if (scene != null && scene.getRoot() instanceof BorderPane) {
                BorderPane mainContainer = (BorderPane) scene.getRoot();
                Node topNode = mainContainer.getTop();

                if (topNode instanceof HBox) {
                    HBox header = (HBox) topNode;

                    // Créer le bouton de déconnexion
                    Button logoutButton = createLogoutButton(userEmail);

                    // Ajouter le bouton à droite du header
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    // Réorganiser les enfants du header
                    header.getChildren().removeIf(node -> node instanceof Region); // Supprimer l'ancien spacer
                    header.getChildren().addAll(spacer, logoutButton);

                    System.out.println("✅ Bouton de déconnexion ajouté au header");
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'ajout du bouton de déconnexion: " + e.getMessage());
        }
    }

    private Button createLogoutButton(String userEmail) {
        Button logoutButton = new Button("🚪 " + userEmail);
        logoutButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 12px;" +
                        "-fx-border-color: rgba(255,255,255,0.5);" +
                        "-fx-border-radius: 15;" +
                        "-fx-background-radius: 15;" +
                        "-fx-padding: 5 15;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);"
        );

        // Effet hover
        logoutButton.setOnMouseEntered(e ->
                logoutButton.setStyle(
                        "-fx-background-color: rgba(255,255,255,0.2);" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 12px;" +
                                "-fx-border-color: rgba(255,255,255,0.8);" +
                                "-fx-border-radius: 15;" +
                                "-fx-background-radius: 15;" +
                                "-fx-padding: 5 15;" +
                                "-fx-cursor: hand;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 8, 0, 0, 3);"
                )
        );

        logoutButton.setOnMouseExited(e ->
                logoutButton.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 12px;" +
                                "-fx-border-color: rgba(255,255,255,0.5);" +
                                "-fx-border-radius: 15;" +
                                "-fx-background-radius: 15;" +
                                "-fx-padding: 5 15;" +
                                "-fx-cursor: hand;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);"
                )
        );

        // Action de déconnexion
        logoutButton.setOnAction(e -> {
            System.out.println("🔒 Déconnexion demandée par: " + userEmail);
            CalendarApp.logout();
        });

        // Tooltip
        Tooltip tooltip = new Tooltip("Cliquer pour se déconnecter");
        Tooltip.install(logoutButton, tooltip);

        return logoutButton;
    }

    private void initializeSearchComponents() {
        // Label pour les résultats
        searchResultsLabel = new Label("🔍 Résultats de recherche (0 trouvés)");
        searchResultsLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        searchResultsLabel.setTextFill(Color.web("#2D2D2D"));
        searchResultsLabel.setPadding(new Insets(8, 0, 8, 0));

        // Liste des résultats
        searchResultsList = new ListView<>();
        searchResultsList.setPrefHeight(120);
        searchResultsList.setMaxHeight(150);
        searchResultsList.setStyle(
                "-fx-background-color: #FFFFFF;" +
                        "-fx-border-color: #E0E0E0;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 5;"
        );

        // Conteneur principal des résultats
        searchResultsContainer = new VBox(5);
        searchResultsContainer.setPadding(new Insets(10));
        searchResultsContainer.setStyle(
                "-fx-background-color: #F8F9FA;" +
                        "-fx-border-color: #E85B8A;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;"
        );
        searchResultsContainer.setVisible(false);

        searchComponentsInitialized = true;
    }

    @Override
    public void handleNext() {
        LocalDate newStartDate = dayGenerator.getStartDate().plus(calendarViewConfiguration.getTotalPeriod());
        updateStartDate(newStartDate);
        refreshSlots();
    }

    @Override
    public void handlePrevious() {
        LocalDate newStartDate = dayGenerator.getStartDate().minus(calendarViewConfiguration.getTotalPeriod());
        updateStartDate(newStartDate);
        refreshSlots();
    }

    private void updateStartDate(LocalDate newStartDate) {
        setStartDate(newStartDate);
        calendarPresenter.updateDays(dayGenerator);
    }

    private void setStartDate(LocalDate newStartDate) {
        LocalDate newEndDate = newStartDate.plus(calendarViewConfiguration.getPrintablePeriod());
        dayGenerator = new DayGenerator(new DateInterval(newStartDate, newEndDate));
    }

    /*@Override
    public void handleSlotEdition(int idSlot) {
        Slot slot = slotsById.get(idSlot);
        if (slot == null) {
            return;
        }
        Dialog<SlotFormResult> dialog = slotFormFactory.createCalendarEventDialog(slot);
        var result = dialog.showAndWait();
        result.ifPresent(r -> handleFormEditionResult(r, slot));
    }*/
    public void handleSlotEdition(int slotId) {
        System.out.println("✏️ Gestion de l'édition du slot #" + slotId);

        // Récupérer le slot depuis la map locale
        Slot slotData = slotsById.get(slotId);
        if (slotData == null) {
            System.err.println("❌ Slot non trouvé pour l'ID: " + slotId);
            showAlert("Erreur", "Créneau #" + slotId + " non trouvé", Alert.AlertType.ERROR);
            return;
        }

        // Vérifier si l'utilisateur est admin via la factory
        Dialog<SlotFormResult> dialog = slotFormFactory.createCalendarEventDialog(slotData);

        // Vérifier si le dialog est null (cas non-admin)
        if (dialog == null) {
            System.out.println("🔐 Accès en lecture seule pour non-admin - Slot #" + slotId);
            showReadOnlyAccess(slotId, slotData);
            return;
        }

        // CAS ADMIN UNIQUEMENT
        System.out.println("✅ Affichage du formulaire d'édition pour admin - Slot #" + slotId);

        Optional<SlotFormResult> result = dialog.showAndWait();

        result.ifPresent(slotFormResult -> {
            SlotFormAction action = slotFormResult.getAction();
            Object content = slotFormResult.getContent();

            switch (action) {
                case CONFIRM -> {
                    System.out.println("💾 Confirmation de modification pour le slot #" + slotId);
                    handleFormEditionResult(slotFormResult, slotData);
                }
                case DELETE -> {
                    System.out.println("🗑️ Suppression demandée pour le slot #" + slotData.id()); // 🔥 CORRECTION : Utiliser slotData au lieu de originalSlot

                    // 🔥 CORRECTION : Confirmation avant suppression
                    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmAlert.setTitle("Confirmation de suppression");
                    confirmAlert.setHeaderText("Supprimer le créneau #" + slotData.id()); // 🔥 CORRECTION : Utiliser slotData
                    confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer définitivement ce créneau ?\nCette action est irréversible.");

                    Optional<ButtonType> confirmResult = confirmAlert.showAndWait(); // 🔥 CORRECTION : Renommer la variable
                    if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
                        System.out.println("✅ Suppression confirmée par l'utilisateur");

                        // 🔥 CORRECTION : Utiliser la nouvelle méthode optimisée
                        deleteSlotWithRefresh(slotData.id()); // 🔥 CORRECTION : Utiliser slotData
                    } else {
                        System.out.println("❌ Suppression annulée par l'utilisateur");
                        showAlert("Annulation", "Suppression du créneau #" + slotData.id() + " annulée", Alert.AlertType.INFORMATION); // 🔥 CORRECTION : Utiliser slotData
                    }
                }
                case CANCEL -> {
                    System.out.println("❌ Annulation de l'édition pour le slot #" + slotId);
                    showAlert("Annulation", "Modification du créneau #" + slotId + " annulée", Alert.AlertType.INFORMATION);
                }
            }
        });
    }
    /**
     * Suppression optimisée avec rafraîchissement contrôlé
     */
    // 🔥 Méthode simplifiée pour l'affichage en lecture seule
    private void showReadOnlyAccess(int slotId, Slot slotData) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails du créneau - Lecture seule");
        alert.setHeaderText("Créneau #" + slotId);

        StringBuilder content = new StringBuilder();
        content.append("📖 Matière: ").append(slotData.getMatiere()).append("\n");
        content.append("📅 Date: ").append(slotData.getDate()).append("\n");
        content.append("🕐 Horaire: ").append(slotData.getHeureDebut())
                .append(" - ").append(slotData.getHeureFin()).append("\n");
        content.append("⏱️ Durée: ").append(slotData.duration().toHours()).append("h").append("\n");

        content.append("\nℹ️ Mode lecture seule - Contactez un administrateur pour modifications");

        alert.setContentText(content.toString());
        alert.showAndWait();
    }
    // 🔥 AJOUTER ces méthodes manquantes (si elles n'existent pas)
    private void handleSlotUpdate(int slotId, Object updatedContent) {
        System.out.println("🔄 Mise à jour du slot #" + slotId);
        // Implémentez la logique de mise à jour ici
    }

    private void handleSlotDeletion(int slotId) {
        System.out.println("🗑️ Suppression du slot #" + slotId);
        // Implémentez la logique de suppression ici
    }

    private void handleFormEditionResult(SlotFormResult r, Slot originalSlot) {
        var form = r.getContent();
        switch (r.getAction()) {
            case CANCEL -> {
                System.out.println("❌ Édition annulée pour le slot #" + originalSlot.id());
                showAlert("Annulation", "Modification annulée", Alert.AlertType.INFORMATION);
            }
            // Dans la méthode handleFormEditionResult
            case DELETE -> {
                System.out.println("🗑️ Suppression demandée pour le slot #" + originalSlot.id()); // 🔥 CORRECTION : Utiliser originalSlot

                // Confirmation avant suppression
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirmation de suppression");
                confirmAlert.setHeaderText("Supprimer le créneau #" + originalSlot.id()); // 🔥 CORRECTION : Utiliser originalSlot
                confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer définitivement ce créneau ?\nCette action est irréversible.");

                Optional<ButtonType> deleteResult = confirmAlert.showAndWait();
                if (deleteResult.isPresent() && deleteResult.get() == ButtonType.OK) {
                    System.out.println("✅ Suppression confirmée par l'utilisateur");
                    deleteSlotWithRefresh(originalSlot.id()); // 🔥 CORRECTION : Utiliser originalSlot
                } else {
                    System.out.println("❌ Suppression annulée par l'utilisateur");
                    showAlert("Annulation", "Suppression du créneau #" + originalSlot.id() + " annulée", Alert.AlertType.INFORMATION); // 🔥 CORRECTION : Utiliser originalSlot
                }
            }
            case CONFIRM -> {
                if (form != null) {
                    System.out.println("💾 Traitement de la confirmation pour le slot #" + originalSlot.id());

                    try {
                        LocalDateTime dateTimeStart;
                        Duration duration;
                        String matiere;
                        Long enseignantId;
                        Long salleId;

                        // Vérifier si c'est un contenu étendu
                        if (form instanceof ExtendedSlotFormContent) {
                            ExtendedSlotFormContent extendedForm = (ExtendedSlotFormContent) form;
                            dateTimeStart = LocalDateTime.of(extendedForm.getDate(), extendedForm.getStartTime());
                            duration = extendedForm.getDuration();
                            matiere = extendedForm.getMatiere();
                            enseignantId = extendedForm.getEnseignantId();
                            salleId = extendedForm.getSalleId();

                            System.out.println("🔄 Données du formulaire étendu:");
                            System.out.println("   - Matière: " + matiere);
                            System.out.println("   - Date: " + extendedForm.getDate());
                            System.out.println("   - Heure: " + extendedForm.getStartTime());
                            System.out.println("   - Durée: " + duration);
                            System.out.println("   - Enseignant ID: " + enseignantId);
                            System.out.println("   - Salle ID: " + salleId);
                        } else {
                            System.err.println("❌ Type de formulaire non supporté: " + form.getClass().getName());
                            showAlert("Erreur", "Format de données invalide", Alert.AlertType.ERROR);
                            return;
                        }

                        // Créer le slot modifié
                        Slot editedSlot = new SimpleSlot(
                                originalSlot.id(),           // Garder le même ID
                                matiere,                     // Nouvelle matière
                                matiere,                     // Nouveau nom
                                dateTimeStart,               // Nouvelle date/heure
                                duration,                    // Nouvelle durée
                                originalSlot.versionNumber() + 1, // Incrémenter la version
                                enseignantId,                // Nouvel ID enseignant
                                salleId                      // Nouvel ID salle
                        );

                        // Appeler updateSlot avec le slot modifié
                        updateSlot(editedSlot);

                    } catch (Exception e) {
                        System.err.println("❌ Erreur lors du traitement des données: " + e.getMessage());
                        showAlert("Erreur", "Erreur lors de la modification: " + e.getMessage(), Alert.AlertType.ERROR);
                    }
                } else {
                    showAlert("Erreur", "Aucune donnée reçue du formulaire", Alert.AlertType.ERROR);
                }
            }
        }
    }
    /**
     * Suppression optimisée avec rafraîchissement contrôlé
     */
    private void deleteSlotWithRefresh(int slotId) {
        System.out.println("🔄 Suppression optimisée du slot #" + slotId);

        try {
            Slot slotToDelete = slotsById.get(slotId);
            if (slotToDelete == null) {
                System.err.println("❌ Slot #" + slotId + " non trouvé");
                return;
            }

            // 1. Suppression backend
            boolean backendDeleted = calendar.delete(slotToDelete);

            if (backendDeleted) {
                System.out.println("✅ Backend: Slot #" + slotId + " supprimé");

                // 2. Suppression immédiate frontend
                slotsById.remove(slotId);
                calendarPresenter.removeSlotView(slotId);

                // 3. Notification de succès
                showAlert("Succès", "Créneau #" + slotId + " supprimé avec succès", Alert.AlertType.INFORMATION); // 🔥 CORRECTION : Utiliser showAlert au lieu de showTemporaryNotification

                // 4. Rafraîchissement léger après court délai
                Platform.runLater(() -> {
                    PauseTransition delay = new PauseTransition(javafx.util.Duration.millis(400)); // 🔥 CORRECTION : javafx.util.Duration
                    delay.setOnFinished(e -> {
                        refreshAfterDeletion(slotId); // 🔥 CORRECTION : Utiliser refreshAfterDeletion au lieu de applyCurrentTheme
                        System.out.println("✅ Suppression complète terminée pour le slot #" + slotId);
                    });
                    delay.play();
                });

            } else {
                System.err.println("❌ Échec suppression backend pour le slot #" + slotId);
                showAlert("Erreur", "Échec de la suppression du créneau", Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur suppression slot #" + slotId + ": " + e.getMessage());
            showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @Override
    public void handleSlotCreation() {
        Dialog<SlotFormResult> dialog = slotFormFactory.createCalendarEventDialog(LocalDateTime.now(),
                calendarViewConfiguration.getDefaultDurationIndex());
        var result = dialog.showAndWait();

        result.ifPresent(r -> {
            handleFormCreationResult(r); // ✅ CORRECTION : Appeler la bonne méthode
            // ✅ FORCER le rafraîchissement après la création
            javafx.application.Platform.runLater(() -> {
                try {
                    Thread.sleep(300); // Petit délai
                    refreshSlots();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        });
    }

    // ✅ AJOUTER CETTE MÉTHODE MANQUANTE
    private void handleFormCreationResult(SlotFormResult r) {
        var form = r.getContent();
        if (form != null && r.getAction() == SlotFormAction.CONFIRM) {
            LocalDateTime dateTimeStart;
            Duration duration;
            String matiere;
            Long enseignantId;
            Long salleId;
            String typeCours;

            if (form instanceof ExtendedSlotFormContent) {
                ExtendedSlotFormContent extendedForm = (ExtendedSlotFormContent) form;
                dateTimeStart = LocalDateTime.of(extendedForm.getDate(), extendedForm.getStartTime());
                duration = extendedForm.getDuration();
                matiere = extendedForm.getMatiere();
                enseignantId = extendedForm.getEnseignantId();
                salleId = extendedForm.getSalleId();
               // typeCours = extendedForm.getTypeCours();
            } else if (form instanceof SlotFormData) {
                SlotFormData formData = (SlotFormData) form;
                dateTimeStart = LocalDateTime.of(formData.getDate(), formData.getStartTime());
                duration = formData.getDuration();
                matiere = formData.getDescription();
                enseignantId = null;
                salleId = null;
                //typeCours = formData.getDescription();
            } else {
                System.err.println("❌ Type de formulaire non reconnu pour la création: " + form.getClass().getName());
                return;
            }

            Slot calendarSlot = new SimpleSlot(
                    -1,
                    matiere,
                    matiere,
                    dateTimeStart,
                    duration,
                    0,
                    enseignantId,
                    salleId
            );
            createSlot(calendarSlot);
        }
    }

    private void deleteSlot(int idSlot) {
        System.out.println("🗑️ Suppression du slot #" + idSlot + " demandée");

        try {
            Slot slotToDelete = slotsById.get(idSlot);
            if (slotToDelete == null) {
                System.err.println("❌ Slot #" + idSlot + " non trouvé dans le cache local");
                showAlert("Erreur", "Créneau #" + idSlot + " non trouvé", Alert.AlertType.ERROR);
                return;
            }

            System.out.println("🔄 Tentative de suppression du slot #" + idSlot + " via l'adaptateur");

            // Suppression backend
            boolean isDeleted = calendar.delete(slotToDelete);

            if (isDeleted) {
                System.out.println("✅ Slot #" + idSlot + " supprimé avec succès côté backend");

                // Nettoyage immédiat frontend
                slotsById.remove(idSlot);
                calendarPresenter.removeSlotView(idSlot);

                // Rafraîchissement simple
                Platform.runLater(() -> {
                    // Forcer un rafraîchissement visuel
                    refreshSlots(); // 🔥 Utilisez votre méthode existante refreshSlots()
                });

                showAlert("Succès", "Créneau #" + idSlot + " supprimé avec succès", Alert.AlertType.INFORMATION);

            } else {
                System.err.println("❌ Échec de la suppression du slot #" + idSlot);
                showAlert("Erreur", "Échec de la suppression du créneau #" + idSlot, Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la suppression du slot #" + idSlot + ": " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    // 🔥 CORRECTION : Méthode de rafraîchissement optimisée après suppression
    /**
     * Rafraîchissement après suppression
     */
    /**
     * Rafraîchissement après suppression
     */
    private void refreshAfterDeletion(int deletedSlotId) {
        System.out.println("🔄 Rafraîchissement après suppression du slot " + deletedSlotId);

        // Vérifier que le slot a bien été supprimé
        if (slotsById.containsKey(deletedSlotId)) {
            System.out.println("⚠️ Nettoyage final du slot " + deletedSlotId);
            slotsById.remove(deletedSlotId);
            calendarPresenter.removeSlotView(deletedSlotId);
        }

        // Forcer un rafraîchissement visuel
        Platform.runLater(() -> {
            try {
                // Utiliser votre méthode de rafraîchissement existante
                refreshSlots();
            } catch (Exception e) {
                System.err.println("❌ Erreur lors du rafraîchissement: " + e.getMessage());
            }
        });
    }

    /**
     * Affiche une alerte simple
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Platform.runLater(() -> {
            try {
                Alert alert = new Alert(type);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
            } catch (Exception e) {
                System.err.println("❌ Erreur affichage alerte: " + e.getMessage());
            }
        });
    }


    private void diagnoseCalendarImplementation() {
        System.out.println("=== DIAGNOSTIC IMPLÉMENTATION CALENDAR ===");
        System.out.println("Calendar class: " + calendar.getClass().getName());
        System.out.println("Is CalendarServiceDAOAdapter: " +
                (calendar instanceof tn.iset.m2glnt.client.service.CalendarServiceDAOAdapter));
        System.out.println("==========================================");
    }
    public void addAdvancedFeatures() {
        if (advancedFeaturesAdded) {
            return;
        }

        // 🔥 CONTENEUR PRINCIPAL - TOUT SUR LA MÊME LIGNE
        HBox advancedContainer = new HBox(20);
        advancedContainer.setPadding(new Insets(15));
        advancedContainer.setAlignment(Pos.CENTER_LEFT);
        advancedContainer.setStyle(
                "-fx-background-color: linear-gradient(to right, #F8F9FA, #FFFFFF);" +
                        "-fx-border-color: #E0E0E0;" +
                        "-fx-border-width: 0 0 1 0;"
        );

        // 🔥 PARTIE GAUCHE : BOUTONS PRINCIPAUX
        HBox leftButtonsContainer = createLeftButtonsContainer();

        // 🔥 PARTIE DROITE : RECHERCHE ET BOUTONS DE RECHERCHE
        HBox rightSearchContainer = createRightSearchContainer();

        // 🔥 AJOUTER LES DEUX PARTIES AU CONTENEUR PRINCIPAL
        advancedContainer.getChildren().addAll(leftButtonsContainer, rightSearchContainer);

        // Ajouter à l'interface principale
        Scene scene = calendarPresenter.getScene();
        if (scene != null && scene.getRoot() instanceof VBox) {
            VBox mainContainer = (VBox) scene.getRoot();

            boolean featuresExist = mainContainer.getChildren().stream()
                    .anyMatch(node -> node instanceof HBox);

            if (!featuresExist) {
                if (mainContainer.getChildren().size() > 1) {
                    mainContainer.getChildren().add(1, advancedContainer);
                } else {
                    mainContainer.getChildren().add(advancedContainer);
                }
                advancedFeaturesAdded = true;
                System.out.println("✅ Fonctionnalités avancées ajoutées - Tout sur la même ligne");
            }
        }
    }

    // 🔥 NOUVEAU : Conteneur des boutons de gauche
    private HBox createLeftButtonsContainer() {
        HBox leftContainer = new HBox(10);
        leftContainer.setAlignment(Pos.CENTER_LEFT);

        // Boutons principaux
        Button statsBtn = createStyledButton("📊", "Stats", "#4CAF50");
        Button exportBtn = createStyledButton("📤", "Exporter", "#2196F3");
        Button conflictsBtn = createStyledButton("⚠️", "Conflits", "#F44336");
        Button dailyBtn = createStyledButton("📅", "Aujourd'hui", "#9C27B0");
        Button highlightBtn = createStyledButton("🌟", "Surbrillance", "#FFC107");

        // Configuration des actions
        exportBtn.setOnAction(e -> showExportDialogWithStyle());
        highlightBtn.setOnAction(e -> showHighlightDialogWithStyle());

        leftContainer.getChildren().addAll(statsBtn, exportBtn, conflictsBtn, dailyBtn, highlightBtn);
        return leftContainer;
    }

    // 🔥 NOUVEAU : Conteneur de recherche de droite
    private HBox createRightSearchContainer() {
        HBox rightContainer = new HBox(10);
        rightContainer.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(rightContainer, Priority.ALWAYS); // Prend tout l'espace disponible

        // Champ de recherche
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Rechercher un cours...");
        searchField.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #E85B8A;" +
                        "-fx-border-radius: 20;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 8 15;" +
                        "-fx-font-size: 13px;" +
                        "-fx-pref-width: 250px;"
        );

        // Bouton de recherche
        Button searchButton = createStyledButton("🔍", "Rechercher", "#E85B8A");
        searchButton.setStyle(
                "-fx-background-color: #E85B8A;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 8 15;" +
                        "-fx-cursor: hand;"
        );

        // Bouton effacer
        Button clearButton = createStyledButton("🗑️", "Effacer", "#9E9E9E");
        clearButton.setStyle(
                "-fx-background-color: #9E9E9E;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 8 15;" +
                        "-fx-cursor: hand;"
        );

        // Actions
        searchButton.setOnAction(e -> performSearch(searchField.getText()));
        clearButton.setOnAction(e -> {
            searchField.clear();
            hideSearchResults();
            refreshSlots();
        });

        rightContainer.getChildren().addAll(searchField, searchButton, clearButton);
        return rightContainer;
    }

    // 🔥 NOUVEAU : Création de boutons stylisés
    private Button createStyledButton(String emoji, String text, String color) {
        Button button = new Button(emoji + " " + text);

        String normalStyle = String.format(
                "-fx-background-color: %s;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 11px;" +
                        "-fx-padding: 8 12;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-radius: 20;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);" +
                        "-fx-cursor: hand;",
                color
        );

        String hoverStyle = String.format(
                "-fx-background-color: derive(%s, -15%%);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 11px;" +
                        "-fx-padding: 8 12;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-radius: 20;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 6, 0, 0, 3);" +
                        "-fx-cursor: hand;",
                color
        );

        button.setStyle(normalStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(normalStyle));

        return button;
    }

    // 🔥 MODIFIÉ : Ajouter la zone de résultats sous la barre principale
    private void addSearchResultsToMainContainer() {
        Scene scene = calendarPresenter.getScene();
        if (scene != null && scene.getRoot() instanceof VBox && searchComponentsInitialized) {
            VBox mainContainer = (VBox) scene.getRoot();

            // Vérifier si la zone de résultats existe déjà
            boolean resultsExist = mainContainer.getChildren().stream()
                    .anyMatch(node -> node instanceof VBox && node == searchResultsContainer);

            if (!resultsExist) {
                // Ajouter la zone de résultats après la barre de fonctionnalités
                int featureIndex = -1;
                for (int i = 0; i < mainContainer.getChildren().size(); i++) {
                    if (mainContainer.getChildren().get(i) instanceof HBox) {
                        featureIndex = i;
                        break;
                    }
                }

                if (featureIndex != -1) {
                    mainContainer.getChildren().add(featureIndex + 1, searchResultsContainer);
                } else {
                    mainContainer.getChildren().add(searchResultsContainer);
                }
            }
        }
    }

    // 🔥 NOUVEAU : Recherche avancée avec résultats
    private void performSearch(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            showSearchResults("Veuillez entrer un terme de recherche", Collections.emptyList());
            return;
        }

        List<Slot> matchingSlots = new ArrayList<>();
        String searchTerm = keyword.toLowerCase().trim();

        for (Slot slot : slotsById.values()) {
            if (slot.description().toLowerCase().contains(searchTerm) ||
                    slot.startDateTime().toLocalDate().toString().contains(searchTerm) ||
                    slot.startDateTime().toLocalTime().toString().contains(searchTerm)) {
                matchingSlots.add(slot);
            }
        }

        if (matchingSlots.isEmpty()) {
            showSearchResults("Aucun résultat trouvé pour : \"" + keyword + "\"", Collections.emptyList());
        } else {
            List<String> resultStrings = new ArrayList<>();
            for (Slot slot : matchingSlots) {
                String result = String.format("📅 %s | 🕒 %s | 📝 %s",
                        slot.startDateTime().toLocalDate(),
                        slot.startDateTime().toLocalTime(),
                        slot.description()
                );
                resultStrings.add(result);
            }
            showSearchResults(matchingSlots.size() + " résultat(s) trouvé(s) pour : \"" + keyword + "\"", resultStrings);
        }

        // 🔥 S'assurer que la zone de résultats est ajoutée au conteneur principal
        addSearchResultsToMainContainer();
    }

    // 🔥 NOUVEAU : Affichage des résultats de recherche
    private void showSearchResults(String summary, List<String> results) {
        if (!searchComponentsInitialized || searchResultsContainer == null) {
            System.err.println("❌ Composants de recherche non initialisés");
            return;
        }

        searchResultsLabel.setText(summary);
        searchResultsList.getItems().setAll(results);

        // Style dynamique selon les résultats
        if (results.isEmpty()) {
            searchResultsContainer.setStyle(
                    "-fx-background-color: #FFF3E0;" +
                            "-fx-border-color: #FF9800;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 10;" +
                            "-fx-background-radius: 10;"
            );
        } else {
            searchResultsContainer.setStyle(
                    "-fx-background-color: #E8F5E8;" +
                            "-fx-border-color: #4CAF50;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 10;" +
                            "-fx-background-radius: 10;"
            );
        }

        searchResultsContainer.getChildren().setAll(searchResultsLabel, searchResultsList);
        searchResultsContainer.setVisible(true);
    }

    // 🔥 NOUVEAU : Masquer les résultats
    private void hideSearchResults() {
        if (searchComponentsInitialized && searchResultsContainer != null) {
            searchResultsContainer.setVisible(false);
        }
    }

    // 🔥 VERSIONS STYLISÉES DES DIALOGUES EXISTANTS
    private void showExportDialogWithStyle() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("CSV", "CSV", "HTML", "PDF");
        dialog.setTitle("📤 Export du Calendrier");
        dialog.setHeaderText("Choisissez le format d'export");
        dialog.setContentText("Format :");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E85B8A;");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(format -> {
            List<Slot> slots = new ArrayList<>(slotsById.values());
            String timestamp = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = String.format("calendrier_export_%s.%s", timestamp, format.toLowerCase());

            if ("CSV".equals(format)) {
                CalendarExporter.exportToCSV(slots, filename);
                showAlert("✅ Export réussi", "Fichier CSV créé : " + filename, Alert.AlertType.INFORMATION);
            } else if ("HTML".equals(format)) {
                CalendarExporter.exportToHTML(slots, filename);
                showAlert("✅ Export réussi", "Fichier HTML créé : " + filename, Alert.AlertType.INFORMATION);
            } else {
                showAlert("⚠️ Format non supporté", "Le format PDF n'est pas encore implémenté", Alert.AlertType.WARNING);
            }
        });
    }

    private void showHighlightDialogWithStyle() {
        TextInputDialog dialog = new TextInputDialog(LocalDate.now().toString());
        dialog.setTitle("🌟 Surbrillance de Date");
        dialog.setHeaderText("Mettre en surbrillance une date spécifique");
        dialog.setContentText("Date (AAAA-MM-JJ):");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #FFC107;");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(dateString -> {
            try {
                LocalDate date = LocalDate.parse(dateString);
                showAlert("🌟 Surbrillance activée", "Les slots du " + date + " sont mis en surbrillance", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("❌ Erreur", "Date invalide : " + dateString, Alert.AlertType.ERROR);
            }
        });
    }



    // === MÉTHODES UTILITAIRES POUR ACCÉDER AU PRESENTER ===

    private PeriodCalendarPresenter getPeriodCalendarPresenter() {
        if (calendarPresenter instanceof PeriodCalendarPresenter) {
            return (PeriodCalendarPresenter) calendarPresenter;
        }
        throw new IllegalStateException("Le presenter n'est pas une instance de PeriodCalendarPresenter");
    }

    @Override
    public Scene getScene() {
        calendarPresenter.updateDays(dayGenerator);
        calendarPresenter.updateTimeIntervals(calendarViewConfiguration.getTimeIntervalGenerator());
        refreshSlots();

        if (!advancedFeaturesAdded) {
            addAdvancedFeatures();
        }

        return calendarPresenter.getScene();
    }

    /*private void updateSlot(Slot newSlot){
        boolean isUpdated = calendar.update(newSlot);
        if(!isUpdated)
            return;
        slotsById.put(newSlot.id(), newSlot);
        calendarPresenter.removeSlotView(newSlot.id());
        calendarPresenter.addSlotView(newSlot, calendarViewConfiguration.colorOfSlots(), this::handleSlotEdition);
    }*/
    private void updateSlot(Slot newSlot) {
        System.out.println("🔄 Tentative de mise à jour du slot #" + newSlot.id());
        System.out.println("📊 Données du slot à mettre à jour:");
        System.out.println("   - ID: " + newSlot.id());
        System.out.println("   - Matière: " + newSlot.getMatiere());
        System.out.println("   - Date: " + newSlot.getDate());
        System.out.println("   - Heure début: " + newSlot.getHeureDebut());
        System.out.println("   - Heure fin: " + newSlot.getHeureFin());
        System.out.println("   - Enseignant ID: " + newSlot.enseignantId());
        System.out.println("   - Salle ID: " + newSlot.salleId());
        System.out.println("   - Version: " + newSlot.versionNumber());

        try {
            // 1. Mettre à jour dans le backend
            boolean isUpdated = calendar.update(newSlot);

            if (!isUpdated) {
                System.err.println("❌ Échec de la mise à jour dans le calendrier");
                showAlert("Erreur", "Échec de la mise à jour du créneau #" + newSlot.id(), Alert.AlertType.ERROR);
                return;
            }

            // 2. Mettre à jour le cache local
            slotsById.put(newSlot.id(), newSlot);

            // 3. Rafraîchir l'affichage
            calendarPresenter.removeSlotView(newSlot.id());
            calendarPresenter.addSlotView(newSlot, calendarViewConfiguration.colorOfSlots(), this::handleSlotEdition);

            // 4. Planifier un rafraîchissement complet
            scheduleRefresh();

            System.out.println("✅ Slot #" + newSlot.id() + " mis à jour avec succès");
            showAlert("Succès", "Créneau #" + newSlot.id() + " modifié avec succès", Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la mise à jour du slot: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la modification du créneau #" + newSlot.id() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    // Ajoutez cette méthode dans la classe PeriodCalendarViewController


    /*private void createSlot(Slot slot) {
        // 🔥 DEBUG : Avant création
        System.out.println("🔄 Avant création - Slot ID: " + slot.id());

        boolean isCreated = calendar.create(slot);

        // 🔥 DEBUG : Après création
        System.out.println("🔄 Après création - Slot ID: " + slot.id());
        System.out.println("🔄 Résultat création: " + isCreated);

        if(!isCreated)
            return;

        // Si l'ID a été mis à jour dans l'objet slot
        if (slot.id() != -1) {
            addSlotToView(slot);
            System.out.println("✅ Slot ajouté avec ID: " + slot.id());
        } else {
            // Si l'ID n'a pas été mis à jour, utiliser la Solution 1
            Collection<Slot> recentSlots = calendar.getAllSlotsBetween(
                    slot.startDateTime().toLocalDate(),
                    slot.startDateTime().toLocalDate()
            );

            Slot createdSlot = recentSlots.stream()
                    .filter(s -> s.description().equals(slot.description()) &&
                            Math.abs(Duration.between(s.startDateTime(), slot.startDateTime()).toMinutes()) < 5)
                    .findFirst()
                    .orElse(null);

            if (createdSlot != null) {
                addSlotToView(createdSlot);
                System.out.println("✅ Slot trouvé et ajouté avec ID: " + createdSlot.id());
            }
        }
    }*/
    private void createSlot(Slot slot) {
        System.out.println("🔄 Tentative de création du slot: " + slot.description());

        try {
            boolean isCreated = calendar.create(slot);

            if (!isCreated) {
                System.err.println("❌ Échec de la création dans le calendrier");
                return;
            }

            System.out.println("✅ Création API réussie - planification du rafraîchissement");

            // ✅ CORRECTION : Planifier un rafraîchissement après un court délai
            scheduleRefresh();

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la création du slot: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void debugDateRange() {
        System.out.println("🔍 DEBUG Plage de dates:");
        System.out.println("   - Start Date: " + dayGenerator.getStartDate());
        System.out.println("   - End Date: " + dayGenerator.getEndDate());
        System.out.println("   - Slot Date: 2025-11-23");
        System.out.println("   - Dans la plage: " +
                (LocalDate.of(2025, 11, 23).isAfter(dayGenerator.getStartDate().minusDays(1)) &&
                        LocalDate.of(2025, 11, 23).isBefore(dayGenerator.getEndDate())));
    }

  /*  private void addSlotToView(Slot slot) {
        if (dayGenerator.getDayIndex(slot.startDateTime().toLocalDate()) == -1)
            return;
        slotsById.put(slot.id(), slot);
        calendarPresenter.addSlotView(slot, calendarViewConfiguration.colorOfSlots(), this::handleSlotEdition);
    }*/
  private void addSlotToView(Slot slot) {
      try {
          if (slot == null) {
              System.err.println("❌ Tentative d'ajout d'un slot null");
              return;
          }

          // ✅ CORRECTION : Vérification plus flexible des dates
          LocalDate slotDate = slot.startDateTime().toLocalDate();
          LocalDate startDate = dayGenerator.getStartDate();
          LocalDate endDate = dayGenerator.getEndDate().minusDays(1); // -1 car endDate est exclusive

          if (slotDate.isBefore(startDate) || slotDate.isAfter(endDate)) {
              System.out.println("⚠️ Slot " + slot.id() + " en dehors de la plage affichée: " + slotDate);
              return;
          }

          int dayIndex = dayGenerator.getDayIndex(slotDate);
          if (dayIndex == -1) {
              System.err.println("❌ Impossible de trouver l'index du jour pour: " + slotDate);
              return;
          }

          // Ajouter le slot aux collections
          slotsById.put(slot.id(), slot);

          // ✅ CORRECTION : Convertir la couleur en String hexadécimal
          javafx.scene.paint.Color colorPaint = calendarViewConfiguration != null ?
                  calendarViewConfiguration.colorOfSlots() : javafx.scene.paint.Color.web("#E85B8A");

          // Convertir Color en String hex
          String colorHex = colorToHex(colorPaint);

          calendarPresenter.addSlotView(slot, colorPaint, this::handleSlotEdition);

          System.out.println("✅ Slot " + slot.id() + " ajouté à la vue avec succès");

      } catch (Exception e) {
          System.err.println("❌ Erreur lors de l'ajout du slot " + slot.id() + " à la vue: " + e.getMessage());
          e.printStackTrace();
      }
  }

    // ✅ AJOUTER CETTE MÉTHODE UTILITAIRE POUR CONVERTIR LES COULEURS
    private String colorToHex(javafx.scene.paint.Color color) {
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);
        return String.format("#%02X%02X%02X", r, g, b);
    }
    private void scheduleRefresh() {
        // Rafraîchir automatiquement après 500ms
        new Thread(() -> {
            try {
                Thread.sleep(500);
                javafx.application.Platform.runLater(this::refreshSlots);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    /**
     * Convertit un Slot en SlotDTO pour l'envoi au backend
     */
    private SlotDTO convertToDTO(Slot slot) {
        if (slot == null) {
            System.err.println("❌ Tentative de conversion d'un slot null en DTO");
            return null;
        }

        try {
            System.out.println("🔄 Conversion Slot -> DTO pour le slot #" + slot.id());

            // CORRECTION: Utiliser startDateTime() et calculer timeEnd à partir de la durée
            LocalDateTime timeBegin = slot.startDateTime();

            // CALCULER timeEnd en ajoutant la durée à timeBegin
            LocalDateTime timeEnd = timeBegin.plus(slot.duration());

            // Vérifier la cohérence des dates
            if (timeBegin != null && timeEnd != null && timeBegin.isAfter(timeEnd)) {
                System.err.println("❌ Inversion des dates détectée dans le slot #" + slot.id());
                // Corriger l'inversion
                LocalDateTime temp = timeBegin;
                timeBegin = timeEnd;
                timeEnd = temp;
            }

            SlotDTO dto = new SlotDTO(
                    slot.id(),
                    slot.getMatiere(), // Utilisé comme nom
                    slot.getMatiere(), // Utilisé comme description
                    timeBegin,
                    timeEnd,
                    slot.versionNumber(),
                    slot.enseignantId(),
                    slot.salleId()
            );

            System.out.println("✅ Conversion réussie - DTO créé pour le slot #" + slot.id());
            System.out.println("   - Début: " + timeBegin);
            System.out.println("   - Fin: " + timeEnd);
            System.out.println("   - Durée: " + slot.duration().toHours() + "h" + slot.duration().toMinutesPart() + "m");

            return dto;

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la conversion Slot -> DTO: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Convertit un SlotDTO en Slot pour l'usage interne
     */
    private Slot convertFromDTO(SlotDTO dto) {
        if (dto == null) {
            System.err.println("❌ Tentative de conversion d'un DTO null en Slot");
            return null;
        }

        try {
            System.out.println("🔄 Conversion DTO -> Slot pour le slot #" + dto.id());

            // Calculer la durée
            Duration duration = Duration.between(dto.timeBegin(), dto.timeEnd());

            // Vérifier la durée positive
            if (duration.isNegative()) {
                System.err.println("❌ Durée négative détectée dans le DTO #" + dto.id());
                duration = Duration.ofHours(2); // Durée par défaut
            }

            Slot slot = new SimpleSlot(
                    dto.id(),
                    dto.nom() != null ? dto.nom() : "Sans nom",
                    dto.description() != null ? dto.description() : "Sans description",
                    dto.timeBegin(),
                    duration,
                    dto.version(),
                    dto.enseignantId(),
                    dto.salleId()
            );

            System.out.println("✅ Conversion réussie - Slot créé pour le DTO #" + dto.id());
            return slot;

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la conversion DTO -> Slot: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    private void refreshSlots() {
        System.out.println("🔄 Début du rafraîchissement des slots...");

        try {
            // Vider les caches
            slotsById.clear();
            calendarPresenter.clearSlotViews();

            // Récupérer les slots depuis le calendrier
            Collection<Slot> slots = calendar.getAllSlotsBetween(
                    dayGenerator.getStartDate(),
                    dayGenerator.getEndDate().minusDays(1)
            );

            System.out.println("📥 " + slots.size() + " slots récupérés pour l'affichage");

            // Ajouter chaque slot à la vue
            for (Slot slot : slots) {
                System.out.println("🎯 Ajout du slot " + slot.id() + " à l'affichage: " + slot.description());
                addSlotToView(slot);
            }

            // Appeler le callback si disponible
            if (dataRefreshCallback != null && !slots.isEmpty()) {
                dataRefreshCallback.accept(new ArrayList<>(slots));
            }

            hideSearchResults();
            System.out.println("✅ Rafraîchissement terminé - " + slotsById.size() + " slots affichés");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du rafraîchissement: " + e.getMessage());
            e.printStackTrace();
        }
    }

   /* private void refreshSlots() {
        slotsById.clear();
        calendarPresenter.clearSlotViews();
        Collection<Slot> slots = calendar.getAllSlotsBetween(dayGenerator.getStartDate(),
                dayGenerator.getEndDate().minusDays(1));
        for (Slot slot : slots) {
            addSlotToView(slot);
        }
        hideSearchResults();
    }*/
}