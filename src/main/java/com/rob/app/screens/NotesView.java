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

    private String notesFile;
    private String userEmail;

    public NotesView(Stage stage, String userEmail) {
        setPadding(new Insets(16));
        setStyle("-fx-background-color: #F5F7FA;");
        
        this.userEmail = userEmail;
        //sanitize email
        String safeEmail = userEmail.replaceAll("[^a-zA-Z0-9]", "_");
        File userDir = new File("users/" + safeEmail);
        // creating folder if it doesn't already exist
        if (!userDir.exists()) {
        	userDir.mkdirs();
        }
        this.notesFile = new File(userDir, "notes.txt").getPath();
        
        File file = new File(notesFile);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("Error creating notes file");
        }

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
        loadItems(notesList, notesFile);

        addNoteBtn.setOnAction(e -> {
            String note = noteInput.getText().trim();
            if (!note.isEmpty()) {
                notesList.getItems().add(note);
                saveItems(notesList.getItems(), notesFile);
                noteInput.clear();
            }
        });

        Button deleteNoteBtn = new Button("Delete Selected Note");
        deleteNoteBtn.setOnAction(e -> {
            String selected = notesList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                notesList.getItems().remove(selected);
                saveItems(notesList.getItems(), notesFile);
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