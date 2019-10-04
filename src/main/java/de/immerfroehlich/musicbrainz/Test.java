package de.immerfroehlich.musicbrainz;

import de.immerfroehlich.discid.DiscIdCalculator;
import de.immerfroehlich.musicbrainz.model.Disc;
import de.immerfroehlich.musicbrainz.model.Medium;
import de.immerfroehlich.musicbrainz.model.Release;
import de.immerfroehlich.musicbrainz.model.Track;

public class Test {
	
	public static void main(String[] args) {
		
		DiscIdCalculator calculator = new DiscIdCalculator();
		String drivePath = "/dev/sr0";
		String discid = calculator.calculate(drivePath);
		
		MusicbrainzWs2Service service = new MusicbrainzWs2Service();
		Disc disc = service.lookupDiscById(discid);
		
		for(Release release : disc.releases) {
			System.out.println(release.title);
			System.out.println(release.date);
			System.out.println(release.barcode);
			System.out.println();
			for(Medium medium : release.media) {
				for(Track track : medium.tracks) {
					System.out.print(track.position + ": ");
					System.out.println(track.title);
				}
			}
			System.out.println("---------------");
		}
	}

}
