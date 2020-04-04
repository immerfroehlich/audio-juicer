package de.immerfroehlich.javajuicer.utils;

import org.academiadecodigo.bootcamp.Prompt;
import org.academiadecodigo.bootcamp.scanners.string.StringInputScanner;

public class ConsolePrompt {
	
	public static boolean yesNoQuestion(String question) {
		Prompt prompt = new Prompt(System.in, System.out);
    	StringInputScanner scanner = new StringInputScanner();
    	
    	System.out.print(question);
    	String yesNo = prompt.getUserInput(scanner);
    	boolean correct = yesNo.equals("y") ? true : false;
    	
    	return correct;
	}

	public static String askFreeText(String question) {
		Prompt prompt = new Prompt(System.in, System.out);
    	StringInputScanner scanner = new StringInputScanner();
    	
    	System.out.print(question);
    	String releaseTitle = prompt.getUserInput(scanner);
    	
    	return releaseTitle;
	}
}
