package tn.iset.m2glnt.client.model;

import java.time.LocalDate;
import java.util.Collection;

public interface Calendar {

    /**
     * Get all the slots between startDate and endDate
     * @param startDate the start date of the date interval
     * @param endDate the end date of the date interval
     * @return the list of CalendarSlots between startDate and endDate
     */
    Collection<Slot> getAllSlotsBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Add a slot to the Calendar
     * @param slot the new slot to be added
     * @return {@code true} if the slot was added and {@code false} otherwise
     */
    boolean create(Slot slot);

    /**
     * Update a given slot in the calendar
     * @param slot the new version of the slot
     * @return {@code true} if the slot was updated and {@code false} otherwise
     */
    boolean update(Slot slot);

    /**
     * Remove a slot in the calendar
     * @param slot the slot to remove
     * @return {@code true} if the slot was deleted and {@code false} otherwise
     */
    boolean delete(Slot slot);
}
