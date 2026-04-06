package com.rob.app.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class TasksView extends BorderPane {

    private static final String TASK_FILE = "tasks.txt";

    public TasksView(Stage stage) {
        setPadding(new Insets(16));
        setStyle("-fx-background-color: #F5F7FA;");

        // HEADER
        Label header = new Label("Tasks");
        header.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #4A6FA5;");
        setTop(header);
        BorderPane.setAlignment(header, Pos.CENTER);

        // LOGOUT
        Button logout = new Button("Log out");
        logout.setOnAction(e -> stage.getScene().setRoot(new LoginView(stage)));
        setBottom(logout);
        BorderPane.setAlignment(logout, Pos.CENTER);

        // SIDEBAR
        VBox sidebar = createSidebar(stage);

        // TASK CONTENT
        VBox taskContent = new VBox(15);
        taskContent.setPadding(new Insets(10));
        taskContent.setStyle("-fx-border-color: #E0E0E0; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: white;");
        taskContent.setPrefWidth(400);

        Label title = new Label("Your Tasks");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        TextField taskInput = new TextField();
        taskInput.setPromptText("Enter a task");
        taskInput.setPrefWidth(250);

        Button addTaskBtn = new Button("Add");
        HBox inputRow = new HBox(8, taskInput, addTaskBtn);
        inputRow.setAlignment(Pos.CENTER_LEFT);

        ListView<String> taskList = new ListView<>();
        loadItems(taskList, TASK_FILE);

        addTaskBtn.setOnAction(e -> {
            String task = taskInput.getText().trim();
            if (!task.isEmpty()) {
                taskList.getItems().add(task);
                saveItems(taskList.getItems(), TASK_FILE);
                taskInput.clear();
            }
        });

        Button deleteTaskBtn = new Button("Delete Selected");
        deleteTaskBtn.setOnAction(e -> {
            String selected = taskList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                taskList.getItems().remove(selected);
                saveItems(taskList.getItems(), TASK_FILE);
            }
        });

        taskContent.getChildren().addAll(title, inputRow, taskList, deleteTaskBtn);

        HBox centerRow = new HBox(12, sidebar, taskContent);
        centerRow.setAlignment(Pos.TOP_LEFT);
        setCenter(centerRow);
    }

    private VBox createSidebar(Stage stage) {
        VBox sidebar = new VBox(12);
        sidebar.setPadding(new Insets(10));
        sidebar.setStyle("-fx-background-color: #4A6FA5; -fx-padding: 15; -fx-border-radius: 10;");

        Button homeBtn = new Button("Home");
        Button tasksBtn = new Button("Tasks");
        Button notesBtn = new Button("Notes");
        Button coursesBtn = new Button("Courses");

        for (Button btn : new Button[]{homeBtn, tasksBtn, notesBtn, coursesBtn}) {
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-alignment: center-left; -fx-padding: 10;");
        }

        homeBtn.setOnAction(e -> stage.getScene().setRoot(new MainView(stage)));
        tasksBtn.setOnAction(e -> stage.getScene().setRoot(new TasksView(stage)));
        notesBtn.setOnAction(e -> stage.getScene().setRoot(new NotesView(stage)));
        coursesBtn.setOnAction(e -> stage.getScene().setRoot(new CoursesView(stage)));

        sidebar.getChildren().addAll(homeBtn, tasksBtn, notesBtn, coursesBtn);
        return sidebar;
    }

    private void saveItems(List<String> items, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String item : items) {
                writer.write(item);
                writer.newLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void loadItems(ListView<String> listView, String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            List<String> lines = reader.lines().collect(Collectors.toList());
            listView.getItems().addAll(lines);
        } catch (IOException ignored) {
        }
    }
}
