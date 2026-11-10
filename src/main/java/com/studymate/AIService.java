package com.studymate;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AIService implements AutoCloseable {

    private Client client;
    private static final Gson gson = new Gson();

    public AIService() {
        initClient();
    }

    public synchronized boolean initClient() {
        String apiKey = ConfigManager.getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            closeClientQuietly();
            client = null;
            return false;
        }

        try {
            closeClientQuietly();
            client = Client.builder()
                    .apiKey(apiKey.trim())
                    .build();
            System.out.println("Gemini AI Client initialized successfully.");
            return true;
        } catch (Exception e) {
            System.err.println("Gemini Client initialization failed: " + e.getMessage());
            client = null;
            return false;
        }
    }

    public synchronized boolean isConfigured() {
        return client != null;
    }

    private synchronized void closeClientQuietly() {
        if (client != null) {
            try {
                client.close();
            } catch (Exception ignored) {}
            client = null;
        }
    }

    private String callModelWithFallback(String prompt) throws Exception {
        if (client == null) {
            throw new IllegalStateException("Gemini API is not initialized. Please configure your API key.");
        }

        String primaryModel = ConfigManager.getModel();
        String[] fallbackModels = new String[] {
                primaryModel,
                "gemini-3.5-flash",
                "gemini-3.5-flash-lite",
                "gemini-3-flash-preview"
        };

        Exception lastException = null;

        for (String model : fallbackModels) {
            try {
                GenerateContentResponse response = client.models.generateContent(model, prompt, null);
                if (response != null && response.text() != null) {
                    return response.text();
                }
            } catch (Exception e) {
                lastException = e;
                System.err.println("Model " + model + " failed: " + e.getMessage() + ". Trying next fallback...");
            }
        }

        throw (lastException != null) ? lastException : new RuntimeException("No response received from Gemini.");
    }

    public String askTutor(String question) {
        if (question == null || question.isBlank()) {
            return "Please enter a question for the tutor.";
        }

        String prompt = "You are StudyMate AI, an expert, encouraging, and clear personal study tutor.\n"
                + "Explain the following question thoroughly, clearly, and concisely:\n"
                + "- Start with a brief, clear explanation\n"
                + "- Break down complex ideas using bullet points\n"
                + "- Provide an intuitive real-world analogy or example\n"
                + "- Conclude with a helpful key takeaway or tip\n\n"
                + "Student Question: " + question;

        try {
            return callModelWithFallback(prompt);
        } catch (Exception e) {
            System.err.println("Gemini Tutor Error: " + e.getMessage());
            return "AI Error: " + e.getMessage() + "\n\nPlease verify your API key in API Settings.";
        }
    }

    public List<QuizQuestion> generateQuiz(String topic, String difficulty, int count) throws Exception {
        if (topic == null || topic.isBlank()) {
            throw new IllegalArgumentException("Topic cannot be empty.");
        }

        int qCount = Math.max(1, Math.min(count, 10));
        String diff = (difficulty == null || difficulty.isBlank()) ? "Intermediate" : difficulty;

        String prompt = "You are an educational quiz creator. Generate a " + qCount + "-question multiple choice quiz on the topic: \"" + topic + "\".\n"
                + "Difficulty Level: " + diff + ".\n"
                + "Respond ONLY with a valid JSON array of objects without markdown code blocks, backticks, or other text.\n"
                + "Each object must match this schema:\n"
                + "[\n"
                + "  {\n"
                + "    \"question\": \"Question text here?\",\n"
                + "    \"options\": [\"Option A text\", \"Option B text\", \"Option C text\", \"Option D text\"],\n"
                + "    \"correctIndex\": 0,\n"
                + "    \"explanation\": \"Clear explanation of why this answer is correct.\"\n"
                + "  }\n"
                + "]";

        String rawResponse = callModelWithFallback(prompt);
        String cleanedJson = cleanJsonOutput(rawResponse);

        Type listType = new TypeToken<List<QuizQuestion>>() {}.getType();
        List<QuizQuestion> list = gson.fromJson(cleanedJson, listType);

        if (list == null || list.isEmpty()) {
            throw new RuntimeException("Failed to parse quiz questions from AI response.");
        }

        return list;
    }

    public String generateStudyNotes(String topic) throws Exception {
        if (topic == null || topic.isBlank()) {
            throw new IllegalArgumentException("Topic cannot be empty.");
        }

        String prompt = "You are an expert educator. Create comprehensive, beautifully structured study revision notes on: \"" + topic + "\".\n"
                + "Format the output cleanly using Markdown-style sections:\n"
                + "1. 📌 Core Summary & Definition\n"
                + "2. 🔑 Essential Concepts & Principles (bullet points)\n"
                + "3. 💡 Practical Examples / Applications\n"
                + "4. ⚠️ Common Misunderstandings & Pitfalls\n"
                + "5. 🎯 Quick Exam Review & Memory Checklist\n\n"
                + "Keep explanations crisp, accurate, and easy to memorize.";

        return callModelWithFallback(prompt);
    }

    public String summarizeNotes(String notesText) throws Exception {
        if (notesText == null || notesText.isBlank()) {
            throw new IllegalArgumentException("Notes content cannot be empty.");
        }

        String prompt = "You are a concise study assistant. Summarize the following study notes into high-yield revision bullet points, key terms, and core takeaways:\n\n"
                + notesText;

        return callModelWithFallback(prompt);
    }

    public List<StudyTask> generateStudyPlan(String goal, int days) throws Exception {
        if (goal == null || goal.isBlank()) {
            throw new IllegalArgumentException("Goal cannot be empty.");
        }

        int targetDays = Math.max(1, Math.min(days, 14));

        String prompt = "Generate a structured, actionable " + targetDays + "-day study plan for: \"" + goal + "\".\n"
                + "Respond ONLY with a valid JSON array of objects without markdown fences or extra text.\n"
                + "Schema:\n"
                + "[\n"
                + "  {\n"
                + "    \"title\": \"Day 1: Read introduction and fundamentals of ...\",\n"
                + "    \"category\": \"Reading\"\n"
                + "  }\n"
                + "]";

        String rawResponse = callModelWithFallback(prompt);
        String cleanedJson = cleanJsonOutput(rawResponse);

        Type listType = new TypeToken<List<StudyTask>>() {}.getType();
        List<StudyTask> tasks = gson.fromJson(cleanedJson, listType);

        if (tasks == null || tasks.isEmpty()) {
            throw new RuntimeException("Failed to parse study tasks from AI response.");
        }

        return tasks;
    }

    public static String testApiKey(String apiKey, String model) throws Exception {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("API Key cannot be blank.");
        }

        String modelToTest = (model == null || model.isBlank()) ? "gemini-3.5-flash" : model;

        try (Client testClient = Client.builder().apiKey(apiKey.trim()).build()) {
            GenerateContentResponse response = testClient.models.generateContent(
                    modelToTest,
                    "Respond with exactly: OK",
                    null
            );

            if (response != null && response.text() != null) {
                return "Connection successful! Gemini model (" + modelToTest + ") is active and responsive.";
            } else {
                throw new RuntimeException("Received empty response from Gemini.");
            }
        }
    }

    private String cleanJsonOutput(String text) {
        if (text == null) return "[]";
        String clean = text.trim();
        if (clean.startsWith("```json")) {
            clean = clean.substring(7);
        } else if (clean.startsWith("```")) {
            clean = clean.substring(3);
        }
        if (clean.endsWith("```")) {
            clean = clean.substring(0, clean.length() - 3);
        }
        clean = clean.trim();
        int firstBracket = clean.indexOf('[');
        int lastBracket = clean.lastIndexOf(']');
        if (firstBracket >= 0 && lastBracket > firstBracket) {
            clean = clean.substring(firstBracket, lastBracket + 1);
        }
        return clean;
    }

    @Override
    public void close() {
        closeClientQuietly();
        System.out.println("Gemini AI connection closed.");
    }
}
