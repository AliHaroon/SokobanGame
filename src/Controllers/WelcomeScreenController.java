package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;
import java.io.IOException;

public class WelcomeScreenController {
    private Stage PrimaryStage;
    @FXML
    public ImageView image;

    @FXML
    public void GoToMenu()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Menu.fxml"));
            Pane pane = loader.load();
            MenuController menuController =  (MenuController) loader.getController();
            menuController.Show(PrimaryStage, pane);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    public void OnStartPressed(KeyEvent event){
        String KeyPressed = event.getText();
        if(KeyPressed.equals(" ")) {
            GoToMenu();
        }
    }

    public void setStage(Stage primaryStage)
    {
        this.PrimaryStage = primaryStage;
    }

}
