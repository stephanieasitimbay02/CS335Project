package com.rob.app.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class NotesView extends BorderPane {

    private static final String NOTES_FILE = "notes.txt";

    public NotesView(Stage stage) {
        setPadding(new Insets(16));
        setStyle("-fx-background-color: #F5F7FA;");

        // HEADER
        Label header = new Label("Notes");
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

        // NOTES CONTENT
        VBox notesContent = new VBox(15);
        notesContent.setPadding(new Insets(10));
        notesContent.setStyle("-fx-border-color: #E0E0E0; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: white;");
        notesContent.setPrefWidth(400);

        Label title = new Label("Your Notes");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        TextArea noteInput = new TextArea();
        noteInput.setPromptText("Write a note...");
        noteInput.setPrefRowCount(4);

        Button addNoteBtn = new Button("Add Note");
        ListView<String> notesList = new ListView<>();
        loadItems(notesList, NOTES_FILE);

        addNoteBtn.setOnAction(e -> {
            String note = noteInput.getText().trim();
            if (!note.isEmpty()) {
                notesList.getItems().add(note);
                saveItems(notesList.getItems(), NOTES_FILE);
                noteInput.clear();
            }
        });

        Button deleteNoteBtn = new Button("Delete Selected Note");
        deleteNoteBtn.setOnAction(e -> {
            String selected = notesList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                notesList.getItems().remove(selected);
                saveItems(notesList.getItems(), NOTES_FILE);
            }
        });

        notesContent.getChildren().addAll(title, noteInput, addNoteBtn, notesList, deleteNoteBtn);

        HBox centerRow = new HBox(12, sidebar, notesContent);
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