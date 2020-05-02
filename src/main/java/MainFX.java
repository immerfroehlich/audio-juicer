import java.net.URL;

import de.immerfroehlich.gui.ApplicationContext;
import de.immerfroehlich.gui.controllers.MainTableContoller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class MainFX extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			URL fxmlUrl = getClass().getResource("main.fxml");
			FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
			fxmlLoader.setController(new MainTableContoller());
			Parent mainTableView = fxmlLoader.load();
			
			StackPane stackPane = new StackPane();
			stackPane.getChildren().add(mainTableView);
			
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
			scene.getStylesheets().add(getClass().getResource("main.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
