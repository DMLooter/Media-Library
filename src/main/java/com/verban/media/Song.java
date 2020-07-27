import java.io.*;
/**
	Represents a single song held in an audio file. Must point to a valid audio file in the MP3, WAV, WMV, ACC, FLAC, or OGG formats.
*/
public class Song extends Media {
	// Runtime in seconds
	private int length;
	private File file;

	/**
		Attempts to read in information from a file and populate this object.
		If the file can be read, it will attempt to parse out data, if no tag data can be read, internal fields must be set manually.

		@param file the file that contains this song
		@throws IOException if the file is invalid in some way.
		@throws FileFormatException if the file is not a valid audio format
	*/
	public Song(File file) throws IOException{

	}
}
