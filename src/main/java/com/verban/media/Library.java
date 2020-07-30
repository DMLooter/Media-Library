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

	/**
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
	* @param s the song to add.
	*/
	public void addSong(Song s){
		// These are flags so wwe can add the album to the artist's list if need be
		Album orig = null;
		boolean albumValid = false;//TODO could remove flag and just check if orig is null.

		if(!songs.contains(s)){ // If the file already exists in this library, dont add it
			songs.add(s);
			if(s.getOriginalAlbum() != null && !s.getOriginalAlbum().equals("")){
				// see if the album exists, if not, create it.
				boolean exists = false;
				for(Album a: albums){
					if(a.getTitle().equals(s.getOriginalAlbum())){
						if(a.numTracks() == s.getAlbumTracks() || a.getYear() == s.getYear() || a.getArtist() == s.getArtist()){
							exists = true;
							orig = a;
							albumValid = true;
							// If any of those three are equal, it is highly likely it is the correct album.
							a.setTrack(s, s.getAlbumTrackNumber()); // This will fail if out of range or not set.
							break; // no need to keep searching through albums.
						}
					}
				}
				if(!exists){ // If it doesnt exist, we can try to make a new one
					// But only if we have the minimum right information.
					if(s.getAlbumTrackNumber() != 0 && s.getAlbumTracks() != 0){
						orig = new Album(s.getOriginalAlbum(), s.getAlbumTracks(), s.getYear(), s.getArtist());
						albumValid = true;
						orig.setTrack(s, s.getAlbumTrackNumber());
						albums.add(orig);
					}
				}
			}

			if(s.getArtist() != null && !s.getArtist().equals("")){
				Artist test = new Artist(s.getArtist());
				int i = -1;
				if((i = artists.indexOf(test)) > -1){
					// the artist exists
					Artist real = artists.get(i);
					if(albumValid){
						// if the album was set, we need to check if the artist already has it
						// If they do, we do nothing, the reference should have been updated already.
						if(!real.hasAlbum(orig)){
							// Otherwise we add it.
							real.addAlbum(orig);
						}
					}else{
						// Otherwise we add the song standalone
						real.addSong(s);
					}
				}else{
					//create the artist
					Artist real = new Artist(s.getArtist());
					if(albumValid){
						real.addAlbum(orig);
					}else{
						real.addSong(s);
					}
					artists.add(real);
				}
			}
		}
	}

	/**
	* Creates a new playlist with the given name.
	*/
	public void createPlaylist(String name){
		Playlist p = new Playlist(name);
		playlists.add(p);
	}
}
