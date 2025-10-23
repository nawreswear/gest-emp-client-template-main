package tn.iset.m2glnt.client.service.dao;

import tn.iset.m2glnt.client.service.dao.exceptions.ConnexionException;
import tn.iset.m2glnt.client.service.dao.exceptions.UnknownElementException;
import tn.iset.m2glnt.client.service.dao.exceptions.WrongVersionException;
import tn.iset.m2glnt.client.service.dto.SlotDTO;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

/**
 * The Calendar service interface
 */
public interface CalendarServiceDAO {
    /**
     * Get a given element
     * @param key the indexation key
     * @return the element if found
     */
    Optional<SlotDTO> get(int key) throws UnknownElementException;

    /**
     * Get all the the CalendarSlotDTO between startDate and endDate
     * @param startDate the start date of the dates interval
     * @param endDate the end date of the dates interval
     * @throws ConnexionException when the service cannot connect to the server
     * @return the list of elements
     */
    Collection<SlotDTO> getAllSlotsIn(LocalDate startDate, LocalDate endDate) throws ConnexionException;

    /**
     * Create a new entry with a given element
     * @param element the new element
     * @return the identifier of the new element
     * @throws ConnexionException when the service cannot connect to the server
     */
    int create(SlotDTO element) throws ConnexionException;

    /**
     * Update a given element
     * @param element the new version of the element
     * @return the new version number
     * @throws UnknownElementException when the element is not found
     * @throws WrongVersionException if the element has been modified by another transaction
     * @throws ConnexionException when the service cannot connect to the server
     */
    int update(SlotDTO element) throws ConnexionException, UnknownElementException, WrongVersionException;

    /**
     * Remove an element
     * @param element the element to remove
     * @throws UnknownElementException when the element is not found
     * @throws WrongVersionException if the element has been modified by another transaction
     * @throws ConnexionException when the service cannot connect to the server
     */
    void delete(SlotDTO element) throws ConnexionException, UnknownElementException, WrongVersionException;
}
