package tn.iset.m2glnt.client.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CalendarDTO(List<SlotDTO> calendarSlots) {
}
