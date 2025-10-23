package tn.iset.m2glnt.client.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record DateInterval (LocalDate start, LocalDate end) {
    public int getNumberOfDays() {
        return (int) ChronoUnit.DAYS.between(start, end);
    }
}
