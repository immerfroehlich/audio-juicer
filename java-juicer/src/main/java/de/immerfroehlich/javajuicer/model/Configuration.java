package de.immerfroehlich.javajuicer.model;

import de.immerfroehlich.gui.modules.settings.ObservableNamingScheme;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Configuration {
	
	public static StringProperty rootPath = new SimpleStringProperty();
	
	public static StringProperty drivePath = new SimpleStringProperty();
	
	public static ObservableList<ObservableNamingScheme> namingSchemes = FXCollections.observableArrayList(
			namingScheme -> new Observable[]{namingScheme.name, namingScheme.scheme});
	
}
