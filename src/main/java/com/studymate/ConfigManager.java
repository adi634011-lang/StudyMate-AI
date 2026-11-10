package com.studymate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

public class ConfigManager {

    private static final String CONFIG_FILE = "config.properties";
    private static final String ENV_FILE = ".env";
    private static final String DEFAULT_MODEL = "gemini-3.5-flash";

    private static String cachedApiKey = null;
    private static String cachedModel = DEFAULT_MODEL;

    static {
        loadConfig();
    }

    public static synchronized void loadConfig() {
        // 1. Try reading config.properties
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            try (FileInputStream in = new FileInputStream(file)) {
                Properties props = new Properties();
                props.load(in);
                String key = props.getProperty("GOOGLE_API_KEY");
                if (key != null && !key.isBlank()) {
                    cachedApiKey = key.trim();
                }
                String model = props.getProperty("GEMINI_MODEL");
                if (model != null && !model.isBlank()) {
                    cachedModel = model.trim();
                }
            } catch (IOException e) {
                System.err.println("Error reading config.properties: " + e.getMessage());
            }
        }

        // 2. Try reading .env if not found yet
        if (cachedApiKey == null || cachedApiKey.isBlank()) {
            File envFile = new File(ENV_FILE);
            if (envFile.exists()) {
                try {
                    List<String> lines = Files.readAllLines(envFile.toPath());
                    for (String line : lines) {
                        line = line.trim();
                        if (line.startsWith("GOOGLE_API_KEY=")) {
                            cachedApiKey = line.substring("GOOGLE_API_KEY=".length()).trim();
                        } else if (line.startsWith("GEMINI_API_KEY=")) {
                            cachedApiKey = line.substring("GEMINI_API_KEY=".length()).trim();
                        } else if (line.startsWith("GEMINI_MODEL=")) {
                            cachedModel = line.substring("GEMINI_MODEL=".length()).trim();
                        }
                    }
                } catch (IOException ignored) {}
            }
        }

        // 3. Fallback to system environment variables
        if (cachedApiKey == null || cachedApiKey.isBlank()) {
            String envKey = System.getenv("GOOGLE_API_KEY");
            if (envKey == null || envKey.isBlank()) {
                envKey = System.getenv("GEMINI_API_KEY");
            }
            if (envKey != null && !envKey.isBlank()) {
                cachedApiKey = envKey.trim();
            }
        }
    }

    public static synchronized String getApiKey() {
        if (cachedApiKey == null || cachedApiKey.isBlank()) {
            loadConfig();
        }
        return cachedApiKey;
    }

    public static synchronized void saveApiKey(String apiKey) {
        cachedApiKey = apiKey == null ? "" : apiKey.trim();
        saveConfig();
    }

    public static synchronized String getModel() {
        return cachedModel == null || cachedModel.isBlank() ? DEFAULT_MODEL : cachedModel;
    }

    public static synchronized void saveModel(String model) {
        cachedModel = model == null || model.isBlank() ? DEFAULT_MODEL : model.trim();
        saveConfig();
    }

    public static synchronized boolean hasApiKey() {
        String key = getApiKey();
        return key != null && !key.isBlank();
    }

    private static synchronized void saveConfig() {
        Properties props = new Properties();
        if (cachedApiKey != null) {
            props.setProperty("GOOGLE_API_KEY", cachedApiKey);
        }
        props.setProperty("GEMINI_MODEL", getModel());

        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(out, "StudyMate AI Configuration");
        } catch (IOException e) {
            System.err.println("Failed to save config.properties: " + e.getMessage());
        }
    }
}
