import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.academiadecodigo.bootcamp.Prompt;
import org.academiadecodigo.bootcamp.scanners.integer.IntegerInputScanner;
import org.academiadecodigo.bootcamp.scanners.string.StringInputScanner;

import de.immerfroehlich.command.Command;
import de.immerfroehlich.command.CommandExecutor;
import de.immerfroehlich.command.Result;
import de.immerfroehlich.coverartarchive.CoverArtArchiveDownloader;
import de.immerfroehlich.deviceinfo.BlockDevice;
import de.immerfroehlich.deviceinfo.DeviceInfo;
import de.immerfroehlich.discid.DiscIdCalculator;
import de.immerfroehlich.javajuicer.model.Mp3Track;
import de.immerfroehlich.javajuicer.utils.ConsolePrompt;
import de.immerfroehlich.javajuicer.utils.FATCharRemover;
import de.immerfroehlich.musicbrainz.MusicbrainzWs2Service;
import de.immerfroehlich.musicbrainz.model.Disc;
import de.immerfroehlich.musicbrainz.model.Medium;
import de.immerfroehlich.musicbrainz.model.Pregap;
import de.immerfroehlich.musicbrainz.model.Release;
import de.immerfroehlich.musicbrainz.model.Track;
import de.immerfroehlich.prompt.Prompter;

public class App {

    public static void main(String[] args) {
    	
    	String deviceName = promptForDeviceName();
    	
    	String discid = calculateDiscIdByDevicePath(deviceName);
    	System.out.println("Calculated DiscId is: " + discid);
    	Optional<Disc> discOpt = lookupDiscById(discid);
    	List<Release> releases;
    	if(!discOpt.isPresent()) {
    		String releaseTitle = promptForReleaseTitle();
    		releases = searchReleasesByTitle(releaseTitle);
    	}
    	else {
    		releases = discOpt.get().releases;
    	}
    	
    	boolean noReleasesFound = releases.size() == 0; 
    	if(noReleasesFound) {
    		System.out.println("Please login to musicbrainz.org and add your CD/release.");
    		System.out.println("Afterwards you could start this program again.");
    		
    		System.exit(0);
    	}
    	
    	Release release = promptForRelease(releases, "Select release");
    	release = reloadRelease(release);
    	//TODO Das erste Erscheinungsjahr lässt sich so nicht zuverlässig ermitteln! Wird bei Musicbrainz aber je Album angegeben.
    	//Ggf. das erste Erscheinungsjahr je Track ermitteln, z.B. bei Compilations
    	String releaseDate = promptForReleaseYear(releases, "Select first release date");
    	Medium medium = promptForMedium(release);
    	
    	
    	String rootPath = "/home/andreas/Musik/Archiv"; //TODO get the root path
    	String cdArtist = release.artistCredit.get(0).name;
    	String cdTitle = release.title;
    	cdArtist = FATCharRemover.removeUnallowedChars(cdArtist);
    	cdTitle = FATCharRemover.removeUnallowedChars(cdTitle);
    	
    	String mp3RootAlbumPath = rootPath + "/" + "mp3" + "/" + cdArtist + "/" + cdTitle;
    	String mp3CdPath = mp3RootAlbumPath;
    	String wavCdPath = rootPath + "/" + "wav" + "/" + cdArtist + "/" + cdTitle;
    	if(release.multiCDRelease) {
    		String cdPathAddon = "/CD" + medium.position;
    		mp3CdPath += cdPathAddon;
    		wavCdPath += cdPathAddon;
    	}
    	String imagePath = mp3RootAlbumPath + "/" + "images";
    	
    	
    	List<Mp3Track> mp3Tracks = mapToMp3Tracks(release, releaseDate, medium);
    	
    	System.out.println(mp3CdPath);
    	System.out.println(wavCdPath);
    	promptCorrect(mp3Tracks);
    	
    	CoverArtArchiveDownloader coverArtDownloader = new CoverArtArchiveDownloader();
    	boolean frontCoverAvailable = coverArtDownloader.downloadImages(release, imagePath);
    	frontCoverAvailable = promptForManualFrontCoverProvision(frontCoverAvailable, imagePath);
    	addFrontCoverPathTo(mp3Tracks, frontCoverAvailable, imagePath, coverArtDownloader);
    	
    	createPathWithParents(mp3CdPath);
    	createPathWithParents(wavCdPath);
    	
    	ripWavFromStdCdromTo(wavCdPath);
    	
    	findPregapTrack(mp3Tracks, wavCdPath);
    	
    	createMp3OfEachWav(wavCdPath, mp3CdPath, mp3Tracks);
    	
    }

	private static String promptForDeviceName() {
		//TODO String standardDevice = "/dev/sr0";
		String standardDevice = "gibbet nich";
		DeviceInfo devInfo = new DeviceInfo();
		
		List<BlockDevice> devices = devInfo.getRomDevices();
		Optional<BlockDevice> deviceOpt = devices.stream()
			.filter(dev -> dev.name.equals(standardDevice))
			.findFirst();
		
		BlockDevice device;
		device = deviceOpt.orElseGet(() -> {
			
			return Prompter.askSelectFromList(devices, "Please select a device: ", dev -> {
				return dev.name;
			});
		});
		
		return "/dev/" + device.name;
	}

