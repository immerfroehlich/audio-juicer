package de.immerfroehlich.javajuicer.model;

import java.util.ArrayList;
import java.util.List;

public class AlbumInfo {
	
	public String artist;
	public String title;
	public String year;
	public boolean multiCdRelease;
	
	public boolean allTracksSameArtist;
	
	public List<TrackInfo> tracks = new ArrayList<>();
	public String cdNumber;

}
