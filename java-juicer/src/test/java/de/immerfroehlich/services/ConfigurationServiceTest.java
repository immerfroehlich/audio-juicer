package de.immerfroehlich.services;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

import de.immerfroehlich.javajuicer.model.Configuration;

public class ConfigurationServiceTest {

//	public static void main(String[] args) {
//		ConfigurationService configService = new ConfigurationService();
//		
//		configService.createConfig();
//	}
	
//	@Test
//	public void testLoading() {
//		
////		ConfigurationService configService = new ConfigurationService();
//		
//		String json =
//				"{\n" + 
//				"  \"title\" : \"Thinking in Java\",\n" + 
//				"  \"isbn\" : \"978-0131872486\",\n" + 
//				"  \"year\" : 1998,\n" + 
//				"  \"authors\" : [ \"Bruce Eckel\" ]\n" + 
//				"}";
//		
//		InputStream is = new Byte
//		
//		ConfigurationService configService = mock(ConfigurationService.class);
//		when(configService.loadFile()).then(answer)
//	}
	
	@Test
	public void testSaving() {
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
				
		ConfigurationService configService = spy(new ConfigurationService());
		when(configService.openFileForOutput()).thenReturn(out);
		
		Configuration.drivePath.setValue("/dev/cdrom");
		
		configService.saveConfig();
		
		System.out.println(out);
		
		//don't test the whole mapping only if it is essentially working
		assertTrue(out.toString().contains("\"drive-path\":\"/dev/cdrom\""));
	}

}
