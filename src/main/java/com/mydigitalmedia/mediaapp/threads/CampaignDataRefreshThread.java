package com.mydigitalmedia.mediaapp.threads;

import com.mydigitalmedia.mediaapp.MainApplication;
import com.mydigitalmedia.mediaapp.filters.TaskFilter;
import com.mydigitalmedia.mediaapp.generics.SerializedPair;
import com.mydigitalmedia.mediaapp.model.Task;
import com.mydigitalmedia.mediaapp.model.User;
import com.mydigitalmedia.mediaapp.ui.CampaignController;
import com.mydigitalmedia.mediaapp.utils.DatabaseUtils;
import com.mydigitalmedia.mediaapp.utils.FileUtils;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class CampaignDataRefreshThread implements Runnable{

    User user;
    FlowPane campaignFlowPane;

    public CampaignDataRefreshThread(User user, FlowPane campaignFlowPane) {
        this.user = user;
        this.campaignFlowPane = campaignFlowPane;
    }

    @Override
    public void run() {

        TaskFilter taskFilter = TaskFilter.TaskFilterBuilder.aTaskFilter().withAssignedTo(user).build();
        List<Task> currentTasks = new ArrayList<>();

        List<VBox> cards = new ArrayList<>();

        boolean initial = false;

        try {
            while(!Thread.currentThread().isInterrupted()){
                List<Task> taskList = DatabaseUtils.getTasksByFilter(taskFilter).stream().filter(
                        task -> task.getStatus().equals("In Progress") || task.getStatus().equals("Planned")
                                || task.getStatus().equals("On Hold") || task.getStatus().equals("Completed")).toList();

                if(!initial){
                    initial = true;
                    if (!taskList.isEmpty()) {
                        currentTasks.addAll(taskList);
                        for(Task task : currentTasks){
                            VBox card = CampaignController.taskCards(task);
                            cards.add(card);
                            campaignFlowPane.getChildren().add(card);
                        }
                    }
                } else {
                    if (!taskList.isEmpty()){
                        List<Task> taskListCopy = new ArrayList<>(taskList);
                        for(Task task : taskListCopy){
                            if(!currentTasks.stream().map(Task::getId).toList().contains(task.getId())){
                                currentTasks.add(task);
                                VBox card = CampaignController.taskCards(task);
                                cards.add(card);
                                Platform.runLater(() -> campaignFlowPane.getChildren().add(card));
                            }
                        }
                    }
                }

                List<Task> currentTasksCopy = new ArrayList<>(currentTasks);
                for(Task task : currentTasksCopy){
                    Task dbTask = taskList.stream().filter(t -> t.getId().equals(task.getId())).findFirst().orElse(null);
                    if(dbTask != null && dbTask.getStatus().equals("Completed")){
                        currentTasks.remove(task);
                        VBox cardToRemove = cards.stream().filter(
                                card -> ((Task) card.getUserData()).getId().equals(task.getId())).toList().getFirst();
                        Platform.runLater(() -> campaignFlowPane.getChildren().remove(cardToRemove));
                        cards.remove(cardToRemove);
                    } else if(dbTask != null && !dbTask.getStatus().equals("Completed") && !taskList.stream().map(Task::getId).toList().contains(task.getId())){
                        currentTasks.add(task);
                        VBox card = CampaignController.taskCards(task);
                        cards.add(card);
                        Platform.runLater(() -> campaignFlowPane.getChildren().add(card));
                    }
                }

                if(!CampaignController.queueRemove.isEmpty()){
                    for(VBox card : CampaignController.queueRemove){

                        Platform.runLater(()->{
                            campaignFlowPane.getChildren().remove(card);
                        });

                        cards.remove(card);
                        Task changedTask = new Task(
                                ((Task) card.getUserData()).getId(),
                                ((Task) card.getUserData()).getTaskName(),
                                ((Task) card.getUserData()).getAssignedTo(),
                                ((Task) card.getUserData()).getPlatform(),
                                ((Task) card.getUserData()).getDueDate(),
                                ((Task) card.getUserData()).getPriority(),
                                "Completed",
                                ((Task) card.getUserData()).getTaskDescription()
                        );

                        SerializedPair<Task, Task> serializedPair = new SerializedPair<>(
                                (Task) card.getUserData(), changedTask, MainApplication.user);

                        FileUtils<SerializedPair<Task, Task>> fileUtils = new FileUtils<>();
                        fileUtils.addObjectToFile(serializedPair, "dat/changes.dat");
                        DatabaseUtils.updateTask(changedTask);
                    }
                    currentTasks.clear();
                    for(VBox card : cards){
                        currentTasks.add((Task) card.getUserData());
                    }

                    CampaignController.queueRemove.clear();
                }

                for(Task task : taskList){
                    for(Task currentTask : currentTasks){
                        checkCardChanges(task, currentTask, cards);
                    }
                }

                Iterator<Task> iterator = currentTasks.iterator();
                while (iterator.hasNext()) {
                    Task task = iterator.next();
                    if (!taskList.stream().anyMatch(t -> t.getId().equals(task.getId()))) {
                        iterator.remove();
                        VBox cardToRemove = cards.stream()
                                .filter(card -> ((Task) card.getUserData()).getId().equals(task.getId()))
                                .findFirst().orElse(null);
                        if (cardToRemove != null) {
                            Platform.runLater(() -> campaignFlowPane.getChildren().remove(cardToRemove));
                            cards.remove(cardToRemove);
                        }
                    }
                }

                Thread.sleep(2000);

            }
        } catch (IOException | InterruptedException | SQLException e) {
            Thread.currentThread().interrupt();
        }

    }

    private void checkCardChanges(Task task, Task currentTask, List<VBox> cards){
        if(task.getId().equals(currentTask.getId())){
            if(!task.getTaskName().equals(currentTask.getTaskName())){
                Platform.runLater(()->{
                    for(VBox card : cards){
                        if(((Task) card.getUserData()).getId().equals(task.getId())){
                            ((Label) card.getChildren().get(0)).setText(task.getTaskName());
                            ((Task) card.getUserData()).setTaskName(task.getTaskName());
                        }
                    }
                });
            }
        }

        if(!task.getDueDate().isEqual(currentTask.getDueDate())){
            Platform.runLater(()->{
                for(VBox card : cards){
                    if(((Task) card.getUserData()).getId().equals(task.getId())){
                        ((Label) ((HBox) card.getChildren().get(1)).getChildren().get(1))
                                .setText(task.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                        ((Task) card.getUserData()).setDueDate(task.getDueDate());
                    }
                }
            });
        }

        if (!task.getPriority().equals(currentTask.getPriority())){
            Platform.runLater(()->{
                for(VBox card : cards){
                    if(((Task) card.getUserData()).getId().equals(task.getId())){
                        ((Label) ((HBox) card.getChildren().get(2)).getChildren().get(1))
                                .setText(task.getPriority());
                        ((Task) card.getUserData()).setPriority(task.getPriority());
                    }
                }
            });
        }

        if (!task.getPlatform().equals(currentTask.getPlatform())){
            Platform.runLater(()->{
                for(VBox card : cards){
                    if(((Task) card.getUserData()).getId().equals(task.getId())){
                        ((Label) ((HBox) card.getChildren().get(3)).getChildren().get(1))
                                .setText(task.getPlatform());
                        ((Task) card.getUserData()).setPlatform(task.getPlatform());
                    }
                }
            });
        }

        if (!task.getTaskDescription().equals(currentTask.getTaskDescription())){

            Platform.runLater(()->{
                for(VBox card : cards){
                    if(((Task) card.getUserData()).getId().equals(task.getId())){
                        ((Label)card.getChildren().get(5)).setText(task.getTaskDescription());
                        ((Task) card.getUserData()).setTaskDescription(task.getTaskDescription());
                    }
                }
            });
        }

        if (!task.getStatus().equals(currentTask.getStatus())){
            Platform.runLater(()->{
                for(VBox card : cards){
                    if(((Task) card.getUserData()).getId().equals(task.getId())){
                        if (task.getStatus().equals("On Hold")){
                            ((Button)((HBox) card.getChildren().get(6)).getChildren().get(0)).setStyle("-fx-background-color: rgb(137, 79, 218, 0.5);\n" +
                                    "    -fx-border-radius: 1;\n" +
                                    "    -fx-border-color: transparent;\n" +
                                    "    -fx-text-fill: #ffffff;\n" +
                                    "    -fx-focus-color: transparent;\n" +
                                    "    -fx-faint-focus-color: transparent;");
                            ((Button)((HBox) card.getChildren().get(6)).getChildren().get(1)).setStyle("-fx-background-color: rgba(45, 45, 45, 0.5);\n" +
                                    "    -fx-border-radius: 1;\n" +
                                    "    -fx-border-color: transparent;\n" +
                                    "    -fx-text-fill: #969393;\n" +
                                    "    -fx-focus-color: transparent;\n" +
                                    "    -fx-faint-focus-color: transparent;");
                            ((Task) card.getUserData()).setStatus(task.getStatus());
                        } else if (task.getStatus().equals("In Progress")){
                            ((Button)((HBox) card.getChildren().get(6)).getChildren().get(0)).setStyle("-fx-background-color: rgba(45, 45, 45, 0.5);\n" +
                                    "    -fx-border-radius: 1;\n" +
                                    "    -fx-border-color: transparent;\n" +
                                    "    -fx-text-fill: #969393;\n" +
                                    "    -fx-focus-color: transparent;\n" +
                                    "    -fx-faint-focus-color: transparent;");
                            ((Button)((HBox) card.getChildren().get(6)).getChildren().get(1)).setStyle("-fx-background-color: rgb(137, 79, 218, 0.5);\n" +
                                    "    -fx-border-radius: 1;\n" +
                                    "    -fx-border-color: transparent;\n" +
                                    "    -fx-text-fill: #ffffff;\n" +
                                    "    -fx-focus-color: transparent;\n" +
                                    "    -fx-faint-focus-color: transparent;");
                            ((Task) card.getUserData()).setStatus(task.getStatus());
                        }

                        else if (task.getStatus().equals("Planned")){
                            ((Button)((HBox) card.getChildren().get(6)).getChildren().get(0)).setStyle("-fx-background-color: rgba(45, 45, 45, 0.5);\n" +
                                    "    -fx-border-radius: 1;\n" +
                                    "    -fx-border-color: transparent;\n" +
                                    "    -fx-text-fill: #969393;\n" +
                                    "    -fx-focus-color: transparent;\n" +
                                    "    -fx-faint-focus-color: transparent;");
                            ((Button)((HBox) card.getChildren().get(6)).getChildren().get(1)).setStyle("-fx-background-color: rgba(45, 45, 45, 0.5);\n" +
                                    "    -fx-border-radius: 1;\n" +
                                    "    -fx-border-color: transparent;\n" +
                                    "    -fx-text-fill: #969393;\n" +
                                    "    -fx-focus-color: transparent;\n" +
                                    "    -fx-faint-focus-color: transparent;");
                            ((Task) card.getUserData()).setStatus(task.getStatus());
                        }

                    }
                }
            });
        }

    }
}
