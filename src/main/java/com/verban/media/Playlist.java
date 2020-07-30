package com.verban.media;

import java.util.*;
import java.io.Serializable;

/**
* Represents a playlist that contains a list of songs in order.
* @author Michael Verban (2020)
*/
public class Playlist implements Serializable{

	private static final long serialVersionUID=-278146593L;

	private String title;
	private List<Song> tracks;

	/**
	* Creates a new empty playlist with the specified name
	* @param title the name of this Playlist
	*/
	public Playlist(String title){
		this.title = title;
		tracks = new ArrayList<Song>();
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	/**
	* Returns the nth track in this Playlist, or null if n is outside of the range of tracks
	* @param n the number of track to get, 1-INDEXED!
	* @return the nth track, or null.
	*/
	public Song getTrack(int n){
		if(n < 1 || n > tracks.size())
			return null;
		return tracks.get(n-1);
	}

	/**
	* Sets the nth track on this Playlist to the specified song.
	* Fails if n is outside the range of tracks on this album.
	* @param s the song to put into this Playlist
	* @param n the track number to set to s, 1-INDEXED!
	* @return true if n is a valid track number and the set succeded, false otherwise.
	*/
	public boolean setTrack(Song s, int n){
		if(n < 1 || n > tracks.size())
			return false;

		tracks.set(n-1,s);
		return true;
	}

	/**
	* Returns a copy of the list of tracks in this Playlist.
	* @return a copy of the list of tracks in this Playlist
	*/
	public List<Song> getAllTracks(){
		List<Song> copy = new ArrayList<Song>();
		Collections.copy(copy, tracks);
		return copy;
	}

	@Override
	public boolean equals(Object o){
		if(o instanceof Playlist){
			Playlist p = (Playlist)o;
			// Two equal playlists will have the same name and tracks.
			return p.title.equals(this.title) && p.tracks.equals(this.tracks);
		}
		return false;
	}

	@Override
	public int hashCode(){
		return this.title.hashCode() ^ this.tracks.size();
	}

	@Override
	public String toString(){
		return title;
	}
}
