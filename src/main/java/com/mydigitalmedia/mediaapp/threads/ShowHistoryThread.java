package com.mydigitalmedia.mediaapp.threads;

import com.mydigitalmedia.mediaapp.generics.SerializedPair;
import com.mydigitalmedia.mediaapp.model.Task;
import com.mydigitalmedia.mediaapp.ui.ShowTaskHistoryModalController;
import com.mydigitalmedia.mediaapp.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class ShowHistoryThread implements Runnable{

    @Override
    public void run() {
        FileUtils<SerializedPair<Task, String>> fileUtils1 = new FileUtils<>();
        FileUtils<SerializedPair<Task, Task>> fileUtils2 = new FileUtils<>();
        FileUtils<SerializedPair<Task, String>> fileUtils3 = new FileUtils<>();

        List<SerializedPair<Task, ?>> serializedPairs = new ArrayList<>();
        serializedPairs.addAll(fileUtils1.deserializeObjectsFromFile("dat/deleted.dat"));
        serializedPairs.addAll(fileUtils2.deserializeObjectsFromFile("dat/changes.dat"));
        serializedPairs.addAll(fileUtils3.deserializeObjectsFromFile("dat/created.dat"));

        ShowTaskHistoryModalController.historyList.set(serializedPairs);
    }

}
