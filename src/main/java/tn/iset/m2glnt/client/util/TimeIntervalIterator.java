package tn.iset.m2glnt.client.util;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Iterator;

public class TimeIntervalIterator implements Iterator<TimeInterval> {

    private LocalTime currentTime;
    private final Duration slotDuration;
    private final LocalTime endTime;

    public TimeIntervalIterator(LocalTime currentTime, Duration slotDuration, LocalTime endTime) {
        this.currentTime = currentTime;
        this.slotDuration = slotDuration;
        this.endTime = endTime;
    }

    @Override
    public boolean hasNext() {
        return currentTime.isBefore(endTime);
    }

    @Override
    public TimeInterval next() {
        LocalTime start = currentTime;
        currentTime = currentTime.plus(slotDuration);
        return new TimeInterval(start, currentTime);
    }
}
