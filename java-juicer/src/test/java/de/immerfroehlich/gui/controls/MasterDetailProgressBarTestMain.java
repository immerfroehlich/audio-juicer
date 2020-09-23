package de.immerfroehlich.gui.controls;

import java.util.List;
import java.util.function.BiConsumer;

import de.immerfroehlich.gui.ApplicationContext;
import de.immerfroehlich.gui.FXUtils;
import de.immerfroehlich.gui.controls.MasterDetailProgressBarDialog;
import de.immerfroehlich.javajuicer.model.Mp3Track;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MasterDetailProgressBarTestMain extends Application {
	
	int i = 0;
	int j = 0;

	@Override
	public void start(Stage primaryStage) {
		
		StackPane stackPane = new StackPane();
		
		FlowPane flowPane = new FlowPane();
		flowPane.setAlignment(Pos.CENTER);
		flowPane.setStyle("-fx-background-color: rgba(64, 64, 64, 0.85);");
		
		ProgressBar progressBar = new ProgressBar();
		flowPane.getChildren().add(progressBar);
		
		ApplicationContext.stackPane = stackPane;
		ApplicationContext.flowPane = flowPane;
		ApplicationContext.progressBar = progressBar;
		
//			BorderPane root = new BorderPane();
		Scene scene = new Scene(stackPane,1024,786);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		MasterDetailProgressBarDialog dialog = new MasterDetailProgressBarDialog();
		dialog.setMasterTaskNumber(2);
		dialog.setDetailTaskNumber(10);
		dialog.setMasterText("Ripping CD");
		dialog.setDetailText("Converting Track 1 to MP3");
		dialog.update();
		dialog.show();
		
		Task<Integer> task = new Task<>() {
			@Override
			protected Integer call() throws Exception {
				for(i = 0; i < 2; i++) {
					for(j = 0; j < 10; j++) {
						Thread.currentThread().sleep(800);
						FXUtils.runAndWait(() -> {
							dialog.detailTaskFinished();
							if(i == 0) {
								dialog.setDetailText("Ripping Track " + (j+1) + " from CD");
							}
							else {
								dialog.setDetailText("Converting Track " + (j+1) + " to MP3");
							}
						});
					}
					
					Thread.currentThread().sleep(800);
					FXUtils.runAndWait(() -> {
						dialog.masterTaskFinished();
						dialog.setMasterText("Converting to MP3");
					});
				}
				
				return Integer.MAX_VALUE;
			}
		};
		
		BiConsumer<WorkerStateEvent, Service<Integer>> onSucceededCallback = (event1, service) -> {
		};
		
		Service<Integer> service = FXUtils.createService(task, onSucceededCallback);
		service.start();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
