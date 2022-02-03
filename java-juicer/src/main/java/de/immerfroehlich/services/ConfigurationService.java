package de.immerfroehlich.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.immerfroehlich.gui.modules.settings.ObservableNamingScheme;
import de.immerfroehlich.javajuicer.model.Configuration;
import de.immerfroehlich.javajuicer.model.ReleaseType;
import de.immerfroehlich.javajuicer.model.config.ConfigModel;
import de.immerfroehlich.javajuicer.model.config.Naming;
import javafx.beans.property.SimpleStringProperty;

public class ConfigurationService {
	
	private static final String VERSION = "0.2";
	
	String home = System.getProperty("user.home");
	String configFile = home + File.separator + ".JavaJuicer" + File.separator + "config.json";
	
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
			
			ConfigModel config = createInitialConfiguration();
			FileOutputStream fos = new FileOutputStream(file);
			writeConfig(fos, config);
			fos.close();
			
		} catch (IOException e) {
			throw new RuntimeException("This is either a hard IO Exception like the harddisk is corrupt or a programming error.", e);
		}
		
	}

	private ConfigModel createInitialConfiguration() {
		ConfigModel config = new ConfigModel();
		config.rootPath = home + File.separator + "Musik" + File.separator + "Archiv";
		config.drivePath = "/dev/sr0";
		
		//Naming and ReleaseType need to be linked and in the user config the user needs to select the type for a naming scheme.
		
		String name = ReleaseType.SingleArtistAlbumOrSingle.getString();
		String pattern = "/Single Artist/%a/%l</CD%c>/%n %t";
		Naming naming = new Naming(name, pattern);
		config.namings.add(naming);
		
		name = ReleaseType.InterpretedMusic.getString();
		pattern = "/Interpreted Music/%a/%l</CD%c>/%n %t";
		naming = new Naming(name, pattern);
		config.namings.add(naming);
		
		name = ReleaseType.Compilation.getString();
		pattern = "/Compilations/%l</CD%c>/%n %r - %t";
		naming = new Naming(name, pattern);
		config.namings.add(naming);
		
		name = ReleaseType.Audio.getString();
		pattern = "/Audio/%l (%a)</CD%c>/%n %t";
		naming = new Naming(name, pattern);
		config.namings.add(naming);
		
		return config;		
	}

	private void writeConfig(OutputStream out, ConfigModel config) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(out, config);
		} catch (IOException e) {
			throw new RuntimeException("Most likely programming error or harddrive error or you are not allowed to write to the file.", e);
		}
		
	}
	
	InputStream openFileForInput() {
		File file = new File(configFile);
		try {
			FileInputStream fis = new FileInputStream(file);
			return fis;
		} catch (IOException e) {
			throw new RuntimeException("This is either a hard IO Exception like the harddisk is corrupt or a programming error.", e);
		}
	}
	
	public void loadConfig() {
		InputStream fis = openFileForInput();
		ObjectMapper mapper = new ObjectMapper();
		try {
			ConfigModel config = mapper.readValue(fis, ConfigModel.class);
			mapToGUIConfiguration(config);
		} catch (IOException e1) {
			throw new RuntimeException("This is either a hard IO Exception like the harddisk is corrupt or a programming error.", e1);
		}
		
	}
	
	OutputStream openFileForOutput() {
		File file = new File(configFile);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			return fos;
		} catch (IOException e) {
			throw new RuntimeException("This is either a hard IO Exception like the harddisk is corrupt or a programming error.", e);
		}
	}
	
	public void saveConfig() {
		ConfigModel config = mapToFileConfig();
		OutputStream fos = openFileForOutput();
		writeConfig(fos, config);
	}

	private ConfigModel mapToFileConfig() {
		ConfigModel config = new ConfigModel();
		config.rootPath = Configuration.rootPath.getValue();
		config.drivePath = Configuration.drivePath.getValue();
		List<Naming> presets = Configuration.namingSchemes.stream()
				.map(guiPreset -> new Naming(guiPreset.name.getValue(), guiPreset.scheme.getValue()))
				.collect(Collectors.toList());
		config.namings = presets;
		
		return config;
	}

	private void mapToGUIConfiguration(ConfigModel config) {
		if(!config.version.equals(VERSION)) {
			throw new RuntimeException("Upgrading of configuration file currently not supported. Please delete config from disk and reconfigure.");
		}
		
		Configuration.rootPath.setValue(config.rootPath);
		Configuration.drivePath.setValue(config.drivePath);
		List<ObservableNamingScheme> presets = config.namings.stream()
				.map(filePreset -> new ObservableNamingScheme(new SimpleStringProperty(filePreset.name), new SimpleStringProperty(filePreset.pattern)))
				.collect(Collectors.toList());
		Configuration.namingSchemes.addAll(presets);
	}

}
