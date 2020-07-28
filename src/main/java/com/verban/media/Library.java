package com.verban.media;

import java.util.*;
/**
* Represents a collection of media (artists, albums, playlists, songs, videos, etc.)
* @author Michael Verban (2020)
*/
public class Library {
	// Artists should be unique, so we can make a set of them
	private Set<String> artists;
	private List<Album> albums;
	private List<Song> songs;
}
