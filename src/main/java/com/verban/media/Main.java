package com.verban.media;
import com.verban.media.ui.UI;
import javafx.application.Application;
import javafx.stage.Stage;

/**
* Main class to launch the JFX Application
* @author Michael Verban (2020)
*/
public class Main extends Application{
	private UI mainUI;

	@Override
	public void start(Stage primaryStage) throws Exception {
		mainUI = new UI();
		mainUI.start(primaryStage);
	}

	/**
	 * @param args
	 *            unused command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
