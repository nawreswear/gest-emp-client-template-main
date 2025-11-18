package tn.iset.m2glnt.client.service.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import tn.iset.m2glnt.client.service.dao.ApiService;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EtudiantService {
    private final ApiService apiService;

    public EtudiantService() {
        this.apiService = ApiService.getInstance();
    }

    // Récupérer tous les étudiants
    public CompletableFuture<List<Object>> getAllEtudiants() {
        HttpRequest request = apiService.createAuthenticatedRequest("/etudiant")
                .GET()
                .build();

        return apiService.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            return apiService.getMapper().readValue(response.body(), new TypeReference<List<Object>>() {});
                        } catch (Exception e) {
                            throw new RuntimeException("Erreur lors du parsing des étudiants", e);
                        }
                    } else {
                        throw new RuntimeException("Erreur HTTP: " + response.statusCode());
                    }
                });
    }

    // Mettre à jour un étudiant
    public CompletableFuture<String> updateEtudiant(Object etudiant) {
        try {
            String requestBody = apiService.getMapper().writeValueAsString(etudiant);
            HttpRequest request = apiService.createAuthenticatedRequest("/etudiant/update")
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            return apiService.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        if (response.statusCode() == 200) {
                            return "Étudiant mis à jour avec succès";
                        } else {
                            return "Erreur: " + response.body();
                        }
                    });
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    // Supprimer un étudiant
    public CompletableFuture<String> deleteEtudiant(Long etudiantId) {
        HttpRequest request = apiService.createAuthenticatedRequest("/etudiant/delete/" + etudiantId)
                .DELETE()
                .build();

        return apiService.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        return "Étudiant supprimé avec succès";
                    } else {
                        return "Erreur: " + response.body();
                    }
                });
    }
}