package de.immerfroehlich.prompt;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.academiadecodigo.bootcamp.Prompt;
import org.academiadecodigo.bootcamp.scanners.integer.IntegerInputScanner;
import org.academiadecodigo.bootcamp.scanners.string.StringInputScanner;

public class Prompter {
	
	public static <T> T askSelectFromList(List<T> list, String question, Function<T, String> selectionRepresentationFunction) {
		return askSelectFromList(list, "", question, selectionRepresentationFunction, () ->  {
			Integer number = userInputNumber();
			return list.get(number);
		});		
	}
	
	public static <T> Integer askSelectFromList(List<T> list, String additionalEntry, String question, Function<T, String> supplier) {
		return askSelectFromList(list, additionalEntry, question, supplier, () -> {
			Integer number = userInputNumber();
			return number;
		});
	}
	
	private static <T, R> R askSelectFromList(List<T> list, String additionalEntry, String question,
			Function<T, String> selectionRepresentationFunction, Supplier<R> promptSupplier) {
    	
    	for(int i = 0; i < list.size(); i++) {
    		T element = list.get(i);
    		String representation = selectionRepresentationFunction.apply(element);
    		System.out.print("[" + i + "] ");
			System.out.println(representation);
			
			System.out.println("---------------");
		}
    	
    	if(!additionalEntry.isEmpty()) {
    		System.out.print("[" + list.size() + "] ");
			System.out.println(additionalEntry);
			
			System.out.println("---------------");
    	}
    	
    	System.out.print(question);
    	
    	return promptSupplier.get();
	}
	
	private static Integer userInputNumber() {
		Prompt prompt = new Prompt(System.in, System.out);
		IntegerInputScanner scanner = new IntegerInputScanner();
		Integer number = prompt.getUserInput(scanner);
		return number;
	}
	
	public static boolean askYesNo(String question) {
		Prompt prompt = new Prompt(System.in, System.out);
    	StringInputScanner scanner = new StringInputScanner();
    	
    	System.out.print(question);
    	String yesNo = prompt.getUserInput(scanner);
    	boolean correct = yesNo.equals("y") ? true : false;
    	return correct;
	}
	
	public static String askForString(String question) {
		Prompt prompt = new Prompt(System.in, System.out);
		StringInputScanner stringScanner = new StringInputScanner();
		return prompt.getUserInput(stringScanner);
	}

}
