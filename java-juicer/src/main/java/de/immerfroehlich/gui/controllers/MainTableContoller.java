package de.immerfroehlich.gui.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

import de.immerfroehlich.coverartarchive.CoverArtArchiveDownloader;
import de.immerfroehlich.coverartarchive.CoverArtService;
import de.immerfroehlich.coverartarchive.model.Image;
import de.immerfroehlich.gui.FXUtils;
import de.immerfroehlich.gui.InfoAlert;
import de.immerfroehlich.gui.TextInputDialog;
import de.immerfroehlich.gui.YesNoDialog;
import de.immerfroehlich.gui.controls.MasterDetailProgressBarDialog;
import de.immerfroehlich.javajuicer.mappers.Mp3TrackMapper;
import de.immerfroehlich.javajuicer.model.Album;
import de.immerfroehlich.javajuicer.model.Configuration;
import de.immerfroehlich.javajuicer.model.Mp3Track;
import de.immerfroehlich.javajuicer.utils.FATCharRemover;
import de.immerfroehlich.musicbrainz.model.Disc;
import de.immerfroehlich.musicbrainz.model.Medium;
import de.immerfroehlich.musicbrainz.model.Release;
import de.immerfroehlich.services.CdParanoiaService;
import de.immerfroehlich.services.DeviceInfoService;
import de.immerfroehlich.services.JavaJuicerService;
import de.immerfroehlich.services.LibDiscIdService;
import de.immerfroehlich.services.MusicBrainzService;
import de.immerfroehlich.services.parser.FileNamingConfigParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
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
	private DeviceInfoService deviceInfoService = new DeviceInfoService();
	private FileNamingConfigParser fileNamingService = new FileNamingConfigParser();
	
	private Release selectedYearRelease;
	private Release selectedRelease;
	private String releaseTitle;
	private Medium medium;
	private boolean manualCoverDialogCorrect;
	
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
	private MasterDetailProgressBarDialog progressBarDialog;
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		buttonMusicbrainz.setOnAction(this::loadMusicBrainzInfos);
		mp3Button.setOnAction(this::createMp3s);
		settingsButton.setOnAction(this::openSettingsDialog);
		
		pathTextField.textProperty().bind(Configuration.rootPath);
		pathTextField.setDisable(true);
		
		List<String> devices = deviceInfoService.getDeviceList();
		driveChoiceBox.setItems( FXCollections.observableArrayList(devices) );
		
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
		
		this.progressBarDialog = new MasterDetailProgressBarDialog();
	}
	
	private void openSettingsDialog(ActionEvent event) {
		FXUtils.runAndWait(() -> {
			URL fxmlUrl = getClass().getResource("settingsDialog.fxml");
			FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
			SettingsDialogController settingsDialogController = new SettingsDialogController();
			fxmlLoader.setController(settingsDialogController);
			try {
				Parent settingsDialogView = fxmlLoader.load();
				settingsDialogController.initView(settingsDialogView);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
	
	private void loadMusicBrainzInfos(ActionEvent event) {
		Task<List<Mp3Track>> task = new Task<List<Mp3Track>>() {
			@Override
			protected List<Mp3Track> call() throws Exception {
//				List<Release> releases = musicbrainzService.searchReleasesByTitle(releaseTitle);
//				String relText = releases.stream().map(x -> x.title).collect(Collectors.joining());
//				System.out.println(relText);
				
				String discid = libDiscIdService.calculateDiscIdByDevicePath(Configuration.drivePath.get());
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
				
				selectedRelease = promptForRelease(releases, "Please select the right release");
				selectedRelease = musicbrainzService.reloadRelease(selectedRelease);
				//TODO Das erste Erscheinungsjahr lässt sich so nicht zuverlässig ermitteln! Wird bei Musicbrainz aber je Album angegeben.
				//Ggf. das erste Erscheinungsjahr je Track ermitteln, z.B. bei Compilations
				String releaseDate = promptForReleaseYear(selectedRelease.title, "Please select the first release date");
				medium = promptForMedium(selectedRelease);
				
				String cdArtist = selectedRelease.artistCredit.get(0).name;
				String cdTitle = selectedRelease.title;
				cdArtist = FATCharRemover.removeUnallowedChars(cdArtist);
				cdTitle = FATCharRemover.removeUnallowedChars(cdTitle);
				
				lookupCoverArtForRelease(selectedRelease);
				
				List<Mp3Track> mp3Tracks = mp3TrackMapper.mapToMp3Tracks(selectedRelease, releaseDate, medium);
				
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
				FXUtils.runAndWait(() -> {
					progressBarDialog.setMasterText("Downloading Images");
					progressBarDialog.setDetailText("Downloading Images");
					progressBarDialog.setMasterTaskNumber(3);
					progressBarDialog.setDetailTaskNumber(1);
					progressBarDialog.init();
				});
				
				String rootPath = Configuration.rootPath.get();
				String cdArtist = selectedRelease.artistCredit.get(0).name;
				String cdTitle = selectedRelease.title;
				cdArtist = FATCharRemover.removeUnallowedChars(cdArtist);
				cdTitle = FATCharRemover.removeUnallowedChars(cdTitle);
				
				Album cdReleaseInfo = new Album();
				cdReleaseInfo.artist = cdArtist;
				cdReleaseInfo.title = cdTitle;
				cdReleaseInfo.multiCdRelease = selectedRelease.multiCDRelease;
				if(selectedRelease.multiCDRelease) {
					cdReleaseInfo.cdNumber = medium.position;
				}
				
				String directories = fileNamingService.parse(Configuration.naming.get(), cdReleaseInfo);
				String mp3CdPath = rootPath + "/" + "mp3" + directories;
				String wavCdPath = rootPath + "/" + "wav" + directories;
				String imagePath = mp3CdPath + "/" + "images";
				javaJuicerService.createPathWithParents(imagePath);
				javaJuicerService.createPathWithParents(mp3CdPath);
				javaJuicerService.createPathWithParents(wavCdPath);
				
				Mp3TrackMapper mp3TrackMapper = new Mp3TrackMapper();
				String releaseDate = selectedYearRelease.date;
				List<Mp3Track> mp3Tracks = mp3TrackMapper.mapToMp3Tracks(selectedRelease, releaseDate, medium);
				
				CoverArtArchiveDownloader coverArtDownloader = new CoverArtArchiveDownloader();
				boolean frontCoverAvailable = coverArtDownloader.downloadImages(selectedRelease, imagePath);
				frontCoverAvailable = promptForManualFrontCoverProvision(frontCoverAvailable, imagePath);
				javaJuicerService.addFrontCoverPathTo(mp3Tracks, frontCoverAvailable, imagePath, coverArtDownloader);
				
				
				FXUtils.runAndWait(() -> {
					progressBarDialog.masterTaskFinished();
					progressBarDialog.setMasterText("Extracting CD audio");
					progressBarDialog.setDetailText("Extracting CD audio");
					progressBarDialog.setDetailTaskNumber(1);
					progressBarDialog.init();
				});
				
				cdService.ripWavFromStdCdromTo(wavCdPath);
				
				FXUtils.runAndWait(() -> {
					progressBarDialog.masterTaskFinished();
					progressBarDialog.setMasterText("Extracting CD audio");
					progressBarDialog.setDetailText("Extracting CD audio");
					progressBarDialog.setDetailTaskNumber(1);
					progressBarDialog.init();
				});				
				
				findPregapTrack(mp3Tracks, wavCdPath);

				FXUtils.runAndWait(() -> {
					progressBarDialog.masterTaskFinished();
					progressBarDialog.setMasterText("Creating MP3s");
					progressBarDialog.setDetailText("Converting Track to MP3");
					progressBarDialog.setDetailTaskNumber(mp3Tracks.size());
					progressBarDialog.init();
				});				
				
				Runnable calculateProgressBar = () -> {
		    		FXUtils.runAndWait(() -> {
		    			progressBarDialog.detailTaskFinished();
		    		});
				};
				javaJuicerService.createMp3OfEachWav(wavCdPath, mp3CdPath, mp3Tracks, fileNamingService, calculateProgressBar);
				
				return null;
			}
		};
		
		Service<Object> service = FXUtils.createService(task, (e,s) -> {});
		service.start();
		
		progressBarDialog.show();		
	}
	
	private boolean promptForManualFrontCoverProvision(boolean frontCoverAvailable, String imagePath) {
		if(frontCoverAvailable) return frontCoverAvailable;
		
		FXUtils.runAndWait(() -> {
			String question = "Manually provide front cover?";
			String text = "Would you like to manually provide a full size front cover to " + imagePath + "front.jpg ?\n"
					+ "Then copy it to the given path and type 'y' afterwards (y/n)";
			YesNoDialog dialog = new YesNoDialog(text, question);
			manualCoverDialogCorrect = dialog.showAndWait();
		});
		
		if(!manualCoverDialogCorrect) return false;
		
		String fullPath = imagePath + "front.jpg";
		File file = new File(fullPath);
		if(file.exists()) return true;
		
		FXUtils.runAndWait(() -> {
			String question = "Would you like to correct? (y/n)";
			String text = "The front cover at " + fullPath + " could not be found.\n";
			YesNoDialog dialog = new YesNoDialog(text, question);
			manualCoverDialogCorrect = dialog.showAndWait();
		});
		
		
		if(!manualCoverDialogCorrect) return false;
		
		return promptForManualFrontCoverProvision(frontCoverAvailable, imagePath);
    }
	
	
	private Medium promptForMedium(Release release) {
		if(release.media.size() == 1) {
    		return release.media.get(0);
    	}
    	
    	if(release.media.size() > 1) {
    		release.multiCDRelease = true;
    	}
    	
    	FXUtils.runAndWait(() -> {
			URL fxmlUrl = getClass().getResource("releaseSelectionDialog.fxml");
			FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
			ReleaseSelectionDialogController<Medium> dialogController = new ReleaseSelectionDialogController<>(release.media, "format", "position", "trackCount", "Medium:", "Position:", "Track count:");
			dialogController.setRequest("Select first release date");
			dialogController.setText("Select the year where the songs where first released.");
			
			fxmlLoader.setController(dialogController);
			try {
				fxmlLoader.load();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			
			medium = dialogController.showAndWait();
			selectedYearRelease = release;
		});
    	
		return medium;
	}

	
	private String promptForReleaseYear(String releaseTitle, String text) {
		//TODO Maybe there is another way to get the data. Musicbrainz has the so called Release Groups.
		List<Release> releases = musicbrainzService.searchReleasesByTitle(releaseTitle);
		
		FXUtils.runAndWait(() -> {
			URL fxmlUrl = getClass().getResource("releaseSelectionDialog.fxml");
			FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
			ReleaseSelectionDialogController<Release> dialogController = new ReleaseSelectionDialogController<>(releases, "artistCredit.name", "date", "media.format", "Artist:", "Date:", "Medium:");
			dialogController.setRequest("Select first release date");
			dialogController.setText("Select the year where the songs where first released.");
			
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

	private Release promptForRelease(List<Release> releases, String request) {
		FXUtils.runAndWait(()->{
			if(releases.size() == 1) {
				selectedRelease = releases.get(0);
				return;
			}
			
			URL fxmlUrl = getClass().getResource("releaseSelectionDialog.fxml");
			FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
			ReleaseSelectionDialogController<Release> dialogController = new ReleaseSelectionDialogController<>(releases, "title", "date", "barcode", "Title:", "Date: ", "Barcode: ");
			String text = "More then one release was found that has the same track list as your CD.\n"
					+ "Please select the right release.\n"
					+ "Most often the easiest way to do this is to compare the barcode numbers.\n"
					+ "This is neccessary because the metadata and the artwork can be different.";
			dialogController.setRequest(request);
			dialogController.setText(text);
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
			String question = "Is " + filename + " the inaudible pregap track?";
			String text = "If " + filename + " is the pregap track it will be deleted and the application tries to continue.";
			YesNoDialog dialog = new YesNoDialog(text, question);
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
			String question = "Is the hidden (pregap) track audible?";
			String text = "A track was found that is not listed on musicbrainz.org.\r\n”"
					+ "Most propably this is an inaudible pregap track that was used in the past to calibrate\r\n"
					+ "old CD Players.\r\n"
					+ "But it could be a hidden track that is audible but not listed on the cover.\r\n"
					+ "Please listen to the WAV files and decide.\r\n\r\n";
			YesNoDialog dialog = new YesNoDialog(text, question);
			audiblePregapTrackAvailable = dialog.showAndWait();
		});
		
		return audiblePregapTrackAvailable;
	}
	
}
