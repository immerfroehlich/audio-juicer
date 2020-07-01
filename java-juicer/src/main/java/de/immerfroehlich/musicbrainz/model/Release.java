package de.immerfroehlich.musicbrainz.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.KebabCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonNaming(KebabCaseStrategy.class)
public class Release {
	
	public String id = "";
	
	public String title = "";
	public String date = "";
	public String barcode = "";
	
	public List<ArtistCredit> artistCredit = new ArrayList<>();
	
	public List<Medium> media = new ArrayList<>();
	
	@JsonIgnoreProperties
	public boolean multiCDRelease = false;
	
	public CoverArtArchive coverArtArchive;
	
}
