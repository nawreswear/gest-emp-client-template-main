package tn.iset.m2glnt.client.model;

public class Salle {
    private Long id;
    private String nom;
    private String batiment;
    private Integer capacite;

    // Constructeurs
    public Salle() {}

    public Salle(Long id, String nom, String batiment, Integer capacite) {
        this.id = id;
        this.nom = nom;
        this.batiment = batiment;
        this.capacite = capacite;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getBatiment() { return batiment; }
    public void setBatiment(String batiment) { this.batiment = batiment; }

    public Integer getCapacite() { return capacite; }
    public void setCapacite(Integer capacite) { this.capacite = capacite; }

    @Override
    public String toString() {
        return nom + (batiment != null ? " - " + batiment : "");
    }
}