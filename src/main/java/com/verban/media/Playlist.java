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

	// TODO disalow duplicates
	public void addTrack(Song s){
		tracks.add(s);
	}

	/**
	* Returns a copy of the list of tracks in this Playlist.
	* @return a copy of the list of tracks in this Playlist
	*/
	public Song[] getAllTracks(){
		return tracks.toArray(new Song[0]);
	}

	@Override
	public boolean equals(Object o){
		if(o instanceof Playlist){
			Playlist p = (Playlist)o;
			// Two equal playlists will have the same name
			return p.title.equals(this.title);
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
