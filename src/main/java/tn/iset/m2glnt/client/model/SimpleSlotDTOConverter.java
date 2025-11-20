package tn.iset.m2glnt.client.model;

import tn.iset.m2glnt.client.service.dto.SlotDTO;

import java.time.Duration;
import java.time.LocalDateTime;

public class SimpleSlotDTOConverter implements SlotDTOConverter {

    @Override
    public Slot fromDTO(SlotDTO slotDTO) {
        System.out.println("🕐 DEBUG Conversion DTO -> Model (FROM DATABASE):");
        System.out.println("   - DB timeBegin: " + slotDTO.timeBegin());
        System.out.println("   - DB timeEnd: " + slotDTO.timeEnd());

        // CORRECTION : Le backend AJOUTE 1h aux DEUX heures
        // Donc on SOUSTRAIT 1h aux DEUX heures
        LocalDateTime adjustedTimeBegin = slotDTO.timeBegin().minusHours(1);
        LocalDateTime adjustedTimeEnd = slotDTO.timeEnd().minusHours(1);

        System.out.println("   - Frontend timeBegin: " + adjustedTimeBegin);
        System.out.println("   - Frontend timeEnd: " + adjustedTimeEnd);

        Duration duration = Duration.between(adjustedTimeBegin, adjustedTimeEnd);

        return new SimpleSlot(
                slotDTO.id(),
                slotDTO.description(),
                slotDTO.nom(),
                adjustedTimeBegin,
                duration,
                slotDTO.version(),
                slotDTO.enseignantId(),
                slotDTO.salleId()
        );
    }

    @Override
    public SlotDTO toDTO(Slot slot) {
        LocalDateTime timeBegin = slot.startDateTime();
        LocalDateTime timeEnd = timeBegin.plus(slot.duration());

        System.out.println("🕐 DEBUG Conversion Model -> DTO (TO DATABASE):");
        System.out.println("   - Frontend timeBegin: " + timeBegin);
        System.out.println("   - Frontend timeEnd: " + timeEnd);

        // CORRECTION : Le backend SOUSTRAIT 1h aux DEUX heures
        // Donc on AJOUTE 1h aux DEUX heures
        LocalDateTime dbTimeBegin = timeBegin.plusHours(1);
        LocalDateTime dbTimeEnd = timeEnd.plusHours(1);

        System.out.println("   - DB timeBegin: " + dbTimeBegin);
        System.out.println("   - DB timeEnd: " + dbTimeEnd);

        // VÉRIFICATION : S'assurer que timeBegin < timeEnd
        if (dbTimeBegin.isAfter(dbTimeEnd)) {
            System.err.println("❌ ERREUR: timeBegin après timeEnd! Inversion des heures.");
            // Corriger l'inversion
            LocalDateTime temp = dbTimeBegin;
            dbTimeBegin = dbTimeEnd;
            dbTimeEnd = temp;
        }

        return new SlotDTO(
                slot.id(),
                slot.nom(),
                slot.description(),
                dbTimeBegin,
                dbTimeEnd,
                slot.versionNumber(),
                slot.enseignantId(),
                slot.salleId()
        );
    }
}