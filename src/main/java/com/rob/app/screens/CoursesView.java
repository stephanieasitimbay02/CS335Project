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

        if (!userDir.exists()) {
            userDir.mkdirs();
        }

        this.coursesFile = new File(userDir, "courses.txt").getPath();

        try {
            File file = new File(coursesFile);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("Error creating courses file");
        }

        Label header = new Label("Courses");
        header.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #4A6FA5;");
        setTop(header);
        BorderPane.setAlignment(header, Pos.CENTER);

        Button logout = new Button("Log out");
        logout.setOnAction(e -> stage.getScene().setRoot(new LoginView(stage)));
        setBottom(logout);
        BorderPane.setAlignment(logout, Pos.CENTER);

        VBox sidebar = createSidebar(stage);

        VBox courseContent = new VBox(15);
        courseContent.setPadding(new Insets(10));
        courseContent.setStyle("-fx-border-color: #E0E0E0; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: white;");
        courseContent.setPrefWidth(400);

        Label title = new Label("Your Courses");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        VBox courseList = new VBox(8);

        // LOAD SAVED COURSES
        loadCourses(courseList);

        Button addCourseBtn = new Button("+ Add Course");
        addCourseBtn.setPrefSize(300, 50);
        addCourseBtn.setStyle("-fx-background-color: white; -fx-border-color: #dcdcdc; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-size: 16px; -fx-font-weight: bold;");

        addCourseBtn.setOnAction(e -> {
            Optional<Course> result = showAddCourseDialog();

            result.ifPresent(course -> {
                saveCourseToFile(course);
                addCourseToUI(courseList, course);
                displayCourseDetails(course);
            });
        });

        VBox addCourseBox = new VBox(addCourseBtn);
        addCourseBox.setAlignment(Pos.CENTER);

        courseContent.getChildren().addAll(title, addCourseBox, courseList);

        VBox detailsPanel = createDetailsPanel();

        HBox centerRow = new HBox(12, sidebar, courseContent, detailsPanel);
        centerRow.setAlignment(Pos.TOP_LEFT);
        setCenter(centerRow);
    }

    private void saveCourseToFile(Course course) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(coursesFile, true))) {
            writer.write(course.getName() + "||" +
                    course.getInstructor() + "||" +
                    course.getWeeklySchedule() + "||" +
                    course.getCourseInfo());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving course");
        }
    }

    private void loadCourses(VBox courseList) {
        try (BufferedReader reader = new BufferedReader(new FileReader(coursesFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|\\|");

                if (parts.length == 4) {
                    Course course = new Course(
                            parts[0],
                            parts[1],
                            parts[2],
                            parts[3]
                    );

                    addCourseToUI(courseList, course);
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading courses");
        }
    }

    private void addCourseToUI(VBox courseList, Course course) {
        HBox courseItem = new HBox(10);
        courseItem.setAlignment(Pos.CENTER_LEFT);
        courseItem.setStyle(
                "-fx-background-color: white;" +
                        "-fx-padding: 10;" +
                        "-fx-border-color: #e0e0e0;" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;"
        );

        Label courseLabel = new Label("📚 " + course.getName());
        courseLabel.setPrefWidth(220);

        Button viewBtn = new Button("View");
        viewBtn.setOnAction(ev -> displayCourseDetails(course));

        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(ev -> {
            courseList.getChildren().remove(courseItem);
            rewriteCoursesFile(courseList);
            clearCourseDetails();
        });

        courseItem.setOnMouseClicked(ev -> displayCourseDetails(course));

        courseItem.getChildren().addAll(courseLabel, viewBtn, deleteBtn);
        courseList.getChildren().add(courseItem);
    }

    private void rewriteCoursesFile(VBox courseList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(coursesFile))) {

            for (javafx.scene.Node node : courseList.getChildren()) {
                HBox item = (HBox) node;
                Label label = (Label) item.getChildren().get(0);

                String name = label.getText().replace("📚 ", "");

                writer.write(name + "||" + "Saved" + "||" + "Saved" + "||" + "Saved");
                writer.newLine();
            }

        } catch (IOException e) {
            System.out.println("Error rewriting file");
        }
    }

    private VBox createDetailsPanel() {
        VBox detailsPanel = new VBox(15);
        detailsPanel.setPadding(new Insets(15));
        detailsPanel.setPrefWidth(420);
        detailsPanel.setStyle(
                "-fx-border-color: #E0E0E0;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
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

    private Label createValueLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        return label;
    }

    private VBox createFieldBox(String labelText, Label valueLabel) {
        Label label = new Label(labelText);
        VBox box = new VBox(5, label, valueLabel);
        return box;
    }

    private Optional<Course> showAddCourseDialog() {
        Dialog<Course> dialog = new Dialog<>();
        dialog.setTitle("Add Course");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        TextField instructorField = new TextField();
        TextField scheduleField = new TextField();
        TextArea infoArea = new TextArea();

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
                return new Course(
                        nameField.getText(),
                        instructorField.getText(),
                        scheduleField.getText(),
                        infoArea.getText()
                );
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private VBox createSidebar(Stage stage) {
        VBox sidebar = new VBox(12);

        Button homeBtn = new Button("Home");
        Button tasksBtn = new Button("Tasks");
        Button notesBtn = new Button("Notes");
        Button coursesBtn = new Button("Courses");

        homeBtn.setOnAction(e -> stage.getScene().setRoot(new MainView(stage, userEmail)));
        tasksBtn.setOnAction(e -> stage.getScene().setRoot(new TasksView(stage, userEmail)));
        notesBtn.setOnAction(e -> stage.getScene().setRoot(new NotesView(stage, userEmail)));
        coursesBtn.setOnAction(e -> stage.getScene().setRoot(new CoursesView(stage, userEmail)));

        sidebar.getChildren().addAll(homeBtn, tasksBtn, notesBtn, coursesBtn);
        return sidebar;
    }

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

        public String getName() { return name; }
        public String getInstructor() { return instructor; }
        public String getWeeklySchedule() { return weeklySchedule; }
        public String getCourseInfo() { return courseInfo; }
    }
}