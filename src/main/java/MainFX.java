import java.net.URL;

import de.immerfroehlich.gui.controllers.MainTableContoller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MainFX extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {

			
			URL fxmlUrl = getClass().getResource("main.fxml");
			FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
			fxmlLoader.setController(new MainTableContoller());
			Parent root = fxmlLoader.load();
			
//			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,1024,786);
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
