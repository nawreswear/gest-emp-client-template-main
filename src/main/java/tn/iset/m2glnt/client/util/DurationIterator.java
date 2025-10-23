package tn.iset.m2glnt.client.util;

import java.time.Duration;
import java.util.Iterator;

public class DurationIterator implements Iterator<Duration> {
    private Duration nextDuration;
    private final Duration timeUnit;
    private final Duration maximalDuration;

    public DurationIterator(Duration timeUnit, Duration maximalDuration) {
        this.timeUnit = timeUnit;
        this.maximalDuration = maximalDuration;
        nextDuration = timeUnit;
    }

    @Override
    public boolean hasNext() {
        return nextDuration.compareTo(maximalDuration) <= 0;
    }

    @Override
    public Duration next() {
        Duration result = nextDuration;
        nextDuration = nextDuration.plus(timeUnit);
        return result;
    }
}
