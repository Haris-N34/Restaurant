//Creators: Haris Naveed(30240785), Arash Ajdari(30237745), Shayan Shaikh(30241360)

package ca.ucalgary.haris.naveed1.demo3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainGUI extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        // Load the CSS file for styling.
        scene.getStylesheets().add(MainGUI.class.getResource("styles.css").toExternalForm());
        stage.setTitle("McDonald's Nutrition Tracker");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
