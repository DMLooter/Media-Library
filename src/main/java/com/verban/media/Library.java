package com.verban.media;

import java.util.*;
import java.io.*;
import javafx.collections.*;
/**
* Represents a collection of media (artists, albums, playlists, songs, videos, etc.)
*
*
* @author Michael Verban (2020)
*/
public class Library {
	// These lists must be able to be updated on the UI automatically, therefore must be observable.
	private ObservableList<Artist> artists;
	private ObservableList<Album> albums;
	private ObservableList<Song> songs;
	private ObservableList<Playlist> playlists;

	/**
	* Initializes an empty library.
	*/
	public Library(){
		artists = FXCollections.<Artist>observableArrayList();
		albums = FXCollections.<Album>observableArrayList();
		songs = FXCollections.<Song>observableArrayList();
		playlists = FXCollections.<Playlist>observableArrayList();
	}

	/**
	* Creates a library and attempts to read in data from the .library file passed in.
	* If reading fails, an exception will be thrown.
	*/
	public Library(File f) throws IOException{
		load(f);
	}

	/*
	File format notes: file should be read/saved with a ObjectIn/OutputStream.
	First read/write 2 integers: #Songs, #Playlists
	then call read/writeObject the specified number of times for each type.
	*/

	/**
	* Attempts to load in library information from the specified file, OVERWRITING WHAT IS ALREADY LOADED.
	* If the file cannot be read completely, the data in this object will be unchanged.
	* @param f the ".library" file to read from
	* @throws IOException if an error occurs while reading
	* @throws FileFormatException if the file is not .library, or the data is not in the correct format.
	*/
	public void load(File f) throws IOException{
		if(!f.isFile())
			throw new FileFormatException("Not a file");
		String ext = f.getName().substring(f.getName().lastIndexOf(".")+1);
		if(!(ext.equalsIgnoreCase("library"))){
			throw new FileFormatException("Not a .library file");
		}

		ArrayList<Song> tempSongs = new ArrayList<Song>();
		ArrayList<Playlist> tempPlaylists = new ArrayList<Playlist>();

		ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
		int numSongs = in.readInt();
		int numPlaylists = in.readInt();

		try{
			for(int i =0; i < numSongs; i++){
				Song s = (Song)in.readObject();
				if(!tempSongs.contains(s)){
					// ensure no duplicate songs.
					tempSongs.add(s);
				}
			}
			for(int i =0; i < numPlaylists; i++){
				tempPlaylists.add((Playlist)in.readObject());
			}
		}catch(ClassNotFoundException e){
			throw new FileFormatException("Object data in libary file: " +f.toString() + " is not valid");
		}

		in.close();

		songs.clear();
		songs.addAll(tempSongs);
		albums.clear();
		artists.clear();
		playlists.clear();
		playlists.addAll(tempPlaylists);
		validate();
	}

	/**
	* Attempts to save the data in this library to the specified .library file.
	* @param f the ".library" file to read from
	* @throws IOException if an error occurs while writing
	* @throws FileFormatException if the file is not .library
	*/
	public void save(File f) throws IOException {
		String ext = f.getName().substring(f.getName().lastIndexOf(".")+1);
		if(!(ext.equalsIgnoreCase("library"))){
			throw new FileFormatException("Not a .library file");
		}

		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
		out.writeInt(songs.size());
		out.writeInt(playlists.size());

		for(Song s : songs){
			out.writeObject(s);
		}
		for(Playlist p : playlists){
			out.writeObject(p);
		}

		out.close();
	}

	/**
	* This method tells the library to go through and ensure there is only one instance of every song, album, and artist.
	* This ensures that changes made to one object will reflect accross all places that object is used.
	* This should only be needed immediately after loading a library, to coalesce all the deserialized instances into one.
	*
	* Note that this method acheives this by clearing the Album and Artist lists, and rebuilding them from the ground up using just data in the Song objects.
	*/
	public void validate(){
		albums.clear();
		artists.clear();

		for(Song song : songs){
			// We are always part of an album, regardless of how much info we have on it
			Album album = getAlbum(song.getOriginalAlbum(), song.getArtistName());
			album.addTrack(song);
		}
		// We dont need to do anything more, becuase in the creation of the needed albums, they were automatically added to the artists.
	}

	public ObservableList<Song> getSongs(){
		return songs;
	}

