package tn.iset.m2glnt.client.viewer.presenter.dialog;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ExtendedSlotFormContent {
    private final LocalDateTime timeBegin;
    private final LocalDateTime timeEnd;
    private final Duration duration;
    private final String matiere;
    private final Long enseignantId;
    private final Long salleId;
    //private final String typeCours;

    // ✅ CONSTRUCTEUR PRINCIPAL avec LocalDateTime
    public ExtendedSlotFormContent(LocalDateTime timeBegin, LocalDateTime timeEnd, Duration duration,
                                   String matiere, Long enseignantId, Long salleId) {
        this.timeBegin = timeBegin;
        this.timeEnd = timeEnd;
        this.duration = duration;
        this.matiere = matiere;
        this.enseignantId = enseignantId;
        this.salleId = salleId;
       // this.typeCours = typeCours; , String typeCours
    }

    // ✅ CONSTRUCTEUR ALTERNATIF pour compatibilité (si nécessaire)
    public ExtendedSlotFormContent(LocalTime startTime, LocalDate date, Duration duration,
                                   String matiere, Long enseignantId, Long salleId) {
        this.timeBegin = LocalDateTime.of(date, startTime);
        this.timeEnd = this.timeBegin.plus(duration);
        this.duration = duration;
        this.matiere = matiere;
        this.enseignantId = enseignantId;
        this.salleId = salleId;
        //this.typeCours = typeCours; , String typeCours
    }

    // ✅ GETTERS pour LocalDateTime (utilisés par le backend)
    public LocalDateTime getTimeBegin() {
        return timeBegin;
    }

    public LocalDateTime getTimeEnd() {
        return timeEnd;
    }

    public Duration getDuration() {
        return duration;
    }

    public String getMatiere() {
        return matiere;
    }

    public Long getEnseignantId() {
        return enseignantId;
    }

    public Long getSalleId() {
        return salleId;
    }



    // ✅ GETTERS de compatibilité (pour l'interface existante)
    public LocalTime getStartTime() {
        return timeBegin != null ? timeBegin.toLocalTime() : null;
    }

    public LocalDate getDate() {
        return timeBegin != null ? timeBegin.toLocalDate() : null;
    }

    public LocalTime getHeureDebut() {
        return getStartTime();
    }

    public LocalTime getHeureFin() {
        return timeEnd != null ? timeEnd.toLocalTime() : null;
    }

    // ✅ MÉTHODE UTILITAIRE pour créer depuis LocalDate + LocalTime + Duration
    public static ExtendedSlotFormContent fromDateTimeComponents(LocalDate date, LocalTime startTime,
                                                                 Duration duration, String matiere,
                                                                 Long enseignantId, Long salleId) {
        LocalDateTime timeBegin = LocalDateTime.of(date, startTime);
        LocalDateTime timeEnd = timeBegin.plus(duration);
        return new ExtendedSlotFormContent(timeBegin, timeEnd, duration, matiere, enseignantId, salleId);
    }

    // ✅ MÉTHODE UTILITAIRE pour le débogage
    @Override
    public String toString() {
        return "ExtendedSlotFormContent{" +
                "timeBegin=" + timeBegin +
                ", timeEnd=" + timeEnd +
                ", duration=" + duration +
                ", matiere='" + matiere + '\'' +
                ", enseignantId=" + enseignantId +
                ", salleId=" + salleId +
              ///  ", typeCours='" + typeCours + '\'' +
                '}';
    }

    // ✅ MÉTHODE pour vérifier la cohérence des données
    public boolean isValid() {
        return timeBegin != null &&
                timeEnd != null &&
                duration != null &&
                matiere != null &&
                !matiere.trim().isEmpty() &&
               // typeCours != null &&
               // !typeCours.trim().isEmpty() &&
                timeBegin.isBefore(timeEnd);
    }

    // ✅ MÉTHODE pour obtenir la durée formatée
    public String getFormattedDuration() {
        if (duration == null) return "N/A";
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        return String.format("%dh%02d", hours, minutes);
    }

    // ✅ MÉTHODE pour obtenir l'intervalle formaté
    public String getFormattedTimeRange() {
        if (timeBegin == null || timeEnd == null) return "N/A";
        return timeBegin.toLocalTime() + " - " + timeEnd.toLocalTime();
    }
}