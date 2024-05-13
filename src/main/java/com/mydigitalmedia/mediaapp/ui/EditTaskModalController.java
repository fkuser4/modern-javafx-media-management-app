package com.mydigitalmedia.mediaapp.ui;

import com.mydigitalmedia.mediaapp.MainApplication;
import com.mydigitalmedia.mediaapp.filters.UserFilter;
import com.mydigitalmedia.mediaapp.generics.SerializedPair;
import com.mydigitalmedia.mediaapp.model.Task;
import com.mydigitalmedia.mediaapp.model.User;
import com.mydigitalmedia.mediaapp.utils.DatabaseUtils;
import com.mydigitalmedia.mediaapp.utils.FileUtils;
import com.mydigitalmedia.mediaapp.utils.SVGPathExtractor;
import com.mydigitalmedia.mediaapp.utils.UniqueIDGenerator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.util.converter.LocalDateStringConverter;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.UnaryOperator;

public class EditTaskModalController {

    @FXML
    public Pane mainPane;
    @FXML
    public VBox editTaskVbox;
    @FXML
    public HBox labelAndCancelHbox;
    @FXML
    public Label modalTitleLabel;
    @FXML
    public HBox platformNameAndAssignedToHbox;
    @FXML
    public VBox platformVbox;
    @FXML
    public Label platformLabel;
    @FXML
    public ComboBox<String> platfromComboBox;
    @FXML
    public VBox assignedToVbox;
    @FXML
    public Label assigenToLabel;
    @FXML
    public ComboBox<String> assignedToComboBox;
    @FXML
    public HBox datesHbox;
    @FXML
    public VBox dueVbox;
    @FXML
    public Label dueLabel;
    @FXML
    public TextField dueTextField;
    @FXML
    public VBox taskNameVbox;
    @FXML
    public Label taskNameLabel;
    @FXML
    public TextField taskNameTextField;
    @FXML
    public HBox priorityAndStatusHbox;
    @FXML
    public VBox priorityVbox;
    @FXML
    public Label priorityLabel;
    @FXML
    public ComboBox<String> priorityComboBox;
    @FXML
    public VBox statusVbox;
    @FXML
    public Label statusLabel;
    @FXML
    public ComboBox<String> statusComboBox;
    @FXML
    public HBox clearFieldsAndEditTaskButtonsHbox;
    @FXML
    public Button clearFieldsButton;
    @FXML
    public Button editTaskButton;
    @FXML
    public HBox topHbox;
    @FXML
    public HBox descriptionHbox;
    @FXML
    public Label descriptionLabel;
    @FXML
    public TextArea descriptionTextArea;
    @FXML
    public VBox descriptionVbox;
    @FXML
    public HBox labelAndCloseHbox;
    @FXML
    public Button closeModalButton;

    private double xOffset = 0;
    private double yOffset = 0;

    public static ObjectProperty<Task> selectedTask = new SimpleObjectProperty<>();


