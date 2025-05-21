package com.uninaswap.services;

import com.uninaswap.model.User;

public class UserSession {
    private static final UserSession instance = new UserSession();
    private User currentUser;
    private boolean loggedIn = false;

    private UserSession() {
        // Singleton
    }

    public static UserSession getInstance() {
        return instance;
    }

    public void login(User user) {
        this.currentUser = user;
        this.loggedIn = true;
    }

    public void logout() {
        this.currentUser = null;
        this.loggedIn = false;
    }

    public User getCurrentUser() {
        return currentUser;
    }
    public int getCurrentUserId() {
        return currentUser.getId();
    }

    /*public boolean isLoggedIn() {
        return loggedIn;
    }*/
}