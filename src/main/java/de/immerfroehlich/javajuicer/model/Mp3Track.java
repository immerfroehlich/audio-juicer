package de.immerfroehlich.javajuicer.model;

public class Mp3Track {
	
	public String artist = "";
	public String title = "";
	public String firstReleaseYear = "";
	public String releaseYear = "";
	
	@Override
	public String toString() {
		return "Mp3Track [artist=" + artist + ", title=" + title + ", firstReleaseYear=" + firstReleaseYear
				+ ", releaseYear=" + releaseYear + "]";
	}
	
	

}
