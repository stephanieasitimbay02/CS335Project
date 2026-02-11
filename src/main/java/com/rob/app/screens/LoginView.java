package com.rob.app.screens;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView extends VBox {
	
	public LoginView(Stage stage) {
		setPadding(new Insets(24));
		setSpacing(12);
		
		Label title = new Label("Login");
		title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
		
		TextField email = new TextField();
        email.setPromptText("Email");
        
        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        Label status = new Label();

        Button loginBtn = new Button("Sign in");
        loginBtn.setDefaultButton(true);
        
        loginBtn.setOnAction(e -> {
            if (email.getText().trim().isEmpty() || password.getText().isEmpty()) {
                status.setText("Please enter email + password.");
                return;
            }
            stage.getScene().setRoot(new MainView(stage));
        });
        
        getChildren().addAll(title, email, password, loginBtn, status);
        
	}

}
