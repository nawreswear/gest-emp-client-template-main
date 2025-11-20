package tn.iset.m2glnt.client.service.dao;
import com.fasterxml.jackson.core.type.TypeReference;
import tn.iset.m2glnt.client.model.SignupRequest;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class UserRestService {
    private final ApiService apiService;

    public UserRestService() {
        this.apiService = ApiService.getInstance();
    }

    // Récupérer tous les utilisateurs
    public CompletableFuture<List<Map<String, Object>>> getAllUsers() {
        HttpRequest request = apiService.createAuthenticatedRequest("/users")
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
                            throw new RuntimeException("Erreur lors du parsing des utilisateurs", e);
                        }
                    } else {
                        throw new RuntimeException("Erreur HTTP: " + response.statusCode() + " - " + response.body());
                    }
                });
    }

    // Ajouter un utilisateur
    public CompletableFuture<String> addUser(SignupRequest user) {
        try {
            String requestBody = apiService.getMapper().writeValueAsString(user);
            HttpRequest request = apiService.createAuthenticatedRequest("/signup")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            return apiService.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        if (response.statusCode() == 200) {
                            return "Utilisateur créé avec succès";
                        } else {
                            throw new RuntimeException("Erreur: " + response.body());
                        }
                    });
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    // Mettre à jour un utilisateur
    public CompletableFuture<String> updateUser(Long userId, SignupRequest user) {
        try {
            String requestBody = apiService.getMapper().writeValueAsString(user);
            HttpRequest request = apiService.createAuthenticatedRequest("/update/" + userId)
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            return apiService.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        if (response.statusCode() == 200) {
                            return "Utilisateur mis à jour avec succès";
                        } else {
                            throw new RuntimeException("Erreur: " + response.body());
                        }
                    });
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    // Supprimer un utilisateur
    public CompletableFuture<String> deleteUser(Long userId) {
        HttpRequest request = apiService.createAuthenticatedRequest("/deleteUser/" + userId)
                .DELETE()
                .build();

        return apiService.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        return "Utilisateur supprimé avec succès";
                    } else {
                        throw new RuntimeException("Erreur: " + response.body());
                    }
                });
    }
}
