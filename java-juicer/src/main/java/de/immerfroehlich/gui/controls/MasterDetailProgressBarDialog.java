package de.immerfroehlich.gui.controls;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class MasterDetailProgressBarDialog {
	
	private Alert alert;
	
	@FXML private Label masterLabel;
	@FXML private ProgressBar masterProgressBar;
	@FXML private Label detailLabel;
	@FXML private ProgressBar detailProgressBar;
	@FXML private Label masterTaskLabel;
	@FXML private Label detailTaskLabel;
	
	private int masterTaskNumber = 0;
	private int detailTaskNumber = 0;
	private int currentMasterTaskNumber = 0;
	private int currentDetailTaskNumber = 0;
	
	public MasterDetailProgressBarDialog() {
		alert = new javafx.scene.control.Alert(AlertType.INFORMATION, "Fortschritt", ButtonType.OK);
		alert.setHeaderText(null);
		alert.setGraphic(null);
		//TODO Workaround for openjfx bug 222. Remove after backport to Java 11 or update to Java 12
		//See https://github.com/javafxports/openjdk-jfx/issues/222
		alert.setResizable(true);
		alert.getDialogPane().setPrefWidth(350);
		alert.getDialogPane().setPrefHeight(250);
		
		URL fxmlUrl = getClass().getResource("masterDetailProgressBar.fxml");
		FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
		fxmlLoader.setController(this);
		Parent progressBarView = null;
		try {
			progressBarView = fxmlLoader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		alert.getDialogPane().setContent(progressBarView);
	}
	
	public void setMasterTaskNumber(int masterTaskNumber) {
		this.masterTaskNumber = masterTaskNumber;
	}
	
	public void setDetailTaskNumber(int detailTaskNumber) {
		this.detailTaskNumber = detailTaskNumber; 
	}
	
	public void setMasterText(String text) {
		masterLabel.setText(text);
	}
	
	public void setDetailText(String text) {
		detailLabel.setText(text);
	}
	
	public void show() {
		alert.show();
	}
	
	public void update() {
		updateMasterTaskLabel();
		updateDetailTaskLabel();
	}
	
	public void masterTaskFinished() {
		this.currentMasterTaskNumber++;
		this.currentDetailTaskNumber = 0;
		updateMasterPercentage();
		updateMasterTaskLabel();
	}

	private void updateMasterTaskLabel() {
		int currentTaskNum = currentMasterTaskNumber;
		if(currentMasterTaskNumber < masterTaskNumber) {
			currentTaskNum++;
		}
		String text = "" + currentTaskNum + "/" + masterTaskNumber;
		masterTaskLabel.setText(text);
	}

	private void updateMasterPercentage() {
		double percentage = (double)currentMasterTaskNumber / (double)masterTaskNumber;
		masterProgressBar.progressProperty().setValue(percentage);
	}
	
	public void detailTaskFinished() {
		this.currentDetailTaskNumber++;
		updateDetailPercentage();
		updateDetailTaskLabel();
	}

	private void updateDetailTaskLabel() {
		int currentTaskNum = currentDetailTaskNumber;
		if(currentDetailTaskNumber < detailTaskNumber) {
			currentTaskNum++;
		}
		String text = "" + currentTaskNum + "/" + detailTaskNumber;
		detailTaskLabel.setText(text);
	}

	private void updateDetailPercentage() {
		double percentage = (double)currentDetailTaskNumber / (double)detailTaskNumber;
		detailProgressBar.progressProperty().setValue(percentage);
	}
}
