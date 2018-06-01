package Controllers;

import engine.GameEngine;
import engine.GameObject;
import engine.Level;
import javafx.scene.control.MenuBar;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import Graphics.GraphicObject;

import java.awt.*;
import java.io.File;
import java.io.InputStream;

public class LevelController {

    public MenuBar menu;
    public GridPane gameGrid;
    private Stage primaryStage;
    private GameEngine gameEngine;
    private File saveFile;

    void loadLevel(Stage primaryStage, String level) {
        this.primaryStage = primaryStage;
        InputStream in = getClass().getClassLoader().getResourceAsStream("Levels/" + level + ".skb");
        initializeGame(in);
        setEventFilter();
    }

    private void initializeGame(InputStream input) {
        gameEngine = new GameEngine(input, true);
        reloadGrid();
    }

    private void setEventFilter() {
        primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            gameEngine.handleKey(event.getCode());
            reloadGrid();
        });
    }

    private void reloadGrid() {
        if (gameEngine.isGameComplete()) {
            // TODO: fix last move of the game.
            //showVictoryMessage();
            return;
        }

        Level currentLevel = gameEngine.getCurrentLevel();
        Level.LevelIterator levelGridIterator = (Level.LevelIterator) currentLevel.iterator();

        gameGrid.getChildren().clear();

        while (levelGridIterator.hasNext()) {
            addObjectToGrid(levelGridIterator.next(), levelGridIterator.getCurrentPosition());
        }

        gameGrid.autosize();
        primaryStage.sizeToScene();
    }

    private void addObjectToGrid(GameObject gameObject, Point location) {
        GraphicObject graphicObject = new GraphicObject(gameObject);
        gameGrid.add(graphicObject, location.y, location.x);
    }

}
