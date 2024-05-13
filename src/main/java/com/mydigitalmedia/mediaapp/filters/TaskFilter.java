package com.mydigitalmedia.mediaapp.filters;

import com.mydigitalmedia.mediaapp.model.User;

import java.time.LocalDate;

public class TaskFilter {

    private String id;
    private String taskName;
    private User assignedTo;
    private String platform;
    private LocalDate dueDate;
    private String priority;
    private String status;
    private String taskDescription;

    private TaskFilter() {
    }


    public static final class TaskFilterBuilder {
        private String id;
        private String taskName;
        private User assignedTo;
        private String platform;
        private LocalDate dueDate;
        private String priority;
        private String status;
        private String taskDescription;

        private TaskFilterBuilder() {
        }

        public static TaskFilterBuilder aTaskFilter() {
            return new TaskFilterBuilder();
        }

        public TaskFilterBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public TaskFilterBuilder withTaskName(String taskName) {
            this.taskName = taskName;
            return this;
        }

        public TaskFilterBuilder withAssignedTo(User assignedTo) {
            this.assignedTo = assignedTo;
            return this;
        }

        public TaskFilterBuilder withPlatform(String platform) {
            this.platform = platform;
            return this;
        }

        public TaskFilterBuilder withDueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public TaskFilterBuilder withPriority(String priority) {
            this.priority = priority;
            return this;
        }

        public TaskFilterBuilder withStatus(String status) {
            this.status = status;
            return this;
        }

        public TaskFilterBuilder withTaskDescription(String taskDescription) {
            this.taskDescription = taskDescription;
            return this;
        }

        public TaskFilter build() {
            TaskFilter taskFilter = new TaskFilter();
            taskFilter.status = this.status;
            taskFilter.taskDescription = this.taskDescription;
            taskFilter.taskName = this.taskName;
            taskFilter.priority = this.priority;
            taskFilter.id = this.id;
            taskFilter.dueDate = this.dueDate;
            taskFilter.assignedTo = this.assignedTo;
            taskFilter.platform = this.platform;
            return taskFilter;
        }


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
}
