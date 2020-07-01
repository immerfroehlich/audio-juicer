package de.immerfroehlich.javajuicer.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class FATCharRemoverTest {

	@Test
	public void test() {
		
		String filename = "This? is a filename?.txt";
		String newFilename = FATCharRemover.removeUnallowedChars(filename);
		
		assertEquals("This is a filename.txt", newFilename);
	}

}
