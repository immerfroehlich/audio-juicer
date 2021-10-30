package de.immerfroehlich.musicbrainz.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ArtistCredit {
	
	public String name = "";
	public String joinphrase = "";
}
