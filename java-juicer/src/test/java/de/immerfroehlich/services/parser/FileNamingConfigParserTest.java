package de.immerfroehlich.services.parser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.immerfroehlich.javajuicer.model.Album;
import de.immerfroehlich.javajuicer.model.Mp3Track;

public class FileNamingConfigParserTest {

	@Test
	public void testParseWithoutMultiCD() {
		String naming = "</CD%c>/%a/%l/%n %t";
		FileNamingConfigParser parser = new FileNamingConfigParser();
		
		Album albumInfo = new Album();
		albumInfo.artist = "Artist";
		albumInfo.title = "AlbumTitle";
		albumInfo.multiCdRelease = false;
		
		String directories = parser.parse(naming, albumInfo);
		
		assertEquals("/Artist/AlbumTitle", directories);
		
		Mp3Track track = new Mp3Track();
		track.title = "TrackTitle";
		track.artist = "TrackArtist";
		String trackNumber = "11";
		String fileName = parser.parseFileName(track, trackNumber);
		
		assertEquals("11 "+ track.title, fileName);
	}
	
	@Test
	public void testParseMultiCD() {
		String naming = "/%a/%l</CD%c>/%n %t";
		FileNamingConfigParser parser = new FileNamingConfigParser();
		
		Album albumInfo = new Album();
		albumInfo.artist = "Artist";
		albumInfo.title = "AlbumTitle";
		albumInfo.multiCdRelease = true;
		albumInfo.cdNumber = "2";
		
		String directories = parser.parse(naming, albumInfo);
		
		assertEquals("/Artist/AlbumTitle/CD2", directories);
	}
	
	@Test
	public void testParseWithoutOptional() {
		String naming = "/%a/%l/%n %t";
		FileNamingConfigParser parser = new FileNamingConfigParser();
		
		Album albumInfo = new Album();
		albumInfo.artist = "Artist";
		albumInfo.title = "AlbumTitle";
		albumInfo.multiCdRelease = true;
		albumInfo.cdNumber = "2";
		
		String directories = parser.parse(naming, albumInfo);
		
		assertEquals("/Artist/AlbumTitle", directories);
	}
	
	@Test
	public void testParseDifferentNamingScheme() {
		String naming = "/%a - %l/<CD%c>/%n-%a-%r - %t";
		FileNamingConfigParser parser = new FileNamingConfigParser();
		
		Album albumInfo = new Album();
		albumInfo.artist = "Artist";
		albumInfo.title = "AlbumTitle";
		albumInfo.multiCdRelease = true;
		albumInfo.cdNumber = "2";
		
		String directories = parser.parse(naming, albumInfo);
		
		assertEquals("/Artist - AlbumTitle/CD2", directories);
		
		Mp3Track track = new Mp3Track();
		track.title = "TrackTitle";
		track.artist = "TrackArtist";
		String trackNumber = "11";
		String fileName = parser.parseFileName(track, trackNumber);
		
		assertEquals(trackNumber + "-" + albumInfo.artist + "-" + track.artist + " - " + track.title, fileName);
	}
	
}
