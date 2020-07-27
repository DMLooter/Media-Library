package com.verban.media;

import java.io.*;
import org.jaudiotagger.audio.*;
import org.jaudiotagger.audio.exceptions.*;
import org.jaudiotagger.tag.*;
/**
	Represents a single song held in an audio file. Must point to a valid audio file in the MP3, WAV, WMA, ACC, FLAC, or OGG formats.
*/
public class Song extends Media {
	// Runtime in seconds
	private int length;
	private File file;
	private String genre;
	private Album originalAlbum;

	/**
		Attempts to read in information from a file and populate this object.
		If the file can be read, it will attempt to parse out data, if no tag data can be read, internal fields must be set manually.

		@param file the file that contains this song
		@throws IOException if the file is invalid or cannot be read in some way.
		@throws FileFormatException if the file is not a valid audio format
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
		Attempts to parse tags out of the file, such as runtime, artist, song name, date, genre, and original album.
	*/
	private void parseTags() throws CannotReadException, IOException{
		try{
			AudioFile f = AudioFileIO.read(file);
			Tag tag = f.getTag();
			AudioHeader head = f.getAudioHeader();

			this.length = head.getTrackLength();
		}catch(TagException | ReadOnlyFileException | InvalidAudioFrameException e){
			// A tag exception just means invalid tags, the audio should still be fine, no need to do anything.
			// A read only file exception shouldnt be thrown unless i write here, which i do not. This library seems interesting if it can throw that on a read.
			// Also not doing anything with the audio frames here, so that seems irrelevant.
		}
	}
}
