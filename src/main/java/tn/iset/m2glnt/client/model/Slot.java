package tn.iset.m2glnt.client.model;

import java.time.Duration;
import java.time.LocalDateTime;

public interface Slot {
    String description() ;
    int id();
    LocalDateTime startDateTime();
    Duration duration();
    LocalDateTime getEndDateTime();
    int versionNumber();
}
