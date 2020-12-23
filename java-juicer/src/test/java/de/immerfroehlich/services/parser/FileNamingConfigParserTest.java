package de.immerfroehlich.services.parser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.immerfroehlich.javajuicer.model.AlbumInfo;
import de.immerfroehlich.javajuicer.model.TrackInfo;

public class FileNamingConfigParserTest {

	@Test
	public void testParseWithoutMultiCD() {
		String naming = "</CD%c>/%a/%l/%n %t";
		FileNamingConfigParser parser = new FileNamingConfigParser();
		
		AlbumInfo albumInfo = new AlbumInfo();
		albumInfo.artist = "Artist";
		albumInfo.title = "AlbumTitle";
		albumInfo.multiCdRelease = false;
		
		String directories = parser.parse(naming, albumInfo);
		
		assertEquals("/Artist/AlbumTitle", directories);
		
		TrackInfo track = new TrackInfo();
		track.title = "TrackTitle";
		track.artist = "TrackArtist";
		track.trackNumber = "11";
		String fileName = parser.parseFileName(track);
		
		assertEquals("11 "+ track.title, fileName);
	}
	
	@Test
	public void testParseMultiCD() {
		String naming = "/%a/%l</CD%c>/%n %t";
		FileNamingConfigParser parser = new FileNamingConfigParser();
		
		AlbumInfo albumInfo = new AlbumInfo();
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
		
		AlbumInfo albumInfo = new AlbumInfo();
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
		
		AlbumInfo albumInfo = new AlbumInfo();
		albumInfo.artist = "Artist";
		albumInfo.title = "AlbumTitle";
		albumInfo.multiCdRelease = true;
		albumInfo.cdNumber = "2";
		
		String directories = parser.parse(naming, albumInfo);
		
		assertEquals("/Artist - AlbumTitle/CD2", directories);
		
		TrackInfo track = new TrackInfo();
		track.title = "TrackTitle";
		track.artist = "TrackArtist";
		track.trackNumber = "11";
		String fileName = parser.parseFileName(track);
		
		assertEquals(track.trackNumber + "-" + albumInfo.artist + "-" + track.artist + " - " + track.title, fileName);
	}
	
}
