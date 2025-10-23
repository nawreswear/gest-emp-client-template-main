package tn.iset.m2glnt.client.util;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DurationGenerator implements Iterable<Duration> {
    private final Duration timeUnit;
    private final Duration maximalDuration;

    public DurationGenerator(Duration timeUnit, Duration maximalDuration) {
        this.timeUnit = timeUnit;
        this.maximalDuration = maximalDuration;
    }

    @Override
    public @NotNull Iterator<Duration> iterator() {
        return new DurationIterator(timeUnit, maximalDuration);
    }

    public List<Duration> getDurations(){
        List<Duration> durations = new ArrayList<>();
        for (Duration duration : this) {
            durations.add(duration);
        }
        return durations;
    }

    public int getDurationIndex(Duration duration){
        return (int) duration.dividedBy(this.timeUnit) - 1;
    }


}
