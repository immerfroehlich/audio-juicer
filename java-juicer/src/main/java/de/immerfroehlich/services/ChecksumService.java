package de.immerfroehlich.services;

import java.util.List;

import de.immerfroehlich.command.Command;
import de.immerfroehlich.command.CommandExecutor;
import de.immerfroehlich.command.Result;

public class ChecksumService {
	
	public void createChecksumOfAllFilesIn(String directoryPath) {
    	Command command = new Command();
		command.setCommand("sha256sum");
		command.addParameter("-b");
		command.addParameter("*");
		command.setBasePath(directoryPath);
		
		CommandExecutor executor = new CommandExecutor();
		Result result = executor.execute(command);
		
		if(result.hasErrors()) {
			List<String> errorRows = result.getStdErr();
			//TODO Richtige Fehlerbehandlung.
			for(String error : errorRows) {
				System.out.println(error);
			}
		}
		else {
			List<String> output = result.asStringList();
			for(String line : output) {
				System.out.println(line);
			}
		}
	}

}
