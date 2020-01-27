package moviedb;


import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

    public class ConfirmBox {

        static boolean answer;

        public static boolean display(String title, String message) {
            Stage window = new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle(title);
            window.setMinWidth(300);
            Label label = new Label();
            label.setText(message);

            Button yesButton = new Button("Yes");
            Button noButton = new Button("No");

            //Clicking will set answer and close window
            yesButton.setOnAction(e -> {
                answer = true;
                window.close();
            });
            noButton.setOnAction(e -> {
                answer = false;
                window.close();
            });

            VBox layout = new VBox(10);

            layout.getChildren().addAll(label, yesButton, noButton);
            layout.setAlignment(Pos.CENTER);
            Scene scene = new Scene(layout);
            window.setScene(scene);
            window.showAndWait();

            return answer;
        }

    }

