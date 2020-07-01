package de.immerfroehlich.gui;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;

public class ApplicationContext {

	public static StackPane stackPane;
	public static FlowPane flowPane;
	public static ProgressBar progressBar;
	
	public static void showProgressOverlay() {
		stackPane.getChildren().add(flowPane);
	}
	
	public static void hideProgressOverlay() {
		stackPane.getChildren().remove(flowPane);
	}
	
	public static void bindProgressPropertyToProgressBar(ObservableValue<? extends Number> obsVal) {
		progressBar.progressProperty().bind(obsVal);
	}
	
	public static void resetProgressBar() {
		progressBar.progressProperty().unbind();
	}
}
