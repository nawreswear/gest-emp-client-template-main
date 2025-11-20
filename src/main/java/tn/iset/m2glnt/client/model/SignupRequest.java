
// SignupRequest.java
package tn.iset.m2glnt.client.model;

public class SignupRequest {
    private String nom;
    private String prenom;
    private String email;
    private String tel;
    private String type;
    private String cin;
    private String password;

    public SignupRequest(){}
    public SignupRequest(String nom, String prenom, String email, String tel,
                         String type, String cin, String password) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.tel = tel;
        this.type = type;
        this.cin = cin;
        this.password = password;
    }
    // Getters et setters
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getTel() { return tel; }
    public void setTel(String tel) { this.tel = tel; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCin() { return cin; }
    public void setCin(String cin) { this.cin = cin; }

}
