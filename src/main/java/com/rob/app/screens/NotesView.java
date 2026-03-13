package com.rob.app.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
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

public class NotesView extends BorderPane {

    private static final String NOTES_FILE = "notes.txt";

    public NotesView(Stage stage) {

        setPadding(new Insets(16));

        Label header = new Label("Notes");
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

        homeBtn.setMaxWidth(Double.MAX_VALUE);
        tasksBtn.setMaxWidth(Double.MAX_VALUE);
        notesBtn.setMaxWidth(Double.MAX_VALUE);

        homeBtn.setOnAction(e -> stage.getScene().setRoot(new MainView(stage)));
        tasksBtn.setOnAction(e -> stage.getScene().setRoot(new TasksView(stage)));
        notesBtn.setOnAction(e -> stage.getScene().setRoot(new NotesView(stage)));

        sidebar.getChildren().addAll(homeBtn, tasksBtn, notesBtn);

        // NOTES CONTENT
        Label notesLabel = new Label("Notes");

        TextArea noteInput = new TextArea();
        noteInput.setPromptText("Write a note...");
        noteInput.setPrefRowCount(4);

        Button addNote = new Button("Add Note");
        Button deleteNote = new Button("Delete Selected Note");

        ListView<String> notesList = new ListView<>();

        loadItems(notesList, NOTES_FILE);

        addNote.setOnAction(e -> {
            String note = noteInput.getText().trim();
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

        VBox notesContent = new VBox(10,
                notesLabel,
                noteInput,
                addNote,
                notesList,
                deleteNote
        );
        notesContent.setPadding(new Insets(10));
        notesContent.setStyle("-fx-border-color: lightgray;");
        notesContent.setPrefWidth(500);

        HBox centerRow = new HBox(12, sidebar, notesContent);
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