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

        // SIDEBAR
        VBox sidebar = new VBox(12);
        sidebar.setPadding(new Insets(10));
        sidebar.setStyle("-fx-border-color: lightgray;");
        
        Button homeBtn = new Button("Home");
        Button tasksBtn = new Button("Tasks");
        Button notesBtn = new Button("Notes");
        
        homeBtn.setMaxWidth(Double.MAX_VALUE);
        tasksBtn.setMaxWidth(Double.MAX_VALUE);
        notesBtn.setMaxWidth(Double.MAX_VALUE);

        homeBtn.setOnAction(e -> stage.getScene().setRoot(new MainView(stage)));
        tasksBtn.setOnAction(e -> stage.getScene().setRoot(new TasksView(stage)));
        notesBtn.setOnAction(e -> stage.getScene().setRoot(new NotesView(stage)));

        sidebar.getChildren().addAll(homeBtn, tasksBtn, notesBtn);

     // HOME CONTENT
        VBox homeContent = new VBox(15);
        homeContent.setPadding(new Insets(10));
        homeContent.setStyle("-fx-border-color: lightgray;");
        homeContent.setPrefWidth(300);

        Label welcomeLabel = new Label("Welcome to your dashboard");
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // POMODORO TIMER
        Label pomodoroTitle = new Label("Pomodoro Timer");

        Label timerLabel = new Label("25:00");
        timerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button startTimer = new Button("Start");
        Button pauseTimer = new Button("Pause");
        Button resetTimer = new Button("Reset");

        final int[] timeSeconds = {1500};
        final Timeline[] timeline = new Timeline[1];

        timeline[0] = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    if (timeSeconds[0] > 0) {
                        timeSeconds[0]--;
                        int minutes = timeSeconds[0] / 60;
                        int seconds = timeSeconds[0] % 60;
                        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
                    } else {
                        timeline[0].stop();
                    }
                })
        );

        timeline[0].setCycleCount(Timeline.INDEFINITE);

        startTimer.setOnAction(e -> timeline[0].play());
        pauseTimer.setOnAction(e -> timeline[0].pause());
        resetTimer.setOnAction(e -> {
            timeline[0].stop();
            timeSeconds[0] = 1500;
            timerLabel.setText("25:00");
        });

        HBox timerButtons = new HBox(5, startTimer, pauseTimer, resetTimer);

        VBox pomodoroBox = new VBox(8, pomodoroTitle, timerLabel, timerButtons);
        pomodoroBox.setPadding(new Insets(10));
        pomodoroBox.setStyle("-fx-border-color: lightgray;");
        
     // ADD CONTENT TO HOME CONTENT
        homeContent.getChildren().addAll(welcomeLabel, pomodoroBox);

        // CENTER LAYOUT
        HBox centerRow = new HBox(12, sidebar, homeContent, calendarCard);
        centerRow.setAlignment(Pos.TOP_LEFT);

        setCenter(centerRow);
        setBottom(logout);

        BorderPane.setAlignment(header, Pos.CENTER);
        BorderPane.setAlignment(logout, Pos.CENTER);
    }
}