package com.mydigitalmedia.mediaapp.ui;

import com.mydigitalmedia.mediaapp.MainApplication;
import com.mydigitalmedia.mediaapp.enums.UserRole;
import com.mydigitalmedia.mediaapp.model.*;
import com.mydigitalmedia.mediaapp.utils.SVGPathExtractor;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class MainController {

    @FXML
    private ImageView logoImageView;

    @FXML
    private Button homeButton;

    @FXML
    private Button campaignButton;

    @FXML
    private Button tasksButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Label versionLabel;

    @FXML
    private VBox mainVBox;

    @FXML
    private HBox topHBox;

    @FXML
    private Label titleLabel;

    @FXML
    private Button minimizeButton;

    @FXML
    private Button closeButton;

    @FXML
    private VBox centerVBox;

    @FXML
    private GridPane parentGridPane;

    @FXML
    private StackPane mainStackPane;

    @FXML
    private VBox contentVBox;

    private Button currentlySelectedButton = null;
    private double xOffset = 0;
    private double yOffset = 0;

    Logger logger = MainApplication.logger;

    @FXML
    public void initialize() {

        //Platform.runLater(()->{
        //    if(MainApplication.user.getUserRole() == UserRole.USER){
        //        mainVBox.getChildren().remove(tasksButton);
        //        VBox.setMargin(versionLabel, new Insets(475, 0, 0, 0));
        //    }else if(MainApplication.user.getUserRole() == UserRole.ADMIN){
        //        mainVBox.getChildren().remove(campaignButton);
        //        VBox.setMargin(versionLabel, new Insets(475, 0, 0, 0));
        //    }
        //
        //});

        initMenu();
        initTopBar();
        centerVBox.getStyleClass().add("center-vbox");
    }


    private void initTopBar(){
        topHBox.getStyleClass().add("top-hbox");

        windowDragAndResize();
        setMinimizeButton();
        setCloseButton();
        HBox.setMargin(closeButton, new Insets(20, 0, 0, 0));
        HBox.setMargin(minimizeButton, new Insets(20, 7, 0, 732));
        HBox.setMargin(titleLabel, new Insets(25, 0, 0, 30));
        homeButton.fire();
    }

    private void setTitleLabel(String title){
        titleLabel.setText(title);
        titleLabel.getStyleClass().add("title-label");
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
    }

    private void setMinimizeButton() {
        String pathToSVG = "src/main/resources/com/mydigitalmedia/mediaapp/svg/minimize.svg";
        Group svg = new Group(createPathControl(SVGPathExtractor.extractSVGPath(pathToSVG)));

        Bounds bounds = svg.getBoundsInParent();
        double scale = Math.min(12/bounds.getWidth(), 12 / bounds.getHeight());
        svg.setScaleX(scale);
        svg.setScaleY(scale);

        minimizeButton.setId("minimize-button");
        minimizeButton.setGraphic(svg);
        minimizeButton.setMaxSize(20, 20);
        minimizeButton.setMinSize(20, 20);
        minimizeButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

    }

    private void setCloseButton() {
        String pathToSVG = "src/main/resources/com/mydigitalmedia/mediaapp/svg/close.svg";
        Group svg = new Group(createPathControl(SVGPathExtractor.extractSVGPath(pathToSVG)));

        Bounds bounds = svg.getBoundsInParent();
        double scale = Math.min(12/bounds.getWidth(), 12 / bounds.getHeight());
        svg.setScaleX(scale);
        svg.setScaleY(scale);

        closeButton.setId("close-button");
        closeButton.setGraphic(svg);
        closeButton.setMaxSize(20, 20);
        closeButton.setMinSize(20, 20);
        closeButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    private void windowDragAndResize(){
        topHBox.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        topHBox.setOnMouseDragged(event -> {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        topHBox.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        topHBox.setOnMouseDragged(event -> {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

    }

    private void initMenu(){
        mainVBox.getStyleClass().add("main-vbox");

        Map<String, Button> buttonMap = Map.of(
                "home.svg", homeButton,
                "calendar.svg", campaignButton,
                "stacks.svg", tasksButton,
                "settings.svg", settingsButton
        );

        setButtonIcons(buttonMap);
        setLogoImageView();
        setVersionLabel();

        VBox.setMargin(logoImageView, new Insets(15, 0, 0, 0));
        VBox.setMargin(versionLabel, new Insets(425, 0, 0, 0));
        VBox.setMargin(homeButton, new Insets(20, 0, 10, 0));
        VBox.setMargin(campaignButton, new Insets(10, 0, 10, 0));
        VBox.setMargin(tasksButton, new Insets(10, 0, 10, 0));
        VBox.setMargin(settingsButton, new Insets(10, 0, 10, 0));

    }

    private void setLogoImageView() {
        logoImageView.setScaleX(1.5);
        logoImageView.setScaleY(1.5);
        logoImageView.setPreserveRatio(true);
        logoImageView.setSmooth(true);
        logoImageView.setCache(true);

    }

    private void setVersionLabel() {
        versionLabel.setText("v1.0.0");
        versionLabel.getStyleClass().add("version-label");
    }

    private void setButtonIcons(Map<String, Button> buttonMap){
        buttonMap.forEach(
                (key, value) -> {
                    String pathToSVG = "src/main/resources/com/mydigitalmedia/mediaapp/svg/" + key;
                    Group svg = new Group(createPath(SVGPathExtractor.extractSVGPath(pathToSVG)));

                    Bounds bounds = svg.getBoundsInParent();
                    double scale = Math.min(16/bounds.getWidth(), 16 / bounds.getHeight());
                    svg.setScaleX(scale);
                    svg.setScaleY(scale);

                    value.getStyleClass().add("menu-buttons");
                    value.setGraphic(svg);
                    value.setMaxSize(20, 20);
                    value.setMinSize(20, 20);
                    value.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                }
        );

    }

    private SVGPath createPathControl(String d) {
        SVGPath path = new SVGPath();
        path.getStyleClass().add("svg-control");
        path.setContent(d);
        return path;
    }

    private SVGPath createPath(String d) {
        SVGPath path = new SVGPath();
        path.getStyleClass().add("svg");
        path.setContent(d);
        return path;
    }

    private void setSelectedButtonCss(Button button){
        if (currentlySelectedButton != null) {
            currentlySelectedButton.getStyleClass().remove("menu-buttons-selected");
            currentlySelectedButton.getStyleClass().add("menu-buttons");
            currentlySelectedButton.getGraphic().getStyleClass().remove("svg-selected");
            currentlySelectedButton.getGraphic().getStyleClass().add("svg");
        }

        button.getStyleClass().remove("menu-buttons");
        button.getStyleClass().add("menu-buttons-selected");
        button.getGraphic().getStyleClass().remove("svg");
        button.getGraphic().getStyleClass().add("svg-selected");

        currentlySelectedButton = button;
    }

    @FXML
    private void handleHomeButtonAction() {
        setTitleLabel("Dashboard");
        setSelectedButtonCss(homeButton);
        contentVBox.getChildren().clear();
        contentVBox.getChildren().add(MainApplication.preloadedContent.get("dashboard"));
    }

    @FXML
    private void handleCampaignButtonAction() {
        setTitleLabel("Campaign");
        setSelectedButtonCss(campaignButton);
        contentVBox.getChildren().clear();
        contentVBox.getChildren().add(MainApplication.preloadedContent.get("campaign"));
    }

    @FXML
    private void handleTasksButtonAction() {
        setTitleLabel("Tasks");
        setSelectedButtonCss(tasksButton);
        contentVBox.getChildren().clear();
        contentVBox.getChildren().add(MainApplication.preloadedContent.get("tasks"));

        //try {
        //    FXMLLoader fxmlLoader = new FXMLLoader();
        //    fxmlLoader.setLocation(getClass().getResource("/com/mydigitalmedia/mediaapp/fxml/tasks.fxml"));
//
        //    Node settingsNode = fxmlLoader.load();
//
        //    contentVBox.getChildren().clear();
        //    contentVBox.getChildren().add(settingsNode);
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}
    }

    @FXML
    private void handleSettingsButtonAction() {
        setTitleLabel("Settings");
        setSelectedButtonCss(settingsButton);
        contentVBox.getChildren().clear();
        contentVBox.getChildren().add(MainApplication.preloadedContent.get("settings"));

    }

    @FXML
    private void handleMinimizeButtonAction() {
        Stage stage = (Stage) minimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handleCloseButtonAction() {
        Platform.exit();
    }




}