	private static boolean promptForManualFrontCoverProvision(boolean frontCoverAvailable, String imagePath) {
		if(frontCoverAvailable) return frontCoverAvailable;
		
    	System.out.print("Would you like to manually provide a full size front cover to " + imagePath + "front.jpg ?");
    	String question = "Then copy it to the given path and type 'y' afterwards (y/n)";
    	boolean correct = Prompter.askYesNo(question);
    	
    	if(!correct) return false;
    	
    	String fullPath = imagePath + "front.jpg";
    	File file = new File(fullPath);
    	if(file.exists()) return true;
    	
    	System.out.println("The front cover at " + fullPath + " could not be found.");
    	question = "Would you like to correct? (y/n)";
    	
    	correct = Prompter.askYesNo(question);
    	
    	if(!correct) return false;
    	
    	return promptForManualFrontCoverProvision(frontCoverAvailable, imagePath);
    }

	private static void addFrontCoverPathTo(List<Mp3Track> mp3Tracks, boolean frontCoverAvailable, String imagePath, CoverArtArchiveDownloader coverArtDownloader) {
		if(!frontCoverAvailable) return;
		
		String fullImagePath = coverArtDownloader.resizeFrontCoverImage(imagePath);
		if(fullImagePath.isEmpty()) return;
		
		for(Mp3Track mp3Track : mp3Tracks) {
			mp3Track.cover.hasFrontCover = true;
			mp3Track.cover.frontCoverPath = fullImagePath;
		}
	}

	private static void findPregapTrack(List<Mp3Track> mp3Tracks, String wavPath) {
		//TODO: Load pregaptrack info from musicbrainz
    	
		List<File> files = listFilesOfFolder(wavPath);
		Collections.sort(files);
		
    	boolean moreFilesThenTracks = files.size() > mp3Tracks.size();
    	if(moreFilesThenTracks) {
    		boolean oneMoreFileThenTracks = files.size() == mp3Tracks.size() + 1;
    		if(oneMoreFileThenTracks) {
    			System.out.println("A track was found that is not listed on musicbrainz.org.\r\n”"
    					+ "Most propably this is a inaudible pregap track that was used in the past to calibrate\r\n"
    					+ "old CD Players.\r\n"
    					+ "But it could be a hidden track that is audible but not listed on the cover.\r\n"
    					+ "Please listen to the WAV files and decide.\r\n\r\n");
    			boolean yes = Prompter.askYesNo("Is it a hidden audible track?");
    			if(yes) {
    				System.out.println("Please add the track to the selected release on musicbrain.org as track 00 and start again.");
    			}
    			else {
    				File file = files.get(0);
    				String filename = file.getName();
    				System.out.println("If " + filename + " is the pregap track it will be deleted and the application tries to continue.");
    				yes = Prompter.askYesNo("Is " + filename + " the inaudible pregap track?");
    				if(yes) {
    					file.delete();
    				}
    				else {
    					System.out.println("This is currently not supported by this application.");
    					System.exit(0);
    				}
    			}
    		}
    		else {
    			System.out.println("There are more tracks on the CD than are listed on musicbrainz.org.\r\n"
    					+ "Probably the information on musicbrainz.org is not correct. If this is the case, please\r\n"
    					+ "correct it."
    					+ "Otherwise the CD contains multiple hidden tracks. Which is currently not supported by this application.");
    			System.exit(0);
    		}
    	}
	}
    
    private static Release reloadRelease(Release release) {
    	MusicbrainzWs2Service service = new MusicbrainzWs2Service();
		Optional<Release> disc = service.lookupReleaseById(release.id);
		return disc.get();
	}

	private static String promptForReleaseTitle() {
		
    	System.out.println("musicbrainz.org doesn't provide any title information for your CD. Please log in to musicbrainz.org and provide the information.");
    	System.out.println("You can try to enter the release title. Maybe you will find a release with the same track list.");
    	
    	boolean yes = ConsolePrompt.yesNoQuestion("Would you like to try? (y/n)");
    	if(!yes) {
    		System.exit(0);
    	}
    	
    	String releaseTitle = ConsolePrompt.askFreeText("Release Title");
		return releaseTitle;
	}

