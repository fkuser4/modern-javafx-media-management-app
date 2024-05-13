package com.mydigitalmedia.mediaapp.ui;

import com.mydigitalmedia.mediaapp.MainApplication;
import com.mydigitalmedia.mediaapp.generics.SerializedPair;
import com.mydigitalmedia.mediaapp.model.Task;
import com.mydigitalmedia.mediaapp.threads.CampaignDataRefreshThread;
import com.mydigitalmedia.mediaapp.utils.DatabaseUtils;
import com.mydigitalmedia.mediaapp.utils.FileUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CampaignController {

    @FXML
    public VBox campaignContentVBox;
    @FXML
    public ScrollPane campaignScrollPane;
    @FXML
    public FlowPane campaignFlowPane;

    public static List<VBox> queueRemove = new ArrayList<>();

    public void initialize() {
        campaignScrollPane.setMinHeight(680);
        campaignScrollPane.setMinWidth(800);

        campaignContentVBox.getStyleClass().add("campaignContentVBox");
        campaignScrollPane.getStyleClass().add("campaignScrollPane");

        campaignFlowPane.prefWidthProperty().bind(campaignScrollPane.widthProperty());
        campaignFlowPane.prefHeightProperty().bind(campaignScrollPane.heightProperty());
        campaignFlowPane.getStyleClass().add("campaignFlowPane");


        Platform.runLater(() -> {
            CampaignDataRefreshThread campaignDataRefreshThread = new CampaignDataRefreshThread(MainApplication.user, campaignFlowPane);
            Thread thread = new Thread(campaignDataRefreshThread);
            thread.start();
        });

    }

    public static VBox taskCards(Task task){

        VBox taskCard = new VBox();
        taskCard.setUserData(task);
        taskCard.getStyleClass().add("taskCard");
        taskCard.setMinWidth(300);
        taskCard.setMinHeight(470);
        taskCard.setMaxWidth(300);
        taskCard.setMaxHeight(470);

        Label cardTaskName = new Label(task.getTaskName());
        cardTaskName.getStyleClass().add("cardTaskName");


        Label cardTaskDueDate = new Label("Due to: ");
        cardTaskDueDate.getStyleClass().add("cardTaskLabel");
        Label cardTaskDueDateText = new Label(task.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        cardTaskDueDateText.getStyleClass().add("cardTaskText");
        HBox cardTaskDueDateHbox = new HBox(cardTaskDueDate, cardTaskDueDateText);


        Label cardTaskPriority = new Label("Priority: " );
        cardTaskPriority.getStyleClass().add("cardTaskLabel");
        Label cardTaskPriorityText = new Label(task.getPriority());
        cardTaskPriorityText.getStyleClass().add("cardTaskText");
        HBox cardTaskPriorityHbox = new HBox(cardTaskPriority, cardTaskPriorityText);

        Label cardTaskPlatform = new Label("Platform: ");
        cardTaskPlatform.getStyleClass().add("cardTaskLabel");
        Label cardTaskPlatformText = new Label(task.getPlatform());
        cardTaskPlatformText.getStyleClass().add("cardTaskText");
        HBox cardTaskPlatformHbox = new HBox(cardTaskPlatform, cardTaskPlatformText);

        Label cardTaskDescription = new Label("Description:");
        cardTaskDescription.getStyleClass().add("cardTaskLabel");
        Label cardTaskDescriptionText = new Label(task.getTaskDescription());
        cardTaskDescriptionText.getStyleClass().add("cardTaskText");


        HBox cardButtons = new HBox();
        String selected = "-fx-background-color: rgb(137, 79, 218, 0.5);\n" +
                "    -fx-border-radius: 1;\n" +
                "    -fx-border-color: transparent;\n" +
                "    -fx-text-fill: #ffffff;\n" +
                "    -fx-focus-color: transparent;\n" +
                "    -fx-faint-focus-color: transparent;";
        String notSelected = "-fx-background-color: rgba(45, 45, 45, 0.5);\n" +
                "    -fx-border-radius: 1;\n" +
                "    -fx-border-color: transparent;\n" +
                "    -fx-text-fill: #969393;\n" +
                "    -fx-focus-color: transparent;\n" +
                "    -fx-faint-focus-color: transparent;";

        Button cardOnHold = new Button("On Hold");
        cardOnHold.setMinHeight(40);
        cardOnHold.setMinWidth(125);

        Button cardInProgress = new Button("In Progress");
        cardInProgress.setMinHeight(40);
        cardInProgress.setMinWidth(125);

        if(task.getStatus().equals("On Hold")){
            cardOnHold.setStyle(selected);
            cardInProgress.setStyle(notSelected);
        } else if (task.getStatus().equals("In Progress")){
            cardOnHold.setStyle(notSelected);
            cardInProgress.setStyle(selected);
        } else {
            cardOnHold.setStyle(notSelected);
            cardInProgress.setStyle(notSelected);
        }

        cardOnHold.setOnMouseClicked(event -> {
            cardOnHold.setStyle(selected);
            cardInProgress.setStyle(notSelected);

            Task changedTask = new Task(
                    task.getId(),
                    task.getTaskName(),
                    task.getAssignedTo(),
                    task.getPlatform(),
                    task.getDueDate(),
                    task.getPriority(),
                    "On Hold",
                    task.getTaskDescription()
            );

            Task oldTask = new Task(
                    task.getId(),
                    task.getTaskName(),
                    task.getAssignedTo(),
                    task.getPlatform(),
                    task.getDueDate(),
                    task.getPriority(),
                    task.getStatus(),
                    task.getTaskDescription()
            );

            SerializedPair<Task, Task> taskPair = new SerializedPair<>(oldTask, changedTask, MainApplication.user);

            task.setStatus("On Hold");
            Thread thread2 = new Thread(() -> {
                FileUtils<SerializedPair<Task, Task>> fileUtils = new FileUtils<>();
                fileUtils.addObjectToFile(taskPair, "dat/changes.dat");
            });

            Thread thread1 = new Thread(() -> {
                DatabaseUtils.updateTask(changedTask);

            });

            ExecutorService executorService = Executors.newFixedThreadPool(2);
            executorService.execute(thread2);
            executorService.execute(thread1);
            executorService.shutdown();


        });


        cardInProgress.setOnMouseClicked(event -> {
            cardInProgress.setStyle(selected);
            cardOnHold.setStyle(notSelected);

            Task changedTask = new Task(
                    task.getId(),
                    task.getTaskName(),
                    task.getAssignedTo(),
                    task.getPlatform(),
                    task.getDueDate(),
                    task.getPriority(),
                    "In Progress",
                    task.getTaskDescription()
            );

            Task oldTask = new Task(
                    task.getId(),
                    task.getTaskName(),
                    task.getAssignedTo(),
                    task.getPlatform(),
                    task.getDueDate(),
                    task.getPriority(),
                    task.getStatus(),
                    task.getTaskDescription()
            );

            SerializedPair<Task, Task> taskPair = new SerializedPair<>(oldTask, changedTask, MainApplication.user);
            task.setStatus("In Progress");
            Thread thread2 = new Thread(() -> {
                FileUtils<SerializedPair<Task, Task>> fileUtils = new FileUtils<>();
                fileUtils.addObjectToFile(taskPair, "dat/changes.dat");
            });

            Thread thread1 = new Thread(() -> {
                DatabaseUtils.updateTask(changedTask);

            });

            ExecutorService executorService = Executors.newFixedThreadPool(2);
            executorService.execute(thread2);
            executorService.execute(thread1);
            executorService.shutdown();


        });

        cardButtons.getChildren().addAll(cardOnHold, cardInProgress);


        CheckBox cardCompleted = new CheckBox("Mark as completed");
        cardCompleted.getStyleClass().add("cardTaskCheckBox");
        cardCompleted.setOnAction(event -> {
            if(cardCompleted.isSelected()){
                queueRemove.add(taskCard);
            }
        });

        taskCard.getChildren().addAll(cardTaskName, cardTaskDueDateHbox, cardTaskPriorityHbox, cardTaskPlatformHbox,
                cardTaskDescription, cardTaskDescriptionText, cardButtons, cardCompleted);

        FlowPane.setMargin(taskCard, new Insets(50, 0, 0, 55));

        VBox.setMargin(cardTaskName, new Insets(10, 0, 0, 10));

        HBox.setMargin(cardTaskDueDate, new Insets(13, 0, 0, 10));
        HBox.setMargin(cardTaskPriority, new Insets(20, 0, 0, 10));
        HBox.setMargin(cardTaskPlatform, new Insets(20, 0, 0, 10));

        VBox.setMargin(cardTaskDescription, new Insets(20, 0, 0, 10));
        VBox.setMargin(cardTaskDescriptionText, new Insets(10, 0, 0, 10));
        VBox.setMargin(cardButtons, new Insets(130, 0, 0, 10));
        VBox.setMargin(cardCompleted, new Insets(32, 0, 0, 13));

        HBox.setMargin(cardCompleted, new Insets(0, 0, 0, 10));
        HBox.setMargin(cardTaskDueDateText, new Insets(15, 0, 0, 5));
        HBox.setMargin(cardTaskPriorityText, new Insets(22, 0, 0, 5));
        HBox.setMargin(cardTaskPlatformText, new Insets(22, 0, 0, 5));

        return taskCard;
    }

}
