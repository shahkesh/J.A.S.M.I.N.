package moviedb;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ResourceBundle;

public class HomeViewController implements Initializable {


    @FXML
    private Button searchButton;
    @FXML
    private TextField movieSearchField;
    @FXML private ListView favoriteListView;
    @FXML private TextArea factsTextArea;
    @FXML private Label homeTitle;

    //Trending movies section:
    @FXML private Label popular0Label;
    @FXML private Label popular1Label;
    @FXML private Label popular2Label;
    @FXML private Label popular3Label;
    @FXML private ImageView popular0ImageView;
    @FXML private ImageView popular1ImageView;
    @FXML private ImageView popular2ImageView;
    @FXML private ImageView popular3ImageView;


    public static String query;

    @FXML
    public void searchButtonClicked(ActionEvent event) throws IOException, NullPointerException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("MovieView.fxml"));
        Parent movieViewParent = loader.load();

        Scene movieViewScene = new Scene(movieViewParent);
        MovieViewController controller = loader.getController();
        query = (movieSearchField.getText());

        try {
            connect();

            //access the controller and call a method
            controller.requestedMovie.setMediatype(modus);
            controller.requestedMovie.setStory(story);
            controller.requestedMovie.setReleaseDate(releaseDate);
            controller.requestedMovie.setTitle(title);
            controller.requestedMovie.setPoster(posterURL);
            controller.requestedMovie.setSimiliar0(similar0);
            controller.requestedMovie.setSimiliar1(similar1);
            controller.requestedMovie.setVoteAverage(voteAverage / 10);
            controller.requestedMovie.setTrailer(key);
            controller.requestedMovie.setBirthday(birthday);
            controller.requestedMovie.setDeathday(deathday);
            controller.requestedMovie.setPlaceOfBirth(placeOfBirth);
            controller.requestedMovie.setPoster1(qrcode);
            controller.requestedMovie.setCast0(cast0);
            controller.requestedMovie.setCast1(cast1);
            controller.requestedMovie.setCast2(cast2);

            controller.setMovieObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            controller.requestedMovie.setTitle(title);
            controller.requestedMovie.setStory(story);
            e.printStackTrace();
        }


        //This line gets the Stage information
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        window.setScene(movieViewScene);
        window.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) throws NullPointerException {

        //Show random facts
        try {
            factsTextArea.setText(WebScraper.scrape());
        } catch (IOException e) {
            e.printStackTrace();
        }

        searchButton.setOnAction(e -> {
            try {
                searchButtonClicked(e);
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        });

        //Show favorites
        if(MovieViewController.favoriteList != null){
            try {
                String line;
                BufferedReader br = new BufferedReader(new FileReader(MovieViewController.favoriteList));
                while ((line = br.readLine()) != null) {
                    favoriteListView.getItems().addAll(line);
                }
                br.close();
            }catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        popular0 = popular1 = popular2 = popular3 = null;
        populartitle0 = populartitle1 = populartitle2 = populartitle3 = "";

        //Show trending movies
        String popular = "https://api.themoviedb.org/3/movie/popular?api_key=99e4eceeea98daedf8ab2ca3ff2a53ab";

        try {
            generatePopular(popular);
            fillPopular();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Funktion um Popular Movies zu befüllen
    public void fillPopular(){

        if(popular0 != null && populartitle0.compareTo("") != 0){
            Image popular0IMG = new Image(popular0);
            popular0ImageView.setImage(popular0IMG);
            popular0Label.setText(populartitle0);
        }
        if(popular1 != null && populartitle1.compareTo("") != 0){
            Image popular1IMG = new Image(popular1);
            popular1ImageView.setImage(popular1IMG);
            popular1Label.setText(populartitle1);
        }
        if(popular2 != null && populartitle2.compareTo("") != 0){
            Image popular2IMG = new Image(popular2);
            popular2ImageView.setImage(popular2IMG);
            popular2Label.setText(populartitle2);
        }
        if(popular3 != null && populartitle3.compareTo("") != 0){
            Image popular3IMG = new Image(popular3);
            popular3ImageView.setImage(popular3IMG);
            popular3Label.setText(populartitle3);
        }
    }

    HttpURLConnection con = null;
    public static String title , story, releaseDate, birthday, deathday, placeOfBirth;
    public static String posterURL, qrcode, similar0, similar1,cast0,cast1,cast2, key;
    public static String popular0,popular1,popular2,popular3, populartitle0,populartitle1,populartitle2,populartitle3;
    public static Double voteAverage = 0.0;
    public static Long id;
    public static int modus;



    public void connect() throws IOException, IndexOutOfBoundsException,NullPointerException {

        title = story = releaseDate = birthday = deathday = placeOfBirth = posterURL = qrcode = similar0 = similar1 = null;
        cast0 = cast1 = cast2 = key = null;
        id = null;
        voteAverage = 0.0;
        modus = 0;

        query = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());

        String foto = "https://image.tmdb.org/t/p/w600_and_h900_bestv2";
        posterURL = similar0 = similar1 = cast0 = cast1 = cast2 = foto;

        String absolute = "https://api.themoviedb.org/3/search/multi?api_key=99e4eceeea98daedf8ab2ca3ff2a53ab";//multi oder tv oder movie
        absolute = absolute.concat("&query=").concat(query);


        if (absolute.compareTo("https://api.themoviedb.org/3/search/movie?api_key=99e4eceeea98daedf8ab2ca3ff2a53ab&query=") == 0) {
            return;
        }


        try{

            URL url = new URL(absolute);

            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();


            if (con.getResponseCode() == 200) {

                InputStreamReader reader = null;
                BufferedReader buffer = null;
                reader = new InputStreamReader(con.getInputStream());
                buffer = new BufferedReader(reader);
                String value;


                while ((value = buffer.readLine()) != null) {

                    String vote;

                    JSONParser parse = new JSONParser();
                    JSONObject jobj = null;
                    try {
                        jobj = (JSONObject) parse.parse(value);
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }

                    JSONArray jsonarr_1 = (JSONArray) jobj.get("results");
                    JSONObject jsonobj_1 = (JSONObject) jsonarr_1.get(0);
                    String mediatype = ((String) jsonobj_1.get("media_type"));

                    switch (mediatype) {
                        case "tv":
                            modus = 1;
                            break;
                        case "person":
                            modus = 2;
                            break;
                        default:
                            modus = 3;
                            break;
                    }

                    //TV_SERIEN
                    if (modus == 1) {

                        id = ((Long) jsonobj_1.get("id"));
                        story = ((String) jsonobj_1.get("overview"));
                        title = ((String) jsonobj_1.get("original_name"));
                        releaseDate = ((String) jsonobj_1.get("first_air_date"));
                        vote = (jsonobj_1.get("vote_average").toString());
                        voteAverage = Double.parseDouble(vote);

                        try{
                            posterURL = posterURL.concat((String) jsonobj_1.get("poster_path"));
                        }catch (NullPointerException ex){
                            posterURL = posterURL.concat("/nothing");
                        }

                        String detailsTV = "https://api.themoviedb.org/3/tv/".concat(id.toString()) + "?api_key=99e4eceeea98daedf8ab2ca3ff2a53ab";
                        generateQR(detailsTV);

                        String similiarTV = "https://api.themoviedb.org/3/tv/".concat(id.toString())+"/similar?api_key=99e4eceeea98daedf8ab2ca3ff2a53ab";
                        try{
                            getSimilar(similiarTV);
                        }catch (IndexOutOfBoundsException ex){
                            similar0 = null;
                            similar1 = null;
                        }

                        String cast = "https://api.themoviedb.org/3/tv/".concat(id.toString())+"/credits?api_key=99e4eceeea98daedf8ab2ca3ff2a53ab";
                        generateCast(cast);

                    }//ENDE MODUS 1

                    //SCHAUSPIELER
                    if (modus == 2) {

                        id = ((Long) jsonobj_1.get("id"));
                        title = ((String) jsonobj_1.get("name"));
                        posterURL = posterURL.concat((String) jsonobj_1.get("profile_path"));

                        //ANFANG Details Person
                        String person = "https://api.themoviedb.org/3/person/" + id + "?api_key=99e4eceeea98daedf8ab2ca3ff2a53ab";

                        HttpURLConnection conPerson;
                        URL urlPerson = new URL(person);
                        conPerson = (HttpURLConnection) urlPerson.openConnection();
                        conPerson.setRequestMethod("GET");
                        conPerson.connect();
                        if (conPerson.getResponseCode() == 200) {
                            InputStreamReader readerPerson = null;
                            BufferedReader bufferPerson = null;
                            readerPerson = new InputStreamReader(conPerson.getInputStream());
                            bufferPerson = new BufferedReader(readerPerson);
                            String valuePerson;
                            while ((valuePerson = bufferPerson.readLine()) != null) {

                                JSONParser parsePerson = new JSONParser();
                                JSONObject jobjPerson = null;
                                try {
                                    jobjPerson = (JSONObject) parsePerson.parse(valuePerson);
                                } catch (ParseException ex) {
                                    ex.printStackTrace();
                                }
                                releaseDate = ((String) jobjPerson.get("birthday"));
                                birthday = releaseDate;
                                deathday = ((String) jobjPerson.get("deathday"));
                                placeOfBirth = ((String) jobjPerson.get("place_of_birth"));
                                story = ((String) jobjPerson.get("biography"));

                                String homepage = ((String) jobjPerson.get("homepage"));
                                if (homepage == null) {
                                    homepage = "not found";
                                }else if(homepage.compareTo("") == 0){
                                    homepage = "not found";
                                }else{

                                    File file = new File("MyQRCode.jpeg");

                                    QRCodeWriter qrCodeWriter = new QRCodeWriter();
                                    BitMatrix bitMatrix = null;
                                    try {
                                        bitMatrix = qrCodeWriter.encode(homepage, BarcodeFormat.QR_CODE, 250, 250);
                                    } catch (WriterException ex) {
                                        ex.printStackTrace();
                                    }
                                    Path path = FileSystems.getDefault().getPath(file.toString());
                                    MatrixToImageWriter.writeToPath(bitMatrix, "JPEG", path);
                                    qrcode = "file:".concat(file.toString());
                                }
                            }
                        }
                    }//ENDE MODUS 2

                    //FILME
                    if (modus == 3){

                        id = ((Long) jsonobj_1.get("id"));
                        title = ((String) jsonobj_1.get("title"));
                        story = ((String) jsonobj_1.get("overview"));
                        releaseDate = ((String) jsonobj_1.get("release_date"));
                        vote = (jsonobj_1.get("vote_average").toString());
                        voteAverage = Double.parseDouble(vote);

                        try{
                            posterURL = posterURL.concat((String) jsonobj_1.get("poster_path"));
                        }catch (NullPointerException ex){
                            posterURL = posterURL.concat("/nothing");
                        }

                        //ANFANG TRAILER
                        String trailer = "https://api.themoviedb.org/3/movie/".concat(id.toString()) + "/videos?api_key=99e4eceeea98daedf8ab2ca3ff2a53ab";

                        HttpURLConnection conMovie ;
                        URL urlMovie = new URL(trailer);
                        conMovie = (HttpURLConnection) urlMovie.openConnection();
                        conMovie.setRequestMethod("GET");
                        conMovie.connect();

                        if (conMovie.getResponseCode() == 200) {
                            InputStreamReader readerMovie;
                            BufferedReader bufferMovie;
                            readerMovie = new InputStreamReader(conMovie.getInputStream());
                            bufferMovie = new BufferedReader(readerMovie);
                            String valueMovie;
                            while ((valueMovie = bufferMovie.readLine()) != null) {

                                JSONParser parseMovie = new JSONParser();
                                JSONObject jobjMovie = null;
                                try {
                                    jobjMovie = (JSONObject) parseMovie.parse(valueMovie);
                                } catch (ParseException ex) {
                                    ex.printStackTrace();
                                }
                                JSONArray jsonarr_Movie = (JSONArray) jobjMovie.get("results");

                                JSONObject jsonobj_Movie2;
                                if (jsonarr_Movie.isEmpty() == false) {
                                    jsonobj_Movie2 = (JSONObject) jsonarr_Movie.get(0);

                                    key = ((String) jsonobj_Movie2.get("key"));
                                    key = "https://www.youtube.com/watch?v=".concat(key);
                                }
                            }
                        }
                        //ENDE TRAILER

                        String detailsMV = "https://api.themoviedb.org/3/movie/".concat(id.toString()) + "?api_key=99e4eceeea98daedf8ab2ca3ff2a53ab";
                        generateQR(detailsMV);

                        String similar = "https://api.themoviedb.org/3/movie/".concat(id.toString())+"/similar?api_key=99e4eceeea98daedf8ab2ca3ff2a53ab";
                        try{
                            getSimilar(similar);
                        }catch (IndexOutOfBoundsException ex){
                            similar0 = null;
                            similar1 = null;
                        }

                        String cast = "https://api.themoviedb.org/3/movie/".concat(id.toString())+"/credits?api_key=99e4eceeea98daedf8ab2ca3ff2a53ab";
                        generateCast(cast);

                    }//ENDE MODUS 3

                }//klammer while buffer
            }//klammer für response 200

            if (con.getResponseCode() == 400) {
                System.out.println("Server did not answer");
            }

        } catch (IOException e) {
            e.printStackTrace();

        } catch (IndexOutOfBoundsException e) {
            title = "TITLE NOT FOUND";

        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void generateCast(String link) throws IOException {

        HttpURLConnection con = null;
        URL url = new URL(link);
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.connect();
        if (con.getResponseCode() == 200) {
            InputStreamReader reader = null;
            BufferedReader buffer = null;
            reader = new InputStreamReader(con.getInputStream());
            buffer = new BufferedReader(reader);
            String value;
            while ((value = buffer.readLine()) != null) {

                JSONParser parse = new JSONParser();
                JSONObject jobj = null;
                try {
                    jobj = (JSONObject) parse.parse(value);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                JSONArray jsonarr= (JSONArray) jobj.get("cast");

                for (int i = 0; i < 3; i++) {

                    if (jsonarr.size() == 0){
                        break;
                    }
                    JSONObject jsonobj= (JSONObject) jsonarr.get(i);

                    if (i == 0) {
                        String puffer=((String) jsonobj.get("profile_path"));
                        if(puffer != null){
                            cast0 = cast0.concat((String) jsonobj.get("profile_path"));
                        }
                        if (jsonarr.size() == 1){
                            break;
                        }
                    }
                    if (i == 1) {
                        String puffer=((String) jsonobj.get("profile_path"));
                        if(puffer != null){
                            cast1 = cast1.concat((String) jsonobj.get("profile_path"));
                        }
                    }
                    if (i == 2) {
                        String puffer=((String) jsonobj.get("profile_path"));
                        if(puffer != null){
                            cast2 = cast2.concat((String) jsonobj.get("profile_path"));
                        }
                    }
                }
            }
        }
    }

    public void generatePopular(String link) throws IOException,NullPointerException {

        String foto = "https://image.tmdb.org/t/p/w600_and_h900_bestv2";
        popular0 = popular1 = popular2 = popular3 = foto;

        HttpURLConnection con = null;
        URL url = new URL(link);
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.connect();
        if (con.getResponseCode() == 200) {
            InputStreamReader reader = null;
            BufferedReader buffer = null;
            reader = new InputStreamReader(con.getInputStream());
            buffer = new BufferedReader(reader);
            String value;
            while ((value = buffer.readLine()) != null) {

                JSONParser parse = new JSONParser();
                JSONObject jobj = null;
                try {
                    jobj = (JSONObject) parse.parse(value);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }

                JSONArray jsonarr = (JSONArray) jobj.get("results");
                JSONObject jsonobj = null;

                for (int i = 0; i < jsonarr.size(); i++) {

                    try {
                        jsonobj= (JSONObject) jsonarr.get(i);
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }

                    if (i == 0) {
                        popular0 = popular0.concat((String) jsonobj.get("poster_path"));
                        populartitle0 = populartitle0.concat((String) jsonobj.get("original_title"));
                    }
                    if (i == 1) {
                        popular1 = popular1.concat((String) jsonobj.get("poster_path"));
                        populartitle1 = populartitle1.concat((String) jsonobj.get("original_title"));
                    }
                    if (i == 2){
                        popular2 = popular2.concat((String) jsonobj.get("poster_path"));
                        populartitle2 = populartitle2.concat((String) jsonobj.get("original_title"));
                    }
                    if (i == 3){
                        popular3 = popular3.concat((String) jsonobj.get("poster_path"));
                        populartitle3 = populartitle3.concat((String) jsonobj.get("original_title"));
                        break;
                    }
                }
            }
        }
    }

    public void generateQR(String link) throws IOException {

        HttpURLConnection con = null;
        URL url = new URL(link);
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.connect();
        if (con.getResponseCode() == 200) {
            InputStreamReader reader = null;
            BufferedReader buffer = null;
            reader = new InputStreamReader(con.getInputStream());
            buffer = new BufferedReader(reader);
            String value;
            while ((value = buffer.readLine()) != null) {

                JSONParser parse = new JSONParser();
                JSONObject jobj = null;
                try {
                    jobj = (JSONObject) parse.parse(value);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                String homepage = ((String) jobj.get("homepage"));

                if (homepage == null) {
                    homepage = "not found";
                }else if(homepage.compareTo("") == 0){
                    homepage = "not found";
                }else{

                    File file = new File("MyQRCode.jpeg");

                    QRCodeWriter qrCodeWriter = new QRCodeWriter();
                    BitMatrix bitMatrix = null;
                    try {
                        bitMatrix = qrCodeWriter.encode(homepage, BarcodeFormat.QR_CODE, 250, 250);
                    } catch (WriterException ex) {
                        ex.printStackTrace();
                    }
                    Path path = FileSystems.getDefault().getPath(file.toString());
                    MatrixToImageWriter.writeToPath(bitMatrix, "JPEG", path);
                    qrcode = "file:".concat(file.toString());
                    //ENDE QR
                }
            }
        }
    }

    public void getSimilar(String link) throws IOException {

        HttpURLConnection conSIM = null;
        URL urlSIM = new URL(link);
        conSIM = (HttpURLConnection) urlSIM.openConnection();
        conSIM.setRequestMethod("GET");
        conSIM.connect();
        if (conSIM.getResponseCode() == 200) {
            InputStreamReader readerSIM = null;
            BufferedReader bufferSIM = null;
            readerSIM = new InputStreamReader(conSIM.getInputStream());
            bufferSIM = new BufferedReader(readerSIM);
            String valueSIM;
            while ((valueSIM = bufferSIM.readLine()) != null) {

                JSONParser parseSIM = new JSONParser();
                JSONObject jobjSIM = null;
                try {
                    jobjSIM = (JSONObject) parseSIM.parse(valueSIM);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                JSONArray jsonarrSIM = (JSONArray) jobjSIM.get("results");

                for (int i = 0; i < jsonarrSIM.size(); i++) {
                    JSONObject jsonobj_SIM = (JSONObject) jsonarrSIM.get(i);

                    if (i == 0) {
                        String puffer = (String) jsonobj_SIM.get("poster_path");
                        if(puffer != null){
                            similar0 = similar0.concat(puffer);
                        }else{
                            similar0 = null;
                        }
                    }
                    if (i == 1) {
                        String puffer = (String) jsonobj_SIM.get("poster_path");
                        if(puffer != null){
                            similar1 = similar1.concat(puffer);
                        }else{
                            similar1 = null;
                        }
                        break;
                    }
                }
            }
        }
    }
}

