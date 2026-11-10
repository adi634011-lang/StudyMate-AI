package com.studymate;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

public class TestModel {
    public static void main(String[] args) {
        String key = ConfigManager.getApiKey();
        System.out.println("Key: " + (key != null ? key.substring(0, 5) + "..." : "null"));
        String[] models = {"gemini-3.8-flash", "gemini-flash-latest", "gemini-2.5-flash", "models/gemini-3.8-flash", "gemini-2.0-flash"};
        for (String m : models) {
            try (Client c = Client.builder().apiKey(key).build()) {
                System.out.print("Testing: " + m + " -> ");
                GenerateContentResponse r = c.models.generateContent(m, "Say hello", null);
                System.out.println("SUCCESS! Response: " + r.text().trim());
                return;
            } catch (Exception e) {
                System.out.println("FAILED: " + e.getMessage());
            }
        }
    }
}
