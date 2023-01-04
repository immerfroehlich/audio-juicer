package de.immerfroehlich.services;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;

import org.junit.Test;

public class ChecksumServiceTest {
	
	@Test
	public void testChecksum()  {
		
		String packageName = getClass().getPackageName();
		packageName = packageName.replace(".", "/");
		packageName = packageName.replace("java", "resources");
		
		//System.out.println(packageName);
		
		
		URL url = Thread.currentThread().getContextClassLoader().getResource(packageName);
		String path = url.getPath();
		path = path.replace("java-juicer/bin/main", "java-juicer/src/test/resources");
		//TODO Why isn't resources copied over to bin/test ?
		//System.out.println(path);
		
		ChecksumService checksum = new ChecksumService();
		checksum.createChecksumOfAllFilesIn(path);
		
		path+= "/sha256sum.txt";
		File file = new File(path);
		assertTrue(file.exists());
		
		file.delete();
	}

}
