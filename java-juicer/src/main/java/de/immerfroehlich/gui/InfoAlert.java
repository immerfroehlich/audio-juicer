package de.immerfroehlich.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class InfoAlert {
	
	Alert alert;
	
	public InfoAlert(String contentText) {
		alert = new javafx.scene.control.Alert(AlertType.INFORMATION, contentText, ButtonType.OK);
		alert.setHeaderText(null);
		//TODO Workaround for openjfx bug 222. Remove after backport to Java 11 or update to Java 12
		//See https://github.com/javafxports/openjdk-jfx/issues/222
		alert.setResizable(true);
		alert.getDialogPane().setPrefWidth(350);
		alert.getDialogPane().setPrefHeight(250);
	}
	
	public void showAndWait() {
		alert.showAndWait();
	}

}
