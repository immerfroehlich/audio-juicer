package de.immerfroehlich.gui.controllers;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import de.immerfroehlich.javajuicer.mappers.Mp3TrackMapper;
import de.immerfroehlich.javajuicer.model.Mp3Track;
import de.immerfroehlich.musicbrainz.model.Disc;
import de.immerfroehlich.musicbrainz.model.Medium;
import de.immerfroehlich.musicbrainz.model.Release;
import de.immerfroehlich.musicbrainz.model.Track;
import de.immerfroehlich.services.LibDiscIdService;
import de.immerfroehlich.services.MusicBrainzService;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class MainTableContoller implements Initializable{
	
	LibDiscIdService libDiscIdService = new LibDiscIdService();
	MusicBrainzService musicbrainzService = new MusicBrainzService();
	Mp3TrackMapper mp3TrackMapper = new Mp3TrackMapper();
	
	@FXML
	private Button buttonMusicbrainz;
	
	@FXML
	private TableView<Mp3Track> tableView;

	@FXML
	private TableColumn<Mp3Track, StringProperty> trackColumn;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		buttonMusicbrainz.setOnAction(this::handleButtonMusicbrainzAction);		
	}
	
	protected void handleButtonMusicbrainzAction(ActionEvent event) {
		System.out.println("Button wurde geklickt.");
		
		//TODO get Drive path from drive selection
		String drivePath = "/dev/sr0";
		String discId = libDiscIdService.calculateDiscIdByDevicePath(drivePath);
		
		Optional<Disc> discOpt = musicbrainzService.lookupDiscById(discId);
    	List<Release> releases = null;
    	if(!discOpt.isPresent()) {
    		//TODO impl
    		System.err.println("Disc wasn't found.");
    		System.exit(0);    		
    	}
    	else {
    		releases = discOpt.get().releases;
    	}
    	
    	if(releases.size() == 1) {
    		Release release = releases.get(0);
    		
    		Medium medium = null;
    		if(release.media.size() == 1) {
        		medium = release.media.get(0);
        	}
    		else {
    			System.err.println("More then one medium!");
    		}
    		
    		ObservableList<Mp3Track> tracks = mp3TrackMapper.mapToMp3Tracks(release, "1990", medium);
    		tableView.setItems(tracks);
    	}
	}
}
