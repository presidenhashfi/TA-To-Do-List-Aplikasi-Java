package model;

import java.time.LocalDate;

public class Task {
    public int id;
    public String taskName;
    public String description;
    public LocalDate deadline;
    public String status;

    public Task(int id, String taskName, String description, LocalDate deadline, String status) {
        this.id = id;
        this.taskName = taskName;
        this.description = description;
        this.deadline = deadline;
        this.status = status;
    }
}
