package tn.iset.m2glnt.client.viewer.presenter.dialog;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;

public interface SlotFormData {
    LocalTime getStartTime();
    LocalDate getDate();
    Duration getDuration();
    String getDescription();
}