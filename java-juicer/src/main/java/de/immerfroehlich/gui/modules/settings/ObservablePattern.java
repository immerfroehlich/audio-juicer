package de.immerfroehlich.gui.modules.settings;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ObservablePattern {
	
	public ObservablePattern(StringProperty name, StringProperty pattern) {
		super();
		this.name = name;
		this.pattern = pattern;
	}
	public StringProperty name = new SimpleStringProperty();
	public StringProperty pattern = new SimpleStringProperty();
	
}

