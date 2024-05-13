package com.mydigitalmedia.mediaapp.threads;

import com.mydigitalmedia.mediaapp.MainApplication;
import com.mydigitalmedia.mediaapp.generics.SerializedPair;
import com.mydigitalmedia.mediaapp.model.Task;
import com.mydigitalmedia.mediaapp.utils.DatabaseUtils;
import com.mydigitalmedia.mediaapp.utils.FileUtils;
import javafx.scene.control.TableView;

import java.time.LocalDateTime;


public class RemoveTaskFromTableViewThread implements Runnable{
    private final Task task;
    private final TableView<Task> tableView;

    public RemoveTaskFromTableViewThread(Task task, TableView<Task> tableView) {
        this.task = task;
        this.tableView = tableView;
    }

    @Override
    public void run() {
        tableView.getItems().remove(task);
        DatabaseUtils.deleteTask(task);

        FileUtils<SerializedPair<Task, String>> fileUtils = new FileUtils<>();
        SerializedPair<Task, String> serializedPair = new SerializedPair<>(task, "deleted", MainApplication.user);
        fileUtils.addObjectToFile(serializedPair, "dat/deleted.dat");

    }

}
