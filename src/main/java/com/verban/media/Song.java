package com.verban.media;

import java.io.*;
import java.util.*;
import java.util.stream.*;
import org.jaudiotagger.audio.*;
import org.jaudiotagger.audio.exceptions.*;
import org.jaudiotagger.tag.*;
/**
* Represents a single song held in an audio file. Must point to a valid audio file in the MP3, WAV, WMA, ACC, FLAC, or OGG formats.
*
* @author Michael Verban (2020)
*/
public class Song extends Media {

	/**
	* Placeholder song to use in albums where some songs are missing.
	*/
	public static final Song PLACEHOLDER;
	// List containing just the placeholder, for use with removeAll operations in Lists.
	public static final List<Song> PLACEHOLDER_LIST;

	static{
		PLACEHOLDER = new Song();
		PLACEHOLDER.title = "";
		PLACEHOLDER.file = new File("");
		PLACEHOLDER_LIST = new LinkedList<Song>();
		PLACEHOLDER_LIST.add(PLACEHOLDER);
	}

	private static final long serialVersionUID=234768901L;

	// Runtime in seconds
	private int runtime;
	// File where this song is
	private File file;
	// Genre of the song
	private String genre;
	// The name of the  original Album which this recording of the song came from
	private String originalAlbum;
	//The track number, and number of tracks on the album this song comes from
	private int albumTrackNumber, albumTracks;

	private Song(){}

	/**
	* Attempts to read in information from a file and populate this object.
	* If the file can be read, it will attempt to parse out data, if no tag data can be read, internal fields must be set manually.
	*
	* @param file the file that contains this song
	* @throws IOException if the file is invalid or cannot be read in some way.
	* @throws FileFormatException if the file is not a valid audio format
	*/
	public Song(File file) throws IOException{
		if(!file.isFile())
			throw new FileFormatException("Not a file");
		String s = file.getName().substring(file.getName().lastIndexOf(".")+1);
		if(!(s.equalsIgnoreCase("mp3") || s.equalsIgnoreCase("wav") || s.equalsIgnoreCase("wma") ||
			s.equalsIgnoreCase("acc") || s.equalsIgnoreCase("flac") || s.equalsIgnoreCase("ogg") ||
			s.equalsIgnoreCase("m4a"))){
			throw new FileFormatException(
				"Not an accepted File type, valid song filetypes are mp3,wav,wma,acc,flac,ogg,m4a");
		}
		this.file = file;
		// Default the title to the name of the file
		this.title = file.getName();
		try{
			parseTags();
		}catch(NoClassDefFoundError | CannotReadException e){
			e.printStackTrace();
			throw new IOException("File cannot be read at all.");
		}
	}

	/**
	* Attempts to parse tags out of the file, such as runtime, artist, song name, date, genre, and original album.
	*/
	private void parseTags() throws CannotReadException, IOException{
		try{
					System.out.println(1);
			AudioFile f = AudioFileIO.read(file);
					System.out.println("file read");
			Tag tag = f.getTag();
			AudioHeader head = f.getAudioHeader();
					System.out.println("tag read");

			this.runtime = head.getTrackLength();

			this.title = tag.getFirst(FieldKey.TITLE);

			this.artist = tag.getFirst(FieldKey.ARTIST);
			try{
				this.year = Integer.parseInt(tag.getFirst(FieldKey.YEAR));
			}catch(NumberFormatException e){
				this.year = 0;
			}
			this.genre = tag.getFirst(FieldKey.GENRE);
			this.originalAlbum = tag.getFirst(FieldKey.ALBUM);

			try{
				this.albumTrackNumber = Integer.parseInt(tag.getFirst(FieldKey.TRACK));
			}catch(NumberFormatException e){
				this.albumTrackNumber = 0;
			}
			try{
				this.albumTracks = Integer.parseInt(tag.getFirst(FieldKey.TRACK_TOTAL));
			}catch(NumberFormatException | UnsupportedOperationException e ){
				this.albumTracks = 0;
			}
					System.out.println("fully loaded");


		}catch(ArrayStoreException | TagException | ReadOnlyFileException | InvalidAudioFrameException e){
			e.printStackTrace();
			// A tag exception just means invalid tags, the audio should still be fine, no need to do anything.
			// A read only file exception shouldnt be thrown unless i write here, which i do not.
			//This library seems interesting if it can throw that on a read.
			// Also not doing anything with the audio frames here, so that seems irrelevant.
		}
	}

	/**
	* Takes the metadata that this file has been given and writes it back to the file.
	* @return true if write fully succeded, false otherwise. Note that even on a false return, some data may be modified.
	*/
	public boolean writeMetaData(){
		try{
			AudioFile f = AudioFileIO.read(file);
			Tag tag = f.getTag();
			tag.setField(FieldKey.TITLE, this.title);
			tag.setField(FieldKey.ARTIST, artist);

			tag.setField(FieldKey.YEAR, ""+this.year);

			tag.setField(FieldKey.ALBUM, this.originalAlbum);
			tag.setField(FieldKey.GENRE, this.genre);

			tag.setField(FieldKey.TRACK,this.albumTrackNumber+"");
			tag.setField(FieldKey.TRACK_TOTAL,this.albumTracks+"");

		}catch(IOException | CannotReadException | TagException | ReadOnlyFileException | InvalidAudioFrameException e){
			return false;
		}
		return true;
	}

	public int getRuntime(){
		return runtime;
	}

	public String getGenre(){
		return genre;
	}

	public void setGenre(String genre){
		this.genre = genre;
	}

	public String getOriginalAlbum(){
		return originalAlbum;
	}

	public void setOriginalAlbum(String a){
		this.originalAlbum = a;
	}

	/**
	* Returns the track number of this song on the album it is from.
	*/
	public int getAlbumTrackNumber(){
		return albumTrackNumber;
	}

	public void setAlbumTrackNumber(int trackNum){
		this.albumTrackNumber = trackNum;
	}

	/**
	* Returns the number of tracks on the album this song is from.
	*/
	public int getAlbumTracks(){
		return albumTracks;
	}

	public void setAlbumTracks(int albumTracks){
		this.albumTracks = albumTracks;
	}

	@Override
	public boolean equals(Object o){
		if(o instanceof Song){
			// If the file is the same, the two Songs must be the same, regardless of what their fields have been changed to
			return ((Song) o).file.equals(this.file);
		}
		return false;
	}

	@Override
	public int hashCode(){
		// Because file is the only factor in equals, it should be the only factor here.
		return this.file.hashCode();
	}

	@Override
	public String toString(){
		return title;
	}
}
