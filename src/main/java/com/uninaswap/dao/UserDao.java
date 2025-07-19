package com.uninaswap.dao;

import com.uninaswap.model.User;

public interface UserDao {
    // User findByUsername(String username);
    User authenticateUser(String username, String hashedPassword);
    boolean insertUser(User u);
    boolean updatePassword(int userId, String hashedPassword);
    boolean updateUsername(int userId, String username);
    boolean usernameAlreadyExists(String username);
    boolean updateFaculty(int userId, String faculty);
    String findUsernameFromID(int id);
    String findFullNameFromID(int id);
    User findUserFromID(int id);

    User findUserByUsername(String username);
}