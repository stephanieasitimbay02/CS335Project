package com.rob.app.screens;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.calendarfx.view.CalendarView;
import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import java.io.*;
import java.time.LocalDate;
import com.calendarfx.model.Entry;


public class MainView extends BorderPane {

    private int timeSeconds = 1500; // 25 minutes
    private Timeline timeline;
    private String userEmail;

    public MainView(Stage stage, String userEmail) {
        setPadding(new Insets(16));
        setStyle("-fx-background-color: #F5F7FA;");
        
        this.userEmail = userEmail;
        String safeEmail = userEmail.replaceAll("[^a-zA-Z0-9]", "_");
        File userDir = new File("users/" + safeEmail);
        String tasksFile = new File(userDir, "tasks.txt").getPath();

        // HEADER
        Label header = new Label("Home");
        header.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #4A6FA5;");
        setTop(header);
        BorderPane.setAlignment(header, Pos.CENTER);

        // LOGOUT
        Button logout = new Button("Log out");
        logout.setOnAction(e -> stage.getScene().setRoot(new LoginView(stage)));
        setBottom(logout);
        BorderPane.setAlignment(logout, Pos.CENTER);

        // SIDEBAR
        VBox sidebar = createSidebar(stage, userEmail);
        
        // CALENDAR
        CalendarView calendarView = new CalendarView();
        calendarView.setShowAddCalendarButton(false);
        calendarView.setShowPrintButton(false);
        calendarView.setShowSearchField(false);
        VBox calendarCard = new VBox(calendarView);
        calendarCard.setPadding(new Insets(10));
        calendarCard.setStyle("-fx-border-color: #E0E0E0; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: white;");
        calendarCard.setPrefSize(520, 420);
        Calendar calendar = new Calendar("Tasks");
        calendar.setStyle(Calendar.Style.STYLE1);

        CalendarSource source = new CalendarSource("My Calendars");
        source.getCalendars().add(calendar);

        calendarView.getCalendarSources().add(source);
        
        loadTasksToCalendar(calendar, tasksFile);

        // HOME CONTENT
        VBox homeContent = new VBox(15);
        homeContent.setPadding(new Insets(10));
        homeContent.setStyle("-fx-border-color: #E0E0E0; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: white;");
        homeContent.setPrefWidth(300);

        Label welcomeLabel = new Label("Welcome to your dashboard");
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Pomodoro
        VBox pomodoroBox = createPomodoroBox();
        homeContent.getChildren().addAll(welcomeLabel, pomodoroBox);

        // CENTER LAYOUT
        HBox centerRow = new HBox(12, sidebar, homeContent, calendarCard);
        centerRow.setAlignment(Pos.TOP_LEFT);
        setCenter(centerRow);
    }

    private VBox createSidebar(Stage stage, String userEmail) {
        VBox sidebar = new VBox(12);
        sidebar.setPadding(new Insets(10));
        sidebar.setStyle("-fx-background-color: #4A6FA5; -fx-padding: 15; -fx-border-radius: 10;");

        Button homeBtn = new Button("Home");
        Button tasksBtn = new Button("Tasks");
        Button notesBtn = new Button("Notes");
        Button coursesBtn = new Button("Courses");
        Button settingsBtn = new Button("Settings");

        for (Button btn : new Button[]{homeBtn, tasksBtn, notesBtn, coursesBtn, settingsBtn}) {
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-alignment: center-left; -fx-padding: 10;");
        }

        homeBtn.setOnAction(e -> stage.getScene().setRoot(new MainView(stage, userEmail)));
        tasksBtn.setOnAction(e -> stage.getScene().setRoot(new TasksView(stage, userEmail)));
        notesBtn.setOnAction(e -> stage.getScene().setRoot(new NotesView(stage, userEmail)));
        coursesBtn.setOnAction(e -> stage.getScene().setRoot(new CoursesView(stage, userEmail)));
        settingsBtn.setOnAction(e -> stage.getScene().setRoot(new SettingsView(stage, userEmail)));

        sidebar.getChildren().addAll(homeBtn, tasksBtn, notesBtn, coursesBtn, settingsBtn);
        return sidebar;
    }

    private VBox createPomodoroBox() {
        Label pomodoroTitle = new Label("Pomodoro Timer");
        pomodoroTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        Label timerLabel = new Label(formatTime(timeSeconds));
        timerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #4A6FA5;");

        Button startTimer = new Button("Start");
        Button pauseTimer = new Button("Pause");
        Button resetTimer = new Button("Reset");

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (timeSeconds > 0) {
                timeSeconds--;
                timerLabel.setText(formatTime(timeSeconds));
            } else {
                timeline.stop();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);

        startTimer.setOnAction(e -> timeline.play());
        pauseTimer.setOnAction(e -> timeline.pause());
        resetTimer.setOnAction(e -> {
            timeline.stop();
            timeSeconds = 1500;
            timerLabel.setText(formatTime(timeSeconds));
        });

        HBox timerButtons = new HBox(5, startTimer, pauseTimer, resetTimer);
        VBox pomodoroBox = new VBox(8, pomodoroTitle, timerLabel, timerButtons);
        pomodoroBox.setPadding(new Insets(10));
        pomodoroBox.setStyle("-fx-border-color: #E0E0E0; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: white;");

        return pomodoroBox;
    }

    private String formatTime(int seconds) {
        int m = seconds / 60;
        int s = seconds % 60;
        return String.format("%02d:%02d", m, s);
    }
    
    private void loadTasksToCalendar(Calendar calendar, String tasksFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(tasksFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");

                if (parts.length == 2) {
                    String title = parts[0];
                    LocalDate date = LocalDate.parse(parts[1]);

                    Entry<String> entry = new Entry<>(title);
                    entry.changeStartDate(date);
                    entry.changeEndDate(date);

                    calendar.addEntry(entry);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}