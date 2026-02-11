package com.rob.app.screens;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;



public class MainView extends BorderPane {
	
	public MainView(Stage stage) {
		setPadding(new Insets(16));
		
		Label header = new Label("Main Page");
		header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
		
		Button logout = new Button("Log out");
		logout.setOnAction(e -> stage.getScene().setRoot(new LoginView(stage)));
		
		setTop(header);
		setCenter(new Label("You are logged in!"));
		setBottom(logout);
		BorderPane.setMargin(logout, new Insets(12, 0, 0, 0));
	}

}
