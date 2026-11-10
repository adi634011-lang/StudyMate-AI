package com.studymate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class StudyTask {
    private String id;
    private String title;
    private String category;
    private boolean completed;
    private String date;

    public StudyTask() {
        this.id = UUID.randomUUID().toString();
        this.completed = false;
        this.category = "General";
        this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd"));
    }

    public StudyTask(String title, String category) {
        this();
        this.title = title;
        this.category = category != null ? category : "General";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return (completed ? "[✓] " : "[ ] ") + title;
    }
}
