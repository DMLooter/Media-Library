package com.verban.media;

import java.io.*;
/**
* Exception thrown when a file passed is not of a valid or expected format.
* @author Michael Verban (2020)
*/
public class FileFormatException extends IOException{
	public FileFormatException(String message){
		super(message);
	}
}
