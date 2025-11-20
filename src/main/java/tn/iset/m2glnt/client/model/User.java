// User.java
package tn.iset.m2glnt.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String type;
    private String tel;
    private String cin;
    private String photo;

    // Constructeurs
    public User() {}

    public User(Long id, String nom, String prenom, String email, String type, String tel, String cin, String photo) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.type = type;
        this.tel = tel;
        this.cin = cin;
        this.photo = photo;
    }

    // Getters et setters
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
}