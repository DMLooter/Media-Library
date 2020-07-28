package com.verban.media;

import java.util.*;

/**
* This class represents any single peice of media, Album, Song, Video, etc.
* The media must have one or more creators, and a name.
*
* @author Michael Verban (2020)
*/
public abstract class Media{
	// Title of the piece of media
	protected String title;
	// List of Artists who contributed to this piece of media
	protected String[] artists;
	// Year this media was created
	protected int year;
	// Five star rating of this piece of media
	private double rating;

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public void setArtists(String... a){
		artists = a;
	}

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
