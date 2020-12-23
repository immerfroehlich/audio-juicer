package de.immerfroehlich.services;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import de.immerfroehlich.musicbrainz.MusicbrainzWs2Service;
import de.immerfroehlich.musicbrainz.model.Disc;
import de.immerfroehlich.musicbrainz.model.Release;

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
