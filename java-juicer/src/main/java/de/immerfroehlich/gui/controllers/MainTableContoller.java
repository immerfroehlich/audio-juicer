package de.immerfroehlich.gui.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

import de.immerfroehlich.coverartarchive.CoverArtArchiveDownloader;
import de.immerfroehlich.coverartarchive.CoverArtService;
import de.immerfroehlich.coverartarchive.model.Image;
import de.immerfroehlich.gui.FXUtils;
import de.immerfroehlich.gui.controls.CopyableTextInfoDialog;
import de.immerfroehlich.gui.controls.InfoAlert;
import de.immerfroehlich.gui.controls.MasterDetailProgressBarDialog;
import de.immerfroehlich.gui.controls.YesNoDialog;
import de.immerfroehlich.gui.modules.settings.NamingSchemeExampleUpdater;
import de.immerfroehlich.gui.modules.settings.ObservableNamingScheme;
import de.immerfroehlich.gui.modules.settings.ObservableNamingSchemeStringConverter;
import de.immerfroehlich.gui.modules.settings.SettingsDialogController;
import de.immerfroehlich.javajuicer.mappers.MapperSettings;
import de.immerfroehlich.javajuicer.mappers.TrackInfoMapper;
import de.immerfroehlich.javajuicer.model.AlbumInfo;
import de.immerfroehlich.javajuicer.model.Configuration;
import de.immerfroehlich.javajuicer.model.ReleaseType;
import de.immerfroehlich.javajuicer.model.TrackInfo;
import de.immerfroehlich.javajuicer.utils.FATCharRemover;
import de.immerfroehlich.musicbrainz.model.Disc;
import de.immerfroehlich.musicbrainz.model.Medium;
import de.immerfroehlich.musicbrainz.model.Release;
import de.immerfroehlich.services.CdParanoiaService;
import de.immerfroehlich.services.ChecksumService;
import de.immerfroehlich.services.DeviceInfoService;
import de.immerfroehlich.services.JavaJuicerService;
import de.immerfroehlich.services.LibDiscIdService;
import de.immerfroehlich.services.MusicBrainzService;
import de.immerfroehlich.services.parser.FileNamingConfigParser;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.CheckBox;
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
	private TrackInfoMapper mp3TrackMapper = new TrackInfoMapper();
	private DeviceInfoService deviceInfoService = new DeviceInfoService();
	private FileNamingConfigParser fileNamingService = new FileNamingConfigParser();
	private ChecksumService checksumService = new ChecksumService();
	
	private Release selectedYearRelease;
	private Release selectedRelease;
	private Medium medium;
	private boolean manualCoverDialogCorrect;
	private ObservableNamingScheme selectedNamingScheme;
	private List<TrackInfo> loadedTrackInfos;
	private String selectedDrive;
	private MapperSettings mapperSettings = new MapperSettings();
	
	@FXML private Button buttonMusicbrainz;
	@FXML private Button mp3Button;
	@FXML private Button settingsButton;
	@FXML private TableView<MainTableModel> tableView;
	@FXML private TableColumn<MainTableModel, String> columnTrack;
	@FXML private TableColumn<MainTableModel, Boolean> columnPregap;
	@FXML private TableColumn<MainTableModel, String> columnArtist;
	@FXML private TableColumn<MainTableModel, String> columnTitle;
	@FXML private TableColumn<MainTableModel, String> columnYear;
	@FXML private TableColumn<MainTableModel, String> columnAlbumTitle;
	@FXML private VBox vboxImages;
	@FXML private Label driveLabel;
	@FXML private ChoiceBox<String> driveChoiceBox;
	@FXML private ChoiceBox<ObservableNamingScheme> namingSchemeChoiceBox;
	@FXML private TextField namingExampleTextField;
	@FXML private Label pathLabel;
	@FXML private TextField pathTextField;
	@FXML private CheckBox includeAllArtistsCheckBox;
	
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
		driveChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				selectedDrive = newValue;
			}
		});
		driveChoiceBox.getSelectionModel().selectFirst();
		updateNamingSchemeChoiceBox();
		namingSchemeChoiceBox.getSelectionModel().selectedIndexProperty().addListener(this::setNamingScheme);
		namingSchemeChoiceBox.setConverter(new ObservableNamingSchemeStringConverter());
		namingSchemeChoiceBox.getSelectionModel().selectFirst();
		
		tableView.setItems(data);
		
		//Workaround: The table is included in the fxml it seems it is not possible
		//to access the fx:ids in the same controller.
		columnTrack = (TableColumn<MainTableModel, String>) tableView.getColumns().get(0);
		columnPregap = (TableColumn<MainTableModel, Boolean>) tableView.getColumns().get(1);
		columnArtist = (TableColumn<MainTableModel, String>) tableView.getColumns().get(2);
		columnTitle = (TableColumn<MainTableModel, String>) tableView.getColumns().get(3);
		columnYear = (TableColumn<MainTableModel, String>) tableView.getColumns().get(4);
		columnAlbumTitle = (TableColumn<MainTableModel, String>) tableView.getColumns().get(5);
		
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
		
		columnYear.setCellValueFactory( (cellDataFeature) -> {
			return cellDataFeature.getValue().year;
		});
		
		columnAlbumTitle.setCellValueFactory( (cellDataFeature) -> {
			return cellDataFeature.getValue().album;
		});
		
		this.progressBarDialog = new MasterDetailProgressBarDialog();
		
		includeAllArtistsCheckBox.selectedProperty().addListener( (obervable, oldValue, newValue) ->  {
			mapperSettings.includeAllArtists = newValue;
		});
	}
	
	private void openSettingsDialog(ActionEvent event) {
		FXUtils.runAndWait(() -> {
			URL fxmlUrl = getClass().getResource("settingsDialog.fxml");
			FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
			SettingsDialogController settingsDialogController = new SettingsDialogController();
			fxmlLoader.setController(settingsDialogController);
			try {
				Parent settingsDialogView = fxmlLoader.load();
				settingsDialogController.initView(settingsDialogView, this::updateNamingSchemeChoiceBox);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
	
	private void setNamingScheme(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		selectedNamingScheme = namingSchemeChoiceBox.getItems().get(newValue.intValue());
		NamingSchemeExampleUpdater updater = new NamingSchemeExampleUpdater();
		if(selectedRelease == null) {
			updater.updateExampleTextField(selectedNamingScheme.scheme.getValue(), namingExampleTextField);
			return;
		}
		
		AlbumInfo album = createAlbumInfo();
		TrackInfo track = loadedTrackInfos.get(1);
		updater.updateExampleTextField(selectedNamingScheme.scheme.getValue(), namingExampleTextField, album, track);
	}
	
	private void updateNamingSchemeChoiceBox() {
		namingSchemeChoiceBox.getItems().setAll(Configuration.namingSchemes);
	}
	
	private void loadMusicBrainzInfos(ActionEvent event) {
		Task<List<TrackInfo>> task = new Task<List<TrackInfo>>() {
			@Override
			protected List<TrackInfo> call() throws Exception {
				Optional<String> discidOpt = libDiscIdService.calculateDiscIdByDevicePath(selectedDrive);
				if(discidOpt.isEmpty()) {
					showNoDiscInsertedDialog();
					return new ArrayList<>();
				}
				String discid = discidOpt.get();
				System.out.println("Calculated DiscId is: " + discid);
				Optional<Disc> discOpt = musicbrainzService.lookupDiscById(discid);
				List<Release> releases;
				if(discOpt.isEmpty() || discOpt.get().releases.size() == 0) { //Why is this even possible to have a disc without releases? Might be a data problem. Look at test/ressources/cd_without_release.json
					String toc = libDiscIdService.getDiscToc(selectedDrive).get();
					String tocAddLink = musicbrainzService.createTocAddLink(discid, toc);
					showTocDialog(tocAddLink);
					//releases = musicbrainzService.searchReleasesByTitle(releaseTitle);
					return new ArrayList<>();
				}
				
				releases = discOpt.get().releases;
				
				selectedRelease = promptForRelease(releases, "Please select the right release");
				selectedRelease = musicbrainzService.reloadRelease(selectedRelease);
				medium = promptForMedium(selectedRelease);
				promptForReleaseDate(selectedRelease, medium);
				
				String cdArtist = selectedRelease.artistCredit.get(0).name;
				String cdTitle = selectedRelease.title;
				cdArtist = FATCharRemover.removeUnallowedChars(cdArtist);
				cdTitle = FATCharRemover.removeUnallowedChars(cdTitle);
				
				lookupCoverArtForRelease(selectedRelease);
				
				List<TrackInfo> mp3Tracks = mp3TrackMapper.mapToTrackInfos(selectedRelease, medium, mapperSettings);
				
				return mp3Tracks;
			}
		};
		
		BiConsumer<WorkerStateEvent, Service<List<TrackInfo>>> onSucceededCallback = (event1, service) -> {
			this.loadedTrackInfos = service.getValue();
			mapper.map(loadedTrackInfos);
		};
		
		Service<List<TrackInfo>> service = FXUtils.createService(task, onSucceededCallback);
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
	
	protected void showNoDiscInsertedDialog() {
		FXUtils.runAndWait(() -> {
			InfoAlert alert = new InfoAlert("No CD is inserted into the selected drive. Please insert CD.");
			alert.showAndWait();
		});
	}

	private void createMp3s(ActionEvent event) {
		
		progressBarDialog = new MasterDetailProgressBarDialog();
		
		Task<Object> task = new Task<Object>() {
			@Override
			protected Object call() throws Exception {
				FXUtils.runAndWait(() -> {
					progressBarDialog.setMasterText("Downloading Images");
					progressBarDialog.setDetailText("Downloading Images");
					progressBarDialog.setMasterTaskNumber(4);
					progressBarDialog.setDetailTaskNumber(1);
					progressBarDialog.update();
				});
				
				String rootPath = Configuration.rootPath.get();
				AlbumInfo cdReleaseInfo = createAlbumInfo();
				
				String directories = fileNamingService.parse(selectedNamingScheme.scheme.getValue(), cdReleaseInfo);
				String mp3CdPath = rootPath + "/" + "mp3" + directories;
				String wavCdPath = rootPath + "/" + "wav" + directories;
				String imagePath = mp3CdPath + "/" + "images";
				javaJuicerService.createPathWithParents(imagePath);
				javaJuicerService.createPathWithParents(mp3CdPath);
				javaJuicerService.createPathWithParents(wavCdPath);
				
				TrackInfoMapper mp3TrackMapper = new TrackInfoMapper();
				List<TrackInfo> mp3Tracks = mp3TrackMapper.mapToTrackInfos(selectedRelease, medium, mapperSettings);
				
				CoverArtArchiveDownloader coverArtDownloader = new CoverArtArchiveDownloader();
				final String imagePathWithSeparator = imagePath + File.separatorChar;
				boolean frontCoverAvailable = coverArtDownloader.downloadImages(selectedRelease, imagePathWithSeparator);
				frontCoverAvailable = promptForManualFrontCoverProvision(frontCoverAvailable, imagePath);
				javaJuicerService.addFrontCoverPathTo(mp3Tracks, frontCoverAvailable, imagePath, coverArtDownloader);
				
				
				FXUtils.runAndWait(() -> {
					progressBarDialog.masterTaskFinished();
					progressBarDialog.setMasterText("Extracting CD audio");
					progressBarDialog.setDetailText("Extracting CD audio");
					progressBarDialog.setDetailTaskNumber(1);
					progressBarDialog.update();
				});
				
				cdService.ripWavFromStdCdromTo(wavCdPath);
				
				FXUtils.runAndWait(() -> {
					progressBarDialog.masterTaskFinished();
					progressBarDialog.setMasterText("Extracting CD audio");
					progressBarDialog.setDetailText("Extracting CD audio");
					progressBarDialog.setDetailTaskNumber(1);
					progressBarDialog.update();
				});				
				
				findPregapTrack(mp3Tracks, wavCdPath);

				FXUtils.runAndWait(() -> {
					progressBarDialog.masterTaskFinished();
					progressBarDialog.setMasterText("Creating MP3s");
					progressBarDialog.setDetailText("Converting Track to MP3");
					progressBarDialog.setDetailTaskNumber(mp3Tracks.size());
					progressBarDialog.update();
				});				
				
				Runnable calculateProgressBar = () -> {
		    		FXUtils.runAndWait(() -> {
		    			progressBarDialog.detailTaskFinished();
		    		});
				};
				
				javaJuicerService.createMp3OfEachWav(wavCdPath, mp3CdPath, mp3Tracks, fileNamingService, calculateProgressBar);
				
				FXUtils.runAndWait(() -> {
					progressBarDialog.masterTaskFinished();
					progressBarDialog.setMasterText("Creating checksum for each MP3");
					progressBarDialog.setDetailText("Creating checksum ...");
					progressBarDialog.setDetailTaskNumber(1);
					progressBarDialog.update();
				});
				
				checksumService.createChecksumOfAllFilesIn(mp3CdPath);
				
				FXUtils.runAndWait(() -> {
					progressBarDialog.masterTaskFinished();
					progressBarDialog.detailTaskFinished();
					progressBarDialog.update();
				});
				
				return null;
			}
		};
		
		Service<Object> service = FXUtils.createService(task, (e,s) -> {});
		service.start();
		
		progressBarDialog.show();		
	}
	
	private boolean promptForManualFrontCoverProvision(boolean frontCoverAvailable, String imagePath) {
		if(frontCoverAvailable) return frontCoverAvailable;
		
		String fullPath = imagePath + File.separator + "front.jpg";
		
		FXUtils.runAndWait(() -> {
			String question = "Manually provide front cover?";
			String text = "Would you like to manually provide a full size front cover to " + fullPath + "?\n"
					+ "Then copy it to the given path and type 'y' afterwards (y/n)";
			YesNoDialog dialog = new YesNoDialog(text, question);
			manualCoverDialogCorrect = dialog.showAndWait();
		});
		
		if(!manualCoverDialogCorrect) return false;
		
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
			dialogController.setRequest("Select the inserted medium");
			dialogController.setText("Please select the medium you have inserted of this multi medium release.");
			
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

	
	private void promptForReleaseDate(Release selectedRelease, Medium medium) {
		
		ReleaseType releaseType = ReleaseType.parse(selectedNamingScheme.name.getValue());
				
		boolean foundReleaseDates = musicbrainzService.findAndwriteReleaseDateToEachMediumTrack(selectedRelease, medium, releaseType);
		if(foundReleaseDates) {
			return;
		}
		
		//If no release date could be found on musicbrainz, do a semi manual process.
		
		List<Release> releases = musicbrainzService.searchReleasesByTitle(selectedRelease.title);
		
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
	
		String releaseDate = selectedYearRelease.date;
		medium.tracks.forEach(e -> e.firstReleaseDate = releaseDate);

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

	private void showTocDialog(final String toc) {
		FXUtils.runAndWait(()->{
			String text = 
					"The CD is not yet listed on musicbrainz.org. \n\n"
					+ "Please add this CD to musicbrainz.org via the given "
					+ "link and enter the tracklist and artist information. \n\n"
					+ "Copy the following link to your browser to add the CD:";
			CopyableTextInfoDialog alert = new CopyableTextInfoDialog(text, toc);
			alert.showAndWait();
		});
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
	
	private void findPregapTrack(List<TrackInfo> mp3Tracks, String wavPath) {
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

	private AlbumInfo createAlbumInfo() {
		String cdArtist = mp3TrackMapper.mapArtistName(selectedRelease.artistCredit, mapperSettings);
		String cdTitle = selectedRelease.title;
		cdArtist = FATCharRemover.removeUnallowedChars(cdArtist);
		cdTitle = FATCharRemover.removeUnallowedChars(cdTitle);
		
		AlbumInfo cdReleaseInfo = new AlbumInfo();
		cdReleaseInfo.artist = cdArtist;
		cdReleaseInfo.title = cdTitle;
		cdReleaseInfo.multiCdRelease = selectedRelease.multiCDRelease;
		if(selectedRelease.multiCDRelease) {
			cdReleaseInfo.cdNumber = medium.position;
		}
		return cdReleaseInfo;
	}
	
}
