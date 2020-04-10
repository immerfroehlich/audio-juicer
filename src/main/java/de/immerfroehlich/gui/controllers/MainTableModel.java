package de.immerfroehlich.gui.controllers;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MainTableModel {
	
	StringProperty track;
	SimpleBooleanProperty pregap;
	StringProperty artist;
	StringProperty title;
	
	public MainTableModel(String track, boolean pregap, String artist, String title) {
		super();
		this.track = new SimpleStringProperty(track);
		this.pregap = new SimpleBooleanProperty(pregap);
		this.artist = new SimpleStringProperty(artist);
		this.title = new SimpleStringProperty(title);
	}

}
