package com.rob.app.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.calendarfx.view.CalendarView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class MainView extends BorderPane {

    private static final String TASK_FILE = "tasks.txt";
    private static final String NOTES_FILE = "notes.txt";

    public MainView(Stage stage) {

        setPadding(new Insets(16));

        Label header = new Label("Home");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button logout = new Button("Log out");
        logout.setOnAction(e -> stage.getScene().setRoot(new LoginView(stage)));

        setTop(header);

        // CALENDAR
        CalendarView calendarView = new CalendarView();
        calendarView.setShowAddCalendarButton(false);
        calendarView.setShowPrintButton(false);
        calendarView.setShowSearchField(false);

        VBox calendarCard = new VBox(calendarView);
        calendarCard.setPadding(new Insets(10));
        calendarCard.setStyle("-fx-border-color: lightgray; -fx-border-radius: 8;");
        calendarCard.setPrefSize(520, 420);

        // LEFT PANEL
        VBox leftPanel = new VBox(12);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setStyle("-fx-border-color: lightgray;");

        // TODO LIST
        Label todoLabel = new Label("To Do");

        TextField taskInput = new TextField();
        taskInput.setPromptText("Enter a task");

        Button addTask = new Button("Add Task");
        Button deleteTask = new Button("Delete Selected Task");

        ListView<String> taskList = new ListView<>();

        loadItems(taskList, TASK_FILE);

        addTask.setOnAction(e -> {
            String task = taskInput.getText();
            if (!task.isEmpty()) {
                taskList.getItems().add(task);
                saveItems(taskList.getItems(), TASK_FILE);
                taskInput.clear();
            }
        });

        deleteTask.setOnAction(e -> {
            String selected = taskList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                taskList.getItems().remove(selected);
                saveItems(taskList.getItems(), TASK_FILE);
            }
        });

        HBox taskInputRow = new HBox(5, taskInput, addTask);

        VBox todoBox = new VBox(5, todoLabel, taskInputRow, taskList, deleteTask);

        // POMODORO TIMER
        Label timerLabel = new Label("25:00");
        timerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button startTimer = new Button("Start");
        Button pauseTimer = new Button("Pause");
        Button resetTimer = new Button("Reset");

        final int[] timeSeconds = {1500};

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    timeSeconds[0]--;
                    int minutes = timeSeconds[0] / 60;
                    int seconds = timeSeconds[0] % 60;
                    timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
                })
        );

        timeline.setCycleCount(Timeline.INDEFINITE);

        startTimer.setOnAction(e -> timeline.play());
        pauseTimer.setOnAction(e -> timeline.pause());
        resetTimer.setOnAction(e -> {
            timeline.stop();
            timeSeconds[0] = 1500;
            timerLabel.setText("25:00");
        });

        HBox timerButtons = new HBox(5, startTimer, pauseTimer, resetTimer);

        VBox pomodoroBox = new VBox(5,
                new Label("Pomodoro Timer"),
                timerLabel,
                timerButtons
        );

        // NOTES
        Label notesLabel = new Label("Notes");

        TextArea noteInput = new TextArea();
        noteInput.setPromptText("Write a note...");
        noteInput.setPrefRowCount(3);

        Button addNote = new Button("Add Note");
        Button deleteNote = new Button("Delete Selected Note");

        ListView<String> notesList = new ListView<>();

        loadItems(notesList, NOTES_FILE);

        addNote.setOnAction(e -> {
            String note = noteInput.getText();
            if (!note.isEmpty()) {
                notesList.getItems().add(note);
                saveItems(notesList.getItems(), NOTES_FILE);
                noteInput.clear();
            }
        });

        deleteNote.setOnAction(e -> {
            String selected = notesList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                notesList.getItems().remove(selected);
                saveItems(notesList.getItems(), NOTES_FILE);
            }
        });

        VBox notesBox = new VBox(5,
                notesLabel,
                noteInput,
                addNote,
                notesList,
                deleteNote
        );

        // UPLOAD MEDIA
        Label uploadLabel = new Label("Upload Media");

        Button uploadButton = new Button("Choose File");

        ListView<String> uploadedFiles = new ListView<>();

        FileChooser fileChooser = new FileChooser();

        uploadButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                uploadedFiles.getItems().add(file.getName());
            }
        });

        VBox uploadBox = new VBox(5,
                uploadLabel,
                uploadButton,
                uploadedFiles
        );

        leftPanel.getChildren().addAll(
                todoBox,
                pomodoroBox,
                notesBox,
                uploadBox
        );

        // SIDEBAR
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(10));
        sidebar.setPrefWidth(120);
        sidebar.setStyle("-fx-border-color: lightgray;");

        Button homeBtn = new Button("Home");
        Button notesBtn = new Button("Notes");
        Button uploadBtn = new Button("Upload");

        sidebar.getChildren().addAll(homeBtn, notesBtn, uploadBtn);

        // CENTER
        HBox centerRow = new HBox(12, sidebar, leftPanel, calendarCard);
        centerRow.setAlignment(Pos.TOP_LEFT);

        setCenter(centerRow);

        setBottom(logout);

        BorderPane.setAlignment(header, Pos.CENTER);
        BorderPane.setAlignment(logout, Pos.CENTER);
    }

    private void saveItems(List<String> items, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String item : items) {
                writer.write(item);
                writer.newLine();
            }
        } catch (IOException ignored) {}
    }

    private void loadItems(ListView<String> listView, String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            List<String> lines = reader.lines().collect(Collectors.toList());
            listView.getItems().addAll(lines);
        } catch (IOException ignored) {}
    }
}