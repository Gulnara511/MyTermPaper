package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("signIn.fxml"));
        primaryStage.setTitle("Графическое окно");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        //primaryStage.setMaximized(true);

    }


    public static void main(String[] args) {
        launch(args);
    }
}
