package com.mydigitalmedia.mediaapp.ui;

import com.mydigitalmedia.mediaapp.MainApplication;
import com.mydigitalmedia.mediaapp.model.FacebookData;
import com.mydigitalmedia.mediaapp.model.InstagramData;
import com.mydigitalmedia.mediaapp.model.TikTokData;
import com.mydigitalmedia.mediaapp.model.TwitterData;
import com.mydigitalmedia.mediaapp.threads.SetChartDataThread;
import com.mydigitalmedia.mediaapp.utils.FileUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsController {

    @FXML
    private Label twitterLabel;
    @FXML
    private Label facebookLabel;
    @FXML
    private Label instagramLabel;
    @FXML
    private Label tikTokLabel;
    @FXML
    private VBox contentVBox;
    @FXML
    private Label usernameLabel;
    @FXML
    private Button saveTwitterUsernameButton;
    @FXML
    private TextField facebookTextField;
    @FXML
    private Button saveFacebookUsernameButton;
    @FXML
    private Button saveInstagramUsernameButton;
    @FXML
    private TextField tikTokTextField;
    @FXML
    private Button saveTikTokUsernameButton;
    @FXML
    private TextField twitterTextField;
    @FXML
    private TextField instagramTextField;
    @FXML
    private Button logoutButton;


    Logger logger = MainApplication.logger;

    @FXML
    private void initialize() {

        usernameLabel.setText("Usernames");
        usernameLabel.getStyleClass().add("usernameLabel");

        twitterTextField.getStyleClass().add("settings-text-field");
        instagramTextField.getStyleClass().add("settings-text-field");
        facebookTextField.getStyleClass().add("settings-text-field");
        tikTokTextField.getStyleClass().add("settings-text-field");

        twitterTextField.setPromptText("TheRock");
        instagramTextField.setPromptText("therock");
        facebookTextField.setPromptText("DwayneJohnson");
        tikTokTextField.setPromptText("therock");

        saveFacebookUsernameButton.getStyleClass().add("save-button");
        saveInstagramUsernameButton.getStyleClass().add("save-button");
        saveTikTokUsernameButton.getStyleClass().add("save-button");
        saveTwitterUsernameButton.getStyleClass().add("save-button");


        saveTikTokUsernameButton.setText("Save");
        saveInstagramUsernameButton.setText("Save");
        saveFacebookUsernameButton.setText("Save");
        saveTwitterUsernameButton.setText("Save");

        logoutButton.getStyleClass().add("logout-button");
        logoutButton.setText("Logout");

        VBox.setMargin(usernameLabel, new Insets(50, 0, 20, 50));
        HBox.setMargin(tikTokTextField, new Insets(10, 0, 0, 67));
        HBox.setMargin(twitterTextField, new Insets(10, 0, 0, 65));
        HBox.setMargin(instagramTextField, new Insets(10, 0, 0, 47));
        HBox.setMargin(facebookTextField, new Insets(10, 0, 0, 50));
        HBox.setMargin(saveFacebookUsernameButton, new Insets(10, 0, 0, 17));
        HBox.setMargin(saveInstagramUsernameButton, new Insets(10, 0, 0, 17));
        HBox.setMargin(saveTikTokUsernameButton, new Insets(10, 0, 0, 17));
        HBox.setMargin(saveTwitterUsernameButton, new Insets(10, 0, 0, 17));
        VBox.setMargin(logoutButton, new Insets(345, 0, 0, 50));

        HBox.setMargin(tikTokLabel, new Insets(20, 0, 0, 50));
        HBox.setMargin(twitterLabel, new Insets(20, 0, 0, 50));
        HBox.setMargin(instagramLabel, new Insets(20, 0, 0, 50));
        HBox.setMargin(facebookLabel, new Insets(20, 0, 0, 50));

        tikTokLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #ffffff");
        twitterLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #ffffff");
        instagramLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #ffffff");
        facebookLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #ffffff");

        setInputTextFields();

    }

    public void setInputTextFields() {
        Properties properties = new Properties();
        String propertiesFilePath = "config/settings.properties";

        try (FileInputStream in = new FileInputStream(propertiesFilePath)) {
            properties.load(in);
        } catch (IOException e) {
            logger.error("Error while reading settings file!");
        }

        twitterTextField.setText(properties.getProperty("twitterUsername"));
        facebookTextField.setText(properties.getProperty("facebookUsername"));
        instagramTextField.setText(properties.getProperty("instagramUsername"));
        tikTokTextField.setText(properties.getProperty("tiktokUsername"));
    }

    @FXML
    private void handleLogoutAction() throws IOException {
        String path = "/com/mydigitalmedia/mediaapp/fxml/login-view.fxml";
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(path));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load(), 1200, 740);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.setScene(scene);
        scene.getStylesheets().add(Objects.requireNonNull(MainApplication.class
                .getResource("/com/mydigitalmedia/mediaapp/css/login.css")).toExternalForm());

        FileUtils.clearLoginCredentials(Path.of("dat/credentials.txt"));

        stage.show();
    }

    @FXML
    private void handleSaveTwitterUsernameButtonAction() {
        saveSettings("twitterUsername", twitterTextField.getText(), twitterTextField);
        if (!twitterTextField.getText().isEmpty()) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            TwitterData twitterData = new TwitterData(twitterTextField.getText());
            Thread thread = new Thread(new SetChartDataThread<>(twitterData));
            executorService.execute(thread);;
        }else {
            DashboardController.twitterProperty.set(null);
        }
    }
    @FXML
    private void handleSaveFacebookUsernameButtonAction() {
        saveSettings("facebookUsername", facebookTextField.getText(), facebookTextField);
        if (!facebookTextField.getText().isEmpty()) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            FacebookData facebookData = new FacebookData(facebookTextField.getText());
            Thread thread = new Thread(new SetChartDataThread<>(facebookData));
            executorService.execute(thread);;
        }else{
            DashboardController.facebookProperty.set(null);
        }
    }
    @FXML
    private void handleSaveInstagramUsernameButtonAction() {
        saveSettings("instagramUsername", instagramTextField.getText(), instagramTextField);
        if (!instagramTextField.getText().isEmpty()) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            InstagramData instagramData = new InstagramData(instagramTextField.getText());
            Thread thread = new Thread(new SetChartDataThread<>(instagramData));
            executorService.execute(thread);;
        }else {
            DashboardController.instagramProperty.set(null);
        }
    }
    @FXML
    private void handleSaveTikTokUsernameAction() {
        saveSettings("tiktokUsername", tikTokTextField.getText(), tikTokTextField);
        if (!tikTokTextField.getText().isEmpty()) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            TikTokData tikTokData = new TikTokData(tikTokTextField.getText());
            Thread thread = new Thread(new SetChartDataThread<>(tikTokData));
            executorService.execute(thread);;
        }else {
            DashboardController.tiktokProperty.set(null);
        }
    }

    private void saveSettings(String key, String value, TextField textField){
        Properties properties = new Properties();
        String propertiesFilePath = "config/settings.properties";

        try (FileInputStream in = new FileInputStream(propertiesFilePath)) {
            properties.load(in);
        } catch (IOException e) {
            textField.setStyle("-fx-border-color: #d75b5b");
            logger.error("Error while reading settings file!");
        }

        properties.setProperty(key, value);

        try (FileOutputStream out = new FileOutputStream(propertiesFilePath)) {
            properties.store(out, null);
        } catch (IOException e) {
            textField.setStyle("-fx-border-color: #d75b5b");
            logger.error("Error while writing settings file!");
        }

        textField.setStyle("-fx-border-color: #56d556");
    }
}
