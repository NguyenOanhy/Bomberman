package uet.oop.bomberman.controlSystem;

import javafx.util.Pair;
import uet.oop.bomberman.Map;
import uet.oop.bomberman.entities.*;


import java.util.ArrayList;
import java.util.List;

import static uet.oop.bomberman.graphics.Sprite.SCALED_SIZE;


public class Collision {
    private Map map;
    private BombControl bombControl;
    private EnemyControl enemyControl;
    public static final int FIX = 4;

    public Collision(Map map, BombControl bombControl, EnemyControl enemyControl) {
        this.bombControl = bombControl;
        this.enemyControl = enemyControl;
        this.map = map;
    }

    public BombControl getBombControl() {
        return bombControl;
    }

    public EnemyControl getEnemyControl() {
        return enemyControl;
    }

    public Entity getEntity(int xPos, int yPos) {
        return map.getEntity(xPos, yPos);
    }

    // add direction to the entity to compare to entity1
    public boolean collide(Entity entity, Entity entity1) {
//        System.out.println(entity.getX() + " " + entity.getY());
//        System.out.println(entity1.getX() + " " + entity1.getY());
        ArrayList<Pair<Integer, Integer>> coordinates = new ArrayList<>();
        coordinates.add(new Pair<Integer, Integer>(entity1.getX(), entity1.getY()));
        coordinates.add(new Pair<Integer, Integer>(entity1.getX(), entity1.getY() + SCALED_SIZE));
        coordinates.add(new Pair<Integer, Integer>(entity1.getX() + SCALED_SIZE, entity1.getY()));
        coordinates.add(new Pair<Integer, Integer>(entity1.getX() + SCALED_SIZE, entity1.getY() + SCALED_SIZE));
        return contain(entity, coordinates.get(0))
                || contain(entity, coordinates.get(1))
                || contain(entity, coordinates.get(2))
                || contain(entity, coordinates.get(3));
    }

    public boolean checkCollide(Entity entity, Entity entity1) {
//        System.out.println(entity.getX() + " " + entity.getY());
//        System.out.println(entity1.getX() + " " + entity1.getY());
        ArrayList<Pair<Integer, Integer>> coordinates = new ArrayList<>();
        coordinates.add(new Pair<Integer, Integer>(entity1.getX() + 2 * FIX, entity1.getY() + 2 * FIX));
        coordinates.add(new Pair<Integer, Integer>(entity1.getX() + 2 * FIX, entity1.getY() + SCALED_SIZE - 2 * FIX));
        coordinates.add(new Pair<Integer, Integer>(entity1.getX() + SCALED_SIZE - 2 * FIX, entity1.getY() + 2 * FIX));
        coordinates.add(new Pair<Integer, Integer>(entity1.getX() + SCALED_SIZE - 2 * FIX, entity1.getY() + SCALED_SIZE - 2 * FIX));
        return contain(entity, coordinates.get(0))
                || contain(entity, coordinates.get(1))
                || contain(entity, coordinates.get(2))
                || contain(entity, coordinates.get(3));
    }

    public boolean collide(Entity entity, int x, int y) {
        ArrayList<Pair<Integer, Integer>> coordinates = new ArrayList<>();
        coordinates.add(new Pair<Integer, Integer>(x, y));
        coordinates.add(new Pair<Integer, Integer>(x, y + SCALED_SIZE));
        coordinates.add(new Pair<Integer, Integer>(x + SCALED_SIZE, y));
        coordinates.add(new Pair<Integer, Integer>(x + SCALED_SIZE, y + SCALED_SIZE));
        return contain(entity, coordinates.get(0))
                || contain(entity, coordinates.get(1))
                || contain(entity, coordinates.get(2))
                || contain(entity, coordinates.get(3));
    }

    public boolean contain(Entity entity, Pair<Integer, Integer> point) {
        return (entity.getX() <= point.getKey() &&
                point.getKey() <= entity.getX() + SCALED_SIZE &&
                point.getValue() >= entity.getY() &&
                entity.getY() + SCALED_SIZE >= point.getValue());
    }

