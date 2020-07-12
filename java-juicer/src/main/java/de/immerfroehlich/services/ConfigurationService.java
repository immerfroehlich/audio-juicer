package de.immerfroehlich.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import de.immerfroehlich.javajuicer.model.Configuration;

public class ConfigurationService {
	
	String home = System.getProperty("user.home");
	String configFile = home + File.separator + ".JavaJuicer" + File.separator + "config.prop";
	
	public void createConfig() {
		System.out.println(home);
		System.out.println(configFile);
		
		File file = new File(configFile);
		if(file.exists()) {
			return;
		}
		
		File parent = file.getParentFile();
		if (!parent.exists() && !parent.mkdirs()) {
			throw new IllegalStateException("Couldn't create dir: " + parent);
		}
		
		try {
		
			file.createNewFile();
					
			createInitialConfiguration();
			Properties prop = createPropertiesFromConfiguration();
			
			FileOutputStream fos = new FileOutputStream(file);
			prop.store(fos, "");
			fos.close();
			
		} catch (IOException e) {
			throw new RuntimeException("This is either a hard IO Exception like the harddisk is corrupt or a programming error.", e);
		}
		
	}

	private void createInitialConfiguration() {
		Configuration.rootPath.setValue(home + File.separator + "Musik" + File.separator + "Archiv"); 
		Configuration.drivePath.setValue("/dev/sr0");
		Configuration.naming.setValue("/%a/%l</CD%c>/%n %t");
	}

	private Properties createPropertiesFromConfiguration() {
		//TODO: Isn't there already some code in the Java API that creates the properties from the object?
		Properties prop = new Properties();
		prop.setProperty("version", Configuration.version);
		prop.setProperty("rootPath", Configuration.rootPath.get());
		prop.setProperty("drivePath", Configuration.drivePath.get());
		prop.setProperty("naming", Configuration.naming.get());
		
		return prop;
	}
	
	public void loadConfig() {
		File file = new File(configFile);
		try {
			FileInputStream fis = new FileInputStream(file);
			Properties prop = new Properties();
			prop.load(fis);
			mapToConfiguration(prop);
		} catch (IOException e) {
			throw new RuntimeException("This is either a hard IO Exception like the harddisk is corrupt or a programming error.", e);
		}
	}
	
	public void saveConfig() {
		File file = new File(configFile);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			Properties prop = createPropertiesFromConfiguration();
			prop.store(fos, "");
		} catch (IOException e) {
			throw new RuntimeException("This is either a hard IO Exception like the harddisk is corrupt or a programming error.", e);
		}
	}

	private void mapToConfiguration(Properties prop) {
		String version = prop.getProperty("version");
		if(!version.equals(Configuration.version)) {
			throw new RuntimeException("Upgrading of configuration file currently not supported. Please delete config from disk and reconfigure.");
		}
		
		Configuration.rootPath.setValue(prop.getProperty("rootPath"));
		Configuration.drivePath.setValue(prop.getProperty("drivePath"));
		Configuration.naming.setValue(prop.getProperty("naming"));
	}

}
