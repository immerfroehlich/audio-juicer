package de.immerfroehlich.coverartarchive;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import de.immerfroehlich.command.Command;
import de.immerfroehlich.command.CommandExecutor;
import de.immerfroehlich.command.Result;
import de.immerfroehlich.musicbrainz.model.CoverArtArchive;
import de.immerfroehlich.musicbrainz.model.Release;
import fm.last.musicbrainz.coverart.CoverArt;
import fm.last.musicbrainz.coverart.CoverArtArchiveClient;
import fm.last.musicbrainz.coverart.CoverArtImage;
import fm.last.musicbrainz.coverart.impl.DefaultCoverArtArchiveClient;

public class CoverArtArchiveDownloader {
	
	public String downloadImages(Release release, String path) {
		boolean viaHttps = false;
		CoverArtArchiveClient client = new DefaultCoverArtArchiveClient(viaHttps);
		UUID mbid = UUID.fromString(release.id);
		String frontCoverSmallPath = "";
		
		CoverArt coverArt = null;
		coverArt = client.getByMbid(mbid);
		if (coverArt != null) {
			CoverArtArchive coverInfo = release.coverArtArchive;
			if(coverInfo.front) {
				CoverArtImage image = coverArt.getFrontImage();
				String filename = "front";
				downloadImage(image, filename, path);
				frontCoverSmallPath = resizeImage(filename, path);
			}
			if(coverInfo.back) {
				CoverArtImage image = coverArt.getBackImage();
				String filename = "back";
				downloadImage(image, filename, path);
			}
		}
		
		return frontCoverSmallPath;	
	}
	
	private void downloadImage(CoverArtImage image, String filename, String path) {
		File output = new File(path + "/" + filename + ".jpg"); //Always jpg?
		boolean successfull = false;
		try {
			FileUtils.copyInputStreamToFile(image.getImage(), output);
			successfull = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(successfull) {
			System.out.println(filename + ".jpg" + " was downloaded from Cover Art Archive.");
		}
	}
	
	private String resizeImage(String filename, String path) {
		
		String source = path + "/" + filename + ".jpg";
		String target = path + "/" + "front_small.jpg";
		
		Command command = new Command();
		command.setCommand("convert");
		command.addParameter("-resize");
		command.addParameter("300x300");
		command.addParameter(source);
		command.addParameter(target);
		
		System.out.println(command.toString());
		
		//convert -resize 300x300 front.jpg front_small.jpg
		
		CommandExecutor cmd = new CommandExecutor();
		Result result = cmd.execute(command);
		
		if(result.hasErrors()) {
			System.err.println("Image resizing (for embedding into mp3) failed.");
			//result.printStdErr(); //TODO new version currently not available.
			for(String error : result.getStdErr()) {
				System.err.println(error);
			}
		}
		else {
			System.out.println("Image Image resizing (for embedding into mp3) was successfull");
		}
		
		return target;
	}

}
