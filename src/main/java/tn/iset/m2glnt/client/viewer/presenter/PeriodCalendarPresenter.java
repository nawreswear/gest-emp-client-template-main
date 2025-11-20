package tn.iset.m2glnt.client.viewer.presenter;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import tn.iset.m2glnt.client.model.Slot;
import tn.iset.m2glnt.client.util.DayGenerator;
import tn.iset.m2glnt.client.util.DateTimeNormalizer;
import tn.iset.m2glnt.client.util.TimeInterval;
import tn.iset.m2glnt.client.util.TimeIntervalGenerator;
import tn.iset.m2glnt.client.viewer.controller.CalendarViewController;
import tn.iset.m2glnt.client.viewer.view.ButtonConfiguration;
import tn.iset.m2glnt.client.viewer.view.CalendarView;
import tn.iset.m2glnt.client.viewer.view.GridCalendarView;
import tn.iset.m2glnt.client.viewer.view.SlotView;

import java.time.*;
import java.util.*;
import java.util.function.Consumer;

/**
 * Presenter final pour l'affichage périodique du calendrier.
 * Version "finale" : propre, validations et synchronisation basiques.
 */
public class PeriodCalendarPresenter implements CalendarPresenter {

    private final CalendarView view;
    private DayGenerator days;
    private TimeIntervalGenerator timeIntervals;
    private final ViewElementFactory viewElementFactory = new SimpleViewElementFactory();
    private final Map<Integer, Slot> slotCache = new HashMap<>();

    /**
     * Constructeur.
     *
     * @param calendarController controller gérant les actions (précédent / suivant / création)
     * @param days               generator des jours affichés
     * @param timeIntervals      generator des intervalles horaires
     */
    public PeriodCalendarPresenter(CalendarViewController calendarController,
                                   DayGenerator days,
                                   TimeIntervalGenerator timeIntervals) {
        if (days == null || timeIntervals == null || calendarController == null) {
            throw new IllegalArgumentException("Days, timeIntervals et calendarController doivent être non null");
        }

        this.days = days;
        this.timeIntervals = timeIntervals;

        // Création de la grille via la factory (taille colonne/ligne + styles)
        this.view = viewElementFactory.createGrid(
                days.getNumberOfDays() + 1,
                timeIntervals.getNumberOfIntervals() + 1,
                100, 100, 50, 20,
                Color.BLACK, Color.WHITE
        );

        updateButtons(List.of(
                new ButtonConfiguration("<", calendarController::handlePrevious),
                new ButtonConfiguration(">", calendarController::handleNext),
                new ButtonConfiguration("+", calendarController::handleSlotCreation)
        ));

        updateDays(days);
        updateTimeIntervals(timeIntervals);
        setupRefreshListener();
    }
    private void setupRefreshListener() {
        if (this.view instanceof GridCalendarView) {
            GridCalendarView gridView = (GridCalendarView) this.view;

            gridView.addCalendarEventListener((eventType, data) -> {
                switch (eventType) {
                    case "calendarRefreshRequested" -> {
                        System.out.println("🔄 Rafraîchissement demandé par la vue");
                        // Recharger tous les slots depuis la source de données
                        refreshAllSlotsFromDataSource();
                    }
                    case "slotRefreshRequested" -> {
                        int slotId = (int) data;
                        System.out.println("🔄 Rafraîchissement du slot " + slotId + " demandé");
                        refreshSingleSlot(slotId);
                    }
                }
            });
        }
    }
    private void refreshAllSlotsFromDataSource() {
        // Implémentez cette méthode selon votre source de données
        System.out.println("🔄 Rechargement de tous les slots depuis la source...");

        // Exemple: vider et recharger tous les slots
        clearSlotViews();

        // Ici, vous devriez recharger les slots depuis votre service/DAO
        // loadSlotsFromService();
    }
    public CalendarView getView() {
        return this.view;
    }
    private void refreshSingleSlot(int slotId) {
        System.out.println("🔄 Rechargement du slot " + slotId);

        // Implémentez le rechargement d'un slot spécifique
        // Slot updatedSlot = loadSlotFromService(slotId);
        // if (updatedSlot != null) {
        //     updateSlotInView(updatedSlot);
        // }
    }

