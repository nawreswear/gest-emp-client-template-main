package tn.iset.m2glnt.client.service.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import tn.iset.m2glnt.client.model.Enseignant;
import tn.iset.m2glnt.client.service.dto.EnseignantRequest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class EnseignantService {
    private final ApiService apiService;
    //private final AuthService authService;

    public EnseignantService() {
        this.apiService = ApiService.getInstance();
        //this.authService = AuthService.getInstance();
    }

    // Récupérer tous les enseignants
    public CompletableFuture<List<Enseignant>> getAllEnseignants() {
        HttpRequest request = apiService.createAuthenticatedRequest("/enseignant")
                .GET()
                .build();

        return apiService.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    System.out.println("🔍 Réponse API Enseignants - Status: " + response.statusCode());
                    System.out.println("🔍 Body: " + response.body());

                    if (response.statusCode() == 200) {
                        try {
                            List<Map<String, Object>> enseignantsMap = apiService.getMapper().readValue(
                                    response.body(),
                                    new TypeReference<List<Map<String, Object>>>() {}
                            );

                            // Convertir Map -> Enseignant avec gestion sécurisée des types
                            List<Enseignant> enseignants = enseignantsMap.stream()
                                    .map(this::mapToEnseignant)
                                    .toList();

                            System.out.println("✅ " + enseignants.size() + " enseignants chargés avec succès");

                            // Debug des enseignants chargés
                            if (!enseignants.isEmpty()) {
                                System.out.println("📋 Liste des enseignants:");
                                enseignants.forEach(ens ->
                                        System.out.println("   - " + ens.getPrenom() + " " + ens.getNom() + " (ID: " + ens.getId() + ", Tel: " + ens.getTel() + ")")
                                );
                            }

                            return enseignants;
                        } catch (Exception e) {
                            System.err.println("❌ Erreur lors du parsing des enseignants: " + e.getMessage());
                            e.printStackTrace();
                            throw new RuntimeException("Erreur lors du parsing des enseignants: " + e.getMessage(), e);
                        }
                    } else {
                        System.err.println("❌ Erreur HTTP " + response.statusCode() + ": " + response.body());
                        throw new RuntimeException("Erreur HTTP: " + response.statusCode() + " - " + response.body());
                    }
                })
                .exceptionally(throwable -> {
                    System.err.println("❌ Exception dans getAllEnseignants: " + throwable.getMessage());
                    throwable.printStackTrace();
                    throw new RuntimeException("Erreur lors de la récupération des enseignants: " + throwable.getMessage(), throwable);
                });
    }

    // ✅ CORRECTION : Méthode pour convertir Map en Enseignant avec gestion sécurisée des types
    private Enseignant mapToEnseignant(Map<String, Object> map) {
        Enseignant enseignant = new Enseignant();

        try {
            System.out.println("🔄 Mapping des données: " + map);

            // Gestion sécurisée de l'ID
            Object idObj = map.get("id");
            if (idObj != null) {
                if (idObj instanceof Number) {
                    enseignant.setId(((Number) idObj).longValue());
                } else if (idObj instanceof String) {
                    enseignant.setId(Long.parseLong((String) idObj));
                }
                System.out.println("   ID mappé: " + enseignant.getId());
            }

            // Gestion sécurisée des autres champs avec conversion toString()
            Object nomObj = map.get("nom");
            enseignant.setNom(nomObj != null ? nomObj.toString() : null);
            System.out.println("   Nom mappé: " + enseignant.getNom());

            Object prenomObj = map.get("prenom");
            enseignant.setPrenom(prenomObj != null ? prenomObj.toString() : null);
            System.out.println("   Prénom mappé: " + enseignant.getPrenom());

            Object emailObj = map.get("email");
            enseignant.setEmail(emailObj != null ? emailObj.toString() : null);
            System.out.println("   Email mappé: " + enseignant.getEmail());

            Object cinObj = map.get("cin");
            enseignant.setCin(cinObj != null ? cinObj.toString() : null);
            System.out.println("   CIN mappé: " + enseignant.getCin());

            // ✅ CORRECTION : Gestion spéciale pour le téléphone qui peut être un Integer
            Object telObj = map.get("tel");
            if (telObj != null) {
                if (telObj instanceof Number) {
                    enseignant.setTel(String.valueOf(((Number) telObj).longValue()));
                    System.out.println("   Tel (Number) mappé: " + enseignant.getTel());
                } else {
                    enseignant.setTel(telObj.toString());
                    System.out.println("   Tel (String) mappé: " + enseignant.getTel());
                }
            } else {
                enseignant.setTel(null);
                System.out.println("   Tel: null");
            }

            // Gestion des autres champs du modèle Enseignant
            Object passwordObj = map.get("password");
            enseignant.setPassword(passwordObj != null ? passwordObj.toString() : null);

            Object typeObj = map.get("type");
            enseignant.setType(typeObj != null ? typeObj.toString() : null);

            Object photoObj = map.get("photo");
            enseignant.setPhoto(photoObj != null ? photoObj.toString() : null);

            System.out.println("✅ Enseignant mappé avec succès: " + enseignant.getNom() + " " + enseignant.getPrenom());

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du mapping de l'enseignant: " + e.getMessage());
            System.err.println("📋 Données problématiques: " + map);
            e.printStackTrace();
            throw new RuntimeException("Erreur lors du mapping de l'enseignant: " + e.getMessage(), e);
        }

        return enseignant;
    }

    // Méthode synchrone pour SimpleSlotFormFactory
    public List<Enseignant> getAllEnseignantsSync() {
        try {
            System.out.println("🔄 Chargement synchrone des enseignants...");
            List<Enseignant> enseignants = getAllEnseignants().get();
            System.out.println("✅ " + enseignants.size() + " enseignants chargés en mode synchrone");
            return enseignants;
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement synchrone des enseignants: " + e.getMessage());
            return List.of();
        }
    }

    // Ajouter un enseignant
    public CompletableFuture<String> addEnseignant(EnseignantRequest enseignant) {
        try {
            System.out.println("➕ Envoi ajout enseignant: " + enseignant);

            String requestBody = apiService.getMapper().writeValueAsString(enseignant);
            HttpRequest request = apiService.createAuthenticatedRequest("/enseignant")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Content-Type", "application/json")
                    .build();

            return apiService.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        System.out.println("📡 Réponse ajout - Status: " + response.statusCode() + " - Body: " + response.body());

                        if (response.statusCode() == 200) {
                            return "Enseignant ajouté avec succès";
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

    // Mettre à jour un enseignant
    public CompletableFuture<String> updateEnseignant(EnseignantRequest enseignant) {
        try {
            if (enseignant.getId() == null) {
                System.err.println("❌ Erreur: ID manquant pour la modification");
                return CompletableFuture.failedFuture(new RuntimeException("ID manquant pour la modification"));
            }

            System.out.println("✏️ Envoi modification enseignant ID: " + enseignant.getId() + " - " + enseignant);

            String requestBody = apiService.getMapper().writeValueAsString(enseignant);
            HttpRequest request = apiService.createAuthenticatedRequest("/enseignant/update")
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Content-Type", "application/json")
                    .build();

            return apiService.getClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        System.out.println("📡 Réponse modification - Status: " + response.statusCode() + " - Body: " + response.body());

                        if (response.statusCode() == 200) {
                            return "Enseignant mis à jour avec succès";
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

    public CompletableFuture<String> deleteEnseignant(Long enseignantId) {
        System.out.println("🗑️ Envoi suppression enseignant ID: " + enseignantId);

        SimpleCalendarServiceDAO calendarService = new SimpleCalendarServiceDAO();
        return calendarService.deleteEnseignant(enseignantId);
    }

    // Ajoutez cette méthode pour obtenir le client HTTP
    private HttpClient getHttpClient() {
        return HttpClient.newHttpClient();
    }

    // Méthode pour parser les messages d'erreur
    private String parseErrorMessage(int statusCode, String errorBody) {
        try {
            Map<String, Object> error = apiService.getMapper().readValue(
                    errorBody,
                    new TypeReference<Map<String, Object>>() {}
            );

            String message = (String) error.get("message");
            String errorDetail = (String) error.get("error");
            String details = (String) error.get("details");

            if (statusCode == 409) { // CONFLICT - Contrainte de clé étrangère
                return "Impossible de supprimer cet enseignant : " +
                        (details != null ? details : message);
            } else if (statusCode == 404) { // NOT FOUND
                return "Enseignant non trouvé";
            } else {
                return message != null ? message : "Erreur lors de la suppression: " + errorBody;
            }

        } catch (Exception e) {
            // Si le parsing échoue, retourner un message générique
            if (errorBody.contains("créneaux horaires") || errorBody.contains("associé")) {
                return "Impossible de supprimer cet enseignant car il est associé à des créneaux horaires. " +
                        "Veuillez d'abord supprimer ou réaffecter ses créneaux.";
            }
            return "Erreur lors de la suppression: " + errorBody;
        }
    }



}