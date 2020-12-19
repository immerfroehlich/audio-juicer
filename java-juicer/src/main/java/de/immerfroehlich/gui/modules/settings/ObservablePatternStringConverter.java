package de.immerfroehlich.gui.modules.settings;

import de.immerfroehlich.javajuicer.model.Configuration;
import javafx.util.StringConverter;

public class ObservablePatternStringConverter extends StringConverter<ObservablePattern>{
	
	@Override
	public String toString(ObservablePattern object) {
		return object.name.getValue();
	}
	
	@Override
	public ObservablePattern fromString(String string) {
		for(ObservablePattern pattern: Configuration.namings) {
			if(pattern.name.getValue().equals(string)) {
				return pattern;
			}
		}
		
		throw new RuntimeException("Programming error!");
	}

}
