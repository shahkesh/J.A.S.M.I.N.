package moviedb;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class TrailerViewController implements Initializable {



    @FXML private WebView trailerWindow;


    public void loadTrailer(String URL) {
        trailerWindow.getEngine().load(URL);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {



    }

}
