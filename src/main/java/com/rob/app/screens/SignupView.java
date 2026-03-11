package com.rob.app.screens;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;

public class SignupView extends VBox {

    public SignupView(Stage stage) {

        setPadding(new Insets(24));
        setSpacing(12);
        setAlignment(javafx.geometry.Pos.CENTER);

        Label title = new Label("Create Account");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField email = new TextField();
        email.setPromptText("Email");

        PasswordField password = new PasswordField();
        password.setPromptText("Create Password");

        Label status = new Label();

        Button createAccount = new Button("Create Account");
        Button backButton = new Button("Back to Login");

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

            try {

                BufferedReader reader = new BufferedReader(new FileReader("users.txt"));
                String line;

                while ((line = reader.readLine()) != null) {

                    String[] parts = line.split(",");

                    if (parts[0].equals(userEmail)) {
                        userExists = true;
                        break;
                    }

                }

                reader.close();

            } catch (IOException ex) {

                status.setText("Error reading users file.");
                return;

            }

            if (userExists) {
                status.setText("An account with this email already exists.");
                return;
            }

            try {

                FileWriter writer = new FileWriter("users.txt", true);
                writer.write(userEmail + "," + userPassword + "\n");
                writer.close();

                status.setText("Account created! Go back and log in.");

            } catch (IOException ex) {

                status.setText("Error saving account.");

            }

        });

        backButton.setOnAction(e -> {
            stage.getScene().setRoot(new LoginView(stage));
        });

        getChildren().addAll(title, email, password, createAccount, backButton, status);
    }
}