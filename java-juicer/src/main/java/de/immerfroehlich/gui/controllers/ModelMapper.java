package de.immerfroehlich.gui.controllers;

import java.util.List;

import de.immerfroehlich.javajuicer.model.Mp3Track;
import javafx.collections.ObservableList;

public class ModelMapper {
	
	ObservableList<MainTableModel> list;
	
	public ModelMapper(ObservableList<MainTableModel> list) {
		this.list = list;
	}
	
	public void map(List<Mp3Track> mp3Tracks) {
		list.clear();
		mp3Tracks.stream().forEach( (mp3Track) -> {
			MainTableModel model = new MainTableModel("00", mp3Track.isPregap, mp3Track.artist, mp3Track.title);
			list.add(model);
		});
	}

}
