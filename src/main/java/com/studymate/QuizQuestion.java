package com.studymate;

import java.util.ArrayList;
import java.util.List;

public class QuizQuestion {
    private String question;
    private List<String> options;
    private int correctIndex;
    private String explanation;
    private transient int userSelectedIndex = -1;

    public QuizQuestion() {
        this.options = new ArrayList<>();
    }

    public QuizQuestion(String question, List<String> options, int correctIndex, String explanation) {
        this.question = question;
        this.options = options != null ? options : new ArrayList<>();
        this.correctIndex = correctIndex;
        this.explanation = explanation;
        this.userSelectedIndex = -1;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getCorrectIndex() {
        return correctIndex;
    }

    public void setCorrectIndex(int correctIndex) {
        this.correctIndex = correctIndex;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public int getUserSelectedIndex() {
        return userSelectedIndex;
    }

    public void setUserSelectedIndex(int userSelectedIndex) {
        this.userSelectedIndex = userSelectedIndex;
    }

    public boolean isAnswered() {
        return userSelectedIndex >= 0;
    }

    public boolean isCorrect() {
        return userSelectedIndex == correctIndex;
    }
}