    /**
     * Ajoute un nouveau slot créé côté client et mis à jour ensuite avec l'ID réel.
     *
     * @param temporarySlot slot temporaire (ID provisoire)
     * @param newSlotId     nouvel id renvoyé par la BDD
     * @param backGroundColor couleur d'affichage
     * @param actionOnClick action à exécuter au clic (slotId)
     */
    /**
     * Ajoute un nouveau slot créé côté client et mis à jour ensuite avec l'ID réel.
     * CORRECTION: Gestion améliorée du slot temporaire
     */
    public void addNewSlotWithIdUpdate(Slot temporarySlot, int newSlotId, Color backGroundColor, Consumer<Integer> actionOnClick) {
        if (temporarySlot == null) {
            System.out.println("❌ Slot temporaire null dans addNewSlotWithIdUpdate");
            return;
        }

        System.out.println("🎯 Mise à jour du slot temporaire " + temporarySlot.id() + " vers ID réel: " + newSlotId);

        // CORRECTION: Supprimer d'abord l'ancien slot temporaire
        if (slotCache.containsKey(temporarySlot.id())) {
            removeSlotView(temporarySlot.id());
            slotCache.remove(temporarySlot.id());
            System.out.println("🗑️ Slot temporaire " + temporarySlot.id() + " supprimé");
        }

        // Créer le slot corrigé avec le nouvel ID
        Slot correctedSlot = createSlotFromExisting(temporarySlot, newSlotId);

        // CORRECTION: Forcer le réajout avec la nouvelle couleur
        addSlotView(correctedSlot, backGroundColor, actionOnClick);

        // Mettre à jour le cache
        addToSlotCache(correctedSlot);

        System.out.println("✅ Slot mis à jour avec ID: " + newSlotId + " - Nom: " + correctedSlot.nom());
    }

