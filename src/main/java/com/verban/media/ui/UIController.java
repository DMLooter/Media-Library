package com.verban.media.ui;

import com.verban.media.*;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.*;
import javafx.stage.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import java.net.*;
import java.io.*;

/**
* Main class for initiating and controlling the UI of the application.
*
* @author Michael Verban (2020)
*/
public class UIController{
	//Static variables for the GUI
	private static final String APP_TITLE = "Media Library";

	private Scene mainScene;
	private Stage mainStage;

	@FXML
	private ListView songList;
	@FXML
	private ListView albumList;
	@FXML
	private ListView albumSongList;
	@FXML
	private ListView artistList;
	@FXML
	private ListView artistSongList;
	@FXML
	private ListView playlistList;
	@FXML
	private ListView playlistSongList;

	private FileChooser libraryChooser;
	private FileChooser mediaChooser;
	private DirectoryChooser folderChooser;

	// The currently loaded library.
	private Library library;

	public void start(Stage primaryStage) throws Exception {

		// Setup the file pickers with the proper ExtensionFilters
		libraryChooser = new FileChooser();
		mediaChooser = new FileChooser();
		folderChooser = new DirectoryChooser();

		libraryChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Library Files", "*.library"));
		libraryChooser.setInitialDirectory(new File(System.getProperty("user.home")+"/Music"));
		mediaChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio Files",
			"*.mp3", "*.wav","*.wma","*.aac", "*.m4a","*.flac","*.ogg"));
		mediaChooser.setInitialDirectory(new File(System.getProperty("user.home")+"/Music"));
		folderChooser.setInitialDirectory(new File(System.getProperty("user.home")+"/Music"));

		// Initialize an empty library on startup
		library = new Library();

		// Load the actual UI and connect it to this class as a controller
		FXMLLoader loader = new FXMLLoader();
		loader.setController(this);
		loader.setLocation(getClass().getResource("/com/verban/media/ui/UI.fxml"));
		VBox vbox = loader.<VBox>load();

		linkLists();

		mainScene = new Scene(vbox);
		// Add the scene and display the primary stage
		primaryStage.setTitle(APP_TITLE);
		primaryStage.setScene(mainScene);
		primaryStage.show();
		mainStage = primaryStage;
	}

	/**
	* Sets each of the main list views to be backed by the propper list in the Library.
	* This should only need to be called once at the creation of the UI, but subsequent calls should not break anything.
	*/
	private void linkLists(){
		songList.setItems(library.getSongs());
		albumList.setItems(library.getAlbums());
		artistList.setItems(library.getArtists());
		playlistList.setItems(library.getPlaylists());
	}

	@FXML
	public void loadLibrary(){
		File libFile = libraryChooser.showOpenDialog(mainStage);
		try{
			library.load(libFile);
			Alert alert = new Alert(Alert.AlertType.INFORMATION, "Sucessfully loaded library");
			alert.showAndWait();
		}catch(FileFormatException e){
			Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
			alert.showAndWait();
		}catch(IOException e){
			Alert alert = new Alert(Alert.AlertType.ERROR, "Read Failed: " + e.getMessage());
			alert.showAndWait();
		}
	}

	@FXML
	public void saveLibrary(){
		File libFile = libraryChooser.showSaveDialog(mainStage);
		try{
			library.save(libFile);
			Alert alert = new Alert(Alert.AlertType.INFORMATION, "Sucessfully saved library");
			alert.showAndWait();
		}catch(FileFormatException e){
			Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
			alert.showAndWait();
		}catch(IOException e){
			Alert alert = new Alert(Alert.AlertType.ERROR, "Read Failed: " + e.getMessage());
			alert.showAndWait();
		}
	}
}
