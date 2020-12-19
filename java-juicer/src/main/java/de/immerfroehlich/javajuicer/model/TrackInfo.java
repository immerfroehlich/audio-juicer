package de.immerfroehlich.javajuicer.model;

public class TrackInfo {
	
	public String artist = "";
	public String title = "";
	public String firstReleaseYear = "";
	public String releaseYear = "";
	public boolean isPregap = false;
	public Cover cover = new Cover();
	public String trackNumber = "02"; //TODO fill this with the right trackNumber
	
	@Override
	public String toString() {
		return artist + " - " + title + " - " + "(" + firstReleaseYear + ")";
	}

}
