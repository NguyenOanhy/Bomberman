package uet.oop.bomberman;

import javafx.util.Pair;
import uet.oop.bomberman.controlSystem.*;
import uet.oop.bomberman.controlSystem.Timer;
import uet.oop.bomberman.entities.*;
import uet.oop.bomberman.graphics.Sprite;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

import static uet.oop.bomberman.graphics.Sprite.*;

public class Map {
    protected List<List<Entity>> map;
    protected int[][] codeList;
    protected int height;
    protected int width;
    protected int level;

    protected int numberBomberDie = 0;
    protected int numberBomberLife = 3;

    protected int startXPos = 1;
    protected int startYPos = 1;
    protected long time_begin;

    protected boolean isWin = false;

    public void setIsWin(boolean is) {
        isWin = is;
    }

    public boolean getIsWin() {
        return isWin;
    }

    protected Camera camera;
    public static List<Entity> entities;
    public static Collision collision;
    public static BombControl bombControl;
    public static EnemyControl enemyControl;

    public Map(int level, KeyListener keyListener, boolean isContinue) {
        createMap(level, keyListener, isContinue);
    }

    public Map() {

    }

    public void createMap(int level, KeyListener keyListener, boolean isContinue) {
        map = new ArrayList<>();
        entities = new ArrayList<>();
        this.level = level;
        time_begin = Timer.now();

        numberBomberDie = 0;
        numberBomberLife = 3;

        List<Pair<Integer, Integer>> balloomPos = new ArrayList<>();
        List<Pair<Integer, Integer>> onealPos = new ArrayList<>();
        List<Pair<Integer, Integer>> dollPos = new ArrayList<>();

        // Read a map file
        Path path = Paths.get("").toAbsolutePath();
        File file = new File(path.normalize() + "/res/levels/Level" + level + ".txt");

        // Check if press Continue Old Game
        if (isContinue) {
            try {
                file = new File("SaveData.txt");
            } catch (Exception e) {
                System.out.println("Read File Error");
            }
        }

        // Read file to create map
        try {
            Scanner scanner = new Scanner(file);

            this.level = scanner.nextInt();
            height = scanner.nextInt();
            width = scanner.nextInt();

            codeList = new int[height][width];
            scanner.nextLine();

            for (int i = 0; i < height; i++) {
                String tempStr = scanner.nextLine();
                List<Entity> tempList = new ArrayList<>();
                for (int j = 0; j < width; j++) {
                    switch (tempStr.charAt(j)) {
                        case 'p':
                            startXPos = j;
                            startYPos = i;
                            tempList.add(new Grass(j, i, Sprite.grass.getFxImage()));
                            break;
                        case '#':
                            tempList.add(new Wall(j, i, Sprite.wall.getFxImage()));
                            break;
                        case '*':
                            tempList.add(new Brick(j, i, Sprite.brick.getFxImage()));
                            break;
                        case 'x':
                            tempList.add(new Brick(j, i, Sprite.brick.getFxImage()));
                            codeList[i][j] = Portal.code;
                            break;
                        case 'y':
                            tempList.add(new Portal(j, i, portal.getFxImage()));
                            codeList[i][j] = Portal.code;
                            break;
                        case '1':
                            tempList.add(new Grass(j, i, Sprite.grass.getFxImage()));
                            balloomPos.add(new Pair<>(j, i));
                            break;
                        case '2':
                            tempList.add(new Grass(j, i, Sprite.grass.getFxImage()));
                            onealPos.add(new Pair<>(j, i));
                            break;
                        case '3':
                            tempList.add(new Grass(j, i, Sprite.grass.getFxImage()));
                            dollPos.add(new Pair<>(j, i));
                            break;
                        case 'f':
                            tempList.add(new FlameItem(j, i, powerup_flames.getFxImage()));
                            break;
                        case 'b':
                            tempList.add(new BombItem(j, i, powerup_bombs.getFxImage()));
                            break;
                        case 's':
                            tempList.add(new SpeedItem(j, i, powerup_speed.getFxImage()));
                            break;
                        case 'm':
                            tempList.add(new BombPassItem(j, i, powerup_bombpass.getFxImage()));
                            break;
                        default:
                            tempList.add(new Grass(j, i, Sprite.grass.getFxImage()));
                            break;
                    }
                }
                map.add(tempList);
            }
            scanner.close();
        } catch (FileNotFoundException exception) {
            System.out.println(exception.getMessage());
        }

        // generate camera, bombControl, enemyControl
        camera = new Camera(0, 0, width, height);
        bombControl = new BombControl();
        enemyControl = new EnemyControl();

        int bomberSpeed = 2;
        int bomberNumberOfBombs = 1;
        int bomberHasJustSetBomb = 0;
        int bomberPower = 1;
        int bomberLives = 3;
        long bomberTimeRemain = 180;
        if (isContinue) {
            // generate other entities if press continue game.

            try {

                Scanner scanner = new Scanner(file);
                this.level = scanner.nextInt();
                height = scanner.nextInt();
                width = scanner.nextInt();

                scanner.nextLine();
                for (int i = 0; i < height; i++) {
                    String tempStr = scanner.nextLine();
                    for (int j = 0; j < width; j++) {
                        if (tempStr.charAt(j) == '!') {
                            Bomb bomb = new Bomb(j, i, Sprite.bomb.getFxImage(), 0);
                            bombControl.addBomb(bomb);
                        }
                    }
                }

                //get bomber information back.
                bomberSpeed = scanner.nextInt();
                bomberNumberOfBombs = scanner.nextInt();
                bomberHasJustSetBomb = scanner.nextInt();
                bomberPower = scanner.nextInt();
                bomberLives = scanner.nextInt();
                bomberTimeRemain = scanner.nextLong();

                for (int i = 0; i < bombControl.getBombList().size(); i++) {
                    int x, y;
                    x = scanner.nextInt();
                    y = scanner.nextInt();
                    int count = scanner.nextInt();
                    if (bombControl.getBomb(y, x) == null)
                        System.out.println("Wrong index");
                    bombControl.getBomb(y, x).setCount(count);
                }

                scanner.close();
            } catch (FileNotFoundException exception) {
                System.out.println(exception.getMessage());
            }
            if (bomberHasJustSetBomb == 1) {
                bombControl.setHasJustSetBomb(true);
            } else {
                bombControl.setHasJustSetBomb(false);
            }
            bombControl.setNumberOfBomb(bomberNumberOfBombs);
            bombControl.setPower(bomberPower);
        }

        collision = new Collision();

        Entity bomberman = new Bomber(startXPos, startYPos, Sprite.player_right.getFxImage(),
                keyListener, bomberSpeed, bomberLives, bomberTimeRemain);
        entities.add(bomberman);

        for (Pair<Integer, Integer> pos : balloomPos) {
            Enemy balloom = new Balloom(pos.getKey(), pos.getValue(), Sprite.balloom_right1.getFxImage(), false);
            enemyControl.addEnemy(balloom);
        }

        for (Pair<Integer, Integer> pos : onealPos) {
            Enemy oneal = new Oneal(pos.getKey(), pos.getValue(), Sprite.oneal_right1.getFxImage());
            enemyControl.addEnemy(oneal);
        }

        for (Pair<Integer, Integer> pos : dollPos) {
            Enemy doll = new Doll(pos.getKey(), pos.getValue(), Sprite.oneal_right1.getFxImage());
            enemyControl.addEnemy(doll);
        }
    }

