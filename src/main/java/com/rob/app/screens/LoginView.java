package com.rob.app.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LoginView extends VBox {

    public LoginView(Stage stage) {

        setPadding(new Insets(30));
        setSpacing(15);
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: #F5F5F7;");

        Label title = new Label("Study.io");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #4A90E2;");

        TextField email = new TextField();
        email.setPromptText("Email");
        email.setStyle(inputStyle());

        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        password.setStyle(inputStyle());

        Label status = new Label();
        status.setStyle("-fx-text-fill: #E74C3C;");

        Button loginBtn = new Button("Log In");
        loginBtn.setDefaultButton(true);
        loginBtn.setStyle(primaryButtonStyle());

        Button signupBtn = new Button("Sign Up");
        signupBtn.setStyle(secondaryButtonStyle());

        // Login logic
        loginBtn.setOnAction(e -> {
            String userEmail = email.getText().trim();
            String userPassword = password.getText();

            if (userEmail.isEmpty() || userPassword.isEmpty()) {
                status.setText("Please enter email and password.");
                return;
            }

            boolean authenticated = false;

            try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts[0].equals(userEmail) && parts[1].equals(userPassword)) {
                        authenticated = true;
                        break;
                    }
                }
            } catch (IOException ex) {
                status.setText("Error reading users file.");
                return;
            }

            if (authenticated) {
                stage.getScene().setRoot(new MainView(stage, userEmail));
            } else {
                status.setText("Invalid email or password.");
            }
        });

        signupBtn.setOnAction(e -> stage.getScene().setRoot(new SignupView(stage)));

        getChildren().addAll(title, email, password, loginBtn, signupBtn, status);
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