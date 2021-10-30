package de.immerfroehlich.services;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.immerfroehlich.javajuicer.model.ReleaseType;
import de.immerfroehlich.musicbrainz.MusicbrainzWs2Service;
import de.immerfroehlich.musicbrainz.model.Disc;
import de.immerfroehlich.musicbrainz.model.Medium;
import de.immerfroehlich.musicbrainz.model.Release;
import de.immerfroehlich.musicbrainz.model.ReleaseGroup;

public class MusicBrainzService {
	
	public Optional<Disc> lookupDiscById(String discid) {
		MusicbrainzWs2Service service = new MusicbrainzWs2Service();
		Optional<Disc> disc = service.lookupDiscById(discid);
		return disc;
	}
	
	public List<Release> searchReleasesByTitle(String title) {
		MusicbrainzWs2Service service = new MusicbrainzWs2Service();
		return service.searchReleasesByTitle(title);
	}
	
	public boolean findAndwriteReleaseDateToEachMediumTrack(Release release, Medium medium, ReleaseType type) {
		MusicbrainzWs2Service service = new MusicbrainzWs2Service();
		
		//TODO: the year is different for each track and can be retrieved dependend of the ReleaseType. Implement the other release types.
		
		if(type == ReleaseType.SingleArtistAlbumOrSingle) {
			//Try to get the first release year from the release group first.
			// ! This just works for single artist albums !
			String artists = release.artistCredit.stream().map(e -> e.name).collect(Collectors.joining(" "));
			List<ReleaseGroup> releaseGroups = service.searchReleaseGroup(artists, release.title);
			releaseGroups = releaseGroups.stream().filter(e -> e.primaryType.equals("Album")).collect(Collectors.toList());
			releaseGroups = releaseGroups.stream().filter(e -> e.title.equalsIgnoreCase(release.title)).collect(Collectors.toList());
			if(releaseGroups.size() > 0 && releaseGroups.size() < 2) {
				String firstReleaseDate = releaseGroups.get(0).firstReleaseDate;
				medium.tracks.forEach(e -> e.firstReleaseDate = firstReleaseDate);
				return true;
			}
		}
		
		//TODO Ggf. das erste Erscheinungsjahr je Track ermitteln, z.B. bei Compilations
		
		return false;
	}
	
	public Release reloadRelease(Release release) {
    	MusicbrainzWs2Service service = new MusicbrainzWs2Service();
		Optional<Release> disc = service.lookupReleaseById(release.id);
		return disc.get();
	}

	public String createTocAddLink(String discid, String toc) {
		String trackNumber = extractTrackNumber(toc);
		String urlEncodedToc = URLEncoder.encode(toc, StandardCharsets.UTF_8); 
		String url = MusicbrainzWs2Service.MUSICBRAINZ_BASE_URL + "/cdtoc/attach?id=" + discid + "&tracks=" + trackNumber + "&toc=" + urlEncodedToc;
		return url;
	}

	private String extractTrackNumber(String toc) {
		String[] splitted = toc.split(" ");
		return splitted[1];
	}

}
