package de.immerfroehlich.musicbrainz.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.KebabCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonNaming(KebabCaseStrategy.class)
public class Track {
	public int position;
	public String title = "";
	public List<ArtistCredit> artistCredit = new ArrayList<>();
}
