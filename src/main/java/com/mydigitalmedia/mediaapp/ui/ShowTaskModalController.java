package com.mydigitalmedia.mediaapp.ui;

import com.mydigitalmedia.mediaapp.MainApplication;
import com.mydigitalmedia.mediaapp.generics.SerializedPair;
import com.mydigitalmedia.mediaapp.model.Task;
import com.mydigitalmedia.mediaapp.utils.SVGPathExtractor;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

public class ShowTaskModalController {

    @FXML
    public Pane showTaskMainPane;
    @FXML
    public HBox showTaskTopHbox;
    @FXML
    public VBox showTaskMainVbox;
    @FXML
    public HBox showTaskTitleLabelAndCloseButtonHbox;
    @FXML
    public Label showTaskTitleLabel;
    @FXML
    public Button showTaskCloseButton;
    @FXML
    public HBox showTaskNameAndAssignedToHbox;
    @FXML
    public VBox showTaskNameVbox;
    @FXML
    public Label showTaskNameLabel;
    @FXML
    public HBox showTaskNameHbox;
    @FXML
    public VBox showTaskNameAssignedToVbox;
    @FXML
    public Label showTaskAssignedToLabel;
    @FXML
    public HBox showTaskAssignedToHbox;
    @FXML
    public HBox showTaskDueToAndPlatformHbox;
    @FXML
    public VBox showTaskDueToVbox;
    @FXML
    public Label showTaskDueToLabel;
    @FXML
    public HBox showTaskDueToHbox;
    @FXML
    public VBox showTaskPlatformVbox;
    @FXML
    public Label showTaskPlatformLabel;
    @FXML
    public HBox showTaskPlatformHbox;
    @FXML
    public HBox showTaskPriorityAndStatusHbox;
    @FXML
    public VBox showTaskPriorityVbox;
    @FXML
    public Label showTaskPriorityLabel;
    @FXML
    public HBox showTaskPriorityHbox;
    @FXML
    public VBox showTaskStatusVbox;
    @FXML
    public Label showTaskStatusLabel;
    @FXML
    public HBox showTaskStatusHbox;
    @FXML
    public HBox showTaskDescriptionHbox;
    @FXML
    public VBox showTaskDescriptionVbox;
    @FXML
    public Label showTaskDescriptionLabel;
    @FXML
    public HBox showTaskDescriptionLabelHbox;

    private double xOffset = 0;
    private double yOffset = 0;

    public static ObjectProperty<SerializedPair<Task, ?>> pair = new SimpleObjectProperty<>();
    public static ObjectProperty<Task> task = new SimpleObjectProperty<>();

