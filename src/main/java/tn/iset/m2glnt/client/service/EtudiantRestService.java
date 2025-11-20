package tn.iset.m2glnt.client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import tn.iset.m2glnt.client.service.dao.ApiService;
import tn.iset.m2glnt.client.service.dto.EtudiantRequest;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class EtudiantRestService {
    private final ApiService apiService;

    public EtudiantRestService() {
        this.apiService = ApiService.getInstance();
    }

    // Récupérer tous les étudiants
    public CompletableFuture<List<Map<String, Object>>> getAllEtudiants() {
        HttpRequest request = apiService.createAuthenticatedRequest("/etudiant")
                .GET()
                .build();

        return apiService.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    System.out.println("🔍 Réponse API Étudiants - Status: " + response.statusCode());
                    System.out.println("🔍 Body: " + response.body());

                    if (response.statusCode() == 200) {
                        try {
                            List<Map<String, Object>> etudiants = apiService.getMapper().readValue(
                                    response.body(),
                                    new TypeReference<List<Map<String, Object>>>() {}
                            );
                            System.out.println("✅ " + etudiants.size() + " étudiants récupérés avec succès");
                            return etudiants;
                        } catch (Exception e) {
                            System.err.println("❌ Erreur parsing JSON: " + e.getMessage());
                            throw new RuntimeException("Erreur lors du parsing des étudiants: " + e.getMessage(), e);
                        }
                    } else {
                        System.err.println("❌ Erreur HTTP " + response.statusCode() + ": " + response.body());
                        throw new RuntimeException("Erreur HTTP: " + response.statusCode() + " - " + response.body());
                    }
                })
                .exceptionally(throwable -> {
                    System.err.println("❌ Exception dans getAllEtudiants: " + throwable.getMessage());
                    throwable.printStackTrace();
                    throw new RuntimeException("Erreur lors de la récupération des étudiants: " + throwable.getMessage(), throwable);
                });
    }

    // Ajouter un étudiant
    public CompletableFuture<String> addEtudiant(EtudiantRequest etudiant) {
        try {
            System.out.println("➕ Envoi ajout étudiant: " + etudiant);

            String requestBody = apiService.getMapper().writeValueAsString(etudiant);
            HttpRequest request = apiService.createAuthenticatedRequest("/etudiant/save")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Content-Type", "application/json")
                    .build();

            return apiService.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        System.out.println("📡 Réponse ajout - Status: " + response.statusCode() + " - Body: " + response.body());

                        if (response.statusCode() == 200) {
                            return "Étudiant ajouté avec succès";
                        } else {
                            System.err.println("❌ Erreur ajout - HTTP " + response.statusCode() + ": " + response.body());
                            throw new RuntimeException("Erreur lors de l'ajout: " + response.body());
                        }
                    });
        } catch (Exception e) {
            System.err.println("❌ Erreur préparation requête ajout: " + e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    // Mettre à jour un étudiant
    public CompletableFuture<String> updateEtudiant(EtudiantRequest etudiant) {
        try {
            // Vérification que l'ID est présent
            if (etudiant.getId() == null) {
                System.err.println("❌ Erreur: ID manquant pour la modification");
                return CompletableFuture.failedFuture(new RuntimeException("ID manquant pour la modification"));
            }

            System.out.println("✏️ Envoi modification étudiant ID: " + etudiant.getId() + " - " + etudiant);

            String requestBody = apiService.getMapper().writeValueAsString(etudiant);
            HttpRequest request = apiService.createAuthenticatedRequest("/etudiant/update")
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Content-Type", "application/json")
                    .build();

            return apiService.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        System.out.println("📡 Réponse modification - Status: " + response.statusCode() + " - Body: " + response.body());

                        if (response.statusCode() == 200) {
                            return "Étudiant mis à jour avec succès";
                        } else {
                            System.err.println("❌ Erreur modification - HTTP " + response.statusCode() + ": " + response.body());
                            throw new RuntimeException("Erreur lors de la modification: " + response.body());
                        }
                    });
        } catch (Exception e) {
            System.err.println("❌ Erreur préparation requête modification: " + e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    // Supprimer un étudiant
    public CompletableFuture<String> deleteEtudiant(Long etudiantId) {
        System.out.println("🗑️ Envoi suppression étudiant ID: " + etudiantId);

        HttpRequest request = apiService.createAuthenticatedRequest("/etudiant/delete/" + etudiantId)
                .DELETE()
                .build();

        return apiService.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    System.out.println("📡 Réponse suppression - Status: " + response.statusCode() + " - Body: " + response.body());

                    if (response.statusCode() == 200) {
                        return "Étudiant supprimé avec succès";
                    } else {
                        System.err.println("❌ Erreur suppression - HTTP " + response.statusCode() + ": " + response.body());
                        throw new RuntimeException("Erreur lors de la suppression: " + response.body());
                    }
                });
    }
}