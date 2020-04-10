package de.immerfroehlich.javajuicer.mappers;

import java.util.ArrayList;
import java.util.List;

import de.immerfroehlich.javajuicer.model.Mp3Track;
import de.immerfroehlich.musicbrainz.model.Medium;
import de.immerfroehlich.musicbrainz.model.Pregap;
import de.immerfroehlich.musicbrainz.model.Release;
import de.immerfroehlich.musicbrainz.model.Track;

public class Mp3TrackMapper {
	
	public List<Mp3Track> mapToMp3Tracks(Release release, String releaseDate, Medium medium) {
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

}
