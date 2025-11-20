package tn.iset.m2glnt.client.service.dao;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import tn.iset.m2glnt.client.model.User;
import tn.iset.m2glnt.client.model.SignupRequest;

import java.util.List;

public class UserService {

    public static Service<List<User>> getAllUsers() {
        return new Service<List<User>>() {
            @Override
            protected Task<List<User>> createTask() {
                return new Task<List<User>>() {
                    @Override
                    protected List<User> call() throws Exception {
                        return ApiClient.get("/users", List.class);
                    }
                };
            }
        };
    }

    public static Service<User> getUserById(Long userId) {
        return new Service<User>() {
            @Override
            protected Task<User> createTask() {
                return new Task<User>() {
                    @Override
                    protected User call() throws Exception {
                        return ApiClient.get("/getUserById/" + userId, User.class);
                    }
                };
            }
        };
    }

    public static Service<User> addUser(User user) {
        return new Service<User>() {
            @Override
            protected Task<User> createTask() {
                return new Task<User>() {
                    @Override
                    protected User call() throws Exception {
                        return ApiClient.post("/addUser", user, User.class);
                    }
                };
            }
        };
    }

    public static Service<User> updateUser(Long userId, SignupRequest user) {
        return new Service<User>() {
            @Override
            protected Task<User> createTask() {
                return new Task<User>() {
                    @Override
                    protected User call() throws Exception {
                        return ApiClient.put("/update/" + userId, user, User.class);
                    }
                };
            }
        };
    }

    public static Service<Void> deleteUser(Long userId) {
        return new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        ApiClient.delete("/deleteUser/" + userId);
                        return null;
                    }
                };
            }
        };
    }

    public static Service<Boolean> checkEmailUnique(String email) {
        return new Service<Boolean>() {
            @Override
            protected Task<Boolean> createTask() {
                return new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        try {
                            String result = ApiClient.post("/checkEmailUnique", email, String.class);
                            return result.contains("unique");
                        } catch (Exception e) {
                            return false;
                        }
                    }
                };
            }
        };
    }
    // Dans UserService.java - Ajoutez cette méthode si elle n'existe pas
    public static Service<User> getUserByEmail(String email) {
        return new Service<User>() {
            @Override
            protected Task<User> createTask() {
                return new Task<User>() {
                    @Override
                    protected User call() throws Exception {
                        return ApiClient.get("/getUserByEmail/" + email, User.class);
                    }
                };
            }
        };
    }
}