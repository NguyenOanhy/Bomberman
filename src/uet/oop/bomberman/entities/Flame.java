package uet.oop.bomberman.entities;

import javafx.scene.image.Image;
import uet.oop.bomberman.controlSystem.Direction;
import uet.oop.bomberman.controlSystem.Timer;

import static uet.oop.bomberman.graphics.Sprite.*;

public class Flame extends Entity implements Obstacle {
    public enum TYPE {
        BRICK, LAST, BODY
    }

    private final TYPE type;
    private final Direction direction;
    private boolean exploded;
    private final long timeSet;
    private final long limit;
    private final int time = 100;

    private int count = 0;

    public Flame(int xUnit, int yUnit, Direction direction, TYPE type) {
        super(xUnit, yUnit);
        this.direction = direction;
        this.type = type;
        this.setImg(getImg());
        exploded = false;
        timeSet = Timer.now();
        limit = 70000000;
    }

    @Override
    public void update() {
        if (Timer.now() - timeSet > limit) exploded = true;
        count++;
        img = getImg();
    }

    public boolean isExploded() {
        return exploded;
    }

    public Image getImg() {
        switch (type) {
            case BODY:
                switch (direction) {
                    case LEFT:
                    case RIGHT:
                        return movingSprite(explosion_horizontal2, explosion_horizontal1,
                                explosion_horizontal, count, time).getFxImage();
                    case DOWN:
                    case UP:
                        return movingSprite(explosion_vertical2, explosion_vertical1,
                                explosion_vertical, count, time).getFxImage();
                    case CENTER:
                        return movingSprite(bomb_exploded2, bomb_exploded1, bomb_exploded,
                                count, time).getFxImage();
                }
            case LAST:
                switch (direction) {
                    case LEFT:
                        return movingSprite(explosion_horizontal_left_last2, explosion_horizontal_left_last1,
                                explosion_horizontal_left_last, count, time).getFxImage();
                    case RIGHT:
                        return movingSprite(explosion_horizontal_right_last2, explosion_horizontal_right_last1,
                                explosion_horizontal_right_last, count, time).getFxImage();
                    case DOWN:
                        return movingSprite(explosion_vertical_down_last2, explosion_vertical_down_last1,
                                explosion_vertical_down_last, count, time).getFxImage();
                    case UP:
                        return movingSprite(explosion_vertical_top_last2, explosion_vertical_top_last1,
                                explosion_vertical_top_last, count, time).getFxImage();
                    case CENTER:
                        return movingSprite(bomb_exploded2, bomb_exploded1, bomb_exploded,
                                count, time).getFxImage();
                }
            case BRICK:
                return movingSprite(brick_exploded, brick_exploded1,
                        brick_exploded2, count, time).getFxImage();
        }
        return null;
    }
}
