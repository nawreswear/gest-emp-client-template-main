package tn.iset.m2glnt.client.viewer.presenter.dialog;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;

public class SlotFormContent implements SlotFormData {
    private final LocalTime startTime;
    private final LocalDate date;
    private final Duration duration;
    private final String description;

    public SlotFormContent(LocalTime startTime, LocalDate date, Duration duration, String description) {
        this.startTime = startTime;
        this.date = date;
        this.duration = duration;
        this.description = description;
    }

    @Override
    public LocalTime getStartTime() { return startTime; }

    @Override
    public LocalDate getDate() { return date; }

    @Override
    public Duration getDuration() { return duration; }

    @Override
    public String getDescription() { return description; }
}