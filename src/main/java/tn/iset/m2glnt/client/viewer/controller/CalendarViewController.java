package tn.iset.m2glnt.client.viewer.controller;

import javafx.scene.Scene;
import tn.iset.m2glnt.client.model.Slot;

import java.util.List;
import java.util.function.Consumer;

public interface CalendarViewController {
    void handleNext();
    void handlePrevious();
    void handleSlotEdition(int idSlot);
    void handleSlotCreation();
    Scene getScene();
}
