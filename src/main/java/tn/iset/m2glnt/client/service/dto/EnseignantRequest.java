package tn.iset.m2glnt.client.service.dto;

public class EnseignantRequest {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String cin;
    private String tel;
    private String password;
    private String type = "enseignant";

    // Constructeurs
    public EnseignantRequest() {}

    public EnseignantRequest(String nom, String prenom, String email, String cin, String tel) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.cin = cin;
        this.tel = tel;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCin() { return cin; }
    public void setCin(String cin) { this.cin = cin; }

    public String getTel() { return tel; }
    public void setTel(String tel) { this.tel = tel; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    @Override
    public String toString() {
        return "EnseignantRequest{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", cin='" + cin + '\'' +
                ", tel='" + tel + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}