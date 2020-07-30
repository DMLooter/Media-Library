package com.verban.media;

import java.util.*;
import java.io.*;
import javafx.collections.*;
/**
* Represents a collection of media (artists, albums, playlists, songs, videos, etc.)
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
	First read/write 4 integers: #Songs, #Albums, #Artists, #Playlists
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
		ArrayList<Album> tempAlbums = new ArrayList<Album>();
		ArrayList<Artist> tempArtists = new ArrayList<Artist>();
		ArrayList<Playlist> tempPlaylists = new ArrayList<Playlist>();

		ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
		int numSongs = in.readInt();
		int numAlbums = in.readInt();
		int numArtists = in.readInt();
		int numPlaylists = in.readInt();

		try{
			for(int i =0; i < numSongs; i++){
				tempSongs.add((Song)in.readObject());
			}
			for(int i =0; i < numAlbums; i++){
				tempAlbums.add((Album)in.readObject());
			}
			for(int i =0; i < numArtists; i++){
				tempArtists.add((Artist)in.readObject());
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
		albums.addAll(tempAlbums);
		artists.clear();
		artists.addAll(tempArtists);
		playlists.clear();
		playlists.addAll(tempPlaylists);
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
		out.writeInt(albums.size());
		out.writeInt(artists.size());
		out.writeInt(playlists.size());

		for(Song s : songs){
			out.writeObject(s);
		}
		for(Album a : albums){
			out.writeObject(a);
		}
		for(Artist a : artists){
			out.writeObject(a);
		}
		for(Playlist p : playlists){
			out.writeObject(p);
		}

		out.close();
	}

	public ObservableList<Song> getSongs(){
		return songs;
	}

	public ObservableList<Album> getAlbums(){
		return albums;
	}

	public ObservableList<Artist> getArtists(){
		return artists;
	}

	public ObservableList<Playlist> getPlaylists(){
		return playlists;
	}
}
