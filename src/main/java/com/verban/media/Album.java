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
	* Creates a new empty album with the specified title, year, and artists, and room for the specified number of songs.
	* Fils the tracks list with the PLACEHOLDER song.
	* @param title the name of this Album
	* @param tracks the expected number of songs on this Album
	* @param year the year this Album was published
	* @param artists the artists that contributed to this Album
	*/
	public Album(String title, int tracks, int year, String artist){
		this.title = title;
		this.tracks = new ArrayList<Song>();
		for(int i =0; i < tracks; i ++)
			this.tracks.add(Song.PLACEHOLDER);
		this.year = year;
		this.artist = artist;
	}

	/**
	* Creates a new album with the specified title, year, and artists, who's Songs are contained in tracks.
	* @param title the name of this Album
	* @param tracks the in-order list of songs on this Album
	* @param year the year this Album was published
	* @param artists the artists that contributed to this Album
	*/
	public Album(String title, Song[] tracks, int year, String artist){
		this.title = title;
		this.tracks = new ArrayList<Song>();
		this.tracks.addAll(tracks);
		this.year = year;
		this.artist = artist;
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

	/**
	* Sets the nth track on this Album to the specified song.
	* If n is 0, or > tracks.size(), the song will be added to the end.
	* @param s the song to put into this Album
	* @param n the track number to set to s, 1-INDEXED!
	* @return true if n is a valid track number and the set succeded, false if the song was added to the end.
	*/
	public boolean setTrack(Song s, int n){
		if(n < 1 || n > tracks.size()){
			tracks.add(s);
			return false;
		}

		tracks.set(n-1, s);
		return true;
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
			Album a = (Album)o;
			// Two equal albums will have the same title, year, and artist.
			return a.title.equals(this.title) && a.year == this.year;
		}
		return false;
	}

	@Override
	public int hashCode(){
		return this.title.hashCode() ^ this.year;
	}

	@Override
	public String toString(){
		return title;
	}
}
