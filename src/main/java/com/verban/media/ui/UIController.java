package com.verban.media.ui;

import com.verban.media.*;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.*;
import javafx.stage.*;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import java.net.*;
import java.io.*;
import java.util.*;

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
	private TableView<Song> songList;
	@FXML
	private ListView<Album> albumList;
	@FXML
	private ListView<Song> albumSongList;
	@FXML
	private ListView<Artist> artistList;
	@FXML
	private ListView<Song> artistSongList;
	@FXML
	private ListView<Playlist> playlistList;
	@FXML
	private ListView<Song> playlistSongList;

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
	* Sets each of the main list/table views to be backed by the propper list in the Library.
	* This should only need to be called once at the creation of the UI, but subsequent calls should not break anything.
	*/
	private void linkLists(){
		songList.setItems(library.getSongs());

		TableColumn<Song, String> titleColumn = new TableColumn<>("Title");
		titleColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("title"));

		TableColumn<Song, String> artistColumn = new TableColumn<>("Artist");
		artistColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("artist"));

		TableColumn<Song, String> runTimeColumn = new TableColumn<>("Run Time");
		runTimeColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("runtime"));

		TableColumn<Song, String> albumColumn = new TableColumn<>("Album");
		albumColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("originalAlbum"));

		songList.getColumns().addAll(titleColumn, artistColumn, albumColumn, runTimeColumn);



		albumList.setItems(library.getAlbums());
		albumList.setOnMouseClicked(e -> {
			ObservableList<Album> selected = albumList.getSelectionModel().getSelectedItems();
			if(selected.size() > 0){
				Album selAlbum = selected.get(0);
				albumSongList.getItems().clear();
				albumSongList.getItems().addAll(selAlbum.getAllTracks());
				System.out.println(Arrays.toString(selAlbum.getAllTracks()));
			}else{
				albumSongList.getItems().clear();
			}
		});
		artistList.setItems(library.getArtists());
		playlistList.setItems(library.getPlaylists());
	}

	/**
	* Attempts to load in a library file specified by the user.
	*/
	@FXML
	public void loadLibrary(){
		File libFile = libraryChooser.showOpenDialog(mainStage);
		if(libFile != null){
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
	}

	/**
	* Attempts to save teh current libary to a file specified by the user.
	*/
	@FXML
	public void saveLibrary(){
		File libFile = libraryChooser.showSaveDialog(mainStage);
		if(libFile != null){
			try{
				library.save(libFile);
				Alert alert = new Alert(Alert.AlertType.INFORMATION, "Sucessfully saved library");
				alert.showAndWait();
			}catch(FileFormatException e){
				Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
				alert.showAndWait();
			}catch(IOException e){
				Alert alert = new Alert(Alert.AlertType.ERROR, "Save Failed: " + e.getMessage());
				alert.showAndWait();
			}
		}
	}

	/**
	* Attempts to import a single media file
	*/
	@FXML
	public void importFile(){
		File mediaFile = mediaChooser.showOpenDialog(mainStage);
		if(mediaFile != null){
			try{
				System.out.println(mediaFile);
				Song s = new Song(mediaFile);
				System.out.println("Loaded file");
				library.addSong(s);
			}catch(FileFormatException e){
				Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
				alert.showAndWait();
			}catch(IOException e){
				Alert alert = new Alert(Alert.AlertType.ERROR, "Import Failed: " + e.getMessage());
				alert.showAndWait();
			}
		}
	}

	@FXML
	public void importFolder(){
		File mediaFolder = folderChooser.showDialog(mainStage);
		if(mediaFolder != null){
			int read = importFolderHelper(mediaFolder);
			Alert alert = new Alert(Alert.AlertType.INFORMATION, "Sucessfully imported " + read + " files.");
			alert.showAndWait();
		}
	}

	/**
	* Helper method for reading directories recursively. Returns the number of files successfully read.
	*/
	private int importFolderHelper(File folder){
		int read = 0;
		if(folder != null){
			for(File f : folder.listFiles()){
				if(f.isFile()){
					try{
						Song s = new Song(f);
						library.addSong(s);
						read++;
					}catch(Exception e){}
				}else if(f.isDirectory()){
					read += importFolderHelper(f);
				}
			}
		}
		return read;
	}
}
