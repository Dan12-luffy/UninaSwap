package com.uninaswap.services;
import com.uninaswap.dao.UserDao;
import com.uninaswap.dao.UserDaoImpl;
import com.uninaswap.model.User;

import static com.uninaswap.utility.Sha256.hashPassword;


public class LoginService {

    private final UserDao userDao;

    public LoginService() {
        this.userDao = new UserDaoImpl();
    }

    public boolean authenticateUser(String username, String password) {
        User user = userDao.authenticate(username, hashPassword(password));
        if(user != null) {
            UserSession.getInstance().login(user);
            return true;
        }
        return false;
    }




}
