package de.immerfroehlich.javajuicer.utils;

import java.util.List;

public class FATCharRemover {
	
	public static String removeUnallowedChars(String filename) {
		List<String> unallowedChars = FATUnallowedChars.getUnallowedChars();
		for(String charr : unallowedChars) {
			filename = filename.replace(charr, "");
		}
		
		return filename;
	}

}
