package com.verban.media;

import java.util.*;

/**
* Represents an Album that contains a list of songs in order.
* @author Michael Verban (2020)
*/
public class Album extends Media {

	private static final long serialVersionUID=19834754;

	private Song[] tracks;

	/**
	* Creates a new empty album with the specified title, year, and artists, and room for the specified number of songs.
	* @param title the name of this Album
	* @param tracks the number of songs on this Album
	* @param year the year this Album was published
	* @param artists the artists that contributed to this Album
	*/
	public Album(String title, int tracks, int year, String... artists){
		this(title, new Song[tracks], year, artists);
	}

	/**
	* Creates a new album with the specified title, year, and artists, who's Songs are contained in tracks.
	* @param title the name of this Album
	* @param tracks the in-order list of songs on this Album
	* @param year the year this Album was published
	* @param artists the artists that contributed to this Album
	*/
	public Album(String title, Song[] tracks, int year, String... artists){
		this.title = title;
		this.tracks = tracks;
		this.year = year;
		this.artists = artists;
	}

	/**
	* Returns the nth track on this Album, or null if n is outside of the range of tracks
	* @param n the number of track to get, 1-INDEXED!
	* @return the nth track, or null.
	*/
	public Song getTrack(int n){
		if(n < 1 || n > tracks.length)
			return null;
		return tracks[n-1];
	}

	/**
	* Sets the nth track on this Album to the specified song.
	* Fails if n is outside the range of tracks on this album.
	* @param s the song to put into this Album
	* @param n the track number to set to s, 1-INDEXED!
	* @return true if n is a valid track number and the set succeded, false otherwise.
	*/
	public boolean setTrack(Song s, int n){
		if(n < 1 || n > tracks.length)
			return false;

		tracks[n-1] = s;
		return true;
	}

	/**
	* Returns a copy of the list of tracks in this Album.
	* @return a copy of the list of tracks in this Album
	*/
	public Song[] getAllTracks(){
		return Arrays.copyOf(tracks, tracks.length);
	}

	@Override
	public boolean equals(Object o){
		if(o instanceof Album){
			Album a = (Album)o;
			// Two equal albums will have the same name, year, and number of tracks.
			return a.title.equals(this.title) && a.year == this.year && a.tracks.length == this.tracks.length;
		}
		return false;
	}

	@Override
	public int hashCode(){
		return this.title.hashCode() ^ this.year;
	}
}
