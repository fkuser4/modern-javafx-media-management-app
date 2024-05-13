package com.mydigitalmedia.mediaapp;

import com.mydigitalmedia.mediaapp.exceptions.LoginException;
import com.mydigitalmedia.mediaapp.model.*;
import com.mydigitalmedia.mediaapp.service.LoginService;
import com.mydigitalmedia.mediaapp.utils.DatabaseUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MainApplication extends Application {

    public static final Logger logger = LoggerFactory.getLogger(MainApplication.class);

    public static User user = null;
    public static final Map<String, Node> preloadedContent = new HashMap<>();

    @Override
    public void start(Stage stage)  {

        preloadFXML("/com/mydigitalmedia/mediaapp/fxml/dashboard.fxml", "dashboard");
        preloadFXML("/com/mydigitalmedia/mediaapp/fxml/tasks.fxml", "tasks");
        preloadFXML("/com/mydigitalmedia/mediaapp/fxml/settings.fxml", "settings");
        preloadFXML("/com/mydigitalmedia/mediaapp/fxml/addTask-modal.fxml", "addTaskModal");
        preloadFXML("/com/mydigitalmedia/mediaapp/fxml/editTask-modal.fxml", "editTaskModal");
        preloadFXML("/com/mydigitalmedia/mediaapp/fxml/showHistory-modal.fxml", "showHistoryModal");
        preloadFXML("/com/mydigitalmedia/mediaapp/fxml/showTask-modal.fxml", "showTaskModal");
        preloadFXML("/com/mydigitalmedia/mediaapp/fxml/campaign.fxml", "campaign");

        System.setProperty("prism.lcdtext", "false");

        String path;
        System.out.println("Starting application");
        try {
            MainApplication.user = LoginService.login(Path.of("dat/credentials.txt"));
            path = "/com/mydigitalmedia/mediaapp/fxml/main-view.fxml";
        } catch (IOException e) {
            logger.error("Error while reading credentials file!");
            path = "/com/mydigitalmedia/mediaapp/fxml/login-view.fxml";
        } catch (LoginException e){
            logger.error(e.getMessage());
            path = "/com/mydigitalmedia/mediaapp/fxml/login-view.fxml";
        } catch (SQLException e) {
            logger.error("Error while connecting to database!");
            path = "/com/mydigitalmedia/mediaapp/fxml/login-view.fxml";
        }

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(path));
        Scene scene = null;

        try {
            scene = new Scene(fxmlLoader.load(), 1200, 740);
        } catch (IOException e) {
            logger.error("Error while loading FXML: " + path);
            System.exit(1);
        }

        stage.setScene(scene);

        if (path.equals("/com/mydigitalmedia/mediaapp/fxml/main-view.fxml")){
            scene.getStylesheets().add(Objects.requireNonNull(MainApplication.class
                    .getResource("/com/mydigitalmedia/mediaapp/css/main.css")).toExternalForm());
            scene.getStylesheets().add(Objects.requireNonNull(MainApplication.class
                    .getResource("/com/mydigitalmedia/mediaapp/css/settings.css")).toExternalForm());
            scene.getStylesheets().add(Objects.requireNonNull(MainApplication.class
                    .getResource("/com/mydigitalmedia/mediaapp/css/tasks.css")).toExternalForm());
            scene.getStylesheets().add(Objects.requireNonNull(Objects.requireNonNull(MainApplication.class
                    .getResource("/com/mydigitalmedia/mediaapp/css/addTaskModal.css"))).toExternalForm());
            scene.getStylesheets().add(Objects.requireNonNull(Objects.requireNonNull(MainApplication.class
                    .getResource("/com/mydigitalmedia/mediaapp/css/showHistoryModal.css"))).toExternalForm());
            scene.getStylesheets().add(Objects.requireNonNull(Objects.requireNonNull(MainApplication.class
                    .getResource("/com/mydigitalmedia/mediaapp/css/showTaskModal.css"))).toExternalForm());
            scene.getStylesheets().add(Objects.requireNonNull(Objects.requireNonNull(MainApplication.class
                    .getResource("/com/mydigitalmedia/mediaapp/css/campaign.css"))).toExternalForm());
            scene.getStylesheets().add(Objects.requireNonNull(Objects.requireNonNull(MainApplication.class
                    .getResource("/com/mydigitalmedia/mediaapp/css/dashboard.css"))).toExternalForm());
        }else {
            scene.getStylesheets().add(Objects.requireNonNull(MainApplication.class
                    .getResource("/com/mydigitalmedia/mediaapp/css/login.css")).toExternalForm());
        }

        stage.initStyle(StageStyle.UNDECORATED);
        stage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class
                .getResourceAsStream("/com/mydigitalmedia/mediaapp/images/logo-final.png"))));
        stage.sizeToScene();

        stage.setOnCloseRequest(event -> {
            DatabaseUtils.closeAllConnections();
        });
        stage.show();
    }

    private void preloadFXML(String resourcePath, String key) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(resourcePath));
            Node node = fxmlLoader.load();
            preloadedContent.put(key, node);
        } catch (IOException e) {
            logger.error("Failed to preload FXML: " + resourcePath);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}