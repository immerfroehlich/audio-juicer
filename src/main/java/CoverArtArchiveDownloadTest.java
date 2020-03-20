import de.immerfroehlich.coverartarchive.CoverArtArchiveDownloader;
import de.immerfroehlich.musicbrainz.model.CoverArtArchive;
import de.immerfroehlich.musicbrainz.model.Release;

public class CoverArtArchiveDownloadTest {

	public static void main(String[] args) {
		CoverArtArchiveDownloader coverDownloader = new CoverArtArchiveDownloader();
		Release release = new Release();
		release.id = "76df3287-6cda-33eb-8e9a-044b5e15ffdd";
		
		CoverArtArchive coverArt = new CoverArtArchive();
		coverArt.front = true;
		release.coverArtArchive = coverArt;
		
		coverDownloader.downloadImages(release, "images");
	}

}
