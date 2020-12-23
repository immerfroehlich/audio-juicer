package de.immerfroehlich.gui.controls;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;

public class CopyableTextInfoDialog {
	
	private Alert alert;
	
	public CopyableTextInfoDialog(String text, String copyableText) {
		alert = new javafx.scene.control.Alert(AlertType.INFORMATION, "Fortschritt", ButtonType.OK);
		alert.setHeaderText(text);
		alert.setGraphic(null);
		//TODO Workaround for openjfx bug 222. Remove after backport to Java 11 or update to Java 12
		//See https://github.com/javafxports/openjdk-jfx/issues/222
		alert.setResizable(true);
		alert.getDialogPane().setPrefWidth(400);
		alert.getDialogPane().setPrefHeight(350);
		
		TextArea textArea = new TextArea();
		textArea.setText(copyableText);
		alert.getDialogPane().setContent(textArea);
	}
	
	public void showAndWait() {
		alert.showAndWait();
	}
	
}
