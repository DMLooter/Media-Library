package com.verban.media;

import java.util.*;
import java.io.*;
import javafx.collections.*;
/**
* Represents a collection of media (artists, albums, playlists, songs, videos, etc.)
*
* TODO: add a way of getting Song/Album/Artist/Playlist by just defining features, allowing the method to create them as needed.
* TODO: add a validate/link method so that when the Library is deserialized, we can ensure that there is one instance of each song/album/artist/playlist that is coppied to all locations.
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
			if(s.getOriginalAlbum() != null && !s.getOriginalAlbum().isBlank() && s.getAlbumTrackNumber() > 0){
				// see if the album exists, if not, create it.
				boolean exists = false;
				for(Album a: albums){
					if(a.getTitle().equals(s.getOriginalAlbum()) &&
						(s.getYear() == 0 || a.getYear() == 0 || a.getYear() == s.getYear()) &&
						(s.getArtist() == null || a.getArtist().isBlank() || s.getArtist().isBlank()
						 || a.getArtist() == s.getArtist())){
							// All three fields should match, but only if we actually have all three fields.
							// If a field is missing in the song, it is okay for it to not match the album info.
							exists = true;
							orig = a;
							albumValid = true;

							a.setTrack(s, s.getAlbumTrackNumber());
							// If we can, we might as well update the album if the song has more info
							if(a.getArtist().isBlank() && !s.getArtist().isBlank()){
								a.setArtist(s.getArtist());
							}
							if(a.getYear() ==0 && s.getYear() != 0){
								a.setYear(s.getYear());
							}

							// And vice versa
							if(s.getArtist().isBlank() && !a.getArtist().isBlank()){
								s.setArtist(a.getArtist());
							}
							if(s.getYear() ==0 && a.getYear() != 0){
								s.setYear(a.getYear());
							}


							break; // no need to keep searching through albums.
					}
				}
				if(!exists){ // If it doesnt exist, we can try to make a new one
					// But only if we have the minimum right information.
					if(s.getAlbumTrackNumber() > 0 && s.getAlbumTracks() > 0 && s.getAlbumTrackNumber() < s.getAlbumTracks()){
						orig = new Album(s.getOriginalAlbum(), s.getAlbumTracks(), s.getYear(), s.getArtist());
						albumValid = true;
						orig.setTrack(s, s.getAlbumTrackNumber());
						albums.add(orig);
					}
				}
			}

			if(s.getArtist() != null && !s.getArtist().isBlank()){
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
	* Updates all references to old to have the data contained in new. Additionally adjusts the album
	* that contained old (if any) to reflect the new information shown in new regarding number of tracks.
	* @param oldSong the song to be replaced
	* @param newSong the song to replace it with.
	*/
	public void updateSong(Song oldSong, Song newSong){
		// Replace in songs list
		songs.remove(oldSong);
		songs.add(newSong);

		Album newAlbum = null;

		//If album changed, remove it from the old one and add it to the new one
		if(!oldSong.getOriginalAlbum().equals(newSong.getOriginalAlbum())){
			for(Album oldAlbum : albums){
				if(oldAlbum.getTitle().equals(oldSong.getOriginalAlbum()) && oldAlbum.getArtist().equals(oldSong.getArtist())){
					oldAlbum.setTrack(Song.PLACEHOLDER, oldSong.getAlbumTrackNumber());
				}
			}

			// adding will only happen if the new album has a name.
			if(!newSong.getOriginalAlbum().isBlank()){
				boolean exists = false;
				for(Album existAlbum : albums){
					if(existAlbum.getTitle().equals(newSong.getOriginalAlbum()) && existAlbum.getArtist().equals(newSong.getArtist())){
						exists = true;
						existAlbum.setTrack(newSong, newSong.getAlbumTrackNumber());
					}
				}
				// here we must make a new album for the new song
				if(!exists){
					newAlbum = new Album(newSong.getOriginalAlbum(), newSong.getAlbumTracks(), newSong.getYear(), newSong.getArtist());
					newAlbum.setTrack(newSong, newSong.getAlbumTrackNumber());
					albums.add(newAlbum);
				}
			}
		}

		// If artist changed, and either old or new had no album, we need to update the artist's song list
		if(!oldSong.getArtist().equals(newSong.getArtist())){
			if(oldSong.getOriginalAlbum().isBlank()){
				for(Artist oldArtist : artists){
					if(oldArtist.getName().equals(oldSong.getArtist())){
						oldArtist.removeSong(oldSong);
					}
				}
			}

			// Regardless of if the new one has an album, we need to make sure the artist exists.
			Artist test = new Artist(newSong.getArtist());
			int i = -1;
			if((i = artists.indexOf(test)) > -1){
				// the artist exists
				Artist real = artists.get(i);
				if(oldSong.getOriginalAlbum().isBlank()){
					// If no album, just add the song
					real.addSong(newSong);
				}else{
					// if yes album, we need to check if it is new
					// If yes, add it.
					if(newAlbum != null){
						real.addAlbum(newAlbum);
					}
				}
			}else{
				Artist real = new Artist(newSong.getArtist());
				if(oldSong.getOriginalAlbum().isBlank()){
					// If no album, just add the song
					real.addSong(newSong);
				}else{
					// if yes album, add it.
					real.addAlbum(newAlbum);
				}
				artists.add(real);
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
