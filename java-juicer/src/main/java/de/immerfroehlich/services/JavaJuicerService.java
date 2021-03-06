package de.immerfroehlich.services;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.immerfroehlich.command.Command;
import de.immerfroehlich.command.CommandExecutor;
import de.immerfroehlich.command.Result;
import de.immerfroehlich.coverartarchive.CoverArtArchiveDownloader;
import de.immerfroehlich.javajuicer.model.TrackInfo;
import de.immerfroehlich.javajuicer.utils.FATCharRemover;
import de.immerfroehlich.services.parser.FileNamingConfigParser;

public class JavaJuicerService {
	
	public void createPathWithParents(String path) {
		Command command = new Command();
		command.setCommand("mkdir");
		command.addParameter("-p");
		command.addParameter(path);
		
		CommandExecutor executor = new CommandExecutor();
		executor.execute(command);
	}
	
	public void createMp3OfEachWav(String wavPath, String targetPath, List<TrackInfo> tracks, FileNamingConfigParser fileNamingService, Runnable trackfinishedCallback) {
    	List<File> files = listFilesOfFolder(wavPath);
    	Collections.sort(files);
    	
    	for (int i = 0; i < files.size(); i++) {
    		File fileSystemEntity = files.get(i);
    		if (!fileSystemEntity.isFile()) {
    			continue;
    		}
    		
    		String inputFile = fileSystemEntity.getName();
    		
    		TrackInfo track = tracks.get(i);
    		String outputFileWithoutType = fileNamingService.parseFileName(track);
    		String outputFile = outputFileWithoutType + ".mp3";
    		outputFile = FATCharRemover.removeUnallowedChars(outputFile);
    		
    		String fullQualifiedInputFile = wavPath + "/" + inputFile;
    		String fullQualifiedOuputFile = targetPath + "/" + outputFile;
    		
    		Result result = createMp3Of(fullQualifiedInputFile, fullQualifiedOuputFile, track);
    		trackfinishedCallback.run();
    		
    		if(result.hasErrors()) {
    			for(String line : result.getStdErr()) {
    				System.out.println(line);
    			}
    		}
    		
    		for(String line : result.asStringList()) {
    			System.out.println(line);
    		}
    		
    		System.out.println("Track " + track.trackNumber + " finished.");
    	}
    }
	
	public List<File> listFilesOfFolder(String wavPath) {
		File folder = new File(wavPath);
    	List<File> files = Arrays.asList(folder.listFiles());
		return files;
	}
	
	public static Result createMp3Of(String fullQualifiedInputFile, String fullQualifiedOuputFile, TrackInfo track) {
    	Command command = new Command();
		command.setCommand("lame");
		command.addParameter("--preset");
		command.addParameter("standard");
		command.addParameter("-q0");
		command.addParameter("--nohist");
		command.addParameter("--disptime");
		command.addParameter("20");
		
		command.addParameter("--tt");
		command.addParameter(track.title);
		command.addParameter("--ta");
		command.addParameter(track.artist);
		command.addParameter("--tl");
		command.addParameter(track.album);
		command.addParameter("--ty");
		command.addParameter(track.firstReleaseYear);
		command.addParameter("--tn");
		command.addParameter(track.trackNumber);
		
		if(track.cover.hasFrontCover) {
			command.addParameter("--ti");
			command.addParameter(track.cover.frontCoverPath);
		}
		
		command.addParameter(fullQualifiedInputFile);
		command.addParameter(fullQualifiedOuputFile);
		
		CommandExecutor cmd = new CommandExecutor();
		Result result = cmd.execute(command);
		
		return result;
    }
	
	public void addFrontCoverPathTo(List<TrackInfo> mp3Tracks, boolean frontCoverAvailable, String imagePath, CoverArtArchiveDownloader coverArtDownloader) {
		if(!frontCoverAvailable) return;
		
		String fullImagePath = coverArtDownloader.resizeFrontCoverImage(imagePath);
		if(fullImagePath.isEmpty()) return;
		
		for(TrackInfo mp3Track : mp3Tracks) {
			mp3Track.cover.hasFrontCover = true;
			mp3Track.cover.frontCoverPath = fullImagePath;
		}
	}

}
