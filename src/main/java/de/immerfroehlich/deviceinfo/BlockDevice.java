package de.immerfroehlich.deviceinfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "children" })
public class BlockDevice {
	
	public String name = "";
	public String type = "";

}
