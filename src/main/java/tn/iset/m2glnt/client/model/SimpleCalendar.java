package tn.iset.m2glnt.client.model;

import tn.iset.m2glnt.client.service.dao.CalendarServiceDAO;
import tn.iset.m2glnt.client.service.dao.exceptions.UnknownElementException;
import tn.iset.m2glnt.client.service.dao.exceptions.WrongVersionException;
import tn.iset.m2glnt.client.service.dto.SlotDTO;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleCalendar implements Calendar {
    private final CalendarServiceDAO calendarDAO;
    private final SlotDTOConverter slotDTOConverter = new SimpleSlotDTOConverter();

    public SimpleCalendar(CalendarServiceDAO calendarDAO) {
        this.calendarDAO = calendarDAO;
    }

    @Override
    public Collection<Slot> getAllSlotsBetween(LocalDate startDate, LocalDate endDate) {
        try {
            Collection<SlotDTO> slots = calendarDAO.getAllSlotsIn(startDate, endDate);
            return slots.stream()
                    .map(slotDTOConverter::fromDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération des slots: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public boolean create(Slot slot) {
        try {
            SlotDTO slotDTO = slotDTOConverter.toDTO(slot);
            int newId = calendarDAO.create(slotDTO);
            return newId > 0;
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la création du slot: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Slot slot) {
        try {
            SlotDTO slotDTO = slotDTOConverter.toDTO(slot);
            int newVersion = calendarDAO.update(slotDTO);
            return newVersion > 0;
        } catch (UnknownElementException e) {
            System.err.println("❌ Slot non trouvé lors de la mise à jour: " + e.getMessage());
            return false;
        } catch (WrongVersionException e) {
            System.err.println("❌ Conflit de version lors de la mise à jour: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la mise à jour du slot: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Slot slot) {
        try {
            SlotDTO slotDTO = slotDTOConverter.toDTO(slot);
            calendarDAO.delete(slotDTO);
            return true;
        } catch (UnknownElementException e) {
            System.err.println("❌ Slot non trouvé lors de la suppression: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la suppression du slot: " + e.getMessage());
            return false;
        }
    }

    // ✅ CORRECTION : Méthode utilisant le Builder pattern
    // Dans SimpleCalendar.java - CORRIGER la méthode createNewSlot
    public Slot createNewSlot(String nom, String description, LocalDate date, LocalTime startTime,
                              Duration duration, Long enseignantId, Long salleId) {
        // ✅ CORRECTION : Combiner date et heure pour créer LocalDateTime
        LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime endDateTime = startDateTime.plus(duration);

        return SimpleSlot.builder()
                .description(description)
                .nom(nom)
                .startDateTime(startDateTime) // ✅ LocalDateTime complet
                .duration(duration)
                .versionNumber(0)
                .enseignantId(enseignantId)
                .salleId(salleId)
                .build();
    }

    // ✅ CORRECTION : Méthode avec IDs enseignant et salle
    public Slot createNewSlot(String nom, String description, LocalDateTime start, LocalDateTime end,
                              Long enseignantId, Long salleId) {
        Duration duration = Duration.between(start, end);
        return SimpleSlot.builder()
                .description(description)
                .nom(nom)
                .startDateTime(start)
                .duration(duration)
                .versionNumber(0)
                .enseignantId(enseignantId)
                .salleId(salleId)
                .build();
    }

    public Slot getSlotById(int id) {
        try {
            var slotDTOOpt = calendarDAO.get(id);
            return slotDTOOpt.map(slotDTOConverter::fromDTO).orElse(null);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération du slot " + id + ": " + e.getMessage());
            return null;
        }
    }
}