    @FXML
    public void initialize() throws SQLException, IOException {
        windowDragAndResize();
        initMainPane();
        initButtons();

        selectedTask.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Task task = newValue;
                taskNameTextField.setText(task.getTaskName());
                platfromComboBox.getSelectionModel().select(task.getPlatform());
                assignedToComboBox.getSelectionModel().select(task.getAssignedTo().getLoginCredentials().getUsername());
                dueTextField.setText(task.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                priorityComboBox.getSelectionModel().select(task.getPriority());
                statusComboBox.getSelectionModel().select(task.getStatus());
                descriptionTextArea.setText(task.getTaskDescription());
            }
        });
    }

    @FXML
    private void handleClearFieldsAction() {

        platfromComboBox.getSelectionModel().clearSelection();
        assignedToComboBox.getSelectionModel().clearSelection();
        dueTextField.clear();
        priorityComboBox.getSelectionModel().clearSelection();
        statusComboBox.getSelectionModel().clearSelection();
        descriptionTextArea.clear();
    }

    @FXML
    private void handleEditTaskAction() throws SQLException, IOException {

        boolean isAnyFiledEmpty = false;

        if (platfromComboBox.getSelectionModel().isEmpty()) {
            platfromComboBox.getStyleClass().remove("addTaskModal-combo-box");
            platfromComboBox.getStyleClass().add("addTaskModal-combo-box-empty");
            isAnyFiledEmpty = true;
        }

        if (assignedToComboBox.getSelectionModel().isEmpty()) {
            assignedToComboBox.getStyleClass().remove("addTaskModal-combo-box");
            assignedToComboBox.getStyleClass().add("addTaskModal-combo-box-empty");
            isAnyFiledEmpty = true;
        }

        if (dueTextField.getText().isEmpty()) {
            dueTextField.getStyleClass().remove("addTaskModal-text-field");
            dueTextField.getStyleClass().add("addTaskModal-text-field-empty");
            isAnyFiledEmpty = true;
        }

        if (taskNameTextField.getText().isEmpty()) {
            taskNameTextField.getStyleClass().remove("addTaskModal-text-field");
            taskNameTextField.getStyleClass().add("addTaskModal-text-field-empty");
            isAnyFiledEmpty = true;
        }

        if (priorityComboBox.getSelectionModel().isEmpty()) {
            priorityComboBox.getStyleClass().remove("addTaskModal-combo-box");
            priorityComboBox.getStyleClass().add("addTaskModal-combo-box-empty");
            isAnyFiledEmpty = true;
        }

        if (statusComboBox.getSelectionModel().isEmpty()) {
            statusComboBox.getStyleClass().remove("addTaskModal-combo-box");
            statusComboBox.getStyleClass().add("addTaskModal-combo-box-empty");
            isAnyFiledEmpty = true;
        }

        if(!isAnyFiledEmpty){
            String id = selectedTask.get().getId();
            String name = taskNameTextField.getText();
            User assignedTo = DatabaseUtils.getUsersByFilter(UserFilter.UserFilterBuilder.anUserFilter()
                    .withUsername(assignedToComboBox.getSelectionModel().getSelectedItem()).build()).get(0);
            String platform = platfromComboBox.getSelectionModel().getSelectedItem();
            String dateString = dueTextField.getText();
            LocalDate dueDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String priority = priorityComboBox.getSelectionModel().getSelectedItem();
            String status = statusComboBox.getSelectionModel().getSelectedItem();
            String description = descriptionTextArea.getText();
            Task task = new Task(id, name, assignedTo, platform, dueDate, priority, status, description);


            SerializedPair<Task, Task> taskPair = new SerializedPair<>(selectedTask.get(), task, MainApplication.user);

            Thread thread2 = new Thread(() -> {
                FileUtils<SerializedPair<Task, Task>> fileUtils = new FileUtils<>();
                fileUtils.addObjectToFile(taskPair, "dat/changes.dat");
            });

            Thread thread1 = new Thread(() -> {
                DatabaseUtils.updateTask(task);

            });

            ExecutorService executorService = Executors.newFixedThreadPool(2);
            executorService.execute(thread2);
            executorService.execute(thread1);
            executorService.shutdown();

            Scene scene = mainPane.getScene();
            Stage stage = (Stage) scene.getWindow();
            StackPane parentStackPane = (StackPane) stage.getScene().getRoot();
            parentStackPane.getChildren().remove(MainApplication.preloadedContent.get("editTaskModal"));
        }

    }

    @FXML
    private void handleCloseModalAction() {
        Scene scene = mainPane.getScene();
        Stage stage = (Stage) scene.getWindow();
        StackPane parentStackPane = (StackPane) stage.getScene().getRoot();
        parentStackPane.getChildren().remove(MainApplication.preloadedContent.get("editTaskModal"));
    }

    private void initMainPane() throws SQLException, IOException {
        mainPane.getStyleClass().add("main-pane");
        mainPane.setMinSize(1200, 740);


        editTaskVbox.getStyleClass().add("add-task-vbox");
        editTaskVbox.setLayoutX(260);
        editTaskVbox.setLayoutY(50);
        initHboxes();
        initLabels();
        initTextFields();
        setCloseButtonIcon();

    }

    private void initHboxes(){
        labelAndCloseHbox.getStyleClass().add("label-and-cancel-hbox");
        labelAndCloseHbox.setAlignment(Pos.CENTER_RIGHT);

        platformNameAndAssignedToHbox.getStyleClass().add("platform-name-and-assigned-to-hbox");
        platformNameAndAssignedToHbox.setSpacing(20);

        datesHbox.getStyleClass().add("dates-hbox");
        datesHbox.setSpacing(20);

        priorityAndStatusHbox.getStyleClass().add("priority-and-status-hbox");
        priorityAndStatusHbox.setSpacing(20);

        clearFieldsAndEditTaskButtonsHbox.getStyleClass().add("clear-fields-and-create-task-buttons-hbox");
        clearFieldsAndEditTaskButtonsHbox.setSpacing(20);

        descriptionHbox.getStyleClass().add("description-hbox");
        descriptionHbox.setSpacing(20);

    }

    private void initLabels(){
        modalTitleLabel.getStyleClass().add("modal-title-label");
        modalTitleLabel.setText("EDIT TASK");

        platformLabel.getStyleClass().add("addTaskModal-label");
        platformLabel.setText("Platform");

        assigenToLabel.getStyleClass().add("addTaskModal-label");
        assigenToLabel.setText("Assigned to");

        dueLabel.getStyleClass().add("addTaskModal-label");
        dueLabel.setText("Due date");

        taskNameLabel.getStyleClass().add("addTaskModal-label");
        taskNameLabel.setText("Task name");

        priorityLabel.getStyleClass().add("addTaskModal-label");
        priorityLabel.setText("Priority");

        statusLabel.getStyleClass().add("addTaskModal-label");
        statusLabel.setText("Status");

        descriptionLabel.getStyleClass().add("addTaskModal-label");
        descriptionLabel.setText("Description");

        VBox.setMargin(taskNameLabel, new Insets(0, 0, 15, 80));
        VBox.setMargin(assigenToLabel, new Insets(0, 0, 15, 40));
        VBox.setMargin(dueLabel, new Insets(0, 0, 15, 80));
        VBox.setMargin(platformLabel, new Insets(0, 0, 15, 40));
        VBox.setMargin(priorityLabel, new Insets(0, 0, 15, 80));
        VBox.setMargin(statusLabel, new Insets(0, 0, 15, 40));
        VBox.setMargin(descriptionLabel, new Insets(0, 0, 15, 80));
        HBox.setMargin(modalTitleLabel, new Insets(0, 450, 0, 0));
    }

    public void initTextFields() throws SQLException, IOException {
        platfromComboBox.getStyleClass().add("addTaskModal-combo-box");
        platfromComboBox.getItems().addAll("Facebook", "Instagram", "Twitter", "LinkedIn", "Pinterest");
        platfromComboBox.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                platfromComboBox.getStyleClass().remove("addTaskModal-combo-box-empty");
                platfromComboBox.getStyleClass().add("addTaskModal-combo-box");
            }
        });

        assignedToComboBox.getStyleClass().add("addTaskModal-combo-box");
        ObservableList<String> users = FXCollections.observableArrayList(DatabaseUtils.getAllUsers().stream().map(
                (user) -> user.getLoginCredentials().getUsername()).toList());
        assignedToComboBox.getItems().addAll(users);
        assignedToComboBox.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                assignedToComboBox.getStyleClass().remove("addTaskModal-combo-box-empty");
                assignedToComboBox.getStyleClass().add("addTaskModal-combo-box");
            }
        });

        dueTextField.getStyleClass().add("addTaskModal-text-field");
        dueTextField.setPromptText("dd/mm/yyyy");
        dueTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                dueTextField.getStyleClass().remove("addTaskModal-text-field-empty");
                dueTextField.getStyleClass().add("addTaskModal-text-field");
            }
        });

        taskNameTextField.getStyleClass().add("addTaskModal-text-field");
        taskNameTextField.setPromptText("Name");
        taskNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                taskNameTextField.getStyleClass().remove("addTaskModal-text-field-empty");
                taskNameTextField.getStyleClass().add("addTaskModal-text-field");
            }
        });

        priorityComboBox.getStyleClass().add("addTaskModal-combo-box");
        priorityComboBox.getItems().addAll("High", "Medium", "Low");
        priorityComboBox.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                priorityComboBox.getStyleClass().remove("addTaskModal-combo-box-empty");
                priorityComboBox.getStyleClass().add("addTaskModal-combo-box");
            }
        });

        statusComboBox.getStyleClass().add("addTaskModal-combo-box");
        statusComboBox.getItems().addAll( "Planned", "In Progress", "Completed", "On Hold");
        statusComboBox.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                statusComboBox.getStyleClass().remove("addTaskModal-combo-box-empty");
                statusComboBox.getStyleClass().add("addTaskModal-combo-box");
            }
        });

        descriptionTextArea.getStyleClass().add("addTaskModal-text-area");
        descriptionTextArea.setWrapText(true);


        VBox.setMargin(taskNameTextField, new Insets(0, 0, 0, 80));
        VBox.setMargin(assignedToComboBox, new Insets(0, 0, 0, 40));
        VBox.setMargin(dueTextField, new Insets(0, 0, 0, 80));
        VBox.setMargin(platfromComboBox, new Insets(0, 0, 0, 40));
        VBox.setMargin(priorityComboBox, new Insets(0, 0, 0, 80));
        VBox.setMargin(statusComboBox, new Insets(0, 0, 0, 40));
        VBox.setMargin(descriptionTextArea, new Insets(0, 0, 0, 80));


        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        UnaryOperator<TextFormatter.Change> dateFilter = change -> {
            String newText = change.getControlNewText();

            if (change.isAdded()) {
                if (newText.matches("[0-3]?")) {
                    return change;
                } else if (newText.matches("[0-1][0-9]") || newText.matches("2[0-9]") || newText.matches("3[0-1]")) {

                    if (newText.length() == 2) {
                        change.setText(change.getText() + "/");
                        change.setCaretPosition(change.getCaretPosition() + 1);
                        change.setAnchor(change.getAnchor() + 1);
                    }
                    return change;
                } else if (newText.matches("0[1-9]/") || newText.matches("1[0-2]/")) {

                    return change;
                } else if (newText.matches("([0-2][0-9]|3[0-1])/[0-1]?")) {

                    return change;
                } else if (newText.matches("([0-2][0-9]|3[0-1])/0[1-9]") || newText.matches("([0-2][0-9]|3[0-1])/1[0-2]")) {

                    if (newText.length() == 5) {
                        change.setText(change.getText() + "/");
                        change.setCaretPosition(change.getCaretPosition() + 1);
                        change.setAnchor(change.getAnchor() + 1);
                    }
                    return change;
                } else if (newText.matches("([0-2][0-9]|3[0-1])/([0][1-9]|1[0-2])/[0-9]{0,4}")) {

                    return change;
                }
                return null;
            } else if (change.isDeleted()) {

                return change;
            }
            return null;
        };

        TextFormatter<LocalDate> textFormatter = new TextFormatter<>(
                new LocalDateStringConverter(dateFormatter, null),
                null,
                dateFilter);

        dueTextField.setTextFormatter(textFormatter);

    }

    public void initButtons(){
        clearFieldsButton.getStyleClass().add("clearFields-button");
        clearFieldsButton.setText("CLEAR");

        editTaskButton.getStyleClass().add("createTask-button");
        editTaskButton.setText("SAVE CHANGES");

        HBox.setMargin(clearFieldsButton, new Insets(36, 0, 0, 80));
        HBox.setMargin(editTaskButton, new Insets(36, 0, 0, 84));

    }

    private void setCloseButtonIcon() {
        HBox.setMargin(closeModalButton, new Insets(0, 20, 40, 0));
        String pathToSVG = "src/main/resources/com/mydigitalmedia/mediaapp/svg/close.svg";
        Group svg = new Group(createPath(SVGPathExtractor.extractSVGPath(pathToSVG)));

        Bounds bounds = svg.getBoundsInParent();
        double scale = Math.min(12/bounds.getWidth(), 12 / bounds.getHeight());
        svg.setScaleX(scale);
        svg.setScaleY(scale);

        closeModalButton.setId("closeModal-button");
        closeModalButton.setGraphic(svg);
        closeModalButton.setMaxSize(20, 20);
        closeModalButton.setMinSize(20, 20);
        closeModalButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
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
