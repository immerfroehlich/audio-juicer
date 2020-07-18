package de.immerfroehlich.gui.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import de.immerfroehlich.javajuicer.utils.ReflectionUtils;
import de.immerfroehlich.musicbrainz.model.Release;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
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

	private String attributePath1;
	private String attributePath3;
	private String attributePath2;
	private String label1;
	private String label2;
	private String label3;
	
	private String text = "";
	private String request = "";

	
	public ReleaseSelectionDialogController(List<Release> releases, String attributePath1, String attributePath2, String attributePath3,
			String label1, String label2, String label3) {
		this.attributePath1 = attributePath1;
		this.attributePath2 = attributePath2;
		this.attributePath3 = attributePath3;
		this.label1 = label1;
		this.label2 = label2;
		this.label3 = label3;
		
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
		
		Object value1;
		Object value2;
		Object value3;
		value1 = ReflectionUtils.getValueOfPath(String.class, release, this.attributePath1);
		value2 = ReflectionUtils.getValueOfPath(String.class, release, this.attributePath2);
		value3 = ReflectionUtils.getValueOfPath(String.class, release, this.attributePath3);
		
		label.setText(label1 + " " + value1);
		
		label = (Label) children.get(1);
		label.setText(label2 + " " + value2);
		
		label = (Label) children.get(2);
		label.setText(label3 + " " + value3);
		
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
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void setRequest(String request) {
		this.request = request;
	}

	private Dialog<Release> createDialog() {
		Dialog<Release> dialog = new Dialog<>();
		dialog.setResizable(true);
		
		if(text.isEmpty()) {
			throw new IllegalStateException("You have to call setText before initialization first.");
		}
		if(request.isEmpty()) {
			throw new IllegalStateException("You have to call setRequest before initialization first.");
		}
		
		dialog.setTitle(request);
		String headerText = text + "\n\n" + request;
		dialog.setHeaderText(headerText);
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