	private static Medium promptForMedium(Release release) {
    	if(release.media.size() == 1) {
    		return release.media.get(0);
    	}
    	
    	
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

	private static List<Mp3Track> mapToMp3Tracks(Release release, String releaseDate, Medium medium) {
    	List<Mp3Track> tracks = new ArrayList<>();
    	
    	Pregap pregap = medium.pregap;
    	boolean pregapAvailable = pregap != null;
    	if(pregapAvailable) {
    		if(pregap.position != 0) {
    			System.out.println("A pregap audible track other then position 00 is currently not supported by this application.");
    		}
    		System.out.println("=== This CD has a hidden \"first\" track that is most probably not listed on the cover.===");
    		Mp3Track mp3Track = new Mp3Track();
    		
    		boolean noArtistCredit = pregap.artistCredit.size() == 0;
    		if(noArtistCredit) {
    			System.out.println("The pregap does not have an artist-credit. Please correct that for the selected record in musicbrainz.org and try again.");
    			System.exit(0);
    		}
    		mp3Track.isPregap = true;
			mp3Track.artist = pregap.artistCredit.get(0).name;
			mp3Track.releaseYear = release.date;
			mp3Track.firstReleaseYear = releaseDate;
			mp3Track.title = pregap.title;
			tracks.add(mp3Track);
    	}
    	
    	for(Track track : medium.tracks) {
			Mp3Track mp3Track = new Mp3Track();
			mp3Track.artist = release.artistCredit.get(0).name;
			mp3Track.releaseYear = release.date;
			mp3Track.firstReleaseYear = releaseDate;
			mp3Track.title = track.title;
			tracks.add(mp3Track);
		}
		return tracks;
	}
	
	private static Release promptForRelease(List<Release> releases, String text) {
		return Prompter.askSelectFromList(releases, text, (release) -> {
			String entry = "\n"
					+ release.title + "\n" 
					+ release.date + "\n"
					+ release.barcode + "\n"
					+ release.artistCredit.stream().map(x -> x.name).collect(Collectors.joining());
			
			return entry;
		});
	}
	
	private static String promptForReleaseYear(List<Release> releases, String text) {
		String additionalEntry = "Or enter release year/date manually";
		
		Integer number = Prompter.askSelectFromList(releases, additionalEntry, text, (release) -> {
			String entry = "\n"
					+ release.title + "\n" 
					+ release.date + "\n"
					+ release.barcode + "\n"
					+ release.artistCredit.stream().map(x -> x.name).collect(Collectors.joining());
			
			return entry;
		});
		
		String releaseDate;		
		
		boolean manuallySelected = number > (releases.size() -1);
		if(manuallySelected) {
			String question = "Enter release year/date";
			releaseDate = Prompter.askForString(question);
		}
		else {
			releaseDate = releases.get(number).date;
		}
		
		return releaseDate;
	}

	private static String calculateDiscIdByDevicePath(String drivePath) {
    	DiscIdCalculator calculator = new DiscIdCalculator();
		String discid = calculator.calculate(drivePath);
		return discid;
	}
	
	private static Optional<Disc> lookupDiscById(String discid) {
		MusicbrainzWs2Service service = new MusicbrainzWs2Service();
		Optional<Disc> disc = service.lookupDiscById(discid);
		return disc;
	}
	
	private static List<Release> searchReleasesByTitle(String title) {
		MusicbrainzWs2Service service = new MusicbrainzWs2Service();
		return service.searchReleasesByTitle(title);
	}

	public static void createMp3OfEachWav(String wavPath, String targetPath, List<Mp3Track> tracks) {
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

	public static List<File> listFilesOfFolder(String wavPath) {
		File folder = new File(wavPath);
    	List<File> files = Arrays.asList(folder.listFiles());
		return files;
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

    private static String createTrackNumber(int i) {
    	String trackNumber = "";
		if(i < 10) {
			trackNumber = "0";
		}
		trackNumber += i;
		return trackNumber;
	}

	private static void ripWavFromStdCdromTo(String wavPath) {
    	Command command = new Command();
		command.setCommand("cdparanoia");
		command.addParameter("-B");
		command.setBasePath(wavPath);
		
		CommandExecutor executor = new CommandExecutor();
		Result result = executor.execute(command);
		
		if(result.hasErrors()) {
			List<String> errorRows = result.getStdErr();
			//TODO Richtige Fehlerbehandlung.
			for(String error : errorRows) {
				System.out.println(error);
			}
		}
		else {
			List<String> output = result.asStringList();
			for(String line : output) {
				System.out.println(line);
			}
		}
	}

	private static void createPathWithParents(String path) {
		Command command = new Command();
		command.setCommand("mkdir");
		command.addParameter("-p");
		command.addParameter(path);
		
		CommandExecutor executor = new CommandExecutor();
		executor.execute(command);
	}

	private static void promptCorrect(List<Mp3Track> mp3Tracks) {
		
		int i = 0;
		for(Mp3Track track : mp3Tracks) {
			if(!track.isPregap) i++;
			System.out.println(i + " " + track);
		}
		
		Prompt prompt = new Prompt(System.in, System.out);
    	StringInputScanner scanner = new StringInputScanner();
    	
    	System.out.print("Tracks correct (y/n)");
    	String yesNo = prompt.getUserInput(scanner);
    	boolean correct = yesNo.equals("y") ? true : false;
    	
    	if(!correct) {
    		System.exit(0);
    	}
    }
}
