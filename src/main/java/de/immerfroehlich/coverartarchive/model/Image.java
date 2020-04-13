package de.immerfroehlich.coverartarchive.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.KebabCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonNaming(KebabCaseStrategy.class)
public class Image {
	
	public List<String> types = new ArrayList<>();
	public boolean front = false;
	public boolean back = false;
	public String comment = "";
	public String image = "";
	public Map<String, String> thumbnails = new HashMap<String, String>();
	public boolean approved = false;
	public int edit = 0;
	public String id = "";
	
//	"types" : [ "Other" ],
//    "front" : false,
//    "back" : false,
//    "comment" : "autographed by ModBot",
//    "image" : "http://coverartarchive.org/...jpg",
//    "thumbnails" : {
//        "250" : "http://coverartarchive.org/...-250.jpg",
//        "500" : "http://coverartarchive.org/...-500.jpg",
//        "1200" : "http://coverartarchive.org/...-1200.jpg",
//        "small" : "http://coverartarchive.org/...-250.jpg",
//        "large" : "http://coverartarchive.org/...-500.jpg"
//    },
//    "approved" : true,
//    "edit" : 124,
//    "id" : "457"

}
