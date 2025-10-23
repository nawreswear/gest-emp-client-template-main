package tn.iset.m2glnt.client.viewer.presenter.dialog;

import javafx.scene.control.Dialog;
import tn.iset.m2glnt.client.model.Slot;

import java.time.LocalDateTime;

public interface SlotFormFactory {

    Dialog<SlotFormResult> createCalendarEventDialog(Slot slotInfo);

    Dialog<SlotFormResult> createCalendarEventDialog(LocalDateTime defaultLocalDateTime,
                                                     int defaultPossibleDurationIndex);
}
