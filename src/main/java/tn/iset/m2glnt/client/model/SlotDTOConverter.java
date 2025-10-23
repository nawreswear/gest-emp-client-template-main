package tn.iset.m2glnt.client.model;


import tn.iset.m2glnt.client.service.dto.SlotDTO;

public interface SlotDTOConverter {
    Slot fromDTO(SlotDTO slotDTO);
    SlotDTO toDTO(Slot slot);
}
