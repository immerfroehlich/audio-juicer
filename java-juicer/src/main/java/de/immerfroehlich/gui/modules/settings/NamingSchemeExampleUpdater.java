package de.immerfroehlich.gui.modules.settings;

import de.immerfroehlich.javajuicer.model.AlbumInfo;
import de.immerfroehlich.javajuicer.model.TrackInfo;
import de.immerfroehlich.services.parser.FileNamingConfigParser;
import javafx.scene.control.TextField;

public class NamingSchemeExampleUpdater {
	
	public void updateExampleTextField(String namingScheme, TextField exampleTextField) {
		
		AlbumInfo albumInfo = new AlbumInfo();
		albumInfo.artist = "AlbumArtist";
		albumInfo.title = "AlbumTitle";
		albumInfo.multiCdRelease = true;
		albumInfo.cdNumber = "2";
		
		
		TrackInfo track = new TrackInfo();
		track.title = "TrackTitle";
		track.artist = "TrackArtist";
		track.trackNumber = "11";
		
		updateExampleTextField(namingScheme, exampleTextField, albumInfo, track);
	}
	
	
	public void updateExampleTextField(String namingScheme, TextField exampleTextField, AlbumInfo album, TrackInfo track) {
		FileNamingConfigParser parser = new FileNamingConfigParser();
		String directories = parser.parse(namingScheme, album);
		String fileName = parser.parseFileName(track);
		String text = directories + "/" + fileName;
		exampleTextField.setText(text);
	}
}
