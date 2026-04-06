package com.rob.app.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.*;

public class SignupView extends VBox {

    public SignupView(Stage stage) {

        setPadding(new Insets(30));
        setSpacing(15);
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: #F5F5F7;");

        Label title = new Label("Create Account");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #4A90E2;");

        TextField email = new TextField();
        email.setPromptText("Email");
        email.setStyle(inputStyle());

        PasswordField password = new PasswordField();
        password.setPromptText("Create Password");
        password.setStyle(inputStyle());

        Label status = new Label();
        status.setStyle("-fx-text-fill: #E74C3C;");

        Button createAccount = new Button("Create Account");
        createAccount.setStyle(primaryButtonStyle());

        Button backButton = new Button("Back to Login");
        backButton.setStyle(secondaryButtonStyle());

        createAccount.setOnAction(e -> {

            String userEmail = email.getText().trim();
            String userPassword = password.getText();

            if (userEmail.isEmpty() || userPassword.isEmpty()) {
                status.setText("Please fill in all fields.");
                return;
            }

            if (!userEmail.contains("@")) {
                status.setText("Please enter a valid email.");
                return;
            }

            if (userPassword.length() < 6) {
                status.setText("Password must be at least 6 characters.");
                return;
            }

            boolean userExists = false;

            try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts[0].equals(userEmail)) {
                        userExists = true;
                        break;
                    }
                }
            } catch (IOException ex) {
                status.setText("Error reading users file.");
                return;
            }

            if (userExists) {
                status.setText("An account with this email already exists.");
                return;
            }

            try (FileWriter writer = new FileWriter("users.txt", true)) {
                writer.write(userEmail + "," + userPassword + "\n");
                status.setStyle("-fx-text-fill: #4A90E2;");
                status.setText("Account created! Go back and log in.");
            } catch (IOException ex) {
                status.setText("Error saving account.");
            }
        });

        backButton.setOnAction(e -> stage.getScene().setRoot(new LoginView(stage)));

        getChildren().addAll(title, email, password, createAccount, backButton, status);
    }

    private String inputStyle() {
        return "-fx-background-color: #FFFFFF; " +
               "-fx-border-color: #E0E0E0; " +
               "-fx-border-radius: 8; " +
               "-fx-background-radius: 8; " +
               "-fx-padding: 8 12 8 12; " +
               "-fx-font-size: 14;";
    }

    private String primaryButtonStyle() {
        return "-fx-background-color: #4A90E2; " +
               "-fx-text-fill: white; " +
               "-fx-font-weight: bold; " +
               "-fx-background-radius: 10; " +
               "-fx-padding: 8 20 8 20;" +
               "-fx-cursor: hand;" +
               "-fx-font-size: 14;";
    }

    private String secondaryButtonStyle() {
        return "-fx-background-color: #FFFFFF; " +
               "-fx-text-fill: #4A90E2; " +
               "-fx-border-color: #4A90E2; " +
               "-fx-border-radius: 10; " +
               "-fx-background-radius: 10; " +
               "-fx-padding: 8 20 8 20;" +
               "-fx-cursor: hand;" +
               "-fx-font-size: 14;";
    }
}