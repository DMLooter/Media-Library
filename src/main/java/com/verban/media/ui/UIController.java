package com.verban.media.ui;

import com.verban.media.*;

import javafx.beans.binding.Bindings;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.*;
import javafx.stage.*;
import javafx.collections.*;
import javafx.geometry.Pos;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.util.Callback;
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
	private TabPane tabs;

	@FXML
	private TableView<Song> songList;
	@FXML
	private TableView<Album> albumList;
	@FXML
	private TableView<Song> albumSongList;
	@FXML
	private ListView<Artist> artistList;
	@FXML
	private TableView<Song> artistSongList;
	@FXML
	private ListView<Playlist> playlistList;
	@FXML
	private TableView<Song> playlistSongList;

	private FileChooser libraryChooser;
	private FileChooser mediaChooser;
	private DirectoryChooser folderChooser;

	// The currently loaded library.
	private Library library;

	// List of menus that can add songs to playlists, shown in *songList context menus
	ObservableList<MenuItem> playlistMenus;

	public void start(Stage primaryStage) throws Exception {

		// Setup the file pickers with the proper ExtensionFilters
		libraryChooser = new FileChooser();
		mediaChooser = new FileChooser();
		folderChooser = new DirectoryChooser();

		libraryChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Library Files", "*.library"));
		libraryChooser.setInitialDirectory(new File(System.getProperty("user.home")+"/Music"));
		mediaChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio Files",
			"*.mp3", "*.wma","*.aac", "*.m4a","*.flac","*.ogg"));
		mediaChooser.setInitialDirectory(new File(System.getProperty("user.home")+"/Music"));
		folderChooser.setInitialDirectory(new File(System.getProperty("user.home")+"/Music"));

		// Initialize an empty library on startup
		library = new Library();

		// Load the actual UI and connect it to this class as a controller
		FXMLLoader loader = new FXMLLoader();
		loader.setController(this);
		loader.setLocation(getClass().getResource("/com/verban/media/ui/UI.fxml"));
		VBox vbox = loader.<VBox>load();

		tabs.maxHeightProperty().bind(primaryStage.heightProperty());

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

		//Set up columns for the table.
		TableColumn<Song, String> titleColumn = new TableColumn<>("Title");
		titleColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("title"));

		TableColumn<Song, String> artistColumn = new TableColumn<>("Artist");
		artistColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("artistName"));

		TableColumn<Song, String> runTimeColumn = new TableColumn<>("Run Time");
		runTimeColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("formattedRuntime"));

		TableColumn<Song, String> albumColumn = new TableColumn<>("Album");
		albumColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("originalAlbum"));

		songList.getColumns().addAll(titleColumn, artistColumn, albumColumn, runTimeColumn);

		/*Setup the row factory to allow right clicking on individual rows
		Taken and edited from
		https://web.archive.org/web/20140406113922/https://www.marshall.edu/genomicjava/2013/12/30/javafx-tableviews-with-contextmenus/
		*/
		ContextMenu contextMenu = new ContextMenu();
		MenuItem editItem = new MenuItem("Edit");
		editItem.setOnAction(e->{
			ObservableList<Song> selected = songList.getSelectionModel().getSelectedItems();
			if(selected.size() > 0){
				showSongEditDialog(selected.get(0));
			}
		});

		Menu addToPlaylist = new Menu("Add song to playlist...");
		playlistMenus = addToPlaylist.getItems();
		updatePlaylists();

		contextMenu.getItems().addAll(editItem, addToPlaylist);
		songList.setContextMenu(contextMenu);


		albumList.setItems(library.getAlbums());
		//Set up columns for the table.
		TableColumn<Album, String> albumTitleColumn = new TableColumn<>("Title");
		albumTitleColumn.setCellValueFactory(new PropertyValueFactory<Album, String>("title"));

		TableColumn<Album, String> albumArtistColumn = new TableColumn<>("Artist");
		albumArtistColumn.setCellValueFactory(new PropertyValueFactory<Album, String>("artistName"));

		albumList.getColumns().addAll(albumTitleColumn, albumArtistColumn);
		albumList.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Album>() {
			@Override
			public void onChanged(Change<? extends Album> change) {
				ObservableList<Album> selected = albumList.getSelectionModel().getSelectedItems();
				if(selected.size() > 0){
					Album selAlbum = selected.get(0);
					albumSongList.getItems().clear();
					albumSongList.getItems().addAll(selAlbum.getAllTracks());
					albumSongList.getItems().removeAll(Song.PLACEHOLDER_LIST);
				}else{
					albumSongList.getItems().clear();
				}
			}
		});
		//Set up columns for the table, not sure why i need to do this again, but smiply reusing the existing ones didnt work.
		titleColumn = new TableColumn<>("Title");
		titleColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("title"));
		artistColumn = new TableColumn<>("Artist");
		artistColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("artistName"));
		runTimeColumn = new TableColumn<>("Run Time");
		runTimeColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("formattedRuntime"));
		albumSongList.getColumns().addAll(titleColumn, artistColumn, runTimeColumn);
		//albumSongList.setRowFactory(songListRowFactory);



		artistList.setItems(library.getArtists());
		artistList.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Artist>() {
			@Override
			public void onChanged(Change<? extends Artist> change) {
				ObservableList<Artist> selected = artistList.getSelectionModel().getSelectedItems();
				if(selected.size() > 0){
					Artist selArtist = selected.get(0);
					artistSongList.getItems().clear();
					artistSongList.getItems().addAll(selArtist.getAllSongs());
					for(Album a : selArtist.getAllAlbums()){
						artistSongList.getItems().addAll(a.getAllTracks());
					}
					albumSongList.getItems().removeAll(Song.PLACEHOLDER_LIST);
				}else{
					albumSongList.getItems().clear();
				}
			}
		});
		titleColumn = new TableColumn<>("Title");
		titleColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("title"));
		runTimeColumn = new TableColumn<>("Run Time");
		runTimeColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("formattedRuntime"));
		albumColumn = new TableColumn<>("Album");
		albumColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("originalAlbum"));
		artistSongList.getColumns().addAll(titleColumn, albumColumn, runTimeColumn);
		//artistSongList.setRowFactory(songListRowFactory);


		playlistList.setItems(library.getPlaylists());
		playlistList.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Playlist>() {
			@Override
			public void onChanged(Change<? extends Playlist> change) {
				ObservableList<Playlist> selected = playlistList.getSelectionModel().getSelectedItems();
				if(selected.size() > 0){
					Playlist selPlaylist = selected.get(0);
					playlistSongList.getItems().clear();
					playlistSongList.getItems().addAll(selPlaylist.getAllTracks());
				}else{
					playlistSongList.getItems().clear();
				}
			}
		});
		titleColumn = new TableColumn<>("Title");
		titleColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("title"));
		artistColumn = new TableColumn<>("Artist");
		artistColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("artistName"));
		runTimeColumn = new TableColumn<>("Run Time");
		runTimeColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("formattedRuntime"));
		albumColumn = new TableColumn<>("Album");
		albumColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("originalAlbum"));
		playlistSongList.getColumns().addAll(titleColumn, artistColumn, albumColumn, runTimeColumn);
		//playlistSongList.setRowFactory(songListRowFactory);

	}

	/**
	* Creates and shows a dialog allowing the user to edit the tags of a song file
	* @param song the song to edit
	*/
	private void showSongEditDialog(Song song){
		Stage popup = new Stage();
		popup.initOwner(mainStage);
		//Make sure it blocks the main application window while showing
		popup.initModality(Modality.WINDOW_MODAL);

		VBox all = new VBox(10);
		all.setAlignment(Pos.CENTER);
		//Label at the top to describe the function of this pop-up
		Label top = new Label("Edit Tags");
		all.getChildren().add(top);


		HBox titleBox = new HBox();
		Label titleLabel = new Label("Title: ");
		TextField titleInput = new TextField();
		titleInput.setText(song.getTitle());
		titleBox.getChildren().add(titleLabel);
		titleBox.getChildren().add(titleInput);
		titleBox.setAlignment(Pos.CENTER);
		all.getChildren().add(titleBox);

		HBox albumBox = new HBox();
		Label albumLabel = new Label("Album: ");
		TextField albumInput = new TextField();
		albumInput.setText(song.getOriginalAlbum());
		albumBox.getChildren().add(albumLabel);
		albumBox.getChildren().add(albumInput);
		albumBox.setAlignment(Pos.CENTER);
		all.getChildren().add(albumBox);

		HBox artistBox = new HBox();
		Label artistLabel = new Label("Artist: ");
		TextField artistInput = new TextField();
		artistInput.setText(song.getArtistName());
		artistBox.getChildren().add(artistLabel);
		artistBox.getChildren().add(artistInput);
		artistBox.setAlignment(Pos.CENTER);
		all.getChildren().add(artistBox);

		HBox yearBox = new HBox();
		Label yearLabel = new Label("Year: ");
		Spinner yearInput = new Spinner(1000, 3000, song.getYear()); //TODO Yea this needs to be editable.....
		yearInput.setPrefWidth(90);
		yearBox.getChildren().add(yearLabel);
		yearBox.getChildren().add(yearInput);
		yearBox.setAlignment(Pos.CENTER);
		all.getChildren().add(yearBox);

		HBox genreBox = new HBox();
		Label genreLabel = new Label("Genre: ");
		TextField genreInput = new TextField();
		genreInput.setText(song.getGenre());
		genreBox.getChildren().add(genreLabel);
		genreBox.getChildren().add(genreInput);
		genreBox.setAlignment(Pos.CENTER);
		all.getChildren().add(genreBox);

		HBox trackNumBox = new HBox();
		Label trackLabel = new Label("Track number ");
		Spinner trackInput = new Spinner(1,100, song.getAlbumTrackNumber());
		trackInput.setPrefWidth(70);
		Label outOfLabel = new Label(" out of ");
		Spinner numTracksInput = new Spinner(1,100, song.getAlbumTracks());
		numTracksInput.setPrefWidth(70);
		trackNumBox.getChildren().addAll(trackLabel, trackInput, outOfLabel, numTracksInput);
		trackNumBox.setAlignment(Pos.CENTER);
		all.getChildren().add(trackNumBox);


		HBox buttons = new HBox();

		Button save = new Button("Save");
		// Make the button indicate what is missing or invalid before adding anything
		save.setOnAction(e -> {
			boolean success = library.updateSong(song, titleInput.getText(), albumInput.getText(),
				artistInput.getText(), (Integer)yearInput.getValue(), genreInput.getText(),
				(Integer)trackInput.getValue(), (Integer)numTracksInput.getValue());
			if(!success){
				Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to write tags to file");
				alert.showAndWait();
			}
			popup.close();
		});

		Button cancel = new Button("Cancel");
		cancel.setOnAction(e -> popup.close());

		buttons.getChildren().addAll(save, cancel);
		buttons.setAlignment(Pos.CENTER);

		all.getChildren().add(buttons);

		Scene s = new Scene(all, 300, 400);

		popup.setScene(s);
		popup.showAndWait();
	}

	/**
	* Updates the list of playlists under the "Add to playlist" menu in the songList context menu;
	*/
	private void updatePlaylists(){
		playlistMenus.clear();
		for(Playlist playlist : library.getPlaylists()){
			MenuItem pMenu = new MenuItem(playlist.getTitle());
			pMenu.setOnAction(e -> {
				ObservableList<Song> selected = songList.getSelectionModel().getSelectedItems();
				if(selected.size() > 0){
				library.addSongToPlaylist(selected.get(0), playlist.getTitle());
				}
			});
			playlistMenus.add(pMenu);
		}

		//TODO do this for all song views
	}


	/** MENU OPTIONS ***************************************************************************/

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
				updatePlaylists();
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

	@FXML
	public void createPlaylist(){
		Stage popup = new Stage();
		popup.initOwner(mainStage);
		//Make sure it blocks the main application window while showing
		popup.initModality(Modality.WINDOW_MODAL);

		VBox all = new VBox();
		all.setAlignment(Pos.CENTER);
		//Label at the top to describe the function of this pop-up
		Label top = new Label("Create a new Playlist");
		all.getChildren().add(top);

		HBox nameBox = new HBox();
		Label nameLabel = new Label("Name: ");
		TextField nameInput = new TextField();
		nameBox.getChildren().add(nameLabel);
		nameBox.getChildren().add(nameInput);
		nameBox.setAlignment(Pos.CENTER);
		all.getChildren().add(nameBox);

		Label message = new Label();
		all.getChildren().add(message);


		HBox buttons = new HBox();

		Button create = new Button("Create");
		// Make the button indicate what is missing or invalid before adding anything
		create.setOnAction(e -> {
			if (nameInput.getText().isBlank()) { // farmID missing
				nameLabel.setUnderline(true);
			} else {
				String name = nameInput.getText();
				if(library.playlistExists(name)){
					message.setText("That playlist already exists.");
				}else{
					popup.close();
					library.createPlaylist(name);
					updatePlaylists();
				}
			}
		});

		Button cancel = new Button("Cancel");
		cancel.setOnAction(e -> popup.close());

		buttons.getChildren().addAll(create, cancel);
		buttons.setAlignment(Pos.CENTER);

		all.getChildren().add(buttons);

		Scene s = new Scene(all, 300, 100);

		popup.setScene(s);
		popup.showAndWait();
	}

	@FXML
	public void attemptClose(){
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
			"Are you sure you want to exit?");
		//This tests that the user clicked the OK button, and no other one.
		boolean exit = alert.showAndWait().filter(e -> e.equals(ButtonType.OK)).isPresent();
		if(exit){
			Platform.exit();
		}
	}
}
