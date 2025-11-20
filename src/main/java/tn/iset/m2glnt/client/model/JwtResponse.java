package tn.iset.m2glnt.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JwtResponse {
    private Long id;
    private String email;
    private String type;
    private String token;
    private String accessToken;
    private String tokenType;

    public JwtResponse() {}

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    @Override
    public String toString() {
        return "JwtResponse{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", type='" + type + '\'' +
                ", token='" + (token != null ? "***" : "null") + '\'' +
                '}';
    }
}