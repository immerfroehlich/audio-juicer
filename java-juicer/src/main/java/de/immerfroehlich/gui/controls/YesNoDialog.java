package de.immerfroehlich.gui.controls;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;

public class YesNoDialog {
	
	private String text;
	private String question;
	
	public YesNoDialog(String text, String question) {
		this.text = text;
		this.question = question;
	}
	
	public boolean showAndWait() {
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setHeaderText(question);
		alert.setTitle(question);
		
		//TODO Workaround for openjfx bug 222. Remove after backport to Java 11 or update to Java 12
		//See https://github.com/javafxports/openjdk-jfx/issues/222
		alert.setResizable(true);
		alert.setContentText(text);

		ButtonType buttonTypeOne = new ButtonType("Yes", ButtonData.YES);
		ButtonType buttonTypeCancel = new ButtonType("No", ButtonData.NO);

		alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonTypeOne){
		    return true;
		} else {
			return false;
		}
	}
	
}
