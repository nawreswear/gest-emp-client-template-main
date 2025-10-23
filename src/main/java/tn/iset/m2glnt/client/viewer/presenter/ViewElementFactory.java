package tn.iset.m2glnt.client.viewer.presenter;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import tn.iset.m2glnt.client.util.TimeInterval;
import tn.iset.m2glnt.client.viewer.view.ButtonConfiguration;
import tn.iset.m2glnt.client.viewer.view.GridCalendarView;
import tn.iset.m2glnt.client.viewer.view.SlotView;

import java.time.LocalDate;
import java.util.List;

public interface ViewElementFactory {
    Label createDateLabel(LocalDate date);
    Label createTimeIntervalLabel(TimeInterval timeInterval);
    SlotView createSlotView(SlotViewData slotViewData, Color backgroundColor);
    GridCalendarView createGrid(int rows, int columns,
                                int widthFirstColumn,
                                int widthSecondToLastColumn,
                                int heightFirstRow,
                                int heightSecondToLastRow,
                                Color linesColor,
                                Color backgroundColor);
    HBox createButtonBox(List<ButtonConfiguration> buttons);
}
