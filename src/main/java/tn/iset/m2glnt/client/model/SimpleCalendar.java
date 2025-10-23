package tn.iset.m2glnt.client.model;

import tn.iset.m2glnt.client.service.dao.CalendarServiceDAO;
import tn.iset.m2glnt.client.service.dao.exceptions.ConnexionException;
import tn.iset.m2glnt.client.service.dao.exceptions.UnknownElementException;
import tn.iset.m2glnt.client.service.dao.exceptions.WrongVersionException;
import tn.iset.m2glnt.client.service.dto.SlotDTO;

import java.time.LocalDate;
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
            return slots.stream().map(slotDTOConverter::fromDTO).collect(Collectors.toList());
        }
        catch (ConnexionException e){
            return List.of();
        }
    }

    @Override
    public boolean create(Slot slot) {
        SlotDTO slotDTO = slotDTOConverter.toDTO(slot);
        try {
            calendarDAO.create(slotDTO);
            return true;
        }
        catch (ConnexionException e){
            return false;
        }
    }

    @Override
    public boolean update(Slot slot) {
        SlotDTO slotDTO = slotDTOConverter.toDTO(slot);
        try {
            calendarDAO.update(slotDTO);
            return true;
        }
        catch (UnknownElementException | WrongVersionException | ConnexionException e) {
            return false;
        }
    }

    @Override
    public boolean delete(Slot slot){
        SlotDTO slotDTO = slotDTOConverter.toDTO(slot);
        try {
            calendarDAO.delete(slotDTO);
            return true;
        }
        catch (UnknownElementException | WrongVersionException | ConnexionException e) {
            return false;
        }
    }
}
