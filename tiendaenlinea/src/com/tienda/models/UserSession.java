package com.tienda.models;


public class UserSession {
    private static UserSession instance;
    private Usuario user;
    private static int currentUserId;

    private UserSession(Usuario user) {
        this.user = user;
    }

    public static UserSession getInstance(Usuario user) {
        if (instance == null) {
            instance = new UserSession(user);
        }
        return instance;
    }

    public synchronized static UserSession getInstance() {
        if (instance == null) {
            throw new IllegalStateException("UserSession no ha sido inicializado.");
        }
        return instance;
    }


    public Usuario getUser() {
        return user;
    }

    public void cleanUserSession() {
        instance = null;
    }

    // Establece el ID del usuario actual
    public synchronized static void setCurrentUserId(int userId) {
        currentUserId = userId;
    }
    
     // Obtiene el ID del usuario actual
     public synchronized static int getCurrentUserId() {
        return currentUserId;
    }
}
