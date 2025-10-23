package tn.iset.m2glnt.client.model;

import java.time.Duration;

import tn.iset.m2glnt.client.service.dto.DateTimeInterval;
import tn.iset.m2glnt.client.service.dto.SlotDTO;

public class SimpleSlotDTOConverter implements SlotDTOConverter {
    @Override
    public Slot fromDTO(SlotDTO slotDTO) {
        Duration duration = Duration.between(slotDTO.timeInterval().start(), slotDTO.timeInterval().end());
        return new SimpleSlot(slotDTO.description(), slotDTO.timeInterval().start(), duration,
                slotDTO.id(), slotDTO.version());
    }

    @Override
    public SlotDTO toDTO(Slot slot) {
        DateTimeInterval dateTimeInterval = new DateTimeInterval(slot.startDateTime(), slot.getEndDateTime());
        return new SlotDTO(slot.id(), slot.description(), dateTimeInterval, slot.versionNumber());
    }
}
