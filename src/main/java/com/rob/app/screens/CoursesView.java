package com.rob.app.screens;

import java.util.Optional;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;
import java.util.List;
import java.util.stream.Collectors;


public class CoursesView extends BorderPane {
	// Right-side detail labels
    private Label selectedCourseTitle;
    private Label courseNameValue;
    private Label instructorValue;
    private Label weeklyScheduleValue;
    private Label courseInfoValue;
    private String userEmail;
    private String coursesFile;

	
	
	public CoursesView(Stage stage, String userEmail) {
        setPadding(new Insets(16));
        setStyle("-fx-background-color: #F5F7FA;");
        this.userEmail = userEmail;
        String safeEmail = userEmail.replaceAll("[^a-zA-Z0-9]", "_");
        File userDir = new File("users/" + safeEmail);
        // creating folder if it doesn't already exist
        if (!userDir.exists()) {
        	userDir.mkdirs();
        }
        this.coursesFile = new File(userDir, "courses.txt").getPath();
        
        File file = new File(coursesFile);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("Error creating courses file");
        }
        
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

        // LEFT PANEL: course list 
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
            Optional<Course> result = showAddCourseDialog();

            result.ifPresent(course -> {
                HBox courseItem = new HBox(10);
                courseItem.setAlignment(Pos.CENTER_LEFT);
                courseItem.setStyle(
                    "-fx-background-color: white; " +
                    "-fx-padding: 10; " +
                    "-fx-border-color: #e0e0e0; " +
                    "-fx-border-radius: 12; " +
                    "-fx-background-radius: 12;"
                );

                Label courseLabel = new Label("📚 " + course.getName());
                courseLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
                courseLabel.setPrefWidth(220);

                Button viewBtn = new Button("View");
                viewBtn.setStyle("-fx-background-color: #4A6FA5; -fx-text-fill: white; -fx-background-radius: 10;");
                viewBtn.setOnAction(ev -> displayCourseDetails(course));

                Button deleteBtn = new Button("Delete");
                deleteBtn.setStyle("-fx-background-color: #E63946; -fx-text-fill: white; -fx-background-radius: 10;");
                deleteBtn.setOnAction(ev -> {
                    courseList.getChildren().remove(courseItem);

                    // If the deleted course is currently displayed, clear the detail panel
                    if (selectedCourseTitle.getText().equals(course.getName())) {
                        clearCourseDetails();
                    }
                });

                // Clicking the whole item also shows details
                courseItem.setOnMouseClicked(ev -> displayCourseDetails(course));

                courseItem.getChildren().addAll(courseLabel, viewBtn, deleteBtn);
                courseList.getChildren().add(courseItem);

                // Automatically show the newly added course on the right
                displayCourseDetails(course);
            });
        });

        VBox addCourseBox = new VBox(addCourseBtn);
        addCourseBox.setAlignment(Pos.CENTER);

        courseContent.getChildren().addAll(title, addCourseBox, courseList);

        // RIGHT PANEL: COURSE DETAILS
        VBox detailsPanel = createDetailsPanel();

        HBox centerRow = new HBox(12, sidebar, courseContent, detailsPanel);
        centerRow.setAlignment(Pos.TOP_LEFT);
        setCenter(centerRow);
    }

    private VBox createDetailsPanel() {
        VBox detailsPanel = new VBox(15);
        detailsPanel.setPadding(new Insets(15));
        detailsPanel.setPrefWidth(420);
        detailsPanel.setStyle(
            "-fx-border-color: #E0E0E0; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10; " +
            "-fx-background-color: white;"
        );

        selectedCourseTitle = new Label("Course Details");
        selectedCourseTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #4A6FA5;");

        courseNameValue = createValueLabel("No course selected");
        instructorValue = createValueLabel("-");
        weeklyScheduleValue = createValueLabel("-");
        courseInfoValue = createValueLabel("-");

        VBox nameBox = createFieldBox("Course Name", courseNameValue);
        VBox instructorBox = createFieldBox("Instructor", instructorValue);
        VBox scheduleBox = createFieldBox("Weekly Schedule", weeklyScheduleValue);
        VBox infoBox = createFieldBox("Course Info", courseInfoValue);

        detailsPanel.getChildren().addAll(
            selectedCourseTitle,
            nameBox,
            instructorBox,
            scheduleBox,
            infoBox
        );

        return detailsPanel;
    }

    private VBox createFieldBox(String labelText, Label valueLabel) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;");

        VBox box = new VBox(5, label, valueLabel);
        box.setPadding(new Insets(8));
        box.setStyle(
            "-fx-background-color: #F8F9FB; " +
            "-fx-border-color: #E6EAF0; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8;"
        );
        return box;
    }

    private Label createValueLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");
        return label;
    }

    private void displayCourseDetails(Course course) {
        selectedCourseTitle.setText(course.getName());
        courseNameValue.setText(course.getName());
        instructorValue.setText(course.getInstructor());
        weeklyScheduleValue.setText(course.getWeeklySchedule());
        courseInfoValue.setText(course.getCourseInfo());
    }

    private void clearCourseDetails() {
        selectedCourseTitle.setText("Course Details");
        courseNameValue.setText("No course selected");
        instructorValue.setText("-");
        weeklyScheduleValue.setText("-");
        courseInfoValue.setText("-");
    }

    private Optional<Course> showAddCourseDialog() {
        Dialog<Course> dialog = new Dialog<>();
        dialog.setTitle("Add Course");
        dialog.setHeaderText("Enter Course Information");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("Course name");

        TextField instructorField = new TextField();
        instructorField.setPromptText("Instructor name");

        TextField scheduleField = new TextField();
        scheduleField.setPromptText("Example: Mon/Wed 10:00 AM - 11:15 AM");

        TextArea infoArea = new TextArea();
        infoArea.setPromptText("Enter course description, room, assignments, etc.");
        infoArea.setPrefRowCount(4);
        infoArea.setWrapText(true);

        grid.add(new Label("Course Name:"), 0, 0);
        grid.add(nameField, 1, 0);

        grid.add(new Label("Instructor:"), 0, 1);
        grid.add(instructorField, 1, 1);

        grid.add(new Label("Weekly Schedule:"), 0, 2);
        grid.add(scheduleField, 1, 2);

        grid.add(new Label("Course Info:"), 0, 3);
        grid.add(infoArea, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String name = nameField.getText().trim();
                String instructor = instructorField.getText().trim();
                String schedule = scheduleField.getText().trim();
                String info = infoArea.getText().trim();

                if (!name.isEmpty()) {
                    return new Course(
                        name,
                        instructor.isEmpty() ? "Not provided" : instructor,
                        schedule.isEmpty() ? "Not provided" : schedule,
                        info.isEmpty() ? "No additional information" : info
                    );
                }
            }
            return null;
        });

        return dialog.showAndWait();
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
            btn.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-alignment: center-left; " +
                "-fx-padding: 10;"
            );
        }

        homeBtn.setOnAction(e -> stage.getScene().setRoot(new MainView(stage, userEmail)));
        tasksBtn.setOnAction(e -> stage.getScene().setRoot(new TasksView(stage, userEmail)));
        notesBtn.setOnAction(e -> stage.getScene().setRoot(new NotesView(stage, userEmail)));
        coursesBtn.setOnAction(e -> stage.getScene().setRoot(new CoursesView(stage, userEmail)));

        sidebar.getChildren().addAll(homeBtn, tasksBtn, notesBtn, coursesBtn);
        return sidebar;
    }

    // Simple model class for a course
    private static class Course {
        private final String name;
        private final String instructor;
        private final String weeklySchedule;
        private final String courseInfo;

        public Course(String name, String instructor, String weeklySchedule, String courseInfo) {
            this.name = name;
            this.instructor = instructor;
            this.weeklySchedule = weeklySchedule;
            this.courseInfo = courseInfo;
        }

        public String getName() {
            return name;
        }

        public String getInstructor() {
            return instructor;
        }

        public String getWeeklySchedule() {
            return weeklySchedule;
        }

        public String getCourseInfo() {
            return courseInfo;
        }
    }
}