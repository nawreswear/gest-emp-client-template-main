package tn.iset.m2glnt.client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import tn.iset.m2glnt.client.service.dao.ApiService;
import tn.iset.m2glnt.client.service.dao.SimpleCalendarServiceDAO;
import tn.iset.m2glnt.client.service.dto.SalleRequest;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SalleRestService {
    private final ApiService apiService;
    private final SimpleCalendarServiceDAO calendarServiceDAO;

    public SalleRestService() {
        this.apiService = ApiService.getInstance();
        this.calendarServiceDAO = new SimpleCalendarServiceDAO();
    }

    // Récupérer toutes les salles
    public CompletableFuture<List<Map<String, Object>>> getAllSalles() {
        HttpRequest request = apiService.createAuthenticatedRequest("/salle")
                .GET()
                .build();

        return apiService.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    System.out.println("🔍 Réponse API Salles - Status: " + response.statusCode());
                    System.out.println("🔍 Body: " + response.body());

                    if (response.statusCode() == 200) {
                        try {
                            List<Map<String, Object>> salles = apiService.getMapper().readValue(
                                    response.body(),
                                    new TypeReference<List<Map<String, Object>>>() {}
                            );
                            System.out.println("✅ " + salles.size() + " salles récupérées avec succès");
                            return salles;
                        } catch (Exception e) {
                            System.err.println("❌ Erreur parsing JSON: " + e.getMessage());
                            throw new RuntimeException("Erreur lors du parsing des salles: " + e.getMessage(), e);
                        }
                    } else {
                        System.err.println("❌ Erreur HTTP " + response.statusCode() + ": " + response.body());
                        throw new RuntimeException("Erreur HTTP: " + response.statusCode() + " - " + response.body());
                    }
                })
                .exceptionally(throwable -> {
                    System.err.println("❌ Exception dans getAllSalles: " + throwable.getMessage());
                    throwable.printStackTrace();
                    throw new RuntimeException("Erreur lors de la récupération des salles: " + throwable.getMessage(), throwable);
                });
    }

    // Ajouter une salle
    public CompletableFuture<String> addSalle(SalleRequest salle) {
        try {
            String requestBody = apiService.getMapper().writeValueAsString(salle);
            HttpRequest request = apiService.createAuthenticatedRequest("/salle")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            return apiService.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        if (response.statusCode() == 200) {
                            return "Salle ajoutée avec succès";
                        } else {
                            throw new RuntimeException("Erreur: " + response.body());
                        }
                    });
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    // Mettre à jour une salle
    public CompletableFuture<String> updateSalle(SalleRequest salle) {
        try {
            // 🔥 VÉRIFICATION que l'ID est présent
            if (salle.getId() == null) {
                System.err.println("❌ Erreur: ID manquant pour la modification");
                return CompletableFuture.failedFuture(new RuntimeException("ID manquant pour la modification"));
            }

            System.out.println("🔄 Envoi modification salle ID: " + salle.getId() + " - " + salle);

            String requestBody = apiService.getMapper().writeValueAsString(salle);
            HttpRequest request = apiService.createAuthenticatedRequest("/salle/update")
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Content-Type", "application/json")
                    .build();

            return apiService.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        System.out.println("📡 Réponse modification - Status: " + response.statusCode() + " - Body: " + response.body());

                        if (response.statusCode() == 200) {
                            return "Salle mise à jour avec succès";
                        } else {
                            System.err.println("❌ Erreur modification - HTTP " + response.statusCode() + ": " + response.body());
                            throw new RuntimeException("Erreur lors de la modification: " + response.body());
                        }
                    })
                    .exceptionally(throwable -> {
                        System.err.println("❌ Exception dans updateSalle: " + throwable.getMessage());
                        throw new RuntimeException("Erreur lors de la modification: " + throwable.getMessage(), throwable);
                    });
        } catch (Exception e) {
            System.err.println("❌ Erreur préparation requête modification: " + e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    // 🔥 CORRECTION: Une seule méthode deleteSalle qui utilise le DAO du calendrier
    public CompletableFuture<String> deleteSalle(Long salleId) {
        try {
            // Utiliser le DAO du calendrier pour la suppression (qui gère la dissociation des slots)
            return calendarServiceDAO.deleteSalle(salleId);
        } catch (Exception e) {
            System.err.println("❌ Erreur dans SalleRestService.deleteSalle: " + e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    // 🔥 NOUVEAU: Méthode pour vérifier si une salle peut être supprimée
    public CompletableFuture<Map<String, Object>> canDeleteSalle(Long salleId) {
        return calendarServiceDAO.canDeleteSalle(salleId);
    }
}