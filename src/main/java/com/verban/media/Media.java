package com.verban.media;

import java.util.*;
import java.io.Serializable;

/**
* This class represents any single peice of media, Album, Song, Video, etc.
* The media must have one or more creators, and a name.
*
* @author Michael Verban (2020)
*/
public abstract class Media implements Serializable{

	private static final long serialVersionUID=42412398L;


	// Title of the piece of media
	protected String title;
	// List of Artists who contributed to this piece of media
	protected String[] artists;
	// Year this media was created
	protected int year;
	// Five star rating of this piece of media
	private double rating = 0;

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	/**
	* Clears the current artists of this media and replaces them with those passed in.
	* @param artists the new list of artists for this media
	*/
	public void setArtists(String... artists){
		this.artists = artists;
	}

	/**
	* Adds a single artist to this piece of media
	*/
	public void addArtist(String artist){
		this.artists = Arrays.copyOf(this.artists, this.artists.length + 1);
		this.artists[this.artists.length-1] = artist;
	}

	/**
	* Returns a copy of the list of artists for this media.
	*/
	public String[] getArtists(){
		return Arrays.copyOf(artists, artists.length);
	}

	public void setYear(int year){
		this.year = year;
	}

	public int getYear(){
		return year;
	}

	/**
	* Sets the rating of this file out of five stars.
	* @param rating the rating out of five stars
	*/
	public void setRating(double rating){
		this.rating = rating;
	}

	/**
	* Gets the rating of this file out of five stars.
	* @return the rating out of five stars
	*/
	public double getRating(){
		return rating;
	}
}
