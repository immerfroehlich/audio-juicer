package de.immerfroehlich.coverartarchive;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import org.glassfish.jersey.logging.LoggingFeature;

import de.immerfroehlich.coverartarchive.model.Image;
import de.immerfroehlich.coverartarchive.model.Images;

public class CoverArtService {
	
public static final String BASE_URL = "https://coverartarchive.org/";
	
	private Client client;
	
	public static void main(String[] args) {
		CoverArtService coverArtService = new CoverArtService();
		List<Image> images = coverArtService.lookupCoverArtByMbid("76df3287-6cda-33eb-8e9a-044b5e15ffdd");
		
		String smallImageUrl = images.stream().filter(e -> e.thumbnails.containsKey("small")).map(e -> e.thumbnails.get("small")).reduce("", String::concat);
		System.out.println(smallImageUrl);
	}

	public CoverArtService() {
		this.client = ClientBuilder.newClient()
				.property(LoggingFeature.LOGGING_FEATURE_VERBOSITY_CLIENT, LoggingFeature.Verbosity.PAYLOAD_ANY)
	            .property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL_CLIENT, "WARNING");
	}
	
	public List<Image> lookupCoverArtByMbid(String mbid) {
		mbid = urlEncode(mbid);
		String url = BASE_URL + "release/" + mbid + "/";
		WebTarget target = client.target(url);
		
		try {
			List<Image> images = target.request()
					.get(Images.class).images;
			
			return images;
			
		} catch(NotFoundException e) {
			return new ArrayList<>();
		}
		
	}
	
	private String urlEncode(String text) {
		try {
			return URLEncoder.encode(text, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			throw new RuntimeException("This should not happen! -> Said the physicist just before the sun crashed into earth.");
		}
	}

}
