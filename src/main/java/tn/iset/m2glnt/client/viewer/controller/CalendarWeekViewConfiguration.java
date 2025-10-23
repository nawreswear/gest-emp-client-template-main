package tn.iset.m2glnt.client.viewer.controller;

import javafx.scene.paint.Color;
import tn.iset.m2glnt.client.util.DurationGenerator;
import tn.iset.m2glnt.client.util.TimeIntervalGenerator;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;

public class CalendarWeekViewConfiguration implements CalendarViewConfiguration {
    private static final int DAYS_IN_WEEK = 7;
    private static final int PRINTABLE_DAYS_IN_WEEK = 7;
    private final Period totalPeriod = Period.ofDays(DAYS_IN_WEEK);
    private final Period printablePeriod = Period.ofDays(PRINTABLE_DAYS_IN_WEEK);
    private final TimeIntervalGenerator timeIntervalGenerator;
    private final DurationGenerator possibleDurations;
    private final Duration defaultDuration;
    private final Color colorOfSlots;

    public CalendarWeekViewConfiguration(LocalTime dayStartTime, LocalTime dayEndTime,
                                         Duration timeUnit, Duration maximalDuration, Duration defaultDuration, Color colorOfSlots) {
        this.defaultDuration = defaultDuration;
        this.colorOfSlots = colorOfSlots;
        this.timeIntervalGenerator = new TimeIntervalGenerator(dayStartTime, dayEndTime, timeUnit);
        this.possibleDurations = new DurationGenerator(timeUnit, maximalDuration);
    }

    @Override
    public Period getTotalPeriod() {
        return totalPeriod;
    }

    @Override
    public Period getPrintablePeriod() {
        return printablePeriod;
    }

    @Override
    public TimeIntervalGenerator getTimeIntervalGenerator() {
        return timeIntervalGenerator;
    }

    @Override
    public DurationGenerator getPossibleDurations() {
        return possibleDurations;
    }

    @Override
    public LocalDate getPeriodStartDateContaining(LocalDate date) {
        int indexInWeek = date.getDayOfWeek().getValue()-1;
        return date.minus(Period.ofDays(indexInWeek));
    }

    @Override
    public int getDefaultDurationIndex(){
        return getPossibleDurations().getDurationIndex(defaultDuration);
    }

    @Override
    public Color colorOfSlots(){
        return colorOfSlots;
    }


}
