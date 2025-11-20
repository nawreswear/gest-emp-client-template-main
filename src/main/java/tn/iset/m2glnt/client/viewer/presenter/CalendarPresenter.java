package tn.iset.m2glnt.client.viewer.presenter;

import javafx.scene.Scene;
import javafx.scene.paint.Color;
import tn.iset.m2glnt.client.model.Slot;
import tn.iset.m2glnt.client.util.DayGenerator;
import tn.iset.m2glnt.client.util.TimeIntervalGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

public interface CalendarPresenter {
    Scene getScene();
    void clearSlotViews();
    void addSlotView(Slot slot, Color backGroundColor, Consumer<Integer> actionOnClick);
    void removeSlotView(int idSlot);
    void updateDays(DayGenerator days);
    void updateTimeIntervals(TimeIntervalGenerator timeIntervals);

}