package de.immerfroehlich.gui.modules.settings;

import de.immerfroehlich.javajuicer.model.Configuration;
import javafx.util.StringConverter;

public class ObservableNamingSchemeStringConverter extends StringConverter<ObservableNamingScheme>{
	
	@Override
	public String toString(ObservableNamingScheme object) {
		return object.name.getValue();
	}
	
	@Override
	public ObservableNamingScheme fromString(String string) {
		for(ObservableNamingScheme pattern: Configuration.namingSchemes) {
			if(pattern.name.getValue().equals(string)) {
				return pattern;
			}
		}
		
		throw new RuntimeException("Programming error!");
	}

}
