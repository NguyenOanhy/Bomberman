package uet.oop.bomberman.entities;

import javafx.scene.image.Image;

public class SpeedItem extends Item {
    public SpeedItem(int xUnit, int yUnit, Image img) {
        super(xUnit, yUnit, img);
    }

    @Override
    public void update() {
    }

    @Override
    public void powerUp(Bomber bomber) {
        bomber.setSpeed(bomber.speed + 1);
    }
}
