package tn.iset.m2glnt.client.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

public class SimpleSlot implements Slot {
    private final int id;
    private final String description;
    private final String nom;
    private final LocalDateTime startDateTime;
    private final Duration duration;
    private final int versionNumber;
    private final Long enseignantId;
    private final Long salleId;

    public SimpleSlot(int id, String description, String nom, LocalDateTime startDateTime,
                      Duration duration, int versionNumber, Long enseignantId, Long salleId) {
        this.id = id;
        this.description = description;
        this.nom = nom;
        this.startDateTime = startDateTime;
        this.duration = duration;
        this.versionNumber = versionNumber;
        this.enseignantId = enseignantId;
        this.salleId = salleId;
    }

    // Constructeur alternatif pour faciliter la création
    // Dans SimpleSlot.java - AJOUTER CE CONSTRUCTEUR
    public SimpleSlot(String description, LocalDateTime startDateTime, Duration duration,
                      int id, int versionNumber, String nom, Long enseignantId, Long salleId) {
        this(id, description, nom, startDateTime, duration, versionNumber, enseignantId, salleId);
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public LocalDateTime startDateTime() {
        return startDateTime;
    }

    @Override
    public Duration duration() {
        return duration;
    }

    @Override
    public LocalDateTime getEndDateTime() {
        // 🔥 CORRECTION : Calculer l'heure de fin à partir de l'heure de début + durée
        return startDateTime.plus(duration);
    }
    @Override
    public int versionNumber() {
        return versionNumber;
    }

    @Override
    public String nom() {
        return nom;
    }

    @Override
    public Long enseignantId() {
        return enseignantId;
    }

    @Override
    public Long salleId() {
        return salleId;
    }

    // Méthodes par défaut de l'interface Slot
    @Override
    public LocalDate getDate() {
        return startDateTime.toLocalDate();
    }

    @Override
    public LocalTime getHeureDebut() {
        return startDateTime.toLocalTime();
    }

    @Override
    public LocalTime getHeureFin() {
        return getEndDateTime().toLocalTime();
    }

    @Override
    public String getMatiere() {
        return nom;
    }

    @Override
    public String getSalle() {
        return salleId != null ? "Salle " + salleId : "Salle non définie";
    }

    @Override
    public String getEnseignant() {
        return enseignantId != null ? "Enseignant " + enseignantId : "Enseignant non défini";
    }

    @Override
    public String getTypeCours() {
        return description;
    }

    @Override
    public String getGroupes() {
        return "";
    }

    @Override
    public boolean hasEnseignant() {
        return enseignantId != null;
    }

    @Override
    public boolean hasSalle() {
        return salleId != null;
    }

    @Override
    public long getDurationInMinutes() {
        return duration != null ? duration.toMinutes() : 0;
    }

    // Méthodes utilitaires
    public boolean isSameDay(LocalDate date) {
        return getDate().equals(date);
    }

    public boolean overlapsWith(Slot other) {
        return this.startDateTime.isBefore(other.getEndDateTime()) &&
                other.startDateTime().isBefore(this.getEndDateTime());
    }

    public boolean isInTimeRange(LocalTime start, LocalTime end) {
        LocalTime slotStart = getHeureDebut();
        LocalTime slotEnd = getHeureFin();
        return !slotStart.isBefore(start) && !slotEnd.isAfter(end);
    }

    // Méthodes equals et hashCode pour la comparaison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleSlot that = (SimpleSlot) o;

        if (id != that.id) return false;
        if (versionNumber != that.versionNumber) return false;
        if (!description.equals(that.description)) return false;
        if (!nom.equals(that.nom)) return false;
        if (!startDateTime.equals(that.startDateTime)) return false;
        if (!duration.equals(that.duration)) return false;
        if (enseignantId != null ? !enseignantId.equals(that.enseignantId) : that.enseignantId != null) return false;
        return salleId != null ? salleId.equals(that.salleId) : that.salleId == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + description.hashCode();
        result = 31 * result + nom.hashCode();
        result = 31 * result + startDateTime.hashCode();
        result = 31 * result + duration.hashCode();
        result = 31 * result + versionNumber;
        result = 31 * result + (enseignantId != null ? enseignantId.hashCode() : 0);
        result = 31 * result + (salleId != null ? salleId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SimpleSlot{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", nom='" + nom + '\'' +
                ", startDateTime=" + startDateTime +
                ", duration=" + duration +
                ", versionNumber=" + versionNumber +
                ", enseignantId=" + enseignantId +
                ", salleId=" + salleId +
                ", date=" + getDate() +
                ", heureDebut=" + getHeureDebut() +
                ", heureFin=" + getHeureFin() +
                '}';
    }

    // Builder pattern pour faciliter la création
    public static class Builder {
        private int id;
        private String description;
        private String nom;
        private LocalDateTime startDateTime;
        private Duration duration;
        private int versionNumber;
        private Long enseignantId;
        private Long salleId;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder nom(String nom) {
            this.nom = nom;
            return this;
        }

        public Builder startDateTime(LocalDateTime startDateTime) {
            this.startDateTime = startDateTime;
            return this;
        }

        public Builder duration(Duration duration) {
            this.duration = duration;
            return this;
        }

        public Builder versionNumber(int versionNumber) {
            this.versionNumber = versionNumber;
            return this;
        }

        public Builder enseignantId(Long enseignantId) {
            this.enseignantId = enseignantId;
            return this;
        }

        public Builder salleId(Long salleId) {
            this.salleId = salleId;
            return this;
        }

        public SimpleSlot build() {
            return new SimpleSlot(id, description, nom, startDateTime, duration, versionNumber, enseignantId, salleId);
        }
    }

    // Méthode statique pour créer un builder
    public static Builder builder() {
        return new Builder();
    }
}