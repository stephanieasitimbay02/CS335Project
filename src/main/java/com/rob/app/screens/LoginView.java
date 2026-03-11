package com.rob.app.screens;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LoginView extends VBox {

    public LoginView(Stage stage) {

        setPadding(new Insets(24));
        setSpacing(12);
        setAlignment(javafx.geometry.Pos.CENTER);

        Label title = new Label("Study.io");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField email = new TextField();
        email.setPromptText("Email");

        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        Label status = new Label();

        Button loginBtn = new Button("Log In");
        loginBtn.setDefaultButton(true);

        Button signupBtn = new Button("Sign Up");

        loginBtn.setOnAction(e -> {

            String userEmail = email.getText().trim();
            String userPassword = password.getText();

            if (userEmail.isEmpty() || userPassword.isEmpty()) {
                status.setText("Please enter email and password.");
                return;
            }

            boolean authenticated = false;

            try {

                BufferedReader reader = new BufferedReader(new FileReader("users.txt"));
                String line;

                while ((line = reader.readLine()) != null) {

                    String[] parts = line.split(",");

                    if (parts[0].equals(userEmail) && parts[1].equals(userPassword)) {
                        authenticated = true;
                        break;
                    }

                }

                reader.close();

            } catch (IOException ex) {

                status.setText("Error reading users file.");
                return;

            }

            if (authenticated) {
                stage.getScene().setRoot(new MainView(stage));
            } else {
                status.setText("Invalid email or password.");
            }

        });

        signupBtn.setOnAction(e -> {
            stage.getScene().setRoot(new SignupView(stage));
        });

        getChildren().addAll(title, email, password, loginBtn, signupBtn, status);
    }
}