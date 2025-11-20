package tn.iset.m2glnt.client.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private String typeUser;
    private String nom;
    private String prenom;
    private String cin;
    private String tel;
    private String photo;

    public AuthResponse() {
        // Constructeur par défaut nécessaire pour Jackson
    }

    public AuthResponse(String token, Long id, String username, String email, String typeUser) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.typeUser = typeUser;
    }

    public AuthResponse(String token, Long id, String username, String email, String typeUser,
                        String nom, String prenom, String cin, String tel, String photo) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.typeUser = typeUser;
        this.nom = nom;
        this.prenom = prenom;
        this.cin = cin;
        this.tel = tel;
        this.photo = photo;
    }

    // Getters et Setters

    @JsonProperty("token")
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("type")
    public String getTypeUser() {
        return typeUser;
    }

    public void setTypeUser(String typeUser) {
        this.typeUser = typeUser;
    }

    @JsonProperty("nom")
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @JsonProperty("prenom")
    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    @JsonProperty("cin")
    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    @JsonProperty("tel")
    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    @JsonProperty("photo")
    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    // Méthodes utilitaires

    public String getFullName() {
        if (prenom != null && nom != null) {
            return prenom + " " + nom;
        } else if (username != null) {
            return username;
        } else {
            return "Utilisateur";
        }
    }

    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(typeUser);
    }

    public boolean isEnseignant() {
        return "enseignant".equalsIgnoreCase(typeUser);
    }

    public boolean isEtudiant() {
        return "etudiant".equalsIgnoreCase(typeUser);
    }

    public String getUserRole() {
        if (typeUser == null) return "Utilisateur";

        switch (typeUser.toLowerCase()) {
            case "admin": return "Administrateur";
            case "enseignant": return "Enseignant";
            case "etudiant": return "Étudiant";
            default: return typeUser;
        }
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "token='" + (token != null ? "***" : "null") + '\'' +
                ", type='" + type + '\'' +
                ", id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", typeUser='" + typeUser + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", cin='" + cin + '\'' +
                ", tel='" + tel + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }

    // Méthode pour vérifier si la réponse est valide
    public boolean isValid() {
        return token != null && !token.isEmpty() &&
                email != null && !email.isEmpty() &&
                typeUser != null && !typeUser.isEmpty();
    }

    // Méthode pour obtenir les informations formatées
    public String getUserInfo() {
        return String.format("%s (%s) - %s", getFullName(), email, getUserRole());
    }
}