    public boolean canMove(int x, int y, int speed, Direction direction) {
        Entity object1;
        Entity object2;
        switch (direction) {
            case UP:
                object1 = map.getEntity(x + FIX, y + speed);
                object2 = map.getEntity(x + SCALED_SIZE - 2 * FIX, y + speed);
//                entity.setY(y + speed);
                break;
            case DOWN:
                object1 = map.getEntity(x + FIX, y + SCALED_SIZE + FIX - speed);
                object2 = map.getEntity(x + SCALED_SIZE - 2 * FIX, y + SCALED_SIZE + FIX - speed);
//                entity.setY(y - speed);
                break;
            case RIGHT:
                object1 = map.getEntity(x + speed + SCALED_SIZE - 2 * FIX, y + 2 * FIX);
                object2 = map.getEntity(x + speed + SCALED_SIZE - 2 * FIX, y + SCALED_SIZE);
//                entity.setX(x + speed);
                break;
            case LEFT:
                object1 = map.getEntity(x - speed, y + 2 * FIX);
                object2 = map.getEntity(x - speed, y + SCALED_SIZE);
//                entity.setX(x - speed);
                break;
            default:
                object1 = map.getEntity(x, y);
                object2 = map.getEntity(x, y);
                break;
        }
        if (!(object2 instanceof Obstacle) && !(object1 instanceof Obstacle)) {
            return true;
        }
        return false;
    }

    public boolean isNextPosBomb(Entity entity, Direction direction, int speed) {
        if (bombControl.getBombList().size() == 0) return false;
        int a = getNextXPos(entity, direction, speed);
        int b = getNextYPos(entity, direction, speed);
        for (int i = 0; i < bombControl.getBombList().size(); i++) {
            if (collide(bombControl.getBombList().get(i), a, b)) return true;
        }
        return false;
    }

    public boolean isNextPosEnemy(Entity entity, Direction direction, int speed) {
        int countDuplicate = 0;
        if (enemyControl.getEnemyList().size() == 0) return false;
        int a = getNextXPos(entity, direction, speed);
        int b = getNextYPos(entity, direction, speed);
        for (int i = 0; i < enemyControl.getEnemyList().size(); i++) {
            if (collide(enemyControl.getEnemyList().get(i), a, b)) countDuplicate++;
            if (countDuplicate > 1) return true;
        }
        return false;
    }

    public int getNextXPos(Entity entity, Direction direction, int speed) {
        int a = entity.getX();
        switch (direction) {
            case LEFT:
                a -= speed;
                break;
            case RIGHT:
                a += speed;
                break;
            default:
                break;
        }
        return a;
    }

    public int getNextYPos(Entity entity, Direction direction, int speed) {
        int b = entity.getY();
        switch (direction) {
            case DOWN:
                b += speed;
                break;
            case UP:
                b -= speed;
                break;
            default:
                break;
        }
        return b;
    }

    public Map getMap() {
        return map;
    }

//    public Pair<Integer, Integer> getBomberMapPos(Entity bomber) {
//        return new Pair<>(bomber.getXMapCoordinate(bomber.getX()), bomber.getYMapCoordinate(bomber.getY()));
//    }

    public List<List<Integer>> formatMapData() {
        List<List<Integer>> formatMap = new ArrayList<>();
        int height = map.getHeight();
        int width = map.getWidth();
        for (int i = 0; i < height; i++) {
            List<Integer> row = new ArrayList<>();
            for (int j = 0; j < width; j++) {
                if (map.getEntityWithMapPos(i, j) instanceof Wall || map.getEntityWithMapPos(i, j) instanceof Brick) {
                    row.add(1);
                } else if (map.getEntityWithMapPos(i, j) instanceof Grass) {
                    row.add(0);
                }
            }
            formatMap.add(row);
        }
        //Todo: Format Enemy Map && BomberPos
        for (Entity entity : map.getEntities()) {
            formatMap.get(entity.getYMapCoordinate(entity.getY())).set(entity.getXMapCoordinate(entity.getX()), 1);
        }
//        for (int i = 0; i < height; i ++) {
//            System.out.println("Line " + i + ": ");
//            for (int j = 0; j < width; j++) {
//                System.out.print(formatMap.get(i).get(j) + " ");
//            }
//            System.out.println("\n");
//        }
        return formatMap;
    }
}
