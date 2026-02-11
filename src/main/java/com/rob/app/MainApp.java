package com.rob.app;

import com.rob.app.screens.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
	
	@Override
	public void start(Stage stage) {
		stage.setTitle("Studyio App");
		Scene scene = new Scene(new LoginView(stage), 420, 520);
		stage.setScene(scene);
		stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
