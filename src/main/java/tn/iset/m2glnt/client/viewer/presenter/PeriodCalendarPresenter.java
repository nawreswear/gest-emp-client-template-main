package tn.iset.m2glnt.client.viewer.presenter;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import tn.iset.m2glnt.client.model.Slot;
import tn.iset.m2glnt.client.util.DayGenerator;
import tn.iset.m2glnt.client.util.TimeInterval;
import tn.iset.m2glnt.client.util.TimeIntervalGenerator;
import tn.iset.m2glnt.client.viewer.controller.CalendarViewController;
import tn.iset.m2glnt.client.viewer.view.ButtonConfiguration;
import tn.iset.m2glnt.client.viewer.view.CalendarView;
import tn.iset.m2glnt.client.viewer.view.SlotView;

import java.time.*;
import java.util.List;
import java.util.function.Consumer;


public class PeriodCalendarPresenter implements CalendarPresenter {
    private final CalendarView view;
    private DayGenerator days;
    private TimeIntervalGenerator timeIntervals;
    private final ViewElementFactory viewElementFactory = new SimpleViewElementFactory();

    public PeriodCalendarPresenter(CalendarViewController calendarController,
                                   DayGenerator days,
                                   TimeIntervalGenerator timeIntervals) {
        this.view = viewElementFactory.createGrid(days.getNumberOfDays()+1, timeIntervals.getNumberOfIntervals()+1,
                100,100, 50, 20,
                Color.BLACK, Color.WHITE);
        updateButtons(List.of(new ButtonConfiguration("<", calendarController::handlePrevious),
                new ButtonConfiguration(">", calendarController::handleNext),
                new ButtonConfiguration("+", calendarController::handleSlotCreation)));
        updateDays(days);
        updateTimeIntervals(timeIntervals);
    }

    private Position getPositionOf(Slot slotInfo) {
        LocalDateTime startDateTime = slotInfo.startDateTime();
        LocalTime time = startDateTime.toLocalTime();
        LocalDate date = startDateTime.toLocalDate();
        return new Position(dateIndex(date), intervalIndex(time), numberOfSlots(slotInfo.duration()));
    }

    private  record Position(int dayIndex, int intervalIndex, int numberOfIntervals) {

    }

    private int numberOfSlots(Duration duration) {
        return (int) duration.dividedBy(timeIntervals.getIntervalDuration());
    }

    private int intervalIndex(LocalTime time){
        return timeIntervals.getTimeIndex(time);
    }

    private int dateIndex(LocalDate date){
        return days.getDayIndex(date);
    }

    @Override
    public Scene getScene() {
        return view.constructScene();
    }

    @Override
    public void clearSlotViews() {
        view.clearViewSlots();
    }

    @Override
    public void addSlotView(Slot slot, Color backGroundColor, Consumer<Integer> actionOnClick) {
        SlotViewData slotViewData = new SlotViewData(slot.id(), slot.description());
        Position position = getPositionOf(slot);
        SlotView slotView = viewElementFactory.createSlotView(slotViewData, backGroundColor);
        slotView.addEventHandler(MouseEvent.MOUSE_CLICKED, __ -> actionOnClick.accept(slot.id()));
        view.addSlotView(slotView,
                position.dayIndex()+1, position.intervalIndex()+1,
                1, position.numberOfIntervals());
    }

    @Override
    public void removeSlotView(int idSlot) {
        view.removeSlot(idSlot);
    }

    @Override
    public void updateDays(DayGenerator days) {
        clearSlotViews();
        view.clearLabelsInFirstRow();
        this.days = days;
        int dayColumnIndex = 1;
        for (LocalDate date : days) {
            Label label = viewElementFactory.createDateLabel(date);
            view.addLabelInFirstRow(label, dayColumnIndex);
            dayColumnIndex++;
        }

    }

    @Override
    public void updateTimeIntervals(TimeIntervalGenerator timeIntervals) {
        this.timeIntervals = timeIntervals;
        view.clearLabelsInFirstColumn();
        int intervalIndex = 1 ;
        for (TimeInterval timeInterval : timeIntervals){
            Label label = viewElementFactory.createTimeIntervalLabel(timeInterval);
            view.addLabelInFirstColumn(label, intervalIndex);
            intervalIndex++;
        }
    }

    public void updateButtons(List<ButtonConfiguration> buttonConfigurations) {
        HBox hBox = viewElementFactory.createButtonBox(buttonConfigurations);
        view.addButtonBoxInTopLeftCell(hBox);
    }
}
