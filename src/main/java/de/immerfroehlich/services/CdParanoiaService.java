package de.immerfroehlich.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
	
	public CdInfo getCDInfo() {
//		cdparanoia -Q
		Command command = new Command();
		command.setCommand("cd-info");
		
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
			
			extractISRC(output);
		}
		
		return new CdInfo();
	}

	private void extractISRC(List<String> output) {
		Stream<String> isrcInfo = output.stream().filter( (element) -> {
			return element.matches("^TRACK\\s+\\d+\\s+ISRC.*");
		});
		
		List<String> isrcs = new ArrayList<>();
		isrcInfo.forEach( (element) -> {
			System.out.println(element.toString());
			String isrc = element.split(":")[1].trim();
			isrcs.add(isrc);
		});
		
		isrcs.stream().forEach((element) -> {
			System.out.println(element);
		});
	}

}
