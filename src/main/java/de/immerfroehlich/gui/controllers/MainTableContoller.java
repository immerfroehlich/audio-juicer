package de.immerfroehlich.gui.controllers;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import de.immerfroehlich.coverartarchive.CoverArtService;
import de.immerfroehlich.coverartarchive.model.Image;
import de.immerfroehlich.javajuicer.mappers.Mp3TrackMapper;
import de.immerfroehlich.javajuicer.model.Mp3Track;
import de.immerfroehlich.musicbrainz.model.Disc;
import de.immerfroehlich.musicbrainz.model.Medium;
import de.immerfroehlich.musicbrainz.model.Release;
import de.immerfroehlich.services.LibDiscIdService;
import de.immerfroehlich.services.MusicBrainzService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class MainTableContoller implements Initializable{
	
	LibDiscIdService libDiscIdService = new LibDiscIdService();
	MusicBrainzService musicbrainzService = new MusicBrainzService();
	CoverArtService coverArtService = new CoverArtService();
	Mp3TrackMapper mp3TrackMapper = new Mp3TrackMapper();
	
	@FXML
	private Button buttonMusicbrainz;
	
	@FXML
	private TableView<MainTableModel> tableView;

	@FXML
	private TableColumn<MainTableModel, String> columnTrack;
	
	@FXML
	private TableColumn<MainTableModel, Boolean> columnPregap;
	
	@FXML
	private TableColumn<MainTableModel, String> columnArtist;
	
	@FXML
	private TableColumn<MainTableModel, String> columnTitle;
	
	@FXML
	private VBox vboxImages;
	
	private ObservableList<MainTableModel> data = FXCollections.observableArrayList();
	
	private ModelMapper mapper = new ModelMapper(data);

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		buttonMusicbrainz.setOnAction(this::handleButtonMusicbrainzAction);
		
		tableView.setItems(data);
		
		columnTrack.setCellValueFactory( (cellDataFeature) -> {
			return cellDataFeature.getValue().track;
		});
		
		columnPregap.setCellValueFactory( (cellDataFeature) -> {
			return cellDataFeature.getValue().pregap;
		});
				
		columnArtist.setCellValueFactory( (cellDataFeature) -> {
			return cellDataFeature.getValue().artist;
		});
		
		columnTitle.setCellValueFactory( (cellDataFeature) -> {
			return cellDataFeature.getValue().title;
		});
		
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
    		
    		List<Mp3Track> tracks = mp3TrackMapper.mapToMp3Tracks(release, "1990", medium);
    		mapper.map(tracks); //Binding is active!
    		
    		List<Image> images = coverArtService.lookupCoverArtByMbid(release.id);
    		images.stream().forEach(e -> {
    			String url = e.thumbnails.get("small");
    			javafx.scene.image.Image image = new javafx.scene.image.Image(url);
    			ImageView imageView = new ImageView(image);
    			imageView.getStyleClass().add("vboxImage");
    			vboxImages.getChildren().add(imageView);
    		});
    	}
	}
}
