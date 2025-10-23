package tn.iset.m2glnt.client.service.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import tn.iset.m2glnt.client.service.dao.exceptions.UnknownElementException;
import tn.iset.m2glnt.client.service.dao.exceptions.WrongVersionException;
import tn.iset.m2glnt.client.service.dao.json.JsonBodyHandler;
import tn.iset.m2glnt.client.service.dto.CalendarDTO;
import tn.iset.m2glnt.client.service.dto.SlotDTO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class SimpleCalendarServiceDAO implements CalendarServiceDAO {
    private static final String URL_SERVER = "http://localhost:7070/timeslots";
    private static final Logger logger = LogManager.getLogger(SimpleCalendarServiceDAO.class);
    private final HttpClient client;
    private final ObjectMapper mapper;

    public SimpleCalendarServiceDAO() {
        client = HttpClient.newHttpClient();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    // 🔹 Récupération des slots entre deux dates
    private List<SlotDTO> getCalendarSlotBetween(LocalDate startDate, LocalDate endDate) {
        try {
            URI uri = new URI(URL_SERVER + "?start=" + startDate + "&end=" + endDate);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();

            HttpResponse<List<SlotDTO>> response = client.send(
                    request,
                    new JsonBodyHandler<>(new TypeReference<List<SlotDTO>>() {})
            );

            if (response.statusCode() == 200) {
                return response.body();
            }
        } catch (IOException | InterruptedException | URISyntaxException e) {
            logger.error("Exception thrown while getting slots between dates", e);
        }
        return List.of();
    }

    // 🔹 Récupération d’un slot par ID
    @Override
    public Optional<SlotDTO> get(int key) throws UnknownElementException {
        logger.info("Getting calendar slot by key {}", key);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(URL_SERVER + "/slots/" + key))
                    .GET()
                    .build();

            HttpResponse<SlotDTO> response = client.send(
                    request,
                    new JsonBodyHandler<>(new TypeReference<SlotDTO>() {})
            );

            if (response.statusCode() == 200) {
                SlotDTO slot = response.body();
                if (slot == null) throw new UnknownElementException("Slot not found", key);
                return Optional.of(slot);
            } else if (response.statusCode() == 404) {
                throw new UnknownElementException("Slot not found", key);
            } else {
                logger.error("Unexpected status: {}", response.statusCode());
            }
        } catch (IOException | InterruptedException | URISyntaxException e) {
            logger.error("Error while getting slot {}", key, e);
        }

        return Optional.empty();
    }

    // 🔹 Récupération de tous les slots
    public Collection<SlotDTO> getAllSlots() {
        logger.info("Getting all calendar slots");
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(URL_SERVER + "/"))
                    .GET()
                    .build();

            HttpResponse<List<SlotDTO>> response = client.send(
                    request,
                    new JsonBodyHandler<>(new TypeReference<List<SlotDTO>>() {})
            );

            if (response.statusCode() == 200) {
                return response.body();
            }
        } catch (IOException | InterruptedException | URISyntaxException e) {
            logger.error("Error getting all slots", e);
        }
        return List.of();
    }

    // 🔹 Récupération de slots dans une plage de dates
    @Override
    public Collection<SlotDTO> getAllSlotsIn(LocalDate startDate, LocalDate endDate) {
        logger.info("Getting calendar slot between {} and {}", startDate, endDate);
        return getCalendarSlotBetween(startDate, endDate);
    }

    // 🔹 Création d’un nouveau slot
    @Override
    public int create(SlotDTO element) {
        logger.info("Creating calendar slot {}", element);

        try {
            String json = mapper.writeValueAsString(element);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL_SERVER + "/slots"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                int newId = Integer.parseInt(response.body());
                logger.info("Slot created with id {}", newId);
                return newId;
            } else {
                logger.error("Failed to create slot. Status: {}", response.statusCode());
            }
        } catch (Exception e) {
            logger.error("Exception while creating slot", e);
        }

        return -1;
    }

    // 🔹 Mise à jour d’un slot existant
    @Override
    public int update(SlotDTO element) throws UnknownElementException, WrongVersionException {
        logger.info("Updating calendar slot {}", element);

        try {
            String json = mapper.writeValueAsString(element);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(URL_SERVER + "/slots/" + element.id()))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                boolean updated = Boolean.parseBoolean(response.body());
                if (updated) {
                    logger.info("Slot updated successfully");
                    return element.id();
                } else {
                    logger.warn("Wrong version during update");
                    throw new WrongVersionException("Version mismatch for slot ID " + element.id());
                }
            } else if (response.statusCode() == 404) {
                throw new UnknownElementException("Slot not found", element.id());
            } else {
                logger.error("Unexpected status during update: {}", response.statusCode());
            }
        } catch (IOException | InterruptedException | URISyntaxException e) {
            logger.error("Error during update", e);
        }

        return -1;
    }

    // 🔹 Suppression d’un slot
    @Override
    public void delete(SlotDTO element) throws UnknownElementException {
        logger.info("Deleting calendar slot {}", element);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(URL_SERVER + "/slots/" + element.id()))
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                boolean deleted = Boolean.parseBoolean(response.body());
                if (!deleted) {
                    logger.warn("Slot not found during delete");
                    throw new UnknownElementException("Slot not found", element.id());
                }
                logger.info("Slot deleted successfully");
            } else if (response.statusCode() == 404) {
                throw new UnknownElementException("Slot not found", element.id());
            } else {
                logger.error("Unexpected status during delete: {}", response.statusCode());
            }

        } catch (IOException | InterruptedException | URISyntaxException e) {
            logger.error("Error during delete", e);
        }
    }
}
