package com.studymate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Note {
    private String id;
    private String title;
    private String category;
    private String content;
    private String date;

    public Note() {
        this.id = UUID.randomUUID().toString();
        this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
        this.category = "General";
    }

    public Note(String title, String category, String content) {
        this();
        this.title = title;
        this.category = category;
        this.content = content;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return title != null && !title.isBlank() ? title : "Untitled Note";
    }
}
