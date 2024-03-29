package de.immerfroehlich.musicbrainz;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.logging.LoggingFeature;

import de.immerfroehlich.musicbrainz.model.Disc;
import de.immerfroehlich.musicbrainz.model.Release;
import de.immerfroehlich.musicbrainz.model.ReleaseGroup;
import de.immerfroehlich.musicbrainz.model.ReleaseGroupList;

public class MusicbrainzWs2Service {
	
	public static final String MUSICBRAINZ_BASE_URL = "https://musicbrainz.org";	
	public static final String BASE_URL = MUSICBRAINZ_BASE_URL + "/ws/2/";
	
	private Client client;

	public MusicbrainzWs2Service() {
		this.client = ClientBuilder.newClient()
				.property(LoggingFeature.LOGGING_FEATURE_VERBOSITY_CLIENT, LoggingFeature.Verbosity.PAYLOAD_ANY)
	            .property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL_CLIENT, "WARNING");
	}
	
	public Optional<Disc> lookupDiscById(String discid) {
		discid = urlEncode(discid);
		String url = BASE_URL + "discid/" + discid + "/";
		WebTarget target = client.target(url)
				.queryParam("fmt", "json")
				.queryParam("inc", "recordings+artist-credits");
		
		try {
			Disc disc = target.request()
					.get(Disc.class);
			
			return Optional.of(disc);
			
		} catch(NotFoundException e) {
			return Optional.empty();
		}
		
	}
	
	public Optional<Release> lookupReleaseById(String releaseid) {
		releaseid = urlEncode(releaseid);
		String url = BASE_URL + "release/" + releaseid + "/";
		WebTarget target = client.target(url)
				.queryParam("fmt", "json")
				.queryParam("inc", "recordings+artist-credits");
		
		try {
			Release release = target.request()
					.get(Release.class);
			
			return Optional.of(release);
			
		} catch(NotFoundException e) {
			return Optional.empty();
		}
		
	}
	
	public List<Release> searchReleasesByTitle(String title) {
		String urlEncTitle = urlEncode(title);
		String url = BASE_URL + "release?query=" + urlEncTitle;
		WebTarget target = client.target(url)
				.queryParam("fmt", "json")
				.queryParam("inc", "recordings+artist-credits");
		
		Optional<Disc> discOpt;
		
		try {
			Disc disc = target.request()
					.get(Disc.class);
			
			discOpt = Optional.of(disc);
			
		} catch(NotFoundException e) {
			discOpt = Optional.empty();
		}
		
		List<Release> releases = new ArrayList<>();
		if(discOpt.isPresent()) {
			Disc disc = discOpt.get();
			releases = disc.releases.stream()
					.filter(element -> element.title.equals(title))
					.collect(Collectors.toList());
		}
		
		return releases;
	}
	
	public List<ReleaseGroup> searchReleaseGroup(String artist, String title) {
		String url = BASE_URL + "release-group";
		String query = "release:\"" + title + "\" AND artistname:\"" + artist + "\"";
		//https://musicbrainz.org/ws/2/release-group/?query=artistname:Metallica%20AND%20release:...%20and%20justice%20for%20all&fmt=json
		WebTarget target = client.target(url)
				.queryParam("fmt", "json")
				.queryParam("query", query);
		
		ReleaseGroupList groups = target.request().get(ReleaseGroupList.class);
		
		return groups.releaseGroups;
	}
	
	private String urlEncode(String text) {
		try {
			return URLEncoder.encode(text, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			throw new RuntimeException("This should not happen! -> Said the physicist just before the sun crashed into earth.");
		}
	}

}
