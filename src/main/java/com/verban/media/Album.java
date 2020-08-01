package com.verban.media;

import java.util.*;

/**
* Represents an Album that contains a list of songs in order.
* @author Michael Verban (2020)
*/
public class Album extends Media {

	private static final long serialVersionUID=19834754;

	private List<Song> tracks;

	/**
	* Creates a new empty album with the specified title and artist.
	* @param title the name of this Album
	* @param artists the artists that contributed to this Album
	*/
	public Album(String title, String artistName){
		this(title, artistName, new Song[0]);
	}

	/**
	* Creates a new album with the specified title and artist, who's Songs are contained in tracks.
	* @param title the name of this Album
	* @param artists the artists that contributed to this Album
	* @param tracks the in-order list of songs on this Album
	*/
	public Album(String title, String artistName, Song... tracks){
		this.title = title;
		this.tracks = new ArrayList<Song>();
		this.tracks.addAll(Arrays.asList(tracks));
		this.year = year;
		this.artistName = artistName;
	}

	/**
	* Returns the nth track on this Album, or null if n is outside of the range of tracks
	* @param n the number of track to get, 1-INDEXED!
	* @return the nth track, or null.
	*/
	public Song getTrack(int n){
		if(n < 1 || n > tracks.size())
			return null;
		return tracks.get(n-1);
	}

	public void addTrack(Song s){
		tracks.add(s);
	}

	/**
	* Attempts to remove the specified song from this album
	* @return true if the song was in the album and was removed, false otherwise.
	*/
	public boolean removeTrack(Song s){
		return tracks.remove(s);
	}

	/**
	* Returns a copy of the list of tracks in this Album.
	* @return a copy of the list of tracks in this Album
	*/
	public Song[] getAllTracks(){
		return tracks.toArray(new Song[0]);
	}

	/**
	* Returns the number of tracks on this album.
	*/
	public int numTracks(){
		return tracks.size();
	}

	@Override
	public boolean equals(Object o){
		if(o instanceof Album){
			Album a = (Album)o; //TODO remove reliance on year equality.
			// Two equal albums will have the same title and artist.
			return a.title.equals(this.title) && a.artistName.equals(this.artistName);
		}
		return false;
	}

	@Override
	public int hashCode(){
		return this.title.hashCode() ^ this.artistName.hashCode();
	}

	@Override
	public String toString(){
		return title;
	}
}
