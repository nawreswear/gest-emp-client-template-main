package tn.iset.m2glnt.client.service;

import tn.iset.m2glnt.client.model.Calendar;
import tn.iset.m2glnt.client.model.Slot;
import tn.iset.m2glnt.client.service.dao.CalendarServiceDAO;
import tn.iset.m2glnt.client.service.dto.SlotDTO;
import tn.iset.m2glnt.client.service.dao.exceptions.UnknownElementException;
import tn.iset.m2glnt.client.service.dao.exceptions.WrongVersionException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptateur qui fait le pont entre l'interface Calendar et CalendarServiceDAO
 */
public class CalendarServiceDAOAdapter implements Calendar {

    private final CalendarServiceDAO calendarServiceDAO;

    public CalendarServiceDAOAdapter(CalendarServiceDAO calendarServiceDAO) {
        this.calendarServiceDAO = calendarServiceDAO;
        System.out.println("✅ CalendarServiceDAOAdapter créé avec: " + calendarServiceDAO.getClass().getSimpleName());
    }

    @Override
    public Collection<Slot> getAllSlotsBetween(LocalDate startDate, LocalDate endDate) {
        System.out.println("🔄 Adaptateur: getAllSlotsBetween(" + startDate + ", " + endDate + ")");

        Collection<SlotDTO> slotDTOs = calendarServiceDAO.getAllSlotsIn(startDate, endDate);

        // Convertir les DTOs en Slots
        return slotDTOs.stream()
                .map(this::convertFromDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean create(Slot slot) {
        System.out.println("🔄 Adaptateur: create(Slot #" + slot.id() + ")");

        try {
            SlotDTO slotDTO = convertToDTO(slot);
            int newId = calendarServiceDAO.create(slotDTO);

            if (newId > 0) {
                System.out.println("✅ Adaptateur: Slot créé avec ID: " + newId);
                return true;
            } else {
                System.err.println("❌ Adaptateur: Échec de la création du slot");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Adaptateur: Erreur lors de la création: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Slot slot) {
        System.out.println("🔄 Adaptateur: update(Slot #" + slot.id() + ")");

        try {
            SlotDTO slotDTO = convertToDTO(slot);
            int newVersion = calendarServiceDAO.update(slotDTO);

            if (newVersion > 0) {
                System.out.println("✅ Adaptateur: Slot #" + slot.id() + " mis à jour (version: " + newVersion + ")");
                return true;
            } else {
                System.err.println("❌ Adaptateur: Échec de la mise à jour du slot #" + slot.id());
                return false;
            }
        } catch (UnknownElementException e) {
            System.err.println("❌ Adaptateur: Slot #" + slot.id() + " non trouvé");
            return false;
        } catch (WrongVersionException e) {
            System.err.println("❌ Adaptateur: Conflit de version pour le slot #" + slot.id());
            return false;
        } catch (Exception e) {
            System.err.println("❌ Adaptateur: Erreur lors de la mise à jour: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Slot slot) {
        System.out.println("🔄 Adaptateur: delete(Slot #" + slot.id() + ")");

        try {
            SlotDTO slotDTO = convertToDTO(slot);
            calendarServiceDAO.delete(slotDTO);

            System.out.println("✅ Adaptateur: Slot #" + slot.id() + " supprimé avec succès");
            return true;

        } catch (UnknownElementException e) {
            System.err.println("❌ Adaptateur: Slot #" + slot.id() + " non trouvé pour suppression");
            return false;
        } catch (Exception e) {
            System.err.println("❌ Adaptateur: Erreur lors de la suppression: " + e.getMessage());
            return false;
        }
    }

    public Optional<Slot> getSlot(int slotId) {
        System.out.println("🔄 Adaptateur: getSlot(" + slotId + ")");

        try {
            Optional<SlotDTO> slotDTO = calendarServiceDAO.get(slotId);
            return slotDTO.map(this::convertFromDTO);
        } catch (UnknownElementException e) {
            System.err.println("❌ Adaptateur: Slot #" + slotId + " non trouvé");
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("❌ Adaptateur: Erreur lors de la récupération: " + e.getMessage());
            return Optional.empty();
        }
    }

    // Méthodes de conversion
    private SlotDTO convertToDTO(Slot slot) {
        return new SlotDTO(
                slot.id(),
                slot.getMatiere(),
                slot.getMatiere(),
                slot.startDateTime(),
                slot.startDateTime().plus(slot.duration()),
                slot.versionNumber(),
                slot.enseignantId(),
                slot.salleId()
        );
    }

    private Slot convertFromDTO(SlotDTO dto) {
        return new tn.iset.m2glnt.client.model.SimpleSlot(
                dto.id(),
                dto.nom(),
                dto.description(),
                dto.timeBegin(),
                java.time.Duration.between(dto.timeBegin(), dto.timeEnd()),
                dto.version(),
                dto.enseignantId(),
                dto.salleId()
        );
    }
}