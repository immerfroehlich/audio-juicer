package de.immerfroehlich.musicbrainz.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.KebabCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonNaming(KebabCaseStrategy.class)
public class Recording {
	public String firstReleaseDate;
}
