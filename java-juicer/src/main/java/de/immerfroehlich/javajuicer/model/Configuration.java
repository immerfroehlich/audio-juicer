package de.immerfroehlich.javajuicer.model;

import de.immerfroehlich.gui.modules.settings.ObservablePattern;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Configuration {
	
	public static StringProperty rootPath = new SimpleStringProperty();
	
	public static StringProperty drivePath = new SimpleStringProperty();
	
	public static ObservableList<ObservablePattern> namings = FXCollections.observableArrayList(
			preset -> new Observable[]{preset.name, preset.pattern});
	
}
