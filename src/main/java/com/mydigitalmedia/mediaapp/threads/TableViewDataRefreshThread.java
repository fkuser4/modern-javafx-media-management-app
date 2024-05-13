package com.mydigitalmedia.mediaapp.threads;

import com.mydigitalmedia.mediaapp.MainApplication;
import com.mydigitalmedia.mediaapp.model.Task;
import com.mydigitalmedia.mediaapp.utils.DatabaseUtils;
import javafx.application.Platform;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;


public class TableViewDataRefreshThread implements Runnable {


    private ObservableList<Task> masterData;

    public TableViewDataRefreshThread(ObservableList<Task> masterData) {
        this.masterData = masterData;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                MainApplication.logger.error("Thread interrupted");
            }
            try {
                List<Task> tasks = DatabaseUtils.getAllTasks();
                Platform.runLater(() -> {
                    updateMasterData(tasks);
                });

            } catch (SQLException | IOException e) {
                MainApplication.logger.error("Error while reading tasks from database");
            }
        }
    }

    private void updateMasterData(List<Task> tasks) {
        for (Task task : tasks) {
            if (!masterData.contains(task)) {
                masterData.add(task);
            }
        }
        masterData.removeIf(task -> !tasks.contains(task));
    }
}

