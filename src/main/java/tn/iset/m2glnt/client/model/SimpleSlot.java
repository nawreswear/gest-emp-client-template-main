package tn.iset.m2glnt.client.model;

import java.time.Duration;
import java.time.LocalDateTime;

public record SimpleSlot(String description, LocalDateTime startDateTime, Duration duration, int id,
                         int versionNumber) implements Slot {

    @Override
    public LocalDateTime getEndDateTime() {
        return startDateTime.plus(duration);
    }

}
