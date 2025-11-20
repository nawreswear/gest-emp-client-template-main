package tn.iset.m2glnt.client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import tn.iset.m2glnt.client.service.dao.ApiService;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AdminRestService {
    private final ApiService apiService;

    public AdminRestService() {
        this.apiService = ApiService.getInstance();
    }

    // Récupérer tous les admins
    public CompletableFuture<List<Map<String, Object>>> getAllAdmins() {
        HttpRequest request = apiService.createAuthenticatedRequest("/admins")
                .GET()
                .build();

        return apiService.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            return apiService.getMapper().readValue(
                                    response.body(),
                                    new TypeReference<List<Map<String, Object>>>() {}
                            );
                        } catch (Exception e) {
                            throw new RuntimeException("Erreur lors du parsing des admins", e);
                        }
                    } else {
                        throw new RuntimeException("Erreur HTTP: " + response.statusCode() + " - " + response.body());
                    }
                });
    }

    // Supprimer un admin
    public CompletableFuture<String> deleteAdmin(Long adminId) {
        HttpRequest request = apiService.createAuthenticatedRequest("/deleteAdmin/" + adminId)
                .DELETE()
                .build();

        return apiService.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        return "Admin supprimé avec succès";
                    } else {
                        throw new RuntimeException("Erreur: " + response.body());
                    }
                });
    }
}