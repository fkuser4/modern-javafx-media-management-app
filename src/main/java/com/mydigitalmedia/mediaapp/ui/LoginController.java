package com.mydigitalmedia.mediaapp.ui;

import com.mydigitalmedia.mediaapp.MainApplication;
import com.mydigitalmedia.mediaapp.canvas.FloatingIcon;
import com.mydigitalmedia.mediaapp.exceptions.LoginException;
import com.mydigitalmedia.mediaapp.model.User;
import com.mydigitalmedia.mediaapp.records.LoginCredentials;
import com.mydigitalmedia.mediaapp.service.LoginService;
import com.mydigitalmedia.mediaapp.utils.PasswordUtils;
import com.mydigitalmedia.mediaapp.utils.SVGPathExtractor;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import com.mydigitalmedia.mediaapp.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.mydigitalmedia.mediaapp.MainApplication.user;


public class LoginController {

    Logger logger = MainApplication.logger;

    private double xOffset = 0;
    private double yOffset = 0;

    private static final int MAX_ICONS = 20;
    private Set<FloatingIcon> floatingIcons = new HashSet<>();
    private Random rand = new Random();
    private Timeline snowflakeGenerator;

    @FXML
    private Button closeButton;

    @FXML
    private Button minimizeButton;

    @FXML
    private Canvas canvas;

    @FXML
    private StackPane stackPane;

    @FXML
    private VBox vBox;

    @FXML
    private GridPane gridPane;

    @FXML
    private HBox topLeftBarHbox;

    @FXML
    private HBox topRightBarHbox;

    @FXML
    private ImageView imageView;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField loginPasswordField;

    @FXML
    private Label loginLabel;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink forgotPasswordHyperlink;

    @FXML
    private CheckBox rememberMeCheckBox;

    @FXML
    private Label invalidLoginLabel;


    @FXML
    public void initialize() {
        setCloseButtonIcon();
        setMinimizeButton();
        windowDragAndResize();
        setBackgroundColors();
        topRightBarHbox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        topLeftBarHbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(this::createCanvasAnimation);
        setLogo();
        setLoginForm();

    }

