package moviedb;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class MovieViewController implements Initializable {

    /*TODO: check if we need these!*/
    @FXML private Button favoritesButton;
    @FXML private Button backButton;
    @FXML private Button trailerButton;
    @FXML private ImageView favoriteHeart;
    @FXML private ImageView playButton;
    @FXML private Label movieTitle;
    @FXML private TextArea storyArea;
    @FXML private ImageView poster;
    @FXML private ImageView poster1;
    @FXML private ImageView similar0;
    @FXML private ImageView similar1;
    @FXML private ImageView cast0;
    @FXML private ImageView cast1;
    @FXML private ImageView cast2;
    @FXML private Label releaseDateLabel;
    @FXML private ProgressBar ratingProgressBar;
    @FXML private Label ratingLabel;
    @FXML private Label deathDateLabel;
    @FXML private Label birthPlaceLabel;
    @FXML private Label similiarLabel;
    @FXML private Label actorLabel;

    static Movie requestedMovie = new Movie();
    static String fileName = "favorites.txt";
    public static File favoriteList = new File(fileName);
    private static final String newLine = System.getProperty("line.separator");


    public void setMovieObject(){

        if(requestedMovie.title.compareTo("TITLE NOT FOUND") == 0){

            trailerButton.setVisible(false);
            poster1.setVisible(false);
            similar0.setVisible(false);
            similar1.setVisible(false);
            cast0.setVisible(false);
            cast1.setVisible(false);
            cast2.setVisible(false);
            favoritesButton.setVisible(false);
            releaseDateLabel.setVisible(false);
            birthPlaceLabel.setVisible(false);
            movieTitle.setText(requestedMovie.title);
            deathDateLabel.setVisible(false);
            ratingProgressBar.setVisible(false);
            similiarLabel.setVisible(false);
            actorLabel.setVisible(false);

            // katze!
            Image posterImg = new Image("moviedb/notfound.gif");
            poster.setImage(posterImg);
            //QRCODE
            if (requestedMovie.poster1 != null){
                Image qrcode = new Image(requestedMovie.poster1);
                poster1.setImage(qrcode);
            }
        }else{
            movieTitle.setText(requestedMovie.title);
            storyArea.setText(requestedMovie.story);

            if(requestedMovie.poster != null) {
                Image posterImg = new Image(requestedMovie.poster);
                poster.setImage(posterImg);
            }
            //similar Movies
            if(requestedMovie.similiar0 != null){
                Image similar0IMG = new Image(requestedMovie.similiar0);
                similar0.setImage(similar0IMG);
            }
            if(requestedMovie.similiar1 != null){
                Image similar1IMG = new Image(requestedMovie.similiar1);
                similar1.setImage(similar1IMG);
            }

            //CAST
            if(requestedMovie.cast0 != null){
                Image cast0IMG = new Image(requestedMovie.cast0);
                cast0.setImage(cast0IMG);
            }
            if(requestedMovie.cast1 != null){
                Image cast1IMG = new Image(requestedMovie.cast1);
                cast1.setImage(cast1IMG);
            }
            if(requestedMovie.cast2 != null){
                Image cast2IMG = new Image(requestedMovie.cast2);
                cast2.setImage(cast2IMG);
            }

            //QRCODE
            if (requestedMovie.poster1 != null){
                Image qrcode = new Image(requestedMovie.poster1);
                poster1.setImage(qrcode);
            }

            //If the requested media is ACTOR
            if(requestedMovie.mediatype == 2) {

                ratingProgressBar.setVisible(false);
                trailerButton.setVisible(false);
                similiarLabel.setVisible(false);
                actorLabel.setVisible(false);
                releaseDateLabel.setText("Birthday: ".concat(requestedMovie.birthday));
                if(requestedMovie.deathday!=null) {
                    deathDateLabel.setText("Died on: ".concat(requestedMovie.deathday));
                } else deathDateLabel.setVisible(false);

                String birthplace = "Born in: ".concat(requestedMovie.placeOfBirth);
                birthPlaceLabel.setText(birthplace);
            }
            else {
                birthPlaceLabel.setVisible(false);
                deathDateLabel.setVisible(false);
                releaseDateLabel.setText("Original release date: ".concat(requestedMovie.releaseDate));
                ratingProgressBar.setProgress(requestedMovie.voteAverage);
                String rating = String.format("%.0f", (requestedMovie.voteAverage*100));
                ratingLabel.setText("User score: ".concat(rating.concat("%")) );
            }

        }

    }


    @FXML
    public void backButtonClicked(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("HomeView.fxml"));
        Parent homeViewParent = loader.load();

        Scene homeViewScene = new Scene(homeViewParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

        window.setScene(homeViewScene);
        window.show();
    }


    public void favoritesButtonClicked() {

        String msg = requestedMovie.title;
        PrintWriter printWriter = null;

        /*
         * if file doesn't exist, create favorites.txt file and add movie title.
         * if file exists already, check if movie title is in it.
         * if it's not in it, append it to the end in a new line.
         */
        try {
            if (favoriteList.exists()) {

                if(isFavorite(msg, favoriteList)){
                    if(ConfirmBox.display("Attention!","This is already in your Favorites!\nDo you want to remove it?")){

                        File temp = removeFavorite(favoriteList,msg);
                        if (!favoriteList.delete()) {
                            System.out.println("Favorite list was not deleted!");
                        }
                        temp.renameTo(favoriteList);
                        temp.delete();
                    }
                }
                else {
                    printWriter = new PrintWriter(new FileOutputStream(fileName, true));
                    printWriter.write(msg + newLine);
                    System.out.println("added to file: " + msg);
                    printWriter.flush();
                    printWriter.close();
                }
            }
            else {
                favoriteList.createNewFile();
                System.out.println("new file created");
                printWriter = new PrintWriter(new FileOutputStream(fileName, true));
                printWriter.write(msg + newLine );
            }
        } catch (IOException ioex) {
            ioex.printStackTrace();
        } finally {
            if (printWriter != null) {
                printWriter.flush();
                printWriter.close();
            }
        }
    }

    public File removeFavorite(File file, String title) throws IOException {
        String line;
        File temp = new File(file.getAbsolutePath() + ".tmp");
        PrintWriter pw = new PrintWriter(new FileWriter(temp));
        BufferedReader br = new BufferedReader(new FileReader(file));
        while ((line = br.readLine()) != null) {
            if(line.compareTo(title)!=0) {
                pw.println(line);
                pw.flush();
            }
        }
        pw.close();
        br.close();

        return temp;
    }

    public boolean isFavorite(String title, File file) {

        try {
            String line;
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                if(line.compareTo(title)==0) {
                    br.close();
                    return true;
                }
            }

        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }



    @FXML
    public void playTrailerClicked(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("TrailerView.fxml"));
        Parent trailerViewParent = loader.load();

        Scene trailerViewScene = new Scene(trailerViewParent);
        TrailerViewController controller = loader.getController();

        controller.loadTrailer(requestedMovie.trailer);

        Stage window = new Stage();
        window.setTitle("Trailer:  " + requestedMovie.title);
        window.setScene(trailerViewScene);
        window.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }
}
