package com.rob.app.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SettingsView extends BorderPane {

    private String userEmail;

    public SettingsView(Stage stage, String userEmail) {
        this.userEmail = userEmail;

        setPadding(new Insets(16));
        setStyle("-fx-background-color: #F5F7FA;");

        // HEADER
        Label header = new Label("Settings");
        header.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #4A6FA5;");
        setTop(header);
        BorderPane.setAlignment(header, Pos.CENTER);

        // SIDEBAR
        VBox sidebar = createSidebar(stage, userEmail);

        // SETTINGS CONTENT
        VBox settingsContent = new VBox(15);
        settingsContent.setPadding(new Insets(20));
        settingsContent.setPrefWidth(500);
        settingsContent.setStyle(
            "-fx-border-color: #E0E0E0;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;" +
            "-fx-background-color: white;"
        );
        
        

        Label accountTitle = new Label("Account Settings");
        accountTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Label emailLabel = new Label("Signed in as:");
        TextField emailField = new TextField(userEmail);
        emailField.setEditable(false);

        Label themeLabel = new Label("Theme:");
        ComboBox<String> themeBox = new ComboBox<>();
        themeBox.getItems().addAll("Light", "Dark");
        themeBox.setValue("Light");
        Label notificationLabel = new Label("Notifications:");
        CheckBox notificationsCheckBox = new CheckBox("Enable reminders and notifications");
        notificationsCheckBox.setSelected(true);
        
        themeBox.setOnAction(e -> {
            String selectedTheme = themeBox.getValue();

            if (selectedTheme.equals("Dark")) {
                applyDarkMode();
                settingsContent.setStyle(
                    "-fx-border-color: #444;" +
                    "-fx-border-radius: 10;" +
                    "-fx-background-radius: 10;" +
                    "-fx-background-color: #2B2B2B;"
                );
                accountTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
                emailLabel.setStyle("-fx-text-fill: white;");
                themeLabel.setStyle("-fx-text-fill: white;");
                notificationLabel.setStyle("-fx-text-fill: white;");
                notificationsCheckBox.setStyle("-fx-text-fill: white;");
            } else {
                applyLightMode();
                settingsContent.setStyle(
                    "-fx-border-color: #E0E0E0;" +
                    "-fx-border-radius: 10;" +
                    "-fx-background-radius: 10;" +
                    "-fx-background-color: white;"
                );
                accountTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
                emailLabel.setStyle("-fx-text-fill: #333;");
                themeLabel.setStyle("-fx-text-fill: #333;");
                notificationLabel.setStyle("-fx-text-fill: #333;");
                notificationsCheckBox.setStyle("-fx-text-fill: #333;");
            }
        });


        Button saveBtn = new Button("Save Settings");
        saveBtn.setStyle("-fx-background-color: #4A6FA5; -fx-text-fill: white; -fx-font-weight: bold;");

        Label statusLabel = new Label();

        saveBtn.setOnAction(e -> {
            String selectedTheme = themeBox.getValue();
            boolean notificationsOn = notificationsCheckBox.isSelected();

            statusLabel.setText("Settings saved: Theme = " + selectedTheme +
                    ", Notifications = " + (notificationsOn ? "On" : "Off"));
            statusLabel.setStyle("-fx-text-fill: green;");
        });

        settingsContent.getChildren().addAll(
                accountTitle,
                emailLabel,
                emailField,
                themeLabel,
                themeBox,
                notificationLabel,
                notificationsCheckBox,
                saveBtn,
                statusLabel
        );

        HBox centerRow = new HBox(12, sidebar, settingsContent);
        centerRow.setAlignment(Pos.TOP_LEFT);

        setCenter(centerRow);
    }
    
    private void applyLightMode() {
        setStyle("-fx-background-color: #F5F7FA;");
    }

    private void applyDarkMode() {
        setStyle("-fx-background-color: #1E1E1E;");
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
            btn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-alignment: center-left;" +
                "-fx-padding: 10;"
            );
        }

        settingsBtn.setStyle(
            "-fx-background-color: white;" +
            "-fx-text-fill: #4A6FA5;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-alignment: center-left;" +
            "-fx-padding: 10;"
        );

        homeBtn.setOnAction(e -> stage.getScene().setRoot(new MainView(stage, userEmail)));
        tasksBtn.setOnAction(e -> stage.getScene().setRoot(new TasksView(stage, userEmail)));
        notesBtn.setOnAction(e -> stage.getScene().setRoot(new NotesView(stage, userEmail)));
        coursesBtn.setOnAction(e -> stage.getScene().setRoot(new CoursesView(stage, userEmail)));
        settingsBtn.setOnAction(e -> stage.getScene().setRoot(new SettingsView(stage, userEmail)));

        sidebar.getChildren().addAll(homeBtn, tasksBtn, notesBtn, coursesBtn, settingsBtn);

        return sidebar;
    }
}