package de.immerfroehlich.services;

import java.util.List;

import de.immerfroehlich.command.Command;
import de.immerfroehlich.command.CommandExecutor;
import de.immerfroehlich.command.Result;

public class CdParanoiaService {
	
	public void ripWavFromStdCdromTo(String wavPath) {
    	Command command = new Command();
		command.setCommand("cdparanoia");
		command.addParameter("-B");
		command.setBasePath(wavPath);
		
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
