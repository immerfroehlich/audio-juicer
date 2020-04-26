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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.VBox;

public class ReleaseSelectionDialogController implements Initializable {
	
	FXMLLoader fxmlLoader;
	
	@FXML private VBox rootVBox;
	@FXML private ScrollPane selectionScrollPane;
	@FXML private VBox releaseSelectionVBox;
	@FXML private ScrollPane detailScrollPane;
	private Dialog<Release> dialog;
	private Button selectButton;
	
	private List<Release> releases;
	
	private Release selectedRelease;


	
	public ReleaseSelectionDialogController(List<Release> releases) {
		this.releases = releases;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		for(Release release : releases) {
			ToggleButton button = createReleaseSelectionToggleButton(release);
			releaseSelectionVBox.getChildren().add(button);
		}
		
		this.dialog = createDialog();
	}
	
	private ToggleButton createReleaseSelectionToggleButton(Release release) {
		ToggleButton button = null;
		try {
			URL fxmlUrl = getClass().getResource("releaseSelectionToggleButton.fxml");
			fxmlLoader = new FXMLLoader(fxmlUrl);
			//TODO is it loaded from file each time? If yes copy the Stream in memory.
			button = fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		VBox vbox = (VBox) button.getGraphic();
		
		ObservableList<Node> children = vbox.getChildrenUnmodifiable();
		Label label = (Label) children.get(0);
		label.setText("Title: " + release.title);
		
		label = (Label) children.get(1);
		label.setText("Release date: " + release.date);
		
		label = (Label) children.get(2);
		label.setText("Barcode: " + release.barcode);
		
		button.setOnAction((e) -> {
			selectedRelease = release;
			selectButton.setDisable(false);
		});
		
		return button;
	}
	
	public Release showAndWait() {
		
		
		Release release = dialog.showAndWait().get();
		return release;
	}

	private Dialog<Release> createDialog() {
		Dialog<Release> dialog = new Dialog<>();
		dialog.setResizable(true);
		String text = "More then one release was found that has the same track list as your CD.\n"
				+ "Please select the right release.\n"
				+ "Most often the easiest way to do this is to compare the barcode numbers.\n"
				+ "This is neccessary because the metadata and the art work can be different.";
		dialog.setHeaderText(text);
		dialog.getDialogPane().setContent(rootVBox);
		ButtonType buttonTypeOne = new ButtonType("Select", ButtonData.APPLY);
		dialog.getDialogPane().getButtonTypes().add(buttonTypeOne);
		
		this.selectButton = (Button) dialog.getDialogPane().lookupButton(buttonTypeOne);
		selectButton.setDisable(true);
		
		dialog.setResultConverter(dialogButton -> {
		    if (dialogButton == buttonTypeOne) {
		        return selectedRelease;
		    }
		    return null;
		});
		return dialog;
	}

}
