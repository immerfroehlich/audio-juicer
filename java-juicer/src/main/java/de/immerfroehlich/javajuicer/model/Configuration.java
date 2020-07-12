package de.immerfroehlich.javajuicer.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Configuration {
	
	public static final String version = "0.1";
	
	public static StringProperty rootPath = new SimpleStringProperty();
	
	public static StringProperty drivePath = new SimpleStringProperty();
	
	public static StringProperty naming = new SimpleStringProperty();
}
