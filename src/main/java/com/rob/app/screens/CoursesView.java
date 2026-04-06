package com.rob.app.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class CoursesView extends BorderPane {

    public CoursesView(Stage stage) {
        setPadding(new Insets(16));
        setStyle("-fx-background-color: #F5F7FA;");

        // HEADER
        Label header = new Label("Courses");
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

        // COURSES CONTENT
        VBox courseContent = new VBox(15);
        courseContent.setPadding(new Insets(10));
        courseContent.setStyle("-fx-border-color: #E0E0E0; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: white;");
        courseContent.setPrefWidth(400);

        Label title = new Label("Your Courses");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        VBox courseList = new VBox(8);

        Button addCourseBtn = new Button("+ Add Course");
        addCourseBtn.setPrefSize(300, 50);
        addCourseBtn.setStyle("-fx-background-color: white; -fx-border-color: #dcdcdc; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-size: 16px; -fx-font-weight: bold;");
        addCourseBtn.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Course");
            dialog.setHeaderText("Enter Course Name");
            dialog.showAndWait().ifPresent(courseName -> {
                if (!courseName.trim().isEmpty()) {
                    HBox courseItem = new HBox(10);
                    courseItem.setAlignment(Pos.CENTER_LEFT);
                    courseItem.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #e0e0e0; -fx-border-radius: 12; -fx-background-radius: 12;");

                    Label courseLabel = new Label("📚 " + courseName);
                    courseLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

                    Button deleteBtn = new Button("Delete");
                    deleteBtn.setStyle("-fx-background-color: #E63946; -fx-text-fill: white; -fx-background-radius: 10;");
                    deleteBtn.setOnAction(ev -> courseList.getChildren().remove(courseItem));

                    courseItem.getChildren().addAll(courseLabel, deleteBtn);
                    courseList.getChildren().add(courseItem);
                }
            });
        });

        VBox addCourseBox = new VBox(addCourseBtn);
        addCourseBox.setAlignment(Pos.CENTER);

        courseContent.getChildren().addAll(title, addCourseBox, courseList);

        HBox centerRow = new HBox(12, sidebar, courseContent);
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
}