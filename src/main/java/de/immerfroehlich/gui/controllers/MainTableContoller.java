package de.immerfroehlich.gui.controllers;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

import de.immerfroehlich.coverartarchive.CoverArtService;
import de.immerfroehlich.coverartarchive.model.Image;
import de.immerfroehlich.gui.FXUtils;
import de.immerfroehlich.gui.Tuple;
import de.immerfroehlich.javajuicer.mappers.Mp3TrackMapper;
import de.immerfroehlich.javajuicer.model.Mp3Track;
import de.immerfroehlich.musicbrainz.model.Disc;
import de.immerfroehlich.musicbrainz.model.Medium;
import de.immerfroehlich.musicbrainz.model.Release;
import de.immerfroehlich.services.LibDiscIdService;
import de.immerfroehlich.services.MusicBrainzService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
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
	
	//TODO get Drive path from drive selection
	String drivePath = "/dev/sr0";
	
	private Service<String> calculateDiscIdService = new Service<String>() {
		@Override
		protected Task<String> createTask() {
			return new Task<String>() {
				@Override
				protected String call() throws Exception {
					return libDiscIdService.calculateDiscIdByDevicePath(drivePath);
				}
			};
		}
	};
	
//	private Service<Optional<Disc>> loadReleaseByDiscIdService = new Service<Optional<Disc>>() {
//		@Override
//		protected Task<Optional<Disc>> createTask() {
//			return new Task<Optional<Disc>>() {
//
//				@Override
//				protected Optional<Disc> call() throws Exception {
//					return musicbrainzService.lookupDiscById(discId);
//				}
//			};
//		}
//	};

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//buttonMusicbrainz.setOnAction(this::handleButtonMusicbrainzAction);
		
		buttonMusicbrainz.setOnAction(this::handleButtonTest);
		
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
	
	private String title;
	protected void handleButtonTest(ActionEvent event) {
		Service<Tuple<Release, Medium>> service = FXUtils.createServiceTask(()-> {
			
			
			FXUtils.runAndWait(()->{
				ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
				 Dialog<String> dialog = new Dialog<>();
				 dialog.getDialogPane().getButtonTypes().add(loginButtonType);
				 boolean disabled = false; // computed based on content of text fields, for example
				 dialog.getDialogPane().lookupButton(loginButtonType).setDisable(disabled);
				 
				 //TODO Workaround for openjfx bug 222. Remove after backport to Java 11 or update to Java 12
				 //See https://github.com/javafxports/openjdk-jfx/issues/222
				 dialog.setResizable(true);

				 dialog.showAndWait()
				 	.filter(response -> response.equals("OK"))
				 	.ifPresent(response -> System.out.println("Dialog was used."));
				 
				 title = "test";
			});
			
//			String discid = libDiscIdService.calculateDiscIdByDevicePath(drivePath);
//			System.out.println("Calculated DiscId is: " + discid);
//			Optional<Disc> discOpt = musicbrainzService.lookupDiscById(discid);
//			List<Release> releases;
//			if(!discOpt.isPresent()) {
//				String releaseTitle = promptForReleaseTitle();
//				releases = searchReleasesByTitle(releaseTitle);
//			}
//			else {
//				releases = discOpt.get().releases;
//			}
//			
//			boolean noReleasesFound = releases.size() == 0; 
//			if(noReleasesFound) {
//				System.out.println("Please login to musicbrainz.org and add your CD/release.");
//				System.out.println("Afterwards you could start this program again.");
//				
//				System.exit(0);
//			}
//			
//			Release release = promptForRelease(releases, "Select release");
//			release = reloadRelease(release);
//			//TODO Das erste Erscheinungsjahr lässt sich so nicht zuverlässig ermitteln! Wird bei Musicbrainz aber je Album angegeben.
//			//Ggf. das erste Erscheinungsjahr je Track ermitteln, z.B. bei Compilations
//			String releaseDate = promptForReleaseYear(releases, "Select first release date");
//			Medium medium = promptForMedium(release);
//			
			return new Tuple<Release, Medium>(new Release(), new Medium());
		});
		
		service.start();
		
		
//		calculateDiscIdService = FXUtils.createServiceTask( () -> {
//			System.out.println("1: On Thread " + Thread.currentThread().getName());
//			System.out.println("1: sleep 3000");
//			Thread.sleep(3000);
//			FXUtils.runAndWait( () -> {
//				System.out.println("2: On Thread " + Thread.currentThread().getName());
//				System.out.println("2: Run on FX Application Thread.");
//			});
//			return "";
//		});
//		
//		calculateDiscIdService.setOnSucceeded(e->{
//			System.out.println("3: On Thread " + Thread.currentThread().getName());
//			System.out.println("3: On Succeeded.");
//		});
//		calculateDiscIdService.start();
//		
//		System.out.println("4: On Thread " + Thread.currentThread().getName());
//		System.out.println("4: Normal program flow.");
	}
	
	private void showReleaseSelectDialog() {

	}
	
//	protected void handleButtonMusicbrainzAction(ActionEvent event) {
//		System.out.println("Button wurde geklickt.");
//		
//		
//		
//		
//		Optional<Disc> discOpt = musicbrainzService.lookupDiscById(discId);
//    	List<Release> releases = null;
//    	if(!discOpt.isPresent()) {
//    		//TODO impl
//    		System.err.println("Disc wasn't found.");
//    		System.exit(0);    		
//    	}
//    	else {
//    		releases = discOpt.get().releases;
//    	}
//    	
//    	if(releases.size() == 1) {
//    		Release release = releases.get(0);
//    		
//    		Medium medium = null;
//    		if(release.media.size() == 1) {
//        		medium = release.media.get(0);
//        	}
//    		else {
//    			System.err.println("More then one medium!");
//    		}
//    		
//    		List<Mp3Track> tracks = mp3TrackMapper.mapToMp3Tracks(release, "1990", medium);
//    		mapper.map(tracks); //Binding is active!
//    		
//    		List<Image> images = coverArtService.lookupCoverArtByMbid(release.id);
//    		images.stream().forEach(e -> {
//    			String url = e.thumbnails.get("small");
//    			javafx.scene.image.Image image = new javafx.scene.image.Image(url);
//    			ImageView imageView = new ImageView(image);
//    			imageView.getStyleClass().add("vboxImage");
//    			vboxImages.getChildren().add(imageView);
//    		});
//    	}
//	}
}
