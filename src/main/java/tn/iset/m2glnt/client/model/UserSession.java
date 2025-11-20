package tn.iset.m2glnt.client.model;

import tn.iset.m2glnt.client.service.dao.ApiClient;

public class UserSession {
    private static UserSession instance;
    private Long userId;
    private String email;
    private String userType;
    private String token;
    private User currentUser;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setCurrentUser(Long userId, String email, String userType, String token) {
        this.userId = userId;
        this.email = email;
        this.userType = userType;
        this.token = token;
    }

    public void setCurrentUser(User user, String token) {
        this.currentUser = user;
        this.userId = user.getId();
        this.email = user.getEmail();
        this.userType = user.getType();
        this.token = token;
    }

    public void clearSession() {
        this.userId = null;
        this.email = null;
        this.userType = null;
        this.token = null;
        this.currentUser = null;
        ApiClient.setAuthToken(null);
    }

    // Getters
    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getUserType() { return userType; }
    public String getToken() { return token; }
    public User getCurrentUser() { return currentUser; }
    public boolean isLoggedIn() { return token != null; }

    public boolean isAdmin() { return "admin".equals(userType); }
    public boolean isEnseignant() { return "enseignant".equals(userType); }
    public boolean isEtudiant() { return "etudiant".equals(userType); }
}