package tn.iset.m2glnt.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Enseignant {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String type;
    private String tel;
    private String cin;
    private String photo;

    // Ajouter le champ password avec @JsonProperty
    @JsonProperty("password")
    private String password;

    // Constructeurs
    public Enseignant() {}

    public Enseignant(Long id, String nom, String prenom, String email, String type,
                      String tel, String cin, String photo) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.type = type;
        this.tel = tel;
        this.cin = cin;
        this.photo = photo;
    }

    // Getters et setters pour tous les champs
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTel() { return tel; }
    public void setTel(String tel) { this.tel = tel; }

    public String getCin() { return cin; }
    public void setCin(String cin) { this.cin = cin; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    // Getter et setter pour password
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return nom + " " + prenom;
    }

    // Méthode pour convertir en nom complet
    public String getNomComplet() {
        return (prenom != null ? prenom : "") + " " + (nom != null ? nom : "");
    }

    // Méthode utilitaire pour la conversion
    public Enseignant toEnseignant() {
        Enseignant enseignant = new Enseignant();
        enseignant.setId(this.id);
        enseignant.setNom(this.nom);
        enseignant.setPrenom(this.prenom);
        enseignant.setEmail(this.email);
        enseignant.setType(this.type);
        enseignant.setTel(this.tel);
        enseignant.setCin(this.cin);
        enseignant.setPhoto(this.photo);
        return enseignant;
    }
}