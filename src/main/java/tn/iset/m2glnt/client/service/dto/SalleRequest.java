package tn.iset.m2glnt.client.service.dto;

public class SalleRequest {
    private Long id; // 🔥 AJOUTER l'ID pour la modification
    private String nom;
    private String batiment;
    private Integer capacite;

    // Constructeurs
    public SalleRequest() {}

    public SalleRequest(Long id, String nom, String batiment, Integer capacite) {
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
        return "SalleRequest{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", batiment='" + batiment + '\'' +
                ", capacite=" + capacite +
                '}';
    }
}