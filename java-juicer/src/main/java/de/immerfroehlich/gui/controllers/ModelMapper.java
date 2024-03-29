package de.immerfroehlich.gui.controllers;

import java.util.List;

import de.immerfroehlich.javajuicer.model.TrackInfo;
import javafx.collections.ObservableList;

public class ModelMapper {
	
	ObservableList<MainTableModel> list;
	
	public ModelMapper(ObservableList<MainTableModel> list) {
		this.list = list;
	}
	
	public void map(List<TrackInfo> mp3Tracks) {
		list.clear();
		mp3Tracks.stream().forEach( (mp3Track) -> {
			MainTableModel model = new MainTableModel(mp3Track.trackNumber, mp3Track.isPregap, mp3Track.artist, mp3Track.title, mp3Track.firstReleaseDate, mp3Track.album);
			list.add(model);
		});
	}

}
