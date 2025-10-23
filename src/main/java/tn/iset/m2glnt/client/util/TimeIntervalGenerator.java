package tn.iset.m2glnt.client.util;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TimeIntervalGenerator implements Iterable<TimeInterval> {
    private final LocalTime startTime;
    private final Duration intervalDuration;
    private final LocalTime endTime;

    public TimeIntervalGenerator(LocalTime startTime, LocalTime endTime, Duration intervalDuration) {
        this.startTime = startTime;
        this.intervalDuration = intervalDuration;
        this.endTime = endTime;
    }

    public Duration getIntervalDuration() {
        return intervalDuration;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    List<TimeInterval> getTimeIntervals() {
        List<TimeInterval> timeIntervals = new ArrayList<>();
        for (TimeInterval timeInterval : this) {
            timeIntervals.add(timeInterval);
        }
        return timeIntervals;
    }

    public int getNumberOfIntervals(){
        return numberOfIntervalsBetween(startTime, endTime);
    }

    private int numberOfIntervalsBetween(LocalTime startTime, LocalTime endTime) {
        Duration duration = Duration.between(startTime, endTime);
        return (int) duration.dividedBy(intervalDuration);
    }

    public int getTimeIndex(LocalTime time){
        if(time.isBefore(startTime) || !time.isBefore(endTime)){
            throw new IllegalArgumentException("The given time is outside the given interval");
        }
        return numberOfIntervalsBetween(startTime, time);
    }

    public List<LocalTime> getStartTimesOfIntervals(){
        List<LocalTime> times = new ArrayList<>();
        for (TimeInterval timeInterval : this) {
            times.add(timeInterval.start());
        }
        return times;
    }

    @Override
    public @NotNull Iterator<TimeInterval> iterator() {
        return new TimeIntervalIterator(startTime, intervalDuration, endTime);
    }
}
