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
			fail();// Shouldnt get to the poiont where we can throw a generic IOEx
		}
    }
}
