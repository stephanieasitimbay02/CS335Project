package com.rob.app.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import com.calendarfx.view.CalendarView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;



public class MainView extends BorderPane {
	
	public MainView(Stage stage) {
		setPadding(new Insets(16));
		
		Label header = new Label("Home");
		header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
		
		Button logout = new Button("Log out");
		logout.setOnAction(e -> stage.getScene().setRoot(new LoginView(stage)));
		
		setTop(header);
		CalendarView calendarView = new CalendarView();
		calendarView.setShowAddCalendarButton(false);
		calendarView.setShowPrintButton(false);
		calendarView.setShowSearchField(false);
		
		VBox calendarCard = new VBox(calendarView);
		calendarCard.setPadding(new Insets(10));
		calendarCard.setStyle("-fx-border-color: lightgray; -fx-border-radius: 8; -fx-background-radius: 8;");

		calendarCard.setPrefSize(520, 420);
		calendarView.setPrefSize(520,  420);
		
		
		VBox leftPanel = new VBox(10);
		leftPanel.setPadding(new Insets(10));
		leftPanel.setPrefSize(520, 420);     
		leftPanel.setMinWidth(520);           // prevent from shrinking too much
		HBox.setHgrow(leftPanel, javafx.scene.layout.Priority.ALWAYS);
		leftPanel.setMaxWidth(Double.MAX_VALUE);
		leftPanel.setStyle("-fx-border-color: lightgray; -fx-border-radius: 8; -fx-background-radius: 8;");
		leftPanel.getChildren().addAll(
				new Label("To Do")
				);
		
		// let left panel grow to fill remaining space 
		leftPanel.setMinWidth(Region.USE_COMPUTED_SIZE);
		
		HBox centerRow = new HBox(12, leftPanel, calendarCard);
		centerRow.setAlignment(Pos.TOP_LEFT);
		
		setCenter(centerRow);
		
		setBottom(logout);
		
		BorderPane.setAlignment(header, Pos.CENTER);
        BorderPane.setAlignment(logout, Pos.CENTER);

	}

}
