package de.immerfroehlich.services;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import de.immerfroehlich.command.Command;
import de.immerfroehlich.command.CommandExecutor;
import de.immerfroehlich.command.Result;

public class ChecksumService {
	
	public void createChecksumOfAllFilesIn(String directoryPath) {
		
		File file = new File(directoryPath);
		String[] files = file.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return new File(dir, name).isFile();
			}
		});
		
    	Command command = new Command();
		command.setCommand("sha256sum");
		command.addParameter("-b");
		for(String fil : files) {
			command.addParameter(fil); //formerly used *. but * gets replaced by bash as a list of all files in the base directory, so a list of all files is needed here.
		}
		command.setBasePath(directoryPath);
		
		CommandExecutor executor = new CommandExecutor();
		Result result = executor.execute(command);
		
		if(result.hasErrors()) {
			List<String> errorRows = result.getStdErr();
			//TODO Richtige Fehlerbehandlung.
			for(String error : errorRows) {
				System.out.println(error);
				return;
			}
		}
		
		String content = result.asStringList().stream().collect(Collectors.joining("\n"));
		
		file = new File(directoryPath + "/sha256sum.txt");
		try {
			FileUtils.touch(file);
			FileUtils.writeStringToFile(file, content, StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
