package com.mydigitalmedia.mediaapp.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Serializable {

    private String id;
    private String taskName;
    private User assignedTo;
    private String platform;
    private LocalDate dueDate;
    private String priority;
    private String status;
    private String taskDescription;

    public Task(String id, String taskName, User assignedTo, String platform, LocalDate dueDate, String priority,
                String status, String taskDescription) {
        this.id = id;
        this.taskName = taskName;
        this.assignedTo = assignedTo;
        this.platform = platform;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
        this.taskDescription = taskDescription;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) && Objects.equals(taskName, task.taskName) && Objects.equals(assignedTo, task.assignedTo) && Objects.equals(platform, task.platform) && Objects.equals(dueDate, task.dueDate) && Objects.equals(priority, task.priority) && Objects.equals(status, task.status) && Objects.equals(taskDescription, task.taskDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskName, assignedTo, platform, dueDate, priority, status, taskDescription);
    }


}
