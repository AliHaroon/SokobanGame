package Controllers;

import engine.GameEngine;
import engine.GameObject;
import engine.Level;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.MenuBar;
import javafx.scene.effect.Effect;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import Graphics.GraphicObject;
import javafx.scene.image.Image;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class LevelController {

    public MenuBar menu;
    public GridPane gameGrid;
    private Stage primaryStage;
    private GameEngine gameEngine;
    private Level currentLevel;
    @FXML
    private RadioMenuItem radioMenuItem;

    public void loadLevel(Stage primaryStage, String level) {
        this.primaryStage = primaryStage;
        InputStream in = getClass().getClassLoader().getResourceAsStream("Levels/" + level + ".skb");
        initializeGame(in);
    }

    private void initializeGame(InputStream input) {
        gameEngine = new GameEngine(input, true, primaryStage, this);
        currentLevel = gameEngine.getCurrentLevel();
        reloadGrid();
    }

    @FXML
    public void handleKey(KeyEvent event){
        KeyCode KeyPressed = event.getCode();
        gameEngine.handleKey(KeyPressed);
        reloadGrid();
    }

    public void showVictoryMessage(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Congratulations!!");
        alert.setHeaderText("You finished our little game\nhope you liked it");
        alert.setContentText("You can go back and try getting better scores.");
        ButtonType back = new ButtonType("Back to Menu");
        alert.getButtonTypes().setAll(back);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == back){
            showMenu();
        }
    }

    public void reloadGrid() {
        if (gameEngine.isGameComplete()) {
                // TODO: fix last move of the game.
                showVictoryMessage();
                return;
        }
        if (gameEngine.isPlayerPaused())
            gameEngine.playMusic();
        else if(radioMenuItem.isSelected())
            gameEngine.playMusic();
        Level.LevelIterator levelGridIterator = (Level.LevelIterator) currentLevel.iterator();

        gameGrid.getChildren().clear();
        while (levelGridIterator.hasNext()) {
            addObjectToGrid(levelGridIterator.next(), levelGridIterator.getCurrentPosition());
        }

        gameGrid.autosize();
        primaryStage.sizeToScene();
    }

    private void newDialog(String dialogTitle, String dialogMessage, Effect dialogMessageEffect) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setResizable(false);
        dialog.setTitle(dialogTitle);

        Text text1 = new Text(dialogMessage);
        text1.setTextAlignment(TextAlignment.CENTER);
        text1.setFont(javafx.scene.text.Font.font(14));

        if (dialogMessageEffect != null) {
            text1.setEffect(dialogMessageEffect);
        }

        VBox dialogVbox = new VBox(20);
        dialogVbox.setAlignment(Pos.CENTER);
        dialogVbox.setBackground(Background.EMPTY);
        dialogVbox.getChildren().add(text1);

        Scene dialogScene = new Scene(dialogVbox, 350, 150);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    public void toggleMusic(ActionEvent actionEvent) {
        if (radioMenuItem.isSelected()) {
            gameEngine.playMusic();
        } else {
            gameEngine.stopMusic();
        }
    }

    public void showAbout(ActionEvent actionEvent) {
        String title = "About this game";
        String message = "Game created by Ali Haroon and Mahdi Jouni\n Credits to Stefano Frazzetto and Kennly\n for helping to make this game possible";
        newDialog(title, message, null);
    }

    public void showMenu(){
        try {
            gameEngine.stopMusic();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Menu.fxml"));
            Pane pane = loader.load();
            Scene scene = new Scene(pane, 1150, 610);
            scene.getRoot().requestFocus();
            primaryStage.setScene(scene);
            MenuController menuController =  (MenuController) loader.getController();
            menuController.Show(primaryStage);
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void resetLevel(ActionEvent actionEvent){
        gameEngine.stopMusic();
        InputStream in = getClass().getClassLoader().getResourceAsStream("Levels/" + currentLevel.getName() + ".skb");
        initializeGame(in);
    }

    private void addObjectToGrid(GameObject gameObject, Point location) {
        GraphicObject graphicObject = new GraphicObject();
        ImageView imageView = graphicObject.putObject(gameObject);
        GridPane.setRowIndex(imageView, location.x);
        GridPane.setColumnIndex(imageView, location.y);
        gameGrid.getChildren().add(imageView);
    }

}
