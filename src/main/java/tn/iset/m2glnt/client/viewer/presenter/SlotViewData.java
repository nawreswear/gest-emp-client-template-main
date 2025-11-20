package tn.iset.m2glnt.client.viewer.presenter;

import tn.iset.m2glnt.client.util.TimeInterval;

import java.time.LocalDate;

public class SlotViewData {
    private final int id;
    private final String description;
    private final LocalDate date;
    private final TimeInterval timeInterval;
    private final int column;
    private final int row;

    public SlotViewData(int id, String description, LocalDate date, TimeInterval timeInterval, int column, int row) {
        this.id = id;
        this.description = description;
        this.date = date;
        this.timeInterval = timeInterval;
        this.column = column;
        this.row = row;
    }

    // Getters
    public int id() { return id; }
    public String description() { return description; }
    public LocalDate getDate() { return date; }
    public TimeInterval getTimeInterval() { return timeInterval; }
    public int getColumn() { return column; }
    public int getRow() { return row; }

    // Méthode utilitaire pour debug
    public String getGridPosition() {
        return "Colonne: " + column + ", Ligne: " + row;
    }

    @Override
    public String toString() {
        return "SlotViewData{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", timeInterval=" + timeInterval +
                ", column=" + column +
                ", row=" + row +
                '}';
    }
}