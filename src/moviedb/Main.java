package moviedb;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("HomeView.fxml"));
        primaryStage.setTitle("JASMIN - Jenny And Shahram's Movie Information Network");
        primaryStage.setScene(new Scene(root, 800, 600));

        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
