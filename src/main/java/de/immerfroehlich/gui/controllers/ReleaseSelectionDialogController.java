package de.immerfroehlich.gui.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import de.immerfroehlich.musicbrainz.model.Release;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;

public class ReleaseSelectionDialogController implements Initializable {
	
	FXMLLoader fxmlLoader;
	
	@FXML private ScrollPane selectionScrollPane;
	@FXML private VBox releaseSelectionVBox;
	@FXML private ScrollPane detailScrollPane;
	
	private List<Release> releases;
	
	private Release selectedRelease;
	
	public ReleaseSelectionDialogController(List<Release> releases) {
		this.releases = releases;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		URL fxmlUrl = getClass().getResource("releaseSelectionToggleButton.fxml");
		fxmlLoader = new FXMLLoader(fxmlUrl);
		
		for(Release release : releases) {
			ToggleButton button = createReleaseSelectionToggleButton(release);
			releaseSelectionVBox.getChildren().add(button);
		}
	}
	
	private ToggleButton createReleaseSelectionToggleButton(Release release) {
		ToggleButton button = null;
		try {
			//TODO is it loaded from file each time? If yes copy the Stream in memory.
			button = fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		VBox vbox = (VBox) button.getGraphic();
		
		ObservableList<Node> children = vbox.getChildrenUnmodifiable();
		Label label = (Label) children.get(0);
		label.setText(release.title);
		
		label = (Label) children.get(1);
		label.setText(release.date);
		
		label = (Label) children.get(2);
		label.setText(release.barcode);
		
		button.setOnAction((e) -> selectedRelease = release);
		
		return button;
	}

}
