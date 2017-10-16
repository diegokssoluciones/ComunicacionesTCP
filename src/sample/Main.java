package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
    primaryStage.setTitle("Client-Server");
    primaryStage.setScene(new Scene(root, 669, 415));
    primaryStage.show();

    primaryStage.setOnHiding(event -> Platform.runLater(() -> System.exit(0)));
  }


  public static void main(String[] args) {
    launch(args);
  }
}
