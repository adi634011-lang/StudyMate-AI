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

public class TaskManager {
    private static final String TASKS_FILE = "tasks_data.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final List<StudyTask> tasks = new ArrayList<>();

    static {
        loadTasks();
    }

    public static synchronized List<StudyTask> getTasks() {
        return new ArrayList<>(tasks);
    }

    public static synchronized void addTask(StudyTask task) {
        if (task != null) {
            tasks.add(task);
            saveTasks();
        }
    }

    public static synchronized void addTasks(List<StudyTask> newTasks) {
        if (newTasks != null && !newTasks.isEmpty()) {
            tasks.addAll(newTasks);
            saveTasks();
        }
    }

    public static synchronized void updateTask(StudyTask updatedTask) {
        if (updatedTask == null) return;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId().equals(updatedTask.getId())) {
                tasks.set(i, updatedTask);
                saveTasks();
                return;
            }
        }
    }

    public static synchronized void deleteTask(String id) {
        tasks.removeIf(t -> t.getId().equals(id));
        saveTasks();
    }

    public static synchronized void clearCompleted() {
        tasks.removeIf(StudyTask::isCompleted);
        saveTasks();
    }

    public static synchronized int getTotalCount() {
        return tasks.size();
    }

    public static synchronized int getCompletedCount() {
        return (int) tasks.stream().filter(StudyTask::isCompleted).count();
    }

    public static synchronized void loadTasks() {
        tasks.clear();
        File file = new File(TASKS_FILE);
        if (!file.exists()) {
            seedSampleTasks();
            saveTasks();
            return;
        }

        try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
            Type listType = new TypeToken<List<StudyTask>>() {}.getType();
            List<StudyTask> loaded = gson.fromJson(reader, listType);
            if (loaded != null) {
                tasks.addAll(loaded);
            }
        } catch (IOException e) {
            System.err.println("Error reading tasks_data.json: " + e.getMessage());
            seedSampleTasks();
        }
    }

    public static synchronized void saveTasks() {
        try (FileWriter writer = new FileWriter(TASKS_FILE, StandardCharsets.UTF_8)) {
            gson.toJson(tasks, writer);
        } catch (IOException e) {
            System.err.println("Error saving tasks_data.json: " + e.getMessage());
        }
    }

    private static void seedSampleTasks() {
        tasks.add(new StudyTask("Review Java Object-Oriented Programming concepts", "Computer Science"));
        tasks.add(new StudyTask("Generate a 5-question quiz on Algorithms", "Practice"));
        tasks.add(new StudyTask("Read chapter 4 notes on Data Structures", "Reading"));
    }
}
