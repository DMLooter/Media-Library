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
	protected String title = "";
	// Name of Artist who created to this piece of media
	protected String artist = "";
	// Year this media was created
	protected int year = 0;
	// Five star rating of this piece of media
	private double rating = 0;

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	/**
	* Sets the name of the artist of this media.
	*/
	public void setArtist(String artist){
		this.artist = artist;
	}

	/**
	* Returns the name of the artist for this media.
	*/
	public String getArtist(){
		return artist;
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
