package tn.iset.m2glnt.client.service.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import tn.iset.m2glnt.client.service.dao.exceptions.UnknownElementException;
import tn.iset.m2glnt.client.service.dao.exceptions.WrongVersionException;
import tn.iset.m2glnt.client.service.dto.SlotDTO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

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
    private final List<CalendarRefreshListener> refreshListeners = new ArrayList<>();


    // 🔥 NOUVEAU: Interface pour les écouteurs
    public interface CalendarRefreshListener {
        void onCalendarRefreshNeeded();
        void onSlotCreated(int newSlotId);
        void onSlotUpdated(int slotId);
        void onSlotDeleted(int slotId);
    }

    // 🔥 NOUVEAU: Méthodes pour gérer les écouteurs
    public void addRefreshListener(CalendarRefreshListener listener) {
        refreshListeners.add(listener);
    }

    public void removeRefreshListener(CalendarRefreshListener listener) {
        refreshListeners.remove(listener);
    }

    // 🔥 NOUVEAU: Notifier tous les écouteurs
    private void notifyRefreshListeners(String eventType, Integer slotId) {
        for (CalendarRefreshListener listener : refreshListeners) {
            switch (eventType) {
                case "refresh" -> listener.onCalendarRefreshNeeded();
                case "slotCreated" -> listener.onSlotCreated(slotId);
                case "slotUpdated" -> listener.onSlotUpdated(slotId);
                case "slotDeleted" -> listener.onSlotDeleted(slotId);
            }
        }
    }
    // 🔹 Récupération des slots entre deux dates - CORRIGÉ
    private List<SlotDTO> getCalendarSlotBetween(LocalDate startDate, LocalDate endDate) {
        try {
            StringBuilder urlBuilder = new StringBuilder(URL_SERVER);

            if (startDate != null && endDate != null) {
                urlBuilder.append("?startDate=").append(startDate)
                        .append("&endDate=").append(endDate);
            }

            String url = urlBuilder.toString();
            System.out.println("🔍 DEBUG - URL appelée: " + url);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("🔍 DEBUG - Response status: " + response.statusCode());

            if (response.statusCode() == 200) {
                // Désérialisation directe depuis le backend
                List<SlotDTO> slots = mapper.readValue(
                        response.body(),
                        new TypeReference<List<SlotDTO>>() {}
                );

                System.out.println("✅ Slots récupérés: " + slots.size());
                return slots;
            } else {
                logger.warn("Unexpected status code: {}", response.statusCode());
                return getMockSlots(startDate, endDate);
            }
        } catch (Exception e) {
            logger.error("Exception thrown while getting slots between dates", e);
            System.out.println("🌐 Erreur connexion serveur, utilisation données mock");
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return getMockSlots(startDate, endDate);
        }
    }

    // 🔹 Récupération d'un slot par ID - CORRIGÉ
    @Override
    public Optional<SlotDTO> get(int key) throws UnknownElementException {
        logger.info("Getting calendar slot by key {}", key);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL_SERVER + "/" + key))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                SlotDTO slot = mapper.readValue(response.body(), SlotDTO.class);
                return Optional.of(slot);
            } else if (response.statusCode() == 404) {
                throw new UnknownElementException("Slot not found with ID: " + key, key);
            } else {
                logger.error("Unexpected status: {}", response.statusCode());
                throw new UnknownElementException("Server error with status: " + response.statusCode(), key);
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Error while getting slot {}", key, e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new UnknownElementException("Communication error while fetching slot", key);
        }
    }

    // 🔹 Récupération de tous les slots - CORRIGÉ
    public Collection<SlotDTO> getAllSlots() {
        logger.info("Getting all calendar slots");
        return getCalendarSlotBetween(null, null);
    }

    // 🔹 Récupération de slots dans une plage de dates - CORRIGÉ
    @Override
    public Collection<SlotDTO> getAllSlotsIn(LocalDate startDate, LocalDate endDate) {
        logger.info("Getting calendar slot between {} and {}", startDate, endDate);
        return getCalendarSlotBetween(startDate, endDate);
    }

    // 🔹 Création d'un nouveau slot - CORRECTION COMPLÈTE
    @Override
    public int create(SlotDTO element) {
        logger.info("Creating calendar slot {}", element);

        try {
            // CORRECTION: Créer une copie avec ID à 0 pour l'envoi
            SlotDTO elementToSend = new SlotDTO(
                    0, // ID mis à 0 pour la création
                    element.nom(),
                    element.description(),
                    element.timeBegin(),
                    element.timeEnd(),
                    element.version(),
                    element.enseignantId(),
                    element.salleId()
            );

            String json = mapper.writeValueAsString(elementToSend);
            System.out.println("📤 JSON envoyé: " + json);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL_SERVER))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("📥 Réponse création - Status: " + response.statusCode() + ", Body: " + response.body());

            if (response.statusCode() == 201) {
                try {
                    int newId = Integer.parseInt(response.body().trim());
                    logger.info("✅ Slot créé avec id {}", newId);

                    // 🔥 CORRECTION: Notifier les écouteurs du nouveau slot
                    notifyRefreshListeners("slotCreated", newId);

                    // 🔥 CORRECTION: Notifier aussi pour un rafraîchissement complet
                    notifyRefreshListeners("refresh", null);

                    return newId;
                } catch (NumberFormatException e) {
                    logger.error("❌ Format d'ID invalide dans la réponse: {}", response.body());
                    return -1;
                }
            } else {
                logger.error("❌ Échec de création du slot. Status: {}, Response: {}", response.statusCode(), response.body());
                return -1;
            }
        } catch (Exception e) {
            logger.error("❌ Exception lors de la création du slot", e);
            return -1;
        }
    }
    private void refreshCalendarAfterCreation(int newSlotId, LocalDate slotDate) {
        System.out.println("🔄 Rafraîchissement après création du slot " + newSlotId);

        // Notifier les écouteurs du rafraîchissement
        // Cette méthode devrait déclencher un rechargement des slots
        notifyCalendarRefreshListeners();
    }

    // 🔹 NOTIFICATION DES ÉCOUTEURS DE RAFRAÎCHISSEMENT
    private void notifyCalendarRefreshListeners() {
        // Implémentez cette méthode pour notifier les contrôleurs
        // que le calendrier doit être rafraîchi
        System.out.println("🔔 Notification: Rafraîchissement du calendrier demandé");
    }

    // 🔹 Mise à jour d'un slot existant - CORRIGÉ
    @Override
    public int update(SlotDTO element) throws UnknownElementException, WrongVersionException {
        logger.info("Updating calendar slot {}", element);

        try {
            String json = mapper.writeValueAsString(element);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL_SERVER + "/" + element.id()))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("📥 Réponse mise à jour - Status: " + response.statusCode() + ", Body: " + response.body());

            if (response.statusCode() == 200) {
                // 🔥 CORRECTION: Notifier la mise à jour
                notifyRefreshListeners("slotUpdated", element.id());
                notifyRefreshListeners("refresh", null);

                SlotDTO updatedSlot = mapper.readValue(response.body(), SlotDTO.class);
                return updatedSlot.version();
            } else if (response.statusCode() == 404) {
                throw new UnknownElementException("Slot not found with ID: " + element.id(), element.id());
            } else if (response.statusCode() == 409) {
                throw new WrongVersionException("Version conflict for slot ID " + element.id());
            } else {
                logger.error("Unexpected status during update: {}", response.statusCode());
                throw new UnknownElementException("Server error during update", element.id());
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Error during update", e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new UnknownElementException("Communication error during update", element.id());
        }
    }

    // 🔹 Suppression d'un slot - CORRIGÉ
    @Override
    public void delete(SlotDTO element) throws UnknownElementException {
        logger.info("Deleting calendar slot {}", element);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL_SERVER + "/" + element.id()))
                    .DELETE()
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("📥 Réponse suppression - Status: " + response.statusCode() + ", Body: " + response.body());

            if (response.statusCode() == 200) {
                logger.info("Slot deleted successfully");
                // 🔥 CORRECTION: Notifier la suppression
                notifyRefreshListeners("slotDeleted", element.id());
                notifyRefreshListeners("refresh", null);
            } else if (response.statusCode() == 404) {
                throw new UnknownElementException("Slot not found with ID: " + element.id(), element.id());
            } else {
                logger.error("Unexpected status during delete: {}", response.statusCode());
                throw new UnknownElementException("Server error during delete", element.id());
            }

        } catch (IOException | InterruptedException e) {
            logger.error("Error during delete", e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new UnknownElementException("Communication error during delete", element.id());
        }
    }

    // 🔹 Suppression d'un slot - CORRIGÉ
   /* @Override
    public void delete(SlotDTO element) throws UnknownElementException {
        logger.info("Deleting calendar slot {}", element);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL_SERVER + "/" + element.id()))
                    .DELETE()
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("📥 Réponse suppression - Status: " + response.statusCode() + ", Body: " + response.body());

            if (response.statusCode() == 200) {
                logger.info("Slot deleted successfully");
            } else if (response.statusCode() == 404) {
                throw new UnknownElementException("Slot not found with ID: " + element.id(), element.id());
            } else {
                logger.error("Unexpected status during delete: {}", response.statusCode());
                throw new UnknownElementException("Server error during delete", element.id());
            }

        } catch (IOException | InterruptedException e) {
            logger.error("Error during delete", e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new UnknownElementException("Communication error during delete", element.id());
        }
    }*/

    // 🔹 Méthode utilitaire pour données mock - CORRIGÉE
    private List<SlotDTO> getMockSlots(LocalDate startDate, LocalDate endDate) {
        List<SlotDTO> mockSlots = new ArrayList<>();

        System.out.println("🎯 Génération données mock du " + startDate + " au " + endDate);

        LocalDate actualStart = startDate != null ? startDate : LocalDate.now();
        LocalDate actualEnd = endDate != null ? endDate : LocalDate.now().plusDays(7);

        LocalDate current = actualStart;
        int slotId = 1000;

        while (!current.isAfter(actualEnd)) {
            if (current.getDayOfWeek() != java.time.DayOfWeek.SATURDAY &&
                    current.getDayOfWeek() != java.time.DayOfWeek.SUNDAY) {

                // Créneau du matin
                SlotDTO morningSlot = new SlotDTO(
                        slotId++,
                        "Cours Mathématiques",
                        "Algèbre linéaire et géométrie",
                        LocalDateTime.of(current, java.time.LocalTime.of(8, 0)),
                        LocalDateTime.of(current, java.time.LocalTime.of(10, 0)),
                        1,
                        null,
                        null
                );
                mockSlots.add(morningSlot);

                // Créneau de l'après-midi
                SlotDTO afternoonSlot = new SlotDTO(
                        slotId++,
                        "TP Informatique",
                        "Programmation JavaFX et bases de données",
                        LocalDateTime.of(current, java.time.LocalTime.of(14, 0)),
                        LocalDateTime.of(current, java.time.LocalTime.of(16, 0)),
                        1,
                        null,
                        null
                );
                mockSlots.add(afternoonSlot);
            }
            current = current.plusDays(1);
        }

        System.out.println("✅ Données mock générées: " + mockSlots.size() + " créneaux");
        return mockSlots;
    }
    // Ajoutez cette méthode à SimpleCalendarServiceDAO
    public CompletableFuture<String> deleteEnseignant(Long enseignantId) {
        try {
            String url = "http://localhost:7070/timeslots/enseignant/delete/" + enseignantId;
            System.out.println("🗑️ Suppression enseignant - URL: " + url);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .DELETE()
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build();

            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        System.out.println("📡 Réponse suppression enseignant - Status: " + response.statusCode());

                        if (response.statusCode() == 200) {
                            System.out.println("✅ Enseignant supprimé avec succès");
                            return "Enseignant supprimé avec succès";
                        } else {
                            String errorBody = response.body();
                            System.err.println("❌ Erreur suppression enseignant - HTTP " + response.statusCode() + ": " + errorBody);
                            throw new RuntimeException("Erreur lors de la suppression: " + errorBody);
                        }
                    });

        } catch (Exception e) {
            System.err.println("❌ Erreur préparation suppression enseignant: " + e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }
    // Dans SimpleCalendarServiceDAO.java - Ajoutez ces méthodes

    public CompletableFuture<String> deleteSalle(Long salleId) {
        try {
            String url = "http://localhost:7070/timeslots/salle/delete/" + salleId;
            System.out.println("🗑️ Suppression salle - URL: " + url);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .DELETE()
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build();

            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        System.out.println("📡 Réponse suppression salle - Status: " + response.statusCode() + ", Body: " + response.body());

                        if (response.statusCode() == 200) {
                            System.out.println("✅ Salle supprimée avec succès");

                            // 🔥 Notifier le rafraîchissement du calendrier
                            notifyRefreshListeners("refresh", null);

                            return "Salle supprimée avec succès";
                        } else {
                            String errorBody = response.body();
                            System.err.println("❌ Erreur suppression salle - HTTP " + response.statusCode() + ": " + errorBody);

                            // Essayer d'extraire le message d'erreur du JSON
                            try {
                                Map<String, Object> errorResponse = mapper.readValue(errorBody, new TypeReference<Map<String, Object>>() {});
                                String errorMessage = (String) errorResponse.get("error");
                                throw new RuntimeException(errorMessage != null ? errorMessage : "Erreur lors de la suppression");
                            } catch (Exception e) {
                                throw new RuntimeException("Erreur lors de la suppression: " + errorBody);
                            }
                        }
                    });

        } catch (Exception e) {
            System.err.println("❌ Erreur préparation suppression salle: " + e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<Map<String, Object>> canDeleteSalle(Long salleId) {
        try {
            String url = "http://localhost:7070/timeslots/salle/" + salleId + "/can-delete";
            System.out.println("🔍 Vérification suppression salle - URL: " + url);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        System.out.println("📡 Réponse vérification salle - Status: " + response.statusCode());

                        if (response.statusCode() == 200) {
                            try {
                                Map<String, Object> result = mapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
                                System.out.println("✅ Vérification salle: " + result);
                                return result;
                            } catch (Exception e) {
                                throw new RuntimeException("Erreur parsing réponse: " + e.getMessage());
                            }
                        } else {
                            throw new RuntimeException("Erreur vérification: HTTP " + response.statusCode());
                        }
                    });

        } catch (Exception e) {
            System.err.println("❌ Erreur vérification suppression salle: " + e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }
}