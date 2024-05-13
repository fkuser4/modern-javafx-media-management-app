package com.mydigitalmedia.mediaapp.records;

import java.io.Serializable;

public record LoginCredentials(String username, String password) implements Serializable {
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
