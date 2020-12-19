package de.immerfroehlich.javajuicer.model.config;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.KebabCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(KebabCaseStrategy.class)
public class ConfigModel {
	
	public String version = "0.2";
	
	public String rootPath;
	public String drivePath;
	public List<Naming> namings = new ArrayList<>();

}