    private void setLoginForm(){

        loginLabel.getStyleClass().add("login-label");

        invalidLoginLabel.getStyleClass().add("invalid-login-label");
        invalidLoginLabel.setVisible(false);

        forgotPasswordHyperlink.getStyleClass().add("forgot-password-hyperlink");
        forgotPasswordHyperlink.setPadding(new Insets(10, 10, 10, 10));
        forgotPasswordHyperlink.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        forgotPasswordHyperlink.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URI("http://www.google.com"));
            } catch (Exception e) {
                logger.error("Error while opening browser!");
            }
        });

        rememberMeCheckBox.getStyleClass().add("remember-me-checkbox");
        rememberMeCheckBox.setPadding(new Insets(10, 10, 10, 10));
        rememberMeCheckBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        loginButton.getStyleClass().add("login-button");
        loginButton.setPadding(new Insets(10, 10, 10, 10));
        loginButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        usernameTextField.getStyleClass().add("username-textfield");
        loginPasswordField.getStyleClass().add("password-textfield");

        usernameTextField.setPromptText("Username");
        loginPasswordField.setPromptText("Password");

        usernameTextField.setPrefWidth(100);
        usernameTextField.setPrefHeight(40);

        usernameTextField.setPadding(new Insets(10, 10, 10, 10));
        loginPasswordField.setPadding(new Insets(10, 10, 10, 10));

        usernameTextField.setOnMouseClicked(event -> {
            invalidLoginLabel.setVisible(false);
        });
        loginPasswordField.setOnMouseClicked(event -> {
            invalidLoginLabel.setVisible(false);
        });

        usernameTextField.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        loginPasswordField.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        VBox.setMargin(loginButton, new Insets(20, 0, 0, 100));
        VBox.setMargin(forgotPasswordHyperlink, new Insets(0, 0, 0, 266));
        VBox.setMargin(rememberMeCheckBox, new Insets(20, 0, 0, 100));
        VBox.setMargin(usernameTextField, new Insets(0, 0, 0, 100));
        VBox.setMargin(loginPasswordField, new Insets(0, 0, 0, 100));
        VBox.setMargin(loginLabel, new Insets(100, 0, 25, 100));
        VBox.setMargin(invalidLoginLabel, new Insets(20, 0, 0, 100));
    }

    @FXML
    private void handleLoginButtonAction() {
        Task<User> loginTask = new Task<>() {
            @Override
            protected User call() throws Exception {

                LoginCredentials loginCredentials = new LoginCredentials(
                        usernameTextField.getText().trim(), loginPasswordField.getText().trim());

                User user = LoginService.login(loginCredentials.username(),
                        PasswordUtils.hashPassword(loginCredentials.getPassword()));

                if (rememberMeCheckBox.isSelected()) {
                    FileUtils.saveLoginCredentialsToFile(Path.of("dat/credentials.txt"), loginCredentials);
                }
                return user;
            }
        };

        loginTask.setOnSucceeded(event -> {
            user = loginTask.getValue();
            loadMainView();
        });

        loginTask.setOnFailed(event -> {
            Throwable e = loginTask.getException();
            if (e instanceof LoginException) {
                logger.info("Invalid username or password!");
                invalidLoginLabel.setText("Invalid username or password!");
            } else {
                logger.info("Database error while logging in!");
                invalidLoginLabel.setText("Error while logging in!");
            }
            invalidLoginLabel.setVisible(true);
            usernameTextField.clear();
            loginPasswordField.clear();
        });

        new Thread(loginTask).start();
    }

    private void loadMainView() {
        try {
            String path = "/com/mydigitalmedia/mediaapp/fxml/main-view.fxml";
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(path));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 740);
            scene.getStylesheets().add(Objects.requireNonNull(LoginController.class
                    .getResource("/com/mydigitalmedia/mediaapp/css/main.css")).toExternalForm());
            scene.getStylesheets().add(Objects.requireNonNull(LoginController.class
                    .getResource("/com/mydigitalmedia/mediaapp/css/settings.css")).toExternalForm());
            scene.getStylesheets().add(Objects.requireNonNull(LoginController.class
                    .getResource("/com/mydigitalmedia/mediaapp/css/tasks.css")).toExternalForm());
            scene.getStylesheets().add(Objects.requireNonNull(Objects.requireNonNull(LoginController.class
                    .getResource("/com/mydigitalmedia/mediaapp/css/addTaskModal.css"))).toExternalForm());
            scene.getStylesheets().add(Objects.requireNonNull(Objects.requireNonNull(LoginController.class
                    .getResource("/com/mydigitalmedia/mediaapp/css/showHistoryModal.css"))).toExternalForm());
            scene.getStylesheets().add(Objects.requireNonNull(Objects.requireNonNull(LoginController.class
                    .getResource("/com/mydigitalmedia/mediaapp/css/showTaskModal.css"))).toExternalForm());
            scene.getStylesheets().add(Objects.requireNonNull(Objects.requireNonNull(LoginController.class
                    .getResource("/com/mydigitalmedia/mediaapp/css/campaign.css"))).toExternalForm());
            scene.getStylesheets().add(Objects.requireNonNull(Objects.requireNonNull(LoginController.class
                    .getResource("/com/mydigitalmedia/mediaapp/css/dashboard.css"))).toExternalForm());
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            logger.error("Error while loading main view!");
        }
    }

    private void setLogo(){
        Image image = new Image(Objects.requireNonNull(MainApplication.class
                .getResourceAsStream("/com/mydigitalmedia/mediaapp/images/logo-final.png")));
        imageView.setImage(image);
        imageView.setFitHeight(120);
        imageView.setFitWidth(120);
    }

    @FXML
    private void handleCloseButtonAction() {
        Platform.exit();
    }

    @FXML
    private void handleMinimizeButtonAction() {
        Stage stage = (Stage) minimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    private void setMinimizeButton() {
        String pathToSVG = "src/main/resources/com/mydigitalmedia/mediaapp/svg/minimize.svg";
        Group svg = new Group(createPath(SVGPathExtractor.extractSVGPath(pathToSVG)));

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

    private void setCloseButtonIcon() {
        String pathToSVG = "src/main/resources/com/mydigitalmedia/mediaapp/svg/close.svg";
        Group svg = new Group(createPath(SVGPathExtractor.extractSVGPath(pathToSVG)));

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

    private  SVGPath createPath(String d) {
        SVGPath path = new SVGPath();
        path.getStyleClass().add("svg");
        path.setContent(d);
        return path;
    }

    private void setBackgroundColors(){
        stackPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        stackPane.getStyleClass().add("stack-pane");
        vBox.getStyleClass().add("vbox");
        gridPane.setVgap(0);
        gridPane.setHgap(0);
        gridPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        gridPane.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

        HBox.setMargin(closeButton, new Insets(20, 28, 0, 0));
        HBox.setMargin(minimizeButton, new Insets(20, 7, 0, 0));

    }

    private void createCanvasAnimation(){
        startIconGenerator();
        new AnimationTimer() {
            public void handle(long currentNanoTime) {

                GraphicsContext gc = canvas.getGraphicsContext2D();
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                for (Iterator<FloatingIcon> iterator = floatingIcons.iterator(); iterator.hasNext();) {
                    FloatingIcon floatingIcon = iterator.next();
                    floatingIcon.update();
                    if (floatingIcon.isOffScreen((int) canvas.getWidth(), (int) canvas.getHeight())) {
                        iterator.remove();
                    } else {
                        floatingIcon.draw(gc);
                    }
                }
            }
        }.start();

        canvas.widthProperty().bind(stackPane.widthProperty());
        canvas.heightProperty().bind(stackPane.heightProperty());
        canvas.widthProperty().addListener(event -> generateIcon());
        canvas.heightProperty().addListener(event -> generateIcon());
    }

    private void startIconGenerator() {
        snowflakeGenerator = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if ((floatingIcons.size() < MAX_ICONS)){
                generateIcon();
            }

        }));
        snowflakeGenerator.setCycleCount(Timeline.INDEFINITE);
        snowflakeGenerator.play();
    }

    private void generateIcon() {
        if (floatingIcons.size() < MAX_ICONS) {
            floatingIcons.add(new FloatingIcon(rand.nextDouble() * (canvas.getWidth() - 45), -50));
        }
    }

    private void windowDragAndResize(){
        topLeftBarHbox.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        topLeftBarHbox.setOnMouseDragged(event -> {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        topRightBarHbox.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        topRightBarHbox.setOnMouseDragged(event -> {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

    }

}


