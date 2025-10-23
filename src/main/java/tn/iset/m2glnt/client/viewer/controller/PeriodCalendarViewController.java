package tn.iset.m2glnt.client.viewer.controller;

import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import tn.iset.m2glnt.client.model.Calendar;
import tn.iset.m2glnt.client.model.SimpleSlot;
import tn.iset.m2glnt.client.model.Slot;
import tn.iset.m2glnt.client.util.DateInterval;
import tn.iset.m2glnt.client.util.DayGenerator;
import tn.iset.m2glnt.client.viewer.presenter.CalendarPresenter;
import tn.iset.m2glnt.client.viewer.presenter.PeriodCalendarPresenter;
import tn.iset.m2glnt.client.viewer.presenter.dialog.SimpleSlotFormFactory;
import tn.iset.m2glnt.client.viewer.presenter.dialog.SlotFormAction;
import tn.iset.m2glnt.client.viewer.presenter.dialog.SlotFormFactory;
import tn.iset.m2glnt.client.viewer.presenter.dialog.SlotFormResult;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PeriodCalendarViewController implements CalendarViewController {
    private final CalendarPresenter calendarPresenter;
    private final CalendarViewConfiguration calendarViewConfiguration;
    private DayGenerator dayGenerator;
    private final Calendar calendar;
    private final SlotFormFactory slotFormFactory;
    private final Map<Integer, Slot> slotsById = new HashMap<>();

    public PeriodCalendarViewController(Calendar calendar, CalendarViewConfiguration calendarViewConfiguration) {
        this.calendarViewConfiguration = calendarViewConfiguration;
        this.calendar = calendar;
        LocalDate today = LocalDate.now();
        setStartDate(calendarViewConfiguration.getPeriodStartDateContaining(today));
        List<Duration> possiblesDurations = calendarViewConfiguration.getPossibleDurations().getDurations();
        List<LocalTime> possibleTimes = calendarViewConfiguration.getTimeIntervalGenerator().getStartTimesOfIntervals();
        possibleTimes.add(calendarViewConfiguration.getTimeIntervalGenerator().getEndTime());
        this.slotFormFactory = new SimpleSlotFormFactory(possibleTimes, possiblesDurations);
        this.calendarPresenter = new PeriodCalendarPresenter(this, dayGenerator,
                calendarViewConfiguration.getTimeIntervalGenerator());
        refreshSlots();
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

    @Override
    public void handleSlotEdition(int idSlot) {
        Slot slot = slotsById.get(idSlot);
        if (slot == null) {
            return;
        }
        Dialog<SlotFormResult> dialog = slotFormFactory.createCalendarEventDialog(slot);
        var result = dialog.showAndWait();
        result.ifPresent(r -> handleFormEditionResult(r, slot));
    }

    private void handleFormEditionResult(SlotFormResult r, Slot slot) {
        var form = r.slotFormContent();
        switch (r.slotFormAction()){
            case CANCEL -> {}
            case DELETE -> deleteSlot(slot.id());
            case CONFIRM -> {
                if(form != null) {
                LocalDateTime dateTimeStart = LocalDateTime.of(form.startDate(), form.startTime());
                    Slot editedSlot = new SimpleSlot(form.description(), dateTimeStart, form.duration(),
                            slot.id(), slot.versionNumber() + 1);
                    updateSlot(editedSlot);
                }
            }
    }
    }

    private void deleteSlot(int idSlot) {
        boolean isRemoved = calendar.delete(slotsById.get(idSlot));
        if(!isRemoved) {
            return;
        }
        slotsById.remove(idSlot);
        calendarPresenter.removeSlotView(idSlot);
    }

    @Override
    public void handleSlotCreation() {
        Dialog<SlotFormResult> dialog = slotFormFactory.createCalendarEventDialog(LocalDateTime.now(),
                calendarViewConfiguration.getDefaultDurationIndex());
        var result = dialog.showAndWait();
        result.ifPresent(this::handleSlotCreation);
    }

    private void handleSlotCreation(SlotFormResult r) {
        var form = r.slotFormContent();
        if(form != null && r.slotFormAction() == SlotFormAction.CONFIRM) {
            LocalDateTime dateTimeStart = LocalDateTime.of(form.startDate(), form.startTime());
            Slot calendarSlot = new SimpleSlot(form.description(), dateTimeStart, form.duration(), -1, 0);
            createSlot(calendarSlot);
        }
    }

    @Override
    public Scene getScene() {
        calendarPresenter.updateDays(dayGenerator);
        calendarPresenter.updateTimeIntervals(calendarViewConfiguration.getTimeIntervalGenerator());
        refreshSlots();
        return calendarPresenter.getScene();
    }

    private void updateSlot(Slot newSlot){
        boolean isUpdated = calendar.update(newSlot);
        if(!isUpdated)
            return;
        slotsById.put(newSlot.id(), newSlot);
        calendarPresenter.removeSlotView(newSlot.id());
        calendarPresenter.addSlotView(newSlot, calendarViewConfiguration.colorOfSlots(), this::handleSlotEdition);
    }

    private void createSlot(Slot slot) {
        boolean isCreated = calendar.create(slot);
        if(!isCreated)
            return;
        addSlotToView(slot);
    }

    private void addSlotToView(Slot slot) {
        if (dayGenerator.getDayIndex(slot.startDateTime().toLocalDate()) == -1)
            return;
        slotsById.put(slot.id(), slot);
        calendarPresenter.addSlotView(slot, calendarViewConfiguration.colorOfSlots(), this::handleSlotEdition);
    }

    private void refreshSlots() {
        slotsById.clear();
        calendarPresenter.clearSlotViews();
        Collection<Slot> slots = calendar.getAllSlotsBetween(dayGenerator.getStartDate(),
                dayGenerator.getEndDate().minusDays(1));
        for (Slot slot : slots) {
            addSlotToView(slot);
        }
    }

}
