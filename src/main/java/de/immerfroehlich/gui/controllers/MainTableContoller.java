package de.immerfroehlich.gui.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

import org.academiadecodigo.bootcamp.Prompt;
import org.academiadecodigo.bootcamp.scanners.integer.IntegerInputScanner;

import de.immerfroehlich.coverartarchive.CoverArtArchiveDownloader;
import de.immerfroehlich.coverartarchive.CoverArtService;
import de.immerfroehlich.coverartarchive.model.Image;
import de.immerfroehlich.gui.FXUtils;
import de.immerfroehlich.gui.InfoAlert;
import de.immerfroehlich.gui.TextInputDialog;
import de.immerfroehlich.gui.YesNoDialog;
import de.immerfroehlich.javajuicer.mappers.Mp3TrackMapper;
import de.immerfroehlich.javajuicer.model.Configuration;
import de.immerfroehlich.javajuicer.model.Mp3Track;
import de.immerfroehlich.javajuicer.utils.FATCharRemover;
import de.immerfroehlich.musicbrainz.model.Disc;
import de.immerfroehlich.musicbrainz.model.Medium;
import de.immerfroehlich.musicbrainz.model.Release;
import de.immerfroehlich.services.CdParanoiaService;
import de.immerfroehlich.services.JavaJuicerService;
import de.immerfroehlich.services.LibDiscIdService;
import de.immerfroehlich.services.MusicBrainzService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
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
	private JavaJuicerService javaJuicerService = new JavaJuicerService();
	private CdParanoiaService cdService = new CdParanoiaService();
	private Mp3TrackMapper mp3TrackMapper = new Mp3TrackMapper();
	
	//TODO make a static access class that contains the scene and the config. 
	private Configuration config = new Configuration();
	private Release selectedYearRelease;
	private Release selectedRelease;
	private String releaseTitle;
	private Medium medium;
	private boolean manualCoverDialogCorrect;
	private int currentFinishedTrack = 0;
	
	@FXML private Button buttonMusicbrainz;
	@FXML private Button mp3Button;
	@FXML private Button settingsButton;
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
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		buttonMusicbrainz.setOnAction(this::loadMusicBrainzInfos);
		mp3Button.setOnAction(this::createMp3s);
		
		pathTextField.setText(config.rootPath);
		pathTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			config.rootPath = pathTextField.getText();
		});
		pathTextField.setOnAction((e) -> {
		});
		
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
	
	private void loadMusicBrainzInfos(ActionEvent event) {
		Task<List<Mp3Track>> task = new Task<List<Mp3Track>>() {
			@Override
			protected List<Mp3Track> call() throws Exception {
//				List<Release> releases = musicbrainzService.searchReleasesByTitle(releaseTitle);
//				String relText = releases.stream().map(x -> x.title).collect(Collectors.joining());
//				System.out.println(relText);
				
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
				medium = promptForMedium(release);
				
				String cdArtist = release.artistCredit.get(0).name;
				String cdTitle = release.title;
				cdArtist = FATCharRemover.removeUnallowedChars(cdArtist);
				cdTitle = FATCharRemover.removeUnallowedChars(cdTitle);
				
				lookupCoverArtForRelease(release);
				
				List<Mp3Track> mp3Tracks = mp3TrackMapper.mapToMp3Tracks(release, releaseDate, medium);
				
				return mp3Tracks;
			}
		};
		
		BiConsumer<WorkerStateEvent, Service<List<Mp3Track>>> onSucceededCallback = (event1, service) -> {
			List<Mp3Track> result = service.getValue();
			mapper.map(result);
		};
		
		Service<List<Mp3Track>> service = FXUtils.createService(task, onSucceededCallback);
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
	
	
	
	private void createMp3s(ActionEvent event) {
		
		Task<Object> task = new Task<Object>() {
			@Override
			protected Object call() throws Exception {
				String rootPath = config.rootPath;
				String cdArtist = selectedRelease.artistCredit.get(0).name;
				String cdTitle = selectedRelease.title;
				cdArtist = FATCharRemover.removeUnallowedChars(cdArtist);
				cdTitle = FATCharRemover.removeUnallowedChars(cdTitle);
				
				String mp3RootAlbumPath = rootPath + "/" + "mp3" + "/" + cdArtist + "/" + cdTitle;
				String mp3CdPath = mp3RootAlbumPath;
				String wavCdPath = rootPath + "/" + "wav" + "/" + cdArtist + "/" + cdTitle;
				if(selectedRelease.multiCDRelease) {
					String cdPathAddon = "/CD" + medium.position;
					mp3CdPath += cdPathAddon;
					wavCdPath += cdPathAddon;
				}
				String imagePath = mp3RootAlbumPath + "/" + "images";
				javaJuicerService.createPathWithParents(mp3CdPath);
				
				Mp3TrackMapper mp3TrackMapper = new Mp3TrackMapper();
				String releaseDate = selectedYearRelease.date;
				List<Mp3Track> mp3Tracks = mp3TrackMapper.mapToMp3Tracks(selectedRelease, releaseDate, medium);
				
				CoverArtArchiveDownloader coverArtDownloader = new CoverArtArchiveDownloader();
				boolean frontCoverAvailable = coverArtDownloader.downloadImages(selectedRelease, imagePath);
				frontCoverAvailable = promptForManualFrontCoverProvision(frontCoverAvailable, imagePath);
				javaJuicerService.addFrontCoverPathTo(mp3Tracks, frontCoverAvailable, imagePath, coverArtDownloader);
				
				javaJuicerService.createPathWithParents(mp3CdPath);
				javaJuicerService.createPathWithParents(wavCdPath);
				
				cdService.ripWavFromStdCdromTo(wavCdPath);
				
				findPregapTrack(mp3Tracks, wavCdPath);
				
				currentFinishedTrack = 0;
				Runnable calculateProgressBar = () -> {
					currentFinishedTrack++;
					updateProgress(currentFinishedTrack, mp3Tracks.size());
				};
				javaJuicerService.createMp3OfEachWav(wavCdPath, mp3CdPath, mp3Tracks, calculateProgressBar);
				
				return null;
			}
		};
		
		Service<Object> service = FXUtils.createService(task, (e,s) -> {});
		service.start();
		
	}
	
	private boolean promptForManualFrontCoverProvision(boolean frontCoverAvailable, String imagePath) {
		if(frontCoverAvailable) return frontCoverAvailable;
		
		FXUtils.runAndWait(() -> {
			String text = "Would you like to manually provide a full size front cover to " + imagePath + "front.jpg ?\n"
					+ "Then copy it to the given path and type 'y' afterwards (y/n)";
			YesNoDialog dialog = new YesNoDialog(text);
			manualCoverDialogCorrect = dialog.showAndWait();
		});
		
		if(!manualCoverDialogCorrect) return false;
		
		String fullPath = imagePath + "front.jpg";
		File file = new File(fullPath);
		if(file.exists()) return true;
		
		FXUtils.runAndWait(() -> {
			String text = "The front cover at " + fullPath + " could not be found.\n"
					+ "Would you like to correct? (y/n)";
			YesNoDialog dialog = new YesNoDialog(text);
			manualCoverDialogCorrect = dialog.showAndWait();
		});
		
		
		if(!manualCoverDialogCorrect) return false;
		
		return promptForManualFrontCoverProvision(frontCoverAvailable, imagePath);
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

	
	private String promptForReleaseYear(String releaseTitle, String text) {
		//TODO Maybe there is another way to get the data. Musicbrainz has the so called Release Groups.
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

	private Release promptForRelease(List<Release> releases, String string) {
		FXUtils.runAndWait(()->{
			if(releases.size() == 1) {
				selectedRelease = releases.get(0);
				return;
			}
			
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
	
	private String promptForReleaseTitle() {
		FXUtils.runAndWait(()->{
			TextInputDialog dialog = new TextInputDialog("Please enter the release title.");
			releaseTitle = dialog.showAndWait();
			System.out.println(releaseTitle);
		});
		return releaseTitle;
	}
	
	private void lookupCoverArtForRelease(Release release) {
		FXUtils.runAndWait(() -> {
			vboxImages.getChildren().clear();
		});
		
		List<Image> images = coverArtService.lookupCoverArtByMbid(release.id);
		images.stream().forEach(e -> {
			String url = e.thumbnails.get("small");
			javafx.scene.image.Image image = new javafx.scene.image.Image(url);
			ImageView imageView = new ImageView(image);
			imageView.getStyleClass().add("vboxImage");
			FXUtils.runAndWait(() -> {
				vboxImages.getChildren().add(imageView);
			});
		});
	}
	
	private void findPregapTrack(List<Mp3Track> mp3Tracks, String wavPath) {
		List<File> files = javaJuicerService.listFilesOfFolder(wavPath);
		Collections.sort(files);
		
    	boolean moreFilesThenTracks = files.size() > mp3Tracks.size();
    	if(moreFilesThenTracks) {
    		boolean oneMoreFileThenTracks = files.size() == mp3Tracks.size() + 1;
    		if(oneMoreFileThenTracks) {
    			boolean yes = promptForAudiblePregapTrack();
    			if(yes) {
    				showAddPregapToMusicbrainzInfoDialog();
    			}
    			else {
    				File file = files.get(0);
    				String filename = file.getName();
    				yes = askToDeleteInaudiblePregapTrack(filename);
    				if(yes) {
    					file.delete();
    				}
    				else {
    					showCurrentlyNotSupportedInfoDialog();
    				}
    			}
    		}
    		else {
    			showMoreThenOnePregapTrackInfoDialog();
    		}
    	}
	}

	private void showMoreThenOnePregapTrackInfoDialog() {
		FXUtils.runAndWait(() -> {
			String text = "There are more tracks on the CD than are listed on musicbrainz.org.\r\n"
					+ "Probably the information on musicbrainz.org is not correct. If this is the case, please\r\n"
					+ "correct it."
					+ "Otherwise the CD contains multiple hidden tracks. Which is currently not supported by this application.";
			InfoAlert alert = new InfoAlert(text);
			alert.showAndWait();
			System.exit(0);
		});
	}

	private void showCurrentlyNotSupportedInfoDialog() {
		FXUtils.runAndWait(() -> {
			String text = "This is currently not supported by this application.";
			InfoAlert alert = new InfoAlert(text);
			alert.showAndWait();
			System.exit(0);
		});
	}

	
	private boolean inaudiblePregapTrackAvailable;
	private boolean askToDeleteInaudiblePregapTrack(String filename) {
		FXUtils.runAndWait(() -> {
			String text = "If " + filename + " is the pregap track it will be deleted and the application tries to continue.\n"
					+ "Is " + filename + " the inaudible pregap track?";
			YesNoDialog dialog = new YesNoDialog(text);
			inaudiblePregapTrackAvailable = dialog.showAndWait();
		});
		
		return inaudiblePregapTrackAvailable;
	}

	private void showAddPregapToMusicbrainzInfoDialog() {
		FXUtils.runAndWait(() -> {
			String text = "Please add the track to the selected release on musicbrainz.org as track 00 and start again.";
			InfoAlert alert = new InfoAlert(text);
			alert.showAndWait();
		});
	}

	private boolean audiblePregapTrackAvailable;
	private boolean promptForAudiblePregapTrack() {
		FXUtils.runAndWait(() -> {
			String text = "A track was found that is not listed on musicbrainz.org.\r\n”"
					+ "Most propably this is a inaudible pregap track that was used in the past to calibrate\r\n"
					+ "old CD Players.\r\n"
					+ "But it could be a hidden track that is audible but not listed on the cover.\r\n"
					+ "Please listen to the WAV files and decide.\r\n\r\n";
			YesNoDialog dialog = new YesNoDialog(text);
			audiblePregapTrackAvailable = dialog.showAndWait();
		});
		
		return audiblePregapTrackAvailable;
	}
	
}
