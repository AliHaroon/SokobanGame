package Graphics;

import engine.GameEngine;
import engine.GameObject;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * GraphicObject is used to populate the game grid.
 * It creates {@link Rectangle} from {@link GameObject}.
 */
public class GraphicObject {


    public ImageView putObject(GameObject object){
        Image image;
        ImageView iv1;
        switch (object) {
            case WALL:
                image = new Image("Images/wall.png");
                iv1 = new ImageView(image);
                return iv1;



            case CRATE:
                image = new Image("Images/crate.png");
                iv1= new ImageView(image);
                return iv1;


            case DIAMOND:
                image = new Image("Images/daimond.png");
                iv1= new ImageView(image);
                return iv1;

            case KEEPER:
                image = new Image("Images/keeper.png");
                iv1= new ImageView(image);
                return iv1;

            case FLOOR:
                image = new Image("Images/floor.png");
                iv1= new ImageView(image);
                return iv1;

            case CRATE_ON_DIAMOND:
                image = new Image("Images/crateD.png");
                iv1 = new ImageView(image);
                return iv1;

            default:
                String message = "Error in Level constructor. Object not recognized.";
                GameEngine.logger.severe(message);
                throw new AssertionError(message);
        }

    }

}
