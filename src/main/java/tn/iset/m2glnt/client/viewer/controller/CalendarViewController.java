package tn.iset.m2glnt.client.viewer.controller;

import javafx.scene.Scene;

public interface CalendarViewController {
    void handleNext();
    void handlePrevious();
    void handleSlotEdition(int idSlot);
    void handleSlotCreation();
    Scene getScene();
}
