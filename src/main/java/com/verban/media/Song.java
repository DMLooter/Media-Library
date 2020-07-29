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
	// Runtime in seconds
	private int length;
	// File where this song is
	private File file;
	// Genre of the song
	private String genre;
	// The name of the  original Album which this recording of the song came from
	private String originalAlbum;
	//TODO maybe add track number on that album

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
			s.equalsIgnoreCase("acc") || s.equalsIgnoreCase("flac") || s.equalsIgnoreCase("ogg"))){
			throw new FileFormatException(
				"Not an accepted File type, valid song filetypes are mp3,wav,wma,acc,flac,ogg");
		}
		this.file = file;
		try{
			parseTags();
		}catch(CannotReadException e){
			throw new IOException("File cannot be read at all.");
		}
	}

	/**
	* Attempts to parse tags out of the file, such as runtime, artist, song name, date, genre, and original album.
	*/
	private void parseTags() throws CannotReadException, IOException{
		try{
			AudioFile f = AudioFileIO.read(file);
			Tag tag = f.getTag();
			AudioHeader head = f.getAudioHeader();

			this.length = head.getTrackLength();

			this.title = tag.getFirst(FieldKey.TITLE);
			// Artists could be multiple, so we have to get the list of TagFields and map them to strings
			// They are in a wierd format after toString, so some processing is required.
			this.artists = tag.getFields(FieldKey.ARTIST).stream().map(e->e.toString())
				.map(e->e.substring(e.indexOf('"')+1, e.lastIndexOf('"')).trim())
				.collect(Collectors.toList()).toArray(new String[1]);
			try{
				this.year = Integer.parseInt(tag.getFirst(FieldKey.YEAR));
			}catch(NumberFormatException e){
				// If we fail, just leave it blank
			}
			this.genre = tag.getFirst(FieldKey.GENRE);
			this.originalAlbum = tag.getFirst(FieldKey.ALBUM);


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

			tag.deleteField(FieldKey.ARTIST);
			for(String a : this.artists){
				tag.addField(FieldKey.ARTIST, a);
			}

			tag.setField(FieldKey.YEAR, ""+this.year);

			tag.setField(FieldKey.ALBUM, this.originalAlbum);
			tag.setField(FieldKey.GENRE, this.genre);

		}catch(IOException | CannotReadException | TagException | ReadOnlyFileException | InvalidAudioFrameException e){
			return false;
		}
		return true;
	}

	public int getTrackLength(){
		return length;
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
}
