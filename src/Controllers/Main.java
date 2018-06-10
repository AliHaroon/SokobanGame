package Controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private Stage PrimaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.PrimaryStage = primaryStage;
            mainWindow();
    }

    public void mainWindow(){
        try{
            //load the FXML loader
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("../FXML/WelcomeScreen.fxml"));

            //load into a pane
            Pane pane = loader.load();

            //Welcome Screen Controller linker
            WelcomeScreenController welcomeScreenController = loader.getController();

            //Show the menu pane
            welcomeScreenController.setStage(PrimaryStage);

            Scene scene = new Scene(pane);
            scene.getRoot().requestFocus();
            PrimaryStage.setScene(scene);
            PrimaryStage.show();
            PrimaryStage.setResizable(false);

        }
        catch (IOException | NullPointerException | IllegalStateException ex){
                ex.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
