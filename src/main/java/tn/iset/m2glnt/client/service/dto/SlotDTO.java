package tn.iset.m2glnt.client.service.dto;

import java.time.LocalDateTime;

public record SlotDTO(
        int id,
        String nom,           // CORRECTION: Ajout du champ nom
        String description,
        LocalDateTime timeBegin,  // CORRECTION: Champs séparés au lieu de DateTimeInterval
        LocalDateTime timeEnd,
        int version,
        Long enseignantId,    // CORRECTION: Ajout des relations
        Long salleId
) { public SlotDTO(int id) {
    this(id, null, null, null, null, 0, null, null);
}

    // Constructeur pratique sans ID (pour la création)
    public SlotDTO(String nom, String description, LocalDateTime timeBegin,
                   LocalDateTime timeEnd, int version, Long enseignantId, Long salleId) {
        this(0, nom, description, timeBegin, timeEnd, version, enseignantId, salleId);
    }

    // Constructeur pratique avec DateTimeInterval (si vous voulez garder la compatibilité)
    public SlotDTO(int id, String description, DateTimeInterval timeInterval, int version) {
        this(id, description, description, // Utilise description comme nom
                timeInterval.start(), timeInterval.end(), version, null, null);
    }
}