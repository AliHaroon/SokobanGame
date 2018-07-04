package engine;

import Controllers.LevelController;
import Controllers.Main;
import Controllers.MenuController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.effect.Effect;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import javax.sound.sampled.LineUnavailableException;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.List;

/**
 * GameEngine is responsible for handling all the game mechanics.
 *
 * @author Stefano Frazzetto
 * @version 2.0.0
 */
public class GameEngine {
    /**
     * The system logger
     */
    public static GameLogger logger;
    /**
     * The Primary Stage
     */
    public Stage primaryStage;
    /**
     * The game debug mode
     */
    private static boolean debug = false;
    /**
     * The current level displayed in the game
     */
    private Level currentLevel;
    /**
     * The game state
     */
    private boolean gameComplete = false;
    /**
     * The number of moves
     */
    private int movesCount = 0;
    /**
     * Name of current level
     */
    private String levelName;
    /**
     * The music player
     */
    private MediaPlayer player;

    private LevelController parent;

    private MenuController menu;

    /**
     * Uses a {@link File} to load the game map containing all the levels.
     *
     * @param input the file containing the game levels.
     * @param production true if using the engine in live mode, false
     *                   only for testing mode.
     */
    public GameEngine(InputStream input, boolean production, Stage primarystage, LevelController controller) {
        try {
            // Initialize the logger
            logger = new GameLogger();
            parent = controller;
            primaryStage = primarystage;
            levelName = loadGameFile(input);

            if (production) {
                createPlayer();
            }
        } catch (IOException x) {
            System.out.println("Cannot create logger.");
        } catch (NoSuchElementException e) {
            logger.warning("Cannot load the default save file: " + Arrays.toString(e.getStackTrace()));
        } catch (LineUnavailableException e) {
            logger.warning("Cannot load the music file: " + Arrays.toString(e.getStackTrace()));
        }
    }

    /**
     * Checks if the debug mode is active.
     *
     * @return boolean true if the debug mode is active, false otherwise
     */
    public static boolean isDebugActive() {
        return debug;
    }

    public int getMovesCount() {
        return movesCount;
    }

    /**
     * Handles the action that should be executed when a specific
     * keyboard key {@link KeyCode} is pressed.
     *
     * @param code the keyboard key code.
     */
    public void handleKey(KeyCode code) {
        switch (code) {
            case UP:
                move(new Point(-1, 0));
                break;

            case RIGHT:
                move(new Point(0, 1));
                break;

            case DOWN:
                move(new Point(1, 0));
                break;

            case LEFT:
                move(new Point(0, -1));
                break;

            default:
                // TODO: implement something funny.
        }

        if (isDebugActive()) {
            System.out.println(code);
        }
    }

    /**
     * Handles the movement of the keeper and the objects that collide with it
     *
     * @param delta - the movement delta
     */
    private void move(Point delta) {
        // Prevent the player from moving if the game is complete.
        if (isGameComplete()) {
            return;
        }

        Point keeperPosition = currentLevel.getKeeperPosition();
        // Check what kind of object is located at target
        GameObject keeper = currentLevel.getObjectAt(keeperPosition);
        Point targetObjectPoint = GameGrid.translatePoint(keeperPosition, delta);
        GameObject keeperTarget = currentLevel.getObjectAt(targetObjectPoint);

        // Print useful information if the debug mode is active.
        if (GameEngine.isDebugActive()) {
            System.out.println("Current level state:");
            System.out.println(currentLevel.toString());
            System.out.println("Keeper pos: " + keeperPosition);
            System.out.println("Movement source obj: " + keeper);
            System.out.printf("Target object: %s at [%s]", keeperTarget, targetObjectPoint);
        }

        boolean keeperMoved = false;

        // Check keeper target
        switch (keeperTarget) {

            case WALL:
                // Cannot move
                break;

            case CRATE:
                // Get the object located at delta from this crate
                GameObject crateTarget = currentLevel.getTargetObject(targetObjectPoint, delta);

                // If the crate target is not FLOOR, the crate cannot be moved
                if (crateTarget == GameObject.FLOOR) {
                    currentLevel.moveGameObjectBy(keeperTarget, targetObjectPoint, delta);
                    currentLevel.moveGameObjectBy(keeper, keeperPosition, delta);
                    keeperMoved = true;
                }
                break;

            case FLOOR:
                currentLevel.moveGameObjectBy(keeper, keeperPosition, delta);
                keeperMoved = true;
                break;

            default:
                logger.severe("The object to be moved was not a recognised GameObject.");
                throw new AssertionError("This should not have happened. Report this problem to the developer.");
        }

        if (keeperMoved) {
            keeperPosition.translate((int) delta.getX(), (int) delta.getY());
            movesCount++;
            boolean check = currentLevel.isComplete();
            if (check) {
                parent.reloadGrid();
                showMessage();
            }
        }
    }

