package com.mydigitalmedia.mediaapp.service;

import com.mydigitalmedia.mediaapp.MainApplication;
import com.mydigitalmedia.mediaapp.exceptions.LoginException;
import com.mydigitalmedia.mediaapp.model.User;
import com.mydigitalmedia.mediaapp.records.LoginCredentials;
import com.mydigitalmedia.mediaapp.utils.DatabaseUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.mydigitalmedia.mediaapp.utils.FileUtils;
import com.mydigitalmedia.mediaapp.utils.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {

    public static final Logger logger = MainApplication.logger;


    public static User login(Path credentialsPath) throws LoginException, SQLException, IOException {
        List<String> credentials;

        credentials = FileUtils.getLoginCredentialsFromFile(credentialsPath);

        if (credentials.size() >= 2) {

            String username = credentials.get(0);
            String password = credentials.get(1);

            return login(username, password);

        }else {
            throw new LoginException("No valid credentials found!");
        }
    }

    public static User login(String username, String password) throws LoginException, SQLException, IOException {

            LoginCredentials loginCredentials = new LoginCredentials(username, password);

            Optional<User> userOptional = Optional.ofNullable(DatabaseUtils.getUser(
                    loginCredentials.getUsername(), loginCredentials.getPassword()));
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                logger.info("User " + loginCredentials.getUsername() + " logged in successfully!");
                return user;
            } else {
                logger.info("Invalid credentials for user " + loginCredentials.getUsername() + "!");
                throw new LoginException("Invalid credentials for user " + loginCredentials.getUsername() + "!");
            }

    }
}