    @FXML
    public void initialize() {
        showTaskMainPane.getStyleClass().add("showTaskMainPane");
        showTaskMainVbox.getStyleClass().add("showTaskMainVbox");
        showTaskCloseButton.getStyleClass().add("close");

        showTaskMainPane.setMinWidth(1200);
        showTaskMainPane.setMinHeight(740);


        showTaskTopHbox.setMinWidth(1200);
        showTaskTopHbox.setMinHeight(50);

        showTaskMainVbox.setMinWidth(500);
        showTaskMainVbox.setMinHeight(370);
        showTaskMainVbox.setMaxHeight(545);
        showTaskMainVbox.setMaxWidth(500);
        showTaskMainVbox.setLayoutY(85);
        showTaskMainVbox.setLayoutX(375);

        showTaskTitleLabelAndCloseButtonHbox.setMinWidth(700);
        showTaskTitleLabelAndCloseButtonHbox.setMinHeight(100);


        initCloseButton();
        windowDragAndResize();
        initLabels();

        pair.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {

                Object second = newValue.getSecond();
                if (second instanceof Task e){
                    initTextChanges(newValue.getFirst(),(Task) newValue.getSecond());
                }else{
                    initTextNoChanges(newValue.getFirst());
                }
            }
        });

        task.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                initTextNoChanges(newValue);
            }
        });

    }

    @FXML
    public void handleCloseModalAction(){
        StackPane parentStackPane = (StackPane) showTaskMainPane.getScene().getRoot();
        parentStackPane.getChildren().remove(MainApplication.preloadedContent.get("showTaskModal"));


    }

    private void initLabels(){

        showTaskTitleLabel.setText("Task");
        showTaskNameLabel.setText("Name:");
        showTaskAssignedToLabel.setText("Assigned to:");
        showTaskDueToLabel.setText("Due to:");
        showTaskPlatformLabel.setText("Platform:");
        showTaskPriorityLabel.setText("Priority:");
        showTaskStatusLabel.setText("Status:");
        showTaskDescriptionLabel.setText("Description:");

        showTaskTitleLabel.getStyleClass().add("showTaskTitleLabel");
        showTaskNameLabel.getStyleClass().add("showTaskLabels");
        showTaskAssignedToLabel.getStyleClass().add("showTaskLabels");
        showTaskDueToLabel.getStyleClass().add("showTaskLabels");
        showTaskPlatformLabel.getStyleClass().add("showTaskLabels");
        showTaskPriorityLabel.getStyleClass().add("showTaskLabels");
        showTaskStatusLabel.getStyleClass().add("showTaskLabels");
        showTaskDescriptionLabel.getStyleClass().add("showTaskLabels");

        HBox.setMargin(showTaskTitleLabel, new Insets(20, 0, 0, 60));
        VBox.setMargin(showTaskNameLabel, new Insets(0, 0, 8, 60));
        VBox.setMargin(showTaskAssignedToLabel, new Insets(0, 0, 8, 50));
        VBox.setMargin(showTaskDueToLabel, new Insets(0, 0, 8, 60));
        VBox.setMargin(showTaskPlatformLabel, new Insets(0, 0, 8, 50));
        VBox.setMargin(showTaskPriorityLabel, new Insets(0, 0, 8, 60));
        VBox.setMargin(showTaskStatusLabel, new Insets(0, 0, 8, 50));
        VBox.setMargin(showTaskDescriptionLabel, new Insets(0, 0, 8, 60));

    }

    private void initCloseButton() {
        HBox.setMargin(showTaskCloseButton, new Insets(17, 0, 0, 345));
        String pathToSVG = "src/main/resources/com/mydigitalmedia/mediaapp/svg/close.svg";
        Group svg = new Group(createPath(SVGPathExtractor.extractSVGPath(pathToSVG)));

        Bounds bounds = svg.getBoundsInParent();
        double scale = Math.min(12/bounds.getWidth(), 12 / bounds.getHeight());
        svg.setScaleX(scale);
        svg.setScaleY(scale);

        showTaskCloseButton.setId("showTaskCloseButton");
        showTaskCloseButton.setGraphic(svg);
        showTaskCloseButton.setMaxSize(20, 20);
        showTaskCloseButton.setMinSize(20, 20);
        showTaskCloseButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    private void initTextNoChanges(Task task){

        showTaskNameHbox.getChildren().clear();
        showTaskAssignedToHbox.getChildren().clear();
        showTaskDueToHbox.getChildren().clear();
        showTaskPlatformHbox.getChildren().clear();
        showTaskPriorityHbox.getChildren().clear();
        showTaskStatusHbox.getChildren().clear();
        showTaskDescriptionLabelHbox.getChildren().clear();

        Label showTaskNameText = new Label(task.getTaskName());
        showTaskNameHbox.getChildren().add(showTaskNameText);

        Label showTaskAssignedToText = new Label(task.getAssignedTo().getLoginCredentials().getUsername());
        showTaskAssignedToHbox.getChildren().add(showTaskAssignedToText);

        Label showTaskDueToText = new Label(task.getDueDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        showTaskDueToHbox.getChildren().add(showTaskDueToText);

        Label showTaskPlatformText = new Label(task.getPlatform());
        showTaskPlatformHbox.getChildren().add(showTaskPlatformText);

        Label showTaskPriorityText = new Label(task.getPriority());
        showTaskPriorityHbox.getChildren().add(showTaskPriorityText);

        Label showTaskStatusText = new Label(task.getStatus());
        showTaskStatusHbox.getChildren().add(showTaskStatusText);

        Label showTaskDescriptionText = new Label(task.getTaskDescription());
        System.out.println(task.getTaskDescription());
        showTaskDescriptionLabelHbox.getChildren().add(showTaskDescriptionText);

        showTaskNameText.getStyleClass().add("showTaskText");
        showTaskAssignedToText.getStyleClass().add("showTaskText");
        showTaskDueToText.getStyleClass().add("showTaskText");
        showTaskPlatformText.getStyleClass().add("showTaskText");
        showTaskPriorityText.getStyleClass().add("showTaskText");
        showTaskStatusText.getStyleClass().add("showTaskText");
        showTaskDescriptionText.getStyleClass().add("showTaskText");

        VBox.setMargin(showTaskNameHbox, new Insets(0, 0, 15, 60));
        VBox.setMargin(showTaskAssignedToHbox, new Insets(0, 0, 15, 50));
        VBox.setMargin(showTaskDueToHbox, new Insets(0, 0, 15, 60));
        VBox.setMargin(showTaskPlatformHbox, new Insets(0, 0, 15, 50));
        VBox.setMargin(showTaskPriorityHbox, new Insets(0, 0, 15, 60));
        VBox.setMargin(showTaskStatusHbox, new Insets(0, 0, 15, 50));
        VBox.setMargin(showTaskDescriptionLabelHbox, new Insets(0, 0, 15, 60));


    }

    private void initTextChanges(Task oldTask, Task newTask){


        showTaskNameHbox.getChildren().clear();
        showTaskAssignedToHbox.getChildren().clear();
        showTaskDueToHbox.getChildren().clear();
        showTaskPlatformHbox.getChildren().clear();
        showTaskStatusHbox.getChildren().clear();
        showTaskPriorityHbox.getChildren().clear();

        Label showTaskNameText = new Label(newTask.getTaskName());
        showTaskNameHbox.getChildren().add(showTaskNameText);

        if(!oldTask.getTaskName().equals(newTask.getTaskName())) {
            Label showOldTaskNameText = new Label(oldTask.getTaskName());
            showOldTaskNameText.getStyleClass().add("showOldTaskText");
            HBox.setMargin(showOldTaskNameText, new Insets(0, 0, 0, 10));
            showTaskNameHbox.getChildren().add(showOldTaskNameText);
        }

        Label showTaskAssignedToText = new Label(newTask.getAssignedTo().getLoginCredentials().getUsername());
        showTaskAssignedToHbox.getChildren().add(showTaskAssignedToText);

        if(!oldTask.getAssignedTo().getLoginCredentials().getUsername().equals(newTask.getAssignedTo().getLoginCredentials().getUsername())) {
            Label showOldTaskAssignedToText = new Label(oldTask.getAssignedTo().getLoginCredentials().getUsername());
            showOldTaskAssignedToText.getStyleClass().add("showOldTaskText");
            HBox.setMargin(showOldTaskAssignedToText, new Insets(0, 0, 0, 10));
            showTaskAssignedToHbox.getChildren().add(showOldTaskAssignedToText);
        }

        Label showTaskDueToText = new Label(newTask.getDueDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        showTaskDueToHbox.getChildren().add(showTaskDueToText);

        if(!oldTask.getDueDate().equals(newTask.getDueDate())) {
            Label showOldTaskDueToText = new Label(oldTask.getDueDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            showOldTaskDueToText.getStyleClass().add("showOldTaskText");
            HBox.setMargin(showOldTaskDueToText, new Insets(0, 0, 0, 10));
            showTaskDueToHbox.getChildren().add(showOldTaskDueToText);
        }

        Label showTaskPlatformText = new Label(newTask.getPlatform());
        showTaskPlatformHbox.getChildren().add(showTaskPlatformText);

        if(!oldTask.getPlatform().equals(newTask.getPlatform())) {
            Label showOldTaskPlatformText = new Label(oldTask.getPlatform());
            showOldTaskPlatformText.getStyleClass().add("showOldTaskText");
            HBox.setMargin(showOldTaskPlatformText, new Insets(0, 0, 0, 10));
            showTaskPlatformHbox.getChildren().add(showOldTaskPlatformText);
        }

        Label showTaskPriorityText = new Label(newTask.getPriority());
        showTaskPriorityHbox.getChildren().add(showTaskPriorityText);

        if(!oldTask.getPriority().equals(newTask.getPriority())) {
            Label showOldTaskPriorityText = new Label(oldTask.getPriority());
            showOldTaskPriorityText.getStyleClass().add("showOldTaskText");
            HBox.setMargin(showOldTaskPriorityText, new Insets(0, 0, 0, 10));
            showTaskPriorityHbox.getChildren().add(showOldTaskPriorityText);
        }

        Label showTaskStatusText = new Label(newTask.getStatus());
        showTaskStatusHbox.getChildren().add(showTaskStatusText);

        if(!oldTask.getStatus().equals(newTask.getStatus())) {
            Label showOldTaskStatusText = new Label(oldTask.getStatus());
            showOldTaskStatusText.getStyleClass().add("showOldTaskText");
            HBox.setMargin(showOldTaskStatusText, new Insets(0, 0, 0, 10));
            showTaskStatusHbox.getChildren().add(showOldTaskStatusText);
        }

        Label showTaskDescriptionText = new Label(newTask.getTaskDescription());
        showTaskDescriptionLabelHbox.getChildren().add(showTaskDescriptionText);

        if(!oldTask.getTaskDescription().equals(newTask.getTaskDescription())) {
            Label showOldTaskDescriptionText = new Label(oldTask.getTaskDescription());
            showOldTaskDescriptionText.getStyleClass().add("showOldTaskText");
            HBox.setMargin(showOldTaskDescriptionText, new Insets(0, 0, 0, 10));
            showTaskDescriptionLabelHbox.getChildren().add(showOldTaskDescriptionText);
        }

        showTaskNameText.getStyleClass().add("showTaskText");
        showTaskAssignedToText.getStyleClass().add("showTaskText");
        showTaskDueToText.getStyleClass().add("showTaskText");
        showTaskPlatformText.getStyleClass().add("showTaskText");
        showTaskPriorityText.getStyleClass().add("showTaskText");
        showTaskStatusText.getStyleClass().add("showTaskText");
        showTaskDescriptionText.getStyleClass().add("showTaskText");

        VBox.setMargin(showTaskNameHbox, new Insets(0, 0, 15, 60));
        VBox.setMargin(showTaskAssignedToHbox, new Insets(0, 0, 15, 50));
        VBox.setMargin(showTaskDueToHbox, new Insets(0, 0, 15, 60));
        VBox.setMargin(showTaskPlatformHbox, new Insets(0, 0, 15, 50));
        VBox.setMargin(showTaskPriorityHbox, new Insets(0, 0, 15, 60));
        VBox.setMargin(showTaskStatusHbox, new Insets(0, 0, 15, 50));
        VBox.setMargin(showTaskDescriptionLabelHbox, new Insets(0, 0, 15, 60));
    }

    private SVGPath createPath(String d) {
        SVGPath path = new SVGPath();
        path.getStyleClass().add("showTaskCloseButton-svg");
        path.setContent(d);
        return path;
    }

    private void windowDragAndResize(){
        showTaskTopHbox.minWidth(1200);
        showTaskTopHbox.maxHeight(50);


        showTaskTopHbox.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        showTaskTopHbox.setOnMouseDragged(event -> {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        showTaskTopHbox.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        showTaskTopHbox.setOnMouseDragged(event -> {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

    }

}
