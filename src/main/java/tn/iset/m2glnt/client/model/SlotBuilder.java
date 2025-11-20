package tn.iset.m2glnt.client.model;

import java.time.LocalDateTime;
import java.time.Duration;

public class SlotBuilder {
    private String nom;
    private String description;
    private LocalDateTime startDateTime;
    private Duration duration;
    private int id = 0;
    private int version = 1;
    private Long enseignantId;
    private Long salleId;

    public SlotBuilder withNom(String nom) {
        this.nom = nom;
        return this;
    }

    public SlotBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public SlotBuilder withStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
        return this;
    }

    public SlotBuilder withDuration(Duration duration) {
        this.duration = duration;
        return this;
    }

    public SlotBuilder withDurationHours(int hours) {
        this.duration = Duration.ofHours(hours);
        return this;
    }

    public SlotBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public SlotBuilder withVersion(int version) {
        this.version = version;
        return this;
    }

    public SlotBuilder withEnseignantId(Long enseignantId) {
        this.enseignantId = enseignantId;
        return this;
    }

    public SlotBuilder withSalleId(Long salleId) {
        this.salleId = salleId;
        return this;
    }

    public Slot build() {
        if (nom == null) {
            nom = description; // Utilise description comme nom par défaut
        }
        if (description == null) {
            description = nom; // Utilise nom comme description par défaut
        }
        return new SimpleSlot(description, startDateTime, duration, id, version, nom, enseignantId, salleId);
    }
}