package tn.iset.m2glnt.client.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

public interface Slot {
    String description();
    int id();
    LocalDateTime startDateTime();
    Duration duration();
    LocalDateTime getEndDateTime();
    int versionNumber();

    // Méthodes pour les champs ajoutés
    String nom();
    Long enseignantId();
    Long salleId();

    // Méthodes pour accéder aux dates/heures séparément
    default LocalDate getDate() {
        return startDateTime().toLocalDate();
    }

    default LocalTime getHeureDebut() {
        return startDateTime().toLocalTime();
    }

    default LocalTime getHeureFin() {
        return getEndDateTime().toLocalTime();
    }

    // Méthodes pour les informations d'affichage
    default String getMatiere() {
        return nom();
    }

    default String getSalle() {
        // À adapter selon votre logique de récupération du nom de salle
        return salleId() != null ? "Salle " + salleId() : "Salle non définie";
    }

    default String getEnseignant() {
        // À adapter selon votre logique de récupération du nom d'enseignant
        return enseignantId() != null ? "Enseignant " + enseignantId() : "Enseignant non défini";
    }

    default String getTypeCours() {
        return description();
    }

    default String getGroupes() {
        return "";
    }

    // Méthode utilitaire pour vérifier si le slot a un enseignant
    default boolean hasEnseignant() {
        return enseignantId() != null;
    }

    // Méthode utilitaire pour vérifier si le slot a une salle
    default boolean hasSalle() {
        return salleId() != null;
    }

    // Méthode utilitaire pour obtenir la durée en minutes
    default long getDurationInMinutes() {
        return duration() != null ? duration().toMinutes() : 0;
    }
}