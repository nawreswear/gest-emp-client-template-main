package tn.iset.m2glnt.client.util;

import java.time.LocalDate;
import java.util.Iterator;

public class DayIterator implements Iterator<LocalDate> {
    private LocalDate nextDate;
    private final LocalDate endDate;

    public DayIterator(DateInterval dateInterval) {
        nextDate = dateInterval.start();
        endDate = dateInterval.end();
    }

    @Override
    public boolean hasNext() {
        return nextDate.isBefore(endDate);
    }

    @Override
    public LocalDate next() {
        LocalDate nextDate = this.nextDate;
        this.nextDate = nextDate.plusDays(1);
        return nextDate;
    }
}
