package de.immerfroehlich.javajuicer.utils;

import java.util.ArrayList;
import java.util.List;

public class FATUnallowedChars {
	

//    < (less than)
//    > (greater than)
//    : (colon)
//    " (double quote)
//    / (forward slash)
//    \ (backslash)
//    | (vertical bar or pipe)
//    ? (question mark)
//    * (asterisk)

	
	// see https://docs.microsoft.com/de-de/windows/win32/fileio/naming-a-file?redirectedfrom=MSDN
	public static final List<String> getUnallowedChars() {
		List<String> unallowedChars = new ArrayList<>();
		
		unallowedChars.add("<");
		unallowedChars.add(">");
		unallowedChars.add(":");
		unallowedChars.add("\"");
		unallowedChars.add("/");
		unallowedChars.add("\\");
		unallowedChars.add("|");
		unallowedChars.add("?");
		unallowedChars.add("*");
		
		return unallowedChars;
	}

}
