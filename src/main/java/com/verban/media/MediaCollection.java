package com.verban.media;

/**
This class defines any ordered collection of media, such as a playlist, album, or artist. This collection must support adding, removing, and reordering.
*/
public interface MediaCollection<T extends Media>{
	public void add(T media);

	public void remove(T media);

	public T[] getAllMedia();

	public int getSize();

	public T getMediaAt(int pos);
}
