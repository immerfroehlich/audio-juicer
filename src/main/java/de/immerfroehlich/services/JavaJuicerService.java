package de.immerfroehlich.services;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.immerfroehlich.command.Command;
import de.immerfroehlich.command.CommandExecutor;
import de.immerfroehlich.command.Result;
import de.immerfroehlich.coverartarchive.CoverArtArchiveDownloader;
import de.immerfroehlich.javajuicer.model.Mp3Track;
import de.immerfroehlich.javajuicer.utils.FATCharRemover;

public class JavaJuicerService {
	
	public void createPathWithParents(String path) {
		Command command = new Command();
		command.setCommand("mkdir");
		command.addParameter("-p");
		command.addParameter(path);
		
		CommandExecutor executor = new CommandExecutor();
		executor.execute(command);
	}
	
	public void createMp3OfEachWav(String wavPath, String targetPath, List<Mp3Track> tracks) {
    	List<File> files = listFilesOfFolder(wavPath);
    	Collections.sort(files);
    	
    	for (int i = 0; i < files.size(); i++) {
    		File fileSystemEntity = files.get(i);
    		if (!fileSystemEntity.isFile()) {
    			continue;
    		}
    		
    		String inputFile = fileSystemEntity.getName();
    		
    		Mp3Track track = tracks.get(i);
    		String trackNumber = createTrackNumber(i+1);
    		String outputFile = trackNumber + " " + track.title + ".mp3";
    		outputFile = FATCharRemover.removeUnallowedChars(outputFile);
    		
    		String fullQualifiedInputFile = wavPath + "/" + inputFile;
    		String fullQualifiedOuputFile = targetPath + "/" + outputFile;
    		
    		Result result = createMp3Of(fullQualifiedInputFile, fullQualifiedOuputFile, track, trackNumber);
    		
    		if(result.hasErrors()) {
    			for(String line : result.getStdErr()) {
    				System.out.println(line);
    			}
    		}
    		
    		for(String line : result.asStringList()) {
    			System.out.println(line);
    		}
    		
    		System.out.println("Track " + trackNumber + " finished.");
    	}
    }
	
	public List<File> listFilesOfFolder(String wavPath) {
		File folder = new File(wavPath);
    	List<File> files = Arrays.asList(folder.listFiles());
		return files;
	}
	
	private static String createTrackNumber(int i) {
    	String trackNumber = "";
		if(i < 10) {
			trackNumber = "0";
		}
		trackNumber += i;
		return trackNumber;
	}
	
	public static Result createMp3Of(String fullQualifiedInputFile, String fullQualifiedOuputFile, Mp3Track track, String trackNumber) {
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
		command.addParameter(track.title);
		command.addParameter("--ty");
		command.addParameter(track.firstReleaseYear);
		command.addParameter("--tn");
		command.addParameter(trackNumber);
		
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
	
	public void addFrontCoverPathTo(List<Mp3Track> mp3Tracks, boolean frontCoverAvailable, String imagePath, CoverArtArchiveDownloader coverArtDownloader) {
		if(!frontCoverAvailable) return;
		
		String fullImagePath = coverArtDownloader.resizeFrontCoverImage(imagePath);
		if(fullImagePath.isEmpty()) return;
		
		for(Mp3Track mp3Track : mp3Tracks) {
			mp3Track.cover.hasFrontCover = true;
			mp3Track.cover.frontCoverPath = fullImagePath;
		}
	}

}
