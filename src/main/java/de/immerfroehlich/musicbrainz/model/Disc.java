package de.immerfroehlich.musicbrainz.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonPropertyOrder({
//"offsets",
//"releases",
//"offset-count",
//"sectors",
//"id"
//})
@JsonIgnoreProperties(ignoreUnknown=true)
public class Disc {
	
	public List<Release> releases = new ArrayList<>(); 

}
