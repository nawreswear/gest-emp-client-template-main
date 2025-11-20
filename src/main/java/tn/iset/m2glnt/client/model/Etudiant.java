package tn.iset.m2glnt.client.model;

public class Etudiant {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String type;
    private Integer tel;
    private String cin;
    private String photo;
   // Nouveau champ pour les groupes

    // Constructeurs
    public Etudiant() {}

    public Etudiant(Long id, String nom, String prenom, String email) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
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

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getTel() { return tel; }
    public void setTel(Integer tel) { this.tel = tel; }

    public String getCin() { return cin; }
    public void setCin(String cin) { this.cin = cin; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }


    @Override
    public String toString() {
        return nom + " " + prenom;
    }
}