    @Override
    public void addSlotView(Slot slot, Color backGroundColor, Consumer<Integer> actionOnClick) {
        if (slot == null) {
            System.out.println("❌ Slot null dans addSlotView");
            return;
        }

        System.out.println("🎯 Tentative d'ajout du slot ID: " + slot.id() + " - Nom: " + slot.nom());

        // CORRECTION: Autoriser les slots avec ID temporaire (-1) mais avec un warning
        if (slot.id() == -1) {
            System.out.println("⚠️  Slot temporaire détecté (ID: -1), ajout différé");
            addToSlotCache(slot);
            return;
        }

        // Normaliser la date/heure
        LocalDateTime normalized = normalizeSlotDateTime(slot.startDateTime());
        if (normalized == null) {
            System.out.println("❌ Impossible de normaliser la date/heure du slot " + slot.id());
            return;
        }

        // Synchroniser
        DateTimeNormalizer.syncDatabaseWithDisplay(slot.startDateTime(), normalized, slot.id());

        LocalDate slotDate = normalized.toLocalDate();
        LocalTime normalizedStartTime = normalized.toLocalTime();
        LocalTime normalizedEndTime = normalizedStartTime.plus(slot.duration());

        // Calculer position dans la grille
        Position position = getPositionOf(createSlotFromExisting(slot, slot.id(), normalized));
        if (position == null) {
            System.out.println("❌ Position invalide pour le slot " + slot.id());
            return;
        }

        // Vérifier limites de grille
        int maxRows = timeIntervals.getNumberOfIntervals() + 1;
        int maxColumns = days.getNumberOfDays() + 1;
        if (position.rowIndex() >= maxRows || position.columnIndex() >= maxColumns) {
            System.out.println("❌ Slot " + slot.id() + " hors limites de grille");
            return;
        }

        TimeInterval timeInterval = new TimeInterval(normalizedStartTime, normalizedEndTime);
        SlotViewData slotViewData = new SlotViewData(
                slot.id(),
                slot.nom() != null ? slot.nom() : slot.description(),
                slotDate,
                timeInterval,
                position.rowIndex(),
                position.columnIndex()
        );

        SlotView slotView = viewElementFactory.createSlotView(slotViewData, backGroundColor);

        // Clic sur le slot
        slotView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (actionOnClick != null) {
                actionOnClick.accept(slot.id());
            }
            event.consume();
        });

        // CORRECTION: Vérifier si le slot existe déjà avant d'ajouter
        if (slotCache.containsKey(slot.id())) {
            System.out.println("🔄 Slot " + slot.id() + " existe déjà, suppression avant réajout");
            removeSlotView(slot.id());
        }

        // Ajouter à la vue
        view.addSlotView(slotView, position.rowIndex(), position.columnIndex(), position.rowSpan(), 1);

        // Ajouter au cache
        addToSlotCache(slot);

        System.out.println("✅ Slot " + slot.id() + " ajouté à la position (" +
                position.columnIndex() + "," + position.rowIndex() + ")");
    }
    public void updateTemporarySlot(int temporaryId, int realId) {
        Slot temporarySlot = slotCache.get(temporaryId);
        if (temporarySlot == null) return;

        // Supprimer la vue existante (temporaire)
        removeSlotView(temporaryId);

        // Créer slot réel et l'ajouter
        Slot realSlot = createSlotFromExisting(temporarySlot, realId);
        addSlotView(realSlot, Color.LIGHTBLUE, this::handleSlotClick);

        // Mettre à jour le cache
        slotCache.remove(temporaryId);
        slotCache.put(realId, realSlot);
    }

    /**
     * Crée une implémentation concrète de Slot (utile si Slot est une interface).
     */
    private Slot createSlotImplementation(int id, String nom, String description, LocalDateTime startDateTime,
                                          Duration duration, int versionNumber, Long enseignantId, Long salleId) {
        return new Slot() {
            @Override
            public String nom() {
                return nom != null ? nom : description;
            }

            @Override
            public String description() {
                return description;
            }

            @Override
            public int id() {
                return id;
            }

            @Override
            public LocalDateTime startDateTime() {
                return startDateTime;
            }

            @Override
            public Duration duration() {
                return duration;
            }

            @Override
            public LocalDateTime getEndDateTime() {
                return startDateTime.plus(duration);
            }

            @Override
            public int versionNumber() {
                return versionNumber;
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
            public String toString() {
                return String.format("Slot[id=%d, nom=%s, desc=%s, start=%s, duration=%s]",
                        id, nom, description, startDateTime, duration);
            }
        };
    }

    /**
     * Surcharge pour créer un slot cloné depuis un slot existant en changeant seulement l'id.
     */
    private Slot createSlotFromExisting(Slot existingSlot, int newId) {
        if (existingSlot == null) throw new IllegalArgumentException("existingSlot ne peut pas être null");
        return createSlotImplementation(
                newId,
                existingSlot.nom(),
                existingSlot.description(),
                existingSlot.startDateTime(),
                existingSlot.duration(),
                existingSlot.versionNumber(),
                existingSlot.enseignantId(),
                existingSlot.salleId()
        );
    }

    /**
     * Sur-couche qui crée un slot à partir d'un slot existant et force la date/heure normalisée.
     * Utilisée pour le calcul précis de position si on veut appliquer la date normalisée.
     */
    private Slot createSlotFromExisting(Slot existingSlot, int newId, LocalDateTime normalizedStart) {
        if (existingSlot == null) throw new IllegalArgumentException("existingSlot ne peut pas être null");
        return createSlotImplementation(
                newId,
                existingSlot.nom(),
                existingSlot.description(),
                normalizedStart,
                existingSlot.duration(),
                existingSlot.versionNumber(),
                existingSlot.enseignantId(),
                existingSlot.salleId()
        );
    }

    /**
     * Validate placement: date in range, time in range and duration fits.
     */
    private boolean validateSlotPlacement(Slot slot) {
        if (slot == null) return false;
        LocalDateTime start = slot.startDateTime();
        LocalDate date = start.toLocalDate();
        LocalTime time = start.toLocalTime();

        if (!isDateInRange(date)) return false;
        if (!isTimeInGlobalRange(time)) return false;
        LocalTime end = time.plus(slot.duration());
        if (!isTimeInGlobalRange(end) && !end.equals(timeIntervals.getEndTime())) return false;
        return true;
    }

    private boolean isDateInRange(LocalDate date) {
        if (days == null) return false;
        for (LocalDate d : days) {
            if (d.equals(date)) return true;
        }
        return false;
    }

    private boolean isTimeInGlobalRange(LocalTime time) {
        TimeInterval first = null;
        TimeInterval last = null;
        for (TimeInterval t : timeIntervals) {
            if (first == null) first = t;
            last = t;
        }
        if (first == null || last == null) return false;
        boolean atOrAfterStart = !time.isBefore(first.start());
        boolean beforeOrAtEnd = !time.isAfter(last.end());
        return atOrAfterStart && beforeOrAtEnd;
    }

    /**
     * Trouve l'heure normalisée (début d'intervalle) la plus proche pour une heure donnée.
     * Retourne null si aucun intervalle n'est applicable.
     */
    private LocalTime findClosestIntervalStart(LocalTime time) {
        if (timeIntervals == null) return null;
        for (TimeInterval interval : timeIntervals) {
            if (!time.isBefore(interval.start()) && time.isBefore(interval.end())) {
                return interval.start();
            }
        }
        // cas où time == fin du dernier intervalle : renvoyer start du dernier intervalle
        TimeInterval last = null;
        for (TimeInterval interval : timeIntervals) last = interval;
        if (last != null && time.equals(last.end())) return last.start();
        return null;
    }

    /**
     * Normalise la date/heure d'un slot pour l'aligner sur la grille (début d'intervalle).
     * Retourne null si la normalisation est impossible (hors plage).
     */
    private LocalDateTime normalizeSlotDateTime(LocalDateTime originalDateTime) {
        if (originalDateTime == null) return null;
        LocalDate date = originalDateTime.toLocalDate();
        LocalTime time = originalDateTime.toLocalTime();

        if (!isDateInRange(date)) return null;
        if (!isTimeInGlobalRange(time)) return null;

        LocalTime normalized = findClosestIntervalStart(time);
        if (normalized == null) return null;

        return LocalDateTime.of(date, normalized);
    }

    /**
     * Retourne l'index de l'intervalle correspondant à une heure.
     * Si non trouvé, retourne -1.
     */
    private int intervalIndex(LocalTime time) {
        if (timeIntervals == null || time == null) return -1;
        int index = 0;
        for (TimeInterval interval : timeIntervals) {
            boolean matches = !time.isBefore(interval.start()) && time.isBefore(interval.end());
            if (matches) return index;
            index++;
        }
        // cas particulier : heure égale à la fin du dernier intervalle -> index du dernier
        TimeInterval last = null;
        int lastIndex = index - 1;
        for (TimeInterval interval : timeIntervals) last = interval;
        if (last != null && time.equals(last.end())) return lastIndex;
        return -1;
    }

    /**
     * Retourne l'index de la date dans le DayGenerator. -1 si non trouvé.
     */
    private int dateIndex(LocalDate date) {
        if (days == null || date == null) return -1;
        int index = 0;
        for (LocalDate d : days) {
            if (d.equals(date)) return index;
            index++;
        }
        return -1;
    }

    /**
     * Calcule la position (ligne, colonne, span) d'un slot sur la grille.
     * La ligne/colonne retournées sont déjà ajustées (+1) pour tenir compte des en-têtes.
     */
    private Position getPositionOf(Slot slotInfo) {
        if (slotInfo == null) return null;
        LocalDateTime startDateTime = slotInfo.startDateTime();
        LocalDate slotDate = startDateTime.toLocalDate();
        LocalTime slotTime = startDateTime.toLocalTime();

        int rowIndex = intervalIndex(slotTime);
        int columnIndex = dateIndex(slotDate);

        if (rowIndex < 0 || columnIndex < 0) {
            return null;
        }

        // Décalage pour en-têtes
        int finalRow = rowIndex + 1;
        int finalColumn = columnIndex + 1;
        int rowSpan = numberOfSlots(slotInfo.duration());

        return new Position(finalRow, finalColumn, rowSpan);
    }

    /**
     * Calcule le nombre de lignes (intervals) nécessaires pour une durée.
     */
    private int numberOfSlots(Duration duration) {
        if (duration == null || timeIntervals == null) return 1;
        Duration intervalDuration = timeIntervals.getIntervalDuration();
        if (intervalDuration == null || intervalDuration.isZero()) return 1;
        long pieces = duration.dividedBy(intervalDuration);
        return Math.max(1, (int) pieces);
    }

    @Override
    public Scene getScene() {
        return view.constructScene();
    }

    @Override
    public void clearSlotViews() {
        view.clearViewSlots();
        slotCache.clear();
    }

    @Override
    public void removeSlotView(int idSlot) {
        slotCache.remove(idSlot);
        view.removeSlot(idSlot);
    }

    @Override
    public void updateDays(DayGenerator days) {
        if (days == null) return;
        this.days = days;
        clearSlotViews();
        view.clearLabelsInFirstRow();

        int dayColumnIndex = 1;
        for (LocalDate date : days) {
            Label label = viewElementFactory.createDateLabel(date);
            view.addLabelInFirstRow(label, dayColumnIndex);
            dayColumnIndex++;
        }
    }

    @Override
    public void updateTimeIntervals(TimeIntervalGenerator timeIntervals) {
        if (timeIntervals == null) return;
        this.timeIntervals = timeIntervals;
        view.clearLabelsInFirstColumn();

        int intervalIndex = 1;
        for (TimeInterval timeInterval : timeIntervals) {
            Label label = viewElementFactory.createTimeIntervalLabel(timeInterval);
            view.addLabelInFirstColumn(label, intervalIndex);
            intervalIndex++;
        }
    }

    public void updateButtons(List<ButtonConfiguration> buttonConfigurations) {
        HBox hBox = viewElementFactory.createButtonBox(buttonConfigurations);
        view.addButtonBoxInTopLeftCell(hBox);
    }

    private void handleSlotClick(Integer slotId) {
        // Peut être géré par le controller extérieur; présent pour la compatibilité
    }

    /**
     * Récupère tous les slots connus (cache).
     */
    public List<Slot> getAllSlots() {
        return new ArrayList<>(slotCache.values());
    }

    /**
     * Vide le cache des slots (sans toucher à la vue).
     */
    public void clearSlotCache() {
        slotCache.clear();
    }

    /**
     * Ajoute un slot au cache.
     */
    public void addToSlotCache(Slot slot) {
        if (slot == null) return;
        slotCache.put(slot.id(), slot);
    }

    /**
     * Petit enregistrement immuable pour représenter une position dans la grille.
     */
    private record Position(int rowIndex, int columnIndex, int rowSpan) {}
}
