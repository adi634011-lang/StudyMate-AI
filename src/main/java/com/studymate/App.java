package com.studymate;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    private BorderPane root;
    private Stage primaryStage;

    private Button selectedButton;
    private Button dashboardButton;
    private Button tutorButton;
    private Button notesButton;
    private Button quizButton;
    private Button plannerButton;
    private Button settingsButton;

    private Label statusBadge;
    private AIService aiService;

    // Stat trackers
    private int quizzesCompleted = 0;

    // Active Note Tracker
    private Note activeNote = null;

    // STYLING CONSTANTS
    private static final String SIDEBAR_BG = "-fx-background-color: #0F172A;";
    private static final String CONTENT_BG = "-fx-background-color: #0B1120;";
    private static final String CARD_BG = "-fx-background-color: #1E293B; -fx-background-radius: 10; -fx-border-color: #334155; -fx-border-radius: 10;";
    private static final String PRIMARY_BTN = "-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 18;";
    private static final String SECONDARY_BTN = "-fx-background-color: #334155; -fx-text-fill: #E2E8F0; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 16;";
    private static final String ACCENT_PURPLE_BTN = "-fx-background-color: #7C3AED; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 16;";

    private static final String INPUT_STYLE = "-fx-background-color: #1E293B; -fx-text-fill: white; -fx-prompt-text-fill: #64748B; -fx-border-color: #475569; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8 12;";

    private final String normalNavStyle =
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #CBD5E1;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 12 16;" +
            "-fx-background-radius: 8;" +
            "-fx-alignment: CENTER_LEFT;";

    private final String hoverNavStyle =
            "-fx-background-color: #1E293B;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 12 16;" +
            "-fx-background-radius: 8;" +
            "-fx-alignment: CENTER_LEFT;";

    private final String selectedNavStyle =
            "-fx-background-color: #2563EB;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12 16;" +
            "-fx-background-radius: 8;" +
            "-fx-alignment: CENTER_LEFT;";

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.root = new BorderPane();

        aiService = new AIService();

        root.setLeft(createSidebar());
        root.setStyle(CONTENT_BG);

        showDashboard();

        Scene scene = new Scene(root, 1150, 750);

        stage.setTitle("StudyMate AI - Intelligent Learning Assistant");
        stage.setMinWidth(900);
        stage.setMinHeight(620);
        stage.setScene(scene);
        stage.show();
    }

    // ==========================================
    // SIDEBAR & NAVIGATION
    // ==========================================

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20, 15, 20, 15));
        sidebar.setPrefWidth(240);
        sidebar.setStyle(SIDEBAR_BG);

        // Logo
        VBox logoBox = new VBox(3);
        Label logo = new Label("StudyMate AI");
        logo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        logo.setStyle("-fx-text-fill: #38BDF8;");

        Label tag = new Label("Personal Learning Copilot");
        tag.setFont(Font.font("Segoe UI", 11));
        tag.setStyle("-fx-text-fill: #94A3B8;");
        logoBox.getChildren().addAll(logo, tag);

        Separator sep1 = new Separator();
        sep1.setStyle("-fx-background-color: #334155;");

        Label menuHeader = new Label("MAIN MENU");
        menuHeader.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
        menuHeader.setStyle("-fx-text-fill: #64748B; -fx-padding: 5 0 2 5;");

        dashboardButton = createNavButton("Dashboard", this::showDashboard);
        tutorButton = createNavButton("AI Tutor", this::showTutor);
        notesButton = createNavButton("My Notes", this::showNotes);
        quizButton = createNavButton("Quiz Generator", this::showQuiz);
        plannerButton = createNavButton("Study Planner", this::showPlanner);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Status badge
        statusBadge = new Label();
        statusBadge.setFont(Font.font("Segoe UI", 12));
        statusBadge.setCursor(Cursor.HAND);
        statusBadge.setOnMouseClicked(e -> showApiSettingsDialog());
        updateStatusBadge();

        settingsButton = createNavButton("API Settings", this::showApiSettingsDialog);

        Label footer = new Label("v1.0 • Powered by Gemini");
        footer.setFont(Font.font("Segoe UI", 11));
        footer.setStyle("-fx-text-fill: #64748B; -fx-padding: 5 0 0 5;");

        sidebar.getChildren().addAll(
                logoBox,
                sep1,
                menuHeader,
                dashboardButton,
                tutorButton,
                notesButton,
                quizButton,
                plannerButton,
                spacer,
                statusBadge,
                settingsButton,
                footer
        );

        return sidebar;
    }

    private Button createNavButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(normalNavStyle);
        btn.setCursor(Cursor.HAND);

        btn.setOnMouseEntered(e -> {
            if (btn != selectedButton) {
                btn.setStyle(hoverNavStyle);
            }
        });

        btn.setOnMouseExited(e -> {
            if (btn != selectedButton) {
                btn.setStyle(normalNavStyle);
            }
        });

        btn.setOnAction(e -> {
            setSelectedButton(btn);
            action.run();
        });

        return btn;
    }

    private void setSelectedButton(Button button) {
        if (selectedButton != null) {
            selectedButton.setStyle(normalNavStyle);
        }
        selectedButton = button;
        if (selectedButton != null) {
            selectedButton.setStyle(selectedNavStyle);
        }
    }

    private void updateStatusBadge() {
        if (aiService != null && aiService.isConfigured()) {
            statusBadge.setText("● Gemini: Active (" + ConfigManager.getModel() + ")");
            statusBadge.setStyle("-fx-text-fill: #10B981; -fx-padding: 6 10; -fx-background-color: #064E3B; -fx-background-radius: 6;");
        } else {
            statusBadge.setText("▲ API Key Required (Click to set)");
            statusBadge.setStyle("-fx-text-fill: #F59E0B; -fx-padding: 6 10; -fx-background-color: #451A03; -fx-background-radius: 6;");
        }
    }

    private VBox createPageContainer(String title, String subtitle) {
        VBox container = new VBox(18);
        container.setPadding(new Insets(25, 30, 25, 30));
        container.setStyle(CONTENT_BG);

        VBox headerBox = new VBox(4);
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        titleLabel.setStyle("-fx-text-fill: #F8FAFC;");

        Label subLabel = new Label(subtitle);
        subLabel.setFont(Font.font("Segoe UI", 13));
        subLabel.setStyle("-fx-text-fill: #94A3B8;");

        headerBox.getChildren().addAll(titleLabel, subLabel);
        container.getChildren().add(headerBox);

        return container;
    }

    // ==========================================
    // 1. DASHBOARD PAGE
    // ==========================================

    private void showDashboard() {
        setSelectedButton(dashboardButton);
        VBox page = createPageContainer("Dashboard", "Welcome back to StudyMate AI! Ready to master your subjects?");

        // Key Metric Cards
        HBox statsRow = new HBox(15);
        statsRow.setAlignment(Pos.CENTER_LEFT);

        int totalNotes = NotesManager.getCount();
        int pendingTasks = TaskManager.getTotalCount() - TaskManager.getCompletedCount();
        String aiStatusText = aiService.isConfigured() ? "Connected" : "Key Needed";

        VBox noteCard = createStatCard("Notes Saved", String.valueOf(totalNotes), "#3B82F6");
        VBox quizCard = createStatCard("Quizzes Completed", String.valueOf(quizzesCompleted), "#8B5CF6");
        VBox taskCard = createStatCard("Pending Tasks", String.valueOf(pendingTasks), "#F59E0B");
        VBox aiCard = createStatCard("AI Status", aiStatusText, aiService.isConfigured() ? "#10B981" : "#EF4444");

        HBox.setHgrow(noteCard, Priority.ALWAYS);
        HBox.setHgrow(quizCard, Priority.ALWAYS);
        HBox.setHgrow(taskCard, Priority.ALWAYS);
        HBox.setHgrow(aiCard, Priority.ALWAYS);

        statsRow.getChildren().addAll(noteCard, quizCard, taskCard, aiCard);

        // Quick Actions Grid
        Label quickLabel = new Label("Quick Actions");
        quickLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 17));
        quickLabel.setStyle("-fx-text-fill: #E2E8F0; -fx-padding: 10 0 0 0;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        VBox tutorAction = createActionCard(
                "Ask AI Tutor",
                "Instant, clear answers to complex study questions with simple analogies.",
                "#2563EB",
                () -> tutorButton.fire()
        );

        VBox quizAction = createActionCard(
                "Generate Practice Quiz",
                "Create interactive multiple-choice tests on any topic with AI.",
                "#7C3AED",
                () -> quizButton.fire()
        );

        VBox notesAction = createActionCard(
                "Create Study Note",
                "Draft, organize, and use AI to summarize revision outlines.",
                "#059669",
                () -> notesButton.fire()
        );

        VBox plannerAction = createActionCard(
                "Plan Study Session",
                "Organize tasks or let AI build an actionable daily study breakdown.",
                "#D97706",
                () -> plannerButton.fire()
        );

        grid.add(tutorAction, 0, 0);
        grid.add(quizAction, 1, 0);
        grid.add(notesAction, 0, 1);
        grid.add(plannerAction, 1, 1);

        // Daily Learning Tip Card
        VBox tipCard = new VBox(8);
        tipCard.setPadding(new Insets(16));
        tipCard.setStyle("-fx-background-color: #1E293B; -fx-background-radius: 8; -fx-border-color: #38BDF8; -fx-border-radius: 8;");

        Label tipTitle = new Label("★ Daily Study Tip: Active Recall & Spaced Repetition");
        tipTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        tipTitle.setStyle("-fx-text-fill: #38BDF8;");

        Label tipDesc = new Label("Instead of passively reading notes, test yourself frequently using the Quiz Generator or AI Tutor. Review concepts after 1 day, 3 days, and 7 days to lock them into long-term memory.");
        tipDesc.setWrapText(true);
        tipDesc.setFont(Font.font("Segoe UI", 13));
        tipDesc.setStyle("-fx-text-fill: #CBD5E1;");

        tipCard.getChildren().addAll(tipTitle, tipDesc);

        page.getChildren().addAll(statsRow, quickLabel, grid, tipCard);

        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #0B1120; -fx-background-color: transparent;");
        root.setCenter(scroll);
    }

    private VBox createStatCard(String title, String value, String accentColor) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(16, 20, 16, 20));
        card.setStyle(CARD_BG);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", 12));
        titleLabel.setStyle("-fx-text-fill: #94A3B8;");

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        valueLabel.setStyle("-fx-text-fill: " + accentColor + ";");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private VBox createActionCard(String title, String desc, String btnColor, Runnable action) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(18));
        card.setStyle(CARD_BG);
        card.setCursor(Cursor.HAND);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        titleLabel.setStyle("-fx-text-fill: #F1F5F9;");

        Label descLabel = new Label(desc);
        descLabel.setFont(Font.font("Segoe UI", 12));
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-text-fill: #94A3B8;");

        Button goBtn = new Button("Open →");
        goBtn.setStyle("-fx-background-color: " + btnColor + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 6 14;");
        goBtn.setOnAction(e -> action.run());

        card.setOnMouseClicked(e -> action.run());
        card.getChildren().addAll(titleLabel, descLabel, goBtn);
        return card;
    }

    // ==========================================
    // 2. AI TUTOR PAGE
    // ==========================================

    private void showTutor() {
        setSelectedButton(tutorButton);
        VBox page = createPageContainer("AI Tutor", "Ask questions, understand complex topics, or ask for simple analogies.");

        // Suggested Chips
        HBox chipsBox = new HBox(8);
        chipsBox.setAlignment(Pos.CENTER_LEFT);
        Label chipLabel = new Label("Suggested:");
        chipLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 11px;");
        chipsBox.getChildren().add(chipLabel);

        String[] samplePrompts = {
                "Explain Object-Oriented Programming simply",
                "How does Binary Search work?",
                "Effective tips for exam preparation",
                "Explain Photosynthesis"
        };

        TextArea chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
        chatArea.setPrefHeight(400);
        chatArea.setStyle("-fx-control-inner-background: #1E293B; -fx-text-fill: #F1F5F9; -fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-background-radius: 8;");
        chatArea.setText("StudyMate AI: Hello! I'm your AI study tutor powered by Gemini.\n" +
                "Ask me any question from your syllabus, or choose one of the suggested topics above.\n\n");

        TextField questionField = new TextField();
        questionField.setPromptText("Type your question here (e.g. 'Explain recursion with a real-life example')...");
        questionField.setStyle(INPUT_STYLE);
        HBox.setHgrow(questionField, Priority.ALWAYS);

        Button sendButton = new Button("Ask Tutor");
        sendButton.setStyle(PRIMARY_BTN);
        sendButton.setCursor(Cursor.HAND);

        for (String p : samplePrompts) {
            Button chip = new Button(p);
            chip.setStyle("-fx-background-color: #1E293B; -fx-text-fill: #93C5FD; -fx-font-size: 11px; -fx-background-radius: 12; -fx-border-color: #3B82F6; -fx-border-radius: 12; -fx-padding: 3 10;");
            chip.setCursor(Cursor.HAND);
            chip.setOnAction(e -> {
                questionField.setText(p);
                sendButton.fire();
            });
            chipsBox.getChildren().add(chip);
        }

        Label loadingLabel = new Label("");
        loadingLabel.setFont(Font.font("Segoe UI", 12));
        loadingLabel.setStyle("-fx-text-fill: #38BDF8;");

        sendButton.setOnAction(e -> {
            String question = questionField.getText().trim();
            if (question.isEmpty()) return;

            if (!aiService.isConfigured()) {
                chatArea.appendText("\n[!] Gemini API Key is required. Please set your API key in API Settings.\n");
                showApiSettingsDialog();
                return;
            }

            chatArea.appendText("\nYou: " + question + "\n\n");
            questionField.clear();
            sendButton.setDisable(true);
            questionField.setDisable(true);
            loadingLabel.setText("Thinking... Consulting Gemini...");

            Task<String> task = new Task<>() {
                @Override
                protected String call() {
                    return aiService.askTutor(question);
                }
            };

            task.setOnSucceeded(ev -> {
                chatArea.appendText("StudyMate AI:\n" + task.getValue() + "\n\n" + "-".repeat(50) + "\n\n");
                sendButton.setDisable(false);
                questionField.setDisable(false);
                loadingLabel.setText("");
                questionField.requestFocus();
            });

            task.setOnFailed(ev -> {
                chatArea.appendText("StudyMate AI Error: " + task.getException().getMessage() + "\n\n");
                sendButton.setDisable(false);
                questionField.setDisable(false);
                loadingLabel.setText("");
            });

            new Thread(task).start();
        });

        questionField.setOnAction(e -> sendButton.fire());

        Button clearButton = new Button("Clear Chat");
        clearButton.setStyle(SECONDARY_BTN);
        clearButton.setOnAction(e -> chatArea.clear());

        HBox inputBox = new HBox(10);
        inputBox.getChildren().addAll(questionField, sendButton, clearButton);

        page.getChildren().addAll(chipsBox, chatArea, loadingLabel, inputBox);

        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #0B1120; -fx-background-color: transparent;");
        root.setCenter(scroll);
    }

    // ==========================================
    // 3. MY NOTES PAGE
    // ==========================================

    private void showNotes() {
        setSelectedButton(notesButton);
        VBox page = createPageContainer("My Notes", "Capture, summarize, and revise high-yield study notes with Gemini.");

        HBox mainLayout = new HBox(18);
        VBox.setVgrow(mainLayout, Priority.ALWAYS);

        // LEFT: Notes List
        VBox leftPane = new VBox(10);
        leftPane.setPrefWidth(280);
        leftPane.setMinWidth(240);

        Button newNoteBtn = new Button("+ New Note");
        newNoteBtn.setMaxWidth(Double.MAX_VALUE);
        newNoteBtn.setStyle(PRIMARY_BTN);

        ListView<Note> notesListView = new ListView<>();
        notesListView.setPrefHeight(450);
        notesListView.setStyle("-fx-control-inner-background: #1E293B; -fx-background-radius: 8;");
        ObservableList<Note> observableNotes = FXCollections.observableArrayList(NotesManager.getNotes());
        notesListView.setItems(observableNotes);

        leftPane.getChildren().addAll(newNoteBtn, notesListView);

        // RIGHT: Editor
        VBox rightPane = new VBox(12);
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        rightPane.setStyle(CARD_BG);
        rightPane.setPadding(new Insets(18));

        TextField titleField = new TextField();
        titleField.setPromptText("Note Title (e.g. Data Structures Overview)");
        titleField.setStyle(INPUT_STYLE);

        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("General", "Computer Science", "Mathematics", "Science", "Humanities", "Exam Prep");
        categoryBox.setValue("General");
        categoryBox.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-background-radius: 6;");

        HBox metaRow = new HBox(10);
        metaRow.getChildren().addAll(titleField, categoryBox);
        HBox.setHgrow(titleField, Priority.ALWAYS);

        TextArea contentArea = new TextArea();
        contentArea.setPromptText("Write your notes here, or click 'AI Generate Notes' to build an outline on any topic...");
        contentArea.setWrapText(true);
        contentArea.setPrefHeight(340);
        contentArea.setStyle("-fx-control-inner-background: #0F172A; -fx-text-fill: #F8FAFC; -fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-background-radius: 8;");
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        Label noteStatus = new Label("");
        noteStatus.setStyle("-fx-text-fill: #10B981; -fx-font-size: 12px;");

        // Action Buttons
        HBox actionRow = new HBox(10);
        actionRow.setAlignment(Pos.CENTER_LEFT);

        Button saveBtn = new Button("Save Note");
        saveBtn.setStyle(PRIMARY_BTN);

        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 16;");

        Button aiGenerateBtn = new Button("✨ AI Generate Notes");
        aiGenerateBtn.setStyle(ACCENT_PURPLE_BTN);

        Button aiSummarizeBtn = new Button("✨ AI Summarize");
        aiSummarizeBtn.setStyle(SECONDARY_BTN);

        actionRow.getChildren().addAll(saveBtn, deleteBtn, aiGenerateBtn, aiSummarizeBtn);

        rightPane.getChildren().addAll(metaRow, contentArea, noteStatus, actionRow);
        mainLayout.getChildren().addAll(leftPane, rightPane);

        // Selection Listener
        notesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                activeNote = newVal;
                titleField.setText(newVal.getTitle());
                categoryBox.setValue(newVal.getCategory());
                contentArea.setText(newVal.getContent());
                noteStatus.setText("Loaded: " + newVal.getTitle());
            }
        });

        // Select first note by default
        if (!observableNotes.isEmpty()) {
            notesListView.getSelectionModel().select(0);
        }

        newNoteBtn.setOnAction(e -> {
            activeNote = new Note();
            titleField.clear();
            categoryBox.setValue("General");
            contentArea.clear();
            titleField.requestFocus();
            noteStatus.setText("New note started.");
        });

        saveBtn.setOnAction(e -> {
            String title = titleField.getText().trim();
            String content = contentArea.getText().trim();
            if (title.isEmpty()) {
                showAlert("Missing Title", "Please enter a title for your note.");
                return;
            }

            if (activeNote == null) {
                activeNote = new Note();
            }
            activeNote.setTitle(title);
            activeNote.setCategory(categoryBox.getValue());
            activeNote.setContent(content);

            NotesManager.updateNote(activeNote);
            observableNotes.setAll(NotesManager.getNotes());
            notesListView.getSelectionModel().select(activeNote);
            noteStatus.setText("✓ Note saved successfully!");
        });

        deleteBtn.setOnAction(e -> {
            if (activeNote != null) {
                NotesManager.deleteNote(activeNote.getId());
                observableNotes.setAll(NotesManager.getNotes());
                activeNote = null;
                titleField.clear();
                contentArea.clear();
                noteStatus.setText("Note deleted.");
            }
        });

        aiGenerateBtn.setOnAction(e -> {
            if (!aiService.isConfigured()) {
                showAlert("API Key Missing", "Please configure your Gemini API Key in API Settings to use AI features.");
                showApiSettingsDialog();
                return;
            }

            TextInputDialog promptDialog = new TextInputDialog(titleField.getText());
            promptDialog.setTitle("AI Study Note Generator");
            promptDialog.setHeaderText("Generate Comprehensive Revision Notes with Gemini");
            promptDialog.setContentText("Enter the study topic:");

            promptDialog.showAndWait().ifPresent(topic -> {
                if (topic.isBlank()) return;
                noteStatus.setText("Generating notes with Gemini for: " + topic + "...");
                aiGenerateBtn.setDisable(true);

                Task<String> task = new Task<>() {
                    @Override
                    protected String call() throws Exception {
                        return aiService.generateStudyNotes(topic);
                    }
                };

                task.setOnSucceeded(ev -> {
                    titleField.setText(topic);
                    contentArea.setText(task.getValue());
                    aiGenerateBtn.setDisable(false);
                    noteStatus.setText("✓ Notes generated by Gemini! Don't forget to save.");
                });

                task.setOnFailed(ev -> {
                    aiGenerateBtn.setDisable(false);
                    noteStatus.setText("Generation error: " + task.getException().getMessage());
                });

                new Thread(task).start();
            });
        });

        aiSummarizeBtn.setOnAction(e -> {
            String raw = contentArea.getText().trim();
            if (raw.isEmpty()) {
                showAlert("Empty Note", "Please write or paste content into the note area first.");
                return;
            }
            if (!aiService.isConfigured()) {
                showAlert("API Key Missing", "Please configure your Gemini API Key in API Settings.");
                showApiSettingsDialog();
                return;
            }

            noteStatus.setText("Summarizing note with Gemini...");
            aiSummarizeBtn.setDisable(true);

            Task<String> task = new Task<>() {
                @Override
                protected String call() throws Exception {
                    return aiService.summarizeNotes(raw);
                }
            };

            task.setOnSucceeded(ev -> {
                contentArea.appendText("\n\n--- 📌 AI SUMMARY & REVISION CHEATSHEET ---\n" + task.getValue());
                aiSummarizeBtn.setDisable(false);
                noteStatus.setText("✓ Summary added to note!");
            });

            task.setOnFailed(ev -> {
                aiSummarizeBtn.setDisable(false);
                noteStatus.setText("Summarize error: " + task.getException().getMessage());
            });

            new Thread(task).start();
        });

        page.getChildren().add(mainLayout);

        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #0B1120; -fx-background-color: transparent;");
        root.setCenter(scroll);
    }

    // ==========================================
    // 4. QUIZ GENERATOR PAGE
    // ==========================================

    private void showQuiz() {
        setSelectedButton(quizButton);
        VBox page = createPageContainer("Quiz Generator", "Test your knowledge with custom AI-generated quizzes & instant scoring.");

        // Controls Box
        HBox controls = new HBox(12);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setStyle(CARD_BG);
        controls.setPadding(new Insets(16));

        TextField topicField = new TextField();
        topicField.setPromptText("Enter topic (e.g. Java Collections, Photosynthesis, French Revolution)...");
        topicField.setStyle(INPUT_STYLE);
        HBox.setHgrow(topicField, Priority.ALWAYS);

        ComboBox<Integer> countBox = new ComboBox<>();
        countBox.getItems().addAll(3, 5, 10);
        countBox.setValue(5);
        countBox.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-background-radius: 6;");

        ComboBox<String> diffBox = new ComboBox<>();
        diffBox.getItems().addAll("Beginner", "Intermediate", "Advanced");
        diffBox.setValue("Intermediate");
        diffBox.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-background-radius: 6;");

        Button generateBtn = new Button("✨ Generate Quiz");
        generateBtn.setStyle(PRIMARY_BTN);

        controls.getChildren().addAll(topicField, countBox, diffBox, generateBtn);

        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-text-fill: #38BDF8; -fx-font-size: 13px;");

        VBox quizContainer = new VBox(15);
        quizContainer.setPadding(new Insets(10, 0, 10, 0));

        generateBtn.setOnAction(e -> {
            String topic = topicField.getText().trim();
            if (topic.isEmpty()) {
                showAlert("Missing Topic", "Please enter a subject or topic for the quiz.");
                return;
            }

            if (!aiService.isConfigured()) {
                showAlert("API Key Required", "Please configure your Gemini API Key in API Settings.");
                showApiSettingsDialog();
                return;
            }

            statusLabel.setText("Generating " + countBox.getValue() + " questions with Gemini on '" + topic + "'...");
            generateBtn.setDisable(true);
            quizContainer.getChildren().clear();

            Task<List<QuizQuestion>> task = new Task<>() {
                @Override
                protected List<QuizQuestion> call() throws Exception {
                    return aiService.generateQuiz(topic, diffBox.getValue(), countBox.getValue());
                }
            };

            task.setOnSucceeded(ev -> {
                List<QuizQuestion> questions = task.getValue();
                generateBtn.setDisable(false);
                statusLabel.setText("✓ Quiz Ready! Select your answers and click 'Submit Quiz'.");
                renderQuizQuestions(quizContainer, questions);
            });

            task.setOnFailed(ev -> {
                generateBtn.setDisable(false);
                statusLabel.setText("Quiz Generation Failed: " + task.getException().getMessage());
            });

            new Thread(task).start();
        });

        topicField.setOnAction(e -> generateBtn.fire());

        page.getChildren().addAll(controls, statusLabel, quizContainer);

        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #0B1120; -fx-background-color: transparent;");
        root.setCenter(scroll);
    }

    private void renderQuizQuestions(VBox container, List<QuizQuestion> questions) {
        container.getChildren().clear();
        List<ToggleGroup> groups = new ArrayList<>();

        for (int i = 0; i < questions.size(); i++) {
            QuizQuestion q = questions.get(i);
            VBox qCard = new VBox(10);
            qCard.setStyle(CARD_BG);
            qCard.setPadding(new Insets(16));

            Label qTitle = new Label((i + 1) + ". " + q.getQuestion());
            qTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
            qTitle.setStyle("-fx-text-fill: #F8FAFC;");
            qTitle.setWrapText(true);

            VBox optionsBox = new VBox(8);
            ToggleGroup group = new ToggleGroup();
            groups.add(group);

            for (int j = 0; j < q.getOptions().size(); j++) {
                RadioButton rb = new RadioButton(q.getOptions().get(j));
                rb.setToggleGroup(group);
                rb.setStyle("-fx-text-fill: #CBD5E1; -fx-font-size: 13px;");
                final int optIndex = j;
                rb.setOnAction(e -> q.setUserSelectedIndex(optIndex));
                optionsBox.getChildren().add(rb);
            }

            Label explanationLabel = new Label();
            explanationLabel.setWrapText(true);
            explanationLabel.setFont(Font.font("Segoe UI", 12));
            explanationLabel.setVisible(false);
            explanationLabel.setManaged(false);

            qCard.getChildren().addAll(qTitle, optionsBox, explanationLabel);
            container.getChildren().add(qCard);
        }

        // Submit Button
        Button submitBtn = new Button("Submit Answers & Check Score");
        submitBtn.setStyle(ACCENT_PURPLE_BTN);

        Label scoreLabel = new Label();
        scoreLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        scoreLabel.setStyle("-fx-text-fill: #10B981;");

        submitBtn.setOnAction(e -> {
            int score = 0;
            for (int i = 0; i < questions.size(); i++) {
                QuizQuestion q = questions.get(i);
                VBox card = (VBox) container.getChildren().get(i);
                VBox optionsBox = (VBox) card.getChildren().get(1);
                Label explanation = (Label) card.getChildren().get(2);

                explanation.setVisible(true);
                explanation.setManaged(true);

                if (q.isCorrect()) {
                    score++;
                    explanation.setText("✓ Correct! " + q.getExplanation());
                    explanation.setStyle("-fx-text-fill: #10B981; -fx-padding: 6 0;");
                } else {
                    int correctIdx = q.getCorrectIndex();
                    String correctText = (correctIdx >= 0 && correctIdx < q.getOptions().size())
                            ? q.getOptions().get(correctIdx) : "N/A";
                    explanation.setText("✗ Incorrect. Correct Answer: " + correctText + "\n" + q.getExplanation());
                    explanation.setStyle("-fx-text-fill: #F87171; -fx-padding: 6 0;");
                }
            }

            quizzesCompleted++;
            double pct = ((double) score / questions.size()) * 100;
            scoreLabel.setText(String.format("Final Score: %d / %d (%.0f%%) %s",
                    score, questions.size(), pct, pct >= 70 ? "🎉 Great job!" : "Keep practicing!"));

            submitBtn.setDisable(true);
        });

        HBox submitRow = new HBox(15);
        submitRow.setAlignment(Pos.CENTER_LEFT);
        submitRow.getChildren().addAll(submitBtn, scoreLabel);

        container.getChildren().add(submitRow);
    }

    // ==========================================
    // 5. STUDY PLANNER PAGE
    // ==========================================

    private void showPlanner() {
        setSelectedButton(plannerButton);
        VBox page = createPageContainer("Study Planner", "Track daily milestones or generate a complete study schedule with AI.");

        // Progress Section
        VBox progressCard = new VBox(8);
        progressCard.setStyle(CARD_BG);
        progressCard.setPadding(new Insets(16));

        Label progTitle = new Label("Daily Study Progress");
        progTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        progTitle.setStyle("-fx-text-fill: #F8FAFC;");

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setStyle("-fx-accent: #2563EB;");

        Label progLabel = new Label("0 / 0 tasks completed");
        progLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 12px;");

        progressCard.getChildren().addAll(progTitle, progressBar, progLabel);

        // Add Task Bar
        HBox addRow = new HBox(10);
        addRow.setAlignment(Pos.CENTER_LEFT);

        TextField taskField = new TextField();
        taskField.setPromptText("Add a custom study task (e.g. 'Solve 10 LeetCode problems')...");
        taskField.setStyle(INPUT_STYLE);
        HBox.setHgrow(taskField, Priority.ALWAYS);

        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("Study", "Practice", "Reading", "Revision", "Homework");
        categoryBox.setValue("Study");
        categoryBox.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-background-radius: 6;");

        Button addBtn = new Button("Add Task");
        addBtn.setStyle(PRIMARY_BTN);

        Button aiPlanBtn = new Button("✨ AI Generate Plan");
        aiPlanBtn.setStyle(ACCENT_PURPLE_BTN);

        Button clearDoneBtn = new Button("Clear Done");
        clearDoneBtn.setStyle(SECONDARY_BTN);

        addRow.getChildren().addAll(taskField, categoryBox, addBtn, aiPlanBtn, clearDoneBtn);

        // Task List Container
        VBox tasksListContainer = new VBox(8);

        Runnable refreshUI = () -> {
            tasksListContainer.getChildren().clear();
            List<StudyTask> list = TaskManager.getTasks();
            int total = list.size();
            int done = TaskManager.getCompletedCount();

            double progress = total > 0 ? (double) done / total : 0;
            progressBar.setProgress(progress);
            progLabel.setText(done + " of " + total + " tasks completed (" + (int)(progress * 100) + "%)");

            for (StudyTask t : list) {
                HBox item = new HBox(12);
                item.setAlignment(Pos.CENTER_LEFT);
                item.setStyle("-fx-background-color: #1E293B; -fx-background-radius: 6; -fx-padding: 10 14;");

                CheckBox cb = new CheckBox();
                cb.setSelected(t.isCompleted());

                Label title = new Label(t.getTitle());
                title.setStyle("-fx-text-fill: " + (t.isCompleted() ? "#64748B;" : "#F1F5F9;") +
                        (t.isCompleted() ? " -fx-font-style: italic;" : ""));
                HBox.setHgrow(title, Priority.ALWAYS);

                cb.setOnAction(e -> {
                    t.setCompleted(cb.isSelected());
                    TaskManager.updateTask(t);
                    title.setStyle("-fx-text-fill: " + (t.isCompleted() ? "#64748B;" : "#F1F5F9;") +
                            (t.isCompleted() ? " -fx-font-style: italic;" : ""));
                    int d = TaskManager.getCompletedCount();
                    double p = (double) d / list.size();
                    progressBar.setProgress(p);
                    progLabel.setText(d + " of " + list.size() + " tasks completed (" + (int)(p * 100) + "%)");
                });

                Label catBadge = new Label(t.getCategory());
                catBadge.setStyle("-fx-background-color: #334155; -fx-text-fill: #38BDF8; -fx-padding: 3 8; -fx-background-radius: 4; -fx-font-size: 11px;");

                Button deleteBtn = new Button("✕");
                deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94A3B8; -fx-font-weight: bold;");
                deleteBtn.setOnAction(e -> {
                    TaskManager.deleteTask(t.getId());
                    tasksListContainer.getChildren().remove(item);
                    int d = TaskManager.getCompletedCount();
                    int tot = TaskManager.getTotalCount();
                    double p = tot > 0 ? (double) d / tot : 0;
                    progressBar.setProgress(p);
                    progLabel.setText(d + " of " + tot + " tasks completed (" + (int)(p * 100) + "%)");
                });

                item.getChildren().addAll(cb, title, catBadge, deleteBtn);
                tasksListContainer.getChildren().add(item);
            }
        };

        refreshUI.run();

        addBtn.setOnAction(e -> {
            String text = taskField.getText().trim();
            if (!text.isEmpty()) {
                TaskManager.addTask(new StudyTask(text, categoryBox.getValue()));
                taskField.clear();
                refreshUI.run();
            }
        });

        taskField.setOnAction(e -> addBtn.fire());

        clearDoneBtn.setOnAction(e -> {
            TaskManager.clearCompleted();
            refreshUI.run();
        });

        aiPlanBtn.setOnAction(e -> {
            if (!aiService.isConfigured()) {
                showAlert("API Key Required", "Please configure your Gemini API Key in API Settings to generate plans.");
                showApiSettingsDialog();
                return;
            }

            TextInputDialog dialog = new TextInputDialog("Calculus Exam Preparation");
            dialog.setTitle("AI Study Schedule Planner");
            dialog.setHeaderText("Let Gemini create a focused daily study roadmap");
            dialog.setContentText("What exam or goal are you preparing for?");

            dialog.showAndWait().ifPresent(goal -> {
                if (goal.isBlank()) return;
                aiPlanBtn.setDisable(true);
                progLabel.setText("Building study plan with Gemini for: " + goal + "...");

                Task<List<StudyTask>> task = new Task<>() {
                    @Override
                    protected List<StudyTask> call() throws Exception {
                        return aiService.generateStudyPlan(goal, 5);
                    }
                };

                task.setOnSucceeded(ev -> {
                    List<StudyTask> newTasks = task.getValue();
                    TaskManager.addTasks(newTasks);
                    aiPlanBtn.setDisable(false);
                    refreshUI.run();
                });

                task.setOnFailed(ev -> {
                    aiPlanBtn.setDisable(false);
                    showAlert("Planning Failed", "Could not generate plan: " + task.getException().getMessage());
                    refreshUI.run();
                });

                new Thread(task).start();
            });
        });

        page.getChildren().addAll(progressCard, addRow, tasksListContainer);

        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #0B1120; -fx-background-color: transparent;");
        root.setCenter(scroll);
    }

    // ==========================================
    // 6. API SETTINGS DIALOG
    // ==========================================

    private void showApiSettingsDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setTitle("Gemini API Key Configuration");

        VBox content = new VBox(14);
        content.setPadding(new Insets(24));
        content.setStyle(SIDEBAR_BG);
        content.setPrefWidth(480);

        Label header = new Label("Gemini API Settings");
        header.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        header.setStyle("-fx-text-fill: #F8FAFC;");

        Label desc = new Label("Enter your Google Gemini API key to enable AI Tutor, Quiz Generator, and Study Planning.");
        desc.setWrapText(true);
        desc.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 13px;");

        Label keyLabel = new Label("Google API Key:");
        keyLabel.setStyle("-fx-text-fill: #E2E8F0; -fx-font-weight: bold;");

        PasswordField keyField = new PasswordField();
        keyField.setPromptText("Paste your Gemini API key here...");
        keyField.setStyle(INPUT_STYLE);
        if (ConfigManager.getApiKey() != null) {
            keyField.setText(ConfigManager.getApiKey());
        }

        TextField textKeyField = new TextField();
        textKeyField.setStyle(INPUT_STYLE);
        textKeyField.setVisible(false);
        textKeyField.setManaged(false);

        CheckBox showKey = new CheckBox("Show API Key");
        showKey.setStyle("-fx-text-fill: #94A3B8;");
        showKey.setOnAction(e -> {
            if (showKey.isSelected()) {
                textKeyField.setText(keyField.getText());
                textKeyField.setVisible(true);
                textKeyField.setManaged(true);
                keyField.setVisible(false);
                keyField.setManaged(false);
            } else {
                keyField.setText(textKeyField.getText());
                keyField.setVisible(true);
                keyField.setManaged(true);
                textKeyField.setVisible(false);
                textKeyField.setManaged(false);
            }
        });

        Label modelLabel = new Label("Gemini Model:");
        modelLabel.setStyle("-fx-text-fill: #E2E8F0; -fx-font-weight: bold;");

        ComboBox<String> modelCombo = new ComboBox<>();
        modelCombo.getItems().addAll("gemini-3.5-flash", "gemini-3.5-flash-lite", "gemini-3-flash-preview");
        modelCombo.setValue(ConfigManager.getModel());
        modelCombo.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-background-radius: 6;");

        Label testResult = new Label();
        testResult.setWrapText(true);
        testResult.setFont(Font.font("Segoe UI", 12));

        Button testBtn = new Button("Test Connection");
        testBtn.setStyle(SECONDARY_BTN);

        testBtn.setOnAction(e -> {
            String enteredKey = showKey.isSelected() ? textKeyField.getText().trim() : keyField.getText().trim();
            if (enteredKey.isEmpty()) {
                testResult.setText("Please enter an API key first.");
                testResult.setStyle("-fx-text-fill: #F87171;");
                return;
            }

            testResult.setText("Testing connection with Gemini...");
            testResult.setStyle("-fx-text-fill: #38BDF8;");
            testBtn.setDisable(true);

            Task<String> testTask = new Task<>() {
                @Override
                protected String call() throws Exception {
                    return AIService.testApiKey(enteredKey, modelCombo.getValue());
                }
            };

            testTask.setOnSucceeded(ev -> {
                testBtn.setDisable(false);
                testResult.setText("✓ " + testTask.getValue());
                testResult.setStyle("-fx-text-fill: #10B981;");
            });

            testTask.setOnFailed(ev -> {
                testBtn.setDisable(false);
                testResult.setText("✕ Connection failed: " + testTask.getException().getMessage());
                testResult.setStyle("-fx-text-fill: #F87171;");
            });

            new Thread(testTask).start();
        });

        Button saveBtn = new Button("Save & Connect");
        saveBtn.setStyle(PRIMARY_BTN);

        saveBtn.setOnAction(e -> {
            String enteredKey = showKey.isSelected() ? textKeyField.getText().trim() : keyField.getText().trim();
            ConfigManager.saveApiKey(enteredKey);
            ConfigManager.saveModel(modelCombo.getValue());

            boolean ok = aiService.initClient();
            updateStatusBadge();

            if (ok) {
                showAlert("Success", "Gemini API successfully connected! All AI features are ready.");
            } else {
                showAlert("Warning", "API key saved, but Gemini client could not initialize. Check your key.");
            }
            dialog.close();
        });

        Button closeBtn = new Button("Close");
        closeBtn.setStyle(SECONDARY_BTN);
        closeBtn.setOnAction(e -> dialog.close());

        HBox btnRow = new HBox(10);
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        btnRow.getChildren().addAll(testBtn, saveBtn, closeBtn);

        Label helpText = new Label("Need an API key? You can get a free key from https://aistudio.google.com/app/apikey");
        helpText.setWrapText(true);
        helpText.setStyle("-fx-text-fill: #60A5FA; -fx-font-size: 11px;");

        content.getChildren().addAll(
                header,
                desc,
                keyLabel,
                keyField,
                textKeyField,
                showKey,
                modelLabel,
                modelCombo,
                testResult,
                btnRow,
                new Separator(),
                helpText
        );

        Scene scene = new Scene(content);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void stop() {
        if (aiService != null) {
            aiService.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