    /**
     * Loads a game file creating a {@link List} of {@link Level}s.
     *
     * @param input - the file containing the levels
     * @return the list containing the levels
     */
    private String loadGameFile(InputStream input) {
        String levelName = "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            List<String> rawLevel = new ArrayList<>();
            // START - While loop
            while (true) {
                String line = reader.readLine();
                // Break the loop if EOF is reached
                if (line == null) {
                    if (rawLevel.size() != 0) {
                        currentLevel = new Level(levelName, rawLevel);
                    }
                    break;
                }
                if (line.contains("Level")){
                    levelName = line;
                    continue;
                }
                  rawLevel.add(line);
            } // END - While loop

        } catch (IOException e) {
            logger.severe("Error trying to load the game file: " + e);
        } catch (NullPointerException e) {
            logger.severe("Cannot open the requested file: " + e);
        }
        return levelName;
    }

    /**
     * Returns true if the game is complete.
     *
     * @return true if the game is complete, false otherwise
     */
    public boolean isGameComplete() {
        return gameComplete;
    }

    /**
     * Creates the player object loading the music file.
     *
     * @throws LineUnavailableException if the file is not available.
     */
    private void createPlayer() throws LineUnavailableException {
        try {
            Media music = new Media(Main.class.getResource("/Sounds/puzzle_theme.mp3").toURI().toString());
            //Media crateMoved = new Media(this.getClass().getResource(""))
            player = new MediaPlayer(music);
            player.setOnEndOfMedia(() -> player.seek(Duration.ZERO));
        }catch (URISyntaxException e){
            e.printStackTrace();
        }

    }

    /**
     * Starts playing music.
     */
    public void playMusic() {
       player.play();
    }

    /**
     * Stops playing music.
     */
    public void stopMusic() {
        player.stop();
    }

    public boolean isPlayerPaused(){
       return player.getStatus() == MediaPlayer.Status.PAUSED;
    }

    public void resumeMusic(){
        player.play();
    }
    /**
     * Returns true if the player is playing music.
     *
     * @return true if playing music, false otherwise.
     */
    public boolean isPlayingMusic() {
        return player.getStatus() == MediaPlayer.Status.PLAYING;
    }

    /**
     * Returns the next level in the list of levels.
     *
     * @return the next level loaded from the save file.
     */
   private Level getNextLevel(String levelName) {
       String parts = levelName.substring(levelName.lastIndexOf(" ") + 1);
       int nextLevel = Integer.parseInt(parts) + 1;
       String use = "Level " + nextLevel;
       try {
           if(nextLevel == 11) {
               gameComplete = true;
               parent.reloadGrid();
           }
          parent.loadLevel(primaryStage,use);
       }catch (Exception e){
           e.printStackTrace();
       }


        return null;
    }

    /**
     * Returns the current level.
     *
     * @return the current level.
     */
    public Level getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Toggles the debug mode.
     */
    public void toggleDebug() {
        debug = !debug;
    }

    public void showMessage(){
        newDialog();
    }

    private void newDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Good Job!!");
        alert.setHeaderText("What do you wish to do ?");
        alert.setContentText("Choose your option.");

        ButtonType buttonTypeOne = new ButtonType("Back to Menu");
        ButtonType buttonTypeTwo = new ButtonType("Next Level");

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne){
            try {
                stopMusic();
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

        } else if (result.get() == buttonTypeTwo) {
            player.pause();
            getNextLevel(levelName);
        }

    }

}