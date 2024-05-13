package com.mydigitalmedia.mediaapp.utils;

import com.mydigitalmedia.mediaapp.MainApplication;
import com.mydigitalmedia.mediaapp.records.LoginCredentials;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class FileUtils<T> {

    private static final Object lock = new Object();
    Logger logger = MainApplication.logger;

    public static List<String> getLoginCredentialsFromFile(Path credentialsPath) throws IOException {
        return Files.readAllLines(credentialsPath);
    }

    public static void saveLoginCredentialsToFile(Path credentialsPath, LoginCredentials loginCredentials) throws IOException {
        Files.writeString(credentialsPath, loginCredentials.username() + "\n"
        + PasswordUtils.hashPassword(loginCredentials.getPassword()));
    }

    public static void clearLoginCredentials(Path credentialsPath) throws IOException {
        Files.writeString(credentialsPath, "");
    }



    public void addObjectToFile(T object, String fileName) {
        synchronized (lock){
            List<T> objects = deserializeObjectsFromFile(fileName);
            objects.add(object);
            serializeObjectsToFile(objects, fileName);
        }
    }

    private void serializeObjectsToFile(List<T> objects, String fileName) {
        synchronized (lock){
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
                out.writeObject(objects);
            } catch (IOException e) {
                logger.error("Error while writing to file: " + e.getMessage());
            }
        }
    }

    public List<T> deserializeObjectsFromFile(String fileName) {
        synchronized (lock) {
            List<T> objects = new ArrayList<>();
            File file = new File(fileName);
            if (!file.exists() || file.length() == 0) {
                return objects;
            }
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
                objects = (List<T>) in.readObject();
            } catch (EOFException | ClassNotFoundException ignored) {
            } catch (IOException e) {
                logger.error("Error while reading from file: " + e.getMessage());
            }
            return objects;
        }

    }
}
