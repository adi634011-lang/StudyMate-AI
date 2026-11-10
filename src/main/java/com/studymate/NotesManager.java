package com.studymate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class NotesManager {
    private static final String NOTES_FILE = "notes.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final List<Note> notes = new ArrayList<>();

    static {
        loadNotes();
    }

    public static synchronized List<Note> getNotes() {
        return new ArrayList<>(notes);
    }

    public static synchronized void addNote(Note note) {
        if (note != null) {
            notes.add(0, note);
            saveNotes();
        }
    }

    public static synchronized void updateNote(Note updatedNote) {
        if (updatedNote == null) return;
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId().equals(updatedNote.getId())) {
                notes.set(i, updatedNote);
                saveNotes();
                return;
            }
        }
        notes.add(0, updatedNote);
        saveNotes();
    }

    public static synchronized void deleteNote(String id) {
        notes.removeIf(n -> n.getId().equals(id));
        saveNotes();
    }

    public static synchronized int getCount() {
        return notes.size();
    }

    public static synchronized void loadNotes() {
        notes.clear();
        File file = new File(NOTES_FILE);
        if (!file.exists()) {
            seedSampleNotes();
            saveNotes();
            return;
        }

        try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
            Type listType = new TypeToken<List<Note>>() {}.getType();
            List<Note> loaded = gson.fromJson(reader, listType);
            if (loaded != null) {
                notes.addAll(loaded);
            }
        } catch (IOException e) {
            System.err.println("Error reading notes.json: " + e.getMessage());
            seedSampleNotes();
        }
    }

    public static synchronized void saveNotes() {
        try (FileWriter writer = new FileWriter(NOTES_FILE, StandardCharsets.UTF_8)) {
            gson.toJson(notes, writer);
        } catch (IOException e) {
            System.err.println("Error saving notes.json: " + e.getMessage());
        }
    }

    private static void seedSampleNotes() {
        Note n1 = new Note(
                "Introduction to JavaFX",
                "Computer Science",
                "• JavaFX is a modern UI framework for Java.\n• Stage represents the primary window.\n• Scene holds the scene graph with layout nodes.\n• Use VBox and HBox for flexible linear layouts.\n• CSS styles can be applied via -fx-* properties."
        );
        Note n2 = new Note(
                "Effective Study Habits",
                "General",
                "• Pomodoro Technique: 25 minutes of deep focus followed by a 5-minute break.\n• Active Recall: Test yourself rather than re-reading.\n• Spaced Repetition: Review concepts at increasing intervals.\n• Feynman Technique: Explain the concept simply as if teaching someone else."
        );
        notes.add(n1);
        notes.add(n2);
    }
}
