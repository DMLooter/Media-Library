package com.verban.media;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import java.io.*;
import com.verban.media.*;

/**
 * Unit test for simple App.
 */
public class TagTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void test001_Song_Exceptions() {
		Song s;
		try{
			s = new Song(new File(""));
			fail(); // Should except
		}catch(FileFormatException e){
			assertEquals(e.getMessage(), "Not a file");
		}catch(IOException e){
			fail();// Shouldnt get to the point where we can throw a generic IOEx
		}
		try{
			s = new Song(new File("test.txt"));
			fail(); // Should except
		}catch(FileFormatException e){ // Expected
		}catch(IOException e){
			e.printStackTrace();
			fail("Incorrect Exception thrown");// Shouldnt get to the poiont where we can throw a generic IOEx
		}
    }

	@Test
	public void test002_Tag_Read_mp3(){
		try{
			Song s = new Song(new File("src/test/java/com/verban/media/test.mp3"));

			assertEquals("Test MP3", s.getTitle());
			assertEquals(30, s.getRuntime());
			assertEquals("Michael", s.getArtist());
			assertEquals("Test Files", s.getOriginalAlbum());
			assertEquals(2020, s.getYear());
			assertEquals("Silence", s.getGenre());
		}catch(IOException e){
			e.printStackTrace();
			fail("Exception thrown when not expected in Test002");
		}
	}
}
