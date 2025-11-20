package tn.iset.m2glnt.client.service.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import tn.iset.m2glnt.client.model.JwtResponse;
import tn.iset.m2glnt.client.model.LoginRequest;
import tn.iset.m2glnt.client.model.SignupRequest;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AuthService {
    private static final String BASE_URL = "http://localhost:3003";
    private static final ObjectMapper mapper = new ObjectMapper();


    // --- Inscription ---
    public static Service<JwtResponse> register(SignupRequest signupRequest) {
        return new Service<>() {
            @Override
            protected Task<JwtResponse> createTask() {
                return new Task<>() {
                    @Override
                    protected JwtResponse call() throws Exception {
                        String url = BASE_URL + "/signup"; // ✅ même endpoint que le backend
                        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Content-Type", "application/json");
                        connection.setRequestProperty("Accept", "application/json");
                        connection.setDoOutput(true);
                        //String jsonBody = mapper.writeValueAsString(signupRequest);
                        System.out.println("=== REQUÊTE INSCRIPTION COMPLÈTE ===");
                        System.out.println("Nom: " + signupRequest.getNom());
                        System.out.println("Prénom: " + signupRequest.getPrenom());
                        System.out.println("Email: " + signupRequest.getEmail());
                        System.out.println("Tél: " + signupRequest.getTel());
                        System.out.println("Type: " + signupRequest.getType());
                        System.out.println("CIN: " + signupRequest.getCin());
                        System.out.println("Password: " + (signupRequest.getPassword() != null ? "***" : "null"));
                        //System.out.println("Photo: " + signupRequest.getPhoto());
                        System.out.println("=====================================");

                        String jsonBody = mapper.writeValueAsString(signupRequest);
                        System.out.println("JSON envoyé: " + jsonBody);
                        try (OutputStream os = connection.getOutputStream()) {
                            os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
                        }

                        int responseCode = connection.getResponseCode();
                        if (responseCode == 200) {
                            String response = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                            return mapper.readValue(response, JwtResponse.class);
                        } else {
                            String error = new String(connection.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
                            throw new RuntimeException("Erreur d'inscription : " + error);
                        }
                    }
                };
            }
        };
    }

    // --- Connexion ---
    public static Service<JwtResponse> login(String email, String password) {
        return new Service<>() {
            @Override
            protected Task<JwtResponse> createTask() {
                return new Task<>() {
                    @Override
                    protected JwtResponse call() throws Exception {
                        String url = BASE_URL + "/signin"; // ✅ même endpoint
                        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Content-Type", "application/json");
                        connection.setRequestProperty("Accept", "application/json");
                        connection.setDoOutput(true);

                        LoginRequest login = new LoginRequest(email, password);
                        String jsonBody = mapper.writeValueAsString(login);

                        try (OutputStream os = connection.getOutputStream()) {
                            os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
                        }

                        int responseCode = connection.getResponseCode();
                        String responseBody = new String(
                                (responseCode == 200 ? connection.getInputStream() : connection.getErrorStream())
                                        .readAllBytes(),
                                StandardCharsets.UTF_8
                        );

                        if (responseCode == 200) {
                            JwtResponse jwt = mapper.readValue(responseBody, JwtResponse.class);
                            System.out.println("✅ Connexion réussie. Token JWT : " + jwt.getToken());
                            return jwt;
                        } else {
                            throw new RuntimeException("Erreur de connexion : " + responseBody);
                        }
                    }
                };
            }
        };
    }
}
