package Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class MenuController {
    public static Stage PrimaryStage;

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
    @FXML
    private Button LeaderBoard;
    @FXML
    private javafx.scene.control.Label Label;

    public void Show(Stage primaryStage) {
        try {

            PrimaryStage = primaryStage;
            comboBox.setItems(levels);
            comboBox.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
                       SetImage(newValue.toString());
                    }
            );
            primaryStage.show();
            primaryStage.setResizable(false);
            b1.setOnAction(event -> {
                SelectLevel((String) comboBox.getSelectionModel().getSelectedItem());
            });
            LeaderBoard.setOnAction(event -> showLeader());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void SetImage(String current) {
        String url = "Images/" + current + ".jpg";
        Image imageView = new Image(url);
        image.setImage(imageView);
    }

    private void SelectLevel(String Level){
        try {
            if (Level == null){
                Label.setVisible(true);
                return;
            }
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/FXML/window.fxml"));
            Pane pane = loader.load();
            Scene scene = new Scene(pane);
            scene.getRoot().requestFocus();
            PrimaryStage.setScene(scene);
            LevelController levelController = loader.getController();
            levelController.loadLevel(PrimaryStage, Level);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void showLeader(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("LeaderBoard");
        alert.setHeaderText("Do you wish to Load previous game sessions?");
        alert.setContentText("Make Your choice");

        ButtonType buttonTypeOne = new ButtonType("Yes");
        ButtonType buttonTypeTwo = new ButtonType("No");

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne){
            try {
                File saveFile;
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Save File");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Sokoban save file", "*.skb"));
                saveFile = fileChooser.showOpenDialog(PrimaryStage);
                if (saveFile != null){

                }
            }catch (Exception e){
                e.printStackTrace();
            }

        } else if (result.get() == buttonTypeTwo) {

        }

    }

}
