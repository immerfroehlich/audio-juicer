package de.immerfroehlich.gui.modules.settings;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ObservableNamingScheme {
	
	public ObservableNamingScheme(StringProperty name, StringProperty scheme) {
		super();
		this.name = name;
		this.scheme = scheme;
	}
	public StringProperty name = new SimpleStringProperty();
	public StringProperty scheme = new SimpleStringProperty();
	
}

