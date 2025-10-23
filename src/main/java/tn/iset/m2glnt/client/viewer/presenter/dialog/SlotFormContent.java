package tn.iset.m2glnt.client.viewer.presenter.dialog;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public record SlotFormContent(LocalTime startTime, LocalDate startDate, Duration duration, String description) {
}
