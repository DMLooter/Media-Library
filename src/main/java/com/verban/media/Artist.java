package com.verban.media;

import java.util.*;

/**
* This class represents an Artist or Creator of media with a name.
* @author Michael Verban (2020)
*/
public class Artist{
	private String name;
	// For any album this artist was a part of
	private List<Album> albums;
	// This array is only for songs not on a particular album
	private List<Song> songs;

	public Artist(String name){
		this.name = name;
		albums = new ArrayList<Album>();
		songs = new ArrayList<Song>();
	}

	public String getName(){
		return name;
	}

	public Album[] getAllAlbums(){
		return albums.toArray(new Album[0]);
	}

	public void addAlbum(Album a){
		albums.add(a);
	}

	public boolean removeAlbum(Album a){
		return albums.remove(a);
	}

	/**
	* Attempts to retrieve the album with the specified name if it exists in this authors list
	* @param name the name of the album to attempt to retrieve
	* @return the album object with the given name, if it exits, otherwise null
	*/
	public Album getAlbum(String name){
		for(Album a : albums){
			if(a.getTitle().equals(name)){
				return a;
			}
		}
		return null;
	}

	public Song[] getAllSongs(){
		return songs.toArray(new Song[0]);
	}

	public void addSong(Song s){
		songs.add(s);
	}

	public boolean removeSong(Song s){
		return songs.remove(s);
	}

	/**
	* Attempts to retrieve the song with the specified name if it exists in this authors list
	* @param name the name of the song to attempt to retrieve
	* @return the song object with the given name, if it exits, otherwise null
	*/
	public Song getSong(String name){
		for(Song s : songs){
			if(s.getTitle().equals(name)){
				return s;
			}
		}
		return null;
	}
}
