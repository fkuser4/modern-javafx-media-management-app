package com.mydigitalmedia.mediaapp.model;

import com.mydigitalmedia.mediaapp.enums.UserRole;
import com.mydigitalmedia.mediaapp.records.LoginCredentials;

import java.io.Serializable;

public class User implements Serializable {
    private final LoginCredentials loginCredentials;
    private final UserRole userRole;

    public User(LoginCredentials loginCredentials, UserRole userRole) {
        this.loginCredentials = loginCredentials;
        this.userRole = userRole;
    }

    public LoginCredentials getLoginCredentials() {
        return loginCredentials;
    }

    public UserRole getUserRole() {
        return userRole;
    }
}
