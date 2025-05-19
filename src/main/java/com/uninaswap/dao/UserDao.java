package com.uninaswap.dao;

import com.uninaswap.model.User;

import java.util.List;

public interface UserDao {
    // User findByUsername(String username);
    User authenticate(String username, String hashedPassword);
    boolean create(String name, String surname, String faculty, String username, String hashedPassword);
    boolean usernameExists(String username);
}