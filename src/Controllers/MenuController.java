package Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;

public class MenuController {
    public static Stage PrimaryStage;

    @FXML
    private Pane pane;
    ObservableList<String> levels =
            FXCollections.observableArrayList(
                    "Level 1",
                    "Level 2",
                    "Level 3",
                    "Level 4",
                    "Level 5",
                    "Level 6",
                    "Level 7",
                    "Level 8",
                    "Level 9",
                    "Level 10"

            );
    @FXML
    private ComboBox comboBox;
    @FXML
    private ImageView image;
    @FXML
    private Button b1;

    public void Show(Stage primaryStage, Pane pane) {
        try {

            PrimaryStage = primaryStage;
            this.pane = pane;
            comboBox.setItems(levels);
            comboBox.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
                       SetImage(newValue.toString());
                    }
            );

            Scene scene = new Scene(pane, 1156, 650);
            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.setResizable(false);
            b1.setOnAction(event -> {
                SelectLevel((String) comboBox.getSelectionModel().getSelectedItem());
            });
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void SetImage(String current) {
        String url = "Images/" + current + ".jpg";
        Image imageView = new Image(url);
        image.setImage(imageView);
    }

    private void SelectLevel(String Level){
        try {
            //load the FXML loader
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("../FXML/window.fxml"));

            //load into a pane
            Pane pane = loader.load();

            //Controller linker
            LevelController levelController = loader.getController();
            Scene scene = new Scene(pane);
            scene.getRoot().requestFocus();

            PrimaryStage.setScene(scene);
            PrimaryStage.show();
            PrimaryStage.setResizable(false);
            Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
            PrimaryStage.setX((primScreenBounds.getWidth() - PrimaryStage.getWidth()) / 2);
            PrimaryStage.setY((primScreenBounds.getHeight() - PrimaryStage.getHeight()) / 4);

            levelController.loadLevel(PrimaryStage, Level);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
