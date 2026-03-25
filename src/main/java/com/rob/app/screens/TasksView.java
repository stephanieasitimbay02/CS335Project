package com.rob.app.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class TasksView extends BorderPane {

    private static final String TASK_FILE = "tasks.txt";

    public TasksView(Stage stage) {

        setPadding(new Insets(16));

        Label header = new Label("Tasks");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button logout = new Button("Log out");
        logout.setOnAction(e -> stage.getScene().setRoot(new LoginView(stage)));

        // SIDEBAR
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(10));
        sidebar.setPrefWidth(140);
        sidebar.setStyle("-fx-border-color: lightgray;");

        Button homeBtn = new Button("Home");
        Button tasksBtn = new Button("Tasks");
        Button notesBtn = new Button("Notes");
        Button coursesBtn = new Button("Courses");

        homeBtn.setMaxWidth(Double.MAX_VALUE);
        tasksBtn.setMaxWidth(Double.MAX_VALUE);
        notesBtn.setMaxWidth(Double.MAX_VALUE);
        coursesBtn.setMaxWidth(Double.MAX_VALUE);

        homeBtn.setOnAction(e -> stage.getScene().setRoot(new MainView(stage)));
        tasksBtn.setOnAction(e -> stage.getScene().setRoot(new TasksView(stage)));
        notesBtn.setOnAction(e -> stage.getScene().setRoot(new NotesView(stage)));
        coursesBtn.setOnAction(e -> stage.getScene().setRoot(new CoursesView(stage)));

        sidebar.getChildren().addAll(homeBtn, tasksBtn, notesBtn, coursesBtn);

        // TASK CONTENT
        Label todoLabel = new Label("To Do");

        TextField taskInput = new TextField();
        taskInput.setPromptText("Enter a task");

        Button addTask = new Button("Add Task");
        Button deleteTask = new Button("Delete Selected Task");

        ListView<String> taskList = new ListView<>();

        loadItems(taskList, TASK_FILE);

        addTask.setOnAction(e -> {
            String task = taskInput.getText().trim();
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

        VBox taskContent = new VBox(10,
                todoLabel,
                taskInputRow,
                taskList,
                deleteTask
        );
        taskContent.setPadding(new Insets(10));
        taskContent.setStyle("-fx-border-color: lightgray;");
        taskContent.setPrefWidth(500);

        HBox centerRow = new HBox(12, sidebar, taskContent);
        centerRow.setAlignment(Pos.TOP_LEFT);

        setTop(header);
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
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void loadItems(ListView<String> listView, String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            List<String> lines = reader.lines().collect(Collectors.toList());
            listView.getItems().addAll(lines);
        } catch (IOException ex) {
            // okay if file does not exist yet
        }
    }
}
