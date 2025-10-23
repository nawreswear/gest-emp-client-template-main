package tn.iset.m2glnt.client.viewer.controller;

import javafx.scene.paint.Color;
import tn.iset.m2glnt.client.util.DurationGenerator;
import tn.iset.m2glnt.client.util.TimeIntervalGenerator;

import java.time.LocalDate;
import java.time.Period;

public interface CalendarViewConfiguration {
    Period getTotalPeriod();

    Period getPrintablePeriod();

    TimeIntervalGenerator getTimeIntervalGenerator();

    DurationGenerator getPossibleDurations();

    LocalDate getPeriodStartDateContaining(LocalDate date);

    int getDefaultDurationIndex();

    Color colorOfSlots();
}