	/**
	* Returns the song instance representing the specified File, if it exists in the library,
	* If it does not exist, returns null
	*/
	public Song getSong(File songFile){
		for(Song song : songs){
			if(song.getFile().equals(songFile)){
				return song;
			}
		}
		return null;
	}

	public ObservableList<Album> getAlbums(){
		return albums;
	}

	/**
	* Returns the album instance with the specified title, year, and artist, if it exists.
	* If it does not, a new one is created and added to the album list, then returned.
	* A newly created album is also automatically added to the album list of its artist.
	*/
	public Album getAlbum(String title, String artistName){
		Album test = new Album(title, artistName);
		int i = -1;
		if((i = albums.indexOf(test)) > -1){
			return albums.get(i);
		}else{
			albums.add(test);
			Artist artist = getArtist(artistName);
			artist.addAlbum(test);
			return test;
		}
	}

	public ObservableList<Artist> getArtists(){
		return artists;
	}

	/**
	* Returns the artist instance with the specified name, if it exists.
	* If it does not, a new one is created and added to the artist list, then returned.
	*/
	public Artist getArtist(String name){
		Artist test = new Artist(name);
		int i = -1;
		if((i = artists.indexOf(test)) > -1){
			return artists.get(i);
		}else{
			artists.add(test);
			return test;
		}
	}

	public ObservableList<Playlist> getPlaylists(){
		return playlists;
	}

	/**
	* Returns the playlist instance with the specified title, if it exists.
	* If it does not, a new one is created and added to the playlist list, then returned.
	*/
	public Playlist getPlaylist(String title){
		Playlist test = new Playlist(title);
		int i = -1;
		if((i = playlists.indexOf(test)) > -1){
			return playlists.get(i);
		}else{
			playlists.add(test);
			return test;
		}
	}

	/** TODO is this needed?
	* Tests whether a playlist with the given name exists already in this library.
	* @return true if there is a playlist with the given name, false otherwise
	*/
	public boolean playlistExists(String name){
		for(Playlist p : playlists){
			if(p.getTitle().equals(name)){
				return true;
			}
		}
		return false;
	}

	/**
	* Attempts to add the specified song to the library, along with the Artist(s) who made it, and
	* the album it is a part of, if those are specified.
	* @param song the song to add.
	*/
	public void addSong(Song song){
		if(!songs.contains(song)){ // If the file already exists in this library, dont add it
			songs.add(song);

			//Always add to the album, regardless of how blank it is
			Album album = getAlbum(song.getOriginalAlbum(), song.getArtistName());
			album.addTrack(song);
		}
	}

	/**
	* Updates a song to have the new specified tag data, also commits those files to the underlying file.
	* @param song the original Song
	* @param title the new song title
	* @param originalAlbum the new song Album
	* @param artistName the new song Artist
	* @param year the new song year
	* @param genre the new song genre
	* @param albumTrackNumber the new number of the track on its album
	* @param albumTracks the new number of tracks on the album
	*
	* @return true if the data was updated and the tag write succeded, false otherwise.
	*/
	public boolean updateSong(Song song, String title, String originalAlbum, String artistName, int year, String genre, int albumTrackNumber, int albumTracks){
		//TODO maybe ensure the file is the master object in the library using getSong(song.getFile())?

		song.setTitle(title);
		song.setGenre(genre);
		song.setAlbumTrackNumber(albumTrackNumber);
		song.setAlbumTracks(albumTracks);

		//If album changed in any way, remove it from the old one and add it to the new one
		if(!song.getOriginalAlbum().equals(originalAlbum) || song.getYear() != year || !song.getArtistName().equals(artistName)) {
			//TODO deal with empty albums/artists, though a save and reload of the library will remove them
			getAlbum(song.getOriginalAlbum(), song.getArtistName()).removeTrack(song);
			song.setOriginalAlbum(originalAlbum);
			song.setYear(year);
			song.setArtistName(artistName);
			getAlbum(originalAlbum, artistName).addTrack(song);
		}

		return song.writeTags();
	}

	/**
	* Creates a new playlist with the given name.
	*/
	public void createPlaylist(String name){
		Playlist p = new Playlist(name);
		playlists.add(p);
	}

	public void addSongToPlaylist(Song song, String playlistTitle){
		Playlist playlist = getPlaylist(playlistTitle);
		playlist.addTrack(song);
	}

	public void removeSongFromPlaylist(Song song, String playlistTitle){
		Playlist playlist = getPlaylist(playlistTitle);
		playlist.removeTrack(song);
	}
}
