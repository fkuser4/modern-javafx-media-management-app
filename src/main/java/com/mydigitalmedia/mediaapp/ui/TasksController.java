package com.mydigitalmedia.mediaapp.ui;

import com.mydigitalmedia.mediaapp.MainApplication;
import com.mydigitalmedia.mediaapp.model.Task;
import com.mydigitalmedia.mediaapp.model.User;
import com.mydigitalmedia.mediaapp.threads.RemoveTaskFromTableViewThread;
import com.mydigitalmedia.mediaapp.threads.ShowHistoryThread;
import com.mydigitalmedia.mediaapp.threads.TableViewDataRefreshThread;
import com.mydigitalmedia.mediaapp.utils.SVGPathExtractor;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TasksController {

    @FXML
    private HBox controlsHbox;
    @FXML
    private FlowPane filterFlowPane;
    @FXML
    private HBox rightHbox;
    @FXML
    private Button addTaskButton;
    @FXML
    private Button showHistoryButton;
    @FXML
    private TextField filterTextField;
    @FXML
    private TableView<Task> tasksTableView;
    @FXML
    private TableColumn<Task, String> taskNameColumn;
    @FXML
    private TableColumn<Task, String> platformColumn;
    @FXML
    private TableColumn<Task, String> statusColumn;
    @FXML
    private TableColumn<Task, String> priorityColumn;
    @FXML
    private TableColumn<Task, User> assignedToColumn;
    @FXML
    private TableColumn<Task, LocalDate> dueDateColumn;
    @FXML
    private TableColumn<Task, Void> actionsColumn;
    @FXML
    private VBox contentVBox;

    private Button filterButton = new Button();

    private List<String> platfromList = new ArrayList<>(List.of("Facebook", "Instagram", "Twitter", "LinkedIn", "Pinterest"));
    private List<String> statusList = new ArrayList<>(List.of("Planned", "In Progress", "Completed", "On Hold"));
    private List<String> priorityList = new ArrayList<>(List.of("High", "Medium", "Low"));

    private List<String> filterPlatformList = new ArrayList<>();
    private List<String> filterStatusList = new ArrayList<>();
    private List<String> filterPriorityList = new ArrayList<>();

    private String searchFilter = "";

    public static int numberOfFilters = 0;
    private FilteredList<Task> filteredData;


    @FXML
    private void initialize() {


        Platform.runLater(
                () -> {
                    VBox.setMargin(controlsHbox, new Insets(30, 40, 0, 30));
                    VBox.setMargin(tasksTableView, new Insets(40, 40, 0, 30));
                    setFilterButton();
                    initFilters();
                    initRightHbox();
                    initTableView();

                }
        );

    }

    @FXML
    private void handleAddTaskAction() {

        StackPane parentStackPane = (StackPane) addTaskButton.getScene().getRoot();
        parentStackPane.getChildren().add(MainApplication.preloadedContent.get("addTaskModal"));

        //try {
        //    addTaskModalNode = FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource(path)));
        //    parentStackPane.getChildren().add(addTaskModalNode);
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
    }

    @FXML
    private void handleShowHistoryAction() {
        Thread thread = new Thread(new ShowHistoryThread());
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(thread);

        StackPane parentStackPane = (StackPane) addTaskButton.getScene().getRoot();
        parentStackPane.getChildren().add(MainApplication.preloadedContent.get("showHistoryModal"));

    }

    private void initFilters() {
        HBox.setMargin(filterFlowPane, new Insets(0, 0, 0, 0));
        filterFlowPane.getChildren().add(0, filterButton);
        filterButton.setAlignment(Pos.CENTER_RIGHT);

    }

    private void setFilterButton() {
        HBox content = new HBox();
        content.setAlignment(Pos.CENTER);
        Label plusLabel = new Label("+");
        plusLabel.getStyleClass().add("plus-symbol");
        Label filterTextLabel = new Label("Add a filter");
        filterTextLabel.getStyleClass().add("filter-button-text-label");
        HBox.setMargin(filterTextLabel, new Insets(0, 0, 0,5));
        content.getChildren().addAll(plusLabel, filterTextLabel);
        filterButton.setGraphic(content);
        filterButton.getStyleClass().add("filter-button");

        filterButton.setOnMouseClicked(event -> {
            if (numberOfFilters < 12){
                filterFactory();
                numberOfFilters++;
            }
        });


        //FlowPane.setMargin(filterButton, new Insets(0, 0, 10, 0));

    }

    private void filterFactory(){

        HBox hbox = new HBox();
        //FlowPane.setMargin(hbox, new Insets(0, 0, 10, 10));
        filterFlowPane.setHgap(9);
        filterFlowPane.setVgap(9);
        hbox.setSpacing(5);
        hbox.setAlignment(Pos.CENTER);
        ComboBox<String> comboBox1 = new ComboBox<>();
        HBox.setMargin(comboBox1, new Insets(0, 0, 0, 10));
        comboBox1.getStyleClass().add("filter-combo-box");
        List<String> filterList = new ArrayList<>();
        if(!platfromList.isEmpty()){
            filterList.add("Platform");
        }
        if(!statusList.isEmpty()){
            filterList.add("Status");
        }
        if(!priorityList.isEmpty()){
            filterList.add("Priority");
        }
        comboBox1.getItems().addAll(filterList);

        comboBox1.setOnAction(e -> {
            String selectedItem = comboBox1.getSelectionModel().getSelectedItem();
            Label label = new Label(selectedItem + ":");
            HBox.setMargin(label, new Insets(0, 0, 0, 0));
            label.getStyleClass().add("filter-label");
            hbox.getChildren().remove(comboBox1);
            hbox.getChildren().add(label);

            ComboBox<String> comboBox2 = new ComboBox<>();
            HBox.setMargin(comboBox2, new Insets(0, 0, 0, 10));
            comboBox2.getStyleClass().add("filter-combo-box");
            if (selectedItem.equals("Platform")) {
                comboBox2.getItems().addAll(FXCollections.observableArrayList(platfromList.stream().sorted().toList()));
            } else if (selectedItem.equals("Status")) {
                comboBox2.getItems().addAll(FXCollections.observableArrayList(statusList.stream().sorted().toList()));
            } else if (selectedItem.equals("Priority")) {
                comboBox2.getItems().addAll(FXCollections.observableArrayList(priorityList.stream().sorted().toList()));
            }
            hbox.getChildren().add(comboBox2);

            Button button = new Button("Remove");
            HBox.setMargin(button, new Insets(0, 0, 0, 5));
            String pathToSVG = "src/main/resources/com/mydigitalmedia/mediaapp/svg/close.svg";
            Group svg = new Group(createPathControl(SVGPathExtractor.extractSVGPath(pathToSVG), "svg-filter"));

            Bounds bounds = svg.getBoundsInParent();
            double scale = Math.min(9/bounds.getWidth(), 9 / bounds.getHeight());
            svg.setScaleX(scale);
            svg.setScaleY(scale);

            button.setId("filter-remove-button");
            button.setGraphic(svg);
            button.setMaxSize(5, 5);
            button.setMinSize(5, 5);
            button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

            comboBox2.setOnAction(ev -> {
                String selectedItem2 = comboBox2.getSelectionModel().getSelectedItem();
                Label label2 = new Label(selectedItem2);
                HBox.setMargin(label2, new Insets(0, 0, 0, 0));
                label2.getStyleClass().add("filter-label");
                hbox.getChildren().remove(comboBox2);
                hbox.getChildren().add(label2);
                hbox.getChildren().add(button);

                if (selectedItem.equals("Platform")) {
                    platfromList.remove(selectedItem2);
                    filterPlatformList.add(selectedItem2);
                } else if (selectedItem.equals("Status")) {
                    statusList.remove(selectedItem2);
                    filterStatusList.add(selectedItem2);
                } else if (selectedItem.equals("Priority")) {
                    priorityList.remove(selectedItem2);
                    filterPriorityList.add(selectedItem2);
                }

                button.setOnAction(ev2 -> {
                    filterFlowPane.getChildren().remove(hbox);
                    if (selectedItem.equals("Platform")) {
                        platfromList.add(selectedItem2);
                        filterPlatformList.remove(selectedItem2);
                    } else if (selectedItem.equals("Status")) {
                        statusList.add(selectedItem2);
                        filterStatusList.remove(selectedItem2);
                    } else if (selectedItem.equals("Priority")) {
                        priorityList.add(selectedItem2);
                        filterPriorityList.remove(selectedItem2);
                    }

                    numberOfFilters--;


                });

                hbox.getStyleClass().add("filter-hbox");

                filterTableViewData();
            });

        });

        hbox.getChildren().add(comboBox1);
        filterFlowPane.getChildren().add(hbox);

    }

    private SVGPath createPathControl(String d, String css) {
        SVGPath path = new SVGPath();
        path.getStyleClass().add(css);
        path.setContent(d);
        return path;
    }

    private void initRightHbox(){
        rightHbox.getStyleClass().add("right-hbox");
        rightHbox.setAlignment(Pos.CENTER_RIGHT);
        HBox.setMargin(addTaskButton ,new Insets(0, 8, 0, 0));
        HBox.setMargin(showHistoryButton ,new Insets(0, 16, 0, 0));

        setAddTaskButton();
        setShowHistoryButton();
        setFilterTextFiled();
    }

    private void setAddTaskButton() {
        String pathToSVG = "src/main/resources/com/mydigitalmedia/mediaapp/svg/plus.svg";
        SVGPath path = new SVGPath();
        path.getStyleClass().add("svg-rightHbox");
        path.setContent(SVGPathExtractor.extractSVGPath(pathToSVG));
        Group svg = new Group(path);

        Bounds bounds = svg.getBoundsInParent();
        double scale = Math.min(16/bounds.getWidth(), 16 / bounds.getHeight());
        svg.setScaleX(scale);
        svg.setScaleY(scale);

        addTaskButton.setId("addTask-button");
        addTaskButton.setGraphic(svg);
        addTaskButton.setMaxSize(28, 28);
        addTaskButton.setMinSize(28, 28);
        addTaskButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    private void setShowHistoryButton() {
        String pathToSVG = "src/main/resources/com/mydigitalmedia/mediaapp/svg/history.svg";
        SVGPath path = new SVGPath();
        path.getStyleClass().add("svg-rightHbox");
        path.setContent(SVGPathExtractor.extractSVGPath(pathToSVG));
        Group svg = new Group(path);

        Bounds bounds = svg.getBoundsInParent();
        double scale = Math.min(16/bounds.getWidth(), 16/bounds.getHeight());
        svg.setScaleX(scale);
        svg.setScaleY(scale);

        showHistoryButton.setId("showHistory-button");
        showHistoryButton.setGraphic(svg);
        showHistoryButton.setMaxSize(28, 28);
        showHistoryButton.setMinSize(28, 28);
        showHistoryButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    private void setFilterTextFiled(){
        filterTextField.getStyleClass().add("filter-text-field");
        filterTextField.setPromptText("Search");
        filterTextField.setAlignment(Pos.CENTER_LEFT);
        filterTextField.setPadding(new Insets(0, 0, 0, 10));

        filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchFilter = newValue;
            filterTableViewData();
        });

    }

    private void initTableView(){
        tasksTableView.getStyleClass().add("tasks-table-view");
        tasksTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        Label tableViewPlaceholderLabel = new Label("No tasks to display");
        tableViewPlaceholderLabel.getStyleClass().add("table-view-placeholder-label");
        tasksTableView.setPlaceholder(tableViewPlaceholderLabel);

        taskNameColumn.setText("NAME");
        platformColumn.setText("PLATFORM");
        statusColumn.setText("STATUS");
        priorityColumn.setText("PRIORITY");
        assignedToColumn.setText("ASSIGNED TO");
        dueDateColumn.setText("DUE DATE");
        actionsColumn.setText("");

        taskNameColumn.setResizable(false);
        platformColumn.setResizable(false);
        statusColumn.setResizable(false);
        priorityColumn.setResizable(false);
        assignedToColumn.setResizable(false);
        dueDateColumn.setResizable(false);
        actionsColumn.setResizable(false);


        platformColumn.setCellValueFactory(new PropertyValueFactory<>("platform"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        assignedToColumn.setCellValueFactory(new PropertyValueFactory<>("assignedTo"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        taskNameColumn.setCellValueFactory(new PropertyValueFactory<>("taskName"));

        tasksTableView.getColumns().forEach(column -> {
            column.setSortable(false);
        });

        System.out.println(tasksTableView.getWidth());

        taskNameColumn.setPrefWidth(186);
        platformColumn.setPrefWidth(150);
        statusColumn.setPrefWidth(150);
        priorityColumn.setPrefWidth(150);
        assignedToColumn.setPrefWidth(150);
        dueDateColumn.setPrefWidth(150);
        actionsColumn.setPrefWidth(120);

        tasksTableView.setSelectionModel(null);

        actionsColumn.setCellFactory(cell -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            private final Button editButton = new Button("Edit");
            private final StackPane pane = new StackPane(deleteButton, editButton);

            {
                getStyleClass().add("table-cell");
                deleteButton.getStyleClass().add("delete-button");
                editButton.getStyleClass().add("edit-button");
                pane.getStyleClass().add("actions-pane");
                deleteButton.setOnAction(event -> {
                    Task task = getTableView().getItems().get(getIndex());
                    getTableView().getItems().remove(task);
                });
                editButton.setOnAction(event -> {
                    Task task = getTableView().getItems().get(getIndex());
                    System.out.println("Edit task: " + task);
                });
            }

        });

        dueDateColumn.setCellFactory(column -> new TableCell<Task, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    String formattedDate = item.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    setText(formattedDate);
                }
            }
        });

        assignedToColumn.setCellFactory(column -> new TableCell<Task, User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getLoginCredentials().getUsername());
                }
            }
        });

        priorityColumn.setCellFactory(column -> new TableCell<Task, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("-fx-text-fill: #ffffff");
                } else {
                    setText(item);
                    switch (item) {
                        case "High":
                            //setStyle("-fx-text-fill: #d74646");
                            break;
                        case "Medium":
                            //setStyle("-fx-text-fill: #69e056");
                            break;
                        case "Low":
                            //setStyle("-fx-text-fill: #637fde");
                            break;
                        default:
                            //setStyle("-fx-text-fill: #ffffff");
                            break;
                    }
                }
            }
        });

        statusColumn.setCellFactory(column -> new TableCell<Task, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("-fx-text-fill: #ffffff");
                } else {
                    setText(item);
                    switch (item) {
                        case "Completed":
                            //setStyle("-fx-text-fill: #17a600");
                            break;
                        case "On Hold":
                            //setStyle("-fx-text-fill: #fc9811");
                            break;
                        case "In Progress":
                            //setStyle("-fx-text-fill: #f3e42e");
                            break;
                        case "Palnned":
                            //setStyle("-fx-text-fill: #ffffff");
                            break;
                        default:
                            //setStyle("-fx-text-fill: #ffffff");
                            break;
                    }
                }
            }
        });

        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button deleteTaskButton = new Button("Delete");
            private final Button editTaskButton = new Button("Edit");
            private final HBox hbox = new HBox(editTaskButton, deleteTaskButton);

            {
                getStyleClass().add("table-cell");
                setEditTaskButton(editTaskButton);
                setDeleteTaskButton(deleteTaskButton);
                hbox.getStyleClass().add("actions-hbox");
                HBox.setMargin(deleteTaskButton, new Insets(0, 0, 0, 16));
                HBox.setMargin(editTaskButton, new Insets(0, 0, 0, 15));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Task currentTask = getTableView().getItems().get(getIndex());
                    editTaskButton.setUserData(currentTask);
                    deleteTaskButton.setUserData(currentTask);
                    setGraphic(hbox);
                }
            }
        });

        tasksTableView.setRowFactory(tv -> new TableRow<Task>() {
            {
                setOnMouseClicked(event -> {
                    if (!isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                        Task clickedTask = getItem();
                        ShowTaskModalController.task.set(clickedTask);
                        StackPane parentStackPane = (StackPane) getScene().getRoot();
                        parentStackPane.getChildren().add(MainApplication.preloadedContent.get("showTaskModal"));
                    }
                });
            }

            @Override
            protected void updateItem(Task item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                    getStyleClass().removeAll("table-row");
                } else {
                    if (!getStyleClass().contains("table-row")) {
                        getStyleClass().add("table-row");
                    }
                    setStyle("-fx-background-color: #2f2b4d;");
                }
            }
        });

        refreshData();

    }

    private void refreshData() {
        ObservableList<Task> masterData = FXCollections.observableArrayList(new ArrayList<>());
        Thread tableViewDataRefreshThread = new Thread(new TableViewDataRefreshThread(masterData));
        filteredData = new FilteredList<>(masterData , p -> true);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(tableViewDataRefreshThread);
        tasksTableView.setItems(filteredData);
    }

    private void filterTableViewData(){
        filteredData.setPredicate(task ->
                ((filterPlatformList.isEmpty() || filterPlatformList.contains(task.getPlatform())) &&
                        (filterStatusList.isEmpty() || filterStatusList.contains(task.getStatus())) &&
                        (filterPriorityList.isEmpty() || filterPriorityList.contains(task.getPriority()))) &&
                        (searchFilter.isEmpty() ||
                                task.getTaskName().toLowerCase().contains(searchFilter.toLowerCase()) ||
                                task.getPlatform().toLowerCase().contains(searchFilter.toLowerCase()) ||
                                task.getStatus().toLowerCase().contains(searchFilter.toLowerCase()) ||
                                task.getPriority().toLowerCase().contains(searchFilter.toLowerCase()) ||
                                task.getAssignedTo().getLoginCredentials().getUsername().toLowerCase()
                                        .contains(searchFilter.toLowerCase())));
    }

    private void setDeleteTaskButton(Button button){
        button.getStyleClass().add("deleteTask-button");

        String pathToSVG = "src/main/resources/com/mydigitalmedia/mediaapp/svg/delete.svg";
        Group svg = new Group(createPathControl(SVGPathExtractor.extractSVGPath(pathToSVG), "svg-deleteTask"));

        Bounds bounds = svg.getBoundsInParent();
        double scale = Math.min(15/bounds.getWidth(), 15 / bounds.getHeight());
        svg.setScaleX(scale);
        svg.setScaleY(scale);

        button.setGraphic(svg);
        button.setMaxSize(10, 10);
        button.setMinSize(10, 10);
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        button.setOnAction(event -> {
            Task task = (Task) button.getUserData();
            if (Optional.ofNullable(task).isPresent()){

                StackPane parentStackPane = (StackPane) button.getScene().getRoot();
                Pane modal = new Pane();
                parentStackPane.getChildren().add(modal);
                modal.minWidth(parentStackPane.getWidth());
                modal.minHeight(parentStackPane.getHeight());
                modal.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

                Optional<ButtonType> result = confirmDeletionDialog();

                parentStackPane.getChildren().remove(modal);

                if (result.isPresent()){
                    if (result.get().getText().equals("Delete")) {

                        Thread thread = new Thread(new RemoveTaskFromTableViewThread(task, tasksTableView));

                        ExecutorService executorService = Executors.newSingleThreadExecutor();
                        executorService.execute(thread);

                    }else {
                        System.out.println("Cancel deletion");
                    }
                }

            };
        });
    }

    private void setEditTaskButton(Button button){
        button.getStyleClass().add("editTask-button");

        String pathToSVG = "src/main/resources/com/mydigitalmedia/mediaapp/svg/edit.svg";
        Group svg = new Group(createPathControl(SVGPathExtractor.extractSVGPath(pathToSVG), "svg-editTask"));

        Bounds bounds = svg.getBoundsInParent();
        double scale = Math.min(15/bounds.getWidth(), 15 / bounds.getHeight());
        svg.setScaleX(scale);
        svg.setScaleY(scale);

        button.setGraphic(svg);
        button.setMaxSize(10, 10);
        button.setMinSize(10, 10);
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        button.setOnAction(event -> {
            Task task = (Task) button.getUserData();
            if (Optional.ofNullable(task).isPresent()){
                Platform.runLater(() -> {
                    Task selectedTask = (Task) button.getUserData();
                    EditTaskModalController.selectedTask.set(selectedTask);
                    Scene scene = button.getScene();
                    Stage stage = (Stage) scene.getWindow();
                    StackPane parentStackPane = (StackPane) stage.getScene().getRoot();
                    parentStackPane.getChildren().add(MainApplication.preloadedContent.get("editTaskModal"));

                });
            };
        });
    }

    private Optional<ButtonType> confirmDeletionDialog(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setResizable(false);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(addTaskButton.getScene().getWindow());

        ButtonType deleteButtonType = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(deleteButtonType, cancelButtonType);

        VBox contentBox = new VBox(10);

        Label confirmationTitleLabel = new Label("Delete Task?");
        Label confirmationLabel = new Label("You will permanently delete this task.");

        confirmationTitleLabel.getStyleClass().add("confirmation-title-label");
        confirmationLabel.getStyleClass().add("confirmation-label");
        dialog.getDialogPane().setStyle("-fx-background-color: #1f1f27;");

        VBox.setMargin(confirmationTitleLabel, new Insets(15, 0, 0, 15));
        VBox.setMargin(confirmationLabel, new Insets(17, 0, 0, 17));

        contentBox.getChildren().addAll(confirmationTitleLabel, confirmationLabel);
        dialog.getDialogPane().setContent(contentBox);

        Scene scene = dialog.getDialogPane().getScene();
        scene.getStylesheets().add(Objects.requireNonNull(MainApplication.class
                .getResource("/com/mydigitalmedia/mediaapp/css/dialog.css")).toExternalForm());

        Stage stage = (Stage) scene.getWindow();
        stage.initStyle(StageStyle.UNDECORATED);

        dialog.getDialogPane().setMinHeight(204);
        dialog.getDialogPane().setMinWidth(500);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(deleteButtonType);
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);

        okButton.getStyleClass().add("ok-button");
        cancelButton.getStyleClass().add("cancel-button");

        Platform.runLater(() -> {

            stage.setY(stage.getY() - 50);
            stage.setX(stage.getX() - 50);
        });

        return dialog.showAndWait();
    }

}
