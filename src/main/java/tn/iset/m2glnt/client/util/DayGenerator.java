package tn.iset.m2glnt.client.util;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;

public class DayGenerator implements Iterable<LocalDate> {
    private final DateInterval interval;

    public DayGenerator(DateInterval interval) {
        this.interval = interval;
    }

    @Override
    @NotNull
    public Iterator<LocalDate> iterator() {
        return new DayIterator(interval);
    }

    public int getNumberOfDays() {
        return interval.getNumberOfDays();
    }

    public LocalDate getStartDate() {
        return interval.start();
    }

    public LocalDate getEndDate() {
        return interval.end();
    }

    public int getDayIndex(LocalDate date) {
        if (date.isBefore(interval.start()) || !date.isBefore(interval.end())) {
            return -1;
        }
        return (int) ChronoUnit.DAYS.between(interval.start(), date);
    }

    @Override
    public String toString() {
        return "DayGenerator{" +
                "interval=" + interval +
                '}';
    }
}
