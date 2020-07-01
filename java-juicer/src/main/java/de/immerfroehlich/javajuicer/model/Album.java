package de.immerfroehlich.javajuicer.model;

import java.util.ArrayList;
import java.util.List;

public class Album {
	
	public String artist;
	public String title;
	public String year;
	
	public boolean allTracksSameArtist;
	
	public List<Mp3Track> tracks = new ArrayList<>();

}
