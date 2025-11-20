package tn.iset.m2glnt.client.service.dao;

import tn.iset.m2glnt.client.service.dao.exceptions.UnknownElementException;
import tn.iset.m2glnt.client.service.dao.exceptions.WrongVersionException;
import tn.iset.m2glnt.client.service.dto.SlotDTO;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

public interface CalendarServiceDAO {

    Optional<SlotDTO> get(int key) throws UnknownElementException;

    Collection<SlotDTO> getAllSlotsIn(LocalDate startDate, LocalDate endDate);

    int create(SlotDTO element);

    int update(SlotDTO element) throws UnknownElementException, WrongVersionException;

    void delete(SlotDTO element) throws UnknownElementException;

    // Méthode utilitaire pour compatibilité
    default Collection<SlotDTO> getAllSlots() {
        return getAllSlotsIn(null, null);
    }
}