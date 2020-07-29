package com.verban.media.ui;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.*;
import javafx.stage.*;

/**
* Main class for initiating and controlling the UI of the application.
*
* @author Michael Verban (2020)
*/
public class UI{
	//Static variables for the GUI
	private static final String APP_TITLE = "Media Library";
	private static final int WINDOW_WIDTH = 640;
	private static final int WINDOW_HEIGHT = 480;

	public void start(Stage primaryStage) throws Exception {
		BorderPane mainPane = new BorderPane();
/*
		//Setup top row of buttons
		HBox menuBar = createTopSection(primaryStage);

		mainPane.setTop(menuBar);
		//Add spacing below the menuBar
		Insets menuBarInsets = new Insets(0, 0, 10, 0);
		BorderPane.setMargin(menuBar, menuBarInsets);


		//Setup left side of screen
		mainPane.setLeft(createLeftSection());


		//Setup right side in Center to make it fill the rest of the screen and not split evenly with left
		dataTable = new TableView();
		dataTable.setPlaceholder(new Label("No report to display"));
		mainPane.setCenter(dataTable);

		//Load Bottom
		mainPane.setBottom(createBottomSection());

*/
		Scene mainScene = new Scene(mainPane, WINDOW_WIDTH, WINDOW_HEIGHT);

		// Add the scene and display the primary stage
		primaryStage.setTitle(APP_TITLE);
		primaryStage.setScene(mainScene);
		primaryStage.show();
	}
}
