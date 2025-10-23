package tn.iset.m2glnt.client.viewer.view;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public interface CalendarView {
    void addLabelInFirstRow(Label label, int columnIndex);
    void addLabelInFirstColumn(Label label, int rowIndex);
    void addButtonBoxInTopLeftCell(HBox buttonBox);
    void addSlotView(SlotView slotView, int rowIndex, int columnIndex,
                     int rowSpan, int colSpan);
    void clearLabelsInFirstRow();
    void clearLabelsInFirstColumn();
    void clearButtonBoxInFirstCell();
    void clearViewSlots();
    void removeSlot(int slotId);
    Scene constructScene();


}
