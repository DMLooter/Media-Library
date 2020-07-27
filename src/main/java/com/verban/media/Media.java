import java.util.*;

/**
	This class represents any single peice of media, Album, Song, Video, etc.
	The media must have one or more creators, and a name.
*/
public abstract class Media{
	private String name;
	private Artist[] creators;
	private Date creation;

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setCreators(Artist... a){
		creators = a;
	}
}
