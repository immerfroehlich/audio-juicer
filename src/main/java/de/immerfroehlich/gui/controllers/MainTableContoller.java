package de.immerfroehlich.gui.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.academiadecodigo.bootcamp.Prompt;
import org.academiadecodigo.bootcamp.scanners.integer.IntegerInputScanner;

import de.immerfroehlich.coverartarchive.CoverArtService;
import de.immerfroehlich.coverartarchive.model.Image;
import de.immerfroehlich.gui.FXUtils;
import de.immerfroehlich.gui.InfoAlert;
import de.immerfroehlich.gui.TextInputDialog;
import de.immerfroehlich.javajuicer.mappers.Mp3TrackMapper;
import de.immerfroehlich.javajuicer.model.Configuration;
import de.immerfroehlich.javajuicer.model.Mp3Track;
import de.immerfroehlich.javajuicer.utils.FATCharRemover;
import de.immerfroehlich.musicbrainz.model.Disc;
import de.immerfroehlich.musicbrainz.model.Medium;
import de.immerfroehlich.musicbrainz.model.Release;
import de.immerfroehlich.prompt.Prompter;
import de.immerfroehlich.services.LibDiscIdService;
import de.immerfroehlich.services.MusicBrainzService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class MainTableContoller implements Initializable{
	
	private LibDiscIdService libDiscIdService = new LibDiscIdService();
	private MusicBrainzService musicbrainzService = new MusicBrainzService();
	private CoverArtService coverArtService = new CoverArtService();
	private Mp3TrackMapper mp3TrackMapper = new Mp3TrackMapper();
	
	//TODO make a static access class that contains the scene and the config. 
	private Configuration config = new Configuration();
	
	@FXML private Button buttonMusicbrainz;
	@FXML private TableView<MainTableModel> tableView;
	@FXML private TableColumn<MainTableModel, String> columnTrack;
	@FXML private TableColumn<MainTableModel, Boolean> columnPregap;
	@FXML private TableColumn<MainTableModel, String> columnArtist;
	@FXML private TableColumn<MainTableModel, String> columnTitle;
	@FXML private VBox vboxImages;
	@FXML private Label driveLabel;
	@FXML private ChoiceBox<String> driveChoiceBox;
	@FXML private Label pathLabel;
	@FXML private TextField pathTextField;
	
	private ObservableList<MainTableModel> data = FXCollections.observableArrayList();
	
	private ModelMapper mapper = new ModelMapper(data);
	
	
	private Service<String> calculateDiscIdService = new Service<String>() {
		@Override
		protected Task<String> createTask() {
			return new Task<String>() {
				@Override
				protected String call() throws Exception {
					return libDiscIdService.calculateDiscIdByDevicePath(config.drivePath);
				}
			};
		}
	};
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		buttonMusicbrainz.setOnAction(this::handleButtonTest);
		
		pathTextField.setText(config.rootPath);
		pathTextField.setDisable(true);
		
		tableView.setItems(data);
		
		//Workaround: The table is included in the fxml it seems it is not possible
		//to access the fx:ids in the same controller.
		columnTrack = (TableColumn<MainTableModel, String>) tableView.getColumns().get(0);
		columnPregap = (TableColumn<MainTableModel, Boolean>) tableView.getColumns().get(1);
		columnArtist = (TableColumn<MainTableModel, String>) tableView.getColumns().get(2);
		columnTitle = (TableColumn<MainTableModel, String>) tableView.getColumns().get(3);
		
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
	
	protected void handleButtonTest(ActionEvent event) {
		doIt();
	}
	
	private void doIt() {
		Service<List<Mp3Track>> service = FXUtils.createServiceTask(()-> {
//			List<Release> releases = musicbrainzService.searchReleasesByTitle(releaseTitle);
//			String relText = releases.stream().map(x -> x.title).collect(Collectors.joining());
//			System.out.println(relText);
			
			String discid = libDiscIdService.calculateDiscIdByDevicePath(config.drivePath);
			System.out.println("Calculated DiscId is: " + discid);
			Optional<Disc> discOpt = musicbrainzService.lookupDiscById(discid);
			List<Release> releases;
			if(!discOpt.isPresent()) {
				String releaseTitle = promptForReleaseTitle();
				releases = musicbrainzService.searchReleasesByTitle(releaseTitle);
			}
			else {
				releases = discOpt.get().releases;
			}
			
			boolean noReleasesFound = releases.size() == 0; 
			if(noReleasesFound) {
				showNoReleaseFoundDialog();
				return null;
			}
			
			Release release = promptForRelease(releases, "Select release");
			release = musicbrainzService.reloadRelease(release);
			//TODO Das erste Erscheinungsjahr lässt sich so nicht zuverlässig ermitteln! Wird bei Musicbrainz aber je Album angegeben.
			//Ggf. das erste Erscheinungsjahr je Track ermitteln, z.B. bei Compilations
			String releaseDate = promptForReleaseYear(selectedRelease.title, "Select first release date");
			Medium medium = promptForMedium(release);
			
	    	String cdArtist = release.artistCredit.get(0).name;
	    	String cdTitle = release.title;
	    	cdArtist = FATCharRemover.removeUnallowedChars(cdArtist);
	    	cdTitle = FATCharRemover.removeUnallowedChars(cdTitle);
	    	
	    	String mp3RootAlbumPath = config.rootPath + "/" + "mp3" + "/" + cdArtist + "/" + cdTitle;
	    	String mp3CdPath = mp3RootAlbumPath;
	    	String wavCdPath = config.rootPath + "/" + "wav" + "/" + cdArtist + "/" + cdTitle;
	    	if(release.multiCDRelease) {
	    		String cdPathAddon = "/CD" + medium.position;
	    		mp3CdPath += cdPathAddon;
	    		wavCdPath += cdPathAddon;
	    	}
	    	String imagePath = mp3RootAlbumPath + "/" + "images";
	    	
	    	//TODO I guess I need a container for all the paths.
	    	
	    	lookupCoverArtForRelease(release);
	    	
	    	List<Mp3Track> mp3Tracks = mp3TrackMapper.mapToMp3Tracks(release, releaseDate, medium);
			
			return mp3Tracks;
		});
		
		service.setOnSucceeded((e) -> {
			List<Mp3Track> result = service.getValue();
			mapper.map(result);
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
	
	private Medium promptForMedium(Release release) {
		if(release.media.size() == 1) {
    		return release.media.get(0);
    	}
    	
    	//TODO Reuse ReleaseSelectionDialog to select the first release year.
		
    	int cdCount = 0;
    	for(int i=0; i < release.media.size(); i++) {
    		Medium media = release.media.get(i);
			if(media.format.equals("CD")) {
				System.out.println("[" + i + "]");
				System.out.println(media.position);
				System.out.println(media.trackCount);
				
				System.out.println("---------------");
				
				cdCount++;
			}
		}
    	
    	if(cdCount > 1) {
    		release.multiCDRelease = true;
    	}
    	
    	Prompt prompt = new Prompt(System.in, System.out);
    	IntegerInputScanner scanner = new IntegerInputScanner();
    	System.out.print("Select CD:");
    	Integer number = prompt.getUserInput(scanner);
    	
		return release.media.get(number);
	}

	private Release selectedYearRelease;
	private String promptForReleaseYear(String releaseTitle, String text) {
		List<Release> releases = musicbrainzService.searchReleasesByTitle(releaseTitle);
		
		FXUtils.runAndWait(() -> {
			URL fxmlUrl = getClass().getResource("releaseSelectionDialog.fxml");
			FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
			ReleaseSelectionDialogController dialogController = new ReleaseSelectionDialogController(releases, "artistCredit.name", "date", "media.format", "Artist:", "Date:", "Medium:");
			fxmlLoader.setController(dialogController);
			try {
				fxmlLoader.load();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			
			Release release = dialogController.showAndWait();
			selectedYearRelease = release;
		});
	
		return selectedYearRelease.date;

//		String additionalEntry = "Or enter release year/date manually";
//		
//		Integer number = Prompter.askSelectFromList(releases, additionalEntry, text, (release) -> {
//			String entry = "\n"
//					+ release.title + "\n" 
//					+ release.date + "\n"
//					+ release.barcode + "\n"
//					+ release.artistCredit.stream().map(x -> x.name).collect(Collectors.joining());
//			
//			return entry;
//		});
//		
//		String releaseDate;		
//		
//		boolean manuallySelected = number > (releases.size() -1);
//		if(manuallySelected) {
//			String question = "Enter release year/date";
//			releaseDate = Prompter.askForString(question);
//		}
//		else {
//			releaseDate = releases.get(number).date;
//		}
//		
//		return releaseDate;
	}

	private Release selectedRelease;
	private Release promptForRelease(List<Release> releases, String string) {
		FXUtils.runAndWait(()->{
			//TODO if only one release is available select it.
			
			URL fxmlUrl = getClass().getResource("releaseSelectionDialog.fxml");
			FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
			ReleaseSelectionDialogController dialogController = new ReleaseSelectionDialogController(releases, "title", "date", "barcode", "Title:", "Date: ", "Barcode: ");
			fxmlLoader.setController(dialogController);
			try {
				fxmlLoader.load();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			
			Release release = dialogController.showAndWait();
			selectedRelease = release;
		});
		
		return selectedRelease;
	}

	private void showNoReleaseFoundDialog() {
		FXUtils.runAndWait(()->{
			String text = "No Release was found for this CD.\n"
					+ "Please login to musicbrainz.org and add your CD/release.\n"
					+ "Afterwards you could start this program again.";
			InfoAlert alert = new InfoAlert(text);
			alert.showAndWait();
		});
	}
	
	String releaseTitle;
	private String promptForReleaseTitle() {
		FXUtils.runAndWait(()->{
			TextInputDialog dialog = new TextInputDialog("Please enter the release title.");
			releaseTitle = dialog.showAndWait();
			System.out.println(releaseTitle);
		});
		return releaseTitle;
	}

	private void lookupCoverArtForRelease(Release release) {
		List<Image> images = coverArtService.lookupCoverArtByMbid(release.id);
		FXUtils.runAndWait(() -> {
			images.stream().forEach(e -> {
				String url = e.thumbnails.get("small");
				javafx.scene.image.Image image = new javafx.scene.image.Image(url);
				ImageView imageView = new ImageView(image);
				imageView.getStyleClass().add("vboxImage");
				vboxImages.getChildren().add(imageView);
			});
		});
	}
}
