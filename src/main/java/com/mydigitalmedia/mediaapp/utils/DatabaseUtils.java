package com.mydigitalmedia.mediaapp.utils;

import com.mydigitalmedia.mediaapp.MainApplication;
import com.mydigitalmedia.mediaapp.enums.UserRole;
import com.mydigitalmedia.mediaapp.filters.TaskFilter;
import com.mydigitalmedia.mediaapp.filters.UserFilter;
import com.mydigitalmedia.mediaapp.model.Task;
import com.mydigitalmedia.mediaapp.model.User;
import com.mydigitalmedia.mediaapp.records.LoginCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class DatabaseUtils {

    private static final String DATABASE_FILE = "config/database.properties";
    private static List<Connection> connectionPool = new ArrayList<>();
    private static final int MAX_POOL_SIZE = 2;

    private static final Object key = new Object();

    public static final Logger logger = MainApplication.logger;

    static {
        try {
            for (int i = 0; i < MAX_POOL_SIZE; i++) {
                connectionPool.add(createNewConnectionForPool());
            }
        } catch (SQLException | IOException e) {
            logger.error("Error while creating connection pool!", e);
        }
    }

    private DatabaseUtils() {
    }

    private static Connection createNewConnectionForPool() throws SQLException, IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(DATABASE_FILE));

        String databaseUrl = properties.getProperty("databaseUrl");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");

        return DriverManager.getConnection(databaseUrl, username, password);
    }

    public static Connection getConnection() throws SQLException, IOException {
        if (connectionPool.isEmpty()) {
            return createNewConnectionForPool();
        } else {
            return connectionPool.remove(connectionPool.size() - 1);
        }
    }

    public static void releaseConnection(Connection connection) {
        connectionPool.add(connection);
    }

    public static void closeAllConnections() {
        for (Connection connection : connectionPool) {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                logger.error("Error while closing connection!", e);
            }
        }
        connectionPool.clear();
    }

    public static List<Task> getAllTasks() throws SQLException, IOException {
        synchronized (key){
            List<Task> tasks = new ArrayList<>();
            Connection connection = null;
            try {
                connection = getConnection();
                String sqlQuery = "SELECT * FROM TASKS";

                try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            String id = resultSet.getString("ID");
                            String taskName = resultSet.getString("NAME");
                            User assignedTo = DatabaseUtils.getUsersByFilter(
                                    UserFilter.UserFilterBuilder.anUserFilter()
                                            .withUsername(resultSet.getString("ASSIGNEDTO")).build()).get(0);
                            String platform = resultSet.getString("PLATFORM");
                            LocalDate dueDate = resultSet.getDate("DUEDATE").toLocalDate();
                            String priority = resultSet.getString("PRIORITY");
                            String status = resultSet.getString("STATUS");
                            String taskDescription = resultSet.getString("DESCRIPTION");

                            Task task  = new Task(id, taskName, assignedTo, platform, dueDate, priority, status, taskDescription);
                            tasks.add(task);
                        }
                    }
                }
            }finally {
                if (connection != null) {
                    releaseConnection(connection);
                }
            }

            return tasks;
        }
    }

    public static List<Task> getTasksByFilter(TaskFilter taskFilter) throws SQLException, IOException{
        List<Task> tasks = new ArrayList<>();
        StringBuilder baseSqlQuery = new StringBuilder("SELECT * FROM TASKS WHERE 1=1");
        Connection connection = null;
        Map<Integer, Object> queryParams = new HashMap<>();
        int keyCounter = 1;

        synchronized (key){
            try {
                connection = getConnection();

                if (Optional.ofNullable(taskFilter.getTaskName()).isPresent()) {
                    baseSqlQuery.append(" AND NAME = ?");
                    queryParams.put(keyCounter++, taskFilter.getTaskName());
                }

                if (Optional.ofNullable(taskFilter.getAssignedTo()).isPresent()) {
                    baseSqlQuery.append(" AND ASSIGNEDTO = ?");
                    queryParams.put(keyCounter++, taskFilter.getAssignedTo().getLoginCredentials().getUsername());
                }

                if (Optional.ofNullable(taskFilter.getPlatform()).isPresent()) {
                    baseSqlQuery.append(" AND PLATFORM = ?");
                    queryParams.put(keyCounter++, taskFilter.getPlatform());
                }

                if (Optional.ofNullable(taskFilter.getDueDate()).isPresent()) {
                    baseSqlQuery.append(" AND DUEDATE = ?");
                    queryParams.put(keyCounter++, Date.valueOf(taskFilter.getDueDate()));
                }

                if (Optional.ofNullable(taskFilter.getPriority()).isPresent()) {
                    baseSqlQuery.append(" AND PRIORITY = ?");
                    queryParams.put(keyCounter++, taskFilter.getPriority());
                }

                if (Optional.ofNullable(taskFilter.getStatus()).isPresent()) {
                    baseSqlQuery.append(" AND STATUS = ?");
                    queryParams.put(keyCounter++, taskFilter.getStatus());
                }

                if (Optional.ofNullable(taskFilter.getTaskDescription()).isPresent()) {
                    baseSqlQuery.append(" AND DESCRIPTION = ?");
                    queryParams.put(keyCounter++, taskFilter.getTaskDescription());
                }

                PreparedStatement preparedStatement = connection.prepareStatement(baseSqlQuery.toString());
                for (Integer paramNumber : queryParams.keySet()) {
                    preparedStatement.setString(paramNumber, (String) queryParams.get(paramNumber));
                }
                preparedStatement.execute();

                ResultSet resultSet = preparedStatement.getResultSet();

                while (resultSet.next()) {
                    String id = resultSet.getString("ID");
                    String taskName = resultSet.getString("NAME");
                    User assignedTo = DatabaseUtils.getUsersByFilter(
                            UserFilter.UserFilterBuilder.anUserFilter()
                                    .withUsername(resultSet.getString("ASSIGNEDTO")).build()).get(0);
                    String platform = resultSet.getString("PLATFORM");
                    LocalDate dueDate = resultSet.getDate("DUEDATE").toLocalDate();
                    String priority = resultSet.getString("PRIORITY");
                    String status = resultSet.getString("STATUS");
                    String taskDescription = resultSet.getString("DESCRIPTION");

                    Task task = new Task(id, taskName, assignedTo, platform, dueDate, priority, status, taskDescription);
                    tasks.add(task);
                }
            } finally {
                if (connection != null) {
                    releaseConnection(connection);
                }
            }
        }

        return tasks;
    }

    public static void saveTask(Task task){
        synchronized (key){
            Connection connection = null;
            try {
                connection = getConnection();
                String sqlQuery = "INSERT INTO TASKS (ID, NAME, ASSIGNEDTO, PLATFORM, DUEDATE, PRIORITY, STATUS, " +
                        "DESCRIPTION) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
                    preparedStatement.setString(1, task.getId());
                    preparedStatement.setString(2, task.getTaskName());
                    preparedStatement.setString(3, task.getAssignedTo().getLoginCredentials().getUsername());
                    preparedStatement.setString(4, task.getPlatform());
                    preparedStatement.setDate(5, Date.valueOf(task.getDueDate()));
                    preparedStatement.setString(6, task.getPriority());
                    preparedStatement.setString(7, task.getStatus());
                    preparedStatement.setString(8, task.getTaskDescription());
                    preparedStatement.executeUpdate();
                }
            } catch (SQLException | IOException e) {
                logger.error("Error while saving task!", e);
            } finally {
                if (connection != null) {
                    releaseConnection(connection);
                }
            }
        }

    }

    public static void deleteTask(Task task){
        synchronized (key){
            Connection connection = null;
            try {
                connection = getConnection();
                String sqlQuery = "DELETE FROM TASKS WHERE ID = ?";

                try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
                    preparedStatement.setString(1, task.getId());
                    preparedStatement.executeUpdate();
                }
            } catch (SQLException | IOException e) {
                logger.error("Error while deleting task!", e);
            } finally {
                if (connection != null) {
                    releaseConnection(connection);
                }
            }
        }
    }

    public static void updateTask(Task task){
        synchronized (key){
            Connection connection = null;
            try {
                connection = getConnection();
                String sqlQuery = "UPDATE TASKS SET NAME = ?, ASSIGNEDTO = ?, PLATFORM = ?, DUEDATE = ?, PRIORITY = ?, " +
                        "STATUS = ?, DESCRIPTION = ? WHERE ID = ?";

                try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
                    preparedStatement.setString(1, task.getTaskName());
                    preparedStatement.setString(2, task.getAssignedTo().getLoginCredentials().getUsername());
                    preparedStatement.setString(3, task.getPlatform());
                    preparedStatement.setDate(4, Date.valueOf(task.getDueDate()));
                    preparedStatement.setString(5, task.getPriority());
                    preparedStatement.setString(6, task.getStatus());
                    preparedStatement.setString(7, task.getTaskDescription());
                    preparedStatement.setString(8, task.getId());
                    preparedStatement.executeUpdate();
                }
            } catch (SQLException | IOException e) {
                logger.error("Error while updating task!", e);
            } finally {
                if (connection != null) {
                    releaseConnection(connection);
                }
            }
        }
    }

    public static List<User> getUsersByFilter(UserFilter userFilter) throws SQLException, IOException{
        List<User> users = new ArrayList<>();
        StringBuilder baseSqlQuery = new StringBuilder("SELECT * FROM USERS WHERE 1=1");
        Connection connection = null;
        Map<Integer, Object> queryParams = new HashMap<>();
        int keyCounter = 1;

        try {
            connection = getConnection();

            if(Optional.ofNullable(userFilter.getUsername()).isPresent()){
                baseSqlQuery.append(" AND USERNAME = ?");
                queryParams.put(keyCounter++, userFilter.getUsername());
            }

            if(Optional.ofNullable(userFilter.getPassword()).isPresent()){
                baseSqlQuery.append(" AND PASSWORD = ?");
                queryParams.put(keyCounter++,  userFilter.getPassword());
            }

            if (Optional.ofNullable(userFilter.getUserRole()).isPresent()){
                baseSqlQuery.append(" AND ROLE = ?");
                queryParams.put(keyCounter++, userFilter.getUserRole().toString());
            }

            PreparedStatement preparedStatement = connection.prepareStatement(baseSqlQuery.toString());
            for(Integer paramNumber : queryParams.keySet()){
                preparedStatement.setString(paramNumber, (String) queryParams.get(paramNumber));
            }
            preparedStatement.execute();

            ResultSet resultSet = preparedStatement.getResultSet();

            while(resultSet.next()){
                String username = resultSet.getString("USERNAME");
                String password = resultSet.getString("PASSWORD");
                UserRole role = UserRole.valueOf(resultSet.getString("ROLE"));

                LoginCredentials loginCredentials = new LoginCredentials(username, password);
                User user = new User(loginCredentials, role);
                users.add(user);
            }
        }finally {
            if (connection != null) {
                releaseConnection(connection);
            }
        }
        return users;

    }

    public static List<User> getAllUsers() throws SQLException, IOException {
        List<User> users = new ArrayList<>();
        Connection connection = null;

        try {
            connection = getConnection();
            String sqlQuery = "SELECT * FROM USERS";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String username = resultSet.getString("USERNAME");
                        String password = resultSet.getString("PASSWORD");
                        UserRole role = UserRole.valueOf(resultSet.getString("ROLE"));

                        LoginCredentials loginCredentials = new LoginCredentials(username, password);
                        User user = new User(loginCredentials, role);
                        users.add(user);
                    }
                }
            }
        }finally {
            if (connection != null) {
                releaseConnection(connection);
            }
        }
        return users;
    }

    public static User getUser(String username, String password) throws SQLException, IOException {
        User user = null;
        Connection connection = null;
        try {
            connection = getConnection();
            String sqlQuery = "SELECT * FROM USERS WHERE USERNAME = ? AND PASSWORD = ?;";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String userUsername = resultSet.getString("USERNAME");
                        String userPassword = resultSet.getString("PASSWORD");
                        UserRole userRole = UserRole.valueOf(resultSet.getString("ROLE"));

                        LoginCredentials loginCredentials = new LoginCredentials(userUsername, userPassword);
                        user = new User(loginCredentials, userRole);
                    }
                }
            }
        }finally {
            if (connection != null) {
                releaseConnection(connection);
            }
        }

        return user;
    }
}