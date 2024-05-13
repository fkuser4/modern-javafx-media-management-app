package com.mydigitalmedia.mediaapp.ui;

import com.mydigitalmedia.mediaapp.MainApplication;
import com.mydigitalmedia.mediaapp.generics.SerializedPair;
import com.mydigitalmedia.mediaapp.model.Task;
import com.mydigitalmedia.mediaapp.model.User;
import com.mydigitalmedia.mediaapp.utils.SVGPathExtractor;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class ShowTaskHistoryModalController {
    @FXML
    public Pane historyModalMainPane;
    @FXML
    public VBox historyModalMainVbox;
    @FXML
    public HBox historyModalTitleAndCloseHbox;
    @FXML
    public Label historyModalTitleLabel;
    @FXML
    public Button closeHistoryModalButton;
    @FXML
    public ScrollPane historyModalScrollPane;
    @FXML
    public VBox historyModalContentVbox;
    @FXML
    public HBox topHbox;

    private double xOffset = 0;
    private double yOffset = 0;

    public static ObjectProperty<List<SerializedPair<Task, ?>>> historyList = new SimpleObjectProperty<>();

    public void initialize() {
        historyModalMainPane.getStyleClass().add("historyModalMainPane");
        historyModalTitleAndCloseHbox.getStyleClass().add("historyModalTitleAndCloseHbox");
        historyModalTitleLabel.getStyleClass().add("historyModalTitleLabel");
        closeHistoryModalButton.getStyleClass().add("closeModal-button");
        historyModalScrollPane.getStyleClass().add("historyModalScrollPane");
        historyModalContentVbox.getStyleClass().add("historyModalContentVbox");
        topHbox.getStyleClass().add("topHbox");
        historyModalMainVbox.getStyleClass().add("historyModalMainVbox");

        historyModalMainPane.setMinHeight(740);
        historyModalMainPane.setMinWidth(1200);

        topHbox.setMinHeight(30);
        topHbox.setMinWidth(1200);

        historyModalMainVbox.setMinHeight(500);
        historyModalMainVbox.setMinWidth(850);
        historyModalMainVbox.setMaxHeight(500);
        historyModalMainVbox.setMaxWidth(850);

        historyModalMainVbox.setAlignment(Pos.CENTER);
        historyModalMainVbox.setLayoutX(190);
        historyModalMainVbox.setLayoutY(125);

        historyModalTitleAndCloseHbox.setMinHeight(60);
        historyModalTitleAndCloseHbox.setMinWidth(850);


        historyModalScrollPane.setMinHeight(435);
        historyModalScrollPane.setMaxHeight(435);
        historyModalScrollPane.setMinWidth(840);
        historyModalScrollPane.setMaxWidth(840);
        historyModalContentVbox.setMinHeight(435);
        historyModalContentVbox.setMinWidth(840);
        historyModalScrollPane.setMaxWidth(840);

        VBox.setMargin(historyModalContentVbox, new Insets(0, 5, 0, 0));
        VBox.setMargin(historyModalScrollPane, new Insets(0, 5, 0, 0));


        historyModalScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        historyModalScrollPane.setContent(historyModalContentVbox);


        historyModalTitleLabel.setText("TASK HISTORY");
        HBox.setMargin(historyModalTitleLabel, new Insets(20, 0, 0, 23));

        historyList.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                addHistoryItems(newValue);
            }
        });

        historyModalScrollPane.setCache(false);

        for (Node n :  historyModalScrollPane.getChildrenUnmodifiable()) {
            n.setCache(false);
            n.setStyle("-fx-background-color: #222231;");
        }

        windowDragAndResize();
        setCloseButtonIcon();

    }

    @FXML
    private void handleCloseModalAction() {
        StackPane parentStackPane = (StackPane) historyModalMainPane.getScene().getRoot();
        parentStackPane.getChildren().remove(MainApplication.preloadedContent.get("showHistoryModal"));
    }

    private void addHistoryItems(List<SerializedPair<Task, ?>> list){

        Platform.runLater(() -> {

            historyModalContentVbox.getChildren().clear();

            if(Optional.ofNullable(list).isPresent()){
                list.sort((o1, o2) -> {
                    LocalDateTime time1 = o1.getChangesMadeAt();
                    LocalDateTime time2 = o2.getChangesMadeAt();
                    return time2.compareTo(time1);
                });
            }

            for (SerializedPair<Task, ?> pair : list) {
                Task firstTask = pair.getFirst();

                Object second = pair.getSecond();
                if (second instanceof Task e) {
                    historyModalContentVbox.getChildren().add(taskChangedHbox((SerializedPair<Task, Task>) pair));
                } else if (second instanceof String) {
                    historyModalContentVbox.getChildren().add(taskCreatedDeletedHbox((SerializedPair<Task, String>) pair));
                }
            }

            historyModalContentVbox.setMinHeight(60 * list.size());
            historyModalContentVbox.setMaxHeight(60 * list.size());
            historyModalScrollPane.setContent(null);
            historyModalScrollPane.setContent(historyModalContentVbox);
            historyModalScrollPane.requestLayout();
            historyModalContentVbox.requestLayout();
            for (Node n :  historyModalScrollPane.getChildrenUnmodifiable()) {
                n.setCache(false);
                n.setStyle("-fx-background-color: #222231;");
            }
        });
    }

    private HBox taskCreatedDeletedHbox(SerializedPair<Task, String> pair){

        User user = pair.getChangesMadeBy();
        LocalDateTime timestamp = pair.getChangesMadeAt();
        Task task = pair.getFirst();

        HBox hBox = new HBox();
        hBox.getStyleClass().add("taskHistoryHboxItem");
        VBox.setMargin(hBox, new Insets(5, 0, 5, 10));
        hBox.setMinHeight(50);
        hBox.setMinWidth(790);
        hBox.setMaxHeight(50);
        hBox.setMaxWidth(790);

        Label roleLabel = new Label(user.getUserRole().toString());
        HBox.setMargin(roleLabel, new Insets(0, 0, 0, 15));
        roleLabel.getStyleClass().add("taskHistoryLabelItem");
        roleLabel.setMinHeight(50);
        roleLabel.setMinWidth(70);
        roleLabel.setMaxHeight(50);
        roleLabel.setMaxWidth(70);


        Label usernameLabel = new Label(user.getLoginCredentials().getUsername());
        usernameLabel.getStyleClass().add("taskHistoryLabelItem");
        usernameLabel.setMinHeight(50);
        usernameLabel.setMinWidth(80);
        usernameLabel.setMaxHeight(50);
        usernameLabel.setMaxWidth(80);

        Label statusLabel = new Label(pair.getSecond());
        statusLabel.getStyleClass().add("taskHistoryLabelItem");
        statusLabel.setMinHeight(50);
        statusLabel.setMinWidth(140);
        statusLabel.setMaxHeight(50);
        statusLabel.setMaxWidth(140);
        statusLabel.setAlignment(Pos.CENTER);

        Hyperlink taskIdHyperlink = new Hyperlink(task.getId());
        taskIdHyperlink.getStyleClass().add("taskHistoryHyperlinkItem");
        taskIdHyperlink.setMinHeight(50);
        taskIdHyperlink.setMinWidth(320);
        taskIdHyperlink.setMaxHeight(50);
        taskIdHyperlink.setMaxWidth(320);
        HBox.setMargin(taskIdHyperlink, new Insets(0, 0, 0, 25));

        taskIdHyperlink.setOnAction(event -> {
            ShowTaskModalController.pair.set(pair);
            StackPane parentStackPane = (StackPane) historyModalMainPane.getScene().getRoot();
            parentStackPane.getChildren().add(MainApplication.preloadedContent.get("showTaskModal"));
        });

        Label timestampLabel = new Label(timestamp.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")).toString());
        timestampLabel.getStyleClass().add("taskHistoryLabelItem");
        timestampLabel.setMinHeight(50);
        timestampLabel.setMinWidth(220);
        timestampLabel.setMaxHeight(50);
        timestampLabel.setMaxWidth(220);



        hBox.getChildren().addAll(roleLabel, usernameLabel, statusLabel, taskIdHyperlink, timestampLabel);

        hBox.setCache(false);
        roleLabel.setCache(false);
        usernameLabel.setCache(false);
        statusLabel.setCache(false);
        taskIdHyperlink.setCache(false);
        historyModalContentVbox.setCache(false);
        historyModalContentVbox.setCache(false);
        timestampLabel.setCache(false);

        return hBox;
    }

    private HBox taskChangedHbox(SerializedPair<Task, Task> pair){

        User user = pair.getChangesMadeBy();
        LocalDateTime timestamp = pair.getChangesMadeAt();
        Task task = pair.getFirst();
        Task task2 = pair.getSecond();

        HBox hBox = new HBox();
        hBox.getStyleClass().add("taskHistoryHboxItem");
        VBox.setMargin(hBox, new Insets(5, 0, 5, 10));
        hBox.setMinHeight(50);
        hBox.setMinWidth(790);
        hBox.setMaxHeight(50);
        hBox.setMaxWidth(790);

        Label roleLabel = new Label(user.getUserRole().toString());
        HBox.setMargin(roleLabel, new Insets(0, 0, 0, 15));
        roleLabel.getStyleClass().add("taskHistoryLabelItem");
        roleLabel.setMinHeight(50);
        roleLabel.setMinWidth(70);
        roleLabel.setMaxHeight(50);
        roleLabel.setMaxWidth(70);

        Label usernameLabel = new Label(user.getLoginCredentials().getUsername());
        usernameLabel.getStyleClass().add("taskHistoryLabelItem");
        usernameLabel.setMinHeight(50);
        usernameLabel.setMinWidth(80);
        usernameLabel.setMaxHeight(50);
        usernameLabel.setMaxWidth(80);

        Label statusLabel = new Label("made changes in");
        statusLabel.getStyleClass().add("taskHistoryLabelItem");
        statusLabel.setMinHeight(50);
        statusLabel.setMinWidth(140);
        statusLabel.setMaxHeight(50);
        statusLabel.setMaxWidth(140);
        statusLabel.setAlignment(Pos.CENTER);

        Hyperlink taskIdHyperlink = new Hyperlink(task.getId());
        taskIdHyperlink.getStyleClass().add("taskHistoryHyperlinkItem");
        taskIdHyperlink.setMinHeight(50);
        taskIdHyperlink.setMinWidth(320);
        taskIdHyperlink.setMaxHeight(50);
        taskIdHyperlink.setMaxWidth(320);
        HBox.setMargin(taskIdHyperlink, new Insets(0, 0, 0, 25));

        taskIdHyperlink.setOnAction(event -> {
            ShowTaskModalController.pair.set(pair);
            StackPane parentStackPane = (StackPane) historyModalMainPane.getScene().getRoot();
            parentStackPane.getChildren().add(MainApplication.preloadedContent.get("showTaskModal"));
        });

        Label timestampLabel = new Label(timestamp.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")).toString());
        timestampLabel.getStyleClass().add("taskHistoryLabelItem");
        timestampLabel.setMinHeight(50);
        timestampLabel.setMinWidth(220);
        timestampLabel.setMaxHeight(50);
        timestampLabel.setMaxWidth(220);

        hBox.getChildren().addAll(roleLabel, usernameLabel, statusLabel, taskIdHyperlink, timestampLabel);
        hBox.setCache(false);
        roleLabel.setCache(false);
        usernameLabel.setCache(false);
        statusLabel.setCache(false);
        taskIdHyperlink.setCache(false);
        historyModalContentVbox.setCache(false);
        historyModalContentVbox.setCache(false);
        timestampLabel.setCache(false);
        return hBox;
    }

    private void setCloseButtonIcon() {
        HBox.setMargin(closeHistoryModalButton, new Insets(17, 0, 0, 610));
        String pathToSVG = "src/main/resources/com/mydigitalmedia/mediaapp/svg/close.svg";
        Group svg = new Group(createPath(SVGPathExtractor.extractSVGPath(pathToSVG)));

        Bounds bounds = svg.getBoundsInParent();
        double scale = Math.min(12/bounds.getWidth(), 12 / bounds.getHeight());
        svg.setScaleX(scale);
        svg.setScaleY(scale);

        closeHistoryModalButton.setId("closeModal-button");
        closeHistoryModalButton.setGraphic(svg);
        closeHistoryModalButton.setMaxSize(20, 20);
        closeHistoryModalButton.setMinSize(20, 20);
        closeHistoryModalButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    private SVGPath createPath(String d) {
        SVGPath path = new SVGPath();
        path.getStyleClass().add("svg-modal");
        path.setContent(d);
        return path;
    }

    private void windowDragAndResize(){
        topHbox.minWidth(1200);
        topHbox.maxHeight(50);


        topHbox.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        topHbox.setOnMouseDragged(event -> {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        topHbox.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        topHbox.setOnMouseDragged(event -> {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

    }
}