    public List<List<Entity>> getMap() {
        return map;
    }

    public Camera getCamera() {
        return camera;
    }

    public void update() {
        Bomber bomber1 = (Bomber) entities.get(0);
        if (bomber1.isDead()) {
            bomber1.update();
            return;
        }
        try {
            entities.forEach(Entity::update);
        } catch (ConcurrentModificationException e) {
            System.out.println("doll's bug");
        }

        int index = 0;
        for (; index < entities.size(); index++) {
            if (entities.get(index) instanceof Bomber) {
                Bomber bomber = (Bomber) entities.get(index);
                camera.update(bomber);
                bomber.getBombControl().updateBomb();
            }
            if (entities.get(index) instanceof Doll) {
                Doll doll = (Doll) entities.get(index);
                if (doll.getCountTimeDeath() == 35) {
                    Balloom b1 = new Balloom(Math.round((doll.getX() + DEFAULT_SIZE) / SCALED_SIZE),
                            Math.round((doll.getY() + DEFAULT_SIZE) / SCALED_SIZE),
                            balloom_right1.getFxImage(), true);
                    enemyControl.addEnemy(b1);
                }
            }
            if (entities.get(index) instanceof Enemy) {
                Enemy enemy = (Enemy) entities.get(index);
                if (enemy.getCountTimeDeath() > 35) {
                    entities.remove(entities.get(index));
                    index--;
                }
            }
        }

    }

    public void addEntity(Entity entity) {
        int xPos = Math.round(entity.getX() / Sprite.SCALED_SIZE);
        int yPos = Math.round(entity.getY() / Sprite.SCALED_SIZE);
        map.get(yPos).set(xPos, entity);
    }

    public List<Entity> getEntities() {
        return entities;
    }

    /**
     * Return entity in [xPos][yPos].
     */
    public Entity getEntity(int x, int y) {
        int xPos = Math.round(x / Sprite.SCALED_SIZE);
        int yPos = Math.round(y / Sprite.SCALED_SIZE);
        return map.get(yPos).get(xPos);
    }

    public Entity getEntityWithMapPos(int x, int y) {
        return map.get(x).get(y);
    }

    public void replace(int x, int y, Entity entity) {
        map.get(y).set(x, entity);
    }

    public int getLevel() {
        return level;
    }

    public int[][] getCodeList() {
        return codeList;
    }

    public int getCode(int x, int y) {
        return codeList[y][x];
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public long getTime_begin() {
        return time_begin;
    }

    public void clear() {
        map.clear();
        entities.clear();
        enemyControl.clear();
        bombControl.clear();
    }
}
