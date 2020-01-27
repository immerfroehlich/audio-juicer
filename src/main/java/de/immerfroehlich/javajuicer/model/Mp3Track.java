package de.immerfroehlich.javajuicer.model;

public class Mp3Track {
	
	public String artist = "";
	public String title = "";
	public String firstReleaseYear = "";
	public String releaseYear = "";
	public boolean isPregap = false;
	
	@Override
	public String toString() {
		return artist + " - " + title + " - " + "(" + firstReleaseYear + ")";
	}

}
