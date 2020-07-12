package de.immerfroehlich.services.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.immerfroehlich.javajuicer.model.Album;
import de.immerfroehlich.javajuicer.model.Mp3Track;

public class FileNamingConfigParser {
	
	private String directories = "";
	private String fileName = "";
	
	public String parse(String fileNamingConfig, Album albumInfo) {
		if(!albumInfo.multiCdRelease) {
			//Remove all optional cd terms
			int start = fileNamingConfig.indexOf("<");
			int end = fileNamingConfig.indexOf(">");
			
			String substring = fileNamingConfig.substring(start, end+1);
			if(substring.contains("%c")) {
				fileNamingConfig = fileNamingConfig.replace(substring, "");
			}
			
			albumInfo.cdNumber = "1";
		}
		
		if(!fileNamingConfig.startsWith("/")) {
			//TODO throw new Exception Please start with /
		}
		
		fileNamingConfig = replaceVariables(fileNamingConfig, albumInfo);
		
		List<String> directories = new ArrayList<String>( Arrays.asList(fileNamingConfig.split("/")) );
		directories.remove(0);
		fileName = directories.get(directories.size()-1);
		directories.remove(directories.size()-1);
		
		this.directories = "/" + directories.stream().reduce((s1, s2) -> s1 + "/" + s2).get();
		
		return this.directories;
	}

	private String replaceVariables(String fileNamingConfig, Album albumInfo) {
		fileNamingConfig = fileNamingConfig.replace("%a", albumInfo.artist);
		fileNamingConfig = fileNamingConfig.replace("%c", albumInfo.cdNumber);
		fileNamingConfig = fileNamingConfig.replace("<", "");
		fileNamingConfig = fileNamingConfig.replace(">", "");
		fileNamingConfig = fileNamingConfig.replace("%l", albumInfo.title);
		return fileNamingConfig;
	}
	
	private String replaceVariablesInFilename(String fileNamingConfig, Mp3Track mp3Track, String trackNumber) {
		fileNamingConfig = fileNamingConfig.replace("%n", trackNumber);
		fileNamingConfig = fileNamingConfig.replace("%r", mp3Track.artist);
		fileNamingConfig = fileNamingConfig.replace("%t", mp3Track.title);
		return fileNamingConfig;
	}

	public String parseFileName(Mp3Track mp3Track, String trackNumber) {
		if(fileName.isEmpty()) {
			throw new IllegalStateException("Use the other parse method first to get the filename scheme.");
		}
		return replaceVariablesInFilename(this.fileName, mp3Track, trackNumber);
	}

}
